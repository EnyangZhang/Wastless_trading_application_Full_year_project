package wasteless.cucumber.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.ObjectJSONMapper;
import wasteless.cucumber.CucumberSpringConfiguration;
import wasteless.model.Business;
import wasteless.model.InventoryItem;
import wasteless.model.Product;
import wasteless.model.User;
import wasteless.repository.UserRepository;
import wasteless.test_helpers.BusinessDataCreator;
import wasteless.test_helpers.UserDataCreator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class U19_step_definitions extends CucumberSpringConfiguration {

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  UserRepository userRepository;

  private final ObjectJSONMapper objectJSONMapper = new ObjectJSONMapper();
  Business business;
  Product product;
  private MvcResult result;

  User currentUser;

  @When("I am logged in with the email {string} with the role {string} - U19")
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

  @Given("The user logs in with email {string} and password {string}")
  public void the_user_logs_in_with_credentials_and_password(String email, String password)
      throws Exception {
    String credentials = objectJSONMapper.mapCredentials(email, password);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/login")
                .content(credentials)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @When("The user logs in with DGAA credentials")
  public void userLogsInWithDGAACredentials() throws Exception {
    String credentials = objectJSONMapper.mapCredentials(admin_username, admin_password);
    mockMvc
            .perform(
                    MockMvcRequestBuilders.post("/login")
                            .content(credentials)
                            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
  }


  @Given("The user with email {string} creates a new business")
  public void iCreateANewBusinessAccountWithNameDescriptionAddressType(String email) throws Exception {
    business = BusinessDataCreator.createBusiness(currentUser);
    String businessJSONRequest = objectJSONMapper.businessToRequest(business);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .with(user(email))
                .content(businessJSONRequest)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  public String createProductRequestFromDataTable(DataTable dataTable) throws Exception {
    Map<String, List<String>> map = dataTable.asMap(String.class, List.class);
    String id = map.get("id").get(0);
    String name = map.get("name").get(0);
    String description = map.get("description").get(0);
    String manufacturer = map.get("manufacturer").get(0);
    double recommendedRetailPrice = Double.parseDouble(map.get("recommendedRetailPrice").get(0));
    String created = map.get("created").get(0);

    product =
        new Product(
            business,
            id,
            name,
            description,
            manufacturer,
            recommendedRetailPrice,
            LocalDate.parse(created, DateTimeFormatter.ISO_DATE_TIME));

    return objectJSONMapper.productToRequest(product);
  }

  public String createInventoryItemRequestFromDataTable(DataTable dataTable) {
    Map<String, List<String>> map = dataTable.asMap(String.class, List.class);
    String productId = map.get("productId").get(0);
    int quantity = Integer.parseInt(map.get("quantity").get(0));
    double pricePerItem = Double.parseDouble(map.get("pricePerItem").get(0));
    double totalPrice = Double.parseDouble(map.get("totalPrice").get(0));
    String manufactured = map.get("manufactured").get(0);
    String sellBy = map.get("sellBy").get(0);
    String bestBefore = map.get("bestBefore").get(0);
    String expires = map.get("expires").get(0);

    InventoryItem inventoryItem =
        new InventoryItem(
            product,
            productId,
            quantity,
            pricePerItem,
            totalPrice,
            LocalDate.parse(manufactured),
            LocalDate.parse(sellBy),
            LocalDate.parse(bestBefore),
            LocalDate.parse(expires));
    inventoryItem.setInventoryItemId(Long.parseLong(map.get("id").get(0)));
    return objectJSONMapper.inventoryItemToRequest(inventoryItem);
  }

  @Given("The user with email {string} creates a new product with the following details:")
  public void createProduct(String email, DataTable dataTable) throws Exception {
    Map<String, List<String>> map = dataTable.asMap(String.class, List.class);
    String businessId = map.get("businessId").get(0);
    String productJSONRequest = createProductRequestFromDataTable(dataTable);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + businessId + "/products")
                .with(user(email))
                .content(productJSONRequest)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @When("The user with email {string} creates an inventory item with the following details:")
  @And("The user with email {string} creates an inventory item with these details:")
  public void createInventoryItem(String email, DataTable dataTable) throws Exception {
    Map<String, List<String>> map = dataTable.asMap(String.class, List.class);
    String businessId = map.get("businessId").get(0);
    String inventoryItemJSONRequest = createInventoryItemRequestFromDataTable(dataTable);
    result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/businesses/" + businessId + "/inventory")
                    .with(user(email))
                    .content(inventoryItemJSONRequest)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
  }

  @Then("The inventory item will be created")
  public void validPostResult() {
    Assertions.assertEquals(201, result.getResponse().getStatus());
  }

  @Then("The inventory item will not be created with a {string} error")
  public void invalidPostResult(String statusMessage) {
    int status = 0;
    if (statusMessage.equals("forbidden")) {
      status = 403;
    } else if (statusMessage.equals("bad request")) {
      status = 400;
    }
    Assertions.assertEquals(status, result.getResponse().getStatus());
  }
}
