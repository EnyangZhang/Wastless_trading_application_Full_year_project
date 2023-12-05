package wasteless.service.marketplaceServiceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.web.server.NotAcceptableStatusException;
import wasteless.controller.jsonobjects.MarketplaceCardJson;
import wasteless.exception.BadRequestException;
import wasteless.exception.ForbiddenException;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.repository.MarketplaceCardRepository;
import wasteless.security.AuthUtil;
import wasteless.service.MarketplaceService;
import wasteless.test_helpers.MarketplaceDataCreator;

import java.time.LocalDateTime;

@SpringBootTest
class CreateCardTest {

  @MockBean private MarketplaceCardRepository marketplaceCardRepository;

  @SpyBean private MarketplaceService marketplaceService;

  @MockBean private AuthUtil authUtil;

  private MarketplaceCardJson marketplaceCardJson;

  private MarketplaceCard marketplaceCard;

  private User creator;

  private User user;

  private MarketplaceCardJson createCardJson() {
    int[] keywordIds = {};
    return new MarketplaceCardJson(
            1L,
            "ForSale",
            "title",
            "description",
            keywordIds
    );
  }


  @BeforeEach
  void setup() {
    creator = MarketplaceDataCreator.createUser(9L, "johndoe@gmail.com", "user");
    user = MarketplaceDataCreator.createUser(12L, "user@gmail.com", "user");
    marketplaceCardJson = MarketplaceDataCreator.createCardJson(1L);
    marketplaceCard = MarketplaceDataCreator.createCard(7, creator);

    Mockito.when(marketplaceService.isInvalidCreatorId(Mockito.anyLong())).thenReturn(false);
    Mockito.when(marketplaceCardRepository.save(Mockito.any(MarketplaceCard.class))).thenReturn(marketplaceCard);
  }

  // TEST convertJsonToMarketplaceCard:
  //         Tests that the function converts POJO to card object correctly. Only blue-sky scenarios tested for this
  //         function as it has the precondition of receiving valid MarketplaceCardJson

  @Test
  void convertJsonToMarketplaceCard_validJson_returnsValidSection() {
    MarketplaceCard card = marketplaceService.convertJsonToMarketplaceCard(marketplaceCardJson);
    Assertions.assertEquals(marketplaceCardJson.getSection(), card.getSection());
  }

  @Test
  void convertJsonToMarketplaceCard_validJson_returnsValidTitle() {
    MarketplaceCard card = marketplaceService.convertJsonToMarketplaceCard(marketplaceCardJson);
    Assertions.assertEquals(marketplaceCardJson.getTitle(), card.getTitle());
  }

  @Test
  void convertJsonToMarketplaceCard_validJson_returnsValidDescription() {
    MarketplaceCard card = marketplaceService.convertJsonToMarketplaceCard(marketplaceCardJson);
    Assertions.assertEquals(marketplaceCardJson.getDescription(), card.getDescription());
  }

  @Test
  void convertJsonToMarketplaceCard_validJson_returnsValidKeywordIds() {
    MarketplaceCard card = marketplaceService.convertJsonToMarketplaceCard(marketplaceCardJson);
    Assertions.assertEquals(0, card.getKeywords().size());
  }

  @Test
  void convertJsonToMarketplaceCard_validJson_returnsValidCreated() {
    MarketplaceCard card = marketplaceService.convertJsonToMarketplaceCard(marketplaceCardJson);
    Assertions.assertEquals(LocalDateTime.now().withNano(0), card.getCreated().withNano(0));
  }

  @Test
  void convertJsonToMarketplaceCard_validJson_returnsValidDisplayPeriodEnd() {
    MarketplaceCard card = marketplaceService.convertJsonToMarketplaceCard(marketplaceCardJson);
    Assertions.assertEquals(LocalDateTime.now().plusWeeks(3).withNano(0), card.getDisplayPeriodEnd().withNano(0));
  }

  // TEST createMarketplaceCard

  @Test
  void createMarketplaceCard_validInput_savesCorrectCreator() {
    MarketplaceCard card = marketplaceService.createMarketplaceCard(marketplaceCardJson);
    Assertions.assertEquals(card.getCreator().getUserId(), creator.getUserId());
  }

  @Test
  void createMarketplaceCard_validInput_savesCorrectTitle() {
    MarketplaceCard card = marketplaceService.createMarketplaceCard(marketplaceCardJson);
    Assertions.assertEquals(card.getTitle(), marketplaceCard.getTitle());
  }

  @Test
  void createMarketplaceCard_validInput_savesCorrectDescription() {
    MarketplaceCard card = marketplaceService.createMarketplaceCard(marketplaceCardJson);
    Assertions.assertEquals(card.getDescription(), marketplaceCard.getDescription());
  }

