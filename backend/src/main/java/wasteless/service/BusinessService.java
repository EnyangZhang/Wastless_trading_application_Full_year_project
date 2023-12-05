package wasteless.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;
import wasteless.controller.jsonobjects.SaleItemJson;
import wasteless.controller.jsonobjects.SalesReportJson;
import wasteless.controller.jsonobjects.SalesReportSection;
import wasteless.exception.BadRequestException;
import wasteless.exception.ForbiddenException;
import wasteless.exception.InsufficientInventoryException;
import wasteless.model.*;
import wasteless.repository.BusinessRepository;
import wasteless.repository.InventoryItemRepository;
import wasteless.repository.ProductRepository;
import wasteless.repository.SaleItemRepository;
import wasteless.security.AuthUtil;
import wasteless.service.searching_service.SearchParamsParser;
import wasteless.service.searching_service.SearchToken;
import wasteless.service.searching_service.SearchingService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.NoSuchElementException;

@Service
public class BusinessService {

  private final UserService userService;

  private final ProductRepository productRepository;

  private final BusinessRepository businessRepository;

  private final InventoryItemRepository inventoryItemRepository;

  private final SaleItemRepository saleItemRepository;

  private final AuthUtil authUtil;

  private final SearchingService businessSearchService;

  private final NotificationService notificationService;

  private static final String GLOBAL_ADMIN = "global_admin";

  private static final String DEFAULT_GLOBAL_ADMIN = "default_global_admin";

  private static final String USER_NOT_ADMIN = "User is not a business administrator or global application administrator";

  @Autowired
  public BusinessService(
          ProductRepository productRepository,
          BusinessRepository businessRepository,
          InventoryItemRepository inventoryItemRepository,
          SaleItemRepository saleItemRepository,
          AuthUtil authUtil,
          UserService userService,
          SearchingService businessSearchService,
          NotificationService notificationService) {
    this.productRepository = productRepository;
    this.businessRepository = businessRepository;
    this.inventoryItemRepository = inventoryItemRepository;
    this.saleItemRepository = saleItemRepository;
    this.authUtil = authUtil;
    this.userService = userService;
    this.businessSearchService = businessSearchService;
    this.notificationService = notificationService;
  }

  /**
   * Processes and saves the business passed in argument into the database. Requires an
   * authenticated user to be 16 or older. If user is valid, adds business to user's list of
   * administrated businesses, then adds the user to business' list of administrators. Business'
   * primary admin Id is set to be the currently authenticated user's Id; being the user that
   * created the business.
   *
   * @param business Instance of business to be saved into the database.
   * @return Instance of business that has been saved into the database, if successful.
   */
  public Business addBusiness(Business business){

    ArrayList<User> admins = new ArrayList<>();

    // Get current user from authentication
    User currentUser = authUtil.getCurrentUser();
    userIsUnderage(currentUser.getDateOfBirth());

    // Add business to businesses authenticated
    currentUser.addBusinessAdministered(business);
    admins.add(currentUser);

    business.setPrimaryAdminId(currentUser.getUserId());
    business.setRegistrationDate(LocalDate.now());
    business.setAdmins(admins);

    return businessRepository.save(business);
  }

  /**
   * Checks that the user is over 16 before they can create a new business
   *
   * @param dateOfBirth date of birth of user
   * @throws BadRequestException Generated if the user is not at least 16
   */
  public void userIsUnderage(LocalDate dateOfBirth) throws BadRequestException {
    LocalDate currentDate = LocalDate.now();
    if (dateOfBirth.isAfter(currentDate.minusYears(16))) {
      throw new BadRequestException(
          "You must be at least 16 years of age to create a new business");
    }
  }

  /**
   * Gets a business from the database. Requires a user to be logged in.
   *
   * @param id Id of user to find
   * @return Instance of business found.
   */
  public Business getBusiness(long id) throws NoSuchElementException {
    // Get user from authentication
    authUtil.getCurrentUser();
    return getBusinessById(id);
  }

  /**
   * Gets business from repository. If no business is found, throws NoSuchElementException.
   *
   * @param id Id of business to search database for.
   * @return Instance of Business, if one is found.
   */
  private Business getBusinessById(long id) throws NoSuchElementException {
    return businessRepository.findByBusinessId(id).orElseThrow();
  }

