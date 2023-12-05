package wasteless.controller.businessController.putBusiness;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.model.Business;
import wasteless.repository.BusinessRepository;

import java.util.Optional;

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@SpringBootTest
@AutoConfigureMockMvc
class PutBusinessTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired private BusinessRepository businessRepository;

  @Test
  void transferBusiness_1PrimaryOwnershipToUser_2NotLoggedIn() throws Exception {
    long id = 1L;
    long userId = 2L;
    mockMvc
        .perform(MockMvcRequestBuilders.put("/businesses/" + id + "/transferOwnership/" + userId))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser
  @Test
  void transferBusiness_1PrimaryOwnershipToUser_2AnonymousUser() throws Exception {
    long id = 1L;
    long userId = 2L;
    mockMvc
        .perform(MockMvcRequestBuilders.put("/businesses/" + id + "/transferOwnership/" + userId))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void transferNonExistentBusinessPrimaryOwnershipToUser_2() throws Exception {
    long id = -1L;
    long userId = 2L;
    mockMvc
        .perform(MockMvcRequestBuilders.put("/businesses/" + id + "/transferOwnership/" + userId))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void transferNonExistentBusinessEdgePrimaryOwnershipToUser_2() throws Exception {
    long id = 50L;
    long userId = 2L;
    mockMvc
        .perform(MockMvcRequestBuilders.put("/businesses/" + id + "/transferOwnership/" + userId))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void transferBusiness_1PrimaryOwnershipToNonExistentUser() throws Exception {
    long id = 1L;
    long userId = -1L;
    mockMvc
        .perform(MockMvcRequestBuilders.put("/businesses/" + id + "/transferOwnership/" + userId))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void transferBusiness_1PrimaryOwnershipToNonExistentUserEdge() throws Exception {
    long id = 1L;
    long userId = 10L;
    mockMvc
        .perform(MockMvcRequestBuilders.put("/businesses/" + id + "/transferOwnership/" + userId))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void transferBusiness_1PrimaryOwnershipToUser_2ReturnsOK() throws Exception {
    long id = 1L;
    long userId = 2L;
    mockMvc
        .perform(MockMvcRequestBuilders.put("/businesses/" + id + "/transferOwnership/" + userId))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void transferBusiness_1PrimaryOwnershipToUser_2UserNoLongerOwner() throws Exception {
    long id = 1L;
    long userId = 2L;
    mockMvc.perform(
        MockMvcRequestBuilders.put("/businesses/" + id + "/transferOwnership/" + userId));
    Optional<Business> business = businessRepository.findByBusinessId(id);
    Assertions.assertNotEquals(1L, business.get().getPrimaryAdminId());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void transferBusiness_1PrimaryOwnershipToUser_2OwnershipTransferred() throws Exception {
    long id = 1L;
    long userId = 2L;
    mockMvc.perform(
        MockMvcRequestBuilders.put("/businesses/" + id + "/transferOwnership/" + userId));
    Optional<Business> business = businessRepository.findByBusinessId(id);
    Assertions.assertEquals(userId, business.get().getPrimaryAdminId());
  }

  @WithMockUser(username = "heronwaller@test.com")
  @Test
  void transferBusiness_1PrimaryOwnershipToUser_2NotPrimaryOwner() throws Exception {
    long id = 1L;
    long userId = 2L;
    mockMvc
        .perform(MockMvcRequestBuilders.put("/businesses/" + id + "/transferOwnership/" + userId))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser(username = "heronwaller@test.com")
  @Test
  void transferBusiness_2PrimaryOwnershipToUser_1NotPrimaryOwner() throws Exception {
    long id = 2L;
    long userId = 1L;
    mockMvc
        .perform(MockMvcRequestBuilders.put("/businesses/" + id + "/transferOwnership/" + userId))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }
}
