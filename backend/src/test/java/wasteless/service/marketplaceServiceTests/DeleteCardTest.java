package wasteless.service.marketplaceServiceTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import wasteless.controller.jsonobjects.MarketplaceCardJson;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.repository.MarketplaceCardRepository;
import wasteless.service.MarketplaceService;
import wasteless.service.NotificationService;
import wasteless.test_helpers.MarketplaceDataCreator;

import java.time.LocalDateTime;

@SpringBootTest
class DeleteCardTest {

    @MockBean
    private MarketplaceCardRepository marketplaceCardRepository;

    @MockBean
    private NotificationService notificationService;

    @SpyBean
    private MarketplaceService marketplaceService;

    private MarketplaceCardJson marketplaceCardJson;

    private MarketplaceCard marketplaceCard;

    private User creator;


    @BeforeEach
    void setup() {
        creator = MarketplaceDataCreator.createUser(9L, "johndoe@gmail.com", "user");
        marketplaceCardJson = MarketplaceDataCreator.createCardJson(9L);
        marketplaceCard = MarketplaceDataCreator.createCard(7, creator);

        Mockito.when(marketplaceService.isInvalidCreatorId(Mockito.anyLong())).thenReturn(false);
        Mockito.when(marketplaceCardRepository.save(Mockito.any(MarketplaceCard.class))).thenReturn(marketplaceCard);

    }

    @Test
    void deleteCard_validId_removesCard() {
        marketplaceService.deleteCard(marketplaceCard);
        Mockito.verify(marketplaceCardRepository).delete(Mockito.any(MarketplaceCard.class));
    }

    @Test
    void deleteCard_notExpired_noCallToNotificationService() {
        MarketplaceCard unexpiredCard = marketplaceCard;
        unexpiredCard.setDisplayPeriodEnd(LocalDateTime.now().plusWeeks(2));
        marketplaceService.deleteCard(unexpiredCard);
        Mockito.verify(notificationService, Mockito.never())
                .readExpiryWarningNotifications(unexpiredCard.getCreator(), unexpiredCard.getMarketplaceCardId());
    }

    /**
     * Tests with a card that has expired one second ago, from the time of testing.
     */
    @Test
    void deleteCard_cardIsDeleted_callsNotificationServiceMethod() {
        MarketplaceCard expiredCard = marketplaceCard;
        expiredCard.setDisplayPeriodEnd(LocalDateTime.now().minusSeconds(1));
        marketplaceService.deleteCard(expiredCard);
        Mockito.verify(notificationService)
                .readExpiryWarningNotifications(expiredCard.getCreator(), expiredCard.getMarketplaceCardId());
    }
}
