package wasteless.controller.businessController.patchBusiness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;
import wasteless.model.InventoryItem;
import wasteless.model.Product;
import wasteless.model.SaleItem;
import wasteless.repository.SaleItemRepository;
import wasteless.service.BusinessService;
import wasteless.test_helpers.BusinessDataCreator;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@SpringBootTest
@AutoConfigureMockMvc
class PATCHBusinessSaleItemControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SaleItemRepository saleItemRepository;

    @MockBean
    private BusinessService businessService;

    private SaleItem item;

    @BeforeEach
    void setup() {
        Product product = BusinessDataCreator.createProduct("WATT-420-BEANS");

        InventoryItem inventoryItem = BusinessDataCreator.createInventoryItem(1L, product);

        item = new SaleItem();
        item.setSaleItemId(1L);
        item.setInventoryItem(inventoryItem);
        item.setQuantity(1);
        item.setPrice(10.00);
        item.setCreated(LocalDateTime.now());
    }

    @Test
    void patchSaleListingPurchase_unauthorizedException_returns401() throws Exception {
        when(businessService.purchaseSaleItem(anyLong(), anyLong()))
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        mockMvc
                .perform(MockMvcRequestBuilders.patch("/businesses/1/listings/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @WithMockUser("test@test.com")
    @Test
    void patchSaleListingPurchase_badRequestException_returns400() throws Exception {
        when(businessService.purchaseSaleItem(anyLong(), anyLong()))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST));
        mockMvc
                .perform(MockMvcRequestBuilders.patch("/businesses/1/listings/1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @WithMockUser("test@test.com")
    @Test
    void patchSaleListingPurchase_loggedIn_returns200() throws Exception {
        when(businessService.purchaseSaleItem(anyLong(), anyLong()))
                .thenReturn(item);
        mockMvc
                .perform(MockMvcRequestBuilders.patch("/businesses/1/listings/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser("test@test.com")
    @Test
    void patchSaleListingPurchase_loggedIn_returnsCorrectId() throws Exception {
        when(businessService.purchaseSaleItem(anyLong(), anyLong()))
                .thenReturn(item);
        mockMvc
                .perform(MockMvcRequestBuilders.patch("/businesses/1/listings/1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.saleItemId").value(item.getSaleItemId()));
    }

    @Test
    void patchSaleListingLike_unauthorizedException_returns401() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.patch("/businesses/1/listings/1/like"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @WithMockUser("test@test.com")
    @Test
    void patchSaleListingLike_badRequestException_returns400() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(businessService).likeSaleItem(anyLong(), anyLong());

        mockMvc
                .perform(MockMvcRequestBuilders.patch("/businesses/1/listings/1/like"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @WithMockUser("test@test.com")
    @Test
    void patchSaleListingLike_loggedIn_returns200() throws Exception {
        doNothing().when(businessService).likeSaleItem(anyLong(), anyLong());
        mockMvc
                .perform(MockMvcRequestBuilders.patch("/businesses/1/listings/1/like"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void patchSaleListingUnlike_unauthorizedException_returns401() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.patch("/businesses/1/listings/1/unlike"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @WithMockUser("test@test.com")
    @Test
    void patchSaleListingUnlike_badRequestException_returns400() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST))
                .when(businessService).unlikeSaleItem(anyLong(), anyLong());
        mockMvc
                .perform(MockMvcRequestBuilders.patch("/businesses/1/listings/1/unlike"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @WithMockUser("test@test.com")
    @Test
    void patchSaleListingUnlike_loggedIn_returns200() throws Exception {
        doNothing().when(businessService).unlikeSaleItem(anyLong(), anyLong());
        mockMvc
                .perform(MockMvcRequestBuilders.patch("/businesses/1/listings/1/unlike"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
