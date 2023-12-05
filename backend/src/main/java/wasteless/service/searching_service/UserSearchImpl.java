package wasteless.service.searching_service;

import org.springframework.stereotype.Component;
import wasteless.model.Searchable;
import wasteless.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * UserRepositorySearchImpl implements UserRepositorySearch interface, which defines the method
 * find. This class implements the function to search a repository for users given an ordered
 * list of strings called tokens. It does so by constructing a query that selects from the user
 * table given a set of criteria joined by conjunctions OR and AND.
 */
@Component
public class UserSearchImpl extends SearchingService {

  /** Entity manager object */
  @PersistenceContext private EntityManager entityManager;

  /**
   * Criteria builder. This object calls methods such as like, or, and, orderBy, etc to construct
   * queries.
   */
  private CriteriaBuilder cb;

  /**
   * The root where queries search for results. The path to columns and its values can be specified
   * using .get
   */
  private Root<User> user;

  private final String[] sortOption = new String[] {"nickname", "firstName", "middleName", "lastName", "email", "role"};

  private long pageNum;
  private long perPage;

  private static class Attribute {
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";
    private static final String MIDDLE_NAME = "middleName";
    private static final String USER_ID = "userId";
    private static final String NICKNAME = "nickname";
  }

  /**
   * Main search method. Given the list of tokens, it runs through each token and constructs two
   * main predicates: - Exact matches - Partial matches For each token, a new predicate within the
   * loop is created and joined to the main predicates. The conjunction it is joined by is
   * determined by the state: if the state is 1, it is joined with AND. If it is 0, it is joined
   * with OR.
   *
   * <p>Once the list of tokens is exhausted, the query runs once for each main predicate and two
   * lists are created. The contents of both lists are combined (eliminating duplicate values) and
   * the resulting list of Users is returned. This is done so that users whose names match the
   * tokens exactly are at the top of the list.
   *
   * @param parameters The list of tokens.
   * @param pageNum The current page that frontend will display.
   * @param perPage Total items each page will display.
   * @param sortBy The field name that needs to be sorted.
   * @param orderBy The order in asc or desc.
   * @return The combined list of Users found with the query.
   */
  @Override
  public SearchResult find(List<SearchToken> parameters, long pageNum, long perPage, String sortBy, String orderBy) {
    this.pageNum = pageNum;
    this.perPage = perPage;

    cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<User> query = cb.createQuery(User.class);
    user = query.from(User.class);

    CriteriaBuilder countBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = countBuilder.createQuery(Long.class);
    Root<User> countRoot = countQuery.from(User.class);

    Predicate exactMatch = constructPredicates(cb, parameters, true);
    Predicate partialMatch = constructPredicates(cb, parameters, false);
    Predicate count = constructPredicates(cb, parameters, false);

    List<Searchable> exactResults =  getSearchResults(query, exactMatch, sortBy, orderBy);
    List<Searchable> partialResults = getSearchResults(query, partialMatch, sortBy, orderBy);
    long resultsLength = entityManager.createQuery(countQuery.select(countBuilder.count(countRoot)).where(count)).getSingleResult();

    if(Arrays.asList(sortOption).contains(sortBy)){
      return new SearchResult(partialResults, resultsLength);
    } else {
      return new SearchResult(SearchUtils.joinTwoLists(exactResults, partialResults), resultsLength);
    }
  }

  /**
   * Runs the query to select user based on given predicate and returns the results.
   * @param query CriteriaQuery used to select users from database.
   * @param predicate Predicate instance defining criteria to select users by.
   * @param sortBy The field name that needs to be sorted.
   * @param orderBy The order in asc or desc.
   * @return List of selected Users
   */
  private List<Searchable> getSearchResults(CriteriaQuery<User> query, Predicate predicate, String sortBy, String orderBy) {
    TypedQuery<User> typedQuery;
    if (Arrays.asList(sortOption).contains(sortBy)){
      if (orderBy.equals("desc")){
        typedQuery = setPagination(entityManager.createQuery(query.select(user).where(predicate)
                .orderBy(cb.desc(cb.lower(user.get(sortBy))), cb.asc(cb.lower(user.get(Attribute.USER_ID))))));
      } else {
        typedQuery = setPagination(entityManager.createQuery(query.select(user).where(predicate)
                .orderBy(cb.asc(cb.lower(user.get(sortBy))), cb.asc(cb.lower(user.get(Attribute.USER_ID))))));
      }
    } else {
      typedQuery = setPagination(entityManager.createQuery(query.select(user).where(predicate)
              .orderBy(cb.asc(cb.lower(user.get(Attribute.FIRST_NAME))), cb.asc(cb.lower(user.get(Attribute.USER_ID))))));
    }
    return new ArrayList<>(typedQuery.getResultList());
  }

  /**
   * Sets typedQuery first result and max result for pagination.
   * @param paginationQuery TypedQuery to set first result and max result for pagination.
   * @return TypedQuery with first result and max result set.
   */
  private TypedQuery<User> setPagination (TypedQuery<User> paginationQuery){
    long firstResultIndex = (pageNum - 1) * perPage;
    paginationQuery.setFirstResult((int) firstResultIndex);
    paginationQuery.setMaxResults((int) perPage);
    return paginationQuery;
  }

  /**
   * Constructs a predicate to match the user's name exactly; first name, last name and middle name.
   *
   * @param lowerCaseToken A token (converted to lower case for case-insensitivity) for matching
   * @return the constructed predicate
   */
  protected Predicate exactMatchPredicate(String lowerCaseToken) {
    // First name
    Path<String> firstNamePath = user.get(Attribute.FIRST_NAME);
    Predicate p2 = cb.like(cb.lower(firstNamePath), lowerCaseToken);

    // Middle name
    Path<String> middleNamePath = user.get(Attribute.MIDDLE_NAME);
    p2 = cb.or(p2, cb.like(cb.lower(middleNamePath), lowerCaseToken));

    // Last name
    Path<String> lastNamePath = user.get(Attribute.LAST_NAME);
    p2 = cb.or(p2, cb.like(cb.lower(lastNamePath), lowerCaseToken));

    // Nickname
    Path<String> nicknamePath = user.get(Attribute.NICKNAME);
    p2 = cb.or(p2, cb.like(cb.lower(nicknamePath), lowerCaseToken));

    return p2;
  }

  /**
   * Constructs a predicate to match the user's name partially; first name, last name and middle
   * name
   *
   * @param lowerCaseToken A token (converted to lower case for case-insensitivity) for matching
   * @return the constructed predicate
   */
  protected Predicate partialMatchPredicate(String lowerCaseToken) {
    // First name
    Path<String> firstNamePath = user.get(Attribute.FIRST_NAME);
    Predicate p2 = cb.like(cb.lower(firstNamePath), "%" + lowerCaseToken + "%");

    // Middle name
    Path<String> middleNamePath = user.get(Attribute.MIDDLE_NAME);
    p2 = cb.or(p2, cb.like(cb.lower(middleNamePath), "%" + lowerCaseToken + "%"));

    // Last name
    Path<String> lastNamePath = user.get(Attribute.LAST_NAME);
    p2 = cb.or(p2, cb.like(cb.lower(lastNamePath), "%" + lowerCaseToken + "%"));

    // Nickname
    Path<String> nicknamePath = user.get(Attribute.NICKNAME);
    p2 = cb.or(p2, cb.like(cb.lower(nicknamePath), "%" + lowerCaseToken + "%"));
    return p2;
  }
}
