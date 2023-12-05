package wasteless.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.test_helpers.MarketplaceDataCreator;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
class ExpiredMarketplaceCardTest {

    private static final Logger log = LoggerFactory.getLogger(ExpiredMarketplaceCardTest.class);

    @Autowired
    private MarketplaceCardRepository marketplaceCardRepository;

    @Autowired
    private UserRepository userRepository;

    private static LocalDateTime currentDate;
    private static LocalDateTime oneDayAgo;
    private static User user;

    @BeforeAll
    static void constant() {
        currentDate = LocalDateTime.now();
        oneDayAgo = currentDate.minusDays(1L);

        log.info("Current time of testing: {}", currentDate);
        log.info("One day before current time: {}", oneDayAgo);

    }

    @BeforeEach
    public void setup() {
        userRepository.flush();
        marketplaceCardRepository.flush();
        user = userRepository.save(MarketplaceDataCreator.createUser(1L, "email@email.com", "user"));
    }


    /**
     * Creates a card with display_period_end property 1 hour over expiry, with a generic user
     * @return the created marketplace card
     */
    private MarketplaceCard createGenericExtendableCard() {
        MarketplaceCard card = MarketplaceDataCreator.createCard(1, user);
        card.setDisplayPeriodEnd(currentDate.minusHours(1));
        MarketplaceCard createdCard = marketplaceCardRepository.save(card);
        log.info("Created Card: {}", createdCard);
        return createdCard;
    }

    /**
     * Creates a card with display_period_end property 2 weeks before expiry with a generic user
     */
    private void createGenericUnexpiredCard() {
        MarketplaceCard card = MarketplaceDataCreator.createCard(1, user);
        card.setDisplayPeriodEnd(currentDate.plusWeeks(2L));
        MarketplaceCard createdCard = marketplaceCardRepository.save(card);
        log.info("Created card: {}", createdCard);
    }

    /**
     * Creates a card with display_period_end property 2 weeks before expiry with a generic user
     * @return the created marketplace card
     */
    private MarketplaceCard createGenericInextensibleCard() {
        MarketplaceCard card = MarketplaceDataCreator.createCard(1, user);
        card.setDisplayPeriodEnd(oneDayAgo.minusHours(1));
        MarketplaceCard createdCard = marketplaceCardRepository.save(card);
        log.info("Created card: {}", createdCard);
        return createdCard;
    }

    @Test
    void noExpiredCardsExist_findExtendableCard_findsZeroResults() {
        createGenericUnexpiredCard();
        List<MarketplaceCard> cards = marketplaceCardRepository.findAllByDisplayPeriodEndBetween(oneDayAgo, currentDate);
        Assertions.assertTrue(cards.isEmpty());
    }

    @Test
    void extendableCardExists_findExtendableCard_findsOneResult() {
        createGenericExtendableCard();

        Integer expectedLength = 1;
        List<MarketplaceCard> cards = marketplaceCardRepository.findAllByDisplayPeriodEndBetween(oneDayAgo, currentDate);
        Assertions.assertEquals(expectedLength, cards.size());
    }

    @Test
    void allCardsExist_findExtendableCard_findsOnlyExtendableCards() {
        createGenericUnexpiredCard();
        MarketplaceCard extendableCard = createGenericExtendableCard();
        createGenericInextensibleCard();

        List<MarketplaceCard> cards = marketplaceCardRepository.findAllByDisplayPeriodEndBetween(oneDayAgo, currentDate);
        Assertions.assertEquals(List.of(extendableCard), cards);
    }

    @Test
    void allCardsExist_findInextensibleCard_findsOnlyInextensibleCards() {
        createGenericUnexpiredCard();
        createGenericExtendableCard();
        MarketplaceCard inextensibleCard = createGenericInextensibleCard();

        List<MarketplaceCard> cards = marketplaceCardRepository.findAllByDisplayPeriodEndBefore(oneDayAgo);
        Assertions.assertEquals(List.of(inextensibleCard), cards);
    }

    @Test
    void cardExpiredBy24HoursExists_findExtensibleCards_cardIsNotFound() {
        MarketplaceCard expiredOneDay = MarketplaceDataCreator.createCard(1, user);
        expiredOneDay.setDisplayPeriodEnd(oneDayAgo.minusHours(24));
        MarketplaceCard createdCard = marketplaceCardRepository.save(expiredOneDay);
        log.info("Created Card: {}", createdCard);
        List<MarketplaceCard> cards = marketplaceCardRepository.findAllByDisplayPeriodEndBetween(oneDayAgo, currentDate);
        Assertions.assertFalse(cards.contains(createdCard));
    }

    @Test
    void cardExpiredBy24HoursExists_findInextensibleCard_cardIsFound() {
        MarketplaceCard expiredOneDay = MarketplaceDataCreator.createCard(1, user);
        expiredOneDay.setDisplayPeriodEnd(oneDayAgo.minusHours(24));
        MarketplaceCard createdCard = marketplaceCardRepository.save(expiredOneDay);
        log.info("Created Card: {}", createdCard);
        List<MarketplaceCard> cards = marketplaceCardRepository.findAllByDisplayPeriodEndBefore(oneDayAgo);
        Assertions.assertTrue(cards.contains(createdCard));
    }

    @ParameterizedTest
    @CsvSource({"1", "24", "4200"})
    void inextensibleCardExists_findExtendableCard_findsOneResult(Long hoursOverExpiry) {
        createGenericExtendableCard();
        MarketplaceCard inextensible = MarketplaceDataCreator.createCard(2, user);
        inextensible.setDisplayPeriodEnd(oneDayAgo.minusHours(hoursOverExpiry));
        log.info("Created card: {}", inextensible);
        log.info("Test inextensibleCardExists_findExtendableCard_findsOneResult: One day before current time: {}", oneDayAgo);
        MarketplaceCard createdCard = marketplaceCardRepository.save(inextensible);
        List<MarketplaceCard> cards = marketplaceCardRepository.findAllByDisplayPeriodEndBefore(oneDayAgo);
        Assertions.assertTrue(cards.contains(createdCard));
    }
}
