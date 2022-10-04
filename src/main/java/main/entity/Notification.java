package main.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "notification")
public class Notification implements Serializable {
  @Id
  @Column(name = "notification_id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "user_sender_id")
  private Long userSenderId;

  @Column(name = "status")
  private String status;

  @Column(name = "page_id")
  private Long pageId;

  @Column(name = "change_id")
  private Long changeId;

}
