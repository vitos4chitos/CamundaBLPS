package main.service;

import lombok.extern.slf4j.Slf4j;
import main.entity.*;
import main.repository.AuthorRepository;
import main.repository.PageRepository;
import main.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@Slf4j
public class PageService {

  private final PageRepository pageRepository;
  private final AuthorRepository authorRepository;
  private final UserRepository userRepository;

  public PageService(
      PageRepository pageRepository,
      AuthorRepository authorRepository,
      UserRepository userRepository) {
    this.pageRepository = pageRepository;
    this.authorRepository = authorRepository;
    this.userRepository = userRepository;
  }

  public ResponseEntity<String> addPage(String login, Request request) {
    Page page = new Page();
    page.setName(request.getName());
    page.setText(request.getText());
    Optional<User> user = userRepository.getUserByLogin(login);
    if (!user.isPresent()) {
      return new ResponseEntity<>(
          "Невозможно создать страницу, пользователя с логином "
              + login
              + " не существует !",
          HttpStatus.BAD_REQUEST);
    }
    page.setOwner(user.get().getId());

    //page.setRole(user.get().getRoles());
    log.info(page.toString());
    if (validationRequestPage(page)) {
      if (pageRepository.existsPageByName(page.getName()))
        return new ResponseEntity<>(
            "Страница с таким именем уже существует !", HttpStatus.BAD_REQUEST);
      // todo если статья с таким именем уже есть, то отправить сообщение об ошбике
      Author author = new Author();
      author.setUserId(user.get().getId());
      author.setPageId(pageRepository.save(page).getId());
      authorRepository.save(author);
      return new ResponseEntity<>(
          "Страница создана пользователем c логином " + user.get().getLogin(), HttpStatus.CREATED);
    }
    return new ResponseEntity<>("Страница не прошла валидацию !", HttpStatus.BAD_REQUEST);
  }

  private boolean validationRequestPage(Page page) {
    return Stream.of(page.getOwner(), page.getName(), page.getText())
        .noneMatch(Objects::isNull);
  }

  public void updatePage(Change change) {
    Optional<Page> page = pageRepository.getPageById(change.getPageId());
    Page currentPage = page.get();
    currentPage.setText(change.getText());
    pageRepository.save(currentPage);
  }
}
