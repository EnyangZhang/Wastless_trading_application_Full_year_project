package wasteless.controller.productImageController;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductImageControllerPostTest {
    @Autowired protected MockMvc mockMvc;
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

    private ProductImage image;

    @BeforeAll
    public void before() {
        image = new ProductImage();
        image.setProductImageId(1);
    }

    @BeforeEach
    public void setup() {
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(image);
    }

    /**
     * Tests that the 201 status response is received when sending valid data.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void addProductImage_withValidData_returnsCreated() throws Exception {
        User user = new User();
        user.setUserId(0);
        Business business = ProductImageTestHelper.createBusiness();
        business.setAdmins(Collections.singletonList(user));

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class))).thenReturn(Collections.singletonList(new Product()));

        MockMultipartHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .multipart("/businesses/1/products/pro/images")
                .file(ProductImageTestHelper.getFile("classpath:unit_testing_image.png",  MediaType.IMAGE_PNG_VALUE));
        mockMvc
                .perform(mock)
                .andExpect(status().isCreated());
    }

    /**
     * Tests that the 201 status response is received when sending valid data and the user is a global admin.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void addProductImage_withValidDataAsAdmin_returnsCreated() throws Exception {
        User user = new User();
        user.setUserId(0);
        user.setRole("global_admin");
        Business business = ProductImageTestHelper.createBusiness();

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(authUtil.isCurrentUserGlobalAdmin()).thenReturn(true);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class))).thenReturn(Collections.singletonList(new Product()));

        MockMultipartHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .multipart("/businesses/1/products/pro/images")
                .file(ProductImageTestHelper.getFile("classpath:unit_testing_image.png",  MediaType.IMAGE_PNG_VALUE));
        mockMvc
                .perform(mock)
                .andExpect(status().isCreated());
    }

    /**
     * Tests that when the user is not authenticated that it returns 401 status code.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    void addProductImage_withUnauthenticatedUser_returnsUnauthorized() throws Exception {
        User user = new User();
        user.setUserId(0);
        Business business = ProductImageTestHelper.createBusiness();
        business.setAdmins(Collections.singletonList(user));

        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class))).thenReturn(Collections.singletonList(new Product()));

        MockMultipartHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .multipart("/businesses/1/products/pro/images")
                .file(ProductImageTestHelper.getFile("classpath:unit_testing_image.png",  MediaType.IMAGE_PNG_VALUE));
        mockMvc
                .perform(mock)
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests that when the user lacks permission to update the product images that a 403 status code is returned.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void addProductImage_withNonAdminUser_returnsForbidden() throws Exception {
        User user = new User();
        Business business = ProductImageTestHelper.createBusiness();

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class))).thenReturn(Collections.singletonList(new Product()));

        MockMultipartHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .multipart("/businesses/1/products/pro/images")
                .file(ProductImageTestHelper.getFile("classpath:unit_testing_image.png",  MediaType.IMAGE_PNG_VALUE));
        mockMvc
                .perform(mock)
                .andExpect(status().isForbidden());
    }

    /**
     * Tests that when adding a product image with invalid product id that 406 status code is returned.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void addProductImage_withInvalidProductId_returnsNotAcceptable() throws Exception {
        User user = new User();
        user.setUserId(0);
        Business business = ProductImageTestHelper.createBusiness();
        business.setAdmins(Collections.singletonList(user));

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class))).thenReturn(new ArrayList<>());

        MockMultipartHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .multipart("/businesses/1/products/pro/images")
                .file(ProductImageTestHelper.getFile("classpath:unit_testing_image.png",  MediaType.IMAGE_PNG_VALUE));
        mockMvc
                .perform(mock)
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that when adding a product image with invalid business id that 406 status code is returned.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void addProductImage_withInvalidBusiness_returnsNotAcceptable() throws Exception {
        User user = new User();
        user.setUserId(0);
        Business business = ProductImageTestHelper.createBusiness();
        business.setAdmins(Collections.singletonList(user));

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.empty());
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class))).thenReturn(Collections.singletonList(new Product()));

        MockMultipartHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .multipart("/businesses/1/products/pro/images")
                .file(ProductImageTestHelper.getFile("classpath:unit_testing_image.png",  MediaType.IMAGE_PNG_VALUE));
        mockMvc
                .perform(mock)
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests that when an image that is too large is sent that a 400 status code is returned.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void addProductImage_withOversizeImage_returnsBadRequest() throws Exception {
        User user = new User();
        user.setUserId(0);
        Business business = ProductImageTestHelper.createBusiness();
        business.setAdmins(Collections.singletonList(user));

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class))).thenReturn(Collections.singletonList(new Product()));

        MockMultipartHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .multipart("/businesses/1/products/pro/images")
                .file(ProductImageTestHelper.getFile("classpath:hubble_test_image.png",  MediaType.IMAGE_PNG_VALUE));
        mockMvc
                .perform(mock)
                .andExpect(status().isBadRequest());
    }

    /**
     * Tests that when adding a product image with invalid type that a 400 status code is returned.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void addProductImage_withInvalidType_returnsBadRequest() throws Exception {
        User user = new User();
        user.setUserId(0);
        Business business = ProductImageTestHelper.createBusiness();
        business.setAdmins(Collections.singletonList(user));

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class))).thenReturn(Collections.singletonList(new Product()));

        MockMultipartHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .multipart("/businesses/1/products/pro/images")
                .file(ProductImageTestHelper.getFile("classpath:invalid_image_data.txt",  MediaType.TEXT_PLAIN_VALUE));
        mockMvc
                .perform(mock)
                .andExpect(status().isBadRequest());
    }
}
