package wasteless.controller.userController.getUser;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;
import wasteless.controller.userController.UserControllerMethodTest;
import wasteless.model.User;
import wasteless.security.AuthUtil;
import wasteless.service.UserService;
import wasteless.service.searching_service.SearchingService;

import java.util.ArrayList;
import java.util.NoSuchElementException;

class GetUserTest extends UserControllerMethodTest {
  @MockBean private UserService userService;
  @MockBean private AuthUtil authUtil;

  @BeforeEach
  void setup() {
    User mockUser = new User();
    mockUser.setUserId(1L);
    Mockito.when(userService.findUsers(Mockito.anyString(), Mockito.anyLong(), Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())).thenReturn(new SearchingService.SearchResult(new ArrayList<>(), 0));
    Mockito.when(authUtil.getCurrentUser()).thenReturn(mockUser);
    Mockito.when(userService.getUserById(Mockito.anyLong())).thenReturn(new User());
  }

  @Test
  void searchForUserNotLoggedInReturns401() throws Exception {
    Mockito.when(authUtil.getCurrentUser())
        .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/search?searchQuery=a&pageNum=1&perPage=10&sortBy=firstName&orderBy=desc"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  void searchForUsersLoggedInReturns200() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/search")
                .param("searchQuery", "a")
                .param("pageNum", String.valueOf(1L))
                .param("perPage", String.valueOf(10L))
                .param("sortBy", "firstName")
                .param("orderBy", "desc"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getUserReturns200() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/1"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  void getNonExistentUserReturns406() throws Exception {
    Mockito.when(userService.getUserById(1L)).thenThrow(new NoSuchElementException(""));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/1"))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @Test
  void getUserNotLoggedInReturns401() throws Exception {
    Mockito.when(authUtil.getCurrentUser())
        .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED, ""));
    mockMvc
        .perform(MockMvcRequestBuilders.get("/users/1"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  void retrieveUserCardsNotLoggedInReturns401() throws Exception {
    Mockito.when(authUtil.getCurrentUser())
            .thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    mockMvc
            .perform(MockMvcRequestBuilders.get("/users/1/cards"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @Test
  void retrieveUserCardsUserReturns200() throws Exception {
    mockMvc
            .perform(MockMvcRequestBuilders.get("/users/1"))
            .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
