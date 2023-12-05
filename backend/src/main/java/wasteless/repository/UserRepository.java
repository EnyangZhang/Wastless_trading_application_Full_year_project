package wasteless.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import wasteless.model.User;

import java.util.List;
import java.util.Optional;

/** UserRepository defines the methods to be called on the JPA repository to retrieve users. */
@RepositoryRestResource
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * A method to find users by ID
   *
   * @param id The ID of the User
   * @return The User with the specified ID
   */
  Optional<User> findById(long id);

  /**
   * A method to find the first user matching a role if one exists
   *
   * @param role The role of the User
   * @return The User with matching role if one exists
   */
  User findByRole(String role);

  /**
   * A method to find all users matching a given role
   *
   * @param role The role of the desired Users
   * @return A list of users with the matching roll
   */
  List<User> findAllByRole(String role);

  /**
   * A method to find users by matching both email and password for logging in
   *
   * @param email The user's email
   * @param password The password hash
   * @return The User that matches both email and password if it exists
   */
  User findByEmailAndPassword(String email, String password);

  /**
   * Find user with email
   *
   * @param email users email
   * @return User with that email
   */
  Optional<User> findByEmail(String email);

  /**
   * Method to find all users by Email. Implemented in UserController, used to check if there are
   * more than one users with the same e-mail.
   *
   * @param email The e-mail passed in as a string
   * @return A list of users with e-mails matching the parameter
   */
  List<User> findAllByEmail(String email);
}
