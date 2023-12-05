package wasteless.controller.userController.putUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.controller.jsonobjects.UserJson;
import wasteless.controller.userController.UserControllerMethodTest;
import wasteless.exception.ConflictException;
import wasteless.model.User;
import wasteless.security.AuthUtil;
import wasteless.service.UserService;

import java.util.NoSuchElementException;

class UpdateUserTest extends UserControllerMethodTest {
  @MockBean private UserService userService;

  @MockBean private AuthUtil authUtil;

  private User mockUser;

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

    mockUser = new User();
    mockUser.setUserId(1L);

    Mockito.when(userService.getUserById(Mockito.any(Long.class))).thenReturn(new User());
    Mockito.when(authUtil.getCurrentUser()).thenReturn(mockUser);
  }

  @Test
  void whenPutIdIsNotSameReturn406() throws Exception {
    String user = constructTestUser();
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  void whenPutValidUserReturn200() throws Exception {
    String user = constructTestUser();
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void whenPutDuplicateEmailReturn409() throws Exception {
    String user = constructTestUser();
    Mockito.when(userService.updateUser(Mockito.any(UserJson.class), Mockito.any(Long.class)))
        .thenThrow(new ConflictException(""));
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user))
        .andExpect(MockMvcResultMatchers.status().isConflict());
  }

  @Test
  void whenPutNonExistentUserReturn403() throws Exception {
    String user = constructTestUser();
    Mockito.when(userService.updateUser(Mockito.any(UserJson.class), Mockito.any(Long.class)))
        .thenThrow(new NoSuchElementException(""));
    mockMvc
        .perform(
            MockMvcRequestBuilders.put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(user))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }
}
