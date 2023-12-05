package wasteless.test_helpers;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.opentest4j.TestAbortedException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import wasteless.DGAAConfig;
import wasteless.model.Address;
import wasteless.model.Business;
import wasteless.model.Product;
import wasteless.model.ProductImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

/**
 * Class of static methods to generate data for unit tests.
 */
public class ProductImageTestHelper {

    private static final Logger logger = LogManager.getLogger(DGAAConfig.class.getName());

    public static final String testFileData = "data";
    public static final String testFileDataThumb = "dataThumb";

    /**
     * Creates an example business.
     * @return A new business object.
     */
    public static Business createBusiness() {
        return new Business(
                "name",
                "description",
                new Address("21", "street", "suburb", "city", "region", "country", "postcode"),
                "ds",
                0L,
                Collections.emptyList()
                );
    }

    /**
     * Retrieves test files from the file system as multipart file for testing.
     * @param path path of the file to retrieve
     * @param type type of file to retrieve.
     * @return A MockMultipartFile which can be passed to the function being tested.
     */
    @SneakyThrows
    public static MockMultipartFile getFile(String path, String type) {
        File file = ResourceUtils.getFile(path);
        InputStream stream = new FileInputStream(file);
        return new MockMultipartFile("filename", "filename", type, stream);
    }

    /**
     * Cleans the image directory so that each test has a fresh directory
     * and are not dependent on each other.
     */
    public static void cleanDirectory(String imagePath) {
        try {
            FileUtils.deleteDirectory(new File(imagePath));
        } catch (IOException ignored) {}
    }

    /**
     * Creates a new ProductImage to be used for testing for deleting an image
     * @return A new ProductImage to delete
     */
    public static ProductImage createTestProductImage(File imageFile, File thumbnailFile) {
        ProductImage productImage;
        try {
            if (imageFile.createNewFile() && thumbnailFile.createNewFile()) {
                productImage = new ProductImage(new Product(), imageFile.getPath(), thumbnailFile.getPath());
            } else {
                throw new IOException();
            }

            // Write test data.
            Files.write(Path.of(productImage.getFilename()), testFileData.getBytes());
            Files.write(Path.of(productImage.getThumbnailFilename()), testFileDataThumb.getBytes());
        } catch (IOException e) {
            throw new TestAbortedException("Could not create test data: " + e.getMessage());
        }
        return productImage;
    }

    /**
     * Creates necessary directories for testing.
     * @param imagePath path of image directory
     * @param thumbnailRelPath path of thumbnail directory.
     */
    public static void createDirectories(String imagePath, String thumbnailRelPath) {
        try {
            Files.createDirectory(Path.of(imagePath));
            Files.createDirectory(Path.of(imagePath+thumbnailRelPath));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
