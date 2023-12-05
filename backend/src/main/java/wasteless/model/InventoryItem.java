package wasteless.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class InventoryItem {

  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long inventoryItemId;

  @NotBlank(message = "Product ID is required")
  @JsonIgnore
  private String productId;

  @ManyToOne
  @JoinColumn(name = "businessId")
  @JsonIgnore
  private Business business;

  @ManyToOne
  @JoinColumn(name = "rowId")
  private Product product;

  @NotNull(message = "Quantity is required")
  @PositiveOrZero(message = "Quantity must be greater or equal 0")
  @Column
  private Integer quantity;

  @PositiveOrZero(message = "Price per item cannot be negative")
  @Column
  private Double pricePerItem;

  @PositiveOrZero(message = "Total price cannot be negative")
  @Column
  private Double totalPrice;

  @Column
  @Past(message = "Manufacturing date cannot be in the future")
  private LocalDate manufactured;

  @Column
  private LocalDate sellBy;

  @Column
  private LocalDate bestBefore;

  @NotNull(message = "Expiry date is required")
  @Column
  private LocalDate expires;

  @JsonIgnore
  @ToString.Exclude
  @OneToMany(mappedBy = "inventoryItem")
  private List<SaleItem> saleItems;

  @Column
  private LocalDateTime created;

  public InventoryItem(
      Product product,
      String productId,
      Integer quantity,
      Double pricePerItem,
      Double totalPrice,
      LocalDate manufactured,
      LocalDate sellBy,
      LocalDate bestBefore,
      LocalDate expires) {
    this.product = product;
    this.productId = productId;
    this.quantity = quantity;
    this.pricePerItem = pricePerItem;
    this.totalPrice = totalPrice;
    this.manufactured = manufactured;
    this.sellBy = sellBy;
    this.bestBefore = bestBefore;
    this.expires = expires;
    this.created = LocalDateTime.now();
  }

  @JsonIgnore
  public String getProductId() {
    return productId;
  }

  @JsonProperty
  public void setProductId(String productId) {
    this.productId = productId;
  }
}
