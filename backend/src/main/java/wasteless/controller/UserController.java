package wasteless.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import wasteless.controller.jsonobjects.UserJson;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.security.AuthUtil;
import wasteless.service.UserService;
import wasteless.service.searching_service.SearchingService;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * This class allows for the registration and logging in of User accounts. Implemented as a REST
 * controller, it contains methods mapped to requests; GET, POST, PUT, DELETE, etc. It carries out
 * these requests and interacts with UserService by creating, editing, deleting and searching and
 * retrieving Users from the repository.
 *
 * <p>It also carries out necessary validation of User attributes, through the @Valid tag.
 */
@RestController
public class UserController {

  private final AuthUtil authUtil;

  private final UserService userService;

  /**
   * Autowired UserController constructor method to initialize userRepository and
   * searchParamsParser.
   *
   * @param authUtil Instance of AuthUtil
   * @param userService Instance of UserService/.
   */
  @Autowired
  public UserController(AuthUtil authUtil, UserService userService) {
    this.authUtil = authUtil;
    this.userService = userService;
  }

  /**
   * Registers a new User into the database, assuming valid attributes. Performs explicit checks for
   * duplicate email addresses and underage users. Also sets the user's password to be a hash of
   * their plaintext password, ensuring security. Generates and sends an appropriate HTTP response.
   *
   * @param requestBody The new User to be registered
   * @return A response to the post request
   */
  @PostMapping(path = "/users")
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<Object> createUser(@Valid @RequestBody UserJson requestBody) {
    String email = requestBody.getEmail();
    String password = requestBody.getPassword();

    User savedUser = userService.createUser(requestBody);

    authUtil.authenticate(email, password);

    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(savedUser.getUserId())
            .toUri();

    return ResponseEntity.created(location).body("{ \"userId\": " + savedUser.getUserId() + " }");
  }

  /**
   * This method updates an existing user, using the ID of the User
   *
   * @param user The new user json object to update the existing user
   * @param id The ID of the user to be updated
   * @return An HTTP Response 200 if successful, 406 upon rejection, or 401 if unauthorized.
   */
  @CrossOrigin(origins = "*", allowedHeaders = "*")
  @PutMapping(path = "/users/{id}")
  public ResponseEntity<Object> updateUser(@Valid @RequestBody UserJson user, @PathVariable long id) {
    if (authUtil.getCurrentUser().getUserId() == id) {
      userService.updateUser(user, id);
      return ResponseEntity.ok().build();
    } else {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
  }

  /**
   * This method retrieves a user given a specific ID. If the user is not logged in, an HTTP 561
   * Unauthorized response is returned. If the user with given ID does not exist, an HTTP 406 Not
   * Acceptable response is returned. If otherwise successful, an HTTP 200 response is returned
   * along with the user object.
   *
   * @param id The id of the user to be retrieved
   * @return An HTTP response to the get request
   */
  @GetMapping(path = "/users/{id}")
  public ResponseEntity<Object> retrieveUser(@PathVariable long id) {
    authUtil.getCurrentUser();
    User user = userService.getUserById(id);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  /**
   * This method searches for users by name, given a string in request parameters. If the user is
   * not logged in, a HTTP 401 Unauthorized response is returned. Otherwise, an HTTP 200 response is
   * returned, along with the user object.
   *
   * @param searchQuery A string containing the search parameters
   * @param pageNum The current page that frontend will display.
   * @param perPage Total items each page will display.
   * @param sortBy The field name that needs to be sorted.
   * @param orderBy The order in asc or desc.
   * @return A HTTP response to the get request
   */
  @GetMapping(path = "/users/search")
  public ResponseEntity<Object> searchForUser(@RequestParam String searchQuery,
                                              @RequestParam long pageNum,
                                              @RequestParam long perPage,
                                              @RequestParam String sortBy,
                                              @RequestParam String orderBy) {
    authUtil.getCurrentUser();
    SearchingService.SearchResult foundUsers = userService.findUsers(searchQuery, pageNum, perPage, sortBy, orderBy);
    HttpHeaders responseHeader = new HttpHeaders();
    responseHeader.add("Total-length", String.valueOf(foundUsers.getResultsLength()));
    return ResponseEntity.ok().headers(responseHeader).body(foundUsers.getResult());

  }

  /**
   * This method deletes a User with a specific ID
   *
   * @param id The ID of the user to be deleted
   * @return ResponseEntity with status code. 401 if auth token is invalid, 406 if path to user does
   *     not exist, 406 if current user is not a global admin or target user is a primary owner of a
   *     business
   */
  @DeleteMapping(path = "/users/{id}")
  public ResponseEntity<String> deleteUser(@PathVariable long id) {
    User currentUser = authUtil.getCurrentUser();
    userService.deleteUser(id, currentUser);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Sets a user to be a global application administrator. The currently logged in user must be the
   * DGAA.
   *
   * @param id the ID of the user who is becoming an administrator
   * @return one of the following responses: 200 OK, 403 Forbidden, 406 Not acceptable
   */
  @PutMapping(path = "/users/{id}/makeadmin")
  public ResponseEntity<Object> makeAdmin(@PathVariable long id) {

    User currentUser = authUtil.getCurrentUser();
    userService.makeAdmin(id, currentUser);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Sets a user's role to be 'user'. The currently logged in user must be the DGAA.
   *
   * @param id the ID of the user whose admin status is being revoked
   * @return one of the following responses: 200 OK, 403 Forbidden, 406 Not acceptable, 409 conflict
   */
  @PutMapping(path = "/users/{id}/revokeadmin")
  public ResponseEntity<Object> revokeAdmin(@PathVariable long id) {

    // Check authorization token validity
    User currentUser = authUtil.getCurrentUser();
    userService.revokeAdmin(id, currentUser);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * This method retrieves a user's cards given a specific user ID.
   *
   * @param id The id of the user who's cards are to be retrieved
   * @return An HTTP response to the get request
   */
  @GetMapping(path = "/users/{id}/cards")
  public ResponseEntity<Object> retrieveUserCards(@PathVariable long id) {
    authUtil.getCurrentUser();
    List<MarketplaceCard> cards = userService.getUserCardsById(id);
    return new ResponseEntity<>(cards, HttpStatus.OK);
  }
}
