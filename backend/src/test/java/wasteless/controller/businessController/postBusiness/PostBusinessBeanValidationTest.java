package wasteless.controller.businessController.postBusiness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.controller.BusinessController;

@SpringBootTest
@AutoConfigureMockMvc
class PostBusinessBeanValidationTest {

  @MockBean private BusinessController businessController;

  @Autowired private MockMvc mockMvc;

  private String business;
  private String name, description, businessType, primaryAdminId;
  private String streetNumber, streetName, suburb, city, region, country, postcode;

  private String constructTestBusiness() {
    return String.format(
        "{"
            + "\"name\": %s,"
            + "\"description\": %s,"
            + "\"address\": {"
            + "\"streetNumber\": %s,"
            + "\"streetName\": %s,"
            + "\"suburb\": %s," 
            + "\"city\": %s,"
            + "\"region\": %s,"
            + "\"country\": %s,"
            + "\"postcode\": %s"
            + "},"
            + "\"businessType\": %s,"
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
  public void setup() {
    name = "\"McRonald's\"";
    description = "\"Fast Food\"";
    streetNumber = "\"75\"";
    streetName = "\"Burger Lane\"";
    suburb = "\"Ilam\"";
    city = "\"Christchurch\"";
    region = "\"Canterbury\"";
    country = "\"New Zealand\"";
    postcode = "\"8041\"";
    businessType = "\"Accommodation and Food Services\"";
    primaryAdminId =
        "\"1\""; // TODO Not compared. Will conflict given the current way we set admin ID is from
    // user, not body
  }

  @Test
  void postValidBusinessRequest() throws Exception {
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void postValidBusinessRequestOnlyRequiredFields() throws Exception {
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void postBusinessRequestEmptyName() throws Exception {
    name = "\"\"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestBlankName() throws Exception {
    name = "\" \"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestName_size250Returns201() throws Exception {
    name = "\"";
    for (int i = 0; i < 250; i++) {
      name = name.concat("a");
    }
    name = name.concat("\"");
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void postBusinessRequestName_size251Returns400() throws Exception {
    name = "\"";
    for (int i = 0; i < 251; i++) {
      name = name.concat("a");
    }
    name = name.concat("\"");
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestName_size3000Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 3000; i++) {
      content = content.concat("a");
    }
    name = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestNullName() throws Exception {
    name = "";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestDescription_size2000Returns201() throws Exception {
    String content = "";
    for (int i = 0; i < 2000; i++) {
      content = content.concat("a");
    }
    description = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void postBusinessRequestDescription_size2001Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 2001; i++) {
      content = content.concat("a");
    }
    description = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestDescription_size3000Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 3000; i++) {
      content = content.concat("a");
    }
    description = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestEmptyStreetNumber() throws Exception {
    streetNumber = "\"\"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestNullStreetNumber() throws Exception {
    streetNumber = "";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestBlankStreetNumber() throws Exception {
    streetNumber = "\" \"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestStreetNumber_size250Returns201() throws Exception {
    String content = "";
    for (int i = 0; i < 250; i++) {
      content = content.concat("a");
    }
    streetNumber = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void postBusinessRequestStreetNumber_size251Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 251; i++) {
      content = content.concat("a");
    }
    streetNumber = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestStreetNumber_size3000Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 3000; i++) {
      content = content.concat("a");
    }
    streetNumber = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestEmptyStreetName() throws Exception {
    streetName = "\"\"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestNullStreetName() throws Exception {
    streetName = "";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestBlankStreetName() throws Exception {
    streetName = "\" \"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestStreetName_size250Returns201() throws Exception {
    String content = "";
    for (int i = 0; i < 250; i++) {
      content = content.concat("a");
    }
    streetName = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void postBusinessRequestStreetName_size251Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 251; i++) {
      content = content.concat("a");
    }
    streetName = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestStreetName_size3000Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 3000; i++) {
      content = content.concat("a");
    }
    streetName = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

    @Test
    void postBusinessRequestEmptySuburb() throws Exception {
        suburb = "\"\"";
        business = constructTestBusiness();
        mockMvc.perform(MockMvcRequestBuilders.post("/businesses").content(business)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void postBusinessRequestNullSuburb() throws Exception {
        suburb = "";
        business = constructTestBusiness();
        mockMvc.perform(MockMvcRequestBuilders.post("/businesses").content(business)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void postBusinessRequestBlankSuburb() throws Exception {
        suburb = "\" \"";
        business = constructTestBusiness();
        mockMvc.perform(MockMvcRequestBuilders.post("/businesses").content(business)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void postBusinessRequestSuburb_size250Returns201() throws Exception {
        String content = "";
        for (int i = 0; i < 250; i++) {
            content = content.concat("a");
        }
        suburb = String.format("\"%s\"", content);
        business = constructTestBusiness();
        mockMvc.perform(MockMvcRequestBuilders.post("/businesses").content(business)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    void postBusinessRequestSuburb_size251Returns400() throws Exception {
        String content = "";
        for (int i = 0; i < 251; i++) {
            content = content.concat("a");
        }
        suburb = String.format("\"%s\"", content);
        business = constructTestBusiness();
        mockMvc.perform(MockMvcRequestBuilders.post("/businesses").content(business)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void postBusinessRequestSuburb_size3000Returns400() throws Exception {
        String content = "";
        for (int i = 0; i < 3000; i++) {
            content = content.concat("a");
        }
        suburb = String.format("\"%s\"", content);
        business = constructTestBusiness();
        mockMvc.perform(MockMvcRequestBuilders.post("/businesses").content(business)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

  @Test
  void postBusinessRequestEmptyCity() throws Exception {
    city = "\"\"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestNullCity() throws Exception {
    city = "";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestBlankCity() throws Exception {
    city = "\" \"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestCity_size250Returns201() throws Exception {
    String content = "";
    for (int i = 0; i < 250; i++) {
      content = content.concat("a");
    }
    city = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void postBusinessRequestCity_size251Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 251; i++) {
      content = content.concat("a");
    }
    city = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestCity_size3000Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 3000; i++) {
      content = content.concat("a");
    }
    city = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestEmptyRegion() throws Exception {
    region = "\"\"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestNullRegion() throws Exception {
    region = "";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestBlankRegion() throws Exception {
    region = "\" \"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestRegion_size250Returns201() throws Exception {
    String content = "";
    for (int i = 0; i < 250; i++) {
      content = content.concat("a");
    }
    region = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void postBusinessRequestRegion_size251Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 251; i++) {
      content = content.concat("a");
    }
    region = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestRegion_size3000Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 3000; i++) {
      content = content.concat("a");
    }
    region = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestEmptyCountry() throws Exception {
    country = "\"\"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestNullCountry() throws Exception {
    country = "";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestBlankCountry() throws Exception {
    country = "\" \"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestCountry_size251Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 251; i++) {
      content = content.concat("a");
    }
    country = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestCountry_size250Returns201() throws Exception {
    String content = "";
    for (int i = 0; i < 250; i++) {
      content = content.concat("a");
    }
    country = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void postBusinessRequestCountry_size3000Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 3000; i++) {
      content = content.concat("a");
    }
    country = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestEmptyPostcode() throws Exception {
    postcode = "\"\"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestNullPostcode() throws Exception {
    postcode = "";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestBlankPostcode() throws Exception {
    postcode = "\" \"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestPostcode_size250Returns201() throws Exception {
    String content = "";
    for (int i = 0; i < 250; i++) {
      content = content.concat("a");
    }
    postcode = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @Test
  void postBusinessRequestPostcode_size251Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 251; i++) {
      content = content.concat("a");
    }
    postcode = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessRequestPostcode_size3000Returns400() throws Exception {
    String content = "";
    for (int i = 0; i < 3000; i++) {
      content = content.concat("a");
    }
    postcode = String.format("\"%s\"", content);
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessInvalidType() throws Exception {
    businessType = "Accomodation and Food Services2";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessEmptyType() throws Exception {
    businessType = "\"\"";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessNullType() throws Exception {
    businessType = "";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void postBusinessNullPrimaryAdminId() throws Exception {
    primaryAdminId = "";
    business = constructTestBusiness();
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses")
                .content(business)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
}
