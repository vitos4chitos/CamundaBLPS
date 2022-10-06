package main.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Verdict implements Serializable {
  private String comment;
  private String is_confirmed;
  // private String userLogin;
  private String whoToConfirm;
  private String pageName;
  private String responseVerdictAns;
}
