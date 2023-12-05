package wasteless.cucumber.stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.ObjectJSONMapper;
import wasteless.cucumber.CucumberSpringConfiguration;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.repository.MarketplaceCardRepository;
import wasteless.repository.UserRepository;
import wasteless.test_helpers.UserDataCreator;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.SQLException;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class UCM4_1_step_definitions extends CucumberSpringConfiguration {
    @Autowired
    DataSource dataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MarketplaceCardRepository marketplaceCardRepository;

    private ObjectJSONMapper objectJSONMapper = new ObjectJSONMapper();

    private MvcResult result;

    User currentUser;


    @Before
    public void setup() throws SQLException {
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("testData/CreateDBTables.sql"));
        ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("testData/MarketplaceData.sql"));

    }

    @When("I am logged in with the email {string} with the role {string} - UCM4.1")
    public void i_am_logged_in(String email, String role) throws Exception {

        currentUser = UserDataCreator.createUser(email, role);
        String userLoginRequest = objectJSONMapper.mapCredentials(currentUser.getEmail(), currentUser.getPassword());

        currentUser.setPassword(passwordEncoder.encode(currentUser.getPassword()));

        User repositoryUser;
        repositoryUser = userRepository.findByEmail(email).orElse(null);
        if (repositoryUser == null) {
            repositoryUser = userRepository.save(currentUser);
        }
        currentUser.setUserId(repositoryUser.getUserId()); // database ignores the given ID, so we need to update the normal user's ID

        mockMvc.perform(
                MockMvcRequestBuilders.post("/login")
                        .content(userLoginRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @Transactional
    @Given("I have a created a marketplace card with ID {int}")
    public void i_have_created_a_marketplace_card(Integer cardId) {
        // Check that a card with the given ID exists
        MarketplaceCard card = marketplaceCardRepository.findMarketplaceCardByMarketplaceCardId(cardId).orElse(null);
        Assertions.assertNotNull(card);

        // Update the creator to the current user (so that they will later be able to delete it)
        card.setCreator(currentUser);
        marketplaceCardRepository.save(card);
        Assertions.assertEquals(currentUser.getUserId(), card.getCreator().getUserId());
    }

    @When("I try to delete the card with ID {int}")
    public void i_delete_card_with_id(Integer cardId) throws Exception {
        if (currentUser.getEmail() == null) {
            result = mockMvc
                    .perform(MockMvcRequestBuilders.delete("/cards/" + cardId))
                    .andReturn();
        } else {
            result = mockMvc
                    .perform(MockMvcRequestBuilders.delete("/cards/" + cardId)
                    .with(user(currentUser.getEmail())))
                    .andReturn();
        }
    }

    @Then("I can no longer see the card with ID {int}")
    public void i_can_no_longer_see_the_card_with_id(Integer cardId) throws Exception {
        result = mockMvc
                .perform(MockMvcRequestBuilders.get("/cards?section=ForSale")
                .with(user(currentUser.getEmail())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    String content = result.getResponse().getContentAsString();
    Assertions.assertFalse(content.contains("\"marketplaceCardId\":" + cardId));
    }

    @Given("another user has created a card with ID {int}")
    public void another_user_has_created_a_card_with_id(Integer cardId) throws Exception {
        User otherUser = UserDataCreator.createUser("another_user@gmail.com", "user");
        // Write code here that turns the phrase above into concrete actions
        result = mockMvc
                .perform(MockMvcRequestBuilders.get("/cards?section=ForSale")
                        .with(user(otherUser.getEmail())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Assertions.assertTrue(content.contains("\"marketplaceCardId\":" + cardId));
    }

    @Given("I am logged in with the email {string} and password {string}")
    public void iAmLoggedInWithTheEmailAndPassword(String email, String password) throws Exception {
        String credentials = objectJSONMapper.mapCredentials(email, password);
        result =
                mockMvc
                        .perform(
                                MockMvcRequestBuilders.post("/login")
                                        .content(credentials)
                                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(MockMvcResultMatchers.status().isOk())
                        .andReturn();
    }

    @Then("I am prevented from deleting the card with the error {string}")
    public void i_am_prevented_from_deleting_the_card_with_the_error(String error) {
        int expectedStatus;
        switch (error) {
            case "Forbidden":
                expectedStatus = 403;
                break;
            case "Unauthorised":
                expectedStatus = 401;
                break;
            case "Not acceptable":
                expectedStatus = 406;
                break;
            default:
                expectedStatus = 0;
        }
        int actualStatus = result.getResponse().getStatus();
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Given("I am logged in as the DGAA")
    public void i_am_logged_in_as_the_dgaa() {
        User repositoryUser = userRepository.findByRole("default_global_admin");
        if (repositoryUser == null) {
            currentUser = UserDataCreator.createUser("admin@defaultglobal", "default_global_admin");
            currentUser.setPassword(passwordEncoder.encode(currentUser.getPassword()));
            repositoryUser = userRepository.save(currentUser);
        }
        currentUser = repositoryUser;
    }

    @Given("I am not logged in as a user")
    public void i_am_not_logged_in_as_a_user() {
        currentUser = UserDataCreator.createUser();
        currentUser.setEmail(null);
    }

}
