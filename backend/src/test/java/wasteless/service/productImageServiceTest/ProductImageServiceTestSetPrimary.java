package wasteless.service.productImageServiceTest;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
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

import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Tests for ProductImageService.setAsPrimary
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class ProductImageServiceTestSetPrimary {
    @Value("${image.storagepath}")
    private String imagePath;

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
        // To prevent 401 error.
        when(authUtil.isCurrentUserGlobalAdmin()).thenReturn(true);
    }

    @AfterAll
    public void afterAll() {
        ProductImageTestHelper.cleanDirectory(imagePath);
    }

    @Test
    public void setAsPrimary_withPreExistingPrimaryImage_updatesImagesCorrectly() {
        Product testProduct = new Product();
        ProductImage firstImage = new ProductImage(testProduct, "path", "paththumb");
        firstImage.setPrimary(true);
        ProductImage secondImage = new ProductImage(testProduct, "path", "paththumb");
        secondImage.setPrimary(false);
        testProduct.setImages(Arrays.asList(firstImage, secondImage));

        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(testProduct)));
        when(productImageRepository.findById(any())).thenReturn(Optional.of(secondImage));
        when(productImageRepository.save(secondImage)).thenReturn(secondImage);

        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        productImageService.setAsPrimary(0L, "Product", 0L);
        verify(productImageRepository, times(2)).save(productImageArgumentCaptor.capture());
        List<ProductImage> capturedImages = productImageArgumentCaptor.getAllValues();
        Assertions.assertTrue(firstImage.equals(capturedImages.get(0)) && secondImage.equals(capturedImages.get(1)));
    }

    @Test
    public void setAsPrimary_withInvalidImageID_doesNotCallSaveFunction() {
        Product testProduct = new Product();
        ProductImage firstImage = new ProductImage(testProduct, "path", "paththumb");
        firstImage.setPrimary(true);
        ProductImage secondImage = new ProductImage(testProduct, "path", "paththumb");
        secondImage.setPrimary(false);
        // Does not include secondary image
        testProduct.setImages(Collections.singletonList(firstImage));

        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(testProduct)));
        when(productImageRepository.findById(any())).thenReturn(Optional.of(secondImage));
        when(productImageRepository.save(secondImage)).thenReturn(secondImage);

        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        try {
            productImageService.setAsPrimary(0L, "Product", 0L);
        } catch (Exception ignored) {}
        verify(productImageRepository, times(0)).save(productImageArgumentCaptor.capture());
    }

    @Test
    public void setAsPrimary_withInvalidImageID_throwsCorrectException() {
        Product testProduct = new Product();
        ProductImage firstImage = new ProductImage(testProduct, "path", "paththumb");
        firstImage.setPrimary(true);
        ProductImage secondImage = new ProductImage(testProduct, "path", "paththumb");
        secondImage.setPrimary(false);
        // Does not include secondary image
        testProduct.setImages(Collections.singletonList(firstImage));

        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(testProduct)));
        when(productImageRepository.findById(any())).thenReturn(Optional.of(secondImage));
        when(productImageRepository.save(secondImage)).thenReturn(secondImage);

        Exception exception = assertThrows(NotAcceptableStatusException.class, () -> productImageService.setAsPrimary(0L, "Product", 0L));
        Assertions.assertTrue(exception.getMessage().contains("The product does not have this image."));
    }

    @Test
    public void setAsPrimary_withInvalidBusiness_throwsCorrectException() {
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.empty());

        Exception exception = assertThrows(NotAcceptableStatusException.class, () -> productImageService.setAsPrimary(0L, "Product", 0L));
        Assertions.assertTrue(exception.getMessage().contains("The business ID does not exist."));
    }

    @Test
    public void setAsPrimary_withInvalidBusiness_doesNotCallSaveFunction() {
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.empty());

        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        try {
            productImageService.setAsPrimary(0L, "Product", 0L);
        } catch (Exception ignored) {}
        verify(productImageRepository, times(0)).save(productImageArgumentCaptor.capture());
    }

    @Test
    public void setAsPrimary_withInvalidProduct_throwsCorrectException() {
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>());

        Exception exception = assertThrows(NotAcceptableStatusException.class, () -> productImageService.setAsPrimary(0L, "Product", 0L));
        Assertions.assertTrue(exception.getMessage().contains("The product ID does not exist."));
    }

    @Test
    public void setAsPrimary_withInvalidProduct_doesNotCallSaveFunction() {
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>());

        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        try {
            productImageService.setAsPrimary(0L, "Product", 0L);
        } catch (Exception ignored) {}
        verify(productImageRepository, times(0)).save(productImageArgumentCaptor.capture());
    }
}
