package wasteless.controller.marketplaceControllerTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.controller.jsonobjects.MarketplaceCardJson;
import wasteless.exception.BadRequestException;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.repository.UserRepository;
import wasteless.security.AuthUtil;
import wasteless.service.MarketplaceService;
import wasteless.test_helpers.MarketplaceDataCreator;

import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
class PostCardTest {

    @MockBean
    MarketplaceService marketplaceService;

    @MockBean
    AuthUtil authUtil;

    @MockBean
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    ObjectMapper mapper;

    MarketplaceCardJson marketplaceCardJson;
    MarketplaceCard marketplaceCard;

    User user1;
    User user2;
    User dgaa;

    @BeforeEach
    void setup() {
        mapper = new ObjectMapper();

        user1 = MarketplaceDataCreator.createUser(2L, "user1@email.com", "user");
        user2 = MarketplaceDataCreator.createUser(9L, "user2@email.com", "user");
        dgaa = MarketplaceDataCreator.createUser(1L, "admin@defaultglobal", "default_global_admin");

        marketplaceCardJson = MarketplaceDataCreator.createCardJson(9L);
        marketplaceCard = MarketplaceDataCreator.createCard(4, user2);
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    void createCard_creatorNotCurrentUser_sends403() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(user1);
        when(marketplaceService.createMarketplaceCard(Mockito.any(MarketplaceCardJson.class))).thenReturn(marketplaceCard);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/cards")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(marketplaceCardJson)))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user2@email.com")
    void createCard_creatorLoggedIn_sends201() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(user2);
        when(marketplaceService.createMarketplaceCard(Mockito.any(MarketplaceCardJson.class))).thenReturn(marketplaceCard);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/cards")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(marketplaceCardJson)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin@defaultglobal")
    void createCard_adminLoggedIn_sends201() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(dgaa);
        when(marketplaceService.createMarketplaceCard(Mockito.any(MarketplaceCardJson.class))).thenReturn(marketplaceCard);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/cards")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(marketplaceCardJson)))
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    @WithMockUser(username = "user2@email.com")
    void createCard_invalidData_sends400() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(user2);
        when(marketplaceService.createMarketplaceCard(Mockito.any(MarketplaceCardJson.class)))
                .thenThrow(new BadRequestException(""));
        marketplaceCardJson.setSection("invalid");
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/cards")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(mapper.writeValueAsString(marketplaceCardJson)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
