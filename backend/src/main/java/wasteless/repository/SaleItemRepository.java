package wasteless.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import wasteless.model.SaleItem;

import java.util.List;
import java.util.Optional;

/**
 * SaleItemRepository defines the methods to be called on the JPA repository to retrieve SaleItem.
 */
@Qualifier("saleItem")
@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

  /**
   * A method to find SaleItem by ID
   *
   * @param id The ID of the SaleItem
   * @return The SaleItem with the specified ID
   */
  Optional<SaleItem> findById(long id);

  /**
   * A method to find all SaleItem by inventoryItemId.
   *
   * @param inventoryItemId The ID of the corresponding inventoryItemId.
   * @return The a list of SaleItem with the specified inventoryItemId.
   */
  @Query(
          value =
                  "SELECT * FROM sale_item s WHERE s.inventory_item_id = ?1",
          nativeQuery = true)
  List<SaleItem> findByInventoryItem(long inventoryItemId);

  /**
   * A method to find all sale items for a given business if sale item is not sold.
   *
   * @param businessId the ID of the business
   * @return a list of SaleItems
   */
  @Query(
      value =
          "SELECT * FROM sale_item s JOIN inventory_item i ON s.inventory_item_id = i.inventory_item_id WHERE (i.business_id = ?1 and s.sold is FALSE)",
      nativeQuery = true)
  List<SaleItem> findSaleListingsByBusiness(long businessId);

  /**
   * A method to find all sale items for a given business if sale item is sold.
   *
   * @param businessId the ID of the business
   * @return a list of SaleItems
   */
  @Query(
          value =
                  "SELECT * FROM sale_item s JOIN inventory_item i ON s.inventory_item_id = i.inventory_item_id WHERE (i.business_id = ?1 and s.sold is TRUE)",
          nativeQuery = true)
  List<SaleItem>findSoldListingsByBusiness(long businessId);
}
