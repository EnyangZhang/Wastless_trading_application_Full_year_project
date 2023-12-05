package wasteless.service;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.NotAcceptableStatusException;
import wasteless.exception.BadRequestException;
import wasteless.exception.ForbiddenException;
import wasteless.exception.InternalServerError;
import wasteless.model.Business;
import wasteless.model.Product;
import wasteless.model.ProductImage;
import wasteless.repository.BusinessRepository;
import wasteless.repository.ProductImageRepository;
import wasteless.repository.ProductRepository;
import wasteless.security.AuthUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class ProductImageService {
    private final List<String> allowedTypes = Arrays.asList(
            MediaType.IMAGE_PNG_VALUE,
            MediaType.IMAGE_JPEG_VALUE,
            MediaType.IMAGE_GIF_VALUE
    );

    @Value("${image.storagepath}")
    private String imagePath;

    // The relative path from imagePath.
    @Value("${image.thumbnail-rel-path}")
    private String thumbnailRelPath;

    // Maximum image size in megabytes
    @Value("${image.maxsize}")
    private Double maxImageSize;

    private final ProductImageRepository productImageRepository;
    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;

    private final AuthUtil authUtil;

    Logger logger = LoggerFactory.getLogger(ProductImageService.class);

    @Autowired
    ProductImageService(ProductImageRepository productImageRespository,
                        ProductRepository productRepository,
                        BusinessRepository businessRepository,
                        AuthUtil authUtil) {
        this.productImageRepository = productImageRespository;
        this.productRepository = productRepository;
        this.businessRepository = businessRepository;
        this.authUtil = authUtil;
    }

    /**
     * Adds a product image to the database.
     * @param businessId The id of the business the target product is from.
     * @param productId The id of the product to add the image to.
     * @param imageInput The image to add to the product.
     * @return Returns the ProductImage that has been created
     */
    public ProductImage createProductImage(long businessId, String productId, MultipartFile imageInput) {
        String loggerMessage = String.format("Creating image for Business ID: %s and Product ID: %s", businessId, productId);
        logger.info(loggerMessage);
        if (!allowedTypes.contains(imageInput.getContentType())) {
            throw new BadRequestException("\"" + Objects.requireNonNull(imageInput.getContentType()).split("/")[1] + "\" is not a supported media type.");
        }

        createDirectories();

        Business business = isAuthorizedForBusiness(businessId);

        Product product;
        try {
            product = productRepository.findByBusinessAndProductId(business, productId).get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new NotAcceptableStatusException("The product ID does not exist.");
        }

        // Creating buffered image from input
        byte[] bytes = readMultipartFileToBytes(imageInput);
        BufferedImage buffImage = bufferedImageFromBytes(bytes);

        File imageFile = findFreeFilename(false, imageInput);
        File thumbnailFile = findFreeFilename(true, imageInput);

        ProductImage newProductImage = createProductImageWithThumbnail(product, buffImage, imageFile, thumbnailFile);

        if (product.getPrimaryProductImage() == null) {
            newProductImage.setPrimary(true);
        }

        // Create database record.
        return productImageRepository.save(newProductImage);
    }

    /**
     * Gets image byte data for a ProductImage and the type of data.
     * @param businessId The business that owns the image and corresponding product.
     * @param productId The product that owns the image.
     * @param imageId The image to get byte data for.
     * @param thumbnail Whether or not the image returned should be the image or thumbnail.
     * @return Class containing the content type and a byte array of the product image.
     */
    public ImageTypeAndData getProductImage(long businessId, String productId, long imageId, boolean thumbnail) {
        Business business = businessRepository.findById(businessId).orElse(null);
        if (business == null) throw new NotAcceptableStatusException("The business ID does not exist.");

        Product product = getProductFromParams(business, productId);

        ProductImage productImage = productImageRepository.findById(imageId).orElse(null);
        if (productImage == null) throw new NotAcceptableStatusException("The image ID does not exist.");
        if (!product.containsProductImage(productImage)) throw new NotAcceptableStatusException("The product does not have this image.");

        Path filePath = Path.of(thumbnail ? productImage.getThumbnailFilename() : productImage.getFilename());
        try {
            String type = Files.probeContentType(filePath);
            byte[] imageBytes = Files.readAllBytes(filePath);
            return new ImageTypeAndData(type, imageBytes);
        } catch (IOException e) {
            throw new InternalServerError("Could not load file.");
        }
    }

    /**
     * Sets a ProductImage as the primary image for a product.
     * @param businessId The business that owns the product.
     * @param productId The product that owns the target image.
     * @param imageId The image to set as primary.
     */
    public void setAsPrimary(long businessId, String productId, long imageId) {
        Business business = isAuthorizedForBusiness(businessId);
        Product product = getProductFromParams(business, productId);
        ProductImage productImage = productImageRepository.findById(imageId).orElse(null);
        if (productImage == null) throw new NotAcceptableStatusException("The image ID does not exist.");

        // Check if that product contains the correct image.
        if (product.containsProductImage(productImage)) {
            // Set the current primary image as secondary.
            ProductImage currentPrimary = product.getPrimaryProductImage();
            if (currentPrimary != null) {
                currentPrimary.setPrimary(false);
                productImageRepository.save(currentPrimary);
            }

            // Set the product image as primary.
            productImage.setPrimary(true);
            productImageRepository.save(productImage);
        } else {
            throw new NotAcceptableStatusException("The product does not have this image.");
        }
    }

    /**
     * Deletes a product from the database and removes files.
     * @param businessId Business that owns the product that the image corresponds to.
     * @param productId The product that owns the target image.
     * @param imageId The target image id.
     */
    public void deleteProductImage(long businessId, String productId, long imageId) {
        Business business = isAuthorizedForBusiness(businessId);
        Product product = getProductFromParams(business, productId);
        ProductImage productImage = productImageRepository.findById(imageId).orElse(null);

        if (productImage == null) {
            throw new NotAcceptableStatusException("Image ID does not exist.");
        }
        if (!product.containsProductImage(productImage)) {
            throw new NotAcceptableStatusException("Image not related to product.");
        }

        File imageFile = new File(productImage.getFilename());
        File thumbnailFile = new File(productImage.getThumbnailFilename());
        try {
            Files.delete(Path.of(imageFile.getPath()));
            Files.delete(Path.of(thumbnailFile.getPath()));
        } catch (IOException e) {
            throw new InternalServerError("Could not delete files.");
        }
        productImageRepository.delete(productImage);
    }

    /**
     * Returns business if user is authorized to administrate that business.
     * @param businessId The business id of the business in question
     * @return The business that corresponds to the businessId
     */
    private Business isAuthorizedForBusiness(long businessId) {
        // Checking parameters given are correct.
        Business business = businessRepository.findById(businessId).orElse(null);
        if (business == null) {throw new NotAcceptableStatusException("The business ID does not exist.");}

        // Checking if user has required permission
        if (!business.getAdmins().contains(authUtil.getCurrentUser()) && !authUtil.isCurrentUserGlobalAdmin()) {
            throw new ForbiddenException("You do not have permission to change this product.");
        }
        return business;
    }

    /**
     * Gets product from params if it exists, else it throws a BadRequestException.
     * @param business the business that owns the product
     * @param productId the product to retrieve
     * @return The product corresponding to the Product ID and business given
     */
    private Product getProductFromParams(Business business, String productId) {
        Product product;
        try {
            product = productRepository.findByBusinessAndProductId(business, productId).get(0);
        } catch (IndexOutOfBoundsException e) {
            throw new NotAcceptableStatusException("The product ID does not exist.");
        }
        return product;
    }

    /**
     * Finds a free filename to save the image to.
     * @param isThumbnail Used to set the location in which the function will look for
     *                    free file names. If set to true it will look in the
     *                    thumbnail directory.
     * @return A file object which contains the path to where a file can be created.
     * Note that this function will NOT create a new file.
     */
    private File findFreeFilename(boolean isThumbnail, MultipartFile inputFile) {
        String filename;
        File file;
        String fileExtension = Objects.requireNonNull(inputFile.getContentType()).split("/")[1];
        String uniqueImageName = UUID.randomUUID().toString();
        do {
            filename = (isThumbnail ? "thumbnail" : "") + uniqueImageName + "." + fileExtension;
            file = new File(imagePath + (isThumbnail ? thumbnailRelPath : ""), filename);
        } while (file.exists());

        return file;
    }

    /**
     * Creates directories for images if they do not already exist.
     */
    private void createDirectories() {
        try {
            Files.createDirectory(Path.of(imagePath));
            Files.createDirectory(Path.of(imagePath+thumbnailRelPath));
        } catch (IOException e) {
            logger.debug(String.format("Tried to create folders: %s", e.getMessage()));
        }
    }

    /**
     * Reads bytes from multipart file and checks if the
     * @param file A multipart file to be converted to bytes.
     * @return A byte array of the multipart file
     * @throws BadRequestException if the file is unreadable or the file exceeds the maximum allowed limit.
     */
    private byte[] readMultipartFileToBytes(MultipartFile file) {
        // Tries to read bytes throws exception if it cannot be read.
        byte[] bytes;
        try {bytes = file.getBytes();
        } catch(IOException e) {
            throw new BadRequestException("Unreadable file format");
        }

        // Throws exception if filesize is greater than that specified in the configuration.
        if (bytes.length > maxImageSize * Math.pow(10, 6)) {
            throw new BadRequestException("The file exceeds the limit of " + maxImageSize + "MB");
        }
        return bytes;
    }

    /**
     * Converts bytes to a buffered image.
     * @param bytes Byte array to be converted to a buffered image.
     * @return buffered image created from bytes.
     * @throws BadRequestException if the bytes cannot be converted to an image.
     */
    private BufferedImage bufferedImageFromBytes(byte[] bytes) {
        InputStream inputStream = new ByteArrayInputStream(bytes);
        BufferedImage buffImage;
        try {
            buffImage = ImageIO.read(inputStream);
            if (buffImage == null) throw new IOException();
        } catch (IOException e) {
            throw new BadRequestException("Invalid image format.");
        }
        return buffImage;
    }

    /**
     * Saves the image and thumbnail images, then creates a new ProductImage which
     * references the file paths for those images.
     * The thumbnail image is automatically generated from the input image.
     * @param product The product to attach the images to.
     * @param image The image.
     * @param imageFile The file location for saving the image file.
     * @param thumbnailFile The file location for saving the thumbnail file.
     * @return A new populated ProductImage
     * already exist in the file system.
     */
    private ProductImage createProductImageWithThumbnail(Product product, BufferedImage image, File imageFile, File thumbnailFile) {
        try {
            if (imageFile.createNewFile() && thumbnailFile.createNewFile()) {
                BufferedImage thumbnailImage = new BufferedImage(300, 300, image.getType());
                thumbnailImage.createGraphics().drawImage(image.getScaledInstance(300, 300, Image.SCALE_AREA_AVERAGING), 0, 0, null);
                return writeImage(image, imageFile, thumbnailImage, thumbnailFile, product);
            } else {
                logger.error("Tried to create file that already existed.");
                throw new InternalServerError("Files already exist.");
            }
        } catch (IOException e) {
            try {
                logger.error(String.format("Failed to write file: %s", e.getMessage()));
                Files.delete(Path.of(imageFile.getPath()));
                Files.delete(Path.of(imageFile.getPath()));
                throw new InternalServerError("Unable to save image.");
            } catch (IOException deleteError) {
                logger.error(String.format("Could not delete files after failed write: %s", deleteError.getMessage()));
                throw new InternalServerError("Unable to save image.");
            }
        }
    }

    /**
     * Write image function, attempts to write an image into file.
     * @param image BufferedImage image
     * @param imageFile Image file location for writing image file
     * @param thumbnailImage BufferedImage of thumbnail
     * @param thumbnailFile Thumbnail file location for writing image file.
     * @param product The product to attach the images to.
     * @return ProductImage instance of image file.
     */
    private ProductImage writeImage(BufferedImage image,
                                    File imageFile,
                                    BufferedImage thumbnailImage,
                                    File thumbnailFile,
                                    Product product) {
        try {
            ImageIO.write(image, "gif", imageFile);
            ImageIO.write(thumbnailImage, "gif", thumbnailFile);
            return new ProductImage(product, imageFile.getPath(), thumbnailFile.getPath());
        } catch (IOException e) {
            throw new BadRequestException("Unable to write image");
        }
    }

    /**
     * Container for storing a bytearray of an image and the content type.
     */
    @Getter
    @Setter
    public static class ImageTypeAndData {
        private final String contentType;
        private final byte[] data;
        public ImageTypeAndData(String contentType, byte[] data) {
            this.contentType = contentType;
            this.data = data;
        }
    }
}
