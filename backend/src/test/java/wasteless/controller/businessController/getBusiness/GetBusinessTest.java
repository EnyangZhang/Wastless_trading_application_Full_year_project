package wasteless.controller.businessController.getBusiness;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@SpringBootTest
@AutoConfigureMockMvc
class GetBusinessTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void getBusinessNotLoggedIn() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInBusinessDoesNotExist() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/200"))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsExistingBusiness() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsCorrectFormat() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsCorrectId() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("businessId").value("1"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsCorrectName() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("name").value("UC"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsCorrectDescription() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("description").value("UNIVERSITY"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsCorrectType() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("businessType")
                .value("Accommodation and Food Services"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsCorrectPrimaryAdminId() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("primaryAdminId").value("1"));
  }
}
