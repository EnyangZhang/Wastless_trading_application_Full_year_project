package wasteless.controller.businessController.getBusiness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;
import wasteless.model.InventoryItem;
import wasteless.model.Product;
import wasteless.model.SaleItem;
import wasteless.service.BusinessService;
import wasteless.test_helpers.BusinessDataCreator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class GetSaleItemControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private SaleItem item;

  @MockBean
  private BusinessService businessService;

  @BeforeEach
  void setup() {
    Product product = BusinessDataCreator.createProduct("WATT-420-BEANS");

    InventoryItem inventoryItem = BusinessDataCreator.createInventoryItem(1L, product);

    item = new SaleItem();
    item.setSaleItemId(1L);
    item.setInventoryItem(inventoryItem);
    item.setQuantity(1);
    item.setPrice(56.78);
    item.setCreated(LocalDateTime.now());
  }

  @Test
  void getSaleItems_unauthorizedException_returns401() throws Exception {
    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean()))
        .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings/"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_badRequestException_returns400() throws Exception {
    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean()))
        .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings/"))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returns200() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCorrectId() throws Exception {

    List<SaleItem> items = new ArrayList<SaleItem>();
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(item.getSaleItemId()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCorrectInventoryItemId() throws Exception {
    List<SaleItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].inventoryItem.inventoryItemId")
                .value(item.getInventoryItem().getInventoryItemId()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCorrectQuantity() throws Exception {
    List<SaleItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].quantity").value(item.getQuantity()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCorrectPrice() throws Exception {
    List<SaleItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].price").value(item.getPrice()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCorrectInfo() throws Exception {
    List<SaleItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].moreInfo").value(item.getMoreInfo()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCreated() throws Exception {
    List<SaleItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].created").isNotEmpty());
  }

  @WithMockUser("test@test.com")
  @Test
  void getSaleItems_loggedIn_returnsCorrectCloses() throws Exception {
    List<SaleItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].closes").value(item.getCloses()));
  }


  // Test getting sold sale listings

  @WithMockUser("test@test.com")
  @Test
  void getSoldItems_loggedIn_returnsCorrectId() throws Exception {

    List<SaleItem> items = new ArrayList<SaleItem>();
    item.setSold(true);
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
            .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=true"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].id").value(item.getSaleItemId()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSoldItems_loggedIn_returnsCorrectInventoryItemId() throws Exception {
    List<SaleItem> items = new ArrayList<>();
    item.setSold(true);
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
            .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
            .andExpect(
                    MockMvcResultMatchers.jsonPath("$.[0].inventoryItem.inventoryItemId")
                            .value(item.getInventoryItem().getInventoryItemId()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSoldItems_loggedIn_returnsCorrectQuantity() throws Exception {
    List<SaleItem> items = new ArrayList<>();
    item.setSold(true);
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
            .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=true"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].quantity").value(item.getQuantity()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSoldItems_loggedIn_returnsCorrectPrice() throws Exception {
    List<SaleItem> items = new ArrayList<>();
    item.setSold(true);
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
            .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=false"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].price").value(item.getPrice()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSoldItems_loggedIn_returnsCorrectInfo() throws Exception {
    List<SaleItem> items = new ArrayList<>();
    item.setSold(true);
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
            .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=true"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].moreInfo").value(item.getMoreInfo()));
  }

  @WithMockUser("test@test.com")
  @Test
  void getSoldItems_loggedIn_returnsCreated() throws Exception {
    List<SaleItem> items = new ArrayList<>();
    item.setSold(true);
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
            .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=true"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].created").isNotEmpty());
  }

  @WithMockUser("test@test.com")
  @Test
  void getSoldItems_loggedIn_returnsCorrectCloses() throws Exception {
    List<SaleItem> items = new ArrayList<>();
    item.setSold(true);
    items.add(item);

    Mockito.when(businessService.retrieveSaleItems(Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(items);
    mockMvc
            .perform(MockMvcRequestBuilders.get("/businesses/1/listings?isSold=true"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.[0].closes").value(item.getCloses()));
  }
}
