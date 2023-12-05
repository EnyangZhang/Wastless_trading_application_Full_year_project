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

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@SpringBootTest
@AutoConfigureMockMvc
class GetBusinessAdminTest {

  @Autowired
  private MockMvc mockMvc;

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_1LoggedInReturnsCorrectAdminsType() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins").isArray());
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsType() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins").isArray());
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_1LoggedInReturnsCorrectAdminsSize() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins.length()").value(1));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsSize() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins.length()").value(2));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_1LoggedInReturnsCorrectAdminsId() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].id").value("1"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsIdAdmin_0() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].id").value("1"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsIdAdmin_1() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[1].id").value("5"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_1LoggedInReturnsCorrectAdminsFirstName() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].firstName").value("David"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsFirstNameAdmin_1() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].firstName").value("David"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsFirstNameAdmin_2() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[1].firstName").value("Heron"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_1LoggedInReturnsCorrectAdminsMiddleName() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].middleName").value("adama"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsMiddleNameAdmin_1() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].middleName").value("adama"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsMiddleNameAdmin_2() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[1].middleName").value("Ashley"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_1LoggedInReturnsCorrectAdminsLastName() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].lastName").value("Enyang"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsLastNameAdmin_1() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].lastName").value("Enyang"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsLastNameAdmin_2() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[1].lastName").value("Waller"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_1LoggedInReturnsCorrectAdminsNickName() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].nickname").value("DAE"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsNickNameAdmin_1() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].nickname").value("DAE"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsNickNameAdmin_2() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[1].nickname").value("Wally"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_1LoggedInReturnsCorrectAdminsBio() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("admins[0].bio").value("Definitely a real person"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsBioAdmin_1() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("admins[0].bio").value("Definitely a real person"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdminsBioAdmin_2() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[1].bio").value(""));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_1LoggedInReturnsCorrectAdminsEmail() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].email").value("test@test.com"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmins_1Email() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].email").value("test@test.com"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmins_2Email() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[1].email").value("heronwaller@test.com"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_1LoggedInReturnsCorrectAdminsDoB() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].dateOfBirth").value("2001-01-01"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmins_1DoB() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].dateOfBirth").value("2001-01-01"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_2sDoB() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[1].dateOfBirth").value("1997-02-12"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_1LoggedInReturnsCorrectAdminsPhoneNumber() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].phoneNumber").value("0800 838383"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmins_1PhoneNumber() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].phoneNumber").value("0800 838383"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmins_2PhoneNumber() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[1].phoneNumber").value("0800 123456"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_1StreetNumber() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("admins[0].homeAddress.streetNumber").value(2176));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_2StreetNumber() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[1].homeAddress.streetNumber").value(560));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_1StreetName() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("admins[0].homeAddress.streetName")
                .value("Paul Wayne Haggerty Road"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_2StreetName() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("admins[1].homeAddress.streetName")
                .value("Rua Planalto"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_1City() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].homeAddress.city").value("Metairie"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_2City() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[1].homeAddress.city").value("Petrolina"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_1Region() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("admins[0].homeAddress.region").value("Louisiana"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_2Region() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("admins[1].homeAddress.region").value("Pernambuco"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_1Country() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("admins[0].homeAddress.country")
                .value("United States of America"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_2Country() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[1].homeAddress.country").value("Brazil"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_1Postcode() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].homeAddress.postcode").value("70001"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmin_2Postcode() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/2"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("admins[1].homeAddress.postcode").value("56310-320"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_1LoggedInReturnsCorrectAdminsRole() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].role").value("user"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmins_1Role() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].role").value("user"));
  }

  @WithMockUser("test@test.com")
  @Test
  void getBusiness_2LoggedInReturnsCorrectAdmins_2Role() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1"))
        .andExpect(MockMvcResultMatchers.jsonPath("admins[0].role").value("user"));
  }
}
