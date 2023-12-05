package wasteless.service.productImageServiceTest;

import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.NotAcceptableStatusException;
import wasteless.DGAAConfig;
import wasteless.exception.BadRequestException;
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
import java.util.Objects;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Tests for the ProductImageService.createProductImage
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
class ProductImageServiceTestAddImage {
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
        // To prevent 401 error.
        when(authUtil.isCurrentUserGlobalAdmin()).thenReturn(true);
    }
    
    @AfterAll
    public void afterAll() {
        ProductImageTestHelper.cleanDirectory(imagePath);
    }


    /**
     * Tests that when creating a ProductImage with valid PNG data the save function is called.
     */
    @Test
    void createProductImage_withValidPngData_callsSaveFunction() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:unit_testing_image.png", MediaType.IMAGE_PNG_VALUE);

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));
        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(productImageArgumentCaptor.capture());

        productImageService.createProductImage(0L, "Product", testFile);

        Mockito.verify(productImageRepository, Mockito.times(1)).save(productImageArgumentCaptor.capture());
    }

    /**
     * Tests that when creating a ProductImage with valid JPEG data the save function is called.
     */
    @Test
    void createProductImage_withValidJpegData_callsSaveFunction() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:unit_testing_image.jpg", MediaType.IMAGE_JPEG_VALUE);

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));
        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(productImageArgumentCaptor.capture());

        productImageService.createProductImage(0L, "Product", testFile);

        Mockito.verify(productImageRepository, Mockito.times(1)).save(productImageArgumentCaptor.capture());
    }

    /**
     * Tests that when creating a ProductImage with valid GIF data the save function is called.
     */
    @Test
    void createProductImage_withValidGifData_callsSaveFunction() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:unit_testing_image.gif", MediaType.IMAGE_GIF_VALUE);

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));
        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(productImageArgumentCaptor.capture());

        productImageService.createProductImage(0L, "Product", testFile);

        Mockito.verify(productImageRepository, Mockito.times(1)).save(productImageArgumentCaptor.capture());
    }

    /**
     * Tests that when creating a ProductImage with an image that is too large that an exception is thrown.
     */
    @Test
    void createProductImage_withTooLargeImage_throwsException() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:hubble_test_image.png", MediaType.IMAGE_PNG.toString());

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));
        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(productImageArgumentCaptor.capture());

        Exception exception = assertThrows(BadRequestException.class, () -> productImageService.createProductImage(0L, "Product", testFile));
        Assertions.assertTrue(exception.getMessage().contains("The file exceeds the limit of"));
    }

    /**
     * Tests that when creating a ProductImage with an image that is too large that the save function is not called.
     */
    @Test
    void createProductImage_withTooLargeImage_doesNotCallSaveFunction() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:hubble_test_image.png", MediaType.IMAGE_PNG.toString());

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));
        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(productImageArgumentCaptor.capture());
        try {
            productImageService.createProductImage(0L, "Product", testFile);
        } catch(BadRequestException ignored) {}

        Mockito.verify(productImageRepository, Mockito.times(0)).save(productImageArgumentCaptor.capture());
    }

    /**
     * Tests that when creating a ProductImage with an invalid image that the save function is not called.
     */
    @Test
    void createProductImage_withInvalidDataType_doesNotCallSaveFunction() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:invalid_image_data.txt", MediaType.TEXT_PLAIN.toString());

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));

        try {
            productImageService.createProductImage(0L, "Product", testFile);
        } catch(BadRequestException ignored) {}

        Mockito.verify(productImageRepository, Mockito.times(0)).save(any(ProductImage.class));
    }

    /**
     * Tests that when creating an image with an invalid image that an exception is thrown.
     */
    @Test
    void createProductImage_withInvalidDataType_throwsException() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:invalid_image_data.txt", MediaType.TEXT_PLAIN.toString());

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));

        Exception exception = assertThrows(BadRequestException.class, () -> productImageService.createProductImage(0L, "Product", testFile));
        Assertions.assertTrue(exception.getMessage().contains("is not a supported media type"));
    }

    /**
     * Tests that when creating a ProductImage with an invalid image that the save function is not called.
     */
    @Test
    void createProductImage_withInvalidDataTypeIncorrectMediaType_doesNotCallSaveFunction() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:invalid_image_data.txt", MediaType.IMAGE_PNG.toString());

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));

        try {
            productImageService.createProductImage(0L, "Product", testFile);
        } catch(BadRequestException ignored) {}

        Mockito.verify(productImageRepository, Mockito.times(0)).save(any(ProductImage.class));
    }

    /**
     * Tests that when creating an image with an invalid image that an exception is thrown.
     */
    @Test
    void createProductImage_withInvalidDataTypeIncorrectMediaType_throwsException() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:invalid_image_data.txt", MediaType.IMAGE_PNG.toString());

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));

        Exception exception = assertThrows(BadRequestException.class, () -> productImageService.createProductImage(0L, "Product", testFile));
        Assertions.assertTrue(exception.getMessage().contains("Invalid image format."));
    }

    /**
     * Tests that after creating multiple ProductImages that save is called the correct number of times.
     */
    @Test
    void createProductImage_runningMultipleTimes_correctNumberOfFilesCreated() {
        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));
        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(productImageArgumentCaptor.capture());

        for (int i = 0; i < 20; i++) {
            MultipartFile testFile = ProductImageTestHelper.getFile("classpath:unit_testing_image.png", MediaType.IMAGE_PNG.toString());
            productImageService.createProductImage(0L, "Product", testFile);
        }

        Mockito.verify(productImageRepository, Mockito.times(20)).save(productImageArgumentCaptor.capture());
    }

    /**
     * Tests that when creating a ProductImage with valid data that an image and a thumbnail are created in the files.
     */
    @Test
    void createProductImage_withValidData_createsThumbnailImages() {
        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));
        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(productImageArgumentCaptor.capture());

        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:unit_testing_image.png", MediaType.IMAGE_PNG.toString());
        productImageService.createProductImage(0L, "Product", testFile);

        // Thumbnail folder should have only one thumbnail image inside.
        boolean one = Objects.requireNonNull(new File(imagePath+thumbnailRelPath).list()).length == 1;
        // The images folder should have one image inside AND one folder which is the thumbnails folder.
        boolean two = Objects.requireNonNull(new File(imagePath).list()).length == 2;
        Assertions.assertTrue(one && two);
    }

    /**
     * Tests that when creating a ProductImage with an invalid business id that it does not call the save function.
     */
    @Test
    void createProductImage_withInvalidBusiness_doesNotCallSaveFunction() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:unit_testing_image.png", MediaType.IMAGE_PNG_VALUE);

        when(businessRepository.findById(0L)).thenReturn(Optional.empty());
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));
        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(productImageArgumentCaptor.capture());

        try {
            productImageService.createProductImage(0L, "Product", testFile);
        } catch (Exception ignored) {}

        Mockito.verify(productImageRepository, Mockito.times(0)).save(productImageArgumentCaptor.capture());
    }

    /**
     * Tests that when creating a ProductImage with an invalid business id that it throws the correct exception.
     */
    @Test
    void createProductImage_withInvalidBusiness_correctExceptionIsThrown() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:unit_testing_image.png", MediaType.IMAGE_PNG_VALUE);

        when(businessRepository.findById(0L)).thenReturn(Optional.empty());
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>(Collections.singletonList(new Product())));
        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(productImageArgumentCaptor.capture());

        Exception exception = assertThrows(NotAcceptableStatusException.class, () -> productImageService.createProductImage(0L, "Product", testFile));
        Assertions.assertTrue(exception.getMessage().contains("The business ID does not exist."));
    }

    /**
     * Tests that when creating a ProductImage with invalid product that it does not call the save function.
     */
    @Test
    void createProductImage_withInvalidProduct_doesNotCallSaveFunction() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:unit_testing_image.png", MediaType.IMAGE_PNG_VALUE);

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>());
        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(productImageArgumentCaptor.capture());

        try {
            productImageService.createProductImage(0L, "Product", testFile);
        } catch (Exception ignored) {}

        Mockito.verify(productImageRepository, Mockito.times(0)).save(productImageArgumentCaptor.capture());
    }

    /**
     * Tests that when creating a ProductImage with invalid product that it throws the correct exception.
     */
    @Test
    void createProductImage_withInvalidProduct_correctExceptionThrown() {
        MultipartFile testFile = ProductImageTestHelper.getFile("classpath:unit_testing_image.png", MediaType.IMAGE_PNG_VALUE);

        when(businessRepository.findById(0L)).thenReturn(Optional.of(ProductImageTestHelper.createBusiness()));
        when(productRepository.findByBusinessAndProductId(any(Business.class), eq("Product")))
                .thenReturn(new ArrayList<>());
        ArgumentCaptor<ProductImage> productImageArgumentCaptor = ArgumentCaptor.forClass(ProductImage.class);
        when(productImageRepository.save(any(ProductImage.class))).thenReturn(productImageArgumentCaptor.capture());

        Exception exception = assertThrows(NotAcceptableStatusException.class, () -> productImageService.createProductImage(0L, "Product", testFile));
        Assertions.assertTrue(exception.getMessage().contains("The product ID does not exist."));
    }
}
