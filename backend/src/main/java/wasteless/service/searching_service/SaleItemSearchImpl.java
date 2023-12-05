package wasteless.service.searching_service;

import org.springframework.stereotype.Component;
import wasteless.controller.SaleItemController;
import wasteless.model.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Child class of SaleItemSearchService, which is an abstract child class of SearchingService.
 * Implements the static methods required by SearchingService and SaleItemSearchingService.
 *
 * Implementation uses the Criteria API to construct a query to the SaleItem root, using the constructPredicate method
 * implemented in parent class SearchingService to obtain matching predicates. Implements partial and exact match
 * predicates defined by SearchingService.
 */
@Component
public class SaleItemSearchImpl extends SaleItemSearchService {

    @PersistenceContext
    private EntityManager entityManager;

    CriteriaBuilder cb;
    CriteriaBuilder cbCount;

    private Integer pageNum;
    private Integer perPage;

    private Root<SaleItem> saleRoot;
    private Join<SaleItem, Product> productJoin;
    private Join<SaleItem, InventoryItem> inventoryJoin;
    private Join<SaleItem, Business> businessJoin;
    private Join<SaleItem, Address> addressJoin;

    private static class Attribute {
        private static final String INVENTORY_ITEM = "inventoryItem";
        private static final String PRODUCT = "product";
        private static final String BUSINESS = "business";
        private static final String ADDRESS = "address";
        private static final String BUSINESS_NAME = "name";
        private static final String PRODUCT_NAME = "name";
        private static final String SUBURB = "suburb";
        private static final String CITY = "city";
        private static final String REGION = "region";
        private static final String COUNTRY = "country";
        private static final String EXPIRY_DATE = "expires";
        private static final String PRICE = "price";
        private static final String QUANTITY = "quantity";
        private static final String CLOSING_DATE = "closes";
        private static final String BUSINESS_TYPE = "businessType";
        private static final String SOLD = "isSold";
    }

    private static class SortOptions {
        private static final String PRODUCT_NAME = "name";
        private static final String BUSINESS_NAME = "seller";
        private static final String SUBURB = "suburb";
        private static final String CITY = "city";
        private static final String COUNTRY = "country";
        private static final String SALE_ITEM_ID = "saleItemId";
        private static final String CREATED_DATE = "created";
        private static final String PRICE = "price";
        private static final String EXPIRY_DATE = "expires";
        private static final String QUANTITY = "quantity";
    }

    private static class Ordering {
        private static final String ASCENDING = "asc";
    }

    /**
     * Sets the root constants to derive from the given query.
     * Not ideal, should look at fixing, but due to time constraints in sprint 5 may have to do.
     * @param query Parameterized instance of CriteriaQuery, can be of type SaleItem or Long, depending on querying
     *              for SaleItem results, or the length of the results.
     */
    private void setRoots(CriteriaQuery<?> query) {
        saleRoot = query.from(SaleItem.class);
        inventoryJoin = saleRoot
                .join(Attribute.INVENTORY_ITEM);
        productJoin = saleRoot
                .join(Attribute.INVENTORY_ITEM)
                .join(Attribute.PRODUCT);
        businessJoin = saleRoot
                .join(Attribute.INVENTORY_ITEM)
                .join(Attribute.BUSINESS);
        addressJoin = saleRoot
                .join(Attribute.INVENTORY_ITEM)
                .join(Attribute.BUSINESS)
                .join(Attribute.ADDRESS);
    }

