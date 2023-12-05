package wasteless.controller.saleItemController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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
import wasteless.controller.SaleItemController.FilterQuery;
import wasteless.model.SaleItem;
import wasteless.model.User;
import wasteless.security.AuthUtil;
import wasteless.service.BusinessService;
import wasteless.service.SaleItemService;
import wasteless.service.searching_service.SearchingService;
import wasteless.test_helpers.BusinessDataCreator;

import java.util.ArrayList;


@SpringBootTest
@AutoConfigureMockMvc
class SaleItemControllerTest {

    private final String page = "page";
    private final String size = "size";
    private final String sorting = "sort";
    private final String order = "order";


    @Autowired private MockMvc mockMvc;

    @MockBean private SaleItemService saleItemService;

    @MockBean private BusinessService businessService;

    @MockBean private AuthUtil authUtil;

    private String url;

    private User mockUser = new User();

    private SaleItem saleItem;

    public SaleItemControllerTest() {
    }

    @BeforeEach
    public void setup() {

        url = "/listings/search";
        Mockito.when(
                saleItemService.searchSaleListings(Mockito.anyList(), Mockito.any(), Mockito.anyInt(),
                        Mockito.anyInt(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(new SearchingService.SearchResult(new ArrayList<>(), 0));
        saleItem = BusinessDataCreator.createSaleItem(7L);
    }

    @Test
    void getSaleItems_NotLoggedIn_StatusCode401() throws Exception {
        Mockito.when(authUtil.getCurrentUser())
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        mockMvc
                .perform(MockMvcRequestBuilders.get(url)
                        .param("searchQuery", "a")
                        .param("filterQuery", String.valueOf(new FilterQuery(null, null,
                                null,null, null)))
                        .param("pageNum", String.valueOf(1L))
                        .param("perPage", String.valueOf(10L))
                        .param("sortBy", "firstName")
                        .param("orderBy", "desc"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void getSaleItems_LoggedIn_StatusCode200() throws Exception {
        Mockito.when(authUtil.getCurrentUser()).thenReturn(mockUser);
        mockMvc
                .perform(MockMvcRequestBuilders.get(url)
                        .param("searchQuery", "a")
                        .param("filterQuery", String.valueOf(new FilterQuery(null, null,
                                null,null, null)))
                        .param("pageNum", String.valueOf(1L))
                        .param("perPage", String.valueOf(10L))
                        .param("sortBy", "firstName")
                        .param("orderBy", "desc"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser
    @Test
    void getListingById_LoggedIn_statusCode200() throws Exception {
        Mockito.when(authUtil.getCurrentUser()).thenReturn(mockUser);
        Mockito.when(businessService.getSaleItemById(Mockito.anyInt())).thenReturn(saleItem);
        mockMvc
                .perform(MockMvcRequestBuilders.get("/listings/7"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getListingById_NotLoggedIn_StatusCode401() throws Exception {
        Mockito.when(authUtil.getCurrentUser())
                .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        mockMvc
                .perform(MockMvcRequestBuilders.get("/listings/7"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

}
