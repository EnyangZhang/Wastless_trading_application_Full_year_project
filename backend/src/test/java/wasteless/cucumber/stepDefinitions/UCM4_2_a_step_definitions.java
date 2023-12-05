package wasteless.cucumber.stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import wasteless.CardExpiryRunner;
import wasteless.cucumber.CucumberSpringConfiguration;
import wasteless.model.MarketplaceCard;
import wasteless.model.Notification;
import wasteless.model.NotificationCategory;
import wasteless.model.User;
import wasteless.repository.MarketplaceCardRepository;
import wasteless.repository.NotificationRepository;
import wasteless.repository.UserRepository;
import wasteless.test_helpers.MarketplaceDataCreator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class UCM4_2_a_step_definitions extends CucumberSpringConfiguration {

    @Autowired
    private MarketplaceCardRepository marketplaceCardRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardExpiryRunner cardExpiryRunner;

    private static final Logger log = LoggerFactory.getLogger(UCM4_2_a_step_definitions.class);

    // Current date is the "future"
    private static final LocalDateTime currentDate = LocalDateTime.now();

    // One day in the past
    private static final LocalDateTime oneDayAgo = currentDate.minusDays(1L);

    private MarketplaceCard expiredCard;

    private User user;

    @Before
    public void setup() {
        notificationRepository.deleteAll();
        log.info("UCM2 Acceptance Test: Current date used for testing: {}", currentDate);
        log.info("UCM2 Acceptance Test: One day before current date used for testing: {}", oneDayAgo);
    }

    @When("A card created by {string} has exceeded the maximum display period")
    public void aCardHasReachedTheMaximumDisplayPeriod(String email) {
        user = userRepository.findByEmail(email).orElse(null);

        Assertions.assertNotNull(user);
        log.info("Found user with email: {}", user);

        MarketplaceCard card = MarketplaceDataCreator.createCard(1, user);
        card.setDisplayPeriodEnd(currentDate.minusSeconds(1)); // Card expired one second ago

        Assertions.assertTrue(card.getDisplayPeriodEnd().isBefore(currentDate));

        expiredCard = marketplaceCardRepository.save(card);
        Assertions.assertNotNull(expiredCard);
    }

    @And("The user has not yet been notified for that card")
    public void theUserHasNotYetBeenNotifiedForThatCard() {
        Assertions.assertFalse(expiredCard.getNotifiedExpiring());
    }

    @Transactional
    @Then("The user receives a notification that the card has expired")
    public void theUserReceivesANotificationThatTheCardHasExpired() {
        cardExpiryRunner.periodicCheck();
        Optional<MarketplaceCard> card =
            marketplaceCardRepository.findMarketplaceCardByMarketplaceCardId(
                expiredCard.getMarketplaceCardId());
        Assertions.assertTrue(card.isPresent());
        Assertions.assertTrue(card.get().getNotifiedExpiring());

        List<Notification> notifications = notificationRepository.findAllByRelatedUser(user);
        Assertions.assertEquals(1, notifications.size());

        Notification notification = notifications.get(0);
        Assertions.assertEquals(user, notification.getRelatedUser());
        Assertions.assertEquals(NotificationCategory.CARD_EXPIRY_WARNING, notification.getCategory());
        Assertions.assertTrue(notification.getMessage().contains(expiredCard.getTitle()));
    }

    @And("The user had been previously notified about that card")
    public void theUserHadBeenPreviouslyNotifiedAboutThatCard() {
        expiredCard.setNotifiedExpiring(Boolean.TRUE);
        expiredCard = marketplaceCardRepository.save(expiredCard);
        Assertions.assertTrue(expiredCard.getNotifiedExpiring());
    }

    @Then("The user receives no additional notification that the card has expired")
    public void theUserReceivesNoAdditionalNotificationThatTheCardHasExpired() {
        cardExpiryRunner.periodicCheck();
        List<Notification> notifications = notificationRepository.findAllByRelatedUser(user);
        Assertions.assertTrue(notifications.isEmpty());
    }

    @When("A card created by {string} has exceeded the maximum display period by more than 24 hours")
    public void aCardCreatedHasExceededTheMaximumDisplayPeriodBy(String email) {
        user = userRepository.findByEmail(email).orElse(null);
        Assertions.assertNotNull(user);
        log.info("Found user with email: {}", user);
        MarketplaceCard card = MarketplaceDataCreator.createCard(1, user);
        card.setDisplayPeriodEnd(currentDate.minusHours(24).minusSeconds(1)); // Card expired for one day and 1 second

        Assertions.assertTrue(card.getDisplayPeriodEnd().isBefore(currentDate));

        expiredCard = marketplaceCardRepository.save(card);
        Assertions.assertNotNull(expiredCard);
    }

    @Then("The card is deleted")
    public void theCardIsDeleted() {
        cardExpiryRunner.periodicCheck();
        Optional<MarketplaceCard> marketplaceCard = marketplaceCardRepository
                .findMarketplaceCardByMarketplaceCardId(expiredCard.getMarketplaceCardId());
        Assertions.assertTrue(marketplaceCard.isEmpty());
    }

    @And("The user is notified that the card has been deleted")
    public void theUserIsNotifiedThatTheCardHasBeenDeleted() {
        List<Notification> notifications = notificationRepository.findAllByRelatedUser(user);
        Assertions.assertEquals(1, notifications.size());

        Notification notification = notifications.get(0);
        Assertions.assertEquals(user, notification.getRelatedUser());
        Assertions.assertEquals(NotificationCategory.CARD_EXPIRED, notification.getCategory());
        Assertions.assertTrue(notification.getMessage().contains(expiredCard.getTitle()));
    }
}
