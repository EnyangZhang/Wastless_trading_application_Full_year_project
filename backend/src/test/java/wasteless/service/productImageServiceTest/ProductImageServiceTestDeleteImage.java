package wasteless.service.productImageServiceTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.NotAcceptableStatusException;
import wasteless.DGAAConfig;
import wasteless.exception.ForbiddenException;
import wasteless.exception.InternalServerError;
import wasteless.model.Business;
import wasteless.model.Product;
import wasteless.model.ProductImage;
import wasteless.model.User;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProductImageServiceTestDeleteImage {
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
    private ProductRepository productRepository;

    @MockBean
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductImageService productImageService;

    File imageFile;
    File thumbnailFile;
    Business business;
    User user;
    Product product;

    @BeforeEach
    void beforeEach() {
        ProductImageTestHelper.cleanDirectory(imagePath);
        ProductImageTestHelper.createDirectories(imagePath, thumbnailRelPath);

        imageFile = new File(imagePath + "img.png");
         thumbnailFile = new File(imagePath + thumbnailRelPath + "img.png");

        business = ProductImageTestHelper.createBusiness();
        user = new User();
        user.setUserId(0L);
        business.setAdmins(Collections.singletonList(user));
        product = new Product();

        when(authUtil.getCurrentUser()).thenReturn(user);
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.of(business));
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(new ArrayList<>(Collections.singletonList(product)));
    }

    /**
     * Tests that when deleting an image, the image file is deleted.
     */
    @Test
    void deleteProductImage_withExistingImage_deletesTheImage() {
        ProductImage productImage = ProductImageTestHelper.createTestProductImage(imageFile, thumbnailFile);
        product.setImages(Collections.singletonList(productImage));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.of(productImage));

        productImageService.deleteProductImage(0L, "bing", 0L);

        Assertions.assertFalse(imageFile.exists() || thumbnailFile.exists());
    }

    /**
     * Tests that when deleting an image as a user that is a global admin but not
     * a business admin, the image file is deleted.
     */
    @Test
    void deleteProductImage_withExistingImageAndAdmin_deletesTheImage() {
        User user = new User();
        user.setUserId(1111);
        when(authUtil.getCurrentUser()).thenReturn(user);
        when(authUtil.isCurrentUserGlobalAdmin()).thenReturn(true);

        ProductImage productImage = ProductImageTestHelper.createTestProductImage(imageFile, thumbnailFile);
        product.setImages(Collections.singletonList(productImage));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.of(productImage));

        productImageService.deleteProductImage(0L, "bing", 0L);

        Assertions.assertFalse(imageFile.exists() && thumbnailFile.exists());
    }

    /**
     * Tests that when attempting to delete an image by its image id, but no image was found to delete, throws an
     * exception with an appropriate message.
     */
    @Test
    void deleteProductImage_nonExistentImage_throwsException() {
        ProductImage productImage = new ProductImage(
                product, imagePath + "img.png", imagePath + thumbnailRelPath + "img.png");
        product.setImages(Collections.singletonList(productImage));
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.of(productImage));

        Exception exception = Assertions.assertThrows(InternalServerError.class, () ->
                productImageService.deleteProductImage(0L, "test", 0L));
        Assertions.assertTrue(exception.getMessage().contains("Could not delete files."));
    }

    /**
     * Test if product repository cannot find a corresponding ProductImage entity from the database, given a product ID,
     * then throw exception with appropriate message.
     */
    @Test
    void deleteProductImage_imageNotFound_throwsException() {
        when(productImageRepository.findById(any(long.class))).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(NotAcceptableStatusException.class, () ->
                productImageService.deleteProductImage(0L, "test", 0L));
        Assertions.assertTrue(exception.getMessage().contains("Image ID does not exist"));
    }

    /**
     * Tests that when trying to delete a product with an invalid business ID that the correct exception is thrown.
     */
    @Test
    void deleteProductImage_withInvalidBusinessId_throwsCorrectException() {
        when(businessRepository.findById(any(long.class))).thenReturn(Optional.empty());

        Exception exception = Assertions.assertThrows(NotAcceptableStatusException.class, () ->
                productImageService.deleteProductImage(0L, "test", 0L));
        Assertions.assertTrue(exception.getMessage().contains("The business ID does not exist."));
    }

    /**
     * Tests that when trying to delete a product image with invalid product ID that the correct exception is thrown.
     */
    @Test
    void deleteProductImage_withInvalidProductId_throwsCorrectException() {
        when(productRepository.findByBusinessAndProductId(any(Business.class), any(String.class)))
                .thenReturn(new ArrayList<>());

        Exception exception = Assertions.assertThrows(NotAcceptableStatusException.class, () ->
                productImageService.deleteProductImage(0L, "test", 0L));
        Assertions.assertTrue(exception.getMessage().contains("The product ID does not exist."));
    }

    /**
     * Tests when trying to delete image with non-admin user that the correct exception is thrown.
     */
    @Test
    void deleteProductImage_withNonAdminUser_throwsCorrectException() {
        User user = new User();
        user.setUserId(1111);
        when(authUtil.getCurrentUser()).thenReturn(user);

        Exception exception = Assertions.assertThrows(ForbiddenException.class, () ->
                productImageService.deleteProductImage(0L, "test", 0L));
        Assertions.assertTrue(exception.getMessage().contains("You do not have permission to change this product."));
    }
}
