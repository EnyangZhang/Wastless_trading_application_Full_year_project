package wasteless.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wasteless.model.Business;
import wasteless.model.Product;

import java.util.List;

@Qualifier("products")
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  /**
   * Returns a list of Business Catalogue items from the specified business, with the given product
   * code (list length should be 1 or 0)
   */
  List<Product> findByRowId(long rowId);

  List<Product> findByBusinessAndProductId(Business business, String productId);
}
