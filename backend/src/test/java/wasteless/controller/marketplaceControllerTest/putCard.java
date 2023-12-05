package wasteless.controller.marketplaceControllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.controller.jsonobjects.MarketplaceCardJson;
import wasteless.exception.ForbiddenException;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.repository.UserRepository;
import wasteless.security.AuthUtil;
import wasteless.service.MarketplaceService;
import wasteless.test_helpers.MarketplaceDataCreator;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
class putCard {

    @SpyBean
    MarketplaceService marketplaceService;

    @MockBean
    AuthUtil authUtil;

    @MockBean
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    MarketplaceCardJson marketplaceCardJson;
    MarketplaceCard marketplaceCard;

    User user1;
    User user2;
    User dgaa;

    @BeforeEach
    void setup() {


        user1 = MarketplaceDataCreator.createUser(2L, "user1@email.com", "user");
        user2 = MarketplaceDataCreator.createUser(9L, "user2@email.com", "user");
        dgaa = MarketplaceDataCreator.createUser(1L, "admin@defaultglobal", "default_global_admin");

        marketplaceCardJson = MarketplaceDataCreator.createCardJson(9L);
        marketplaceCard = MarketplaceDataCreator.createCard(4, user2);
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    void extendDisplayPeriod_creatorNotCurrentUser_responds403() throws Exception {
        Mockito.doThrow(ForbiddenException.class).when(marketplaceService).extendDisplayPeriodEnd(Mockito.anyInt());
        when(authUtil.getCurrentUser()).thenReturn(user1);
        mockMvc
                .perform(
                MockMvcRequestBuilders.put("/cards/" + marketplaceCard.getMarketplaceCardId()
                        + "/extenddisplayperiod"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user2@email.com")
    void extendDisplayPeriod_creatorCurrentUser_responds200() throws Exception {
        Mockito.doNothing().when(marketplaceService).extendDisplayPeriodEnd(Mockito.anyInt());
        when(authUtil.getCurrentUser()).thenReturn(user2);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/cards/" + marketplaceCard.getMarketplaceCardId()
                                + "/extenddisplayperiod"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@defaultglobal")
    void extendDisplayPeriod_DGAACurrentUser_responds200() throws Exception {
        Mockito.doNothing().when(marketplaceService).extendDisplayPeriodEnd(Mockito.anyInt());
        when(authUtil.getCurrentUser()).thenReturn(dgaa);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/cards/" + 4L
                                + "/extenddisplayperiod"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    
    @Test
    @WithMockUser(username = "user2@email.com")
    void extendDisplayPeriod_invalidCardId_responds406() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(user2);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/cards/" + 17
                                + "/extenddisplayperiod"))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }


}
