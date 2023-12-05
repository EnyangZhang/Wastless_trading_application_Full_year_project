package wasteless.controller.businessController.getBusiness;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.model.InventoryItem;
import wasteless.model.Product;
import wasteless.model.SaleItem;
import wasteless.test_helpers.BusinessDataCreator;

import java.time.LocalDateTime;

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@SpringBootTest
@AutoConfigureMockMvc
class GetSaleItemIntegrationTest {

  @Autowired
  private MockMvc mockMvc;
  private MvcResult mvcResult;

  SaleItem expectedSaleItem = new SaleItem();

  @BeforeEach
  void setup() {
    Product product = BusinessDataCreator.createProduct("watt-222-222");

    InventoryItem inventoryItem = BusinessDataCreator.createInventoryItem(1L, product);

    expectedSaleItem = new SaleItem();
    expectedSaleItem.setSaleItemId(1L);
    expectedSaleItem.setInventoryItem(inventoryItem);
    expectedSaleItem.setQuantity(5);
    expectedSaleItem.setPrice(4.0);
    expectedSaleItem.setMoreInfo("Seller may be willing to consider near offers");
    expectedSaleItem.setCreated(LocalDateTime.now());
    expectedSaleItem.setCloses(LocalDateTime.now());
  }

  /*
  Authentication test
   */
  @Test
  void getSaleItems_notLoggedIn_returns401() throws Exception {
    mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/listings/")).andReturn();
    Assertions.assertEquals(401, mvcResult.getResponse().getStatus());
  }

  /*
  Assert all returned fields as expected
   */
  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCorrectId() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].id").value(expectedSaleItem.getSaleItemId()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCorrectInventoryItemId() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].inventoryItem.inventoryItemId")
                .value(expectedSaleItem.getInventoryItem().getInventoryItemId()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCorrectQuantity() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].quantity").value(expectedSaleItem.getQuantity()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCorrectPrice() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].price").value(expectedSaleItem.getPrice()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCorrectInfo() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].moreInfo").value(expectedSaleItem.getMoreInfo()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCreated() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].created").isNotEmpty());
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCloses() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].closes").isNotEmpty());
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_soldItemNotShow() throws Exception {
    mockMvc
            .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.length()").value(2));
  }
}
