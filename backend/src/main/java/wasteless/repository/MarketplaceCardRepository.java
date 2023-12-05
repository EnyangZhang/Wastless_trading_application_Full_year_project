package wasteless.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;
import wasteless.model.MarketplaceCard;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/** MarketplaceCardRepository defines the methods to be called on the JPA repository to retrieve marketplace cards. */
@RepositoryRestResource
public interface MarketplaceCardRepository extends JpaRepository<MarketplaceCard, Long> {

    /**
     * Finds all the marketplace cards in a single page of its section.
     * @param section String defining the section to retrieve cards from.
     * @param pageable Pageable specifying starting page to retrieve cards from, and the number of cards in a page.
     * @return A Page object containing a subset of all marketplace cards for the given section. Contains information
     * about the total number of cards in the section.
     */
    Page<MarketplaceCard> findMarketplaceCardsBySection(String section, Pageable pageable);

    /**
     * Finds all marketplace cards in a single section.
     * @param section String defining the section to retrieve cards from.
     * @return A List containing all the marketplace cards from the given section.
     */
    List<MarketplaceCard> findMarketplaceCardsBySection(String section);

    /**
     * Finds the marketplace card by its given ID. The built-in ID repository method does not work, as the marketplace
     * card ID is an integer and not a long.
     * @param id An integer value for the marketplace card ID.
     * @return An optional wrapper, containing either a marketplace card or null value.
     */
    Optional<MarketplaceCard> findMarketplaceCardByMarketplaceCardId(Integer id);

    /**
     * Selects the cards which display_period_end property is between oneDayAgo and currentDate.
     * In other words, selects the cards that have expired (today's date is past the expiry date), but not by twenty-four
     * hours.
     * @param currentDate LocalDateTime, the current date.
     * @param oneDayAgo LocalDateTime, which is twenty-four hours in the past from the current date.
     * @return A List of MarketplaceCard, which have expired from current date, but not expired more than twenty-four
     * from current date.
     */
    List<MarketplaceCard> findAllByDisplayPeriodEndBetween(LocalDateTime oneDayAgo, LocalDateTime currentDate);

    /**
     * Selects the cards that has expired dates that are less than (or equal to) the value of oneDayAgo.
     * In other words, selects the cards that have expired more than one day ago.
     * @param oneDayAgo LocalDateTime which is twenty-four hours in the past from the current date.
     * @return A List of MarketplaceCard, their expiry dates should be more than twenty-four hours past the current date.
     */
    List<MarketplaceCard> findAllByDisplayPeriodEndBefore(LocalDateTime oneDayAgo);

    /**
     * Selects all the cards which have been created by the user matching userId that have a DisplayPeriodEnd before
     * the current date.
     * @param userId The userId of the user to select the cards
     * @return A list of cards from the given userId
     */
    List<MarketplaceCard> findMarketplaceCardByCreatorUserIdAndDisplayPeriodEndAfter(long userId, LocalDateTime currentDate);

    Optional<MarketplaceCard> findMarketplaceCardByMarketplaceCardId(int marketplaceCardId);

    /**
     * Updates the display period end of a marketplace card
     *
     * @param id The id of the card for which the expiry is to be extended
     * @param extendedDate the new extended date of the marketplace card
     */
    @Modifying(clearAutomatically = true)
    @Query(
            value = " UPDATE marketplace_card c SET c.display_period_end = ?2 WHERE c.marketplace_card_id = ?1", nativeQuery = true
    )
    @Transactional
    void extendDisplayPeriodForCard(int id, LocalDateTime extendedDate);
}
