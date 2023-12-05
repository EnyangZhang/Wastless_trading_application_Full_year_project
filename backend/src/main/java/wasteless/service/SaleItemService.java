package wasteless.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wasteless.controller.SaleItemController.FilterQuery;
import wasteless.service.searching_service.SaleItemSearchService;
import wasteless.service.searching_service.SearchToken;
import wasteless.service.searching_service.SearchingService.SearchResult;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class that handles the business rule validation in-between the SaleItemController and SaleItemSearchService,
 * to ensure the data passed into SaleItemSearchService can always be processed by the module.
 */
@Service
public class SaleItemService {

    private final SaleItemSearchService saleItemSearchService;

    @Autowired
    public SaleItemService(SaleItemSearchService saleItemSearchService) { this.saleItemSearchService = saleItemSearchService; }

    /**
     * Validates the input from the controller (business requirement validation, such as pagination needing to start
     * from 1).
     * If validation checks pass (no exception is thrown), calls find method on saleItemSearchService.
     * @param tokens List of SearchTokens passed from web layer.
     * @param filterQuery Instance of FilterQuery passed from web layer. Contains filter parameters.
     * @param pageNum Page number. Must be validated to be more than 1 (search service assumes it is more than 0)
     * @param perPage Number of items per page. Cannot be 0, otherwise the contents will be empty.
     * @param sortBy String defining column to sort by. Assumed to not be null.
     * @param orderBy String defining order to sort by. Assumed to not be null.
     * @return Instance of SearchResult containing paginated list of results and total number of results
     *          (without pagination)
     */
    public SearchResult searchSaleListings(List<SearchToken> tokens,
                                            FilterQuery filterQuery,
                                            Integer pageNum,
                                            Integer perPage,
                                            String sortBy,
                                            String orderBy) {
        validateFilters(filterQuery);
        validatePagination(pageNum, perPage);
        return saleItemSearchService.find(tokens, filterQuery, pageNum, perPage, sortBy, orderBy);
    }

    /**
     * Checks the pagination variables: pageNum and perPage.
     * Throws an IllegalStateException if either pageNum is less than 1, or perPage is or is less than 0.
     * Searching service assumes pageNum is 1 or more. It will not function correctly if the page number is 0 or less.
     * @param pageNum Integer page number to validate.
     * @param perPage Integer number of items per page to validate.
     */
    private void validatePagination(Integer pageNum, Integer perPage) {
        if (pageNum < 1) {
            throw new IllegalArgumentException("Page number should not be less than 1");
        }
        if (perPage <= 0) {
            throw new IllegalArgumentException("Cannot return page with 0 or less contents");
        }
    }

    /**
     * Checks earliestClosingDate,latestClosingDate. Throws an IllegalArgumentException if latest closing date is earlier than
     * earliest closing date, if both of them are defined. If either one is null, then does not check.
     *
     * Checks min and max price for invalid values (negative prices, if not null).
     * If both prices are not null, checks if max price is less than min price. If true, throws an
     * IllegalArgumentException.
     * @param filterQuery Instance of FilterQuery containing earliest/latest - closingDate, min/max - Price attributes.
     */
    private void validateFilters(FilterQuery filterQuery) {
        LocalDateTime earliestClosingDate = filterQuery.getEarliestClosingDate();
        LocalDateTime latestClosingDate = filterQuery.getLatestClosingDate();

        if ((earliestClosingDate != null && latestClosingDate != null) && latestClosingDate.isBefore(earliestClosingDate)) {
            throw new IllegalArgumentException("Latest closing date cannot be before earliest closing date");
        }

        Double minPrice = filterQuery.getMinPrice();
        Double maxPrice = filterQuery.getMaxPrice();

        if ((minPrice != null && minPrice < 0 ) || (maxPrice != null && maxPrice < 0)) {
            throw new IllegalArgumentException("Minimum or maximum price should not be negative");
        }

        if ((minPrice != null && maxPrice != null) && maxPrice < minPrice) {
            throw new IllegalArgumentException("Maximum price cannot be less than minimum price");
        }
    }
}
