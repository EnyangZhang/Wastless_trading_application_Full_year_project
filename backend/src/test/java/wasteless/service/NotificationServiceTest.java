package wasteless.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.web.server.NotAcceptableStatusException;
import wasteless.exception.ForbiddenException;
import wasteless.exception.UnauthorizedException;
import wasteless.model.*;
import wasteless.repository.NotificationRepository;
import wasteless.repository.UserRepository;
import wasteless.security.AuthUtil;
import wasteless.test_helpers.BusinessDataCreator;
import wasteless.test_helpers.MarketplaceDataCreator;
import wasteless.test_helpers.NotificationDataCreator;
import wasteless.test_helpers.UserDataCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
class NotificationServiceTest {

    @SpyBean
    @Autowired
    private NotificationService notificationService;

    @MockBean
    private NotificationRepository notificationRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AuthUtil authUtil;

    @Captor
    private ArgumentCaptor<Notification> notificationArgumentCaptor;

    private User recipient;

    private User nonRecipient;

    @BeforeEach
    public void setup() {
        notificationArgumentCaptor = ArgumentCaptor.forClass(Notification.class);
        recipient = UserDataCreator.createUser();
        recipient.setUserId(1L);
        nonRecipient = UserDataCreator.createUser();
        nonRecipient.setUserId(2L);
        Mockito.when(userRepository.findById(recipient.getUserId())).thenReturn(Optional.of(recipient));
    }

    @Test
    void createNotification_givenValidInputs_callsRepositorySave() {
        String title = "Test";
        String message = "Test Message";
        NotificationCategory category = NotificationCategory.NO_ACTION;
        Integer actionId = 1;
        notificationService.createNotification(title, message, category, recipient, actionId);
        Mockito.verify(notificationRepository).save(notificationArgumentCaptor.capture());
    }

    @Test
    void createCardExpiryNotification_notificationPropertyTrue_doesNotCallRepository() {
        MarketplaceCard card = MarketplaceDataCreator.createCard(1, recipient);
        card.setNotifiedExpiring(Boolean.TRUE);
        notificationService.createCardExpiryNotification(card);
        Mockito.verify(notificationRepository, Mockito.never()).save(notificationArgumentCaptor.capture());
    }

    @Test
    void getNotificationsForUser_validInputs_callsRepositoryMethod() {
        NotificationDataCreator.createGenericNotification(recipient);
        notificationService.getNotificationsForUser(recipient.getUserId());
        Mockito.verify(notificationRepository).findAllByRelatedUser(recipient);
    }

    @Test
    void getNotificationsForUser_validInputs_returnsCorrectList() {
        List<Notification> expectedNotifications = new ArrayList<>();
        expectedNotifications.add(NotificationDataCreator.createGenericNotification(recipient));
        Mockito.when(notificationRepository.findAllByRelatedUser(recipient)).thenReturn(expectedNotifications);
        NotificationDataCreator.createGenericNotification(recipient);
        List<Notification> actualNotifications = notificationService.getNotificationsForUser(recipient.getUserId());
        Assertions.assertEquals(expectedNotifications.size(), actualNotifications.size());
        for (int i = 0; i < expectedNotifications.size(); i++) {
            Assertions.assertEquals(expectedNotifications.get(i).getId(), actualNotifications.get(i).getId());
        }

    }

    @Test
    void getNotificationsForUser_nonExistentUser_throws406() {
        NotificationDataCreator.createGenericNotification(recipient);
        Assertions.assertThrows(
                    NotAcceptableStatusException.class,
                    () -> notificationService.getNotificationsForUser(999L));
    }

    @Test
    void markAsRead_validInputs_callsRepositorySave() {
        when(authUtil.getCurrentUser()).thenReturn(recipient);
        Notification notification = NotificationDataCreator.createGenericNotification(recipient);
        Mockito.when(notificationRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(notification));
        notificationService.markAsRead(recipient.getUserId(), notification.getId());
        Mockito.verify(notificationRepository).save(notification);
    }

    @Test
    void markAsRead_userNotNotificationOwner_throws403() {
        when(authUtil.getCurrentUser()).thenReturn(nonRecipient);
        Notification notification = NotificationDataCreator.createGenericNotification(recipient);
        Mockito.when(notificationRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(notification));
        Mockito.when(userRepository.findById(nonRecipient.getUserId())).thenReturn(Optional.of(nonRecipient));
        long notificationId = notification.getId();
        long userId = nonRecipient.getUserId();
        Assertions.assertThrows(
                ForbiddenException.class,
                () -> notificationService.markAsRead(userId, notificationId));
    }

    @Test
    void markAsRead_notificationDoesNotExist_throws406() {
        long id = recipient.getUserId();
        Assertions.assertThrows(
                NotAcceptableStatusException.class,
                () -> notificationService.markAsRead(id, 999L));
    }

