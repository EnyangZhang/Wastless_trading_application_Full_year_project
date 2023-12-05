package wasteless.controller.marketplaceControllerTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.NotAcceptableStatusException;
import wasteless.controller.jsonobjects.MarketplaceCardJson;
import wasteless.exception.UnauthorizedException;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.security.AuthUtil;
import wasteless.service.MarketplaceService;
import wasteless.test_helpers.MarketplaceDataCreator;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


@SpringBootTest
@AutoConfigureMockMvc
class DeleteCardTest {

    @MockBean
    MarketplaceService marketplaceService;

    @MockBean
    AuthUtil authUtil;

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
    @WithMockUser(username = "user2@email.com")
    void deleteCard_creatorLoggedIn_sends200() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(user2);
        when(marketplaceService.retrieveCardById(Mockito.anyInt())).thenReturn(marketplaceCard);
        doNothing().when(marketplaceService).deleteCard(Mockito.any(MarketplaceCard.class));
        mockMvc.perform(MockMvcRequestBuilders.delete("/cards/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@defaultglobal")
    void deleteCard_dgaaLoggedIn_sends200() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(dgaa);
        when(marketplaceService.retrieveCardById(Mockito.anyInt())).thenReturn(marketplaceCard);
        doNothing().when(marketplaceService).deleteCard(Mockito.any(MarketplaceCard.class));
        mockMvc.perform(MockMvcRequestBuilders.delete("/cards/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    void deleteCard_otherUserLoggedIn_sends403() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(user1);
        when(marketplaceService.retrieveCardById(Mockito.anyInt())).thenReturn(marketplaceCard);
        doNothing().when(marketplaceService).deleteCard(Mockito.any(MarketplaceCard.class));
        mockMvc.perform(MockMvcRequestBuilders.delete("/cards/1"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user1@email.com")
    void deleteCard_LoggedInCardNotExist_sends406() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(user1);
        when(marketplaceService.retrieveCardById(Mockito.anyInt())).thenThrow(NotAcceptableStatusException.class);
        doNothing().when(marketplaceService).deleteCard(Mockito.any(MarketplaceCard.class));
        mockMvc.perform(MockMvcRequestBuilders.delete("/cards/1"))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }


    @Test
    void deleteCard_NotLoggedIn_sends401() throws Exception {
        when(authUtil.getCurrentUser()).thenThrow(UnauthorizedException.class);
        when(marketplaceService.retrieveCardById(Mockito.anyInt())).thenReturn(marketplaceCard);
        doNothing().when(marketplaceService).deleteCard(Mockito.any(MarketplaceCard.class));
        mockMvc.perform(MockMvcRequestBuilders.delete("/cards/1"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
