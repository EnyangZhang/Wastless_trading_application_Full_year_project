package wasteless.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.server.NotAcceptableStatusException;
import wasteless.DGAAConfig;
import wasteless.controller.jsonobjects.AddressJson;
import wasteless.controller.jsonobjects.UserJson;
import wasteless.exception.BadRequestException;
import wasteless.exception.ConflictException;
import wasteless.exception.ForbiddenException;
import wasteless.model.Address;
import wasteless.model.MarketplaceCard;
import wasteless.model.User;
import wasteless.repository.UserRepository;
import wasteless.service.searching_service.SearchToken;
import wasteless.service.searching_service.SearchingService;
import wasteless.test_helpers.MarketplaceDataCreator;
import wasteless.repository.MarketplaceCardRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {

  @MockBean private DGAAConfig DGAAConfig;

  @MockBean private UserRepository userRepository;

  @MockBean private MarketplaceCardRepository marketplaceCardRepository;

  @Autowired private UserService userService;

  @MockBean
  @Qualifier("userSearchService")
  private SearchingService userRepositorySearch;

  private UserJson userJson;
  private User user;
  private MarketplaceCard card;

  private UserJson makeUserJson() {
    AddressJson address =
        new AddressJson("123", "Madeup Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041");
    return new UserJson(
        1L,
        "John",
        "Hector",
        "Doe",
        "Nickname",
        "Definitely a real person",
        "johndoe@gmail.com",
        LocalDate.parse("1990-01-01"),
        "12345678",
        address,
        "password123");
  }

  private User makeUser() {
    Address address =
            new Address("123", "Madeup Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8041");
    User user = new User(
            "John",
            "Hector",
            "Doe",
            "Nickname",
            "Definitely a real person",
            "johndoe@gmail.com",
            LocalDate.parse("1990-01-01"),
            "12345678",
            address,
            "password123");
    return user;
  }

  @BeforeEach
  void setup() {

    userJson = makeUserJson();
    userJson.setId(1L);

    user = makeUser();
    user.setUserId(1L);
    card = MarketplaceDataCreator.createCard(1, user);
    LocalDateTime currentDate = LocalDateTime.now();
    LocalDateTime displayEnd = currentDate.plusWeeks(1L);
    card.setDisplayPeriodEnd(displayEnd);
    List<MarketplaceCard> cards = new ArrayList<MarketplaceCard>() {{
      add(card);
    }};
    when(marketplaceCardRepository.findMarketplaceCardByCreatorUserIdAndDisplayPeriodEndAfter(Mockito.anyLong(), Mockito.any())).thenReturn(cards);

    Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(new User());
    Mockito.when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
  }

  /*
   * Create user
   * Test user saved is correct
   */

  @Test
  void createUserValidUserSaveCorrectValues() {
    user.setUserId(0L);
    userService.createUser(userJson);
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository, Mockito.times(1)).save(userArgumentCaptor.capture());
    Assertions.assertEquals(user, userArgumentCaptor.getValue());
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

  /*
   * Invalid E-mail
   * Empty, Blank, null, no "@", no ".com", special characters
   * Check: Correct exception is thrown, correct exception message, save is not called
   */
  @Test
  void createUserInvalidEmailThrowsCorrectException() {
    userJson.setEmail("");
    try {
      userService.createUser(userJson);
    } catch (BadRequestException e) {
      Assertions.assertEquals("Invalid E-mail", e.getMessage());
    }
  }

  @Test
  void createUserInvalidEmailSaveNotCalled() {
    userJson.setEmail("");
    try {
      userService.createUser(userJson);
    } catch (Exception ignored) {
    }
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository, Mockito.times(0)).save(userArgumentCaptor.capture());
  }

  @Test
  void createUserInvalidEmail_nullSaveNotCalled() {
    userJson.setEmail(null);
    try {
      userService.createUser(userJson);
    } catch (Exception ignored) {
    }
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository, Mockito.times(0)).save(userArgumentCaptor.capture());
  }

  /*
   * Invalid birth date
   * Test birthdate underage throws BadRequestException
   *   Test no call to save
   * Test birthdate for user who is exactly 13
   */
  @Test
  void createUserUnderageThrowsCorrectException() {
    LocalDate currentDate = LocalDate.now();
    currentDate = currentDate.minusYears(13);
    currentDate = currentDate.minusDays(1);
    userJson.setDateOfBirth(currentDate);
    try {
      userService.createUser(userJson);
    } catch (BadRequestException e) {
      Assertions.assertEquals("Invalid E-mail", e.getMessage());
    }
  }

  @Test
  void createUserUnderageSaveNotCalled() {
    LocalDate currentDate = LocalDate.now();
    currentDate = currentDate.minusYears(13);
    currentDate = currentDate.plusDays(1);
    userJson.setDateOfBirth(currentDate);
    try {
      userService.createUser(userJson);
    } catch (Exception ignored) {
    }
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository, Mockito.times(0)).save(userArgumentCaptor.capture());
  }

  /*
   * Test duplicate email
   * Test Throws correct exception
   *   Test no call to userRepository.save()
   */
  @Test
  void createUserDuplicateEmailThrowsCorrectException() {
    Mockito.when(userRepository.findByEmail(Mockito.anyString()))
        .thenReturn(java.util.Optional.ofNullable(user));
    try {
      userService.createUser(userJson);
    } catch (ConflictException e) {
      Assertions.assertEquals("E-mail is already taken", e.getMessage());
    }
  }

  @Test
  void findUsersCallsFindUsersRepositoryMethod() {
    userService.findUsers("ABC 123", 1, 20, "default", "asc");
    Mockito.verify(userRepositorySearch, Mockito.times(1)).find(Arrays.asList(new SearchToken("ABC", false),
            new SearchToken("123", false)), 1, 20, "default", "asc");
  }

  @Test
  void updateUserValidUser() {
    userService.updateUser(userJson, 1L);
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository, Mockito.times(1)).save(userArgumentCaptor.capture());
  }

  @Test
  void updateNonExistentUserValidUserReturnsCorrectException() {
    try {
      userService.updateUser(userJson, 2L);
    } catch (NoSuchElementException e) {
      Assertions.assertEquals("No value present", e.getMessage());
    }
  }

  @Test
  void updateUserValidUserSaveCorrectValues() {
    userService.updateUser(userJson, 1L);
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository, Mockito.times(1)).save(userArgumentCaptor.capture());
    Assertions.assertEquals(user, userArgumentCaptor.getValue());
  }

  // Invalid E-mail
  @Test
  void updateUserInvalidEmailThrowsCorrectException() {
    userJson.setEmail("");
    try {
      userService.updateUser(userJson, 1L);
    } catch (BadRequestException e) {
      Assertions.assertEquals("Invalid E-mail", e.getMessage());
    }
  }

  // E-mail conflict
  @Test
  void updateUserDuplicateEmailThrowsCorrectException() {
    Mockito.when(userRepository.findByEmail(Mockito.anyString()))
        .thenReturn(java.util.Optional.ofNullable(user));
    try {
      userService.updateUser(userJson, 1L);
    } catch (ConflictException e) {
      Assertions.assertEquals("E-mail is already taken", e.getMessage());
    }
  }

  @Test
  void updateUserInvalidEmailSaveNotCalled() {
    userJson.setEmail("");
    try {
      userService.updateUser(userJson, 1L);
    } catch (Exception ignored) {
    }
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository, Mockito.times(0)).save(userArgumentCaptor.capture());
  }

  @Test
  void updateUserInvalidEmail_nullSaveNotCalled() {
    userJson.setEmail(null);
    try {
      userService.updateUser(userJson, 1L);
    } catch (Exception ignored) {
    }
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository, Mockito.times(0)).save(userArgumentCaptor.capture());
  }

  @Test
  void updateUserUnderageThrowsCorrectException() {
    LocalDate currentDate = LocalDate.now();
    currentDate = currentDate.minusYears(13);
    currentDate = currentDate.minusDays(1);
    userJson.setDateOfBirth(currentDate);
    try {
      userService.updateUser(userJson, 1L);
    } catch (BadRequestException e) {
      Assertions.assertEquals("Invalid E-mail", e.getMessage());
    }
  }

  @Test
  void updateUserUnderageSaveNotCalled() {
    LocalDate currentDate = LocalDate.now();
    currentDate = currentDate.minusYears(13);
    currentDate = currentDate.plusDays(1);
    userJson.setDateOfBirth(currentDate);
    try {
      userService.updateUser(userJson, 1L);
    } catch (Exception ignored) {
    }
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository, Mockito.times(0)).save(userArgumentCaptor.capture());
  }

  /*
   * Test make admin
   * Test saves correct user
   * Test throws ForbiddenException when done by non-admin
   *   Test correct message
   *   Test no call to userRepository.save()
   * Test Exception contains correct message
   * Test Non-existent user, throws NotFoundException
   *   Test no call to userRepository.save()
   * Test Save user who is DGAA, no save is called
   * Test Make user who is admin, no save is called
   */
  @Test
  void makeAdminCallsSavesUserRole() {
    User expectedUser = makeUser();
    expectedUser.setUserId(1L);
    expectedUser.setRole("global_admin");
    User admin = new User();
    admin.setRole("default_global_admin");
    admin.setUserId(2L);

    userService.makeAdmin(1L, admin);
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository, Mockito.times(1)).save(userArgumentCaptor.capture());
    Assertions.assertEquals(expectedUser.getRole(), userArgumentCaptor.getValue().getRole());
  }

  @Test
  void makeAdminDoneByUserThrowsForbiddenException() {
    User notAdmin = new User();
    notAdmin.setRole("user");
    notAdmin.setUserId(2L);

    Exception exception =
        Assertions.assertThrows(
            ForbiddenException.class,
            () -> {
              userService.makeAdmin(1L, notAdmin);
            });
    Assertions.assertEquals(
        "Current user is not Default Global Application Admin", exception.getMessage());
  }

  @Test
  void makeAdminDoneByUserNoCallToSave() {
    User notAdmin = new User();
    notAdmin.setRole("user");
    notAdmin.setUserId(2L);
    try {
      userService.makeAdmin(1L, notAdmin);
    } catch (Exception ignored) {
    }
    Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
  }

  @Test
  void makeAdminNonExistentUserThrowsNotFoundException() {
    User admin = new User();
    admin.setRole("default_global_admin");
    admin.setUserId(2L);
    Exception exception =
        Assertions.assertThrows(
            NoSuchElementException.class,
            () -> {
              userService.makeAdmin(3L, admin);
            });
    Assertions.assertEquals("No value present", exception.getMessage());
  }

  @Test
  void makeAdminNonExistentUserNoCallToSave() {
    User admin = new User();
    admin.setRole("default_global_admin");
    admin.setUserId(2L);
    try {
      userService.makeAdmin(3L, admin);
    } catch (Exception ignored) {
    }
    Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
  }

  @Test
  void makeAdminAdminNoCallToSave() {
    user.setRole("global_admin");
    User admin = new User();
    admin.setRole("default_global_admin");
    admin.setUserId(2L);
    userService.makeAdmin(1L, admin);
    Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
  }

  @Test
  void makeDGAADminNoCallToSave() {
    user.setRole("default_global_admin");
    userService.makeAdmin(1L, user);
    Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
  }

  /*
   * Test revoke admin
   * When done by DGAA, saves correct user
   * Test throws ForbiddenException when done by non-admin
   *   Test has correct message
   *   Test No call to userRepository.save()
   * Test Non-existent user throws not found exception
   *   Test has correct message
   *   Test No call to userRepository.save()
   */
  @Test
  void revokeAdminValid() {
    user.setRole("global_admin");

    User expectedUser = makeUser();
    expectedUser.setUserId(1L);
    expectedUser.setRole("user");

    User admin = new User();
    admin.setRole("default_global_admin");
    admin.setUserId(2L);

    userService.revokeAdmin(1L, admin);
    ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
    Mockito.verify(userRepository, Mockito.times(1)).save(userArgumentCaptor.capture());
    Assertions.assertEquals(expectedUser.getRole(), userArgumentCaptor.getValue().getRole());
  }

  @Test
  void revokeAdminNotAdminThrowsCorrectException() {
    user.setRole("global_admin");

    User notAdmin = new User();
    notAdmin.setRole("user");
    notAdmin.setUserId(2L);

    Exception exception =
        Assertions.assertThrows(
            ForbiddenException.class,
            () -> {
              userService.revokeAdmin(1L, notAdmin);
            });
    Assertions.assertEquals(
        "Current user is not Default Global Application Admin", exception.getMessage());
  }

  @Test
  void revokeAdminNotAdminNoCallToSave() {
    user.setRole("global_admin");

    User notAdmin = new User();
    notAdmin.setRole("user");
    notAdmin.setUserId(2L);

    try {
      userService.revokeAdmin(1L, notAdmin);
    } catch (Exception ignored) {
    }
    Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
  }

  @Test
  void revokeAdminDGAAThrowsConflictException() {
    user.setRole("default_global_admin");
    Exception exception =
        Assertions.assertThrows(
            ConflictException.class,
            () -> {
              userService.revokeAdmin(1L, user);
            });
    Assertions.assertEquals("DGAA cannot revoke their own status", exception.getMessage());
  }

  @Test
  void revokeAdminDGAANoCallToSave() {
    user.setRole("default_global_admin");
    try {
      userService.revokeAdmin(1L, user);
    } catch (Exception ignored) {
    }
    Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
  }

  @Test
  void getUserCardsById_withValidIdOneActiveCard_returnsActiveUserCards() {
    List<MarketplaceCard> cards = userService.getUserCardsById(1L);
    Assertions.assertEquals(1, cards.size());
  }

  @Test
  void getUserCardsById_withInvalidId_returnsNotAcceptable() {
    String message = "Cannot find cards for a user that doesn't exist";
    try {
      userService.getUserCardsById(12387L);
    } catch (NotAcceptableStatusException exception) {
      Assertions.assertTrue(exception.getMessage().contains(message));
    }
  }

  @Test
  void getUserCardsById_withValidIdOneInactiveCard_returnsZeroUserCards() {
    LocalDateTime currentDate = LocalDateTime.now();
    LocalDateTime displayEnd = currentDate.minusMonths(1L);
    card.setDisplayPeriodEnd(displayEnd);
    List<MarketplaceCard> expectedCards = new ArrayList<MarketplaceCard>() {{
      add(card);
    }};
    when(marketplaceCardRepository.findMarketplaceCardByCreatorUserIdAndDisplayPeriodEndAfter(Mockito.anyLong(), Mockito.any())).thenReturn(expectedCards);
    List<MarketplaceCard> cards = userService.getUserCardsById(1L);
    Assertions.assertEquals(expectedCards.size(), cards.size());
    }
}
