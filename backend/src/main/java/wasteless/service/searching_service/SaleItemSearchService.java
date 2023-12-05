package wasteless.service.searching_service;

import wasteless.controller.SaleItemController;

import java.util.List;

/**
 * A segregated interface of SearchingService, containing a find method (currently) specific to SaleItem, which
 * contains a parameter for filtering specifications which the static find method defined in SearchingService does not
 * include.
 */
public abstract class SaleItemSearchService extends SearchingService {

    /**
     * Overrides inherited find method, which does not contain the necessary filtering parameters to find sale listings
     * by. Calls static find method, passes in a new FilterQuery instance, with all of its attributes null by default.
     * @param parameters List of SearchTokens to search sale listings by.
     * @param pageNum Long page number. Converted into integer.
     * @param perPage Long perPage number. Converted into integer.
     * @param sortBy String to sort results by.
     * @param orderBy String to order results by.
     * @return Instance of SearchResult containing paginated list of results, and total number of results.
     */
    @Override
    public SearchResult find(List<SearchToken> parameters, long pageNum, long perPage, String sortBy, String orderBy) {
        return find(parameters,
                new SaleItemController.FilterQuery(
                        null,
                        null,
                        null,
                        null,
                        null),
                Integer.parseInt(String.valueOf(pageNum)),
                Integer.parseInt(String.valueOf(perPage)),
                sortBy,
                orderBy);
    }

    /**
     * Defines abstract find method for child components to implement.
     * @param parameters List of SearchTokens to search sale listings by.
     * @param filterQuery Instance of FilterQuery containing attributes to filter search results by.
     * @param pageNum Integer page number, defining page number to retrieve for pagination.
     * @param perPage Integer per page number, defining the number of results allowed per page. Used for pagination.
     * @param sortBy String defining the column attribute to sort search results by.
     * @param orderBy String defining the order to return sorted search results in.
     * @return Instance of SearchResult containing paginated list of results, and total number of results.
     */
    public abstract SearchResult find(List<SearchToken> parameters, SaleItemController.FilterQuery filterQuery,
                                      Integer pageNum, Integer perPage, String sortBy, String orderBy);
}
