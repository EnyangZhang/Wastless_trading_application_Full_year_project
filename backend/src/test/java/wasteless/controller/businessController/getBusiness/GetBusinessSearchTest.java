package wasteless.controller.businessController.getBusiness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.model.Business;
import wasteless.model.Searchable;
import wasteless.service.BusinessService;
import wasteless.service.searching_service.SearchingService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class GetBusinessSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private BusinessService businessService;

    @BeforeEach
    void setup(){
        Business business = new Business();
        List<Searchable> listBusinesses = new ArrayList<Searchable>();
        listBusinesses.add(business);
        SearchingService.SearchResult businessList = new SearchingService.SearchResult(listBusinesses, 10);
        Mockito.when(businessService.searchBusinesses(Mockito.anyString())).thenReturn(businessList);
    }

    @Test
    void searchBusinesses_notLoggedIn_401() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get("/businesses/search"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @WithMockUser("test@test.com")
    @Test
    void searchBusinesses_loggedInWithoutQuery_400() throws Exception{
        mockMvc
                .perform(MockMvcRequestBuilders.get("/businesses/search"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @WithMockUser("test@test.com")
    @Test
    void searchBusinesses_loggedInWithQuery_200() throws Exception{
        mockMvc
                .perform(MockMvcRequestBuilders.get("/businesses/search").param("searchQuery", "test"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @WithMockUser("test@test.com")
    @Test
    void searchBusinesses_loggedInWithQuery_expectedListInResponse() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.get("/businesses/search").param("searchQuery", "test"))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
    }
}
