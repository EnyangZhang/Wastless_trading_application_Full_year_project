package wasteless.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.List;

/** This class defines a Business catalogue, its attributes and its methods. */
@NoArgsConstructor // Generates a constructor that takes no arguments (for JPA)
@Entity // Declares user as a JPA entity
@Setter
@Getter
@Table(name = "product")
public class Product {

  /** Catalogue Id, that will be auto generated, for current version. */
  @Id
  @JsonIgnore
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long rowId;

  /** The foreign key businessId from Business table. */
  @ManyToOne
  @JoinColumn(name = "businessId")
  @JsonIgnore
  private Business business;

  @Column
  @JsonProperty("id")
  @JsonAlias("productId")
  @NotBlank(message = "Product ID must not be blank")
  private String productId;

  /** The name of the catalogue item. */
  @NotBlank(message = "Name cannot be empty")
  @Column
  private String name;

  /** The description of the catalogue item. */
  @Column
  private String description;

  /** The manufacturer of the catalogue item. */
  @Column
  private String manufacturer;

  /** The recommended retail price of the catalogue item. */
  @Column
  private double recommendedRetailPrice;

  /** The created date of the catalogue item. */
  @Column
  private LocalDate created;

  /** image attribute, a list of all the catalogue's image. */
  @OneToMany(mappedBy = "product")
  private List<ProductImage> images;

  @OneToMany(mappedBy = "product")
  @JsonIgnore
  private List<InventoryItem> inventoryItems;

  public Product(
      Business business,
      String productId,
      String name,
      String manufacturer,
      String description,
      double recommendedRetailPrice,
      LocalDate created) {
    this.business = business;
    this.productId = productId;
    this.name = name;
    this.manufacturer = manufacturer;
    this.description = description;
    this.recommendedRetailPrice = recommendedRetailPrice;
    this.created = created;
  }

  /**
   * Gets the primary image of the product.
   * @return An image which is set to primary.
   * @throws IndexOutOfBoundsException In the case that there is no primary image.
   */
  public ProductImage getPrimaryProductImage() {
    if (getImages() != null) {
      for (ProductImage productImage : getImages()) {
        if (productImage.isPrimary()) {
          return productImage;
        }
      }
    }
    return null;
  }

  /**
   * Checks whether this product contains a given productImage.
   * @param productImage Image to check if this contains.
   * @return Whether this product contains given product image.
   */
  public boolean containsProductImage(ProductImage productImage) {
    return getImages() != null && getImages().contains(productImage);
  }
}
