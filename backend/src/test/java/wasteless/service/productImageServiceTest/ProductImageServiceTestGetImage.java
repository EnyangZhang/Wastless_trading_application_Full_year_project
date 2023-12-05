package wasteless.service.productImageServiceTest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.NotAcceptableStatusException;
import wasteless.DGAAConfig;
import wasteless.model.Business;
import wasteless.model.Product;
import wasteless.model.ProductImage;
import wasteless.repository.BusinessRepository;
import wasteless.repository.ProductImageRepository;
import wasteless.repository.ProductRepository;
import wasteless.security.AuthUtil;
import wasteless.service.ProductImageService;
import wasteless.test_helpers.ProductImageTestHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * These tests test the get function of the ProductImageService.
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductImageServiceTestGetImage {
    @Value("${image.storagepath}")
    private String imagePath;
    @Value("${image.thumbnail-rel-path}")
    private String thumbnailRelPath;

    @MockBean
    private DGAAConfig dgaaConfig;

    @MockBean
    private AuthUtil authUtil;

    @MockBean
    private BusinessRepository businessRepository;

    @MockBean
    private ProductImageRepository productImageRepository;

    @MockBean
    private ProductRepository productRepository;

    @Autowired
    private ProductImageService productImageService;

    @BeforeEach
    public void beforeEach() {
        ProductImageTestHelper.cleanDirectory(imagePath);
        ProductImageTestHelper.createDirectories(imagePath, thumbnailRelPath);
        // To prevent 401 error.
        when(authUtil.isCurrentUserGlobalAdmin()).thenReturn(true);
    }

    @AfterAll
    public void afterAll() {
        ProductImageTestHelper.cleanDirectory(imagePath);
    }

    /**
     * Tests that when using valid data and thumbnail set to false that the image is returned.
     */
    @Test
    void getProductImage_withValidParameters_returnsCorrectImage() {
        File imageFile = new File(imagePath + "img.png");
        File thumbnailFile = new File(imagePath + thumbnailRelPath + "img.png");
        ProductImage productImage = ProductImageTestHelper.createTestProductImage(imageFile, thumbnailFile);

        Product product = new Product();
        product.setImages(Collections.singletonList(productImage));

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(product)));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.of(productImage));

        ProductImageService.ImageTypeAndData data = productImageService.getProductImage(0L, "Product", 0L, false);
        Assertions.assertArrayEquals(ProductImageTestHelper.testFileData.getBytes(), data.getData());
    }

    /**
     * Tests that with valid data and thumbnail set to true that the thumbnail is returned.
     */
    @Test
    void getProductImage_withValidParametersAndThumbnailTrue_returnsCorrectImage() {
        File imageFile = new File(imagePath + "img.png");
        File thumbnailFile = new File(imagePath + thumbnailRelPath + "img.png");
        ProductImage productImage = ProductImageTestHelper.createTestProductImage(imageFile, thumbnailFile);

        Product product = new Product();
        product.setImages(Collections.singletonList(productImage));

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(product)));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.of(productImage));

        ProductImageService.ImageTypeAndData data = productImageService.getProductImage(0L, "Product", 0L, true);
        Assertions.assertArrayEquals(ProductImageTestHelper.testFileDataThumb.getBytes(), data.getData());
    }

    /**
     * Tests that when using invalid business id that the correct exception is thrown.
     */
    @Test
    void getProductImage_withInvalidBusinessID_throwsCorrectException() {
        when(businessRepository.findById(0L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotAcceptableStatusException.class, () ->
                productImageService.getProductImage(0L, "Product", 0L, false));
        Assertions.assertTrue(exception.getMessage().contains("The business ID does not exist."));
    }

    /**
     * Tests that when using invalid product id that the correct exception is thrown.
     */
    @Test
    void getProductImage_withInvalidProductID_throwsCorrectException() {
        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>());

        Exception exception = assertThrows(NotAcceptableStatusException.class, () ->
                productImageService.getProductImage(0L, "Product", 0L, false));
        Assertions.assertTrue(exception.getMessage().contains("The product ID does not exist."));
    }

    /**
     * Tests that when using invalid image id that the correct exception is thrown.
     */
    @Test
    void getProductImage_withInvalidImageID_throwsCorrectException() {
        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(Collections.singletonList(new Product()));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotAcceptableStatusException.class, () ->
                productImageService.getProductImage(0L, "Product", 0L, false));
        Assertions.assertTrue(exception.getMessage().contains("The image ID does not exist."));
    }

    /**
     * Tests that when using an image id that is unrelated to the given product id that the correct exception is thrown.
     */
    @Test
    void getProductImage_withInvalidImageIdForProduct_throwsCorrectException() {
        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(Collections.singletonList(new Product()));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.of(new ProductImage()));

        Exception exception = assertThrows(NotAcceptableStatusException.class, () -> productImageService.getProductImage(0L, "Product", 0L, false));
        Assertions.assertTrue(exception.getMessage().contains("The product does not have this image."));
    }
}
