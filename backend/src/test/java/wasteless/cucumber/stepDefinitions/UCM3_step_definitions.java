package wasteless.cucumber.stepDefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.cucumber.CucumberSpringConfiguration;
import wasteless.model.MarketplaceCard;
import wasteless.repository.MarketplaceCardRepository;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class UCM3_step_definitions extends CucumberSpringConfiguration {

    @Autowired
    private MarketplaceCardRepository marketplaceCardRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DataSource ds;

    private String url;
    private final String sectionParam = "section";
    private final String pageParam = "page";
    private final String sizeParam = "size";
    private final String sortParam = "sort";
    private final String orderParam = "order";
    private int pageSize;

    private MvcResult result;
    private String email;

    private String currentSection;

    private HashMap<String, Integer> section;

    private MarketplaceCard[] retrievedCards;

    @Before
    public void setup() throws SQLException {
        url = "/cards";
        pageSize = 10;
        section = new HashMap<>();
    }

    @Transactional
    @Given("There are {int} cards in the {string} section of the marketplace")
    public void given_numberOfCardsInEachSection(Integer number, String section) throws SQLException {
        this.section.put(section, number);
        Connection connection = ds.getConnection();
        ScriptUtils.executeSqlScript(connection, new ClassPathResource("testData/CreateDBTables.sql"));
        ScriptUtils.executeSqlScript(connection, new ClassPathResource("testData/UCM3Data.sql"));

        List<MarketplaceCard> cards = marketplaceCardRepository.findMarketplaceCardsBySection(section);

        Assertions.assertEquals(number, cards.size());
    }

    @Given("No user is logged in")
    public void notLoggedIn() {
        Assertions.assertNull(email);
    }

    @Given("the number of cards per page is set to {int}")
    public void marketplace_setPagination_toGiven(int number) {
        this.pageSize = number;
        Assertions.assertEquals(number, this.pageSize);
    }

    @When("the user {string} navigates to page {int} of the {string} section of the marketplace")
    public void marketplace_specificSection_getCards(String email, int page, String section) throws Exception {
        currentSection = section;
        this.email = email;
        result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                        .with(user(email))
                        .param(sectionParam, section)
                        .param(pageParam, String.valueOf(page))
                        .param(sizeParam, String.valueOf(this.pageSize))
        ).andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
        ).andReturn();
        retrievedCards = objectMapper.readValue(result.getResponse().getContentAsString(), MarketplaceCard[].class);
        Assertions.assertNotNull(retrievedCards);
    }

    @Then("The user only sees cards belonging to the current section")
    public void marketplace_specificSection_viewCardsOfSection() {
        Assertions.assertEquals(section.get(currentSection), retrievedCards.length);
        for (MarketplaceCard card : retrievedCards) {
            Assertions.assertNotNull(card);
            Assertions.assertEquals(currentSection, card.getSection());
        }
    }

    @When("There is an unauthorized access to the {string} section of the marketplace")
    public void marketplace_notLoggedIn_unauthorizedStatus(String section) throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .param(sectionParam, section)).andExpect(MockMvcResultMatchers.status().isUnauthorized()).andReturn();
        Assertions.assertNotNull(result);
    }

    @When("the user {string} changes the ordering of {string} section to {string}, {string}")
    public void marketplace_changeOrder(String email, String section, String sort, String order) throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .with(user(email))
                .param(sectionParam, section).param(sortParam, sort)
                .param(orderParam, order))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        retrievedCards = objectMapper.readValue(result.getResponse().getContentAsString(), MarketplaceCard[].class);
        Assertions.assertNotNull(retrievedCards);
    }

    @Then("An error prevents any data from being returned")
    public void marketplace_notLoggedIn_noDataReturned() throws UnsupportedEncodingException {
        Assertions.assertEquals("", result.getResponse().getContentAsString());
    }

    @Then("the user only sees the {int} cards in the given section")
    public void marketplace_getCardsLoggedIn_viewGivenCards(int number) {
        Assertions.assertEquals(number, retrievedCards.length);
    }

    @Then("the user can view the non-empty titles of each card in the section")
    public void marketplace_getCardsLoggedIn_viewGivenCardTitles() {
        for (MarketplaceCard card : retrievedCards) {
            Assertions.assertNotNull(card);
            Assertions.assertNotNull(card.getTitle());
            Assertions.assertNotEquals("", card.getTitle());
        }
    }

    @Then("the description is available on the displayed cards")
    public void marketplace_getCards_viewGivenCardDescriptions() {
        for (MarketplaceCard card : retrievedCards) {
            Assertions.assertNotNull(card);
            Assertions.assertNotNull(card.getDescription());
        }
    }

    @Then("the creator is available on the displayed cards")
    public void marketplace_getCards_viewGivenCardCreators() {
        for (MarketplaceCard card : retrievedCards) {
            Assertions.assertNotNull(card);
            Assertions.assertNotNull(card.getCreator());
        }
    }

    @Then("the creator's location is available on the displayed cards")
    public void marketplace_getCards_viewGivenCardLocation() {
        for (MarketplaceCard card : retrievedCards) {
            Assertions.assertNotNull(card);
            Assertions.assertNotNull(card.getCreator().getHomeAddress());
        }
    }

    @Then("the ordering of the cards is changed to:")
    public void marketplace_ordering_isExpected(@Transpose List<Integer> list) {
        List<Integer> cardIds = new ArrayList<>();
        Assertions.assertNotNull(retrievedCards);
        for (MarketplaceCard card : retrievedCards) {
            cardIds.add(card.getMarketplaceCardId());
        }
        Assertions.assertEquals(list, cardIds);
    }
}
