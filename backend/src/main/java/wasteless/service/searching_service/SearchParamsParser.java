package wasteless.service.searching_service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchParamsParser {

  /**
   * This method splits the search parameters into tokens. It matches a given string to a regular
   * expression that expects: - [^"] Any string of characters that do not start with a quotation
   * mark - \s followed by a string of non-space characters - |".+?" or a string of characters
   * enclosed in quotation marks
   *
   * <p>(Although it is called a parser, its purpose is actually that of a tokenizer's)
   *
   * @param string The search parameters
   * @return A List of tokens
   */
  public static List<SearchToken> parse(String string) {
    List<SearchToken> searchParams = new ArrayList<>();
    Matcher matcher = Pattern.compile("([^\"]\\S*|\"[^\"]++\")\\s*").matcher(string);
    while (matcher.find()) {
      String term = matcher.group(1);
      Boolean inQuotes = term.matches("\".*\"");
      term = term.replace("\"", ""); // Removes the quotation marks on tokens separated by quotation marks
      if (!term.isBlank()) {
        searchParams.add(new SearchToken(term, inQuotes));
      }
    }
    return searchParams;
  }

  private SearchParamsParser() {
    throw new IllegalStateException("Utility Class");
  }
}
