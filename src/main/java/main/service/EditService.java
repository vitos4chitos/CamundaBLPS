package main.service;

import main.bean.Status;
import main.entity.*;
import main.exceptions.TransactionException;
import main.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class EditService {

  private final ChangeRepository changeRepository;
  private final UserRepository userRepository;
  private final CheckRepository checkRepository;
  private final SearchRepository searchRepository;
  private final AuthorRepository authorRepository;
  private final NotificationRepository notificationRepository;
  private final PageRepository pageRepository;
  private final NotificationService notificationService;
  private final ValidationService validationService;

  public EditService(
          ChangeRepository changeRepository,
          UserRepository userRepository,
          CheckRepository checkRepository,
          SearchRepository searchRepository,
          AuthorRepository authorRepository,
          NotificationRepository notificationRepository,
          PageRepository pageRepository,
          NotificationService notificationService,
          ValidationService validationService) {
    this.changeRepository = changeRepository;
    this.userRepository = userRepository;
    this.checkRepository = checkRepository;
    this.searchRepository = searchRepository;
    this.authorRepository = authorRepository;
    this.notificationRepository = notificationRepository;
    this.pageRepository = pageRepository;
    this.notificationService = notificationService;
    this.validationService = validationService;
  }

  public Long addChange(Long id, String change, Long userId) {
    Optional<Page> page = pageRepository.getPageById(id);
    Change currentChange = new Change();
    currentChange.setText(change);
    currentChange.setPageId(id);
    currentChange.setOldText(page.get().getText());
    currentChange.setUserId(userId);
    currentChange.setIs_confirmed(Status.NOT_CONFIRMED.toString());
    return changeRepository.save(currentChange).getId();
  }

  public ResponseEntity<String> editWithApprove(String login, Request request) {
    Optional<Page> optionalPage = pageRepository.getPageByName(request.getName());
    if (!optionalPage.isPresent()) {
      return new ResponseEntity<>("Такой страницы не существует !", HttpStatus.BAD_REQUEST);
    }
    if (request.getText() == null) {
      return new ResponseEntity<>("Текст для правки не содержит значений", HttpStatus.BAD_REQUEST);
    }
    Page page = optionalPage.get();
    if (validationService.validationRequestPage(page)) {
      Optional<User> user = userRepository.getUserByLogin(login);
      if (user.isPresent()) {
        try {
          user.ifPresent(
                  value -> {
                    notificationService.sendConfirmationsToAllCoAuthors(
                            value.getId(), page, addChange(page.getId(), request.getText(), value.getId()));
                  });
          return new ResponseEntity<>("Запрос на редактирование принят в обработку", HttpStatus.OK);
        }
        catch (TransactionException e){
          return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
      }
      return new ResponseEntity<>(
              "Пользователя с таким логином не существует !", HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<>(
            "Правка для страницы не проходит валидацию !", HttpStatus.BAD_REQUEST);
  }

  public ResponseEntity<List<ChangeAnswer>> getChanges(String username, String name, String flag) {
    Optional<Page> optionalPage = searchRepository.getPageByName(name);
    Optional<User> userOptional = userRepository.getUserByLogin(username);
    if (optionalPage.isPresent() && userOptional.isPresent()) {
      Page page = optionalPage.get();
      List<ChangeAnswer> answer = new ArrayList<>();
      List<Change> changes = changeRepository.getAllByPageId(page.getId());
      for (Change change : changes) {
        if (Objects.equals(change.getIs_confirmed(), flag)) {
          answer.add(
                  new ChangeAnswer(
                          change.getId(),
                          change.getText(),
                          change.getOldText(),
                          name,
                          userRepository.getUserById(change.getUserId()).getLogin()));
        }
      }
      return new ResponseEntity<>(answer, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }
  }

  //    public ResponseEntity<List<ChangeAnswer>> getWaitingChanges(String username, String name){
  //        Optional<Page> optionalPage = searchRepository.getPageByName(name);
  //        Optional<User> userOptional = userRepository.getUserByLogin(username);
  //        if(optionalPage.isPresent() && userOptional.isPresent()){
  //            Page page = optionalPage.get();
  //            User user = userOptional.get();
  //            if(user.getRole().equals(page.getRole()) || user.getRole().equals("admin")){
  //                List<ChangeAnswer> answer = new ArrayList<>();
  //                List<Change> changes = changeRepository.getAllByPageId(page.getId());
  //                for (Change change : changes) {
  //                    if (change.getIs_confirmed() == null) {
  //                        answer.add(new ChangeAnswer(change.getId(), change.getText(),
  // change.getOldText(), name, userRepository.getUserById(change.getUserId()).getLogin()));
  //                    }
  //                }
  //                return new ResponseEntity<>(answer, HttpStatus.OK);
  //            }
  //            else{
  //                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
  //            }
  //        }
  //        else{
  //            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
  //        }
  //    }

  private void updateStatus(Change change) {
    List<Check> checks = checkRepository.getAllByChangeId(change.getId());
    int sum = 0;
    boolean flag = true;
    for (Check check : checks) {
      if (check.getIs_confirmed() == null) {
        sum++;
      } else {
        if (!check.getIs_confirmed()) {
          flag = false;
        }
      }
    }
    if (sum == 0) {
      changeRepository.setUserInfoById(flag, change.getId());
    }
  }

  private String commit(Long id) {
    Change change = changeRepository.getChangeByPageId(id).get();
    checkRepository.deleteAllByChangeId(change.getId());
    changeRepository.deleteByPageId(id);
    String string = change.getText();
    searchRepository.setUserInfoById(string, id);
    return "everything was updated";
  }
}

