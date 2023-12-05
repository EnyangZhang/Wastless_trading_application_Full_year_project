package wasteless;

import wasteless.model.*;

public class ObjectJSONMapper {
  public String businessToRequest(Business b) {

    Address a = b.getAddress();
    return String.format(
        "{"
            + "\"name\": \"%s\","
            + "\"description\": \"%s\","
            + "\"address\": "
            + "{"
            + "\"streetNumber\": \"%s\","
            + "\"streetName\": \"%s\","
            + "\"suburb\": \"%s\","
            + "\"city\": \"%s\","
            + "\"region\": \"%s\","
            + "\"country\": \"%s\","
            + "\"postcode\": \"%s\""
            + "},"
            + "\"businessType\": \"%s\","
            + "\"primaryAdminId\": \"%s\""
            + "}",
        b.getName(),
        b.getDescription(),
        a.getStreetNumber(),
        a.getStreetName(),
        a.getSuburb(),
        a.getCity(),
        a.getRegion(),
        a.getCountry(),
        a.getPostcode(),
        b.getBusinessType(),
        b.getPrimaryAdminId());
  }

  public String businessToResponse(Business b, Long id) {
    Address a = b.getAddress();
    return String.format(
        ""
            + "{"
            + "\"businessId\":%s,"
            + "\"name\":\"%s\","
            + "\"description\":\"%s\","
            + "\"address\":"
            + "{"
            + "\"streetNumber\":\"%s\","
            + "\"streetName\":\"%s\","
            + "\"suburb\":\"%s\","
            + "\"city\":\"%s\","
            + "\"region\":\"%s\","
            + "\"country\":\"%s\","
            + "\"postcode\":\"%s\""
            + "},"
            + "\"businessType\":\"%s\","
            + "\"primaryAdminId\":%s,"
            + "\"admins\":%s,"
            + "\"registrationDate\":\"%s\","
            + "\"accountAge\":%s"
            + "}",
        id,
        b.getName(),
        b.getDescription(),
        a.getStreetNumber(),
        a.getStreetName(),
        a.getSuburb(),
        a.getCity(),
        a.getRegion(),
        a.getCountry(),
        a.getPostcode(),
        b.getBusinessType(),
        b.getPrimaryAdminId(),
        b.getAdmins(),
        b.getRegistrationDate(),
        b.getAccountAge());
  }

  public String userToRequest(User u) {
    Address a = u.getHomeAddress();
    return String.format(
        "{"
            + "  \"firstName\": \"%s\","
            + "  \"lastName\": \"%s\","
            + "  \"middleName\": \"%s\","
            + "  \"nickname\": \"%s\","
            + "  \"bio\": \"%s\","
            + "  \"email\": \"%s\","
            + "  \"dateOfBirth\": \"%s\","
            + "  \"phoneNumber\": \"%s\","
            + "  \"homeAddress\": {"
            + "    \"streetNumber\": \"%s\","
            + "    \"streetName\": \"%s\","
            + "    \"suburb\": \"%s\","
            + "    \"city\": \"%s\","
            + "    \"region\": \"%s\","
            + "    \"country\": \"%s\","
            + "    \"postcode\": \"%s\""
            + "  },"
            + "  \"password\": \"%s\""
            + "}",
        u.getFirstName(),
        u.getLastName(),
        u.getMiddleName(),
        u.getNickname(),
        u.getBio(),
        u.getEmail(),
        u.getDateOfBirth(),
        u.getPhoneNumber(),
        a.getStreetNumber(),
        a.getStreetName(),
        a.getSuburb(),
        a.getCity(),
        a.getRegion(),
        a.getCountry(),
        a.getPostcode(),
        u.getPassword());
  }

  public String mapCredentials(String email, String password) {
    return String.format(
        "{" + "  \"email\": \"%s\"," + "  \"password\": \"%s\"" + "}", email, password);
  }

  public String userToResponse(User u, Long userId) {
    Address a = u.getHomeAddress();
    return String.format(
        "{"
            + "\"id\":%s,"
            + "\"firstName\":\"%s\","
            + "\"lastName\":\"%s\","
            + "\"middleName\":\"%s\","
            + "\"nickname\":\"%s\","
            + "\"bio\":\"%s\","
            + "\"email\":\"%s\","
            + "\"dateOfBirth\":\"%s\","
            + "\"phoneNumber\":\"%s\","
            + "\"homeAddress\":{"
            + "\"streetNumber\":\"%s\","
            + "\"streetName\":\"%s\","
            + "\"suburb\": \"%s\","
            + "\"city\":\"%s\","
            + "\"region\":\"%s\","
            + "\"country\":\"%s\","
            + "\"postcode\":\"%s\""
            + "},"
            + "\"created\":\"%s\","
            + "\"role\":\"%s\","
            + "\"businessesAdministered\":%s"
            + "}",
        userId,
        u.getFirstName(),
        u.getLastName(),
        u.getMiddleName(),
        u.getNickname(),
        u.getBio(),
        u.getEmail(),
        u.getDateOfBirth(),
        u.getPhoneNumber(),
        a.getStreetNumber(),
        a.getStreetName(),
        a.getSuburb(),
        a.getCity(),
        a.getRegion(),
        a.getCountry(),
        a.getPostcode(),
        u.getCreated(),
        u.getRole(),
        u.getBusinessesAdministered());
  }

  public String productToRequest(Product p) {
    return String.format(
        "{"
            + "  \"id\": \"%s\","
            + "  \"name\": \"%s\","
            + "  \"description\": \"%s\","
            + "  \"manufacturer\": \"%s\","
            + "  \"recommendedRetailPrice\": \"%s\","
            + "  \"created\": \"%s\","
            + "  \"images\": []"
            + "}",
        p.getProductId(),
        p.getName(),
        p.getDescription(),
        p.getManufacturer(),
        p.getRecommendedRetailPrice(),
        p.getCreated());
  }

  public String inventoryItemToRequest(InventoryItem i) {
    return String.format(
        "{"
            + "  \"productId\": \"%s\","
            + "  \"quantity\": \"%s\","
            + "  \"pricePerItem\": \"%s\","
            + "  \"totalPrice\": \"%s\","
            + "  \"manufactured\": \"%s\","
            + "  \"sellBy\": \"%s\","
            + "  \"bestBefore\": \"%s\","
            + "  \"expires\": \"%s\""
            + "}",
        i.getProductId(),
        i.getQuantity(),
        i.getPricePerItem(),
        i.getTotalPrice(),
        i.getManufactured(),
        i.getSellBy(),
        i.getBestBefore(),
        i.getExpires());
  }

  public String cardToJsonRequest(MarketplaceCard c) {
    return String.format(
            "{"
                    + " \"creatorId\": %s,"
                    + " \"section\": \"%s\","
                    + " \"title\": \"%s\","
                    + " \"description\": \"%s\","
                    + " \"keywordIds\": [],"
                    + " \"created\": \"%s\","
                    + " \"displayPeriodEnd\": \"%s\""
                    + "}",
            c.getCreator().getUserId(),
            c.getSection(),
            c.getTitle(),
            c.getDescription(),
            c.getCreated(),
            c.getDisplayPeriodEnd());

  }

  public String createCardJsonRequest(long creatorId, String section, String title, String description) {
    return String.format(
            "{"
                    + "  \"creatorId\": %s,"
                    + "  \"section\": \"%s\","
                    + "  \"title\": \"%s\","
                    + "  \"description\": \"%s\","
                    + "  \"keywordIds\": []"
                    + "}",
            creatorId,
            section,
            title,
            description);
  }
}