  /**
   * Gets list of products from an existing business' catalogue. Requires an authenticated user to
   * be a Global Application Admin, or otherwise an administrator of an existing business.
   *
   * @param id Id of the business to retrieve catalogue from.
   * @return List of products in business' catalogue, if retrieved.
   */
  public List<Product> getProducts(long id) {

    // Get current user from authentication
    User currentUser = authUtil.getCurrentUser();

    // Finds specified business if one exists
    Business business = getBusinessById(id);

    requireAdminOrGAA(currentUser, business);
    return business.getProductCatalogue();
  }

  /**
   * Requires an authorized user to be an Global Application Admin, or otherwise an administrator of
   * an existing business. Creates a new instance of the product in argument and saves it into the
   * database.
   *
   * @param businessId Id of the business to save product to.
   * @param product Instance of product to add to catalogue.
   */
  public String addProduct(long businessId, Product product) {

    // Get current user from authentication
    User currentUser = authUtil.getCurrentUser();

    // Finds specified business if one exists
    Business business = getBusinessById(businessId);

    List<Product> productList =
        productRepository.findByBusinessAndProductId(business, product.getProductId());
    if (!productList.isEmpty()) {
      throw new BadRequestException("Product ID already used in this business's catalog.");
    }

    requireAdminOrGAA(currentUser, business);

    Product newProduct =
        new Product(
            business,
            product.getProductId(),
            product.getName(),
            product.getManufacturer(),
            product.getDescription(),
            product.getRecommendedRetailPrice(),
            LocalDate.now());
    business.addProduct(newProduct);
    productRepository.save(newProduct);
    return newProduct.getProductId();
  }

  /**
   * If current user is NOT a global application admin AND user is not an admin of the business,
   * throw ForbiddenException
   *
   * @param currentUser Instance of current user.
   * @param business Instance of business to check user administrates.
   */
  public void requireAdminOrGAA(User currentUser, Business business) {

    Long userId = currentUser.getUserId();
    Long primaryAdminId = business.getPrimaryAdminId();

    // Gets a list of user Ids in business' administrators
    List<Long> administrators = new ArrayList<>();
    for (User admin : business.getAdmins()) {
      administrators.add(admin.getUserId());
    }

    Boolean userIsGAA =
        currentUser.getRole().equals(GLOBAL_ADMIN)
            || currentUser.getRole().equals(DEFAULT_GLOBAL_ADMIN);
    Boolean userIsBusinessAdmin =
        administrators.contains(userId)
            || userId.equals(primaryAdminId);

    // if the user is NOT a GAA OR Business Admin
    if (!(userIsGAA || userIsBusinessAdmin)) {
      throw new ForbiddenException(
          USER_NOT_ADMIN);
    }
  }

  public void checkProductCode(Long id, String productCode) {
    authUtil.getCurrentUser();
    Business business = getBusinessById(id);
    List<Product> foundProduct =
        productRepository.findByBusinessAndProductId(business, productCode);
    if (!foundProduct.isEmpty()) {
      throw new ForbiddenException("Product code is invalid");
    }
  }

