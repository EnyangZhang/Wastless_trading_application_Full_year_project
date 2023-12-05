package wasteless.controller.businessController.postBusiness;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.controller.jsonobjects.SaleItemJson;
import wasteless.service.BusinessService;

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@SpringBootTest
@AutoConfigureMockMvc
class PostSaleItemTest{

  @Autowired
  private MockMvc mockMvc;

  @Autowired BusinessService businessService;
  @Autowired ObjectMapper objectMapper;
  String saleItem =
      "          {\n"
          + "          \"inventoryItemId\": \"1\",\n"
          + "          \"quantity\": 5,\n"
          + "          \"price\": 4,\n"
          + "          \"moreInfo\": \"Seller may be willing to consider near offers\",\n"
          + "          \"closes\": \"2021-07-21T23:59:00Z\"\n"
          + "          }";

  SaleItemJson saleItemObj;

  @BeforeEach
  void setup() throws JsonProcessingException {
    saleItemObj = objectMapper.readValue(saleItem, SaleItemJson.class);
    businessService = Mockito.mock(BusinessService.class);
  }

  @Test
  void business_1AddSaleItemNotLoggedIn() throws Exception {
    long bueId = 1L;
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/listings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(saleItem))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithAnonymousUser
  @Test
  void business_1AddSaleItemAnonymousUser() throws Exception {
    long bueId = 1L;
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/listings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(saleItem))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void business1_SuccessAddSaleItem_Respond200() throws Exception {
    long bueId = 1L;
    long returnId = 1;
    Mockito.when(businessService.addSaleItem(Mockito.any(Long.TYPE), Mockito.any(SaleItemJson.class)))
        .thenReturn(returnId);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/listings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(saleItem))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @WithMockUser(username = "test@test.com")
  @Test
  void business1_SuccessAddSaleItemAnd_returnNewId() throws Exception {
    long bueId = 1L;
    long returnId = 1;
    Mockito.when(businessService.addSaleItem(Mockito.any(Long.TYPE), Mockito.any(SaleItemJson.class)))
        .thenReturn(returnId);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/listings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(saleItem))
        .andExpect(MockMvcResultMatchers.jsonPath("listingId").value("5"));
  }
}
