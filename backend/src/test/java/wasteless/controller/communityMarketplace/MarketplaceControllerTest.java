package wasteless.controller.communityMarketplace;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.service.MarketplaceService;

@SpringBootTest
@AutoConfigureMockMvc
class MarketplaceControllerTest {

  private final String section = "section";
  private final String page = "page";
  private final String size = "size";
  private final String sorting = "sort";
  private final String order = "order";

  private final String forSale = "ForSale";

  @Autowired private MockMvc mockMvc;
  private MvcResult mvcResult;
  @MockBean private MarketplaceService marketplaceService;
  private String url;

  @BeforeEach
  void setup() {
    url = "/cards";
    Mockito.when(
            marketplaceService.retrieveCards(
                Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString()))
        .thenReturn(Page.empty());
  }

  @Test
  void getCards_NotLoggedIn_StatusCode401() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders.get(url))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser
  @CsvSource({"ForSale, 200", "Wanted, 200", "Exchange, 200", ", 400"})
  @ParameterizedTest
  void getCards_LoggedInWithSection_correctStatusCode(String section, int status) throws Exception {
    mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(url)
                    .param(this.section, section)
                    .param(this.page, String.valueOf(0))
                    .param(this.size, String.valueOf(1)))
            .andReturn();
    Assertions.assertEquals(status, mvcResult.getResponse().getStatus());
  }

  @WithMockUser
  @CsvSource({
    "0, 1, title, desc, 0, 1, title, desc",
    "1, 1, title, asc, 1, 1, title, asc",
    "0, 2, created, asc, 0, 2, created, asc",
    "100, 250, creator.homeAddress.suburb, asc, 100, 250, creator.homeAddress.suburb, asc",
    ", 1, , asc, 0, 1, created, asc",
    "0, , creator.homeAddress.country, , 0, 10, creator.homeAddress.country, desc",
    ", , , , 0, 10, created, desc"
  })
  @ParameterizedTest
  void getCards_loggedInWithParams_callsServiceWithCorrectParams(String page,
                                                                 String size,
                                                                 String sort,
                                                                 String order,
                                                                 int expectedPage,
                                                                 int expectedSize,
                                                                 String expectedSort,
                                                                 String expectedOrder) throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.get(url)
            .param(this.section, forSale)
            .param(this.page, page)
            .param(this.size, size).param(this.sorting, sort).param(this.order, order));
    Mockito.verify(marketplaceService).retrieveCards(forSale, expectedPage, expectedSize, expectedSort, expectedOrder);
  }

  @WithMockUser
  @CsvSource({
    "0, 1, title, desc",
    "1, 1, title, asc",
    "0, 2, created, asc",
    "100, 250, creator.homeAddress.suburb, asc",
    ", , , ,",
    ", , 1, 2"
  })
  @ParameterizedTest
  void getCards_loggedInWithParams_returns200Status(String page, String size, String sorting, String order)
      throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(url)
                .param(this.section, forSale)
                .param(this.page, page)
                .param(this.size, size).param(this.sorting, sorting).param(this.order, order))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @WithMockUser
  @CsvSource({
          "a, a",
          "0, a",
          "a, 1",
          "@, 1",
          "@, @",
          "0, @"
  })
  @ParameterizedTest
  void getCards_loggedInWithInvalidParams_returns400Status(String page, String size) throws Exception {
      mockMvc
            .perform(
                    MockMvcRequestBuilders.get(url)
                            .param(this.section, forSale)
                            .param(this.page, page)
                            .param(this.size, size))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @WithMockUser
  @CsvSource({
          "a, a",
          "0, a",
          "a, 1",
          "@, 1",
          "@, @",
          "0, @"
  })
  @ParameterizedTest
  void getCards_loggedInWithInvalidParams_noServiceCall(String page, String size) throws Exception {
    mockMvc
            .perform(
                    MockMvcRequestBuilders.get(url)
                            .param(this.section, forSale)
                            .param(this.page, page)
                            .param(this.size, size));
    Mockito.verify(marketplaceService, Mockito.never()).retrieveCards(
            Mockito.eq(forSale),
            Mockito.anyInt(),
            Mockito.anyInt(),
            Mockito.anyString(),
            Mockito.anyString());
  }
}
