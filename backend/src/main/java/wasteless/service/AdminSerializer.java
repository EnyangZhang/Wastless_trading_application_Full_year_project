package wasteless.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import wasteless.model.Address;
import wasteless.model.Business;
import wasteless.model.User;

import java.io.IOException;
import java.util.List;

/**
 * Custom JSON serializer, extends StdSerializer provided by Jackson. Used to serialize Business's
 * list of administrators. Takes List of Users and serializes each User into JSON as follows: {
 * "id": id, "firstName": "", "lastName": "", "middleName": "", . . . "businessesAdministrated": [ {
 * "id": "", "administrators": [ {} ] . . . } ] }
 */
public class AdminSerializer extends StdSerializer<List<User>> {

  /** AdminSerializer Constructor that defines no parameters. Initializes itself to null. */
  public AdminSerializer() {
    this(null);
  }

  /**
   * AdminSerializer Constructor that takes Class<List<User>> as an argument. Passes this argument
   * to the superclass, StdSerializer.
   *
   * @param admins Instance of Class<List<User>>, List<User> contains instances of User to be
   *     serialized into JSON.
   */
  public AdminSerializer(Class<List<User>> admins) {
    super(admins);
  }

  /**
   * Implemented serialize method defined in StdSerializer.
   *
   * @param admins List of User instances to be serialized into JSON
   * @param gen Instance of JsonGenerator
   * @param provider Instance of SerializeProvider
   * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the
   *     general class of exceptions produced by failed or interrupted I/O operations.
   */
  @Override
  public void serialize(List<User> admins, JsonGenerator gen, SerializerProvider provider)
      throws IOException {
    gen.writeStartArray();
    for (User admin : admins) {
      writeUser(gen, admin);
    }
    gen.writeEndArray();
  }

  /**
   * Helper method for serialize. Serializes firstName, lastName, middleName, nickname, bio, email,
   * phone number, home address, and businesses administered into JSON format.
   *
   * @param gen Instance of JsonGenerator provided by parent method
   * @param admin Instance of User to be serialized into JSON provided by parent method
   * @throws IOException Signals that an I/O exception of some sort has occurred. This class is the
   *     general class of exceptions produced by failed or interrupted I/O operations.
   */
  private void writeUser(JsonGenerator gen, User admin) throws IOException {
    gen.writeStartObject();

    // Id, first name, last name, middle name, nickname, bio, email, date of birth and phone number
    gen.writeFieldName("id");
    gen.writeNumber(admin.getUserId());
    gen.writeStringField("firstName", admin.getFirstName());
    gen.writeStringField("lastName", admin.getLastName());
    gen.writeStringField("middleName", admin.getMiddleName());
    gen.writeStringField("nickname", admin.getNickname());
    gen.writeStringField("bio", admin.getBio());
    gen.writeStringField("email", admin.getEmail());
    gen.writeStringField("dateOfBirth", admin.getDateOfBirth().toString());
    gen.writeStringField("phoneNumber", admin.getPhoneNumber());

    // Home Address
    Address address = admin.getHomeAddress();
    gen.writeFieldName("homeAddress");
    writeAddress(
        gen,
        address.getStreetNumber(),
        address.getStreetName(),
        address.getCity(),
        address.getRegion(),
        address.getCountry(),
        address.getPostcode());

    // Created, Role
    gen.writeStringField("created", admin.getCreated().toString());
    gen.writeStringField("role", admin.getRole());

    // Businesses Administered
    gen.writeFieldName("businessesAdministered");
    gen.writeStartArray();
    for (Business business : admin.getBusinessesAdministered()) {
      writeBusiness(gen, business);
    }
    gen.writeEndArray();

    gen.writeEndObject();
  }

  /**
   * Helper method for serialize. Serializes id, administrators, primaryAdminId, address, name,
   * businessType and created into JSON. Administrators are serialized in a simplified form, each
   * user is serialized as an empty object. This prevents circular de-serialization and matches the
   * API spec, where two-tiered nested administrators are serialized into JSON as empty objects.
   *
   * @param gen Instance of JsonGenerator provided by parent method
   * @param business Instance of Business to be serialized into JSON provided by parent method
   * @throws IOException Signals that an I/O exception of some sort has occurred This class is the
   *     general class of exceptions produced by failed or interrupted I/O operations.
   */
  private void writeBusiness(JsonGenerator gen, Business business) throws IOException {
    gen.writeStartObject();

    // ID
    gen.writeFieldName("id");
    gen.writeNumber(business.getBusinessId());

    // Admins (Ignored)
    gen.writeFieldName("administrators");
    gen.writeStartArray();
    for (User ignored : business.getAdmins()) {
      gen.writeStartObject();
      gen.writeEndObject();
    }
    gen.writeEndArray();

    // Primary Admin ID
    gen.writeFieldName("primaryAdministratorId");
    gen.writeNumber(business.getPrimaryAdminId());

    // Business Address
    gen.writeFieldName("address");
    Address address1 = business.getAddress();
    writeAddress(
        gen,
        address1.getStreetNumber(),
        address1.getStreetName(),
        address1.getCity(),
        address1.getRegion(),
        address1.getCountry(),
        address1.getPostcode());
    gen.writeStringField("name", business.getName());
    gen.writeStringField("businessType", business.getBusinessType());
    gen.writeStringField("created", business.getRegistrationDate().toString());

    gen.writeEndObject();
  }

  /**
   * Helper method for serialize. Serializes Address types JSON. These properties are: Street
   * number, street name, city, region, country and postcode.
   *
   * @param gen Instance of JsonGenerator.
   * @param streetNumber Street number to be serialized as a string field
   * @param streetName Street name to be serialized
   * @param city City name to be serialized
   * @param region Region name to be serialized
   * @param country Country name to be serialized
   * @param postcode Postcode to be serialized
   * @throws IOException Signals that an I/O exception of some sort has occurred This class is the
   *     general class of exceptions produced by failed or interrupted I/O operations.
   */
  private void writeAddress(
      JsonGenerator gen,
      String streetNumber,
      String streetName,
      String city,
      String region,
      String country,
      String postcode)
      throws IOException {
    gen.writeStartObject();

    gen.writeStringField("streetNumber", streetNumber);
    gen.writeStringField("streetName", streetName);
    gen.writeStringField("city", city);
    gen.writeStringField("region", region);
    gen.writeStringField("country", country);
    gen.writeStringField("postcode", postcode);

    gen.writeEndObject();
  }
}
