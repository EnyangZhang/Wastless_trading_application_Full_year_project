package wasteless.cucumber.stepDefinitions;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.ObjectJSONMapper;
import wasteless.cucumber.CucumberSpringConfiguration;
import wasteless.model.Address;
import wasteless.model.User;
import wasteless.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class U1_step_definitions extends CucumberSpringConfiguration {

  private final ObjectJSONMapper objectJSONMapper = new ObjectJSONMapper();

  private MvcResult result;

  @Autowired private UserRepository userRepository;

  private String backgroundEmail;

  public String createUserRequestFromDataTable(DataTable dataTable) {
    Map<String, List<String>> map = dataTable.asMap(String.class, List.class);
    String firstName = map.get("firstName").get(0);
    String middleName = map.get("middleName").get(0);
    String lastName = map.get("lastName").get(0);
    String nickname = map.get("nickname").get(0);
    String bio = map.get("bio").get(0);
    String email = map.get("email").get(0);
    String DoB = map.get("DoB").get(0);
    String phone = map.get("phone").get(0);
    String password = map.get("password").get(0);
    String streetNumber = map.get("streetNumber").get(0);
    String streetName = map.get("streetName").get(0);
    String suburb = map.get("suburb").get(0);
    String city = map.get("city").get(0);
    String postcode = map.get("postcode").get(0);
    String region = map.get("region").get(0);
    String country = map.get("country").get(0);

    backgroundEmail = email;

    Address address = new Address(streetNumber, streetName, suburb, city, region, country, postcode);
    User user =
        new User(
            firstName,
            lastName,
            middleName,
            nickname,
            bio,
            email,
            LocalDate.parse(DoB),
            phone,
            address,
            password);

    return objectJSONMapper.userToRequest(user);
  }

  @Given("A user with the following details has been registered:")
  public void a_user_with_the_following_details_has_been_registered(DataTable dataTable) throws Exception {
    String userJSONRequest = createUserRequestFromDataTable(dataTable);
    User backgroundUser = userRepository.findByEmail(backgroundEmail).orElse(null);
    if (backgroundUser == null) {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(userJSONRequest)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }
    Assertions.assertTrue(userRepository.findByEmail(backgroundEmail).isPresent());
  }

  @When("A user with the following details is registered:")
  public void a_user_with_the_following_details_is_registered(DataTable dataTable)
      throws Exception {
    String userJSONRequest = createUserRequestFromDataTable(dataTable);
    result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(userJSONRequest)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
  }

  @Given("No user exists with email {string}")
  public void no_user_exists_with_email(String email) {
    List<User> users = userRepository.findAllByEmail(email);
    Assertions.assertEquals(0, users.size());
  }

  @Given("The user with email {string} exists")
  public void the_user_with_email_exists(String email) {
    List<User> users = userRepository.findAllByEmail(email);
    Assertions.assertEquals(1, users.size());
  }

  @When("The user logs in with credentials {string} and password {string}")
  public void the_user_logs_in_with_credentials_and_password(String email, String password)
      throws Exception {
    String credentials = objectJSONMapper.mapCredentials(email, password);
    result =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/login")
                    .content(credentials)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
  }

  @Then("The user will be logged in")
  public void the_user_will_be_logged_in() {
    int status = result.getResponse().getStatus();
    Assertions.assertEquals(200, status);
  }

  @Then("The user will be registered with the email: {string}")
  public void the_user_will_be_registered(String email) {
    List<User> users = userRepository.findAllByEmail(email);
    Assertions.assertEquals(1, users.size());
  }

  @Then("The user will receive an error message of {string}")
  public void the_user_will_receive_an_error_message_of(String message) throws Exception {
    String content = result.getResponse().getContentAsString();
    Assertions.assertTrue(content.contains(message));
  }

  @Then("The user will receive a {string} error")
  public void the_user_will_receive_an_error_status_of(String statusMessage) {
    int status = 0;
    if (statusMessage.equals("unauthorized")) {
      status = 401;
    } else if (statusMessage.equals("bad request")) {
      status = 400;
    }
    Assertions.assertEquals(status, result.getResponse().getStatus());
  }
}
