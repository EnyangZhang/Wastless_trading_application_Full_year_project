package wasteless.controller.businessController.getBusiness;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;
import wasteless.exception.ForbiddenException;
import wasteless.model.InventoryItem;
import wasteless.model.Product;
import wasteless.service.BusinessService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@SpringBootTest
@AutoConfigureMockMvc
class GetInventoryControllerTest {

  @Autowired
  private MockMvc mockMvc;
  private MvcResult mvcResult;

  private InventoryItem item;

  @MockBean private BusinessService businessService;

  @BeforeEach
  public void setup() {
    Product product = new Product();
    product.setProductId("WATT-420-BEANS");
    product.setName("Watties Baked Beans - 420g can");
    product.setDescription("Baked beans as they should be");
    product.setManufacturer("Heinz Wattie's Limited");
    product.setRecommendedRetailPrice(2.2);
    product.setCreated(LocalDate.now());

    item = new InventoryItem();
    item.setInventoryItemId(1L);
    item.setProduct(product);
    item.setQuantity(1);
    item.setPricePerItem(6.5);
    item.setTotalPrice(21.99);
    item.setManufactured(LocalDate.parse("2021-05-14"));
    item.setSellBy(LocalDate.parse("2021-05-14"));
    item.setBestBefore(LocalDate.parse("2021-05-14"));
    item.setExpires(LocalDate.parse("2021-05-14"));
  }

  @Test
  void get_business_1_inventory_path_exists() throws Exception {
    mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/businesses/1/inventory/")).andReturn();
    Assertions.assertNotEquals(401, mvcResult.getResponse().getStatus());
  }

  @Test
  void get_business_1_unauthorized_exception_returns_401() throws Exception {
    Mockito.when(businessService.retrieveInventory(Mockito.anyLong()))
        .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  void get_business_1_forbidden_exception_returns_403() throws Exception {
    Mockito.when(businessService.retrieveInventory(Mockito.anyLong()))
        .thenThrow(new ForbiddenException(""));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  void get_business_1_not_acceptable_exception_returns_406() throws Exception {
    Mockito.when(businessService.retrieveInventory(Mockito.anyLong()))
        .thenThrow(new NoSuchElementException(""));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @Test
  void get_business_1_returns_200() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void get_business_1_returns_correct_id() throws Exception {

    List<InventoryItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveInventory(Mockito.anyLong())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].inventoryItemId").value(item.getInventoryItemId()));
  }

  @Test
  void get_business_1_inventory_returns_correct_product_id() throws Exception {
    List<InventoryItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveInventory(Mockito.anyLong())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].product.id")
                .value(item.getProduct().getProductId()));
  }

  @Test
  void get_business_1_inventory_returns_correct_quantity() throws Exception {
    List<InventoryItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveInventory(Mockito.anyLong())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].quantity").value(item.getQuantity()));
  }

  @Test
  void get_business_1_inventory_returns_correct_pricePerItem() throws Exception {
    List<InventoryItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveInventory(Mockito.anyLong())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].pricePerItem").value(item.getPricePerItem()));
  }

  @Test
  void get_business_1_inventory_returns_correct_total_price() throws Exception {
    List<InventoryItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveInventory(Mockito.anyLong())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].totalPrice").value(item.getTotalPrice()));
  }

  @Test
  void get_business_1_inventory_returns_correct_manufactured() throws Exception {
    List<InventoryItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveInventory(Mockito.anyLong())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].manufactured")
                .value(item.getManufactured().toString()));
  }

  @Test
  void get_business_1_inventory_returns_correct_sell_by() throws Exception {
    List<InventoryItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveInventory(Mockito.anyLong())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].sellBy").value(item.getSellBy().toString()));
  }

  @Test
  void get_business_1_inventory_returns_correct_best_before() throws Exception {
    List<InventoryItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveInventory(Mockito.anyLong())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].bestBefore")
                .value(item.getBestBefore().toString()));
  }

  @Test
  void get_business_1_inventory_returns_correct_expiry() throws Exception {
    List<InventoryItem> items = new ArrayList<>();
    items.add(item);

    Mockito.when(businessService.retrieveInventory(Mockito.anyLong())).thenReturn(items);
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].expires").value(item.getExpires().toString()));
  }
}
