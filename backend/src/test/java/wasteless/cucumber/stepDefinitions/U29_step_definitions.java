package wasteless.cucumber.stepDefinitions;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.ObjectJSONMapper;
import wasteless.cucumber.CucumberSpringConfiguration;
import wasteless.model.SaleItem;
import wasteless.repository.SaleItemRepository;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

public class U29_step_definitions extends CucumberSpringConfiguration {

    @Autowired
    private SaleItemRepository saleItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private final ObjectJSONMapper objectJSONMapper = new ObjectJSONMapper();

    @Autowired
    private DataSource ds;
    private String url;
    private final String searchQuery = "";
    private final Integer pageNum = 1;
    private Integer pageSize = 10;

    private final String searchQueryString = "searchQuery";
    private final String pageParam = "pageNum";
    private final String sizeParam = "perPage";
    private final String sortParam = "sortBy";
    private final String orderParam = "orderBy";

    private MvcResult result;
    private String email;

    private SaleItem[] retrievedSaleListings;


    @Before
    public void setup() throws SQLException {
        url = "/listings/search";
    }

    @Given("There are {int} sale items listed for sale by businesses")
    public void given_numberOfSaleItemsListedForSaleByBusinesses(Integer number) throws SQLException {
        ScriptUtils.executeSqlScript(ds.getConnection(), new ClassPathResource("testData/CreateDBTables.sql"));
        ScriptUtils.executeSqlScript(ds.getConnection(), new ClassPathResource("testData/U29Data.sql"));

        List<SaleItem> saleItems = saleItemRepository.findAll();
        Assertions.assertEquals(number, saleItems.size());
    }



    @Given("There is no user logged in currently")
    public void userNotLoggedIn() {
        Assertions.assertNull(email);
    }

