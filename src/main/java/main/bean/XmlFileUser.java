package main.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import main.entity.User;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class XmlFileUser {
  private String name;

  private List<User> users;
}
