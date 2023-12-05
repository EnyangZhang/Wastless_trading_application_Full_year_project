package wasteless.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/** This class defines a Business catalogue image, its attributes and its methods. */
@NoArgsConstructor // Generates a constructor that takes no arguments (for JPA)
@Entity // Declares user as a JPA entity
@Getter
@Setter
@Table(name = "productImage")
public class ProductImage {

  /** Catalogue image id, that will be auto generated. */
  @Id
  @Column
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long productImageId;

  /** The foreign key catalogueId from BusinessCatalogue. */
  @ManyToOne
  @JoinColumn(name = "rowId")
  @JsonIgnore
  private Product product;

  @Column(name = "isPrimary")
  private boolean isPrimary;

  /** The image directory in string. */
  @JsonIgnore
  @Column
  private String filename;

  /** The image thumbnail directory in string. */
  @JsonIgnore
  @Column
  private String thumbnailFilename;

  public ProductImage(Product product, String imageFilename, String thumbnailFilename) {
    this.product = product;
    this.filename = imageFilename;
    this.thumbnailFilename = thumbnailFilename;
  }
}
