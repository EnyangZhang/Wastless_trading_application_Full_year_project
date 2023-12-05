package wasteless.controller.businessController.postBusiness;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.model.Business;
import wasteless.repository.BusinessRepository;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@SpringBootTest
@AutoConfigureMockMvc
class PostBusinessTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BusinessRepository businessRepository;

  private String name, description, businessType, primaryAdminId;
  private String streetNumber, streetName, suburb, city, region, country, postcode;

  private String constructTestBusiness() {
    return String.format(
        "{"
            + "\"name\": \"%s\","
            + "\"description\": \"%s\","
            + "\"address\": {"
            + "\"streetNumber\": \"%s\","
            + "\"streetName\": \"%s\","
            + "\"suburb\": \"%s\","
            + "\"city\": \"%s\","
            + "\"region\": \"%s\","
            + "\"country\": \"%s\","
            + "\"postcode\": \"%s\""
            + "},"
            + "\"businessType\": \"%s\","
            + "\"primaryAdminId\": %s"
            + "}",
        name,
        description,
        streetNumber,
        streetName,
        suburb,
        city,
        region,
        country,
        postcode,
        businessType,
        primaryAdminId);
  }

  @BeforeEach
  void setup() {
    name = "McRonald's";
    description = "Fast Food";
    streetNumber = "75";
    streetName = "Burger Lane";
    suburb = "Ilam";
    city = "Christchurch";
    region = "Canterbury";
    country = "New Zealand";
    postcode = "8041";
    businessType = "Accommodation and Food Services";
    primaryAdminId = "1";
    when(businessRepository.save(any(Business.class))).thenReturn(new Business());
  }

  @Test
  void createBusinessNotLoggedIn() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(constructTestBusiness()))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser
  @Test
  void createBusinessAnonymousUser() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(constructTestBusiness()))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser(username = " ")
  @Test
  void createBusinessLoggedInInvalidUser() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(constructTestBusiness()))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenReturns201() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(constructTestBusiness()))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsToBusinessName() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(name, businessCaptor.getValue().getName());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsToBusinessType() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(businessType, businessCaptor.getValue().getBusinessType());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsToBusinessDescription() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(description, businessCaptor.getValue().getDescription());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsToBusinessStreetNumber() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(streetNumber, businessCaptor.getValue().getAddress().getStreetNumber());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsToBusinessStreetName() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(streetName, businessCaptor.getValue().getAddress().getStreetName());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsToBusinessCity() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(city, businessCaptor.getValue().getAddress().getCity());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsToBusinessRegion() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(region, businessCaptor.getValue().getAddress().getRegion());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsToBusinessCountry() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(country, businessCaptor.getValue().getAddress().getCountry());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsToBusinessPostcode() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(postcode, businessCaptor.getValue().getAddress().getPostcode());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminSize() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(1, businessCaptor.getValue().getAdmins().size());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminId() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(1L, businessCaptor.getValue().getAdmins().get(0).getUserId());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectPrimaryAdminId() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(1L, businessCaptor.getValue().getPrimaryAdminId());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminFirstName() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals("David", businessCaptor.getValue().getAdmins().get(0).getFirstName());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminLastName() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals("Enyang", businessCaptor.getValue().getAdmins().get(0).getLastName());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminMiddleName() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals("adama", businessCaptor.getValue().getAdmins().get(0).getMiddleName());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminNickname() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals("DAE", businessCaptor.getValue().getAdmins().get(0).getNickname());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminBio() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(
        "Definitely a real person", businessCaptor.getValue().getAdmins().get(0).getBio());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminEmail() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(
        "test@test.com", businessCaptor.getValue().getAdmins().get(0).getEmail());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminDateOfBirth() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(
        LocalDate.parse("2001-01-01"),
        businessCaptor.getValue().getAdmins().get(0).getDateOfBirth());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminPhone() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(
        "0800 838383", businessCaptor.getValue().getAdmins().get(0).getPhoneNumber());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminRole() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals("user", businessCaptor.getValue().getAdmins().get(0).getRole());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminStreetNumber() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(
        "2176", businessCaptor.getValue().getAdmins().get(0).getHomeAddress().getStreetNumber());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminStreetName() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(
        "Paul Wayne Haggerty Road",
        businessCaptor.getValue().getAdmins().get(0).getHomeAddress().getStreetName());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminCity() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(
        "Metairie", businessCaptor.getValue().getAdmins().get(0).getHomeAddress().getCity());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminRegion() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(
        "Louisiana", businessCaptor.getValue().getAdmins().get(0).getHomeAddress().getRegion());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminCountry() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(
        "United States of America",
        businessCaptor.getValue().getAdmins().get(0).getHomeAddress().getCountry());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void createBusinessLoggedInThenMapsCorrectAdminPostcode() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(constructTestBusiness()));
    ArgumentCaptor<Business> businessCaptor = ArgumentCaptor.forClass(Business.class);
    verify(businessRepository, times(1)).save(businessCaptor.capture());
    Assertions.assertEquals(
        "70001", businessCaptor.getValue().getAdmins().get(0).getHomeAddress().getPostcode());
  }

  // Test with a user who is exactly 15 (birthday = current date - 15 years)
  @WithMockUser(username = "test1@test.com")
  @Test
  void createBusinessLoggedInUnderage_edge() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(constructTestBusiness()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  // Test with a user who is exactly 16 (birthday = current date - 16 years)
  @WithMockUser(username = "MichelleGJarvis@dayrep.com")
  @Test
  void createBusinessLoggedInAgeValid_edge() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(constructTestBusiness()))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  // Test with user who is 13 (birthday = current date - 13 years)
  @WithMockUser(username = "michaelcblack@rhyta.com")
  @Test
  void createBusinessLoggedInUnderage_max() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(constructTestBusiness()))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
}
