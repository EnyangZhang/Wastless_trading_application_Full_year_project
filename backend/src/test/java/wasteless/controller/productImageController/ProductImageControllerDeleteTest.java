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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductImageControllerDeleteTest {
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

    private ProductImage productImage;
    private Product product;
    private User user;
    private Business business;
    private File imageFile;
    private File thumbnailFile;

    @BeforeEach
    void beforeEach() {
        ProductImageTestHelper.cleanDirectory(imagePath);
        ProductImageTestHelper.createDirectories(imagePath, thumbnailRelPath);

        user = new User();
        user.setUserId(1);
        business = ProductImageTestHelper.createBusiness();
        business.setAdmins(Collections.singletonList(user));
        product = new Product();

        imageFile = new File(imagePath + "img.png");
        thumbnailFile = new File(imagePath + thumbnailRelPath + "img.png");

        productImage = ProductImageTestHelper.createTestProductImage(imageFile, thumbnailFile);
        productImage.setPrimary(true);
        product.setImages(Collections.singletonList(productImage));
        business.setProductCatalogue(Collections.singletonList(product));

        when(authUtil.getCurrentUser()).thenReturn(user);
    }

    @Test
    @WithMockUser
    void deleteProductImage_withValidData_returnsOk() throws Exception {
        when(businessRepository.findById(any(long.class)))
                .thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(Collections.singletonList(product));
        when(productImageRepository.findById(any(long.class)))
                .thenReturn(Optional.of(productImage));

        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .delete("/businesses/1/products/pro/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void deleteProductImage_withValidDataAndGlobalAdmin_returnsOk() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(new User());
        when(authUtil.isCurrentUserGlobalAdmin()).thenReturn(true);

        when(businessRepository.findById(any(long.class)))
                .thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(Collections.singletonList(product));
        when(productImageRepository.findById(any(long.class)))
                .thenReturn(Optional.of(productImage));

        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .delete("/businesses/1/products/pro/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isOk());
    }

    @Test
    void deleteProductImage_withUnauthorizedUser_returnsUnauthorized() throws Exception {
        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .delete("/businesses/1/products/pro/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void deleteProductImage_withInvalidBusinessId_returnsNotAcceptable() throws Exception {
        when(businessRepository.findByBusinessId(any(long.class))).thenReturn(Optional.empty());
        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .delete("/businesses/1/products/pro/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void deleteProductImage_withInvalidProductId_returnsNotAcceptable() throws Exception {
        when(businessRepository.findById(any(long.class)))
                .thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(new ArrayList<>());

        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .delete("/businesses/1/products/pro/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void deleteProductImage_withInvalidImageId_returnsNotAcceptable() throws Exception {
        when(businessRepository.findById(any(long.class)))
                .thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(Collections.singletonList(product));
        when(productImageRepository.findById(any(long.class)))
                .thenReturn(Optional.empty());
        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .delete("/businesses/1/products/pro/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void deleteProductImage_withUnrelatedImageId_returnsNotAcceptable() throws Exception {
        when(businessRepository.findById(any(long.class)))
                .thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(Collections.singletonList(product));
        when(productImageRepository.findById(any(long.class)))
                .thenReturn(Optional.of(new ProductImage()));
        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .delete("/businesses/1/products/pro/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @WithMockUser
    void deleteProductImage_withNonAdminUser_returnsForbidden() throws Exception {
        when(authUtil.getCurrentUser()).thenReturn(new User());
        when(businessRepository.findById(any(long.class)))
                .thenReturn(Optional.of(business));
        MockHttpServletRequestBuilder mock = MockMvcRequestBuilders
                .delete("/businesses/1/products/pro/images/1");
        mockMvc
                .perform(mock)
                .andExpect(status().isForbidden());
    }
}
