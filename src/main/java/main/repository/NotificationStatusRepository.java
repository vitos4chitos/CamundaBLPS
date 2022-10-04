package main.repository;

import main.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationStatusRepository extends JpaRepository<NotificationStatus, Long> {

    Optional<NotificationStatus> getNotificationStatusByNotificationId(Long notificationId);

}
