package wasteless.service.marketplaceServiceTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import wasteless.controller.jsonobjects.MarketplaceCardJson;
import wasteless.exception.BadRequestException;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.repository.MarketplaceCardRepository;
import wasteless.repository.MarketplaceKeywordRepository;
import wasteless.service.MarketplaceService;
import wasteless.test_helpers.MarketplaceDataCreator;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;

@SpringBootTest
class RetrieveCardsTest {

    @MockBean
    private MarketplaceCardRepository marketplaceCardRepository;

    @MockBean
    private MarketplaceKeywordRepository marketplaceKeywordRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MarketplaceService marketplaceService;

    private final JSONParser jsonParser = new JSONParser();
    private List<MarketplaceCard> saleItems;

    User user1;
    MarketplaceCardJson marketplaceCardJson;
    MarketplaceCard marketplaceCard;

    @BeforeEach
    void setup() throws ParseException, IOException {
        FileReader reader = new FileReader("src/test/resources/testData/marketplace.json");
        JSONObject obj = (JSONObject) jsonParser.parse(reader);

        JSONArray forSale = (JSONArray) obj.get("forSale");

        saleItems = new ArrayList<>();
        for (Object card : forSale) {
            saleItems.add(objectMapper.readValue(card.toString(), MarketplaceCard.class));
        }

        Page<MarketplaceCard> page = new PageImpl<>(saleItems);

        Mockito.when(marketplaceCardRepository.findMarketplaceCardsBySection(Mockito.eq("ForSale"), Mockito.any(Pageable.class)))
                .thenReturn(page);

        user1 = MarketplaceDataCreator.createUser(2L, "user1@email.com", "user");
        marketplaceCardJson = MarketplaceDataCreator.createCardJson(9L);
        marketplaceCard = MarketplaceDataCreator.createCard(4, user1);
        Optional<MarketplaceCard> marketPlaceCards = Optional.of(marketplaceCard);
        when(marketplaceCardRepository.findMarketplaceCardByMarketplaceCardId(Mockito.anyInt())).thenReturn(marketPlaceCards);

    }

    @CsvSource({
            "ForSale, 0, 1, created, desc",
            "Wanted, 0, 1, created, desc",
            "Exchange, 0, 1, created, desc",
            "ForSale, 1, 1, created, desc",
            "ForSale, 1, 4, created, desc",
            "ForSale, 0, 1, title, desc",
            "ForSale, 0, 1, creator.homeAddress.city, desc",
            "ForSale, 0, 1, creator.homeAddress.suburb, desc",
            "ForSale, 0, 1, created, asc",
            "ForSale, 0, 1, created, desc",
    })
    @ParameterizedTest
    void retrieveCards_callsRepository(String section, int start, int size, String sort, String order) {
        marketplaceService.retrieveCards(section, start, size, sort, order);
        ArgumentCaptor<String> stringArgumentCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Pageable> pageableArgumentCaptor = ArgumentCaptor.forClass(Pageable.class);
        Mockito.verify(marketplaceCardRepository, Mockito.times(1))
                .findMarketplaceCardsBySection(stringArgumentCaptor.capture(), pageableArgumentCaptor.capture());
    }

    @CsvSource({
            "0, 0, created, desc",
            "0, 1, someOtherField, asc",
            "0, 1, title, invalidOrder",
            "0, -1, creator.homeAddress.suburb, desc",
            "-1, 1, creator.homeAddress.country, desc",
    })
    @ParameterizedTest
    void retrieveCards_invalidPageSize_throwsException(int start, int size, String sort, String order) {
        Assertions.assertThrows(BadRequestException.class, () -> marketplaceService
                .retrieveCards("ForSale", start, size, sort, order));
    }

    @CsvSource({
            "0, 0, created, desc",
            "0, 1, someOtherField, asc",
            "0, 1, title, invalidOrder",
            "0, -1, creator.homeAddress.suburb, desc",
            "-1, 1, creator.homeAddress.country, desc"
    })
    @ParameterizedTest
    void retrieveCards_invalidPageSize_doesNotCallRepository(int start, int size, String sort, String order) {
        try {
            marketplaceService
                    .retrieveCards("ForSale", start, size, sort, order);
        } catch (BadRequestException ignored) {
            Mockito.verify(marketplaceCardRepository, Mockito.never())
                    .findMarketplaceCardsBySection(Mockito.anyString(), Mockito.any(Pageable.class));
        }
    }

    @CsvSource({
            "0, 0, created, desc, Invalid page size: page size must be at least 1",
            "0, -1, creator.homeAddress.suburb, desc, Invalid page size: page size must be at least 1",
            "-1, 1, creator.homeAddress.suburb, asc, Invalid page number: page number less than 0",
            "0, 1, someOtherField, asc, Unrecognized sorting field",
            "0, 1, title, invalidOrder, Unrecognized ordering field",
    })
    @ParameterizedTest
    void retrieveCards_invalidPageSize_correctMessage(int start, int size, String sort, String order, String expected) {
        String message = "";
        try {
            marketplaceService.retrieveCards("Exchange", start, size, sort, order);
        } catch (BadRequestException e) {
            message = e.getMessage();
        }
        Assertions.assertEquals(expected, message);
    }

    public static Stream<Arguments> retrievePage_nullParameters_returnsException() {
        return Stream.of(
                Arguments.of(null, 0, 1, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void retrievePage_nullParameters_returnsException(String section, int start, int size, String sort, String order) {
        Assertions.assertThrows(BadRequestException.class, () -> marketplaceService.retrieveCards(section, start, size, sort, order));
    }

    @Test
    void retrieveCardById_cardExist_returnCard() throws Exception{
       MarketplaceCard marketplaceCardResult = marketplaceService.retrieveCardById(1);
       Assertions.assertEquals(marketplaceCard, marketplaceCardResult);
    }

}
