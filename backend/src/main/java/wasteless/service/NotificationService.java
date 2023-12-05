package wasteless.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;
import wasteless.exception.ForbiddenException;
import wasteless.exception.UnauthorizedException;
import wasteless.model.*;
import wasteless.repository.MarketplaceCardRepository;
import wasteless.repository.NotificationRepository;
import wasteless.repository.UserRepository;
import wasteless.security.AuthUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

/**
 * Service class that handles the creation, retrieval and modification of notification objects in the database.
 * Interacts with the notification repository to persist changes to the notification database, and related model objects,
 * such as updating marketplace cards when notifications are created for them.
 */
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final MarketplaceCardRepository marketplaceCardRepository;
    private final UserRepository userRepository;
    private final AuthUtil authUtil;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
                               MarketplaceCardRepository marketplaceCardRepository,
                               UserRepository userRepository,
                               AuthUtil authUtil) {
        this.notificationRepository = notificationRepository;
        this.marketplaceCardRepository = marketplaceCardRepository;
        this.userRepository = userRepository;
        this.authUtil = authUtil;
    }

    /**
     * Gets the notifications based on user ID from the database.
     * @return a list of notifications related to the given user ID.
     */
    public List<Notification> getNotificationsForUser(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotAcceptableStatusException("No user found with ID " + userId));
        return notificationRepository.findAllByRelatedUser(user);
    }

    /**
     * Creates a notification in the database with the given attributes.
     * @param title The title of the notification that would be shown to the user.
     * @param message The detailed message with the notification that will be shown to the user.
     * @param category The category of notification. This will NOT be shown to the user and should be
     *                 taken from the categories provided by {@link NotificationCategory}.
     * @param targetUser The user that the notification will target.
     * @param actionId The Id of the entity that the notification is relating to
     */
    public void createNotification(String title, String message, NotificationCategory category, User targetUser, Integer actionId) {
        Notification notification = new Notification(targetUser, category, title, message, actionId);
        notificationRepository.save(notification);
    }

    /**
     * Prepares notification title and message for a marketplace card that has expired, but can still be extended.
     * If the marketplace card has its notifiedExpiring property set to false (meaning the user had not been notified
     * of this change before), then a new notification is created by calling createNotification with the required
     * parameters, and the card's notifiedExpiring attribute is updated in the repository to True.
     * @param card An instance of MarketplaceCard, the card to create a notification for. Its title and expiry time
     *             is included in the notification message. It may already have an expiry notification.
     */
    public void createCardExpiryNotification(MarketplaceCard card) {
        User recipient = card.getCreator();
        LocalDateTime expired = card.getDisplayPeriodEnd();
        String formattedDate = expired.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
        String title = "Card Expired";
        String message = "Your card \"" + card.getTitle() + "\" has expired on " + formattedDate + ". Please renew or delete your " +
                "card or it will be automatically deleted in 24 hours.";
        Integer actionId = card.getMarketplaceCardId();
        if (card.getNotifiedExpiring().equals(Boolean.FALSE)) {
            createNotification(
                    title,
                    message,
                    NotificationCategory.CARD_EXPIRY_WARNING,
                    recipient,
                    actionId
            );
            card.setNotifiedExpiring(true);
            marketplaceCardRepository.save(card);
        }
    }

    /**
     * Prepares notification title and message for a marketplace card that has expired for more than 24 hours, and has
     * been automatically deleted.
     * A new notification is created by calling createNotification with the required parameters.
     * @param card An instance of MarketplaceCard, the card to create a notification for. Its title is included in the
     *             notification message.
     */
    public void createDeletionNotification(MarketplaceCard card) {
        User recipient = card.getCreator();
        String title = "Card Deleted";
        String message = "Your card \"" + card.getTitle() + "\" has not been renewed for 24 hours, the card has been " +
                "automatically deleted";
        Integer actionId = card.getMarketplaceCardId();
        createNotification(
                title,
                message,
                NotificationCategory.CARD_EXPIRED,
                recipient,
                actionId
        );
        readExpiryWarningNotifications(recipient, actionId);
    }

    /**
     * Finds notifications given a recipient user and related ID, that has the category CARD_EXPIRY_WARNING. 
     * Sets notifications to read. 
     * @param recipient ID of the user
     * @param actionId ID of the related card
     */
    public void readExpiryWarningNotifications(User recipient, Integer actionId) {
        // find the notification with the same action ID as the deletion notification and the category "CARD_EXPIRY_WARNING"
        List<Notification> notifications = notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                        recipient, actionId, NotificationCategory.CARD_EXPIRY_WARNING);
        if (notifications != null) {
            for (Notification notification : notifications) {
                if (!notification.isNotificationRead()) {
                    notification.setNotificationRead(true);
                    notificationRepository.save(notification);
                }
            }
        }
    }

    /**
     * Prepares a notification for user's who have liked (bookmarked) an item which has been sold to another user.
     * @param saleItem the sale item that has been purchased
     * @param user the user to send the notification to
     */
    public void createLikedItemPurchaseNotification(SaleItem saleItem, User user) {
        String productName = saleItem.getInventoryItem().getProduct().getName();
        String title = "\"" + productName + "\" Listing No Longer Available";
        String message = "The \"" + productName + "\" listing that you liked was purchased by another user.";
        Long actionId = saleItem.getSaleItemId();
        createNotification(
                title,
                message,
                NotificationCategory.LIKED_LISTING_SOLD,
                user,
                actionId.intValue()
        );
    }

    /**
     * Prepares a notification for a user after they LIKE a notification.
     * @param saleItem The sale item that has been liked.
     * @param user The user to send the notification.
     */
    public void createLikedAnItemNotification(SaleItem saleItem, User user) {
        String productName = saleItem.getInventoryItem().getProduct().getName();
        String title = "You liked \"" + productName + "\"";
        String message = "You will now be alerted if it is sold.";
        Long actionId = saleItem.getSaleItemId();
        createNotification(
                title,
                message,
                NotificationCategory.LIKED_A_LISTING,
                user,
                actionId.intValue()
        );
    }

    /**
     * Prepares a notification for a user after they UNLIKE a notification.
     * @param saleItem The sale item that has been unliked.
     * @param user The user to send the notification.
     */
    public void createUnlikedAnItemNotification(SaleItem saleItem, User user) {
        String productName = saleItem.getInventoryItem().getProduct().getName();
        String title = "You unliked \"" + productName + "\"";
        String message = "You now won't be alerted if it is sold.";
        Long actionId = saleItem.getSaleItemId();
        createNotification(
                title,
                message,
                NotificationCategory.LIKED_A_LISTING,
                user,
                actionId.intValue()
        );
    }

    /**
     * Prepares notification title and message for a sale listing that was successfully purchased by a user.
     * @param saleItem An instance of SaleItem, the sale item to create a notification for.
     */
    public void createPurchaseNotification(SaleItem saleItem) {
        User recipient = saleItem.getPurchaser();
        String productName = saleItem.getInventoryItem().getProduct().getName();
        String title = "\""+ productName +"\" Purchased";

        String message = "You have purchased " + productName + ".\n" + saleItem.getMoreInfo();

        createNotification(
                title,
                message,
                NotificationCategory.PURCHASE_OF_LISTING,
                recipient,
                saleItem.getSaleItemId().intValue());
    }

    /**
     * This sets a notification as read so that the frontend can highlight unread notifications.
     * @param userId The UserId of the user who has called the method for the target notification
     * @param notificationId The ID of the target notification.
     */
    public void markAsRead(long userId, long notificationId) {
        User currentUser = authUtil.getCurrentUser();
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification == null) {
            throw new NotAcceptableStatusException("No notification found with ID " + notificationId);
        } else if (currentUser == null) {
            throw new UnauthorizedException("No user exists with ID " + userId);
        } else {
            if (userId != notification.getRelatedUser().getUserId()) {
                throw new ForbiddenException("This notification is not owned by the user with ID " + userId);
            } else {
                notification.setNotificationRead(true);
                notificationRepository.save(notification);
            }

        }
    }
}
