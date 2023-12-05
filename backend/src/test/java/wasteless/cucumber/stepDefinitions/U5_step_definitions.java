package wasteless.cucumber.stepDefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.ObjectJSONMapper;
import wasteless.cucumber.CucumberSpringConfiguration;
import wasteless.model.Address;
import wasteless.model.Business;

import javax.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Long.parseLong;

public class U5_step_definitions extends CucumberSpringConfiguration {

  String user =
      "{"
          + "\"firstName\": \"John\","
          + "\"middleName\": \"Johnson\","
          + "\"lastName\": \"Doe\","
          + "\"nickName\": \"JD\","
          + "\"bio\": \"A real person\","
          + "\"email\": \"jdo@email.com\","
          + "\"dateOfBirth\": \"1990-12-01\","
          + "\"phoneNumber\": \"0800 123123\","
          + "\"homeAddress\": "
          + "{"
          + "\"streetNumber\": \"98\","
          + "\"streetName\": \"Madeup Street\","
          + "\"suburb\": \"Ilam\","
          + "\"city\": \"Christchurch\","
          + "\"region\": \"Canterbury\","
          + "\"country\": \"New Zealand\","
          + "\"postcode\": \"8041\""
          + "},"
          + "\"password\": \"password\"}";
  private MvcResult response;
  private ObjectJSONMapper objectJSONMapper;
  private Business business;
  private String businessJSONRequest;
  private String businessJSONResponse;
  private Cookie sessionId;

  private Long businessId;
  private Long retrievedBusinessId;
  private HashMap<Long, Business> businessMap;

  @Before
  public void setup() {
    sessionId = new Cookie("JSESSIONID", "unset");
    objectJSONMapper = new ObjectJSONMapper();
    businessId = 1L;
    businessMap = new HashMap<>();
  }

  @Then("Controller returns 401 code when I get business account")
  public void iCreateANewBusinessAccount() throws Exception {
    if (userRequestPostProcessor == null) {
      mockMvc
              .perform(MockMvcRequestBuilders.get("/businesses/1"))
              .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    } else {
      mockMvc
              .perform(MockMvcRequestBuilders.get("/businesses/1")
                      .with(userRequestPostProcessor))
              .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
  }

  @Given("No user exists")
  public void getUser() throws Exception {
    response =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/users"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    Assertions.assertEquals("[]", response.getResponse().getContentAsString());
  }

  @When("I create a new user")
  public void createNewUser() throws Exception {
    response =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andReturn();
    sessionId = new Cookie("JESSIONID", response.getResponse().getHeader("set-cookie"));
    response =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/users"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    Assertions.assertNotEquals("[]", response.getResponse().getContentAsString());
  }

  @When(
          "I create a new business account with name {string}, description {string}, address {string} {string} {string} {string} {string}, " +
                  "{string} {string}, type {string}")
  public void iCreateANewBusinessAccountWithNameDescriptionAddressType(
      String name,
      String description,
      String streetNum,
      String streetName,
      String suburb,
      String city,
      String region,
      String country,
      String postcode,
      String type) {
    Address a = new Address(streetNum, streetName, suburb, city, region, country, postcode);
    business = new Business(name, description, a, type, 1, new ArrayList<>());
    businessJSONRequest = objectJSONMapper.businessToRequest(business);
    Assertions.assertNotNull(business);
    Assertions.assertNotNull(businessJSONRequest);
  }

  @Then("The server responds with a {int} created response")
  public void theServerRespondsWithACreatedResponse(int arg0) throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .cookie(sessionId)
                .content(businessJSONRequest)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated())
        .andReturn();
    businessJSONResponse = objectJSONMapper.businessToResponse(business, businessId);
    businessMap.put(businessId, business);
    businessId = businessId + 1L;
  }

  @And("I am logged in")
  public void iAmLoggedIn() {
    if (sessionId != null) {
      sessionId = new Cookie("JSESSIONID", "1");
    }
    Assertions.assertNotNull(sessionId);
  }

  @Then("The server responds with a {int} unauthorized response")
  public void theServerRespondsWithAUnauthorizedResponse(int arg0) throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .cookie(sessionId)
                .content(businessJSONRequest)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @When("I retrieve business {string}")
  public void andIRetrieveBusiness(String id) throws Exception {
    retrievedBusinessId = parseLong(id);
    response =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/businesses/" + id).cookie(sessionId))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn();
    Assertions.assertNotNull(response);
  }

  @Then("The retrieved business maps to the correct business")
  public void theRetrievedBusinessMapsToTheCorrectBusiness() throws UnsupportedEncodingException {
    String expectedResponse =
        objectJSONMapper.businessToResponse(
            businessMap.get(retrievedBusinessId), retrievedBusinessId);
    Assertions.assertEquals(expectedResponse, response.getResponse().getContentAsString());
  }

  @Given("A user is not logged in")
  public void aUserIsNotLoggedIn() {
    // Not implemented
  }
}