    /**
     * Implementation of static find method defined by parent class.
     * Assembles the Criteria API components (builder, query, root) and constructs predicates using the inherited
     * constructPredicates method.
     *
     * Obtains the results using the constructed query and returns them.
     * @param parameters A list of search tokens; the search parameters.
     * @param filterQuery A class containing the information to filter the results by.
     * @param pageNum Starting page for pagination.
     * @param perPage Defines how many items in the results are allowed per page.
     * @param sortBy Defines the column to sort results by.
     * @param orderBy Defines the ordering results are returned in.
     * @return A list of SearchResult objects, containing a list of paginated results
     *          and the total length of the unpaginated results.
     */
    public SearchResult find(List<SearchToken> parameters,
                             SaleItemController.FilterQuery filterQuery,
                             Integer pageNum,
                             Integer perPage,
                             String sortBy,
                             String orderBy) {
        this.pageNum = pageNum;
        this.perPage = perPage;

        cb = entityManager.getCriteriaBuilder();
        cbCount = entityManager.getCriteriaBuilder();

        CriteriaQuery<SaleItem> saleQuery = cb.createQuery(SaleItem.class);
        setRoots(saleQuery);

        Predicate filtersPredicate = filtersPredicate(filterQuery);

        // Get Predicates and results, it might be possible that this is order-dependent to be before Get Count stage
        Predicate exactMatch = constructPredicates(cb, parameters, true);
        Predicate partialMatch = constructPredicates(cb, parameters, false);

        exactMatch = cb.and(exactMatch, filtersPredicate);
        partialMatch = cb.and(partialMatch, filtersPredicate);

        List<Searchable> exactResults =  getSearchResults(saleQuery, exactMatch, sortBy, orderBy);
        List<Searchable> partialResults = getSearchResults(saleQuery, partialMatch, sortBy, orderBy);

        // Get count
        CriteriaQuery<Long> countQuery = cbCount.createQuery(Long.class);
        setRoots(countQuery);

        Predicate countMatch = constructPredicates(cbCount, parameters, false);
        Predicate countFilterPredicate = filtersPredicate(filterQuery);
        countMatch = cbCount.and(countMatch, countFilterPredicate);

        // At this point saleRoot is taken from countQuery, so we use the same variable
        Long countResult = entityManager.createQuery(countQuery.select(cbCount.count(saleRoot)).where(countMatch))
                .getSingleResult();

        return new SearchResult(SearchUtils.joinTwoLists(exactResults, partialResults), countResult);
    }

    /**
     * Applies sorting, ordering and pagination. Uses entity manager to create query and gets the list of results.
     * @param query Instance of CriteriaQuery to apply sorting, ordering and pagination to.
     * @param predicate Predicate conditions to retrieve results by.
     * @param sortBy Column to sort results by.
     * @param orderBy Ordering of results (asc, or desc)
     * @return ArrayList of type Searchable; the list of results retrieved by the assembled query.
     */
    public List<Searchable> getSearchResults(CriteriaQuery<SaleItem> query, Predicate predicate, String sortBy, String orderBy) {
        TypedQuery<SaleItem> typedQuery;
        CriteriaQuery<SaleItem> resultsQuery = query.select(saleRoot).where(predicate);
        Expression<String> orderingPath = getOrderingPath(sortBy);
        setResultOrdering(orderBy, resultsQuery, orderingPath);
        typedQuery = setPagination(entityManager.createQuery(resultsQuery));
        return new ArrayList<>(typedQuery.getResultList());
    }

    /**
     * A conditional statement that applies a different sorting order to the criteria query depending on the parameter.
     * @param orderBy A string defining the ordering of results. This is compared to the constant in the module,
     *                and the appropriate ordering is applied.
     * @param resultsQuery Instance of Criteria Query. Depending on the given ordering,
     * @param orderingPath An expression defining the path to order the results by. Applied directly to query (lowercase)
     */
    private void setResultOrdering(String orderBy, CriteriaQuery<SaleItem> resultsQuery, Expression<String> orderingPath) {
        if (orderBy.equals(Ordering.ASCENDING)) {
            resultsQuery.orderBy(
                    cb.asc(orderingPath),
                    cb.asc(cb.lower(saleRoot.get(SortOptions.SALE_ITEM_ID)))
            );
        } else {
            resultsQuery.orderBy(
                    cb.desc(orderingPath),
                    cb.asc(cb.lower(saleRoot.get(SortOptions.SALE_ITEM_ID)))
            );
        }
    }

