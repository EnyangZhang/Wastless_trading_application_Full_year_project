package wasteless.controller.businessController.getBusiness;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@SpringBootTest
@AutoConfigureMockMvc
class GetProductTest {

  @Autowired
  private MockMvc mockMvc;
  private MvcResult mvcResult;

  /** Test if user not logged in and try to get the catalogues, 401 will be returned. */
  @Test
  void getAllCatalogueItemBusinessIdOneNotLoggedIn() throws Exception {
    mvcResult =
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/" + 1 + "/products")).andReturn();
    Assertions.assertEquals(401, mvcResult.getResponse().getStatus());
  }

  @WithAnonymousUser
  @Test
  void getAllCatalogueItemBusiness_1AnonymousUser() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/products"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  /**
   * Test if user has logged in and try to get the catalogue that is not owned by the user, 403 will
   * be returned.
   */
  @WithMockUser(username = "david@test.com")
  @Test
  void getAllCatalogueItemBusinessIdOneLoggedInNotOwned() throws Exception {
    mvcResult =
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/" + 1 + "/products")).andReturn();
    Assertions.assertEquals(403, mvcResult.getResponse().getStatus());
  }

  /**
   * Test if user logged in and try to get the catalogues not owned the catalogue but is global
   * admin, 200 will be returned.
   */
  @WithMockUser(username = "test1@test.com")
  @Test
  void getAllCatalogueItemBusinessIdOneLoggedInNotOwnedButGAA() throws Exception {
    mvcResult =
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/" + 1 + "/products")).andReturn();
    Assertions.assertEquals(200, mvcResult.getResponse().getStatus());
  }

  /**
   * Test if user logged in and try to get the catalogues not owned the catalogue but is default
   * global admin, 200 will be returned.
   */
  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void getAllCatalogueItemBusinessIdOneLoggedInNotOwnedButDGAA() throws Exception {
    mvcResult =
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/" + 1 + "/products")).andReturn();
    Assertions.assertEquals(200, mvcResult.getResponse().getStatus());
  }

  /** Test if user logged in and try to get the owned catalogues, 200 will be returned. */
  @WithMockUser(username = "test@test.com")
  @Test
  void getAllCatalogueItemBusinessIdOneLoggedInOwned() throws Exception {
    mvcResult =
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/" + 1 + "/products")).andReturn();
    Assertions.assertEquals(200, mvcResult.getResponse().getStatus());
  }

  /**
   * Test if user not logged in and try to get the catalogues but no related business exists, 406
   * will be returned.
   */
  @WithMockUser(username = "test@test.com")
  @Test
  void getAllCatalogueItemBusinessIdOneLoggedInNoBusiness() throws Exception {
    mvcResult =
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/" + 90 + "/products")).andReturn();
    Assertions.assertEquals(406, mvcResult.getResponse().getStatus());
  }

  /**
   * Test if user not logged in and try to get the catalogues, but no related user exists, 406 will
   * be returned.
   */
  @WithMockUser
  @Test
  void getAllCatalogueItemBusinessIdOneLoggedInNoUser() throws Exception {
    mvcResult =
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/" + 1 + "/products")).andReturn();
    Assertions.assertEquals(401, mvcResult.getResponse().getStatus());
  }
}
