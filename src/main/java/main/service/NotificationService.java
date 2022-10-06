package main.service;

import lombok.AllArgsConstructor;
import main.bean.Status;
import main.entity.*;
import main.exceptions.TransactionException;
import main.repository.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class NotificationService implements UserFinder {

  private final AuthorRepository authorRepository;
  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final ChangeRepository changeRepository;
  private final PageService pageService;
  private final TransactionTemplate transactionTemplate;
  private final MqttGateway mqttGateway;

  private final NotificationStatusRepository notificationStatusRepository;

  private boolean transactionForSendingConfirmations(Long senderUser, Page page, Long changeId){
        try{
          List<Long> authorId = findAllCoAuthorUsers(page.getId());
          authorId.forEach(
                  id -> {
                    Notification notification = new Notification();
                    notification.setUserId(id);
                    notification.setUserSenderId(senderUser);
                    notification.setStatus(Status.NOT_CONFIRMED.toString());
                    notification.setPageId(page.getId());
                    notification.setChangeId(changeId);

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = null;
                    try {
                      oos = new ObjectOutputStream(bos);
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                    try {
                      oos.writeObject(notification);
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                    try {
                      oos.flush();
                    } catch (IOException e) {
                      e.printStackTrace();
                    }
                    byte [] data = bos.toByteArray();
                    mqttGateway.sendToMqtt(data);
                    //notificationRepository.save(notification);
                  });
          return true;
        }
        catch (NullPointerException e){
          return false;
        }
      }

  private boolean emitToRabbitMq() {
    return true;
  }

  public void sendConfirmationsToAllCoAuthors(Long senderUser, Page page, Long changeId) throws TransactionException {
    boolean flag = transactionForSendingConfirmations(senderUser, page, changeId);
    if(!flag){
      throw new TransactionException("Ошибка во время выполнения транзакции");
    }
  }

  public void acknowledgeNotification(Notification notification) {
    notification.setStatus(Status.TRUE.toString());
    notificationRepository.save(notification);
  }

  @Override
  public List<Long> findAllCoAuthorUsers(Long pageId) {
    ArrayList<Author> authors = new ArrayList<>(authorRepository.findAuthorsByPageId(pageId));
    List<Long> usersId = new ArrayList<>();
    authors.forEach(
            author -> {
              usersId.add(author.getUserId());
            });

    return usersId;
  }

  public ResponseEntity<?> getAllNotifications(String userLogin) {

    Optional<User> user = userRepository.getUserByLogin(userLogin);
    if (user.isPresent()) {
      List<Notification> notificationList = this.notificationRepository.getNotificationsByUserId(user.get().getId()).stream()
              .filter(
                      notification ->
                              notification.getStatus().equals(Status.NOT_CONFIRMED.toString()))
              .collect(Collectors.toList());
      for(Notification notification: notificationList) {
        Optional<NotificationStatus> optionalNotificationStatus = notificationStatusRepository.
                getNotificationStatusByNotificationId(notification.getId());
        if (optionalNotificationStatus.isPresent()) {
          NotificationStatus notificationStatus = optionalNotificationStatus.get();
          notificationStatus.setIsRead(true);
          System.out.println(notificationStatus.getIsRead());
          notificationStatusRepository.save(notificationStatus);
        }
      }
      return ResponseEntity.status(HttpStatus.OK)
              .body(notificationList);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Пользователя с логином " + userLogin + " не существует !");
  }

  public ResponseEntity<?> getAllNotificationsToRead(String userLogin) {

    Optional<User> user = userRepository.getUserByLogin(userLogin);
    if (user.isPresent()) {
      List<Notification> notificationList = this.notificationRepository.getNotificationsByUserId(user.get().getId()).stream()
              .filter(
                      notification ->
                              notification.getStatus().equals(Status.NOT_CONFIRMED.toString()))
              .collect(Collectors.toList());
      List<Notification> notificationsToRead = new ArrayList<>();
      for(Notification notification: notificationList){
        Optional<NotificationStatus> optionalNotificationStatus = notificationStatusRepository.
                getNotificationStatusByNotificationId(notification.getId());
        if(optionalNotificationStatus.isPresent()){
          NotificationStatus notificationStatus = optionalNotificationStatus.get();
          if(!notificationStatus.getIsRead()){
            notificationsToRead.add(notification);
            notificationStatus.setIsRead(true);
            System.out.println(notificationStatus.getIsRead());
            notificationStatusRepository.save(notificationStatus);
          }
        }
      }
      return ResponseEntity.status(HttpStatus.OK)
              .body(notificationsToRead);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Пользователя с логином " + userLogin + " не существует !");
  }

  private String transactionToGetAllNotifications(Long changeId, Long userId, Long pageId, Notification currentNotification){
    return (String) transactionTemplate.execute(new TransactionCallback(){

      @Override
      public Object doInTransaction(@NotNull TransactionStatus transactionStatus) {
        try{
          notificationRepository.save(currentNotification);
          List<Notification> notificationList = notificationRepository.getAllByChangeId(changeId);
          boolean verdict =
                  notificationList.stream()
                          .allMatch(notification -> notification.getStatus().equals(Status.TRUE.toString()));
          boolean verdictFalse =
                  notificationList.stream()
                          .anyMatch(notification -> notification.getStatus().equals(Status.FALSE.toString()));

          if (verdict) {
            Optional<Change> changeById = changeRepository.getChangeById(changeId);
            Change change = changeById.get();
            change.setIs_confirmed(Status.TRUE.toString());
            changeRepository.save(change);

            if (authorRepository.findAuthorsByPageId(pageId).stream()
                    .noneMatch(author -> Objects.equals(author.getUserId(), userId))) {
              Author author = new Author();
              author.setUserId(userId);
              author.setPageId(pageId);
              authorRepository.save(author);
            }
            pageService.updatePage(change);
            return Status.TRUE.toString();
          }

          if (verdictFalse) {
            Optional<Change> changeById = changeRepository.getChangeById(changeId);
            Change change = changeById.get();
            change.setIs_confirmed(Status.FALSE.toString());
            changeRepository.save(change);
            return Status.FALSE.toString();
          }
          return Status.NOT_CONFIRMED.toString();
        }
        catch (NullPointerException | NoSuchElementException e){
          throw new TransactionException("Ошибка во время выполнения транзакции");
        }
      }
    });
  }

  public String getAllNotificationsByChangeId(Long changeId, Long userId, Long pageId, Notification currentNotification) throws TransactionException{
    return transactionToGetAllNotifications(changeId, userId, pageId, currentNotification);
  }
}