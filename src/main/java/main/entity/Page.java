package main.entity;

import lombok.*;

import javax.persistence.*;

@Entity(name = "pages")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Page {
  @Id
  @Column(name = "page_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "owner")
  private Long owner;

  @Column(name = "name")
  private String name;

  @Column(name = "text")
  private String text;

  @Column(name = "roles")
  private String role;
}
