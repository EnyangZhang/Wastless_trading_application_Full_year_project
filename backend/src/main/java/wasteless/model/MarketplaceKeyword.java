package wasteless.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
public class MarketplaceKeyword {

  /** Marketplace keyword ID attribute */
  @Id @Column private Integer marketplaceKeywordId;

  /** Marketplace keyword name attribute */
  @Column private String name;

  /** Date attribute of when the marketplace keyword was created auto generated on creation */
  @Column private LocalDateTime created;

  @JsonIgnore
  @ManyToMany(mappedBy = "keywords")
  private List<MarketplaceCard> cards;

  public MarketplaceKeyword(
      int marketplaceKeywordId, String name, LocalDateTime created, List<MarketplaceCard> cards) {
    this.marketplaceKeywordId = marketplaceKeywordId;
    this.name = name;
    this.created = created;
    this.cards = cards;
  }
}
