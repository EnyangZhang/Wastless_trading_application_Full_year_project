package wasteless.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wasteless.model.ProductImage;

@Qualifier("productImage")
@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

}
