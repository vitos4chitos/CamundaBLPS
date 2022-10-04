package main.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "change")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Change {

  @Id
  @Column(name = "change_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "page_id")
  private Long pageId;

  @Column(name = "new_page")
  private String text;

  @Column(name = "is_confirmed")
  private String is_confirmed;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "old_page")
  private String oldText;
}
