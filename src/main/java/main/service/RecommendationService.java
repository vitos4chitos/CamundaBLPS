package main.service;

import lombok.extern.slf4j.Slf4j;
import main.entity.Page;
import main.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RecommendationService {

  public RecommendationService() {}

  public ResponseEntity<String> getAnswer(Page page, User user) {
    log.info("page with text {}", page.getText());
    return new ResponseEntity<>(page.getText(), HttpStatus.OK);
    //String role = page.getRole();
    // String uRole = user.getRole();
//    if (chechAccess(role, uRole)) {
//      return new ResponseEntity<>(page.getText(), HttpStatus.OK);
//    } else {
//      return  new ResponseEntity<>(user.getLogin() + " " + page.getName(), HttpStatus.BAD_REQUEST);
//    }
    //return null;
  }

  private boolean chechAccess(String role, String uRole) {
    return role.equals(uRole) || uRole.equals("admin");
  }

  public ResponseEntity<String> getRecommendations(List<Page> pages, User user, String name) {
    StringBuilder answer = new StringBuilder();
    for (Page page : pages) {
      if ((page.getName().contains(name) || page.getText().contains(name))) {
        answer.append(page.getName()).append("\n");
      }
    }
    if (answer.toString().length() == 0) {
      return new ResponseEntity<>("no matches", HttpStatus.OK);
    } else {
      return new ResponseEntity<>(answer.toString(), HttpStatus.OK);
    }
  }
}
