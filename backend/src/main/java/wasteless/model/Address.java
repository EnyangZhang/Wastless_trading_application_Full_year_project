package wasteless.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Getter
@Setter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "address_id", "user"})
@Table(name = "address")
public class Address {
  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long addressId;

  @NotBlank(message = "Street number cannot be empty")
  @Size(min = 1, max = 250)
  @Column
  private String streetNumber;

  @NotBlank(message = "Street name cannot be empty")
  @Size(min = 1, max = 250)
  @Column
  private String streetName;

  @NotBlank(message = "Suburb cannot be empty")
  @Size(min = 1, max = 250)
  @Column
  private String suburb;

  @NotBlank(message = "City cannot be empty")
  @Size(min = 1, max = 250)
  @Column
  private String city;

  @NotBlank(message = "Region cannot be empty")
  @Size(min = 1, max = 250)
  @Column
  private String region;

  @NotBlank(message = "Country cannot be empty")
  @Size(min = 1, max = 250)
  @Column
  private String country;

  @NotBlank(message = "Postcode cannot be empty")
  @Size(min = 1, max = 250)
  @Column
  private String postcode;

  public Address(
      String streetNumber,
      String streetName,
      String suburb,
      String city,
      String region,
      String country,
      String postcode) {
    this.streetNumber = streetNumber;
    this.streetName = streetName;
    this.suburb = suburb;
    this.city = city;
    this.region = region;
    this.country = country;
    this.postcode = postcode;
  }

  @Override
  public String toString() {
    return "Address{"
        + "addressId="
        + addressId
        + ", streetNumber='"
        + streetNumber
        + '\''
        + ", streetName='"
        + streetName
        + '\''
        + ", suburb='"
        + suburb
        + '\''
        + ", city='"
        + city
        + '\''
        + ", region='"
        + region
        + '\''
        + ", country='"
        + country
        + '\''
        + ", postcode='"
        + postcode
        + '\''
        + '}';
  }
}
