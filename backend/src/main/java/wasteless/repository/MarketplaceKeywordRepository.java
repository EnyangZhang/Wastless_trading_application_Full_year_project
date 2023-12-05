package wasteless.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import wasteless.model.MarketplaceKeyword;

import java.util.Optional;

/** MarketplaceKeywordRepository defines the methods to be called on the JPA repository to retrieve keywords. */
@RepositoryRestResource
public interface MarketplaceKeywordRepository extends JpaRepository<MarketplaceKeyword, Long> {

    /**
     * A method to find the MarketplaceKeyword matching a keyword ID if one exists
     *
     * @param id The ID of the MarketplaceKeyword
     * @return The MarketplaceKeyword with matching ID
     */
    Optional<MarketplaceKeyword> findByMarketplaceKeywordId(Integer id);
}
