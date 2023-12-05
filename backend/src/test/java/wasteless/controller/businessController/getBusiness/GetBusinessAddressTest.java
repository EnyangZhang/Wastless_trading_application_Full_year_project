package wasteless.controller.businessController.getBusiness;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import javax.servlet.http.Cookie;

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@SpringBootTest
@AutoConfigureMockMvc
class GetBusinessAddressTest {

  @Autowired
  private MockMvc mockMvc;

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsCorrectStreetNumber() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("address.streetNumber").value("10"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsCorrectStreetName() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("address.streetName").value("ENGINEER STREET"));
  }

    @WithMockUser("test@test.com")
    @Test
    void getBusinessLoggedInReturnsCorrectSuburbName() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1")
        ).andExpect(MockMvcResultMatchers.jsonPath("address.streetName").value("ENGINEER STREET"));
    }

    @WithMockUser("test@test.com")
    @Test
    void getBusinessLoggedInReturnsCorrectCity() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1")
        ).andExpect(MockMvcResultMatchers.jsonPath("address.city").value("CHRISTCHURCH"));
    }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsCorrectRegion() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("address.region").value("CANTABURY"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsCorrectCountry() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("address.country").value("NZ"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsCorrectPostcode() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1").cookie(new Cookie("JSESSIONID", "1")))
        .andExpect(MockMvcResultMatchers.jsonPath("address.postcode").value("8011"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusinessLoggedInReturnsCorrectRegistrationDate() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("registrationDate").value("2020-01-01"));
  }
}
