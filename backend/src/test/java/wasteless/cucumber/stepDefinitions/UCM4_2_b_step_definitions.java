package wasteless.cucumber.stepDefinitions;


import io.cucumber.java.Before;
import io.cucumber.java.en.And;
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
import org.springframework.transaction.annotation.Transactional;
import wasteless.ObjectJSONMapper;
import wasteless.cucumber.CucumberSpringConfiguration;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.repository.MarketplaceCardRepository;
import wasteless.repository.UserRepository;
import wasteless.test_helpers.MarketplaceDataCreator;
import wasteless.test_helpers.UserDataCreator;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


public class UCM4_2_b_step_definitions extends CucumberSpringConfiguration {

    @Autowired
    private MarketplaceCardRepository marketplaceCardRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DataSource ds;

    private String url;

    private ObjectJSONMapper objectJSONMapper;

    private MvcResult result;
    private int cardId;
    private User user;
    private MarketplaceCard marketplaceCard;
    private String email;
    private LocalDateTime testOriginalExpiry;
    private MarketplaceCard expiredCard;

    // Current date is the "future"
    private static final LocalDateTime currentDate = LocalDateTime.now();
    @Before
    public void setup() throws SQLException {

        objectJSONMapper = new ObjectJSONMapper();
        ScriptUtils.executeSqlScript(ds.getConnection(), new ClassPathResource("testData/CreateDBTables.sql"));
        ScriptUtils.executeSqlScript(ds.getConnection(), new ClassPathResource("testData/UCM4_2Data.sql"));

    }

    @Given("The user is logged in with the email {string} and password {string}")
    public void userIsLoggedInWithEmailAndPassword(String email, String password) throws Exception {
        this.user = UserDataCreator.createUser(email, "user");
        User repoUser = userRepository.findByEmail(email).orElse(null);
        if (repoUser == null) {
            repoUser = userRepository.save(this.user);
        }
        user.setUserId(repoUser.getUserId());
        this.email = email;
        String credentials = objectJSONMapper.mapCredentials(email, password);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/login")
                                .content(credentials)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Given("The user has created a marketplace card with ID {int}")
    public void userHasCreatedMarketplaceCard(Integer id) throws Exception {
        this.cardId = id;
        this.marketplaceCard = MarketplaceDataCreator.createCard(id, this.user);

        String cardJsonRequest = objectJSONMapper.cardToJsonRequest(this.marketplaceCard);
        result = mockMvc.perform(
                MockMvcRequestBuilders.post("/cards")
                        .content(cardJsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(user(this.email)))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        String content = result.getResponse().getContentAsString();
        String[] contentAsArray = content.split(" ");
        this.cardId = Integer.parseInt(contentAsArray[2]);
    }
    @Transactional
    @And("The user has received a notification that the card has expired")
    public void theUserReceivesANotificationThatTheCardHasExpired() {
        this.marketplaceCard = marketplaceCardRepository.findMarketplaceCardByMarketplaceCardId(this.cardId).orElse(null);
        assert this.marketplaceCard != null;
        this.marketplaceCard.setDisplayPeriodEnd(currentDate.minusSeconds(1)); // Card expired one second ago
        this.marketplaceCard.setNotifiedExpiring(true);

        Assertions.assertTrue(this.marketplaceCard.getDisplayPeriodEnd().isBefore(currentDate));

        expiredCard = marketplaceCardRepository.save(this.marketplaceCard);
        Assertions.assertNotNull(expiredCard);

    }

    @Then("The user can extend their expired card")
    public void userCanExtendCardExpiryDate() throws Exception {
        // Assert that calling the extenddisplayperiod method works
        this.url = "/cards/" + this.cardId + "/extenddisplayperiod";
        result = mockMvc.perform(
                MockMvcRequestBuilders.put("/cards/" + this.cardId + "/extenddisplayperiod")
                        .with(user(this.user.getEmail())))
                .andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        Assertions.assertNotNull(result);

    }

    @When("The user chooses to extend the card")
    public void userExtendsCardExpiryDate() throws Exception {
        // Call extendDisplayPeriod method
        testOriginalExpiry = marketplaceCard.getDisplayPeriodEnd();

        this.url = "/cards/" + this.cardId + "/extenddisplayperiod";
        mockMvc.perform(
                MockMvcRequestBuilders.put(this.url)
                        .with(user(this.email)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Then("The closing date is extended by {int} weeks")
    public void closingDateExtendedByGivenNumberOfWeeks(Integer numWeeks) {
        // Assert that display period has been extended by two weeks
        LocalDateTime expectedExtendedExpiry = testOriginalExpiry.plusWeeks(numWeeks);
        this.marketplaceCard = marketplaceCardRepository.findMarketplaceCardByMarketplaceCardId(this.cardId).orElse(null);
        assert this.marketplaceCard != null;
        LocalDate expectedDate = expectedExtendedExpiry.toLocalDate();
        LocalDate actualDate = this.marketplaceCard.getDisplayPeriodEnd().toLocalDate();
        Assertions.assertEquals(expectedDate, actualDate);
        Assertions.assertEquals(expectedExtendedExpiry.truncatedTo(ChronoUnit.SECONDS),
                this.marketplaceCard.getDisplayPeriodEnd().truncatedTo(ChronoUnit.SECONDS));
    }

    @Then("The user can delete their expired card")
    public void userCanDeleteCard() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/cards/" + cardId)
                            .with(user(this.email)))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }



}
