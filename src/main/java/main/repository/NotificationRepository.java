package main.repository;

import main.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

  Optional<Notification> getTopByUserIdAndUserSenderIdAndPageIdAndStatusOrderByIdDesc(Long userId, Long userSenderId, Long pageId, String status);

  List<Notification> getNotificationsByUserId(Long userId);

  List<Notification> getAllByChangeId(Long ChangeId);
}
