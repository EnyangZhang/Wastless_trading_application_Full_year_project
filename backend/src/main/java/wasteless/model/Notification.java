package wasteless.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * This class defines the notifications that relate to users.
 * Notifications are stored with a category which will be used by the frontend to decide the
 * necessary action.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    private User relatedUser;

    // Stores whether of not the notification has been read by the user
    @JsonProperty("read")
    private boolean notificationRead;

    // Date the notification was issued
    private LocalDateTime created;

    // Must be a category in the Category class
    private NotificationCategory category;

    private String title;

    private String message;

    // The ID of the object that will be acted on.
    private Integer actionId;

    @JsonProperty
    private long getUserId() {
        return relatedUser.getUserId();
    }

    public Notification(User relatedUser, NotificationCategory category, String title, String message, Integer actionId) {
        this.relatedUser = relatedUser;
        this.category = category;
        this.title = title;
        this.message = message;
        this.notificationRead = false;
        this.created = LocalDateTime.now();
        this.actionId = actionId;
    }
}
