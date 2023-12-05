package wasteless.controller.userController.putUser;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@AutoConfigureMockMvc
class RevokeAdminTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void revokeAdmin_3AdminNotLoggedin() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/3/revokeadmin"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser
  @Test
  void revokeAdmin_3AdminLoggedInAnonymousUser() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/3/revokeadmin"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser(username = "thisusershouldneverexist@nonexistent.com")
  @Test
  void revokeUser_3AdminLoggedInNonExistentUser() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/3/revokeadmin"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser(username = "david@test.com")
  @Test
  void revokeAdmin_3AdminLoggedInAsUser() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/3/revokeadmin"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser(username = "test1@test.com")
  @Test
  void revokeAdmin_3AdminLoggedInAsAdmin() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/3/revokeadmin"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void revokeNonExistentUserAdminLoggedInAsDGAA() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/3000/revokeadmin"))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void revokeDGAA_4AdminLoggedInAsDGAA() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/4/revokeadmin"))
        .andExpect(MockMvcResultMatchers.status().isConflict());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void revokeAdmin_3AdminLoggedInAsDGAA() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.put("/users/3/revokeadmin"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void revokeAdmin_3LoggedInAsDGAAVerifyUser_3IsNotAdmin() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/users/3/revokeadmin"));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/3"))
        .andExpect(MockMvcResultMatchers.jsonPath("role").value("user"));
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void revokeUser_1LoggedInAsDGAAVerifyUser_1IsNotAdmin() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.put("/users/1/revokeadmin"));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("role").value("user"));
  }
}