  /**
   * Requires an authorized user to be an Global Application Admin, or otherwise an administrator of
   * an existing business. Modify an exists product catalogue of the product in argument and saves
   * it into the database.
   *
   * @param businessId Id of the business to save product to.
   * @param catalogueId Id of the catalogue to save to business.
   * @param catalogueReq The request body of from the front end that contains information of
   *     modification to exists catalogue.
   */
  public void  modifyProduct(long businessId, String catalogueId, Product catalogueReq) {
    Business business = getBusinessById(businessId);

    User user = authUtil.getCurrentUser();
    requireAdminOrGAA(user, business);

    List<Product> cataloguesWithOldId =
        productRepository.findByBusinessAndProductId(business, catalogueId);
    List<Product> cataloguesWithNewProductCode =
        productRepository.findByBusinessAndProductId(business, catalogueReq.getProductId());

    if (cataloguesWithOldId.isEmpty()) {
      throw new BadRequestException("Product not found.");
    }
    if (catalogueReq.getName().isEmpty()) {
      throw new BadRequestException("Name cannot be empty.");
    }
    if (catalogueReq.getProductId().isEmpty()) {
      throw new BadRequestException("Product code cannot be empty.");
    }

    Product oldCatalogue = cataloguesWithOldId.get(0);
    if (cataloguesWithNewProductCode.size() == 1
        && !oldCatalogue.getProductId().equals(catalogueReq.getProductId())) {
      throw new BadRequestException("New product code already exists.");
    }

    oldCatalogue.setProductId(catalogueReq.getProductId());
    oldCatalogue.setName(catalogueReq.getName());
    oldCatalogue.setManufacturer(catalogueReq.getManufacturer());
    oldCatalogue.setDescription(catalogueReq.getDescription());
    oldCatalogue.setRecommendedRetailPrice(catalogueReq.getRecommendedRetailPrice());
    productRepository.save(oldCatalogue);
  }

  /**
   * Transfers a business's primary administrator to a specified user.
   *
   * <p>Requires a user to be logged in as the business's administrator, or a global application
   * administrator. A primary owner cannot transfer the status to themselves.
   *
   * @param id The id of the business the user is being granted primary ownership to.
   * @param userId The id of the user being granted primary ownership to the specified business.
   */
  public void transferOwnership(Long id, Long userId) {
    User currentUser = authUtil.getCurrentUser();

    User targetUser = userService.getUserById(userId);
    Business targetBusiness = getBusinessById(id);

    requireAdminOrGAA(currentUser, targetBusiness);

    List<Long> adminIds = new ArrayList<>();
    for (User admin : targetBusiness.getAdmins()) {
      adminIds.add(admin.getUserId());
    }

    if (adminIds.contains(targetUser.getUserId())
        || targetUser.getUserId() == targetBusiness.getPrimaryAdminId()) {
      throw new ForbiddenException("Target user is already an admin of the business");
    }

    targetBusiness.setPrimaryAdminId(targetUser.getUserId());

    businessRepository.save(targetBusiness);
  }

  /**
   * Makes a user an admin of a business
   *
   * <p>if the individual is added as an administrator successfully, the a HTTP 200 response is
   * returned.
   *
   * @param id The ID of the business the user is being requested to be made an admin for
   * @param userId The ID of the user being requested to be made an admin
   */
  public void addToAdmins(Long id, Long userId) {
    User currentUser = authUtil.getCurrentUser();

    Business targetBusiness = getBusinessById(id);
    User targetUser = userService.getUserById(userId);

    requireAdminOrGAA(currentUser, targetBusiness);
    List<Business> businesses = businessRepository.findAllByPrimaryAdminId(currentUser.getUserId());
    String role = currentUser.getRole();
    if (!(businesses.contains(targetBusiness)
        || role.equals(GLOBAL_ADMIN)
        || role.equals(DEFAULT_GLOBAL_ADMIN))) {
      throw new ForbiddenException(
          USER_NOT_ADMIN);
    }

    if (targetUser.getBusinessesAdministered().contains(targetBusiness)) {
      throw new BadRequestException("User is already an admin of the target business");
    }

    targetUser.addBusinessAdministered(targetBusiness);
    businessRepository.save(targetBusiness);
  }

  /**
   * Removes an existing user from a specified business's list of administrators. The user must be
   * an administrator of the specified business in order to be removed. Requires a User to be logged
   * in as an admin of the specified business, or a global application admin.
   *
   * @param id The id of the target business to remove an administrator from.
   * @param userId The id of the user to be removed from the particular business's list of
   *     administrators.
   */
  public void removeFromAdmins(Long id, Long userId) {
    User currentUser = authUtil.getCurrentUser();

    Business targetBusiness = getBusinessById(id);
    User targetUser = userService.getUserById(userId);

    List<Business> businesses = businessRepository.findAllByPrimaryAdminId(currentUser.getUserId());
    String role = currentUser.getRole();
    if (!(businesses.contains(targetBusiness)
        || role.equals(GLOBAL_ADMIN)
        || role.equals(DEFAULT_GLOBAL_ADMIN))) {
      throw new ForbiddenException(
          USER_NOT_ADMIN);
    }

    if (!targetUser.getBusinessesAdministered().contains(targetBusiness)
        || targetUser.getUserId() == targetBusiness.getPrimaryAdminId()) {
      throw new BadRequestException("Target user is not an administrator of the business");
    }

    targetUser.getBusinessesAdministered().remove(targetBusiness);
    businessRepository.save(targetBusiness);
  }

