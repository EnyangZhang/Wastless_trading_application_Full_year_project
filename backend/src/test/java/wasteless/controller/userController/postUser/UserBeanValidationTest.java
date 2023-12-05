package wasteless.controller.userController.postUser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.controller.UserController;
import wasteless.controller.userController.UserControllerMethodTest;
import wasteless.model.User;
import wasteless.security.AuthUtil;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
class UserBeanValidationTest extends UserControllerMethodTest {

  @Autowired
  private UserController userController;

    @BeforeEach
    void setup() {
        firstName = "\"John\"";
        lastName = "\"Johnson\"";
        middleName = "\"Doe\"";
        nickName = "\"JD\"";
        bio = "\"Definitely a real person\"";
        email = "\"jdo@email.com\"";
        dateOfBirth = "\"1990-12-01\"";
        phoneNumber = "\"0800 123123\"";
        streetNumber = "\"1716\"";
        streetName = "\"Timber Ridge Road\"";
        suburb = "\"Ilam\"";
        city = "\"Roseville\"";
        region = "\"California\"";
        country = "\"United States of America\"";
        postcode = "\"95678\"";
        password = "\"password123\"";
    }

  @Test
  void postValidUserRequest() throws Exception {
    user = constructTestUser();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void postValidUserRequestOnlyRequiredFields() throws Exception {
    middleName = "\"\"";
    nickName = "\"\"";
    bio = "\"\"";
    phoneNumber = "\"\"";
    user = constructTestUser();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @ParameterizedTest
  @CsvSource({"\"TestName\", 201", "\"\", 400", "\" \", 400", ", 400"})
  void postUser_requestInvalidFirstName_returnsCorrectStatusCodes(String input, int expected)
      throws Exception {
    firstName = input;
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"250,201", "251,400", "3000,400"})
  void postUser_RequestFirsNameSizes_returnCorrectStatusCodes(int size, int expected)
      throws Exception {
    String content = "";
    for (int i = 0; i < size; i++) {
      content = content.concat("a");
    }
    firstName = String.format("\"%s\"", content);
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"250,201", "251,400", "3000,400"})
  void postUser_Request_LastNameSizes_returnCorrectStatusCodes(int size, int expected)
      throws Exception {
    String content = "";
    for (int i = 0; i < size; i++) {
      content = content.concat("a");
    }
    lastName = String.format("\"%s\"", content);
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"250,201", "251,400", "3000,400"})
  void postUser_RequestMiddleNameSizes_returnCorrectStatusCodes(int size, int expected)
      throws Exception {
    String content = "";
    for (int i = 0; i < size; i++) {
      content = content.concat("a");
    }
    middleName = String.format("\"%s\"", content);
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"2000,201", "2001,400", "3000,400"})
  void postUser_requestBioSizes_returnCorrectStatusCodes(int size, int expected) throws Exception {
    String content = "";
    for (int i = 0; i < size; i++) {
      content = content.concat("a");
    }
    bio = String.format("\"%s\"", content);
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"\"\", 400", "\" \", 400", ", 400", "\"jj2email.com\", 400"})
  void postUser_email_returnsCorrectStatusCodes(String input, int expected) throws Exception {
    email = input;
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({
    "64, 201", "65, 400",
  })
  void postUser_requestEmail_UsernameSize_returnsCorrectStatusCodes(int size, int expected)
      throws Exception {
    String tail = "@email.com";
    String content = "";
    for (int i = 0; i < size; i++) {
      content = content.concat("a");
    }
    email = String.format("\"%s\"", content.concat(tail));
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({
    "64, 67, 201",
    "65, 67, 400",
    "64, 68, 400",
    "63, 68, 400",
    "65, 66, 400",
    "3000, 3000, 400"
  })
  void postUser_requestEmail_UsernameAndDomainSize_returnsCorrectStatusCodes(
      int uSize, int dSize, int expected) throws Exception {
    String middle = "@";
    String domain = "";
    String tail = ".com";
    for (int i = 0; i < dSize - tail.length(); i++) {
      domain = domain.concat("a");
    }
    String content = "";
    for (int i = 0; i < uSize; i++) {
      content = content.concat("a");
    }
    email = String.format("\"%s\"", content.concat(middle).concat(domain).concat(tail));
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @Test
  void postUserRequestEmail_size3000Returns400() throws Exception {
    String tail = "@email.com";
    String content = "";
    for (int i = 0; i < 3000 - tail.length(); i++) {
      content = content.concat("a");
    }
    email = String.format("\"%s\"", content.concat(tail));
    user = constructTestUser();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @ParameterizedTest
  @CsvSource({"\"1990/12/01\", 400", "\"1990-13-01\", 400", "\"\", 400", ", 400"})
  void postUserInvalidDateOfBirthFormat(String input, int expected) throws Exception {
    dateOfBirth = input;
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"\"\", 400", "\" \", 400", ", 400"})
  void postUser_streetNumber_returnsCorrectStatusCodes(String input, int expected)
      throws Exception {
    streetNumber = "\"\"";
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({
    "250, 201",
    "251, 400",
    "3000, 400",
  })
  void postUser_requestStreetNumberSizes_returnCorrectStatusCodes(int size, int expected)
      throws Exception {
    String content = "";
    for (int i = 0; i < size; i++) {
      content = content.concat("a");
    }
    streetNumber = String.format("\"%s\"", content);
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"\"\", 400", "\" \", 400", ", 400"})
  void postUserEmptyStreetName(String input, int expected) throws Exception {
    streetName = input;
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"250, 201", "251, 400", "3000, 400"})
  void postUser_requestStreetNameSize_returnsCorrectStatusCode(int size, int expected)
      throws Exception {
    String content = "";
    for (int i = 0; i < size; i++) {
      content = content.concat("a");
    }
    streetName = String.format("\"%s\"", content);
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"\"\", 400", "\" \", 400", ", 400"})
  void postUser_city_returnsCorrectStatusCodes(String input, int expected) throws Exception {
    city = input;
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"250, 201", "251, 400", "3000, 400"})
  void postUser_requestCitySizes_returnsCorrectStatusCodes(int size, int expected)
      throws Exception {
    String content = "";
    for (int i = 0; i < size; i++) {
      content = content.concat("a");
    }
    city = String.format("\"%s\"", content);
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

    @Test
    void postUserEmptySuburb() throws Exception {
        suburb = "\"\"";
        user = constructTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/users").content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void postUserBlankSuburb() throws Exception {
        suburb = "\" \"";
        user = constructTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/users").content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void postUserNullSuburb() throws Exception {
        suburb = "";
        user = constructTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/users").content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void postUserRequestSuburb_size250Returns201() throws Exception {
        String content = "";
        for (int i = 0; i < 250; i++) {
            content = content.concat("a");
        }
        suburb = String.format("\"%s\"", content);
        user = constructTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/users").content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void postUserRequestSuburb_size251Returns400() throws Exception {
        String content = "";
        for (int i = 0; i < 251; i++) {
            content = content.concat("a");
        }
        suburb = String.format("\"%s\"", content);
        user = constructTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/users").content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void postUserRequestSuburb_size3000Returns400() throws Exception {
        String content = "";
        for (int i = 0; i < 3000; i++) {
            content = content.concat("a");
        }
        suburb = String.format("\"%s\"", content);
        user = constructTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/users").content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void postUserEmptyCity() throws Exception {
        city = "\"\"";
        user = constructTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/users").content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void postUserBlankCity() throws Exception {
        city = "\" \"";
        user = constructTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/users").content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void postUserNullCity() throws Exception {
        city = "";
        user = constructTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/users").content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void postUserRequestCity_size250Returns201() throws Exception {
        String content = "";
        for (int i = 0; i < 250; i++) {
            content = content.concat("a");
        }
        city = String.format("\"%s\"", content);
        user = constructTestUser();
        mockMvc.perform(MockMvcRequestBuilders.post("/users").content(user)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

  @ParameterizedTest
  @CsvSource({"\"\", 400", "\" \", 400", ", 400"})
  void postUser_region_returnsCorrectStatusCodes(String input, int expected) throws Exception {
    region = input;
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"250, 201", "251, 400", "3000, 400"})
  void postUser_requestRegionSize_returnsCorrectStatusCodes(int size, int expected)
      throws Exception {
    String content = "";
    for (int i = 0; i < size; i++) {
      content = content.concat("a");
    }
    region = String.format("\"%s\"", content);
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"\"\", 400", "\" \", 400", ", 400"})
  void postUser_postCodes_returnCorrectStatusCodes(String input, int expected) throws Exception {
    region = input;
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"250, 201", "251, 400", "3000, 400"})
  void postUser_requestPostcodeSizes_returnsCorrectStatusCodes(int size, int expected)
      throws Exception {
    String content = "";
    for (int i = 0; i < size; i++) {
      content = content.concat("a");
    }
    postcode = String.format("\"%s\"", content);
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"\"\", 400", "\" \", 400", ", 400"})
  void postUser_passwords_returnsCorrectStatusCodes(String input, int expected) throws Exception {
    password = input;
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @ParameterizedTest
  @CsvSource({"250, 201", "251, 400", "3000, 400"})
  void postUser_requestPasswordSizes_returnsCorrectStatusCodes(int size, int expected)
      throws Exception {
    String content = "";
    for (int i = 0; i < size; i++) {
      content = content.concat("a");
    }
    password = String.format("\"%s\"", content);
    user = constructTestUser();
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.post("/users")
                    .content(user)
                    .contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    Assertions.assertEquals(expected, mvcResult.getResponse().getStatus());
  }

  @Test
  void postUserFutureDateOfBirth() throws Exception {
    dateOfBirth = "\"2050-12-01\"";
    user = constructTestUser();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postUserExactlyThirteen() throws Exception {
    LocalDate currentDate = LocalDate.now();
    dateOfBirth = String.format("\"%s\"", currentDate.minusYears(13));
    user = constructTestUser();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .content(user)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @MockBean AuthUtil authUtil;
  @Test
  void getUserWithId() throws Exception {
    Mockito.when(authUtil.getCurrentUser()).thenReturn(new User());

    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
