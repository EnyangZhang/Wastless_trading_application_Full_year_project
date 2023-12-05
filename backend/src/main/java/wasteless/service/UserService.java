package wasteless.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.NotAcceptableStatusException;
import org.springframework.web.server.ResponseStatusException;
import wasteless.controller.jsonobjects.AddressJson;
import wasteless.controller.jsonobjects.UserJson;
import wasteless.exception.BadRequestException;
import wasteless.exception.ConflictException;
import wasteless.exception.ForbiddenException;
import wasteless.model.Address;
import wasteless.model.Business;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.repository.MarketplaceCardRepository;
import wasteless.repository.UserRepository;
import wasteless.service.searching_service.SearchParamsParser;
import wasteless.service.searching_service.SearchToken;
import wasteless.service.searching_service.SearchingService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Validated
public class UserService {

  private final PasswordEncoder passwordEncoder;

  private final UserRepository userRepository;

  private final SearchingService search;

  private final MarketplaceCardRepository marketplaceCardRepository;

  @Autowired
  public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository,
                     SearchingService userSearchService,
                     MarketplaceCardRepository marketplaceCardRepository) {
    this.passwordEncoder = passwordEncoder;
    this.marketplaceCardRepository = marketplaceCardRepository;
    this.userRepository = userRepository;
    this.search = userSearchService;
  }

  /**
   * This method calls the find method implemented by UserRepository. It extends the findUser
   * implementation, defined in UserRepositorySearch interface and implemented in
   * UserRepositorySearchImpl class.
   *
   * <p>Before it calls UserRepository's find, it constructs a list of tokens using
   * SearchParamsParser's parse method.
   *
   * @param params String that can be divided into tokens.
   * @return The list of users found using the search parameters
   */
  public SearchingService.SearchResult findUsers(String params, long pageNum, long perPage, String sortBy, String orderBy) {
    List<SearchToken> searchParams = SearchParamsParser.parse(params);
    return search.find(searchParams, pageNum, perPage, sortBy, orderBy);
  }

  /**
   * Creates user from UserJson request body. With throw exception if request is invalid.
   *
   * @param userJson Request received to create user with.
   * @return User if created.
   */
  public User createUser(UserJson userJson) throws ConflictException, BadRequestException {

    AddressJson addressJson = userJson.getHomeAddress();
    Address address = new Address(
            addressJson.getStreetNumber(),
            addressJson.getStreetName(),
            addressJson.getSuburb(),
            addressJson.getCity(),
            addressJson.getRegion(),
            addressJson.getCountry(),
            addressJson.getPostcode()
    );
    User user = new User(
            userJson.getFirstName(),
            userJson.getLastName(),
            userJson.getMiddleName(),
            userJson.getNickname(),
            userJson.getBio(),
            userJson.getEmail(),
            userJson.getDateOfBirth(),
            userJson.getPhoneNumber(),
            address,
            userJson.getPassword()
    );
    user.setCreated(LocalDate.now());
    user.setRole("user"); // TODO Make not hardcoded
    validateUser(user);
    user.setPassword(passwordEncoder.encode(user.getPassword()));

    user = saveUser(user);
    return userRepository.save(user);
  }

  /**
   * Updates the user
   *
   * @param userJson Instance of existing user.
   * @param id Id of the user to be edited.
   */
  public User updateUser(UserJson userJson, long id) throws BadRequestException {
    AddressJson addressJson = userJson.getHomeAddress();
    Address address = new Address(
            addressJson.getStreetNumber(),
            addressJson.getStreetName(),
            addressJson.getSuburb(),
            addressJson.getCity(),
            addressJson.getRegion(),
            addressJson.getCountry(),
            addressJson.getPostcode()
            );
    User user = new User(
            userJson.getFirstName(),
            userJson.getLastName(),
            userJson.getMiddleName(),
            userJson.getNickname(),
            userJson.getBio(),
            userJson.getEmail(),
            userJson.getDateOfBirth(),
            userJson.getPhoneNumber(),
            address,
            userJson.getPassword()
    );
    User existingUser = getUserById(id);
    user.setUserId(existingUser.getUserId());
    user.setRole(existingUser.getRole());
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    validateUser(user);
    return userRepository.save(user);
  }

  /**
   * Deletes a user from the database.
   *
   * @param id Id of the user to be deleted
   * @throws ResponseStatusException 403 if user is null from database, 406 if user is primary owner
   *     of a business
   */
  public void deleteUser(Long id, User currentUser) {
    requireGlobalAdmin(currentUser);
    User user = getUserById(id);
    userIsPrimaryAdmin(user);
    userRepository.deleteById(id);
  }

  /**
   * Validates user is of correct form.
   *
   * @param user The JSON body to validate.
   * @throws ResponseStatusException Thrown in case that user is invalid.
   */
  public void validateUser(@Valid User user) throws BadRequestException {
    if (isUnderage(user.getDateOfBirth())) {
      throw new BadRequestException("Must be at least 13 years of age");
    }
    if (isInvalidEmail(user.getEmail())) {
      throw new BadRequestException("Invalid E-mail");
    }
    if (isDuplicateEmail(user.getEmail())) {
      throw new ConflictException("E-mail is already taken");
    }
    if(user.getPassword().strip().length() == 0) {
      throw new BadRequestException("Password cannot be empty");
    }
    if(user.getPassword().length() > 250) {
      throw new BadRequestException("Password cannot be empty");
    }
  }

  /**
   * Ensures that the user is at least 13 years of age given the current date. All users must be at
   * least 13 to use Wasteless, and this check is used in registration.
   *
   * @param dateOfBirth The date of birth of the user to be registered
   */
  public Boolean isUnderage(LocalDate dateOfBirth) {
    LocalDate currentDate = LocalDate.now();
    return dateOfBirth.isAfter(currentDate.minusYears(13));
  }

  /**
   * Retrieves a user from the database using UserRepository matching e-mail argument.
   *
   * @param email E-mail string of user to retrieve from database.
   * @return Instance of found user, otherwise returns null.
   */
  public User getUserByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
  }

  /**
   * Retrieves a user from the database using UserRepository matching id argument.
   *
   * @param id User id of user to retrieve from database
   * @return Instance of found user, otherwise returns null.
   */
  public User getUserById(long id) {
    return userRepository.findById(id).orElseThrow();
  }

  /**
   * Check that an e-mail string is valid for a new user account.
   *
   * @param email String representing the e-mail input for a new user account.
   * @return True if string is a valid e-mail, false otherwise.
   */
  public Boolean isInvalidEmail(String email) {
    if (email == null) {
      return true;
    }
    Pattern expectedPattern;
    expectedPattern =
        Pattern.compile(
            "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$");
    Matcher match = expectedPattern.matcher(email);
    return !match.matches();
  }

  /**
   * Ensures the registering user's e-mail is unique before creating new instance of User.
   * Accomplished by performing a search for any user with the given e-mail in the database.
   *
   * @param email The e-mail to be registered
   */
  public Boolean isDuplicateEmail(String email) {
    return getUserByEmail(email) != null;
  }

  /**
   * Checks that the currently logged in user is a global admin.
   *
   * @param currentUser instance of currently logged in user
   * @throws ResponseStatusException 406 if user is not global admin
   */
  public void requireGlobalAdmin(User currentUser) throws ForbiddenException {
    if (!currentUser.getRole().equals("global_admin")
        && !currentUser.getRole().equals("default_global_admin")) {
      throw new ForbiddenException("Current user is not a global admin");
    }
  }

  /**
   * Requires that the current user holds the Default Global Application Admin role.
   *
   * @param currentUser Instance of currently logged in user.
   * @throws ResponseStatusException 406 if user is not the DGAA.
   */
  public void requireDGAA(User currentUser) throws ForbiddenException {
    if (!currentUser.getRole().equals("default_global_admin")) {
      throw new ForbiddenException("Current user is not Default Global Application Admin");
    }
  }

  /**
   * Checks if a user is the primary admin of an existing business.
   *
   * @param user Instance of user
   * @throws ResponseStatusException 406 if user is an
   */
  public void userIsPrimaryAdmin(User user) throws ForbiddenException {
    for (Business business : user.getBusinessesAdministered()) {
      if (user.getUserId() == business.getPrimaryAdminId()) {
        throw new ForbiddenException("Cannot delete: User is primary admin of a business");
      }
    }
  }

  /**
   * This method checks if a user with the userId exists in the DB or not. It is used to throw a
   * NotAcceptableStatusException if the userId does not match a user
   * @param userId to check if a user exists with this userId
   * @throws ForbiddenException If a user does not exist with the userId
   */
  public void userExists(Long userId) throws ForbiddenException {
    try {
      getUserById(userId);
    } catch (NoSuchElementException e) {
      throw new NotAcceptableStatusException("Cannot find cards for a user that doesn't exist");
    }
  }

  /**
   * If user is NOT global admin AND user is NOT default global admin, set role to GAA and save user
   * to database. If user is already a GAA or DGAA, the server performs no action.
   *
   * @param id id of the user to make admin.
   * @param currentUser An instance of the current user
   */
  public void makeAdmin(long id, User currentUser) {

    requireDGAA(currentUser);

    User user = getUserById(id);

    if ((!(user.getRole().equals("global_admin"))
        && !(user.getRole().equals("default_global_admin")))) {
      user.setRole("global_admin");
      userRepository.save(user);
    }
  }

  /**
   * Revoke admin status from the user, and saves user to database.
   *
   * @param id id of the user to make admin.
   * @param currentUser Instance of the currently logged in user.
   * @throws ResponseStatusException 409 if the current user is trying to revoke their own DGAA
   *     status.
   */
  public void revokeAdmin(long id, User currentUser) throws ConflictException {

    requireDGAA(currentUser);
    User user = getUserById(id);

    // if the DGAA is trying to revoke their own admin status, respond with 409
    if (user.getUserId() == currentUser.getUserId()) {
      throw new ConflictException("DGAA cannot revoke their own status");
    }

    // otherwise set the user's role to 'user' and respond with 200
    if (user.getRole().equals("global_admin")) {
      user.setRole("user");
      userRepository.save(user);
    }
  }

  /**
   * Takes a userId and returns all of the marketplace cards associated with that userId
   * If there is no user by the given id, then returns a 406 not acceptable error
   * @param id The id of the user requesting their cards
   * @return A list of marketplace cards
   * @throws NotAcceptableStatusException if user by given ID doesn't exist
   */
  public List<MarketplaceCard> getUserCardsById(long id) {
    userExists(id);
    LocalDateTime currentDate = LocalDateTime.now();
    return marketplaceCardRepository.findMarketplaceCardByCreatorUserIdAndDisplayPeriodEndAfter(id, currentDate);
  }

  private User saveUser(@Valid User user) {
    return user;
  }
}