    @Test
    void markAsRead_noUserLoggedIn_throws401() {
        when(authUtil.getCurrentUser()).thenReturn(null);
        Notification notification = NotificationDataCreator.createGenericNotification(recipient);
        Mockito.when(notificationRepository.findById(Mockito.any(long.class))).thenReturn(Optional.of(notification));
        long notificationId = notification.getId();
        Assertions.assertThrows(
                UnauthorizedException.class,
                () -> notificationService.markAsRead(100, notificationId));
    }

    @Test
    void createLikedItemPurchaseNotification_givenValidInputs_callsRepositorySave() {
        SaleItem saleItem = BusinessDataCreator.createSaleItem(17L);
        notificationService.createLikedItemPurchaseNotification(saleItem, recipient);
        Mockito.verify(notificationRepository).save(notificationArgumentCaptor.capture());
    }

    @Test
    void createPurchaseNotification_givenValidSaleItem_callsRepository() {
        SaleItem saleItem = BusinessDataCreator.createSaleItem(1L);
        notificationService.createPurchaseNotification(saleItem);
        Mockito.verify(notificationRepository).save(notificationArgumentCaptor.capture());
    }

    @Test
    void createDeleteNotification_givenValidCard_callsRepository() {
        MarketplaceCard card = MarketplaceDataCreator.createCard(1, recipient);
        notificationService.createDeletionNotification(card);
        Mockito.verify(notificationRepository).save(notificationArgumentCaptor.capture());
    }

    @Test
    void createDeleteNotification_giveValidCard_callsDeleteExpiryWarningNotifications() {
        MarketplaceCard card = MarketplaceDataCreator.createCard(1, recipient);
        notificationService.createDeletionNotification(card);
        Mockito.verify(notificationService).readExpiryWarningNotifications(recipient, card.getMarketplaceCardId());
    }

    @Test
    void deleteExpiryWarningNotifications_givenPreviousNotificationsExist_callsRepositorySave() {
        Notification prevNotification = NotificationDataCreator
                .createNotification(recipient, NotificationCategory.CARD_EXPIRY_WARNING, 1);
        List<Notification> notifications = List.of(prevNotification);
        Mockito.when(notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                        recipient, 1, NotificationCategory.CARD_EXPIRY_WARNING))
                .thenReturn(notifications);

        notificationService.readExpiryWarningNotifications(recipient, 1);
        Mockito.verify(notificationRepository, Mockito.times(1))
                .save(notificationArgumentCaptor.capture());
    }

    @Test
    void deleteExpiryWarningNotifications_givenPreviousNotificationExist_callsRepositorySaveMultipleTimes() {
        Notification prevNotification1 = NotificationDataCreator
                .createNotification(recipient, NotificationCategory.CARD_EXPIRY_WARNING, 1);
        Notification prevNotification2 = NotificationDataCreator
                .createNotification(recipient, NotificationCategory.CARD_EXPIRY_WARNING, 1);
        Mockito.when(notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                        recipient, 1, NotificationCategory.CARD_EXPIRY_WARNING))
                .thenReturn(List.of(prevNotification1, prevNotification2));

        notificationService.readExpiryWarningNotifications(recipient, 1);
        Mockito.verify(notificationRepository, Mockito.times(2))
                .save(notificationArgumentCaptor.capture());
    }

    @Test
    void deleteExpiryWarningNotifications_givenPreviousNotificationsExist_savesCorrectNotification() {
        Notification prevNotification = NotificationDataCreator.createNotification(recipient, NotificationCategory.CARD_EXPIRY_WARNING, 1);
        List<Notification> notifications = List.of(prevNotification);
        Mockito.when(notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                        recipient, 1, NotificationCategory.CARD_EXPIRY_WARNING))
                .thenReturn(notifications);

        notificationService.readExpiryWarningNotifications(recipient, 1);

        prevNotification.setNotificationRead(true);

        Mockito.verify(notificationRepository).save(notificationArgumentCaptor.capture());
        Notification savedNotification = notificationArgumentCaptor.getValue();
        Assertions.assertEquals(prevNotification, savedNotification);
    }

    @Test
    void deleteExpiryWarningNotifications_givenPreviousNotificationNotExists_noCallToRepository() {
        Mockito.when(notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                        recipient, 1, NotificationCategory.CARD_EXPIRY_WARNING))
        .thenReturn(new ArrayList<>());

        notificationService.readExpiryWarningNotifications(recipient, 1);
        Mockito.verify(notificationRepository, Mockito.never()).save(Mockito.any(Notification.class));
    }

    @Test
    void deleteExpiryWarningNotifications_givenRepositoryReturnsNullNotifications_doesNotCallRepositoryDelete() {
        Mockito.when(notificationRepository
                .findNotificationsByRelatedUserAndActionIdAndCategoryAndNotificationReadFalse(
                        recipient, 1, NotificationCategory.CARD_EXPIRY_WARNING))
                .thenReturn(null);

        notificationService.readExpiryWarningNotifications(recipient, 1);
        Mockito.verify(notificationRepository, Mockito.never()).deleteAll(Mockito.anyIterable());
    }
}