  /**
   * Requires an authorized user to be an Global Application Admin, or otherwise an administrator of
   * an existing business. Creates a new instance of the inventory item in argument and saves it
   * into the database.
   *
   * @param id Id of the business to save item to.
   * @param item Instance of item to add to inventory.
   */
  public InventoryItem addInventoryItem(Long id, InventoryItem item) {
    // Get current user from authentication
    User currentUser = authUtil.getCurrentUser();

    // Finds specified business if one exists
    Business business = getBusinessById(id);

    requireAdminOrGAA(currentUser, business);

    // Checks if inventory corresponds to a product
    List<Product> products =
        productRepository.findByBusinessAndProductId(business, item.getProductId());
    if (products.isEmpty()) {
      throw new BadRequestException(
          "Inventory item's product code does not exist in the specified business");
    }

    // Set the business and product foreign keys of inventory item
    item.setBusiness(business);
    item.setProduct(products.get(0));
    item.setCreated(LocalDateTime.now());
    return inventoryItemRepository.save(item);
  }

  /**
   * Method to retrieve the contents of a business's product inventory.
   *
   * @param id id of business to retrieve inventory from.
   * @return A List containing InventoryItem instances.
   */
  public List<InventoryItem> retrieveInventory(long id) {
    User currentUser = authUtil.getCurrentUser();
    Business business = getBusinessById(id);

    requireAdminOrGAA(currentUser, business);

    return business.getInventoryItems();
  }

  /**
   * Validation for saleItem. if the inventoryItemId are empty, an 400 BadRequest exception will
   * raise. If the quantity of the saleItem will be over the quantity of corresponding inventoryItem
   * after adding, an 400 BadRequest exception will raise.
   *
   * @param saleItemJson The saleItem POJO that needs to be validated.
   */
  public boolean saleItemValidation(SaleItemJson saleItemJson) {
    if (saleItemJson.getQuantity() < 0) {
      throw new BadRequestException("Quantity cannot be less than zero");
    }
    if (saleItemJson.getPrice() < 0) {
      throw new BadRequestException("Price cannot be less than zero");
    }
    if (saleItemJson.getMoreInfo().length() > 255) {
      throw new BadRequestException("More info cannot be longer than 255 characters");
    }

    long inventoryItemId = saleItemJson.getInventoryItemId();

    InventoryItem inventoryItem = inventoryItemRepository.findByInventoryItemId(inventoryItemId).orElseThrow(
            () -> new BadRequestException("Inventory Item with this ID does not exist"));

    List<SaleItem> saleItems = saleItemRepository.findByInventoryItem(inventoryItemId);
    int totalQuantity = 0;

    for (SaleItem item : saleItems) {
      if (!item.isSold()) {
        totalQuantity += item.getQuantity();
      }
    }

    if ((totalQuantity + saleItemJson.getQuantity()) > inventoryItem.getQuantity()) {
      throw new BadRequestException("Quantity exceeded inventory item quantity.");
    }

    return true;
  }

  /**
   * Requires an authorized user to be an Global Application Admin, or otherwise an administrator of
   * an existing business. Creates a new instance of the saleItem in argument and saves it into the
   * database.
   *
   * @param businessId Id of the business to save product to.
   * @param saleItemJson Instance of saleItem to add database.
   */
  public Long addSaleItem(long businessId, SaleItemJson saleItemJson) {
    User currentUser = authUtil.getCurrentUser();
    Business business = getBusinessById(businessId);
    requireAdminOrGAA(currentUser, business);

    saleItemValidation(saleItemJson);
    SaleItem saleItem = convertJsonToSaleItem(saleItemJson);

    SaleItem addedSaleItem = saleItemRepository.save(saleItem);
    return addedSaleItem.getSaleItemId();
  }

