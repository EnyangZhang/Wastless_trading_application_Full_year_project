package wasteless.service.searching_service;

import wasteless.model.Business;
import wasteless.model.Searchable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BusinessRepositorySearchImpl implements BusinessRepositorySearch interface, which defines the method
 * find. This class implements the function to search a repository for businesses given an ordered
 * list of strings called tokens. It does so by constructing a query that selects from the business
 * table given a set of criteria joined by conjunctions OR and AND.
 */
public class BusinessSearchImpl extends SearchingService {
    /** Entity manager object */
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Criteria builder. This object calls methods such as like, or, and, orderBy, etc to construct
     * queries.
     */
    private CriteriaBuilder cb;

    /**
     * The root where queries search for results. The path to columns and its values can be specified
     * using .get
     */
    private Root<Business> business;

    /**
     * wasteless.Main search method. Given the list of tokens, it runs through each token and calls constructPredicates
     * to create predicates.
     *
     * <p>Once the list of tokens is exhausted, the query runs once for each main predicate and two
     * lists are created. The contents of both lists are combined (eliminating duplicate values) and
     * the resulting list of Businesses is returned. This is done so that businesses whose names match the
     * tokens exactly are at the top of the list.
     *
     * @param parameters The list of tokens.
     * @return The combined list of Businesses found with the query.
     */
    @Override
    public SearchResult find(List<SearchToken> parameters, long pageNum, long perPage, String sortBy, String orderBy) {
        cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Business> query = cb.createQuery(Business.class);
        business = query.from(Business.class);

        Predicate partialMatch = constructPredicates(cb, parameters, false);
        Predicate exactMatch = constructPredicates(cb, parameters, true);

        // Runs query and returns result
        query.select(business).where(exactMatch);

        List<Searchable> results = new ArrayList<>(entityManager.createQuery(query).getResultList());

        query.select(business).where(partialMatch);

        List<Searchable> results2 = new ArrayList<>(entityManager.createQuery(query).getResultList());

        return new SearchResult(SearchUtils.joinTwoLists(results, results2), 0);

    }

    /**
     * Constructs a predicate to match the business's name exactly.
     *
     * @param lowerCaseToken A token (converted to lower case for case-insensitivity) for matching
     * @return the constructed predicate
     */
    protected Predicate exactMatchPredicate(String lowerCaseToken) {
        Path<String> businessnamePath = business.get("name");

        return cb.like(cb.lower(businessnamePath), lowerCaseToken);
    }

    /**
     * Constructs a predicate to match the business's name partially.
     *
     * @param lowerCaseToken A token (converted to lower case for case-insensitivity) for matching
     * @return the constructed predicate
     */
    protected Predicate partialMatchPredicate(String lowerCaseToken) {
        Path<String> businessNamePath = business.get("name");
        return cb.like(cb.lower(businessNamePath), "%" + lowerCaseToken + "%");
    }

}
