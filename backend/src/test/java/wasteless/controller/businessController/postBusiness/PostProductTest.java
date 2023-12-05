package wasteless.controller.businessController.postBusiness;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.model.Address;
import wasteless.model.Business;
import wasteless.model.Product;
import wasteless.model.User;
import wasteless.repository.BusinessRepository;
import wasteless.repository.ProductRepository;
import wasteless.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class PostProductTest{

  @Autowired
  private MockMvc mockMvc;

  private ObjectMapper objectMapper;
  @MockBean private ProductRepository productRepository;
  @MockBean private UserRepository userRepository;
  @MockBean private BusinessRepository businessRepository;
  private HashMap<Long, Business> businesses;
  private User admin;
  private User notAdmin;
  private Product product;

  @BeforeEach
  void setup() {
    businesses = new HashMap<>();
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());

    // Create a regular user
    notAdmin =
        new User(
            1L,
            "John",
            "Doe",
            "Hector",
            "Hector",
            "Johnny",
            "johnsmith99@gmail.com",
            LocalDate.parse("1997-02-01"),
            "",
            new Address("12", "Madeup Lane", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
            "password123",
            LocalDate.now(),
            "user",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>());

    // Create a twin user who is an admin of a business
    admin =
        new User(
            2L,
            "John",
            "Doe",
            "Hector",
            "Hector",
            "Johnny",
            "johndoe99@gmail.com",
            LocalDate.parse("1997-02-01"),
            "",
            new Address("12", "Madeup Lane", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"),
            "password123",
            LocalDate.now(),
            "user",
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new HashSet<>());

    // Add a new business to hash map
    Business business = new Business();
    List<User> admins = new ArrayList<>();
    admins.add(admin);
    business.setBusinessId(1L);
    business.setName("McRonalds");
    business.setDescription("Fast Food");
    business.setAddress(
        new Address("12", "Burger Lane", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041"));
    business.setBusinessType("Accommodation and Food Services");
    business.setPrimaryAdminId(admin.getUserId());
    business.setAdmins(admins);
    business.setProductCatalogue(new ArrayList<>());
    businesses.put(1L, business);

    // Add the business to Admin's businessesAdministered collection
    admin.addBusinessAdministered(businesses.get(1L));

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

    // Configure mocked repos
    // When userRepo is searched for either e-mails, it returns the corresponding user
    // When businessRepo is searched for the one business, it returns it from the hash map
    when(userRepository.findByEmail("johnsmith99@gmail.com"))
        .thenReturn(java.util.Optional.ofNullable(notAdmin));
    when(userRepository.findByEmail("johndoe99@gmail.com"))
        .thenReturn(java.util.Optional.ofNullable(admin));
    when(businessRepository.findByBusinessId(1L))
        .thenReturn(java.util.Optional.ofNullable(businesses.get(1L)));
  }

  @Test
  void business_1AddProductToCatalogueNotLoggedIn() throws Exception {
    long bueId = 1L;
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser
  @Test
  void business_1AddProductToCatalogueAnonymousUser() throws Exception {
    long bueId = 1L;
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser(username = "johndoe99@gmail.com")
  @Test
  void business_1AddProductToCatalogueLoggedInAsAdminOfBusinessReturnsCreated()
      throws Exception {
    long bueId = 1L;
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(MockMvcResultMatchers.status().isCreated());
  }

  @WithMockUser(username = "johndoe99@gmail.com")
  @Test
  void business_1AddProductToCatalogueLoggedInAsAdminOfBusinessIsSavedWithName()
      throws Exception {
    long bueId = 1L;
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses/" + bueId + "/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(product)));
    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository, times(1)).save(productCaptor.capture());
    Assertions.assertEquals(product.getName(), productCaptor.getValue().getName());
  }

  @WithMockUser(username = "johndoe99@gmail.com")
  @Test
  void business_1AddProductToCatalogueLoggedInAsAdminOfBusinessIsSavedWithDescription()
      throws Exception {
    long bueId = 1L;
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses/" + bueId + "/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(product)));
    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository, times(1)).save(productCaptor.capture());
    Assertions.assertEquals(product.getDescription(), productCaptor.getValue().getDescription());
  }

  @WithMockUser(username = "johndoe99@gmail.com")
  @Test
  void business_1AddProductToCatalogueLoggedInAsAdminOfBusinessIsSavedWithRetailPrice()
      throws Exception {
    long bueId = 1L;
    mockMvc.perform(
        MockMvcRequestBuilders.post("/businesses/" + bueId + "/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(product)));
    ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
    verify(productRepository, times(1)).save(productCaptor.capture());
    Assertions.assertEquals(
        product.getRecommendedRetailPrice(), productCaptor.getValue().getRecommendedRetailPrice());
  }

  @WithMockUser(username = "johndoe99@gmail.com")
  @Test
  void edgeNonExistentBusinessAddProductToCatalogueLoggedInAsAdmin() throws Exception {
    long bueId = 2L;
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser(username = "johndoe99@gmail.com")
  @Test
  void extremeNonExistentBusinessAddProductToCatalogueLoggedInAsAdmin() throws Exception {
    long bueId = -1L;
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser(username = "johndoe99@gmail.com")
  @Test
  void extreme_2NonExistentBusinessAddProductToCatalogueLoggedInAsAdmin() throws Exception {
    long bueId = 100L;
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
  }

  @WithMockUser(username = "johnsmith99@gmail.com")
  @Test
  void business_1AddProductToCatalogueLoggedInAsNotAdmin() throws Exception {
    long bueId = 1L;
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }

  @WithMockUser(username = "johndoe99@gmail.com")
  @Test
  void business_1AddProductToCatalogueLoggedInAsAdminButProductHasNoName() throws Exception {
    long bueId = 1L;
    product.setName(null);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/businesses/" + bueId + "/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());
  }

  @Test
  void business_1IsCodeValidNotLoggedInReturn401() throws Exception {
    long bueId = 1L;
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/business/1/check-product-code/" + bueId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithAnonymousUser
  @Test
  void business_1IsCodeValidLoggedInAsAnonymousReturn401() throws Exception {
    long bueId = 1L;
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/business/1/check-product-code/" + bueId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(product)))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser("thisusercannotexist")
  @Test
  void business_1IsCodeValidLoggedInAsNonExistentUser401() throws Exception {
    long bueId = 1L;
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(
                "/business/" + bueId + "/check-product-code/" + "unused_product_code"))
        .andExpect(MockMvcResultMatchers.status().isUnauthorized());
  }

  @WithMockUser("johndoe99@gmail.com")
  @Test
  void business_1IsCodeValidLoggedInNoFoundProductsReturn200() throws Exception {
    long bueId = 1L;
    Mockito.when(
            productRepository.findByBusinessAndProductId(
                Mockito.any(Business.class), Mockito.anyString()))
        .thenReturn(new ArrayList<>());
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(
                "/business/" + bueId + "/check-product-code/" + "unused_product_code"))
        .andExpect(MockMvcResultMatchers.status().isOk());
  }
}
