package wasteless.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import wasteless.model.ProductImage;
import wasteless.service.ProductImageService;

/**
 * This REST controller governs requests in relation to product images
 * It passes teh received information to ProductImageService for
 * validation and then saving.
 */
@RestController
public class ProductImageController {
    private final ProductImageService productImageService;

    /**
     * Autowired constructor that sets the product image service with the
     * application context.
     * @param productImageService The product service to call when requests
     *                            are received.
     */
    @Autowired
    public ProductImageController(ProductImageService productImageService) {
        this.productImageService = productImageService;
    }

    /**
     * This endpoint is for the creation of product images. The request must provide
     * the business and product the image will be related to and the file to be saved.
     * @param businessId The business the target product is owned by.
     * @param productId The product ID to add the image to.
     * @param filename The file to add as the product image.
     * @return A response entity with the corresponding status code depending on
     * if the product image was saved.
     */
    @PostMapping(path = "/businesses/{businessId}/products/{productId}/images", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Object> addProductImage(@PathVariable long businessId,
                                                  @PathVariable String productId,
                                                  @RequestPart("filename") MultipartFile filename) {
        ProductImage newProductImage = productImageService.createProductImage(businessId, productId, filename);
        return new ResponseEntity<>(newProductImage.getProductImageId(), HttpStatus.CREATED);
    }

    /**
     * This endpoint is for setting the primary product image. The request must provide
     * the business, product and image ids.
     * @param businessId The business the target product is owned by.
     * @param productId The product ID to set the primary image.
     * @param imageId The ID of the image to be made primary.
     * @return A response entity with the corresponding status code depending on
     * if the request was successful.
     */
    @PutMapping(path = "/businesses/{businessId}/products/{productId}/images/{imageId}/makeprimary")
    public ResponseEntity<Object> setPrimaryImage(@PathVariable long businessId,
                                                  @PathVariable String productId,
                                                  @PathVariable long imageId) {
        productImageService.setAsPrimary(businessId, productId, imageId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * This endpoint is for getting images by their image ID and the IDs
     * of the owning business and product.
     * @param businessId The business the target product is owned by.
     * @param productId The product ID to add the image to.
     * @param imageId The target image ID.
     * @return A response entity containing a byte array of the image requested.
     */
    @GetMapping(path = "/businesses/{businessId}/products/{productId}/images/{imageId}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable long businessId,
                                                  @PathVariable String productId,
                                                  @PathVariable long imageId) {
        ProductImageService.ImageTypeAndData imageData = productImageService.getProductImage(businessId, productId, imageId, false);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(imageData.getContentType()));
        return new ResponseEntity<>(imageData.getData(), headers, HttpStatus.OK);
    }

    /**
     * This endpoint is for getting images thumbnails by their image ID and
     * the IDs of the owning business and product.
     * @param businessId The business the target product is owned by.
     * @param productId The product ID to add the image to.
     * @param imageId The target image ID.
     * @return A response entity containing a byte array of the image
     * thumbnail requested.
     */
    @GetMapping(path = "/businesses/{businessId}/products/{productId}/images/{imageId}/thumbnail")
    public ResponseEntity<byte[]> getProductThumbnail(@PathVariable long businessId,
                                                  @PathVariable String productId,
                                                  @PathVariable long imageId) {
        ProductImageService.ImageTypeAndData imageData = productImageService.getProductImage(businessId, productId, imageId, true);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(imageData.getContentType()));
        return new ResponseEntity<>(imageData.getData(), headers, HttpStatus.OK);
    }

    /**
     * This endpoint allows deletion of productImage database objects and their
     * related image files.
     * @param businessId The ID of the business that owns the given product and product image.
     * @param productId The ID of the product that owns the given product image.
     * @param imageId The ID of the product image to delete.
     * @return A response entity confirming that the process was successful.
     */
    @DeleteMapping(path = "/businesses/{businessId}/products/{productId}/images/{imageId}")
    public ResponseEntity<Object> deleteProductImage(@PathVariable long businessId,
                                                     @PathVariable String productId,
                                                     @PathVariable long imageId) {
        productImageService.deleteProductImage(businessId, productId, imageId);
        return new ResponseEntity<>("Image deleted successfully", HttpStatus.OK);
    }
}
