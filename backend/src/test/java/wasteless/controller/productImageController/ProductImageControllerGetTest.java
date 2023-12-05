package wasteless.controller.productImageController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductImageControllerGetTest {
    @Value("${image.storagepath}")
    private String imagePath;
    @Value("${image.thumbnail-rel-path}")
    private String thumbnailRelPath;

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

    private Business business;
    private User user;
    private Product product;
    private ProductImage productImage;

    @BeforeEach
    public void beforeEach() {
        ProductImageTestHelper.cleanDirectory(imagePath);
        ProductImageTestHelper.createDirectories(imagePath, thumbnailRelPath);
        user = new User();
        product = new Product();
        business = ProductImageTestHelper.createBusiness();
        business.setAdmins(Collections.singletonList(user));
        business.setProductCatalogue(Collections.singletonList(product));
        File imageFile = new File(imagePath + "img.png");
        File thumbnailFile = new File(imagePath + thumbnailRelPath + "img.png");
        productImage = ProductImageTestHelper.createTestProductImage(imageFile, thumbnailFile);
        product.setImages(Collections.singletonList(productImage));
    }

    /**
     * Tests getting an image with valid parameters returns OK.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void getProductImage_withValidData_returnsOk() throws Exception {
        product.setImages(Collections.singletonList(productImage));

        when(authUtil.getCurrentUser()).thenReturn(user);

        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(product)));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.of(productImage));

        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .get("/businesses/1/products/Product/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isOk());
    }

    /**
     * Tests getting an image thumbnail with valid parameters returns OK.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void getProductImageThumbnail_withValidData_returnsOk() throws Exception {
        product.setImages(Collections.singletonList(productImage));

        when(authUtil.getCurrentUser()).thenReturn(user);

        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(product)));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.of(productImage));

        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .get("/businesses/1/products/Product/images/1/thumbnail");
        mockMvc
                .perform(mock)
                .andExpect(status().isOk());
    }

    /**
     * Tests getting an image with valid parameters returns the correct data.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void getProductImage_withValidData_returnsCorrectData() throws Exception {
        product.setImages(Collections.singletonList(productImage));

        when(authUtil.getCurrentUser()).thenReturn(user);

        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(product)));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.of(productImage));

        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .get("/businesses/1/products/Product/images/1");
        mockMvc
                .perform(mock)
                .andExpect(MockMvcResultMatchers.content().bytes(ProductImageTestHelper.testFileData.getBytes()));
    }

    /**
     * Tests getting an image thumbnail with valid parameters returns the correct data.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void getProductImageThumbnail_withValidData_returnsCorrectData() throws Exception {
        product.setImages(Collections.singletonList(productImage));

        when(authUtil.getCurrentUser()).thenReturn(user);

        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(product)));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.of(productImage));

        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .get("/businesses/1/products/Product/images/1/thumbnail");
        mockMvc
                .perform(mock)
                .andExpect(MockMvcResultMatchers.content().bytes(ProductImageTestHelper.testFileDataThumb.getBytes()));
    }

    /**
     * Tests getting an image while not logged in returns UNAUTHORIZED.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    void getProductImage_notLoggedIn_returnsUnauthorized() throws Exception {
        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .get("/businesses/1/products/Product/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests getting an image thumbnail while not logged in returns UNAUTHORIZED.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    void getProductImageThumbnail_notLoggedIn_returnsUnauthorized() throws Exception {
        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .get("/businesses/1/products/Product/images/1/thumbnail");
        mockMvc
                .perform(mock)
                .andExpect(status().isUnauthorized());
    }

    /**
     * Tests getting a product with an invalid business ID returns NOT ACCEPTABLE.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void getProductImage_withInvalidBusinessId_returnsNotAcceptable() throws Exception {
        product.setImages(Collections.singletonList(productImage));

        when(authUtil.getCurrentUser()).thenReturn(user);

        when(businessRepository.findById(any(long.class))).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .get("/businesses/1/products/Product/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests getting an image with an invalid product ID or a product ID that is
     * unrelated to the business ID. Tests that it returns NOT ACCEPTABLE.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void getProductImage_withInvalidProductId_returnsNotAcceptable() throws Exception {
        product.setImages(Collections.singletonList(productImage));

        when(authUtil.getCurrentUser()).thenReturn(user);

        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>());

        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .get("/businesses/1/products/Product/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests getting a product image with an image ID that does not exist returns NOT ACCEPTABLE.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void getProductImage_withInvalidProductImageId_returnsNotAcceptable() throws Exception {
        product.setImages(Collections.singletonList(productImage));

        when(authUtil.getCurrentUser()).thenReturn(user);

        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(product)));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .get("/businesses/1/products/Product/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isNotAcceptable());
    }

    /**
     * Tests getting a product image with an image ID that is unrelated to the given productId
     * returns NOT ACCEPTABLE.
     * @throws Exception Exception thrown by mock mvc. 
     */
    @Test
    @WithMockUser
    void getProductImage_withUnrelatedProductImage_returnsNotAcceptable() throws Exception {
        product.setImages(Collections.singletonList(productImage));

        when(authUtil.getCurrentUser()).thenReturn(user);

        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.of(productImage));

        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .get("/businesses/1/products/Product/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isNotAcceptable());
    }
}
