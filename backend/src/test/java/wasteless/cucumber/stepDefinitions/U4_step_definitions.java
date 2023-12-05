package wasteless.cucumber.stepDefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.ObjectJSONMapper;
import wasteless.cucumber.CucumberSpringConfiguration;
import wasteless.model.Address;
import wasteless.model.User;
import wasteless.repository.UserRepository;

import javax.servlet.http.Cookie;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class U4_step_definitions extends CucumberSpringConfiguration {

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
  private final User testUser =
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
  /**
   * The security context to send along with a makeadmin/revokeadmin request. Set this variable with
   * the function: user(userEmail) MockMvc has a 'with' clause, so you put this variable as the
   * argument and the server responds as if the user with the given email has logged in already.
   */
  SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userRequestPostProcessor = null;
  @Autowired private UserRepository userRepository;
  private ObjectMapper objectMapper;
  private ObjectJSONMapper objectJSONMapper;
  private Cookie session;
  private String userJSONRequest;
  private MvcResult result;
  @Autowired private DataSource ds;

  @Before
  public void setup() throws SQLException {
    objectMapper = new ObjectMapper();
    objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    objectJSONMapper = new ObjectJSONMapper();
    session = new Cookie("JSESSIONID", "unset");
  }

  @Given("A user exists with user ID: {long}")
  public void createUserAndCheckId(long id) throws Exception {
    // create new test user
    ScriptUtils.executeSqlScript(ds.getConnection(), new ClassPathResource("testData/CreateDBTables.sql"));
    ScriptUtils.executeSqlScript(ds.getConnection(), new ClassPathResource("testData/U4Data.sql"));
    userJSONRequest = objectJSONMapper.userToRequest(this.testUser);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/users")
            .content(userJSONRequest)
            .contentType(MediaType.APPLICATION_JSON));

    // check that there is now a user with User ID = id, with the role 'role'

    User user = userRepository.findById(id).orElse(null);
    Assertions.assertNotNull(user);

    Assertions.assertEquals("user", user.getRole());
  }

  @Given("A user with user ID: {long} does not exist")
  public void checkNoUserExists(long id) {
    User user = userRepository.findById(id).orElse(null);
    Assertions.assertNull(user);
  }

  @Given("A global application admin exists with user ID: {long}")
  public void createAdminAndCheckId(long id) throws Exception {
    // create new test user
    userJSONRequest = objectJSONMapper.userToRequest(this.testUser);
    mockMvc.perform(
        MockMvcRequestBuilders.post("/users")
            .content(userJSONRequest)
            .contentType(MediaType.APPLICATION_JSON));

    // make the test user and admin
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/users/" + id + "/makeadmin")
                .with(user("admin@defaultglobal")))
        .andExpect(MockMvcResultMatchers.status().isOk());

    // check that there is now a user with User ID = id, with the role 'role'
    User user = userRepository.findById(id).orElse(null);
    Assertions.assertNotNull(user);
    Assertions.assertEquals("global_admin", user.getRole());
  }

  @Given("There is no user logged in")
  public void emptyAdminUsername() {
    userRequestPostProcessor = null;
  }

  @Given("A user whose role is {string} is logged in")
  public void setAdminUsername(String role) throws Exception {
    if (role.equals("DGAA")) {
      userRequestPostProcessor = user("admin@defaultglobal");
      User dgaa = userRepository.findByRole("default_global_admin");
      if (dgaa == null) {
        dgaa = testUser;
        dgaa.setRole("default_global_admin");
      }
      dgaa.setEmail("admin@defaultglobal");
      userRepository.save(dgaa);
    } else if (role.equals("user")) {
      // create a 3rd user
      testUser.setEmail("test2@test.com");
      userJSONRequest = objectJSONMapper.userToRequest(this.testUser);
      mockMvc.perform(
          MockMvcRequestBuilders.post("/users")
              .content(userJSONRequest)
              .contentType(MediaType.APPLICATION_JSON));
      userRequestPostProcessor = user("test2@test.com");
    } else {
      userRequestPostProcessor = null;
    }
  }

  @When("The user tries to {string} the user with ID = {string} as an admin")
  public void adminRequest(String adminAction, String userId) throws Exception {
    String request = "";
    if (adminAction.equals("add")) {
      request = "makeadmin";
    } else if (adminAction.equals("remove")) {
      request = "revokeadmin";
    }

    if (userRequestPostProcessor == null) {
      result =
          mockMvc
              .perform(MockMvcRequestBuilders.put("/users/" + userId + "/" + request))
              .andReturn();
    } else {
      result =
          mockMvc
              .perform(
                  MockMvcRequestBuilders.put("/users/" + userId + "/" + request)
                      .with(userRequestPostProcessor))
              .andReturn();
    }
  }

  @Then("The user with user ID: {long} has the role: {string}")
  public void checkUserIsUpdated(long id, String expectedRole) {
    User user = userRepository.findById(id).orElse(null);
    Assertions.assertNotNull(user);
    Assertions.assertEquals(expectedRole, user.getRole());
  }

  @Then("The server responds to the DGAA request with a {int} status code")
  public void serverResponse(int status) {
    Assertions.assertEquals(status, result.getResponse().getStatus());
  }
}
