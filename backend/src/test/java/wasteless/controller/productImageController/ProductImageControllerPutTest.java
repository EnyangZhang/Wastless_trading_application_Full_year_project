package wasteless.controller.productImageController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.DGAAConfig;
import wasteless.model.Business;
import wasteless.model.Product;
import wasteless.model.ProductImage;
import wasteless.model.User;
import wasteless.repository.BusinessRepository;
import wasteless.repository.ProductImageRepository;
import wasteless.repository.ProductRepository;
import wasteless.security.AuthUtil;
import wasteless.test_helpers.ProductImageTestHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductImageControllerPutTest {
    @Autowired
    protected MockMvc mockMvc;
    protected MvcResult mvcResult;

    @MockBean
    private DGAAConfig dgaaConfig;

    @MockBean
    private AuthUtil authUtil;

    @MockBean
    private ProductImageRepository productImageRepository;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private ProductRepository productRepository;

    private User user;

    private Business business;

    private ProductImage image;

    private Product product;

    @BeforeEach
    public void before() {
        user = new User();
        business = ProductImageTestHelper.createBusiness();
        image = new ProductImage();
        image.setProductImageId(1);
        product = new Product();
        product.setImages(Collections.singletonList(image));
    }

    /**
     * Tests that the 200 status response is received when sending valid data.
     * @throws Exception Exception thrown by mock mvc.
     */
    @Test
    @WithMockUser
    void setPrimaryImage_withValidData_returnsOK() throws Exception {
        user.setUserId(0);
        business.setAdmins(Collections.singletonList(user));

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(Collections.singletonList(product));
        when(productImageRepository.findById(any(Long.class))).thenReturn(Optional.of(image));

        mockMvc
                .perform(MockMvcRequestBuilders.put(
                        "/businesses/" + business.getBusinessId() + "/products/" + product.getProductId() +
                                "/images/" + image.getProductImageId() + "/makeprimary"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Tests that the 200 status response is received when sending valid data and the user is a global admin.
     * @throws Exception Exception thrown by mock mvc.
     */
    @Test
    @WithMockUser
    void setPrimaryImage_withValidDataAsAdmin_returnsOK() throws Exception {
        user.setRole("global_admin");

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(authUtil.isCurrentUserGlobalAdmin()).thenReturn(true);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(Collections.singletonList(product));
        when(productImageRepository.findById(any(Long.class))).thenReturn(Optional.of(image));

        mockMvc
                .perform(MockMvcRequestBuilders.put(
                        "/businesses/" + business.getBusinessId() + "/products/" + product.getProductId() +
                                "/images/" + image.getProductImageId() + "/makeprimary"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    /**
     * Tests that when the user is not authenticated that it returns 401 status code.
     * @throws Exception Exception thrown by mock mvc.
     */
    @Test
    void setPrimaryImage_withUnauthenticatedUser_returnsUnauthorized() throws Exception {
        mockMvc
                .perform(MockMvcRequestBuilders.put(
                        "/businesses/" + business.getBusinessId() + "/products/" + product.getProductId() +
                                "/images/" + image.getProductImageId() + "/makeprimary"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    /**
     * Tests that when the user lacks permission to update the product images that a 403 status code is returned.
     * @throws Exception Exception thrown by mock mvc.
     */
    @Test
    @WithMockUser
    void setPrimaryImage_withNonAdminUser_returnsForbidden() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(Collections.singletonList(product));
        when(productImageRepository.findById(any(Long.class))).thenReturn(Optional.of(image));

        mockMvc
                .perform(MockMvcRequestBuilders.put(
                        "/businesses/" + business.getBusinessId() + "/products/" + product.getProductId() +
                                "/images/" + image.getProductImageId() + "/makeprimary"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    /**
     * Tests that when setting a product image as primary with invalid product id that 406 status code is returned.
     * @throws Exception Exception thrown by mock mvc.
     */
    @Test
    @WithMockUser
    void setPrimaryImage_withInvalidProductId_returnsNotAcceptable() throws Exception {
        user.setUserId(0);
        business.setAdmins(Collections.singletonList(user));

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(new ArrayList<>());
        when(productImageRepository.findById(any(Long.class))).thenReturn(Optional.of(image));

        mockMvc
                .perform(MockMvcRequestBuilders.put(
                        "/businesses/" + business.getBusinessId() + "/products/" + product.getProductId() +
                                "/images/" + image.getProductImageId() + "/makeprimary"))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    /**
     * Tests that when setting a product image as primary with invalid business id that 406 status code is returned.
     * @throws Exception Exception thrown by mock mvc.
     */
    @Test
    @WithMockUser
    void setPrimaryImage_withInvalidBusiness_returnsNotAcceptable() throws Exception {
        user.setUserId(0);
        business.setAdmins(Collections.singletonList(user));

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.empty());
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(Collections.singletonList(product));
        when(productImageRepository.findById(any(Long.class))).thenReturn(Optional.of(image));

        mockMvc
                .perform(MockMvcRequestBuilders.put(
                        "/businesses/" + business.getBusinessId() + "/products/" + product.getProductId()
                                + "/images/" + image.getProductImageId() + "/makeprimary"))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    /**
     * Tests that when the primary image is set for a product that doesn't exist, a 406 status code is returned
     * @throws Exception Exception thrown by mock mvc.
     */
    @Test
    @WithMockUser
    void setPrimaryImage_withInvalidProductImageId_returnsNotAcceptable() throws Exception {
        user.setUserId(0);
        business.setAdmins(Collections.singletonList(user));

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(Collections.singletonList(product));
        when(productImageRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        mockMvc
                .perform(MockMvcRequestBuilders.put(
                        "/businesses/" + business.getBusinessId() + "/products/" + product.getProductId() +
                                "/images/" + image.getProductImageId() + "/makeprimary"))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    /**
     * Tests that the 200 status response is received when setting an image that is already primary to be primary.
     * @throws Exception Exception thrown by mock mvc.
     */
    @Test
    @WithMockUser
    void setPrimaryImage_withImageAsPrimaryImage_returnsOK() throws Exception {
        user.setUserId(0);
        business.setAdmins(Collections.singletonList(user));
        image.setPrimary(true);

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(Collections.singletonList(product));
        when(productImageRepository.findById(any(Long.class))).thenReturn(Optional.of(image));

        mockMvc
                .perform(MockMvcRequestBuilders.put(
                        "/businesses/" + business.getBusinessId() + "/products/" + product.getProductId() +
                                "/images/" + image.getProductImageId() + "/makeprimary"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
