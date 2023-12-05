package wasteless.service.searching_service;

import lombok.Getter;
import wasteless.model.Searchable;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.List;

/**
 * Abstract searching service that defines a find method for classes to implement.
 * Used by classes to query a repository of objects that implement Searchable, such as User or Business,
 * by passing in a list of parameters.
 */
public abstract class SearchingService {
    public abstract SearchResult find(List<SearchToken> parameters, long pageNum, long perPage, String sortBy, String orderBy);

    /**
     * Constructs two main predicates: - Exact matches - Partial matches For each token, a new
     * predicate within the loop is created and joined to the main predicates. The conjunction it is
     * joined by is determined by the state: if the state is 1, it is joined with AND. If it is 0,
     * it is joined with OR.
     *
     * <p>By default, the state is 0. The local variable state is initialized to 0. If the current *
     * token matches "OR" or "AND" (case-sensitive), then it is consumed and the state is set *
     * according to the state rules mentioned. At the end of each loop, the state is reset to 0.
     *
     * @param cb Criteria Builder.
     * @param parameters The list of tokens.
     * @param exactMatch Boolean to tell method whether it is a exact match or partial match.
     * @return Processed match.
     */
    protected Predicate constructPredicates(CriteriaBuilder cb, List<SearchToken> parameters, Boolean exactMatch) {
        int state = 0;

        Predicate match = cb.conjunction();

        for (SearchToken token : parameters) {

            String lowerCaseToken =
                    token.getTerm().toLowerCase(); // Converts token to lowercase (for use in case-insensitive queries)

            if (lowerCaseToken.equals("or") && Boolean.FALSE.equals(token.inQuotes())) {
                // If token is OR, consume and set state to 1
                state = 1;
            } else if (lowerCaseToken.equals("and") && Boolean.FALSE.equals(token.inQuotes())) {
                // If token is AND, consume and set state to 0
                state = 0;
            } else {
                Predicate predicate = getPredicate(exactMatch, token, lowerCaseToken);

                // Joins predicate to previous predicate with conjunction, based on the state set by the
                // previous token
                if (state == 1) {
                    match = cb.or(match, predicate);
                } else {
                    match = cb.and(match, predicate);
                }
                // Resets state back to 0, as that is the assumed state when no logical operative is given
                state = 0; 
            }
        }
        return match;
    }

    /**
     * Calls abstract exactMatchPredicate or partialMatchPredicate depending on if the function is set to exact match.
     * If the token is in quotes, always calls exactMatchPredicate.
     *
     * @param exactMatch Boolean variable defining if normal tokens are to be matched partially or exactly.
     * @param token Instance of SearchToken.
     * @param lowerCaseToken Contents of SearchToken, converted to lowercase
     * @return Predicate set by return value of exactMatchPredicate() or partialMatchPredicate()
     */
    private Predicate getPredicate(Boolean exactMatch, SearchToken token, String lowerCaseToken) {
        Predicate predicate;
        if (Boolean.TRUE.equals(exactMatch) || Boolean.TRUE.equals(token.inQuotes())){
            predicate = exactMatchPredicate(lowerCaseToken);
        } else {
            predicate = partialMatchPredicate(lowerCaseToken);
        }
        return predicate;
    }

    /**
     * Defines a static function for matching the token to attribute values exactly.
     * Which attributes are matched are delegated to the child classes to implement.
     *
     * @param lowerCaseToken Search token, in lowercase to match.
     * @return Instance of Predicate, defining exact match conditions for query.
     */
    protected abstract Predicate exactMatchPredicate(String lowerCaseToken);

    /**
     * Defines a static function for matching the token to attribute values partially.
     * Which attributes are matched are delegated to the child classes to implement.
     *
     * @param lowerCaseToken Search token, in lowercase to match.
     * @return Instance of Predicate, defining partial match conditions for query.
     */
    protected abstract Predicate partialMatchPredicate(String lowerCaseToken);

    /**
     * Companion class for controller to retrieve searching result and resultsLength.
     * */
    @Getter
    public static class SearchResult {
        private final List<Searchable> result;
        private final long resultsLength;

        public SearchResult(List<Searchable> result, long resultsLength){
            this.result = result;
            this.resultsLength = resultsLength;
        }
    }
}