  /**
   * Converts request body POJO to a SaleItem object, and sets date created to now.
   * Preconditions: the card POJO contains all valid data
   * @param saleItemJson POJO request body
   * @return a SaleItem object
   */
  public SaleItem convertJsonToSaleItem(SaleItemJson saleItemJson) {
    InventoryItem inventoryItem = inventoryItemRepository.findByInventoryItemId(saleItemJson.getInventoryItemId())
            .orElse(null);
    SaleItem saleItem = new SaleItem();
    saleItem.setInventoryItem(inventoryItem);
    saleItem.setCreated(LocalDateTime.now());
    saleItem.setCloses(saleItemJson.getCloses());
    saleItem.setMoreInfo(saleItemJson.getMoreInfo());
    saleItem.setPrice(saleItemJson.getPrice());
    saleItem.setQuantity(saleItemJson.getQuantity());

    return saleItem;
  }

  /**
   * Method to retrieve the sale listings for a business.
   * If isSold is true returns all sold listings for the business.
   * If isSold is false returns all unsold listings for the business.
   *
   * @param businessId id of business to retrieve sale listings from.
   * @return A List containing SaleListing instances.
   */
  public List<SaleItem> retrieveSaleItems(long businessId, boolean isSold) {
    if (isSold) {
      return saleItemRepository.findSoldListingsByBusiness(businessId);
    } else {
      return saleItemRepository.findSaleListingsByBusiness(businessId);
    }
  }

  /**
   * Method to retrieve a List of businesses.
   *
   * @param searchQuery Business name provided by user.
   * @return A List containing businesses.
   */
  public SearchingService.SearchResult searchBusinesses(String searchQuery){
    List<SearchToken> searchParam = SearchParamsParser.parse(searchQuery);
    return businessSearchService.find(searchParam, 0,  10, "default", "asc");
  }

  /**
   * Method for when sale item for a business when it has been purchased.
   * Requires a user to be logged in
   *
   * @param saleItemId the id of the sale item being removed
   */
  public SaleItem purchaseSaleItem(Long businessId, Long saleItemId) {
    // Get user from authentication
    User purchaser = authUtil.getCurrentUser();

    Business targetBusiness = getBusiness(businessId);
    SaleItem targetSaleItem = getSaleItemById(saleItemId);

    if (targetBusiness.getBusinessId() != targetSaleItem.getInventoryItem().getBusiness().getBusinessId()) {
      throw new BadRequestException("Sale listing does not belong to the provided business.");
    }

    Integer quantityPurchased = targetSaleItem.getQuantity();
    Long inventoryItemId = targetSaleItem.getInventoryItem().getInventoryItemId();

    if (targetSaleItem.isSold()) {
      throw new BadRequestException("Cannot purchase an already sold Sale Listing");
    } else {
      targetSaleItem.setSold(true);
      targetSaleItem.setPurchased(LocalDateTime.now());
      targetSaleItem.setPurchaser(purchaser);
    }
    try {
      decrementInventoryItemQuantity(inventoryItemId, quantityPurchased);
    } catch (InsufficientInventoryException e) {
      throw new BadRequestException("Insufficient inventory to make purchase.");
    }

    SaleItem purchasedListing = saleItemRepository.save(targetSaleItem);
    notificationService.createPurchaseNotification(purchasedListing);
    return purchasedListing;
  }

  /**
   * Method to decrement the quantity of a inventory item when the corresponding sale item has been purchased.
   * If the quantity of the inventory item is zero then it is removed from
   * @param inventoryItemId the id of the inventory item that the quantity is being decremented
   * @param quantityRemoved the quantity that has been removed via purchasing of the sale listing
   */
  public void decrementInventoryItemQuantity(Long inventoryItemId, Integer quantityRemoved) {
    InventoryItem targetInventoryItem = getInventoryItemById(inventoryItemId);

    Integer inventoryItemQuantity = targetInventoryItem.getQuantity();
    inventoryItemQuantity = inventoryItemQuantity - quantityRemoved;

    if (inventoryItemQuantity < 0) {
      throw new InsufficientInventoryException("Cannot decrement inventory below zero.");
    } else {
      targetInventoryItem.setQuantity(inventoryItemQuantity);
      inventoryItemRepository.save(targetInventoryItem);
    }
  }