  @Test
  void createMarketplaceCard_validInput_savesCorrectSection() {
    MarketplaceCard card = marketplaceService.createMarketplaceCard(marketplaceCardJson);
    Assertions.assertEquals(card.getSection(), marketplaceCard.getSection());
  }

  @Test
  void createMarketplaceCard_validInput_savesCorrectKeywordIds() {
    MarketplaceCard card = marketplaceService.createMarketplaceCard(marketplaceCardJson);
    Assertions.assertEquals(0, card.getKeywords().size());
  }

  @Test
  void createMarketplace_invalidSection_throws400()  {
    marketplaceCardJson.setSection("invalidSection");
    Assertions.assertThrows(
            BadRequestException.class,
            () -> {
              marketplaceService.createMarketplaceCard(marketplaceCardJson);
            });
  }

  @Test
  void createMarketplace_titleTooLong_throws400()  {
    String longString = new String(new char[300]).replace("\0", "-");
    marketplaceCardJson.setTitle(longString);
    Assertions.assertThrows(
            BadRequestException.class,
            () -> {
              marketplaceService.createMarketplaceCard(marketplaceCardJson);
            });
  }

  @Test
  void createMarketplace_descriptionTooLong_throws400()  {
    String longString = new String(new char[300]).replace("\0", "-");
    marketplaceCardJson.setDescription(longString);
    Assertions.assertThrows(
            BadRequestException.class,
            () -> {
              marketplaceService.createMarketplaceCard(marketplaceCardJson);
            });
  }

  @Test
  void extendDisplayPeriodEnd_CardDoesNotExist_Throws406() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(creator);
    Assertions.assertThrows(
            NotAcceptableStatusException.class,
            () -> {
              marketplaceService.extendDisplayPeriodEnd(100);
            }
    );
  }

  @Test
  void extendDisplayPeriodEnd_CardDoesNotExist_returnsCorrectErrorMessage() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(creator);
    Exception exception = Assertions.assertThrows(
            NotAcceptableStatusException.class,
            () -> {
              marketplaceService.extendDisplayPeriodEnd(100);
            }
    );
    Assertions.assertEquals("406 NOT_ACCEPTABLE \"The card you are trying to extend the display period for does not exist.\"",
            exception.getMessage());
  }

  @Test
  void extendDisplayPeriodEnd_UserNotCreator_Throws403() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(user);
    Mockito.when(marketplaceCardRepository.findMarketplaceCardByMarketplaceCardId(Mockito.anyInt())).thenReturn(java.util.Optional.ofNullable(marketplaceCard));

    Assertions.assertThrows(
            ForbiddenException.class,
            () -> {
              marketplaceService.extendDisplayPeriodEnd(7);
            }
    );
  }

  @Test
  void extendDisplayPeriodEnd_UserNotCreator_returnsCorrectErrorMessage() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(user);
    Mockito.when(marketplaceCardRepository.findMarketplaceCardByMarketplaceCardId(Mockito.anyInt())).thenReturn(java.util.Optional.ofNullable(marketplaceCard));
    Exception exception = Assertions.assertThrows(
            ForbiddenException.class,
            () -> {
              marketplaceService.extendDisplayPeriodEnd(7);
            }
    );
    Assertions.assertEquals("Cannot extend the display period for a card you did not create.",
            exception.getMessage());
  }

  @Test
  void extendDisplayPeriodEnd_allInputsValid_extendDisplayPeriodRepositoryCalled() {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(creator);
    Mockito.when(marketplaceCardRepository.findMarketplaceCardByMarketplaceCardId(Mockito.anyInt())).thenReturn(java.util.Optional.ofNullable(marketplaceCard));
    Mockito.doNothing().when(marketplaceService).resetNotifiedExpiring(marketplaceCard);
    marketplaceService.extendDisplayPeriodEnd(7);
    Mockito.verify(marketplaceCardRepository).extendDisplayPeriodForCard(Mockito.anyInt(), Mockito.any(LocalDateTime.class));
  }

  @Test
  void resetNotifiedExpiring_allInputsValid_repositorySaveMethodCalled() {
    marketplaceCard.setNotifiedExpiring(true);
    marketplaceService.resetNotifiedExpiring(marketplaceCard);
    Mockito.verify(marketplaceCardRepository).save(marketplaceCard);
  }

  @Test
  void resetNotifiedExpiring_notifiedExpiringFalse_throws400() {
    marketplaceCard.setNotifiedExpiring(false);
    Assertions.assertThrows(BadRequestException.class, () -> {
      marketplaceService.resetNotifiedExpiring(marketplaceCard);
    });
  }


  // TODO add tests to do with invalid keywords (this is not possible until we do UCM6)
}