    @When("There is an unauthorized access to the browse sale listings page")
    public void browseSaleListings_notLoggedIn_unauthorizedStatus() throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get(url))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized()).andReturn();
        Assertions.assertNotNull(result);
    }

    @Then("An error prevents any data from being returned to the browse sale listing page")
    public void browseSaleListings_notLoggedIn_noDataReturned() throws UnsupportedEncodingException {
        Assertions.assertEquals("", result.getResponse().getContentAsString());
    }

    @Given("The user is logged in with email {string} and password {string}")
    public void userIsLoggedInWithEmailAndPassword(String email, String password) throws Exception {
        String credentials = objectJSONMapper.mapCredentials(email, password);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/login")
                                .content(credentials)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @When("The user {string} navigates to page {int} of the browse sale listings page")
    public void browseSaleListings_userNavigatesToPage_withPageSize(String email, Integer pageNum) throws Exception {
        this.email = email;
        result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .with(user(email))
                .param(searchQueryString, this.searchQuery)
                .param(pageParam, String.valueOf(pageNum))
                .param(sizeParam, String.valueOf(this.pageSize))
                .param(sortParam, "name")
                .param(orderParam, "asc")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn();
        retrievedSaleListings = objectMapper.readValue(result.getResponse().getContentAsString(), SaleItem[].class);
        Assertions.assertNotNull(retrievedSaleListings);
    }

    @Then("The user sees the currently unsold sale listings")
    public void userSeesUnsoldSaleListings() {
        for (SaleItem listing : retrievedSaleListings) {
            Assertions.assertNotNull(listing);
            Assertions.assertFalse(listing.isSold());
        }
    }

    @Then("The ordering of the cards is:")
    public void browseSaleListings_ordering_isExpected(@Transpose List<Long> list) {
        List<Long> saleListingIds = new ArrayList<>();
        Assertions.assertNotNull(retrievedSaleListings);
        for (SaleItem listing : retrievedSaleListings) {
            saleListingIds.add(listing.getSaleItemId());
        }
        Assertions.assertEquals(list, saleListingIds);
    }

    @Given("The number of listings per page is set to {int}")
    public void browseSaleListings_setPagination_toGiven(int number) {
            this.pageSize = number;
            Assertions.assertEquals(number, this.pageSize);
    }



    @Then("The user only sees {int} sale listings on the page")
    public void browseSaleListings_getListingsLoggedIn_viewGivenListingsPage1(int number) {
        Assertions.assertEquals(number, retrievedSaleListings.length);
    }

    @Then("The user only sees the {int} listings on the page")
    public void browseSaleListings_getListingsLoggedIn_viewGivenListingsPage2(int number) {
        Assertions.assertEquals(number, retrievedSaleListings.length);
    }

    @Then("The product name is available on the displayed listings")
    public void browseSaleListings_getListingsLoggedIn_productNameAvailable() {
        for (SaleItem listing : retrievedSaleListings) {
            Assertions.assertNotNull(listing);
            Assertions.assertNotNull(listing.getInventoryItem().getProduct().getName());
            Assertions.assertNotEquals("", listing.getInventoryItem().getProduct().getName());
        }
    }

    @Then("The total price is available on the displayed listings")
    public void browseSaleListings_getListingsLoggedIn_totalPriceAvailable() {
        for (SaleItem listing : retrievedSaleListings) {
            Assertions.assertNotNull(listing);
            Assertions.assertNotNull(listing.getPrice());
        }
    }

    @Then("The quantity is available on the displayed listings")
    public void browseSaleListings_getListingsLoggedIn_quantityAvailable() {
        for (SaleItem listing : retrievedSaleListings) {
            Assertions.assertNotNull(listing);
            Assertions.assertNotNull(listing.getQuantity());
            Assertions.assertNotEquals(0, listing.getQuantity());
        }
    }

    @Then("The closing date is available on the displayed listings")
    public void browseSaleListings_getListingsLoggedIn_closingDateAvailable() {
        for (SaleItem listing : retrievedSaleListings) {
            Assertions.assertNotNull(listing);
            Assertions.assertNotNull(listing.getCloses());
        }
    }

    @Then("The more info is available on the displayed listings")
    public void browseSaleListings_getListingsLoggedIn_moreInfoAvailable() {
        for (SaleItem listing : retrievedSaleListings) {
            Assertions.assertNotNull(listing);
            Assertions.assertNotNull(listing.getMoreInfo());
        }
    }

    @When("The user {string} changes the ordering to {string}, {string}")
    public void browseSaleListings_changeOrder(String email, String sort, String order) throws Exception{
        result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .with(user(email))
                .param(searchQueryString, this.searchQuery)
                .param(pageParam, String.valueOf(this.pageNum))
                .param(sizeParam, String.valueOf(this.pageSize))
                .param(sortParam, sort)
                .param(orderParam, order)
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn();
        retrievedSaleListings = objectMapper.readValue(result.getResponse().getContentAsString(), SaleItem[].class);
        Assertions.assertNotNull(retrievedSaleListings);
    }

    @Then("The ordering of the listings is:")
    public void browseSaleListings_getListings_orderIsCorrect(@Transpose List<Long> list) {
        List<Long> saleListingIds = new ArrayList<>();
        Assertions.assertNotNull(retrievedSaleListings);
        for (SaleItem listing : retrievedSaleListings) {
            saleListingIds.add(listing.getSaleItemId());
        }
        Assertions.assertEquals(list, saleListingIds);
    }

    @When("The user {string} filters the search results by {string}")
    public void browseSaleListings_filterByBusinessType(String email, String businessType) throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .with(user(email))
                .param(searchQueryString, this.searchQuery)
                .param("businessType", businessType)
                .param(pageParam, String.valueOf(this.pageNum))
                .param(sizeParam, String.valueOf(this.pageSize))
                .param(sortParam, "created")
                .param(orderParam, "asc")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn();
        retrievedSaleListings = objectMapper.readValue(result.getResponse().getContentAsString(), SaleItem[].class);
        Assertions.assertNotNull(retrievedSaleListings);
    }

    @When("The user {string} searches for a listing with {string}")
    public void browseSaleListings_searchForListings(String email, String searchQuery) throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .with(user(email))
                .param(searchQueryString, searchQuery)
                .param(pageParam, String.valueOf(this.pageNum))
                .param(sizeParam, String.valueOf(this.pageSize))
                .param(sortParam, "created")
                .param(orderParam, "asc")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn();
        retrievedSaleListings = objectMapper.readValue(result.getResponse().getContentAsString(), SaleItem[].class);
        Assertions.assertNotNull(retrievedSaleListings);
    }

    @When("The user {string} filters the search results with min price {double} and max price {double}")
    public void browseSaleListings_filterByPrice(String email, Double minPrice, Double maxPrice) throws Exception {
        result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .with(user(email))
                .param(searchQueryString, this.searchQuery)
                .param("maxPrice", String.valueOf(maxPrice))
                .param("minPrice", String.valueOf(minPrice))
                .param(pageParam, String.valueOf(this.pageNum))
                .param(sizeParam, String.valueOf(this.pageSize))
                .param(sortParam, "created")
                .param(orderParam, "asc")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn();
        retrievedSaleListings = objectMapper.readValue(result.getResponse().getContentAsString(), SaleItem[].class);
        Assertions.assertNotNull(retrievedSaleListings);
    }

    @When("The user {string} filters the search results with earliest closing date tomorrow")
    public void browseSaleListings_filterByEarliestClosingDate(String email) throws Exception {
        LocalDate earliestDate = LocalDate.now().plusDays(1);
        result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .with(user(email))
                .param(searchQueryString, this.searchQuery)
                .param("earliestClosingDate", String.valueOf(earliestDate))
                .param(pageParam, String.valueOf(this.pageNum))
                .param(sizeParam, String.valueOf(this.pageSize))
                .param(sortParam, "created")
                .param(orderParam, "asc")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn();
        retrievedSaleListings = objectMapper.readValue(result.getResponse().getContentAsString(), SaleItem[].class);
        Assertions.assertNotNull(retrievedSaleListings);
    }

    @When("The user {string} filters the search results with latest closing date tomorrow")
    public void browseSaleListings_filterByLatestClosingDate(String email) throws Exception {
        LocalDate latestDate = LocalDate.now().plusDays(1);
        result = mockMvc.perform(MockMvcRequestBuilders.get(url)
                .with(user(email))
                .param(searchQueryString, this.searchQuery)
                .param("latestClosingDate", String.valueOf(latestDate))
                .param(pageParam, String.valueOf(this.pageNum))
                .param(sizeParam, String.valueOf(this.pageSize))
                .param(sortParam, "created")
                .param(orderParam, "asc")
        ).andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON)
                ).andReturn();
        retrievedSaleListings = objectMapper.readValue(result.getResponse().getContentAsString(), SaleItem[].class);
        Assertions.assertNotNull(retrievedSaleListings);
    }


    @Then("The listings returned are:")
    public void browseSaleListings_getListings_correctListingsReturned(@Transpose List<Long> list) {
        List<Long> saleListingIds = new ArrayList<>();
        Assertions.assertNotNull(retrievedSaleListings);
        for (SaleItem listing : retrievedSaleListings) {
            saleListingIds.add(listing.getSaleItemId());
        }
        Assertions.assertEquals(list, saleListingIds);
    }
}
