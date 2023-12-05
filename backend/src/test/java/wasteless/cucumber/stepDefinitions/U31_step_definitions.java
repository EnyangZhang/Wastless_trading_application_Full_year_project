package wasteless.cucumber.stepDefinitions;

import com.jayway.jsonpath.JsonPath;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import wasteless.model.*;
import wasteless.repository.InventoryItemRepository;
import wasteless.repository.NotificationRepository;
import wasteless.repository.SaleItemRepository;
import wasteless.repository.UserRepository;
import wasteless.test_helpers.UserDataCreator;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class U31_step_definitions extends CucumberSpringConfiguration {

    Logger log = LoggerFactory.getLogger(U31_step_definitions.class);

    @Autowired DataSource dataSource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    private final ObjectJSONMapper objectJSONMapper = new ObjectJSONMapper();

    @Autowired
    private NotificationRepository notificationRepository;

    private MvcResult result;

    private SaleItem saleItem;

    String firstName = "Test";
    String lastName = "Test";
    String email = "test@test.com";
    String dateOfBirth = "1990-12-01";
    String password = "password";
    String streetNumber = "1";
    String suburb = "Ilam";
    String streetName = "Test Street";
    String city = "Test City";
    String postcode = "0000";
    String region = "Test Region";
    String country = "Test Country";
    Address address = new Address(streetNumber, streetName, suburb, city, region, country, postcode);
    private User testUser =
            new User(
                    firstName,
                    lastName,
                    null,
                    null,
                    null,
                    email,
                    LocalDate.parse(dateOfBirth),
                    null,
                    address,
                    password);

    private User currentUser;
    private User savedUser;
    private Notification createdNotification;

    @Transactional
    @Given("There are {int} inventory items in seller's inventory with {int} quantity for each of inventory item")
    public void given_inventoryInSellerInventory(Integer inventories, Integer inventoryQuantity) throws SQLException {
            ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("testData/CreateDBTables.sql"));
            ScriptUtils.executeSqlScript(dataSource.getConnection(), new ClassPathResource("testData/U31Data.sql"));
            List<InventoryItem> sellerInventory = inventoryItemRepository.findAll();
            Assertions.assertEquals(inventories, sellerInventory.size());
            Assertions.assertEquals(inventoryQuantity, sellerInventory.get(0).getQuantity());
            Assertions.assertEquals(inventoryQuantity, sellerInventory.get(1).getQuantity());
    }

    @Given("Inventory ID {int} has {int} corresponding sale item quantity")
    public void given_OneInventoryDetail(Integer saleItemId, Integer saleItemQuantity) throws Exception {
        if (saleItemRepository.findById(saleItemId).isPresent()){
            SaleItem sellerSaleItem = saleItemRepository.findById(saleItemId).get();
            Assertions.assertEquals(saleItemQuantity, sellerSaleItem.getQuantity());
        } else {
            throw new Exception("Sale Item not found.");
        }
    }

    @Given("I am logged in with the email {string} with the role {string} - U31")
    public void i_am_logged_in(String email, String role) throws Exception {

        currentUser = UserDataCreator.createUser(email, role);
        String userLoginRequest = objectJSONMapper.mapCredentials(currentUser.getEmail(), currentUser.getPassword());

        currentUser.setPassword(passwordEncoder.encode(currentUser.getPassword()));

        savedUser = userRepository.findByEmail(email).orElse(null);
        if (savedUser == null) {
            savedUser = userRepository.save(currentUser);
        }
        currentUser.setUserId(savedUser.getUserId()); // database ignores the given ID, so we need to update the normal user's ID

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/login")
                                .content(userLoginRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

    }

    @When("The user purchase the saleItem ID {long} that has {int} quantity in seller's sale listings with correct business ID {int}")
    public void when_theUserPurchase(Long id, Integer saleItemWithFiveStock, Integer businessId) throws Exception {
        String urlTemplate = "/businesses/" + businessId.toString() + "/listings/" + id.toString();
        if (currentUser.getEmail() != null) {
            result = mockMvc
                    .perform(MockMvcRequestBuilders.patch(urlTemplate)
                            .with(user(currentUser.getEmail())))
                    .andReturn();
        } else {
            result = mockMvc
                    .perform(MockMvcRequestBuilders.patch(urlTemplate))
                    .andReturn();
        }

        if (saleItemRepository.findById(id).isPresent()) {
            saleItem = saleItemRepository.findById(id).get();
            Assertions.assertEquals(saleItemWithFiveStock, saleItem.getQuantity());
        } else {
            throw new Exception("Sale Item not found.");
        }
    }

    @Given("I am not logged in as a user - U31")
    public void i_am_not_logged_in_as_a_user() {
        currentUser = UserDataCreator.createUser();
        currentUser.setEmail(null);
    }

    @Then("The quantity of inventory id {int} of seller will be decremented to {int}")
    public void then_inventoryDecremented(int id, int inventoryQuantity) throws Exception {
        if (inventoryItemRepository.findByInventoryItemId(id).isPresent()){
            int actualQuantity = inventoryItemRepository.findByInventoryItemId(id).get().getQuantity();
            Assertions.assertEquals(inventoryQuantity, actualQuantity);
        } else {
            throw new Exception("Inventory item not found.");
        }
    }

    @Then("The server responds {int}")
    public void then_serverResponds(int response) {
        Assertions.assertEquals(response, result.getResponse().getStatus());
    }

    @When("The user has not purchase sale listings with ID {int} and {int}")
    public void when_notPurchased(int idOne, int idTwo) throws Exception {
        if(saleItemRepository.findById(idOne).isPresent() && saleItemRepository.findById(idTwo).isPresent()) {
            SaleItem saleItemOne = saleItemRepository.findById(idOne).get();
            SaleItem saleItemTwo = saleItemRepository.findById(idTwo).get();
            Assertions.assertFalse(saleItemOne.isSold());
            Assertions.assertFalse(saleItemTwo.isSold());
        } else {
            throw new Exception("Sale item not found.");
        }
    }

    @Then("The sale listings with ID {int} and {int} belongs to business id {int} will appear in the search result")
    public void then_saleListingsInSearchResult(int idOne, int idTwo, int businessId) {
        List<SaleItem> saleItems = saleItemRepository.findSaleListingsByBusiness(businessId);
        Assertions.assertEquals(idOne, saleItems.get(0).getId());
        Assertions.assertEquals(idTwo, saleItems.get(1).getId());
    }

    @Then("The sale listings with ID {int} belongs to business id {int} will appear in the search result")
    public void then_showSaleListingsNotSold(int idOne, int businessId) {
        List<SaleItem> saleItems = saleItemRepository.findSaleListingsByBusiness(businessId);
        Assertions.assertEquals(idOne, saleItems.get(0).getId());
        Assertions.assertEquals(1, saleItems.size());
    }

    @When("The user purchase the saleItem ID {int} that has {int} quantity in seller's sale listings but with incorrect request")
    public void when_userSendWrongRequest(int id, int saleItemWithFiveStock) throws Exception {
        if (currentUser.getEmail() != null) {
            result = mockMvc
                    .perform(MockMvcRequestBuilders.patch("/businesses/1/listings/test")
                            .with(user(currentUser.getEmail())))
                    .andReturn();
        } else {
            result = mockMvc
                    .perform(MockMvcRequestBuilders.patch("/businesses/1/listings/test"))
                    .andReturn();
        }

        if (saleItemRepository.findById(id).isPresent()){
            Assertions.assertEquals(saleItemWithFiveStock, saleItemRepository.findById(id).get().getQuantity());
        } else {
            throw new Exception("Sale Item not found.");
        }
    }

    @When("The user purchase the saleItem ID {int} that has {int} quantity in seller's sale listings but with incorrect business Id {int}")
    public void when_userPurchaseSaleListingWithWrongBusinessId(Integer id, Integer saleItemWithFiveStock, Integer businessId) throws Exception {
        String urlTemplate = "/businesses/" + businessId.toString() + "/listings/" + id.toString();
        if (currentUser.getEmail() != null) {
            result = mockMvc
                    .perform(MockMvcRequestBuilders.patch(urlTemplate)
                            .with(user(currentUser.getEmail())))
                    .andReturn();
        } else {
            result = mockMvc
                    .perform(MockMvcRequestBuilders.patch(urlTemplate))
                    .andReturn();
        }
        if (saleItemRepository.findById(id).isPresent()) {
            Assertions.assertEquals(saleItemWithFiveStock, saleItemRepository.findById(id).get().getQuantity());
        } else {
            throw new Exception("Sale item not found.");
        }
    }


    @When("The user requests to see all sold listings for the business with ID {int}")
    public void when_userRequestsSoldListings(int businessId) throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get(
                "/businesses/" + businessId + "/listings?isSold=true").with(user(currentUser.getEmail())))
                .andReturn();
    }

    @When("A user who is not logged in requests to see all sold listings for the business with ID {int}")
    public void when_notLoggedInUserRequestsSoldListings(int businessId) throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get(
                "/businesses/" + businessId + "/listings?isSold=true"))
                .andReturn();
    }

    @Then("The user receives a list of sale listings of length {long}")
    public void then_returnedListHasLength(Long expectedLength) throws Exception {
        String content = result.getResponse().getContentAsString();
        Long responseLength = Long.parseLong(JsonPath.parse(content).read("$.length()").toString());
        Assertions.assertEquals(expectedLength, responseLength);
    }

    @Then("The first sale listing in the list for business ID {int} has an ID of {int}")
    public void then_idOfFirstItemIs(Integer businessId, Integer listingId) throws Exception {
        String content = result.getResponse().getContentAsString();
        Integer responseId = JsonPath.parse(content).read("$.[0].id");
        Assertions.assertEquals(listingId, responseId);
    }

    @Given("The business with ID {long} has no sold sale listings")
    public void the_business_with_id_has_no_sold_sale_listings(Long businessId) {
        List<SaleItem> soldSaleItems = saleItemRepository.findSaleListingsByBusiness(businessId);
        for (SaleItem item : soldSaleItems) {
            Assertions.assertFalse(item.isSold());
        }
    }

    @Then("The request fails with response code {int}")
    public void the_request_fails_with_response_code(Integer expectedStatus) {
        int status = result.getResponse().getStatus();
        Assertions.assertEquals(expectedStatus, status);
    }

    @Given("the user with email {string} likes the sale listing with ID {int}")
    public void the_user_with_email_likes_the_sale_listing_with_id(String string, Integer saleListingId) throws Exception {
        int businessId = (int) saleItemRepository.findById((long) saleListingId).orElseThrow().getBusinessId();
        String urlTemplate = "/businesses/" + businessId + "/listings/" + saleListingId + "/like";

        result = mockMvc
                .perform(MockMvcRequestBuilders.patch(urlTemplate)
                        .with(user(currentUser.getEmail())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

    @When("Another user purchased the sale listing with ID {int}")
    public void another_user_purchased_the_sale_listing_with_id(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("The user with email {string} will get the notification")
    public void the_user_with_email_will_get_the_notification(String string) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    @Then("The user receives a notification about the purchase")
    public void theUserReceivesANotificationAboutThePurchase() {
        List<Notification> notifications = notificationRepository.findAllByRelatedUser(savedUser);
        Assertions.assertEquals(1, notifications.size());
        createdNotification = notifications.get(0);
        Assertions.assertEquals(NotificationCategory.PURCHASE_OF_LISTING, createdNotification.getCategory());
        Assertions.assertEquals("\""+ saleItem.getInventoryItem().getProduct().getName() +"\" Purchased",
                createdNotification.getTitle());
    }

    @Then("The notification message contains {string}")
    public void theUserReceiveANotificationWithContent(String content) {
        Assertions.assertFalse(content.isBlank());
        log.info("Notification Message: {}", createdNotification.getMessage());
        log.info("---------------------------------------------------------------------------------------------------");
        log.info("Expected Content: {}", content);
        Assertions.assertTrue(createdNotification.getMessage().contains(content));
    }
}
