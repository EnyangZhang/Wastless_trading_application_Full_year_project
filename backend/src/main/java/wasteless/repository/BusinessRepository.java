package wasteless.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import wasteless.model.Business;

import java.util.List;
import java.util.Optional;

@Qualifier("businesses")
@Repository

public interface BusinessRepository extends JpaRepository<Business, Long> {

  /**
   * A method to find businesses by ID
   *
   * @param id The ID of the Business
   * @return The Business with the specified ID
   */
  Optional<Business> findByBusinessId(long id);

  /**
   * Method to find all businesses that a given user is the primary admin of
   *
   * @param id The userID of the potential primary admin
   * @return A list containing all businesses of which the given user is the primary admin
   */
  List<Business> findAllByPrimaryAdminId(long id);

}
