package wasteless.test_helpers;

import wasteless.model.Notification;
import wasteless.model.NotificationCategory;
import wasteless.model.User;

public class NotificationDataCreator {

    /**
     * Create a generic notification with a recipient.
     * @param recipient An instance of user.
     * @return An instance of notification with recipient, action ID 1 and category NO_ACTION.
     */
    public static Notification createGenericNotification(User recipient) {
        return new Notification(
                recipient,
                NotificationCategory.NO_ACTION,
                "Notification Title",
                "Notification Message",
                1);
    }

    /**
     * Create a notification with the given arguments.
     * @param recipient An instance of user.
     * @param category An instance of NotificationCategory, what category the notification will be.
     * @param actionId Integer specifying the related object ID for a notification.
     * @return An instance of notification with the given arguments.
     */
    public static Notification createNotification(User recipient, NotificationCategory category, Integer actionId) {
        return new Notification(
                recipient,
                category,
                "Notification Title",
                "Notification Message",
                actionId
        );
    }
}
