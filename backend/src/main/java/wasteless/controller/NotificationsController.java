package wasteless.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import wasteless.service.NotificationService;

/**
 * This controller contains endpoints related to notifications for users.
 */
@RestController
public class NotificationsController {
    private final NotificationService notificationService;

    @Autowired
    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * This is a GET endpoint to retrieve notifications for a specific user.
     * @param userId the user id to get notifications for.
     * @return A list of notifications that are related to the given user.
     */
    @GetMapping(path = "/notifications/{userId}")
    public ResponseEntity<Object> getNotificationsForUser(@PathVariable long userId) {
        return new ResponseEntity<>(notificationService.getNotificationsForUser(userId), HttpStatus.OK);
    }

    /**
     * PATCH endpoint to update the status of a notification being read on the backend.
     * @param userId the user id that the notification being read belongs to
     * @param notificationId the id of the notification that has been read
     * @return a response with a status of 200
     */
    @PatchMapping(path = "/notifications/{userId}/read/{notificationId}")
    public ResponseEntity<Object> markNotificationAsRead(@PathVariable long userId, @PathVariable long notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
