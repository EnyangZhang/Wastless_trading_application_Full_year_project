package wasteless.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wasteless.service.AdminSerializer;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;

/** This class defines a Business account, its attributes and its methods. */
@Getter
@Setter
@NoArgsConstructor // Generates a constructor that takes no arguments (for JPA)
@AllArgsConstructor
@Entity // Declares user as a JPA entity
public class Business implements Searchable {

  /** Business ID attribute */
  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long businessId;

  /** Business name attribute */
  @Column
  @NotBlank(message = "Name cannot be empty")
  @Size(min = 1, max = 250, message = "Name must be between 1 and 250 characters")
  private String name;

  /** Description attribute */
  @Column
  @Size(max = 2000, message = "Description cannot exceed 2000 characters")
  private String description;

  /** Address attribute */
  @Valid
  @NotNull(message = "Address cannot be null")
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "address_id", referencedColumnName = "addressId")
  private Address address;

  /** Business type attribute */
  @Column
  @NotEmpty(message = "Type of business cannot be empty")
  @Pattern(
      regexp =
          "Accommodation and Food Services|Retail Trade|Charitable Organisation|Non-Profit Organisation")
  private String businessType;

  /** Primary Admin attribute */
  @NotNull(message = "Primary Admin cannot be null")
  @Column
  private long primaryAdminId;

  /** Multivalued attribute, a list of all the business's administrators */
  @JsonSerialize(using = AdminSerializer.class)
  @ManyToMany(mappedBy = "businessesAdministered")
  private List<User> admins;

  /** Catalogues attribute, a list of all the business's catalogues. */
  @JsonIgnore
  @OneToMany(mappedBy = "business")
  private List<Product> productCatalogue;

  @JsonIgnore
  @OneToMany(mappedBy = "business")
  private List<InventoryItem> inventoryItems;

  /** Registration date. Automatically set when a business is created. */
  @Column
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
  private LocalDate registrationDate;

  public Business(
      String businessName,
      String description,
      Address address,
      String type,
      long primaryAdminId,
      List<User> admins) {
    this.name = businessName;
    this.description = description;
    this.address = address;
    this.businessType = type;
    this.primaryAdminId = primaryAdminId;
    this.admins = admins;
    this.registrationDate = LocalDate.now();
  }

  /**
   * toString method for Business class
   *
   * @return all of a Business's attributes as a single string.
   */
  @Override
  public String toString() {
    return "Business"
        + "\nID: "
        + businessId
        + "\nName: "
        + name
        + "\nDescription: "
        + description
        + "\nAddress: "
        + address
        + "\nType: "
        + businessType
        + "\nPrimaryAdminId: "
        + primaryAdminId
        + "\nRegistration Date: "
        + registrationDate;
  }

  public String getJson() {
    return String.format(
            "{"
                    + " \"businessId\": %d,"
                    + " \"name\": \"%s\","
                    + " \"description\": \"%s\","
                    + " \"businessType\": \"%s\","
                    + " \"primaryAdminId\": %d,"
                    + " \"registrationDate\": \"%s\""
                    + "}",
            businessId,
            name,
            description,
            businessType,
            primaryAdminId,
            registrationDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
  }

  /**
   * This method gets the age of the business account in months rounded down
   *
   * @return The age of the account in months
   */
  public int getAccountAge() {
    return Period.between(registrationDate, LocalDate.now()).getMonths();
  }

  /**
   * This method adds a new product to the business catalogue
   *
   * @param product the new product to be added
   */
  public void addProduct(Product product) {
    this.productCatalogue.add(product);
  }

  public List<Product> getProductCatalogue() {
    return this.productCatalogue;
  }

  public void addInventoryItem(InventoryItem item) {
    this.inventoryItems.add(item);
  }

  public void addAdministrator(User user) {
    admins.add(user);
  }

  public void removeAdministrator(User user) {
    admins.remove(user);
  }

  public long getId() {
    return this.businessId;
  }
}
