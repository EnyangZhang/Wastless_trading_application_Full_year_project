package wasteless;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.repository.MarketplaceCardRepository;
import wasteless.service.NotificationService;
import wasteless.test_helpers.MarketplaceDataCreator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class CardExpiryIntegrationTest {

    @Autowired
    private CardExpiryRunner cardExpiryRunner;

    @MockBean
    private MarketplaceCardRepository marketplaceCardRepository;

    @MockBean
    private NotificationService notificationService;

    @Captor
    ArgumentCaptor<LocalDateTime> currentDate;

    @Captor
    ArgumentCaptor<LocalDateTime> oneDayAgo;

    @Captor
    ArgumentCaptor<ApplicationArguments> appArgs;

    private void mockToDeleteCards(List<MarketplaceCard> fakeCards) {
        Mockito.when(marketplaceCardRepository.findAllByDisplayPeriodEndBefore(Mockito.any(LocalDateTime.class)))
                .thenReturn(fakeCards);
    }

    private void mockExpiredCards(List<MarketplaceCard> fakeCards) {
        Mockito.when(marketplaceCardRepository
                .findAllByDisplayPeriodEndBetween(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(fakeCards);
    }

    @Test
    void beanIsLoadedIntoContext_findExtendableCardCalls_isGreaterThanZero() {
        cardExpiryRunner.run(Mockito.eq(appArgs.capture()));
        Mockito.verify(marketplaceCardRepository, Mockito.atLeast(1))
                .findAllByDisplayPeriodEndBetween(oneDayAgo.capture(), currentDate.capture());
    }

    @Test
    void beanIsLoadedIntoContext_findInExtensibleCardCalls_isGreaterThanZero() {
        cardExpiryRunner.run(Mockito.eq(appArgs.capture()));
        Mockito.verify(marketplaceCardRepository, Mockito.atLeast(1))
                .findAllByDisplayPeriodEndBefore(oneDayAgo.capture());
    }

    @Test
    void scheduledMethodIsCalled_findExtendableCardCalls_isGreaterThanZero() {
        cardExpiryRunner.periodicCheck();
        Mockito.verify(marketplaceCardRepository, Mockito.atLeast(1))
                .findAllByDisplayPeriodEndBetween(oneDayAgo.capture(), currentDate.capture());
    }

    @Test
    void scheduledMethodIsCalled_findInextensibleCardCalls_isGreaterThanZero() {
        cardExpiryRunner.periodicCheck();
        Mockito.verify(marketplaceCardRepository, Mockito.atLeast(1))
                .findAllByDisplayPeriodEndBefore(oneDayAgo.capture());
    }

    @Test
    void checkExpiredCardsIsCalled_repositoryReturnsOneDeletableCard_deleteCardIsCalledForCard() {
        MarketplaceCard fakeCard = MarketplaceDataCreator
                .createCard(1, MarketplaceDataCreator.createUser(2L, "email@email.com", "user"));
        mockToDeleteCards(List.of(fakeCard));
        cardExpiryRunner.periodicCheck();
        Mockito.verify(marketplaceCardRepository).delete(fakeCard);
    }

    @Test
    void checkExpiredCardsIsCalled_repositoryReturnsOneDeletableCard_notificationIsCreated() {
        MarketplaceCard fakeCard = MarketplaceDataCreator
                .createCard(1, MarketplaceDataCreator.createUser(2L, "email@email.com", "user"));
        mockToDeleteCards(List.of(fakeCard));
        cardExpiryRunner.periodicCheck();
        Mockito.verify(notificationService).createDeletionNotification(fakeCard);
    }

    @ParameterizedTest
    @CsvSource({"0", "1", "2", "100"})
    void checkExpiredCardsIsCalled_repositoryReturnsInextensibleCards_deleteCardIsCalledForEach(int numberCardsToDelete) {
        ArrayList<MarketplaceCard> cardsToDelete = new ArrayList<>();
        for (int i=0; i < numberCardsToDelete; i++) {
            User fakeUser = MarketplaceDataCreator.createUser(i, i + "@email.com", "user");
            MarketplaceCard fakeCard = MarketplaceDataCreator.createCard(i, fakeUser);
            cardsToDelete.add(fakeCard);
        }
        mockToDeleteCards(cardsToDelete);
        cardExpiryRunner.periodicCheck();
        Mockito.verify(marketplaceCardRepository, Mockito.times(numberCardsToDelete)).delete(Mockito.any(MarketplaceCard.class));
    }

    @ParameterizedTest
    @CsvSource({"0", "1", "2", "100"})
    void checkExpiredCardsIsCalled_repositoryReturnsInextensibleCards_notificationIsCreatedForEach(int numberCardsToDelete) {
        ArrayList<MarketplaceCard> cardsToDelete = new ArrayList<>();
        for (int i=0; i < numberCardsToDelete; i++) {
            User fakeUser = MarketplaceDataCreator.createUser(i, i + "@email.com", "user");
            MarketplaceCard fakeCard = MarketplaceDataCreator.createCard(i, fakeUser);
            cardsToDelete.add(fakeCard);
        }
        mockToDeleteCards(cardsToDelete);
        cardExpiryRunner.periodicCheck();
        Mockito.verify(notificationService, Mockito.times(numberCardsToDelete))
                .createDeletionNotification(Mockito.any(MarketplaceCard.class));
    }

    @Test
    void checkExpiredCardsIsCalled_repositoryReturnsOneExpiredCard_notificationIsCreated() {
        MarketplaceCard fakeCard = MarketplaceDataCreator
                .createCard(1, MarketplaceDataCreator.createUser(2L, "email@email.com", "user"));
        mockExpiredCards(List.of(fakeCard));
        cardExpiryRunner.periodicCheck();
        Mockito.verify(notificationService).createCardExpiryNotification(fakeCard);
    }

    @ParameterizedTest
    @CsvSource({"0", "1", "2", "100"})
    void checkExpiredCardIsCalled_repositoryReturnsExpiredCards_notificationIsCreatedForEach(int numberExpiredCards) {
        ArrayList<MarketplaceCard> expiredCards = new ArrayList<>();
        for (int i=0; i < numberExpiredCards; i++) {
            User fakeUser = MarketplaceDataCreator.createUser(i, i + "@email.com", "user");
            MarketplaceCard fakeCard = MarketplaceDataCreator.createCard(i, fakeUser);
            expiredCards.add(fakeCard);
        }
        mockExpiredCards(expiredCards);
        cardExpiryRunner.periodicCheck();
        Mockito.verify(notificationService, Mockito.times(numberExpiredCards))
                .createCardExpiryNotification(Mockito.any(MarketplaceCard.class));
    }
}
