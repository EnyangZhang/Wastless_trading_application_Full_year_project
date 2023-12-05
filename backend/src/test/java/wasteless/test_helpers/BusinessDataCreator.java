/**
 * This class contains methods to create test data for various model classes. Essentially the methods are constructors,
 * but only the 'relevant' attributes are inputs (e.g. email and userId) and the rest are set to defaults.
 */

package wasteless.test_helpers;

import wasteless.controller.jsonobjects.SaleItemJson;
import wasteless.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BusinessDataCreator {
    /** Method to create a dummy business */
    public static Business createBusiness(long businessId) {
        User primaryAdmin = UserDataCreator.createUser();
        Business business = new Business();
        business.setBusinessId(businessId);
        business.setName("McRonalds");
        business.setDescription("Fast Food");
        business.setAddress(UserDataCreator.createAddress());
        business.setBusinessType("Accommodation and Food Services");
        business.setPrimaryAdminId(primaryAdmin.getUserId());
        business.setRegistrationDate(LocalDate.now());
        List<User> admins = new ArrayList<>();
        admins.add(primaryAdmin);
        business.setAdmins(admins);
        business.setProductCatalogue(new ArrayList<>());
        return business;
    }

    /** Method to create a dummy business */
    public static Business createBusiness(long businessId, Address address, User primaryAdmin) {
        Business business = new Business();
        business.setBusinessId(businessId);
        business.setName("McRonalds");
        business.setDescription("Fast Food");
        business.setAddress(address);
        business.setBusinessType("Accommodation and Food Services");
        business.setPrimaryAdminId(primaryAdmin.getUserId());
        List<User> admins = new ArrayList<>();
        admins.add(primaryAdmin);
        business.setAdmins(admins);
        business.setProductCatalogue(new ArrayList<>());
        return business;
    }

    /** Method to create a dummy business with a given list of users */
    public static Business createBusiness(long businessId, Address address, User primaryAdmin, List<User> admins) {
        Business business = new Business();
        business.setBusinessId(businessId);
        business.setName("McRonalds");
        business.setDescription("Fast Food");
        business.setAddress(address);
        business.setBusinessType("Accommodation and Food Services");
        business.setPrimaryAdminId(primaryAdmin.getUserId());
        business.setAdmins(admins);
        business.setProductCatalogue(new ArrayList<>());
        return business;
    }

    /** Method to create a dummy business with a given list of users */
    public static Business createBusiness(User primaryAdmin) {
        Business business = new Business();
        business.setName("McRonalds");
        business.setDescription("Fast Food");
        business.setAddress(UserDataCreator.createAddress());
        business.setBusinessType("Accommodation and Food Services");
        business.setPrimaryAdminId(primaryAdmin.getUserId());
        business.setProductCatalogue(new ArrayList<>());
        return business;
    }

    /** Method to create a dummy product */
    public static Product createProduct(String productId) {
        Address address = new Address();
        User admin = new User();
        return new Product(
                createBusiness(1L, address, admin),
                productId,
                "Watties Baked Beans - 420g can",
                "Watties",
                "Baked Beans as they should be",
                2.2,
                LocalDate.now());
    }

    /** Method to create a dummy product for a given business */
    public static Product createProduct(String productId, Business business) {
        return new Product(
                business,
                productId,
                "Watties Baked Beans - 420g can",
                "Watties",
                "Baked Beans as they should be",
                2.2,
                LocalDate.now());
    }

    /** Method to create a dummy inventory item with for a given product */
    public static InventoryItem createInventoryItem(long inventoryItemId) {
        Product product = createProduct("ABC123");
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setInventoryItemId(inventoryItemId);
        inventoryItem.setProduct(product);
        inventoryItem.setProductId(product.getProductId());
        inventoryItem.setBusiness(product.getBusiness());
        inventoryItem.setExpires(LocalDate.now().plusDays(10));
        return inventoryItem;
    }

    /** Method to create a dummy inventory item with for a given product */
    public static InventoryItem createInventoryItem(long inventoryItemId, Product product) {
        InventoryItem inventoryItem = new InventoryItem();
        inventoryItem.setInventoryItemId(inventoryItemId);
        inventoryItem.setProduct(product);
        inventoryItem.setProductId(product.getProductId());
        inventoryItem.setBusiness(product.getBusiness());
        inventoryItem.setExpires(LocalDate.now().plusDays(10));
        return inventoryItem;
    }

    /**
     * Method to create a dummy sale item
     * @param saleItemId ID of the sale item
     * @return a SaleItem object
     */
    public static SaleItem createSaleItem(long saleItemId) {
        InventoryItem inventoryItem = createInventoryItem(1L);
        SaleItem saleItem = new SaleItem(inventoryItem, 5, 4.0, "test", LocalDateTime.now(),
                LocalDateTime.now());
        saleItem.setSaleItemId(saleItemId);
        return saleItem;
    }

    /**
     * Method to create a dummy sale item
     * @param saleItemId ID of the sale item
     * @param inventoryItem the inventory item that the sale item corresponds to
     * @return a SaleItem object
     */
    public static SaleItem createSaleItem(long saleItemId, InventoryItem inventoryItem) {
        SaleItem saleItem = new SaleItem(inventoryItem, 5, 4.0, "test", LocalDateTime.now(),
                LocalDateTime.now());
        saleItem.setSaleItemId(saleItemId);
        return saleItem;
    }

    /**
     * Method to create a dummy sale item
     * @param inventoryItem the inventory item that the sale item corresponds to
     * @return a SaleItem object
     */
    public static SaleItemJson createSaleItemJson(InventoryItem inventoryItem) {
        return new SaleItemJson(inventoryItem.getInventoryItemId(), 5, 4.0,
                "test", LocalDateTime.now());
    }
}
