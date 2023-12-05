package wasteless.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wasteless.model.Notification;
import wasteless.model.NotificationCategory;
import wasteless.model.User;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByRelatedUser(User user);

    List<Notification> findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
            User user, Integer actionId, NotificationCategory category);
}
