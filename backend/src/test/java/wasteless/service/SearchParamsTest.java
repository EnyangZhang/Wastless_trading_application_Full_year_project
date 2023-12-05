package wasteless.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wasteless.service.searching_service.SearchParamsParser;
import wasteless.service.searching_service.SearchToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

class SearchParamsTest {

  private String testString;
  private List<String> expected;
  private List<String> actual;

  public static Stream<Arguments> toTokens_inputString_expectedTokens() {
    return Stream.of(
        Arguments.of("John", Collections.singletonList(new SearchToken("John", false))),

        Arguments.of(
            "John Smith",
            Arrays.asList(new SearchToken("John", false), new SearchToken("Smith", false))),

        Arguments.of(
            "John                     Smith",
            Arrays.asList(new SearchToken("John", false), new SearchToken("Smith", false))),

        Arguments.of(
            "\"John Smith\"", Collections.singletonList(new SearchToken("John Smith", true))),

        Arguments.of(
            "Jack Frost \"John Smith\"",
            Arrays.asList(
                new SearchToken("Jack", false),
                new SearchToken("Frost", false),
                new SearchToken("John Smith", true))),

        Arguments.of(
            "\"John Smith\"Jack Frost",
            Arrays.asList(
                new SearchToken("John Smith", true),
                new SearchToken("Jack", false),
                new SearchToken("Frost", false))),

        Arguments.of(
            "\"John Smith\"\"Jack Frost\"",
            Arrays.asList(
                new SearchToken("John Smith", true), new SearchToken("Jack Frost", true))),

        Arguments.of(
            "\"John Smith\"JackFrost",
            Arrays.asList(
                new SearchToken("John Smith", true), new SearchToken("JackFrost", false))));
  }

  @ParameterizedTest
  @MethodSource
  void toTokens_inputString_expectedTokens(String input, List<String> expected) {
    List<SearchToken> result = SearchParamsParser.parse(input);
    Assertions.assertEquals(expected, result);
  }
}
