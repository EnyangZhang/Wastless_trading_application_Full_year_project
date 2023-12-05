package wasteless.controller.userController.putUser;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.controller.userController.UserControllerMethodTest;

class MakeAdminTest extends UserControllerMethodTest {

  @Test
  void makeUser_1AdminNotLoggedIn() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/1/makeadmin"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser
  @Test
  void makeUser_1AdminLoggedInAnonymousUser() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/1/makeadmin"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser(username = "thisusershouldneverexist@nonexistent.com")
  @Test
  void makeUser_1AdminLoggedInNonExistentUser() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/1/makeadmin"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser(username = "david@test.com")
  @Test
  void makeUser_1AdminLoggedInAsUser() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/1/makeadmin"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser(username = "test1@test.com")
  @Test
  void makeUser_1AdminLoggedInAsGlobalAdmin() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/1/makeadmin"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void makeUser_1AdminLoggedInAsDGAA() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/1/makeadmin"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void makeUser_1AdminLoggedInAsDGAAVerifyUserIsGlobalAdmin() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/users/1/makeadmin"));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("role").value("global_admin"));
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void makeNonExistentUserAdminLoggedInAsDGAA() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/10000000/makeadmin"))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void makeAdmin_3AdminLoggedInAsDGAA() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/3/makeadmin"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void makeAdmin_3AdminLoggedInAsDGAAVerifyUserIsGlobalAdmin() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/users/3/makeadmin"));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/3"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("role").value("global_admin"));
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void makeDGAA_4AdminLoggedInAsDGAA() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/4/makeadmin"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void makeDGAA_4AdminLoggedInAsDGAAVerifyUserIsGlobalAdmin() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/users/4/makeadmin"));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/4"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("role").value("default_global_admin"));
  }
}
