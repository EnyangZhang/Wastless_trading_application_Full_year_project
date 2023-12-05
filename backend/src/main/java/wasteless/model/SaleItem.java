package wasteless.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class SaleItem implements Searchable {
  /** SaleItem Id, that will be auto generated, for current version. */
  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long saleItemId;

  /** The foreign key of inventoryId from inventory entity. */
  @ManyToOne
  @JoinColumn(name = "inventoryItemId")
  private InventoryItem inventoryItem;

  /**
   * Indicator whether the sale listing has been purchased from the business
   */
  @Column(name = "sold")
  private boolean isSold = false;

  /** The quantity of the saleItem. */
  @NotNull(message = "Quantity can not be null.")
  @Column
  private Integer quantity;

  /** The price of the saleItem. */
  @NotNull(message = "Price can not be null.")
  @Column
  private Double price;

  /** The extra information of the saleItem. */
  @Column private String moreInfo;

  /** The time that the saleItem is created. */
  @Column private LocalDateTime created;

  /** The time that the saleItem will close. */
  @Column private LocalDateTime closes;

  /** The time that the sale item is purchased */
  @Column private LocalDateTime purchased;

  /** The foreign key of userId from user model. For the user who purchased the product. */
  @ManyToOne
  @JsonIgnore
  @JoinColumn(name="purchaserId")
  private User purchaser;

  @ManyToMany
  @JoinTable(
          name = "sale_listing_like",
          joinColumns = @JoinColumn(name = "saleItemId"),
          inverseJoinColumns = @JoinColumn(name = "userId")
  )
  @JsonIgnoreProperties("likedByUsers")
  private Set<User> likedByUsers;

  /**
   * The constructor for saleItem entity.
   *
   * @param inventoryItem The inventoryItem that the saleItem belongs to.
   * @param quantity The quantity of the saleItem.
   * @param price The price of the saleItem.
   * @param moreInfo Extra information of the saleItem.
   * @param created The date that the saleItem is created.
   * @param closes The close date that the saleItem will expire.
   */
  public SaleItem(
      InventoryItem inventoryItem,
      Integer quantity,
      Double price,
      String moreInfo,
      LocalDateTime created,
      LocalDateTime closes) {
    this.inventoryItem = inventoryItem;
    this.quantity = quantity;
    this.price = price;
    this.moreInfo = moreInfo;
    this.created = created;
    this.closes = closes;
  }

  @JsonProperty("numberOfLikes")
  public int getNumberOfLikes() {
    return likedByUsers.size();
  }

  @JsonProperty("id")
  public Long getSaleItemId() {
    return saleItemId;
  }

  @JsonIgnore
  public void setSaleItemId(Long saleItemId) {
    this.saleItemId = saleItemId;
  }

  @Override
  public long getId() {
    return this.getSaleItemId();
  }

  @JsonProperty(value = "country")
  public String getBusinessCountry() {
    return inventoryItem.getBusiness().getAddress().getCountry();
  }

  @JsonProperty(value = "businessId")
  public long getBusinessId() {
    return inventoryItem.getBusiness().getBusinessId();
  }
}
