package wasteless.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wasteless.model.Searchable;
import wasteless.repository.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/UserSearchData.sql"})
@SpringBootTest
class SearchTest {

  @Autowired private UserService userService;

  @Autowired private UserRepository userRepository;

  private List<Long> expectedUserIds;
  private List<Long> actualUserIds;

  @BeforeEach
  void setUp() {
    expectedUserIds = new ArrayList<>();
    actualUserIds = new ArrayList<>();
  }

  @Test
  void testRepo() {
    long expectedId = 1;
    List<Searchable> users = userService.findUsers("David", 1, 1000, "default", "asc").getResult();
    Assertions.assertEquals(expectedId, users.get(0).getId());
  }

  // Find user David test centre but with weird conjunctions
  @Test
  void testBadConj() {
    expectedUserIds = Collections.singletonList(1L);
    List<Searchable> users =
        userService.findUsers("AND OR AND David test centre", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Find users that does not exist anywhere
  @Test
  void findNoUser() {
    expectedUserIds = Collections.emptyList();
    List<Searchable> users =
        userService.findUsers("Tai Sing Wei", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Find the users with Neil in their name
  @Test
  void findNeil() {
    expectedUserIds = Arrays.asList(3L, 2L);
    List<Searchable> users = userService.findUsers("Neil", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Test quotation marks matching
  @Test
  void findWuiWen() {
    expectedUserIds = Collections.singletonList(4L);
    List<Searchable> users =
        userService.findUsers("\"Wui Wen\"", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Find the users with John OR Neil in their name
  @Test
  void testConj() {
    expectedUserIds = Arrays.asList(3L, 5L, 6L, 2L, 15L);
    List<Searchable> users =
        userService.findUsers("John OR Neil", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Search params "Chin Wui" tests the quotation marks matching
  @Test
  void findWithQuotationMarks() {
    expectedUserIds = Arrays.asList(4L);
    List<Searchable> users =
        userService.findUsers("\"Chin Wui\"", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Tests the results of the above, if there were no quotation marks
  @Test
  void findWithoutQuotationMarks() {
    expectedUserIds = Arrays.asList(7L, 4L);
    List<Searchable> users = userService.findUsers("Chin Wui", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Tests with lots of logical conjunctions
  // (((Dave OR Smith) OR Chin) And Wen) AND Tai)
  // Finds the user with Wen and Tai in their name, provided they are also called Dave, Smith or
  // Chin
  @Test
  void findWith2ORs() {
    expectedUserIds = Arrays.asList(7L, 8L);
    List<Searchable> users =
        userService
            .findUsers("dave OR Smith OR Chin AND Wen AND Tai", 1, 1000, "default", "asc")
            .getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Same as above test, but with broken logical conjunctions
  @Test
  void findWithBadConj() {
    expectedUserIds = Arrays.asList(7L, 8L);
    List<Searchable> users =
        userService
            .findUsers("dave OR OR OR Smith OR Chin AND AND Wen AND Tai", 1, 1000, "default", "asc")
            .getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findWithBadConj2() {
    expectedUserIds = Arrays.asList(7L, 8L);
    List<Searchable> users =
        userService
            .findUsers("AND dave OR Smith OR Chin AND Wen AND Tai OR OR", 1, 1000, "default", "asc")
            .getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // ((John OR William) AND Smith) OR Henry
  // Returns any user with Henry, or users with Smith provided they are also called John or William
  @Test
  void findWithMixedORs() {
    expectedUserIds = Arrays.asList(5L, 6L, 11L, 13L, 14L);
    List<Searchable> users =
        userService
            .findUsers("John OR William AND Smith OR Henry", 1, 1000, "default", "asc")
            .getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // (((William OR Mei) OR Wen) AND Smith)
  // Returns any user named Smith, provided they are also called William, Mei or Wen
  // Returns Tai Wen Mei Smith, William Parker Smith, Ru MeiTest Smith, Smith Ru Mei Smith, but not
  // John Smith
  @Test
  void findWithMixedORs2() {
    expectedUserIds = Arrays.asList(8L, 13L, 10L, 12L);
    List<Searchable> users =
        userService
            .findUsers("William OR Mei OR Wen AND Smith", 1, 1000, "default", "asc")
            .getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Same test as above, but testing the ordering of results
  @Test
  void findWithMixedORsOrdering() {
    expectedUserIds = Arrays.asList(8L, 13L, 10L, 12L);
    List<Searchable> users =
        userService
            .findUsers("Mei OR William OR Wen AND Smith", 1, 1000, "default", "asc")
            .getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Test with mixed logical conjunctions
  // ((Dave OR Chin) AND Wen) OR Test
  @Test
  void findWithORsAndANDs() {
    expectedUserIds = Arrays.asList(1L, 7L, 10L, 4L);
    List<Searchable> users =
        userService.findUsers("dave OR Chin AND Wen OR Test", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Test quotation marks match the exact name
  // Finds user Mei Ru, but no user called "Ru Mei"
  @Test
  void findMeiRu() {
    expectedUserIds = Collections.singletonList(9L);
    List<Searchable> users =
        userService.findUsers("\"Mei Ru\"", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Tests the opposite of above: returns all users with either Ru and Mei in their name
  @Test
  void findMeiRuAndRuMei() {
    expectedUserIds = Arrays.asList(9L, 11L, 10L, 12L);
    List<Searchable> users = userService.findUsers("Ru Mei", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Test if the exact match shows up before the partial match?
  // Finds user "Ru Mei", results show up in order of match: Ru Mei Henry, Smith Ru Mei Smith, Ru
  // MeiTest Smith
  @Test
  void findRuMeiOrder() {
    expectedUserIds = Arrays.asList(11L, 12L);
    List<Searchable> users =
        userService.findUsers("\"Ru Mei\"", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Tests the precedence of result matching.
  @Test
  void findKelsi() {
    expectedUserIds = Arrays.asList(17L, 18L, 16L);
    List<Searchable> users = userService.findUsers("Kelsi", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Tests the precedence of result matching again
  @Test
  void findKelsi2() {
    expectedUserIds = Arrays.asList(17L, 16L);
    List<Searchable> users =
        userService
            .findUsers("Pugh AND Haris OR Lloyd AND Kelsi", 1, 1000, "default", "asc")
            .getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Tests that extra whitespace between words are ignored
  @Test
  void findKelsiWithBadWhiteSpace() {
    expectedUserIds = Arrays.asList(18L);
    List<Searchable> users =
        userService.findUsers("Kelsi     AND Saunders", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Check that the search function returns every user when given an empty query
  @Test
  void testEmptyQuery() {
    List<Searchable> users = userService.findUsers("", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(userRepository.findAll().size(), actualUserIds.size());
  }

  // Find the users with John OR Neil in their name
  @Test
  void testSimpleOr() {
    expectedUserIds = Arrays.asList(3L, 5L, 6L, 2L, 15L);
    List<Searchable> users =
        userService.findUsers("John OR Neil", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Find the users with John or Neil in their name
  @Test
  void findUsers_lowerCaseOr_FindsCorrectUsers() {
    expectedUserIds = Arrays.asList(3L, 5L, 6L, 2L, 15L);
    List<Searchable> users =
        userService.findUsers("John or Neil", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }//

  // Find the user who matches David AND Test
  @Test
  void testSimpleAnd() {
    expectedUserIds = Collections.singletonList(1L);
    List<Searchable> users =
        userService.findUsers("David AND Test", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Find the user who matches David and Test
  @Test
  void findUsers_lowerCaseAnd_FindsCorrectUsers() {
    expectedUserIds = Collections.singletonList(1L);
    List<Searchable> users =
        userService.findUsers("David and Test", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Find the user David Test with repetition appropriately ignored
  @Test
  void testRepeatName() {
    expectedUserIds = Collections.singletonList(1L);
    List<Searchable> users =
        userService.findUsers("David OR David OR David Test", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Fail to return any users when two different users first and last names are combined
  @Test
  void testBadAnd() {
    expectedUserIds = Collections.emptyList();
    List<Searchable> users =
        userService.findUsers("John AND Test", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Fail to find user David Test when entered with incorrect whitespace
  @Test
  void testBadWhitespace() {
    expectedUserIds = Collections.emptyList();
    List<Searchable> users =
        userService.findUsers("DavidTest", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  // Test finding user with AND in their name works when using quotes
  @Test
  void findUsers_AndInQuotes_FindsName() {
    expectedUserIds = Collections.singletonList(3L);
    List<Searchable> users =
        userService.findUsers("\"Angus AND\"", 1, 1000, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_3PerPage_find3OutOf5() {
    expectedUserIds = Arrays.asList(3L, 5L, 6L);
    List<Searchable> users =
            userService.findUsers("John or Neil", 1, 3, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_page2and3PerPage_find2OutOf5() {
    expectedUserIds = Arrays.asList(2L, 15L);
    List<Searchable> users =
            userService.findUsers("John or Neil", 2, 3, "default", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_sortByFirstNameOrderByAsc_findNameInCorrectOrder() {
    expectedUserIds = Arrays.asList(3L, 5L, 6L, 2L, 15L);
    List<Searchable> users =
            userService.findUsers("John or Neil", 1, 1000, "firstName", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_sortByFirstNameOrderByDesc_findNameInCorrectOrder() {
    expectedUserIds = Arrays.asList(15L, 2L, 5L, 6L, 3L);
    List<Searchable> users =
            userService.findUsers("John or Neil", 1, 1000, "firstName", "desc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_sortByMiddleNameOrderByAsc_findNameInCorrectOrder() {
    expectedUserIds = Arrays.asList(15L, 2L, 3L, 5L, 6L);
    List<Searchable> users =
            userService.findUsers("John or Neil", 1, 1000, "middleName", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_sortByMiddleNameOrderByDesc_findNameInCorrectOrder() {
    expectedUserIds = Arrays.asList(6L, 5L, 2L, 3L, 15L);
    List<Searchable> users =
            userService.findUsers("John or Neil", 1, 1000, "middleName", "desc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_sortByLastNameOrderByAsc_findNameInCorrectOrder() {
    expectedUserIds = Arrays.asList(2L, 5L, 15L, 3L, 6L);
    List<Searchable> users =
            userService.findUsers("John or Neil", 1, 1000, "lastName", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_sortByLastNameOrderByDesc_findNameInCorrectOrder() {
    expectedUserIds = Arrays.asList(6L, 3L, 15L, 5L, 2L);
    List<Searchable> users =
            userService.findUsers("John or Neil", 1, 1000, "lastName", "desc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_sortByNicknameOrderByAsc_findNameInCorrectOrder() {
    expectedUserIds = Arrays.asList(15L, 3L, 6L, 5L, 2L);
    List<Searchable> users =
            userService.findUsers("John or Neil", 1, 1000, "nickname", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_sortByNicknameOrderByDesc_findNameInCorrectOrder() {
    expectedUserIds = Arrays.asList(2L, 5L, 6L, 3L, 15L);
    List<Searchable> users =
            userService.findUsers("John or Neil", 1, 1000, "nickname", "desc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_sortByEmailOrderByAsc_findNameInCorrectOrder() {
    expectedUserIds = Arrays.asList(3L, 5L, 6L, 2L, 15L);
    List<Searchable> users =
            userService.findUsers("John or Neil", 1, 1000, "email", "asc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_sortByEmailOrderByDesc_findNameInCorrectOrder() {
    expectedUserIds = Arrays.asList(15L, 2L, 6L, 5L, 3L);
    List<Searchable> users =
            userService.findUsers("John or Neil", 1, 1000, "email", "desc").getResult();
    for (Searchable user : users) {
      actualUserIds.add(user.getId());
    }
    Assertions.assertEquals(expectedUserIds, actualUserIds);
  }

  @Test
  void findUsers_normalSearch_correctItemsLength() {
    long resultsLength =
            userService.findUsers("John or Neil", 1, 1000, "email", "desc").getResultsLength();

    Assertions.assertEquals(5, resultsLength);
  }
}
