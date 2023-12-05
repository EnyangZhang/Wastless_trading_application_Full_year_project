package wasteless.model;

/**
 * Stores the definitions for categories, so they are consistent for the frontend.
 * Notifications should be created with one of these strings.
 */
public enum NotificationCategory {
    NO_ACTION,
    PURCHASE_OF_LISTING,
    LIKED_LISTING_SOLD,
    LIKED_A_LISTING,
    CARD_EXPIRED,
    CARD_EXPIRY_WARNING,
}
