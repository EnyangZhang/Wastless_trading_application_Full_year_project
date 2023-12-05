package wasteless.controller.userController.postUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.controller.jsonobjects.UserJson;
import wasteless.controller.userController.UserControllerMethodTest;
import wasteless.exception.BadRequestException;
import wasteless.exception.ConflictException;
import wasteless.model.User;
import wasteless.security.AuthUtil;
import wasteless.service.UserService;

import java.time.LocalDate;

class PostUserTest extends UserControllerMethodTest {

  @MockBean private UserService userService;

  @MockBean private AuthUtil authUtil;

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
    Mockito.when(userService.createUser(Mockito.any(UserJson.class))).thenReturn(new User());
  }

  @Test
  void postValidUserReturns201() throws Exception {
    String user = constructTestUser();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void postUserInvalidEmailReturns400() throws Exception {
    String user = constructTestUser();
    Mockito.when(userService.createUser(Mockito.any(UserJson.class)))
        .thenThrow(new BadRequestException(""));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postUserUnderageReturns400() throws Exception {
    LocalDate currentDate = LocalDate.now();
    currentDate.minusYears(13);
    currentDate.minusDays(1);
    dateOfBirth = currentDate.toString();
    String user = constructTestUser();
    Mockito.when(userService.createUser(Mockito.any(UserJson.class)))
        .thenThrow(new BadRequestException(""));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postUserDuplicateEmailReturns409() throws Exception {
    String user = constructTestUser();
    Mockito.when(userService.createUser(Mockito.any(UserJson.class)))
        .thenThrow(new ConflictException(""));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user))
        .andExpect(MockMvcResultMatchers.status().isConflict());
  }
}
