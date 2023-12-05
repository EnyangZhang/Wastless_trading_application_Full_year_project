package wasteless.controller.businessController.postBusiness;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.ObjectJSONMapper;
import wasteless.exception.BadRequestException;
import wasteless.exception.ForbiddenException;
import wasteless.exception.UnauthorizedException;
import wasteless.model.*;
import wasteless.repository.BusinessRepository;
import wasteless.repository.ProductRepository;
import wasteless.repository.UserRepository;
import wasteless.service.BusinessService;

import java.time.LocalDate;
import java.util.*;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostInventoryItemTest {

  @Autowired
  private MockMvc mockMvc;

  private ObjectMapper objectMapper;
  private ObjectJSONMapper objectJSONMapper;
  @MockBean private UserRepository userRepository;
  @MockBean private BusinessRepository businessRepository;
  @MockBean private ProductRepository productRepository;
  @MockBean private BusinessService businessService;

  private HashMap<Long, Business> businesses;
  private User dgaa;
  private User businessAdmin;
  private User notAdmin;
  private Product product;
  private InventoryItem inventoryItem;

  @BeforeEach
  void setup() {
    businesses = new HashMap<>();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectJSONMapper = new ObjectJSONMapper();

    // Create a regular user
    notAdmin =
        new User(
            1L,
            "John",
            "Doe",
            "Hector",
            "Hector",
            "Johnny",
            "notadmin@testing.com",
            LocalDate.parse("1997-02-01"),
            "",
            new Address("8", "Madeup Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
            "password123",
            LocalDate.now(),
            "user",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>());

    // Create a twin user who is an admin of a business
    businessAdmin =
        new User(
            2L,
            "John",
            "Doe",
            "Hector",
            "Hector",
            "Johnny",
            "businessadmin@testing.com",
            LocalDate.parse("1997-02-01"),
            "",
            new Address("8", "Madeup Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
            "password123",
            LocalDate.now(),
            "user",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>());

    // Create a user who is a dgaa
    dgaa =
        new User(
            4L,
            "admin@defaultglobal",
            "Default Global",
            "",
            "DGAA",
            "",
            "admin@defaultglobal",
            LocalDate.parse("1997-02-01"),
            "",
            new Address("8", "Madeup Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
            "password123",
            LocalDate.now(),
            "default_global_admin",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>());

    // Add a new business to hash map
    Business business =
        new Business(
            1L,
            "McRonalds",
            "Fast Food",
            new Address("12", "Burger Lane", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
            "Accommodation and Food Services",
            businessAdmin.getUserId(),
            Collections.singletonList(businessAdmin),
            new ArrayList<>(),
            new ArrayList<>(),
            LocalDate.now());
    businesses.put(1L, business);

    // Add the business to Admin's businessesAdministered collection
    businessAdmin.addBusinessAdministered(businesses.get(1L));

    // Create product
    product =
        new Product(
            businesses.get(1L),
            "productId",
            "Watties Baked Beans - 420g can",
            "Watties",
            "Baked Beans as they should be",
            2.2,
            LocalDate.now());

    // Create inventoryItem
    inventoryItem =
        new InventoryItem(
            product,
            "inventoryItemId",
            10,
            3.5,
            30D,
            LocalDate.now().minusYears(1L),
            LocalDate.now().plusYears(1L),
            LocalDate.now().plusYears(1L),
            LocalDate.now().plusYears(1L));

    // Configure mocked repos
    // When userRepo is searched for either e-mails, it returns the corresponding user
    // When businessRepo is searched for the one business, it returns it from the hash map
    when(userRepository.findByEmail("notadmin@testing.com"))
        .thenReturn(java.util.Optional.ofNullable(notAdmin));
    when(userRepository.findByEmail("businessadmin@testing.com"))
        .thenReturn(java.util.Optional.ofNullable(businessAdmin));
    when(userRepository.findByEmail("admin@defaultglobal"))
        .thenReturn(java.util.Optional.ofNullable(dgaa));
    when(businessRepository.findByBusinessId(1L))
        .thenReturn(java.util.Optional.ofNullable(businesses.get(1L)));
    when(productRepository.findByBusinessAndProductId(business, inventoryItem.getProductId()))
        .thenReturn(List.of(product));

    when(businessService.addInventoryItem(anyLong(), any(InventoryItem.class)))
        .thenCallRealMethod();
  }

  @WithMockUser(username = "businessadmin@testing.com")
  @Test
  void addInventoryItem_loggedInUserIsAdminOfBusiness_succeedsWith201() throws Exception {
    long bueId = 1L;
    when(businessService.addInventoryItem(anyLong(), any(InventoryItem.class)))
        .thenReturn(inventoryItem);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectJSONMapper.inventoryItemToRequest(inventoryItem)))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @WithMockUser(username = "admin@defaultglobal")
  @Test
  void addInventoryItem_loggedInUserIsDGAA_succeedsWith201() throws Exception {
    long bueId = 1L;
    when(businessService.addInventoryItem(anyLong(), any(InventoryItem.class)))
        .thenReturn(inventoryItem);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectJSONMapper.inventoryItemToRequest(inventoryItem)))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @WithMockUser(username = "notadmin@testing.com")
  @Test
  void addInventoryItem_loggedInUserIsNotAdminOfBusiness_failsWith403() throws Exception {
    long bueId = 1L;
    when(businessService.addInventoryItem(anyLong(), any(InventoryItem.class)))
        .thenThrow(new ForbiddenException(""));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectJSONMapper.inventoryItemToRequest(inventoryItem)))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @Test
  void addInventoryItem_notLoggedInUser_failsWith401() throws Exception {
    long bueId = 1L;
    when(businessService.addInventoryItem(anyLong(), any(InventoryItem.class)))
        .thenThrow(new UnauthorizedException(""));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectJSONMapper.inventoryItemToRequest(inventoryItem)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser(username = "businessadmin@testing.com")
  @Test
  void addInventoryItem_productIdIsNull_failsWith400() throws Exception {
    long bueId = 1L;
    when(businessService.addInventoryItem(anyLong(), any(InventoryItem.class)))
        .thenThrow(new BadRequestException(""));
    inventoryItem.setProductId(null);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectJSONMapper.inventoryItemToRequest(inventoryItem)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @WithMockUser(username = "businessadmin@testing.com")
  @Test
  void addInventoryItem_manufacturedDateInTheFuture_failsWith400() throws Exception {
    long bueId = 1L;
    inventoryItem.setManufactured(LocalDate.now().plusDays(1L));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectJSONMapper.inventoryItemToRequest(inventoryItem)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @WithMockUser(username = "businessadmin@testing.com")
  @Test
  void addInventoryItem_quantityIsNull_failsWith400() throws Exception {
    long bueId = 1L;
    inventoryItem.setQuantity(null);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectJSONMapper.inventoryItemToRequest(inventoryItem)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @WithMockUser(username = "businessadmin@testing.com")
  @Test
  void addInventoryItem_expiresIsNull_failsWith400() throws Exception {
    long bueId = 1L;
    inventoryItem.setExpires(null);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectJSONMapper.inventoryItemToRequest(inventoryItem)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @WithMockUser(username = "businessadmin@testing.com")
  @Test
  void addInventoryItem_businessDoesNotExist_failsWith400() throws Exception {
    long bueId = 2L;
    when(businessService.addInventoryItem(anyLong(), any(InventoryItem.class)))
        .thenThrow(new BadRequestException(""));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectJSONMapper.inventoryItemToRequest(inventoryItem)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @WithMockUser(username = "businessadmin@testing.com")
  @Test
  void addInventoryItem_productDoesNotExist_failsWith400() throws Exception {
    long bueId = 1L;
    inventoryItem.setProduct(null);
    inventoryItem.setProductId("no_product");
    when(businessService.addInventoryItem(anyLong(), any(InventoryItem.class)))
        .thenThrow(new BadRequestException(""));
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/inventory")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectJSONMapper.inventoryItemToRequest(inventoryItem)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }
}
