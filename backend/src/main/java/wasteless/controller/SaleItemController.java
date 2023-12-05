package wasteless.controller;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wasteless.exception.BadRequestException;
import wasteless.model.SaleItem;
import wasteless.service.BusinessService;
import wasteless.service.SaleItemService;
import wasteless.service.searching_service.SearchParamsParser;
import wasteless.service.searching_service.SearchingService;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


@RestController
public class SaleItemController {

    private final SaleItemService saleItemService;

    private final BusinessService businessService;

    /**
     * Autowired SaleItemController constructor method to initialize saleItemRepository.
     *
     * @param saleItemService Instance of SaleItemService.
     * @param businessService
     */
    @Autowired
    public SaleItemController(SaleItemService saleItemService, BusinessService businessService) {
        this.saleItemService = saleItemService;
        this.businessService = businessService;
    }

    @Getter
    public static class FilterQuery {

        private final String businessType;
        private final Double maxPrice;
        private final Double minPrice;
        private final LocalDateTime earliestClosingDate;
        private final LocalDateTime latestClosingDate;

        public FilterQuery(String businessType,
                           Double maxPrice,
                           Double minPrice,
                           String earliestClosingDate,
                           String latestClosingDate) {
            this.businessType = businessType;
            this.maxPrice = maxPrice;
            this.minPrice = minPrice;

            try {
                this.earliestClosingDate = earliestClosingDate == null || earliestClosingDate.isBlank()?
                        null : LocalDate.parse(earliestClosingDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
                this.latestClosingDate = latestClosingDate == null || latestClosingDate.isBlank() ?
                        null : LocalDate.parse(latestClosingDate, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
            } catch (DateTimeParseException ignored) {
                throw new BadRequestException("Invalid Dates.");
            }
        }
    }

    /**
     * This method searches for sale items by product name, business name or location, given a string in request
     * parameters, and a filter query for filtering results by price, closing date and business type. If the user is
     * not logged in, a HTTP 401 Unauthorized response is returned. Otherwise, an HTTP 200 response is
     * returned, along with the Sale Item object.
     *
     * @param searchQuery A string containing the search parameters
     * @param filterQuery A Filter Query containing the queries for filtering the search results by
     * @param pageNum The current page that frontend will display.
     * @param perPage Total items each page will display.
     * @param sortBy The field name that needs to be sorted.
     * @param orderBy The order in asc or desc.
     * @return A HTTP response to the get request
     */
    @GetMapping(path = "listings/search")
    public ResponseEntity<Object> getListingsSearch(@RequestParam(value = "searchQuery", required = false) String searchQuery,
                                                    FilterQuery filterQuery,
                                                    @RequestParam(value = "pageNum") @NotNull Integer pageNum,
                                                    @RequestParam(value = "perPage") @NotNull Integer perPage,
                                                    @RequestParam(value = "sortBy")  @NotNull String sortBy,
                                                    @RequestParam(value = "orderBy") @NotNull String orderBy) {
        if (searchQuery == null) {
            searchQuery = "";
        }
        SearchingService.SearchResult foundListings = saleItemService.searchSaleListings(
                SearchParamsParser.parse(searchQuery),
                filterQuery,
                pageNum,
                perPage,
                sortBy,
                orderBy);
        HttpHeaders responseHeader = new HttpHeaders();
        responseHeader.add("Total-length", String.valueOf(foundListings.getResultsLength()));
        return ResponseEntity.ok().headers(responseHeader).body(foundListings.getResult());
    }

    /**
     * This method retrieves a sale item given a specific ID.
     *
     * If the user is not logged in, an HTTP 401 Unauthorized response is returned.
     * If the sale item with given ID does not exist, an HTTP 406 Not Acceptable response is returned.
     * If otherwise successful, an HTTP 200 response is returned along with the user object.
     *
     * @param listingId the id of the listing to be retrieved
     * @return
     */
    @GetMapping(path = "/listings/{listingId}")
    public ResponseEntity<Object> getListingById(@PathVariable long listingId) {
        SaleItem saleItem = businessService.getSaleItemById(listingId);
        return new ResponseEntity<>(saleItem, HttpStatus.OK);
    }

}
