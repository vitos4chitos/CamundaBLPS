package main.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "check")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Check {
  @Id
  @Column(name = "check_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "change_id")
  private Long changeId;

  @Column(name = "comment")
  private String comment;

  @Column(name = "is_confirmed")
  private Boolean is_confirmed;
}
