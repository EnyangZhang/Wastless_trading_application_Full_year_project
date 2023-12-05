package wasteless.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wasteless.model.InventoryItem;

import java.util.Optional;

/**
 * InventoryItemRepository defines the methods to be called on the JPA repository to retrieve
 * InventoryItem.
 */
@Qualifier("inventoryItem")
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

  /**
   * A method to find InventoryItem by ID
   *
   * @param id The ID of the Inventory
   * @return The InventoryItem with the specified ID
   */
  Optional<InventoryItem> findByInventoryItemId(long id);
}
