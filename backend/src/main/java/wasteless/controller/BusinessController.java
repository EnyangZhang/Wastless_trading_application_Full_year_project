package wasteless.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import wasteless.controller.jsonobjects.SaleItemJson;
import wasteless.controller.jsonobjects.SalesReportJson;
import wasteless.model.Business;
import wasteless.model.InventoryItem;
import wasteless.model.Product;
import wasteless.model.SaleItem;
import wasteless.service.BusinessService;
import wasteless.service.searching_service.SearchingService;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class BusinessController {

  private final BusinessService businessService;

  @Autowired
  public BusinessController(BusinessService businessService) {
    this.businessService = businessService;
  }

  /**
   * This method adds a new business with the logged in user as its primary admin. Returns an HTTP
   * 401 Unauthorized response if the user is not logged in. Returns an HTTP 400 Bad Request
   * response if one or more of the business's attributes are invalid. Otherwise if successful,
   * returns an HTTP 200 response and the business object created.
   *
   * @param session A string representing whether or not the user is logged in
   * @param business The new business to be added
   * @return an HTTP response to the post request
   */
  @CrossOrigin(origins = "*", allowedHeaders = "*")
  @PostMapping(path = "/businesses")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Object> addBusiness(
      @CookieValue(value = "JSESSIONID", defaultValue = "unset") String session,
      @Valid @RequestBody Business business) {
    Business savedBusiness = businessService.addBusiness(business);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedBusiness.getBusinessId())
            .toUri();
    return ResponseEntity.created(location).body(business);
  }

  /**
   * This method retrieves the Business with the specified ID. If the user is not logged in, an HTTP
   * 401 Unauthorized response is returned. If a business with the specified ID cannot be found, an
   * HTTP 406 Not Acceptable request is returned. Otherwise if successful, returns an HTTP 200
   * response and the requested business object.
   *
   * @param id The ID of the business to be retrieved
   * @return A response to the get request
   */
  @GetMapping(path = "/businesses/{id}")
  public ResponseEntity<Object> retrieveBusiness(@PathVariable long id) {
    Business business = businessService.getBusiness(id);
    return new ResponseEntity<>(business, HttpStatus.OK);
  }

  /**
   * This method retrieves the Business Product with the specified Business ID. If the user is not
   * logged in, an HTTP 401 Unauthorized response is returned. If the user is not the administrator
   * or the global admin or the default global admin, an HTTP 403 Forbidden response is returned. If
   * a business with the specified ID cannot be found, an HTTP 406 Not Acceptable request is
   * returned. Otherwise if successful, returns an HTTP 200 response and the requested business
   * object.
   *
   * @param id The ID of the business to be retrieved
   * @return A response to the get request
   */
  @GetMapping(path = "/businesses/{id}/products")
  public ResponseEntity<Object> retrieveProduct(@PathVariable long id) {
    List<Product> products = businessService.getProducts(id);
    return new ResponseEntity<>(products, HttpStatus.OK);
  }

  /**
   * This method allows the user to add a new product to the business Product with the specified
   * businessId.
   *
   * @param id The ID of the business
   * @param product The parameters of the product to be added
   * @return A response to the POST request
   */
  @CrossOrigin(origins = "*", allowedHeaders = "*")
  @PostMapping(path = "/businesses/{id}/products")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Object> addProductToCatalogue(
      @PathVariable long id, @Valid @RequestBody Product product) {
    String productId = businessService.addProduct(id, product);

    return new ResponseEntity<>(productId, HttpStatus.CREATED);
  }

  /**
   * This method checks if the product code for a new product is valid
   *
   * @param businessId The business ID that the product will be added to
   * @param productCode The code we are checking if it exists
   * @return A response to the POST request
   */
  @CrossOrigin(origins = "*", allowedHeaders = "*")
  @PostMapping(path = "business/{businessId}/check-product-code/{productCode}")
  public ResponseEntity<Object> isCodeValid(
      @PathVariable long businessId, @PathVariable String productCode) {
    businessService.checkProductCode(businessId, productCode);
    return new ResponseEntity<>("Product Code is Valid", HttpStatus.OK);
  }

  /**
   * This method modify the Business Product with the specified Business ID and product ID. If the
   * user is not logged in, an HTTP 401 Unauthorized response is returned. If the user is not the
   * administrator or the global admin or the default global admin, an HTTP 403 Forbidden response
   * is returned. If a business with the specified ID cannot be found, an HTTP 406 Not Acceptable
   * request is returned. Otherwise if successful, returns an HTTP 200 response and the requested
   * business object.
   *
   * @param businessId The ID of the business to be retrieved.
   * @param productId The ID of the product.
   * @param product The request body.
   * @return A response to the get request.
   */
  @PutMapping(path = "/businesses/{businessId}/products/{productId}")
  public ResponseEntity<Object> modifyProduct(
      @PathVariable long businessId, @PathVariable String productId, @RequestBody Product product) {
    businessService.modifyProduct(businessId, productId, product);
    return new ResponseEntity<>("Product updated successfully", HttpStatus.OK);
  }

  /**
   * Transfer the current logged in user's primary ownership of the target business to the target
   * user.
   *
   * @param id Id of the target business to change primary ownership
   * @param userId Id of the target user to transfer primary ownership to
   * @return Response entity: 401 Unauthorized, 406 Not Acceptable if target business or user does
   *     not exist, 403 Forbidden if current user is not primary owner of business. 200 OK on
   *     successful transfer.
   */
  @PutMapping(path = "/businesses/{id}/transferOwnership/{userId}")
  public ResponseEntity<Object> transferPrimaryOwnership(
      @PathVariable long id, @PathVariable long userId) {

    // Check authorization token validity

    businessService.transferOwnership(id, userId);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * This method promotes the given user to an administrator of the specified business. If the user
   * is not logged in, an HTTP 401 Response is returned. If the target business does not exist, an
   * HTTP 406 Not Acceptable response is returned. If the user is not the business's administrator
   * or global admin, an HTTP 403 Forbidden response is returned. If the target user cannot be
   * promoted to an administrator (either because they already are or do not exist), an HTTP 400 Bad
   * Request response is returned. If the user is successfully promoted, an HTTP 200 OK Response is
   * returned.
   *
   * @param id The Business ID of which the target user will become an administrator
   * @param targetUser A JSON userId object given in the request body used to specify the target
   *     user
   * @return An HTTP Response to the given Put Request
   */
  @PutMapping(path = "/businesses/{id}/makeAdministrator")
  public ResponseEntity<Object> makeAdministrator(
      @PathVariable long id, @RequestBody JSONObject targetUser) {

    long userId = ((Number) targetUser.get("userId")).longValue();
    businessService.addToAdmins(id, userId);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * This method revokes a given target users business administrator status. If the user sending the
   * request is not logged in, an HTTP 401 Response is returned. If the target business does not
   * exist, an HTTP 406 Not Acceptable response is returned. If the requesting user is not the
   * business's administrator or global admin, an HTTP 403 Forbidden response is returned. If the
   * target user's administrator status cannot be removed (either because they were never an
   * administrator, they are the primary administrator or they do not exist), an HTTP 400 Bad
   * Request response is returned. If the target user's administrator status is successfully
   * removed,an HTTP 200 OK Response is returned.
   *
   * @param id The Business ID from which the target user will have its admin status removed
   * @param targetUser A JSON userId object given in the request body used to specify the target
   *     user
   * @return An HTTP Response to the given Put Request
   */
  @PutMapping(path = "/businesses/{id}/removeAdministrator")
  public ResponseEntity<Object> removeAdministrator(
      @PathVariable long id, @RequestBody JSONObject targetUser) {
    long userId = ((Number) targetUser.get("userId")).longValue();
    businessService.removeFromAdmins(id, userId);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * This method allows the user to add items to the inventory
   *
   * <p>If the inventory item is created successfully then a HTTP 201 CREATED Response is returned.
   * If there was some error with the data supplied by the user, then a HTTP 400 Bad Request is
   * returned and appropriate error message(s) should be shown to user. If a user tries to add an
   * inventory item to the inventory of a business they do not administer AND the user is not a
   * global application admin, then a HTTP 403 Forbidden is returned.
   *
   * @param id The ID of the business that the inventory item will be added to.
   * @param newInventoryItem The parameters of the inventory item to be added.
   * @return String containing created inventory item id
   */
  @PostMapping(path = "/businesses/{id}/inventory")
  public ResponseEntity<Object> addInventoryItem(
      @PathVariable long id, @Valid @RequestBody InventoryItem newInventoryItem) {
    InventoryItem savedItem = businessService.addInventoryItem(id, newInventoryItem);
    return new ResponseEntity<>(
        "{ \"inventoryItemId\": " + savedItem.getInventoryItemId() + " }", HttpStatus.CREATED);
  }

  /**
   * Gets the specified business' product inventory. The product inventory are instances of products
   * with a quantity, price for each unit of product and total price for the whole quantity.
   *
   * <p>If the inventory is retrieved successfully the controller returns HTTP 200 OK with the
   * inventory item content. If the account performing this request is not authorized i.e. they are
   * not an admin of the specified business, or they are not a global application administrator, the
   * controller returns HTTP 403 Forbidden. If the requested route is not specified e.g. an invalid
   * business, the controller returns a HTTP 406 response.
   *
   * @param id Id of the business to retrieve inventory of.
   * @return List containing instances of InventoryItems belonging to the specified business.
   */
  @GetMapping(path = "/businesses/{id}/inventory")
  public ResponseEntity<Object> getInventory(@PathVariable long id) {
    List<InventoryItem> items = businessService.retrieveInventory(id);
    return new ResponseEntity<>(items, HttpStatus.OK);
  }

  /**
   * This method adds a new saleListing with the specified business ID. If the user provides
   * incorrect data of saleListing , an HTTP 400 bad request is returned. If a user tries to add
   * listing for a business they do not administer AND the user is not a global application admin,
   * an HTTP 403 forbidden is returned. Otherwise if successful, returns an HTTP 201 created
   * response and the listing id as body.
   *
   * @param id The ID of the business corresponding to sale listing.
   * @return a listing id as response body.
   */
  @PostMapping(path = "/businesses/{id}/listings")
  public ResponseEntity<Object> addSaleItem(
      @PathVariable long id, @Valid @RequestBody SaleItemJson saleItemJson) {
    Long addedSaleItemId = businessService.addSaleItem(id, saleItemJson);
    return new ResponseEntity<>(
        "{\n" + "  \"listingId\":" + addedSaleItemId + "\n" + "}", HttpStatus.CREATED);
  }

  /**
   * This method retrieves the sale list with the specified business ID. If the user is not logged
   * in, an HTTP 401 Unauthorized response is returned. If a business with the specified ID cannot
   * be found, an HTTP 406 Not Acceptable request is returned. Otherwise if successful, returns an
   * HTTP 200 response and the requested business object.
   *
   * @param id The ID of the business corresponding to sale list.
   * @return A response to the get request.
   */
  @GetMapping(path = "/businesses/{id}/listings")
  public ResponseEntity<Object> getSaleItems(@PathVariable long id, @RequestParam boolean isSold) {
    List<SaleItem> listings = businessService.retrieveSaleItems(id, isSold);
    return new ResponseEntity<>(listings, HttpStatus.OK);
  }

  /**
   * This method retrieves businesses with the specified business name. If the user is not logged
   * in, an HTTP 401 Unauthorized response is returned. If the request sent with no query, an HTTP 400 Bad Request is
   * returned. Otherwise if successful, returns an HTTP 200 response and the requested business object.
   *
   * @param searchQuery contains business name input by user.
   * @return  A response to the get GET request.
   */
  @GetMapping(path = "/businesses/search")
  public ResponseEntity<Object> getBusinessesSearch(@RequestParam String searchQuery) {
    SearchingService.SearchResult businessResult = businessService.searchBusinesses(searchQuery);
    return new ResponseEntity<>(businessResult.getResult(), HttpStatus.OK);
  }

  /**
   * This method sets the SOLD attribute of sale item to TRUE. If the user is not logged in, an HTTP
   * 401 Unauthorized response is returned. If there was some error with the data supplied by the
   * user, an HTTP 400 Bad Request is returned. If a Listing with the specified ID cannot be found,
   * an HTTP 406 Not Acceptable request is returned. Otherwise if successful, returns an HTTP 200
   * response and the relevant sale listing ID.
   *
   * @param id The ID of the business corresponding to sale list.
   * @param listingId The ID of the sale listing.
   * @return A response to the PATCH request.
   */
  @PatchMapping(path = "/businesses/{id}/listings/{listingId}")
  public ResponseEntity<Object> patchSaleListingPurchase(
      @PathVariable long id, @PathVariable long listingId) {
    SaleItem targetSaleItem = businessService.purchaseSaleItem(id, listingId);
    businessService.sendPurchaseNotificationToOtherUsers(listingId);
    return new ResponseEntity<>(
        String.format("{\n" + "  \"saleItemId\": %d\n" + "}", targetSaleItem.getSaleItemId()), HttpStatus.OK);
  }

  /**
   * This method returns the information required for a business sales report.
   * If request parameters are missing or of the wrong format -> 400
   * If the user is not logged in -> 401
   * If a business with the given ID does not exist -> 406
   * If the user is not a GAA or business admin -> 403
   * Otherwise the sales report is returned with 200
   * @param id the ID of the business
   * @param periodStart the start of the report period
   * @param periodEnd the end of the report period
   * @param granularity how the data should be summarised (TOTAL or MONTHLY)
   * @return an HTTP response ot the GET request
   */
  @GetMapping(path = "/businesses/{id}/sales-report")
  public ResponseEntity<Object> getSalesReport(
          @PathVariable long id,
          @RequestParam("periodStart") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate periodStart,
          @RequestParam("periodEnd") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate periodEnd,
          @RequestParam String granularity) {
    Business business;
    try {
      business = businessService.getBusiness(id);
    } catch (NoSuchElementException exception) {
      throw new NotAcceptableStatusException("No business found with ID " + id);
    }
    SalesReportJson saleReport = businessService.getSalesReport(business, periodStart, periodEnd, granularity);
    return new ResponseEntity<>(saleReport.toString(), HttpStatus.OK);
  }

  /**
   * This method adds the user to the likedUsers for a sale item. If the user is not logged in, an HTTP
   * 401 Unauthorized response is returned. If there was some error with the data supplied by the
   * user, an HTTP 400 Bad Request is returned. If a Listing with the specified ID cannot be found,
   * an HTTP 406 Not Acceptable request is returned. Otherwise if successful, returns an HTTP 200
   * response and the relevant sale listing ID.
   *
   * @param id The ID of the business corresponding to sale list.
   * @param listingId The ID of the sale listing.
   * @return A response to the PATCH request.
   */
  @PatchMapping(path = "/businesses/{id}/listings/{listingId}/like")
  public ResponseEntity<Object> patchSaleListingLike(
          @PathVariable long id, @PathVariable long listingId) {
    businessService.likeSaleItem(id, listingId);
    return new ResponseEntity<>( HttpStatus.OK);
  }

  /**
   * This method removes the user from the likedUsers for a sale item. If the user is not logged in, an HTTP
   * 401 Unauthorized response is returned. If there was some error with the data supplied by the
   * user, an HTTP 400 Bad Request is returned. If a Listing with the specified ID cannot be found,
   * an HTTP 406 Not Acceptable request is returned. Otherwise if successful, returns an HTTP 200
   * response and the relevant sale listing ID.
   *
   * @param id The ID of the business corresponding to sale list.
   * @param listingId The ID of the sale listing.
   * @return A response to the PATCH request.
   */
  @PatchMapping(path = "/businesses/{id}/listings/{listingId}/unlike")
  public ResponseEntity<Object> patchSaleListingUnlike(
          @PathVariable long id, @PathVariable long listingId) {
    businessService.unlikeSaleItem(id, listingId);
    return new ResponseEntity<>( HttpStatus.OK);
  }

}
