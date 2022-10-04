package main.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Verdict {
  private String comment;
  private String is_confirmed;
  // private String userLogin;
  private String whoToConfirm;
  private String pageName;
  private String responseVerdictAns;
}
