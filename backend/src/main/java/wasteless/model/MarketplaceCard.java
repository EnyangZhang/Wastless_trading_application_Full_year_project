package wasteless.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class MarketplaceCard {

  /** Marketplace card ID attribute */
  @Id
  @Column
  @Getter
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer marketplaceCardId;

  /** User who created the card, one user can create many cards */
  @ManyToOne
  @JoinColumn(name = "creatorId")
  private User creator;

  /** Section of the marketplace the card is displayed in, one section can display many cards */
  @Column
  @NotBlank(message = "section cannot be blank")
  private String section;

  /** Marketplace card title attribute */
  @Column
  @NotBlank(message = "title cannot be blank")
  @Size(max = 32, message = "Title cannot exceed 32 characters")
  private String title;

  /** Marketplace card description attribute */
  @Column
  @Size(max = 250, message = "Description cannot exceed 250 characters")
  private String description;

  /** Multivalued attribute, a list of all the keywords that the card contains */
  @ManyToMany
  @JoinTable(
      name = "cardKeywords",
      joinColumns = @JoinColumn(name = "marketplaceCardId"),
      inverseJoinColumns = @JoinColumn(name = "marketplaceKeywordId"))
  private List<MarketplaceKeyword> keywords;

  /** Date attribute of when the marketplace card was created auto generated on creation */
  @Setter @Column private LocalDateTime created;

  /** Date attribute of when the marketplace card stop displaying */
  @Setter @Column private LocalDateTime displayPeriodEnd;

  /**
   * Is set to true when a notification is created for this specific card.
   * Once the card is renewed, this attribute is set to false.
   */
  @Column(columnDefinition = "boolean default false")
  @NotNull
  private Boolean notifiedExpiring;

  /**
   * Constructor for Marketplace card using only required fields JPA setters can be used for
   * optional attributes
   *
   * @param creator the User who created the card
   * @param section the section the card belongs too
   * @param title the title of the card
   * @param description the description of the card
   * @param keywords a list of the keywords associated with the card
   */
  public MarketplaceCard(
      User creator,
      String section,
      String title,
      String description,
      List<MarketplaceKeyword> keywords) {
    this.creator = creator;
    this.section = section;
    this.title = title;
    this.description = description;
    this.keywords = keywords;
  }

  /**
   * Returns true if the displayPeriodEnd attribute of model's date is 'past' the current date.
   * e.g. If today's date is 29th September 2021, 3 PM, and displayPeriodEnd is 29th September 2021, 2 PM, that means
   * that the current date is past the expiry date set for the card, and the card is expired.
   * @return true if the card is considered expired, false otherwise.
   */
  public boolean isExpired() {
    return displayPeriodEnd.isBefore(LocalDateTime.now());
  }
}
