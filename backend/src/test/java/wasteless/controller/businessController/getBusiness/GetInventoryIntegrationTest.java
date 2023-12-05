package wasteless.controller.businessController.getBusiness;

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

import java.time.LocalDate;

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@SpringBootTest
@AutoConfigureMockMvc
class GetInventoryIntegrationTest{

  @Autowired
  private MockMvc mockMvc;

  /*
  Authentication tests
   */
  @Test
  void get_inventory_not_logged_in_returns_unauthorized() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  /*
  Authorization tests
   */
  @WithMockUser("MichelleGJarvis@dayrep.com")
  @Test
  void get_inventory_as_user_not_admin_of_business_1_returns_forbidden() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_ok() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser("admin@defaultglobal")
  @Test
  void get_inventory_as_dgaa_returns_ok() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser("test1@test.com")
  @Test
  void get_inventory_as_gaa_returns_ok() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_route_does_not_exist_returns_not_acceptable() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/-1/inventory/"))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  /*
  Assert all returned fields as expected
   */
  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_correct_item_id() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].inventoryItemId").value(1));
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_correct_best_before() throws Exception {
    MvcResult result =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.[0].bestBefore")
                    .value(LocalDate.now().plusYears(1).toString()))
            .andReturn();
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_correct_sell_by() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].sellBy")
                .value(LocalDate.now().plusYears(2).toString()));
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_correct_manufactured() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].manufactured")
                .value(LocalDate.now().minusYears(1).toString()));
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_correct_price_per_item() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].pricePerItem").value(12.00));
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_correct_total_price() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].totalPrice").value(120.00));
  }

  /*
  Tests relating to associated product
   */
  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_does_not_return_product_code() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].productCode").doesNotExist());
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_correct_product_id() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].product.id").value("watt-222-222"));
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_correct_product_name() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].product.name")
                .value("Watties Baked Beans - 420g can"));
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_correct_product_description() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].product.description")
                .value("Baked Beans as they should be."));
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_correct_manufacturer() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].product.manufacturer")
                .value("this is MANUFACTURER"));
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_correct_recommended_retail_price() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].product.recommendedRetailPrice").value(2.2));
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_correct_created_date() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(
            MockMvcResultMatchers.jsonPath("$.[0].product.created")
                .value("2020-01-01"));
  }

  @WithMockUser("test@test.com")
  @Test
  void get_inventory_as_admin_returns_product_image() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/businesses/1/inventory/"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.[0].product.images").exists());
  }
}
