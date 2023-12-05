package wasteless;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import wasteless.model.MarketplaceCard;
import wasteless.repository.MarketplaceCardRepository;
import wasteless.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A runner that checks the card database periodically for expired cards.
 * It does so by comparing the expiry date to the current system date.
 *
 * If the expiry date is past and a notification has not yet been created, creates a new notification for that card.
 */
@Component
@EnableScheduling
public class CardExpiryRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(CardExpiryRunner.class);

    private final MarketplaceCardRepository marketplaceCardRepository;

    private final NotificationService notifyService;

    @Autowired
    public CardExpiryRunner(MarketplaceCardRepository marketplaceCardRepository, NotificationService notificationService) {
        this.marketplaceCardRepository = marketplaceCardRepository;
        this.notifyService = notificationService;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Starting card expiry runner");
        checkExpiredCards();
    }

    /**
     * Scheduled to call a check for expired marketplace cards once per interval,
     * the interval is obtained from the application.properties file.
     */
    @Scheduled(fixedDelayString = "${marketplace.expired.check.period}",
            initialDelayString = "${marketplace.expired.check.period}")
    public void periodicCheck() {
        checkExpiredCards();
    }

    /**
     * Called when the runner wishes to check the database for expired cards.
     * Calls the appropriate repository method and filters for cards which contain expiry dates
     * that are equal to or less than (past) the current system date.
     */
    private void checkExpiredCards() {
        logger.info("Checking for expired cards... ");
        LocalDateTime currentDate = LocalDateTime.now();
        LocalDateTime oneDayAgo = currentDate.minusDays(1L);

        List<MarketplaceCard> extendableCards = marketplaceCardRepository.findAllByDisplayPeriodEndBetween(oneDayAgo, currentDate);
        List<MarketplaceCard> inextensibleCards = marketplaceCardRepository.findAllByDisplayPeriodEndBefore(oneDayAgo);

        logger.info("Found {} extendable cards", extendableCards.size());
        logger.info("Found {} inextensible cards", inextensibleCards.size());

        for (MarketplaceCard extendableCard : extendableCards) {
            logger.info("Marketplace Card {} has expired on {} and can be extended",
                    extendableCard.getMarketplaceCardId(),
                    extendableCard.getDisplayPeriodEnd());
            notifyService.createCardExpiryNotification(extendableCard);
        }
        for (MarketplaceCard inextensibleCard : inextensibleCards) {
            logger.info("Marketplace Card {} has expired on {} and will be deleted",
                    inextensibleCard.getMarketplaceCardId(),
                    inextensibleCard.getDisplayPeriodEnd());
            notifyService.createDeletionNotification(inextensibleCard);
            deleteCard(inextensibleCard);
        }
    }

    /**
     * Deletes the given card from the database.
     * @param expiredCard An instance of marketplace card which has an expired date that is two weeks
     * or more past the system date.
     */
    private void deleteCard(MarketplaceCard expiredCard) {
        marketplaceCardRepository.delete(expiredCard);
        logger.info("Marketplace Card {} was successfully deleted", expiredCard.getMarketplaceCardId());
    }
}