  /**
   * Sends a notification to all users who have liked a sale item that has been purchased by another user.
   * This method should be called after the item's purchaser has been set.
   * @param saleItemId the ID of the sale item that has been purchased.
   */
  public void sendPurchaseNotificationToOtherUsers(long saleItemId) {
    SaleItem saleItem =  saleItemRepository.findById(saleItemId).orElseThrow(() -> new NotAcceptableStatusException("No sale item with ID " + saleItemId + " found"));
    Set<User> usersWhoLikedSaleItem = saleItem.getLikedByUsers();
    for (User user : usersWhoLikedSaleItem) {
      // Only send notification if the user is not the purchaser.
      if (user.getUserId() != saleItem.getPurchaser().getUserId()) {
        notificationService.createLikedItemPurchaseNotification(saleItem, user);
      }
    }
  }


  /**
   * This function will return a POJO containing information required for a sales report.
   * Will throw a 403 if the current user is not a GAA or admin of the business.
   * @param business the business the report is for
   * @param periodStart the start of the period the report is for
   * @param periodEnd the end of the period the report is for
   * @param granularity either TOTAL or MONTHLY. Determines how the data is summarised.
   * @return a POJO containing data for a sales report.
   */
  public SalesReportJson getSalesReport(Business business, LocalDate periodStart,
                                        LocalDate periodEnd, String granularity) {
    // Throw 403 if user is not a GAA or business admin
    requireAdminOrGAA(authUtil.getCurrentUser(), business);

    // Get the businesses sold sales listings in the given period.
    List<SaleItem> soldListings = retrieveSaleItems(business.getBusinessId(), true);

    List<SalesReportSection> sections;
    if (granularity.equals("TOTAL")) {
      sections = getSectionsTotalGranularity(soldListings, periodStart, periodEnd);
    } else if (granularity.equals("MONTHLY")) {
      sections = getSectionsMonthlyGranularity(soldListings, periodStart, periodEnd);
    } else {
      throw new BadRequestException("granularity must be TOTAL or MONTHLY");
    }


    return new SalesReportJson(periodStart, periodEnd, sections, business);
  }


  /**
   * Returns a list with length 1 of SalesReportSections, with all data for the period in one section
   * @param soldListings a list of the businesses sold sale listings
   * @param periodStart the start of the report period
   * @param periodEnd the end of the report period
   * @return a list of SalesReportSections
   */
  public List<SalesReportSection> getSectionsTotalGranularity(List<SaleItem> soldListings,
                                                               LocalDate periodStart,
                                                               LocalDate periodEnd) {
    ArrayList<SalesReportSection> sections = new ArrayList<>();
    int numberOfSales = 0;
    double valueOfSales = 0;
    for (SaleItem listing : soldListings) {
      // If the listing was purchased between periodStart and periodEnd, increase numberOfSales and valueOfSales
      if (listing.getPurchased().toLocalDate().compareTo(periodStart) >= 0
              && listing.getPurchased().toLocalDate().compareTo(periodEnd) <= 0) {
        numberOfSales++;
        valueOfSales += listing.getPrice();
      }
    }
    SalesReportSection section = new SalesReportSection("TOTAL", periodStart, periodEnd, numberOfSales, valueOfSales);
    sections.add(section);
    return sections;
  }

