package main.service;

import main.bean.Status;
import main.entity.Notification;
import main.entity.Page;
import main.entity.User;
import main.entity.Verdict;
import main.exceptions.TransactionException;
import main.repository.NotificationRepository;
import main.repository.PageRepository;
import main.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ApproveService {

  private final NotificationService notificationService;

  private final UserRepository userRepository;
  private final NotificationRepository notificationRepository;
  private final PageRepository pageRepository;
  private final TransactionTemplate transactionTemplate;

  public ApproveService(
          NotificationService notificationService, UserRepository userRepository,
          NotificationRepository notificationRepository,
          PageRepository pageRepository,
          PlatformTransactionManager transactionManager) {
    this.notificationService = notificationService;
    this.transactionTemplate = new TransactionTemplate(transactionManager);
    this.userRepository = userRepository;
    this.notificationRepository = notificationRepository;
    this.pageRepository = pageRepository;
  }


  public Verdict approve(Verdict verdict, String login) {
    Optional<User> firstUser = userRepository.getUserByLogin(login);
    Optional<User> secondUser = userRepository.getUserByLogin(verdict.getWhoToConfirm());
    Optional<Page> page = pageRepository.getPageByName(verdict.getPageName());

    if (firstUser.isPresent() && secondUser.isPresent() && page.isPresent()) {
      Optional<Notification> notification =
              notificationRepository.getTopByUserIdAndUserSenderIdAndPageIdAndStatusOrderByIdDesc(
                      firstUser.get().getId(),
                      secondUser.get().getId(),
                      page.get().getId(),
                      Status.NOT_CONFIRMED.toString());
      if (notification.isPresent()) {
        Notification currentNotification = notification.get();
        String oldStatus = currentNotification.getStatus();
        String newStatus = validateVerdictStatus(verdict.getIs_confirmed());
        currentNotification.setStatus(newStatus);
        try {
          checkApproveStatus(
                  currentNotification.getChangeId(), secondUser.get().getId(), page.get().getId(),currentNotification);
          verdict.setResponseVerdictAns("Статус изменился с " + oldStatus + " на " + newStatus);
          return verdict;
        }
        catch (TransactionException e){
          verdict.setResponseVerdictAns("Статус не изменился, возникла ошибка: " + e.getMessage());
          return verdict;
        }
      }
      verdict.setResponseVerdictAns("Для этих пользователей нет подтверждений !");
      return verdict;
    }

    verdict.setResponseVerdictAns("Пользователи с такими логинами не найдены !");
    return verdict;
  }

  private String validateVerdictStatus(String status){
    if ("TRUE".equals(status) || "FALSE".equals(status))
      return status;
    return "NOT_CONFIRMED";
  }

  private void checkApproveStatus(Long changeId, Long userId, Long pageId, Notification notification) throws TransactionException {
    String answer = notificationService.getAllNotificationsByChangeId(changeId, userId, pageId, notification);
  }

  public ResponseEntity<?> getApprovePages(String login) {
    Optional<User> user = userRepository.getUserByLogin(login);
    if (user.isPresent()) {
      List<Page> pagesWithStatus = new ArrayList<>();
      List<Notification> notificationList =
              notificationRepository.getNotificationsByUserId(user.get().getId());
      notificationList.forEach(
              notification -> {
                Optional<Page> page = pageRepository.getPageById(notification.getPageId());
                page.ifPresent(pagesWithStatus::add);
              });
      return ResponseEntity.status(HttpStatus.OK).body(pagesWithStatus);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Пользователя с логином " + login + " не существует !");
  }
}