    /**
     * A conditional statement obtains a Path from different roots depending on the case.
     * If no matches to valid sorting options are found, the default ordering path is set to the creation date of
     * each sale listing.
     * @param sortBy A string defining the ordering. It is compared to the valid options in a switch statement.
     * @return A String instance of Expression, that defines the column that results are to be sorted by.
     */
    private Expression<String> getOrderingPath(String sortBy) {
        Expression<String> orderingPath;
        switch (sortBy) {
            case SortOptions.PRODUCT_NAME:
                orderingPath = cb.lower(productJoin.get(Attribute.PRODUCT_NAME));
                break;
            case SortOptions.SUBURB:
                orderingPath = cb.lower(addressJoin.get(Attribute.SUBURB));
                break;
            case SortOptions.CITY:
                orderingPath = cb.lower(addressJoin.get(Attribute.CITY));
                break;
            case SortOptions.COUNTRY:
                orderingPath = cb.lower(addressJoin.get(Attribute.COUNTRY));
                break;
            case SortOptions.PRICE:
                orderingPath = saleRoot.get(Attribute.PRICE);
                break;
            case SortOptions.QUANTITY:
                orderingPath = saleRoot.get(Attribute.QUANTITY);
                break;
            case SortOptions.EXPIRY_DATE:
                orderingPath = inventoryJoin.get(Attribute.EXPIRY_DATE);
                break;
            case SortOptions.BUSINESS_NAME:
                orderingPath = cb.lower(businessJoin.get(Attribute.BUSINESS_NAME));
                break;
            default:
                orderingPath = saleRoot.get(SortOptions.CREATED_DATE);
        }
        return orderingPath;
    }

    /**
     * Sets the typedQuery's first result and maximum result to paginate the selected rows when the results are obtained
     * from the query.
     * The perPage and pageNum variables are obtained as parameters from the initial find() call.
     *
     * Pagination starts as 0, but page number from the client-side starts at 1. Hence, this function subtracts 1 from
     * the pageNum before setting the first result property of query. It assumes that the client will always send
     * a positive, non-zero value as the page number.
     *
     * @param query Instance of TypedQuery to set the first result and max result properties.
     * @return SaleItem instance of TypedQuery, with pagination properties pageNum and perPage applied for pagination.
     */
    private TypedQuery<SaleItem> setPagination(TypedQuery<SaleItem> query) {
        int firstResultIndex = (pageNum - 1) * perPage;
        query.setFirstResult(firstResultIndex);
        query.setMaxResults(perPage);
        return query;
    }

    /**
     * Implementation of abstract exactMatchPredicate defined in parent class.
     * Constructs specific predicates for each possible property that can be searched:
     *  - seller business name
     *  - product name
     *  - business address (suburb)
     *  - business address (city)
     *  - business address (region)
     *  - business address (country)
     *
     *  Paths are obtained from specific roots and joins. It assumes that roots and joins are consistent with each other
     *  i.e. They are derived from the same set of criteria builder and query.
     *
     *  lowerCaseTokens are matched to lowercase path, so matches are not case sensitive. The path must match the
     *  token exactly.
     *
     * @param lowerCaseToken A string containing lower-case characters.
     * @return A conjoined Predicate of conditions.
     */
    protected Predicate exactMatchPredicate(String lowerCaseToken) {
        Path<String> businessName = businessJoin.get(Attribute.BUSINESS_NAME);
        Predicate exactPredicate = cb.like(cb.lower(businessName), lowerCaseToken);

        Path<String> productName = productJoin.get(Attribute.PRODUCT_NAME);
        exactPredicate = cb.or(exactPredicate, cb.like(cb.lower(productName), lowerCaseToken));

        Path<String> addressSuburb = addressJoin.get(Attribute.SUBURB);
        exactPredicate = cb.or(exactPredicate, cb.like(cb.lower(addressSuburb), lowerCaseToken));

        Path<String> addressCity = addressJoin.get(Attribute.CITY);
        exactPredicate = cb.or(exactPredicate, cb.like(cb.lower(addressCity), lowerCaseToken));

        Path<String> addressRegion = addressJoin.get(Attribute.REGION);
        exactPredicate = cb.or(exactPredicate, cb.like(cb.lower(addressRegion), lowerCaseToken));

        Path<String> addressCountry = addressJoin.get(Attribute.COUNTRY);
        exactPredicate = cb.or(exactPredicate, cb.like(cb.lower(addressCountry), lowerCaseToken));

        return exactPredicate;
    }

