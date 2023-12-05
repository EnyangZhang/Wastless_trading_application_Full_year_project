package wasteless.controller.userController.deleteUser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.controller.userController.UserControllerMethodTest;
import wasteless.repository.UserRepository;

class DeleteUserTest extends UserControllerMethodTest {

  @Autowired private UserRepository userRepository;

  @Test
  void deleteUser_2NotLoggedIn() throws Exception {
    long id = 2L;
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser
  @Test
  void deleteUser_2LoggedInAsAnonymousUser() throws Exception {
    long id = 2L;
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser(username = "thisusercannotexist")
  @Test
  void deleteUser_2LoggedInAsNonExistentUser() throws Exception {
    long id = 2L;
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void deleteUser_2LoggedInAsUser() throws Exception {
    long id = 2L;
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void deleteUser_2LoggedInAsUserDoesNotRemoveUser() throws Exception {
    long id = 2L;
    mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + id));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser(username = "test1@test.com")
  @Test
  void deleteUser_2LoggedInAsAdminReturnOK() throws Exception {
    long id = 2L;
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void deleteUser_2LoggedInAsDGAAReturnOK() throws Exception {
    long id = 2L;
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser(username = "test1@test.com")
  @Test
  void deleteUser_2LoggedInAsAdminRemovesUser() throws Exception {
    long id = 2L;
    mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + id));
    Assertions.assertNull(userRepository.findById(id).orElse(null));
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void deleteUser_2LoggedInAsDGAARemovesUser() throws Exception {
    long id = 2L;
    mockMvc.perform(MockMvcRequestBuilders.delete("/users/" + id));
    Assertions.assertNull(userRepository.findById(id).orElse(null));
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void deleteNonExistentUserLoggedInAsUser() throws Exception {
    long id = -1L;
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser(username = "test1@test.com")
  @Test
  void deleteNonExistentUserLoggedInAsAdmin() throws Exception {
    long id = -1L;
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void deleteNonExistentUserLoggedInAsDGAA() throws Exception {
    long id = -1L;
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser(username = "david@test.com")
  @Test
  void deletePrimaryOwnerUserLoggedInAsUser() throws Exception {
    long id = 1L;
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser(username = "test1@test.com")
  @Test
  void deletePrimaryOwnerUserLoggedInAsAdmin() throws Exception {
    long id = 1L;
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void deletePrimaryOwnerUserLoggedInAsDGAA() throws Exception {
    long id = 1L;
    mockMvc
        .perform(MockMvcRequestBuilders.delete("/users/" + id))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }
}