  /**
   * Returns a list of SalesReportSections, with each section correlating to a month of sales.
   * The sections are divided into months of the year
   * @param soldListings a list of the businesses sold sale listings
   * @param periodStart the start of the report period
   * @param periodEnd the end of the report period
   * @return a list of SalesReportSections
   */
  public List<SalesReportSection> getSectionsMonthlyGranularity(List<SaleItem> soldListings,
                                                              LocalDate periodStart,
                                                              LocalDate periodEnd) {
    ArrayList<SalesReportSection> sections = new ArrayList<>();
    LocalDate granularityStart;
    LocalDate granularityEnd;
    // Loop through the set period monthly to create sections for each month
    for (LocalDate date = periodStart; date.isBefore(periodEnd.plusMonths(1)); date = date.plusMonths(1)) {
      int numberOfSales = 0;
      double valueOfSales = 0;
      String granularName = date.getMonth().getValue() + "/" + date.getYear();

      if (date == periodStart) {
        // On the first run through the loop the start of the month section will be the periodStart
        granularityStart= periodStart;
      } else {
        // On subsequent runs through the loop the start of the section will be the first day of the month
        granularityStart = date.withDayOfMonth(1);
      }
      granularityEnd = date.withDayOfMonth(date.lengthOfMonth());
      if (granularityEnd.isAfter(periodEnd)) {
        // When the periodEnd is reached the end of the section will be the periodEnd
        granularityEnd = periodEnd;
      }

      for (SaleItem listing : soldListings) {
        // If the listing was purchased between granularityStart and granularityEnd, increase numberOfSales and valueOfSales
        if (listing.getPurchased().toLocalDate().compareTo(granularityStart) >= 0
                && listing.getPurchased().toLocalDate().compareTo(granularityEnd) <= 0) {
          numberOfSales++;
          valueOfSales += listing.getPrice();
        }
      }

      SalesReportSection section = new SalesReportSection(granularName, granularityStart, granularityEnd, numberOfSales, valueOfSales);
      if (!granularityStart.isAfter(periodEnd)) {
        sections.add(section);
      }
    }
    return sections;
  }

  /**
   * Gets a sale item from repository. If no sale item is found, throws NoSuchElementException.
   * @param id Id of the sale item to search the database for.
   * @return Instance of SaleItem, if one is found.
   */
  public SaleItem getSaleItemById(long id) {
    return saleItemRepository.findById(id).orElseThrow();
  }

  /**
   * Gets an inventory item from repository. If no inventory item is found, throws NoSuchElementException.
   * @param id Id of the inventory item to search the database for.
   * @return Instance of InventoryItem, if one is found.
   */
  private InventoryItem getInventoryItemById(long id) {
    return inventoryItemRepository.findByInventoryItemId(id).orElseThrow();
  }

  /**
   * This function LIKES a sale item.
   * @param businessId The business ID owning the sale item to like.
   * @param saleItemId The sale item ID of the sale item to like.
   */
  public void likeSaleItem(long businessId, long saleItemId) {
    likeSaleItem(businessId, saleItemId, false);
  }

  /**
   * This function UNLIKES a sale item.
   * @param businessId The business ID owning the sale item to like.
   * @param saleItemId The sale item ID of the sale item to like.
   */
  public void unlikeSaleItem(long businessId, long saleItemId) {
    likeSaleItem(businessId, saleItemId, true);
  }

  /**
   * This function is to like and unlike sale items.
   * The user it uses to like the sale item is the currently logged in user.
   * @param businessId The ID of the business that owns the sale item.
   * @param saleItemId The ID of the sale item to like or unlike.
   * @param unlike Boolean to indicate whether to unlike the sale item.
   */
  private void likeSaleItem(long businessId, long saleItemId, boolean unlike) {
    User currentUser = authUtil.getCurrentUser();
    Business business = businessRepository.findByBusinessId(businessId).orElse(null);
    SaleItem saleItem = saleItemRepository.findById(saleItemId).orElse(null);

    if (business == null) {
      throw new NotAcceptableStatusException("Business ID does not exist.");
    }
    if (saleItem == null) {
      throw new NotAcceptableStatusException("SaleItem ID does not exist.");
    }
    if (saleItem.getBusinessId() != businessId) {
      throw new NotAcceptableStatusException("SaleItem not related to business.");
    }

    Set<User> likedUsers = saleItem.getLikedByUsers();
    if (unlike) {
      boolean change = likedUsers.remove(currentUser);
      if (change) notificationService.createUnlikedAnItemNotification(saleItem, currentUser);
    } else {
      boolean change = likedUsers.add(currentUser);
      if (change) notificationService.createLikedAnItemNotification(saleItem, currentUser);
    }

    saleItem.setLikedByUsers(likedUsers);
    saleItemRepository.save(saleItem);
  }

}

