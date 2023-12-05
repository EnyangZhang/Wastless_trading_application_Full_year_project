package wasteless.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import wasteless.model.Notification;
import wasteless.model.NotificationCategory;
import wasteless.model.User;
import wasteless.test_helpers.NotificationDataCreator;
import wasteless.test_helpers.UserDataCreator;

import java.util.List;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    User user;
    User user2;

    @BeforeEach
    public void setup() {
        userRepository.flush();
        notificationRepository.flush();
        user = UserDataCreator.createUser("user1@email.com", "user");
        user2 = UserDataCreator.createUser("user2@email.com", "user");
        userRepository.save(user);
        userRepository.save(user2);
        notificationRepository.save(NotificationDataCreator.createNotification(user, NotificationCategory.CARD_EXPIRY_WARNING, 1));
        notificationRepository.save(NotificationDataCreator.createNotification(user2, NotificationCategory.CARD_EXPIRED, 2));
        notificationRepository.save(NotificationDataCreator.createNotification(user, NotificationCategory.PURCHASE_OF_LISTING, 1));
        notificationRepository.save(NotificationDataCreator.createNotification(user, NotificationCategory.CARD_EXPIRY_WARNING, 2));
    }

    @Test
    void expiryWarningNotificationsUnread_findAllExpiredNotifications_findsNotifications() {
        List<Notification> notifications = notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                        user, 1, NotificationCategory.CARD_EXPIRY_WARNING
                );
        Assertions.assertNotNull(notifications);
    }

    @Test
    void expiryWarningNotificationsUnread_findAllExpiredNotifications_findsOneNotification() {
        List<Notification> notifications = notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                        user, 1, NotificationCategory.CARD_EXPIRY_WARNING
                );
        Assertions.assertEquals(1, notifications.size());
    }

    @Test
    void expiryWarningNotificationUnread_findAllExpiredNotifications_notificationIsCorrectCategory() {
        List<Notification> notifications = notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                        user, 1, NotificationCategory.CARD_EXPIRY_WARNING
                );
        Notification notification = notifications.get(0);
        Assertions.assertEquals(NotificationCategory.CARD_EXPIRY_WARNING, notification.getCategory());
    }

    @Test
    void expiryWarningNotificationRead_findAllExpiredNotifications_notificationHasCorrectUser() {
        List<Notification> notifications = notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                        user, 1, NotificationCategory.CARD_EXPIRY_WARNING
                );
        Notification notification = notifications.get(0);
        Assertions.assertEquals(user, notification.getRelatedUser());
    }

    @Test
    void expiryWarningNotificationRead_findAllExpiredNotifications_notificationHasCorrectActionId() {
        List<Notification> notifications = notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                user, 1, NotificationCategory.CARD_EXPIRY_WARNING
        );
        Notification notification = notifications.get(0);
        Assertions.assertEquals(1, notification.getActionId());
    }

    @Test
    void notificationsExist_findAllNotification_returnsAllNotifications() {
        List<Notification> notifications = notificationRepository.findAll();
        Assertions.assertEquals(4, notifications.size());
    }

    @Test
    void notificationsExist_deleteAllGivenNotifications_notificationsDeleted() {
        List<Notification> toDelete = notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                        user, 1, NotificationCategory.CARD_EXPIRY_WARNING
                );
        notificationRepository.deleteAll(toDelete);
        List<Notification> notifications = notificationRepository.findAll();
        Assertions.assertEquals(3, notifications.size());
    }

    @Test
    void notificationsExist_deleteAllGivenNotifications_correctNotificationsDeleted() {
        List<Notification> toDelete = notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                        user, 1, NotificationCategory.CARD_EXPIRY_WARNING
                );
        notificationRepository.deleteAll(toDelete);
        List<Notification> notifications = notificationRepository.findAll();
        for (Notification notification : toDelete) {
            Assertions.assertFalse(notifications.contains(notification));
        }
    }
}