    /**
     * Implementation of abstract partialMatchPredicate method defined in parent class.
     * Constructs specific predicates for each possible property that can be searched:
     *  - seller business name
     *  - product name
     *  - business address (suburb)
     *  - business address (city)
     *  - business address (region)
     *  - business address (country)
     *
     *  Paths are obtained from specific roots and joins. It assumes that roots and joins are consistent with each other
     *  i.e. They are derived from the same set of criteria builder and query.
     *
     *  lowerCaseTokens are matched to lowercase path, so matches are not case sensitive. The path may match as long
     *  as the token is a substring i.e. it obtains rows that match the tokens partially.
     *
     *  This also includes full matches, duplicates obtained from this predicate are handled in the main find function.
     *
     * @param lowerCaseToken A string containing lower-case characters.
     * @return A conjoined Predicate of conditions.
     */
    protected Predicate partialMatchPredicate(String lowerCaseToken) {
        Path<String> businessName = businessJoin.get(Attribute.BUSINESS_NAME);
        Predicate partialPredicate = cb.like(cb.lower(businessName), "%" + lowerCaseToken + "%");

        Path<String> productName = productJoin.get(Attribute.PRODUCT_NAME);
        partialPredicate = cb.or(partialPredicate, cb.like(cb.lower(productName), "%" + lowerCaseToken + "%"));

        Path<String> addressSuburb = addressJoin.get(Attribute.SUBURB);
        partialPredicate = cb.or(partialPredicate, cb.like(cb.lower(addressSuburb), "%" + lowerCaseToken + "%"));

        Path<String> addressCity = addressJoin.get(Attribute.CITY);
        partialPredicate = cb.or(partialPredicate, cb.like(cb.lower(addressCity), "%" + lowerCaseToken + "%"));

        Path<String> addressRegion = addressJoin.get(Attribute.REGION);
        partialPredicate = cb.or(partialPredicate, cb.like(cb.lower(addressRegion), "%" + lowerCaseToken + "%"));

        Path<String> addressCountry = addressJoin.get(Attribute.COUNTRY);
        partialPredicate = cb.or(partialPredicate, cb.like(cb.lower(addressCountry), "%" + lowerCaseToken + "%"));

        return partialPredicate;
    }

    /**
     * Constructs a predicate that defines filtering conditions based on the attributes of the given FilterQuery
     * instance.
     *
     * Checks each attribute for null values. If a value exists for each attribute, constructs a predicate that applies
     * that attribute as a filter condition, and conjoins it with an "and" to the main conjunction predicate.
     *
     * @param filters Instance of FilterQuery that contains (possibly null) values defining filter conditions.
     * @return A Predicate defining filtering conditions to be applied to the query. This predicate is used by
     * the find method. 
     */
    private Predicate filtersPredicate(SaleItemController.FilterQuery filters) {
        Predicate filterPredicate = cb.conjunction();
        if (filters.getBusinessType() != null) {
            Path<String> businessType = businessJoin.get(Attribute.BUSINESS_TYPE);
            filterPredicate = cb.and(filterPredicate, cb.like(businessType, filters.getBusinessType()));
        }

        Path<Double> saleItemPrice = saleRoot.get(Attribute.PRICE);

        if (filters.getMinPrice() != null) {
            filterPredicate = cb.and(filterPredicate, cb.greaterThanOrEqualTo(saleItemPrice, filters.getMinPrice()));
        }

        if (filters.getMaxPrice() != null) {
            filterPredicate = cb.and(filterPredicate, cb.lessThanOrEqualTo(saleItemPrice, filters.getMaxPrice()));
        }

        Path<LocalDateTime> saleItemClosingDate = saleRoot.get(Attribute.CLOSING_DATE);

        if (filters.getEarliestClosingDate() != null) {
            filterPredicate = cb.and(filterPredicate, cb.greaterThanOrEqualTo(saleItemClosingDate, filters.getEarliestClosingDate()));
        }

        if (filters.getLatestClosingDate() != null) {
            filterPredicate = cb.and(filterPredicate, cb.lessThanOrEqualTo(saleItemClosingDate, filters.getLatestClosingDate()));
        }

        Path<Boolean> saleItemSold = saleRoot.get(Attribute.SOLD);
        filterPredicate = cb.and(filterPredicate, cb.not(saleItemSold));

        return filterPredicate;
    }
}
