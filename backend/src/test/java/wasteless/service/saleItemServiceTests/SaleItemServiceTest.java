package wasteless.service.saleItemServiceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import wasteless.controller.SaleItemController;
import wasteless.service.SaleItemService;
import wasteless.service.searching_service.SaleItemSearchService;
import wasteless.service.searching_service.SearchParamsParser;
import wasteless.service.searching_service.SearchToken;

import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
public class SaleItemServiceTest {

    @Autowired
    private SaleItemService saleItemService;

    @MockBean
    @Qualifier("saleItemSearchService")
    private SaleItemSearchService saleItemSearchService;

    public static Stream<Arguments> findSaleItems_withSearchParams_correctNumberOfCalls() {
        return Stream.of(
                // findSaleItems_validParams_callsSearchOnce
                Arguments.of(
                    "", // Search Terms
                    new SaleItemController.FilterQuery( // Filters
                            null, 1000.0, 0.0, null, null),
                    1, // PageNum
                    1000, // PerPage
                    "default", // Sorting Column
                    "asc", // Ordering
                    1
                ),

                // findSaleItems_nullableParamsNull_callsSearchOnce
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                null,
                                null,
                                null),
                        1,
                        100,
                        "default",
                        "asc",
                        1
                ),

                // findSaleItems_latestClosingDateAfterEarliestClosingDate_callsSearch
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                null,
                                "2020-05-03",
                                "2020-05-04"),
                        1,
                        100,
                        "default",
                        "asc",
                        1
                ),

                // findSaleItems_latestClosingDateEqualEarliestClosingDate_callsSearch
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                null,
                                "2020-05-04",
                                "2020-05-04"),
                        1,
                        100,
                        "default",
                        "asc",
                        1
                ),

                // findSaleItems_oneDateNull_callsSearch
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                null,
                                "2020-05-04",
                                null),
                        1,
                        100,
                        "default",
                        "asc",
                        1
                ),

                // findSaleItems_oneDateNull_callsSearch
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                null,
                                null,
                                "2020-05-04"),
                        1,
                        100,
                        "default",
                        "asc",
                        1
                ),

                // findSaleItems_maxAndMinPriceZero_callsSearch
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                0.0,
                                0.0,
                                null,
                                null),
                        1,
                        100,
                        "default",
                        "asc",
                        1
                ),

                // findSaleItems_maxPriceGreaterThanMinPrice_callsSearch
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                2.0,
                                1.0,
                                null,
                                null),
                        1,
                        100,
                        "default",
                        "asc",
                        1
                ),

                // findSaleItems_onePriceNull_callsSearch
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                1.0,
                                null,
                                null,
                                null),
                        1,
                        100,
                        "default",
                        "asc",
                        1
                ),

                // findSaleItems_onePriceNull_callsSearch
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                2.0,
                                null,
                                null),
                        1,
                        100,
                        "default",
                        "asc",
                        1
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void findSaleItems_withSearchParams_correctNumberOfCalls(String searchString,
                                                             SaleItemController.FilterQuery filterQuery,
                                                             Integer pageNum,
                                                             Integer perPage,
                                                             String sortBy,
                                                             String orderBy,
                                                             Integer timesCalled) {
        List<SearchToken> tokens = SearchParamsParser.parse(searchString);
        saleItemService.searchSaleListings(tokens, filterQuery, pageNum, perPage, sortBy, orderBy);
        Mockito.verify(saleItemSearchService, Mockito.times(timesCalled))
                .find(tokens, filterQuery, pageNum, perPage, sortBy, orderBy);
    }

    public static Stream<Arguments> findSaleItems_withInvalidInput_throwsException() {
        return Stream.of(

                // findSaleItems_pageNumZero_returnsCorrectErrorMessage
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                null,
                                null,
                                null),
                        0,
                        100,
                        "default",
                        "asc",
                        "Page number should not be less than 1"
                ),

                // findSaleItems_pageNumBelowZero_returnsCorrectErrorMessage
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                null,
                                null,
                                null),
                        -10,
                        100,
                        "default",
                        "asc",
                        "Page number should not be less than 1"
                ),

                // findSaleItems_perPageZero_returnsCorrectErrorMessage
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                null,
                                null,
                                null),
                        1,
                        0,
                        "default",
                        "asc",
                        "Cannot return page with 0 or less contents"
                ),

                // findSaleItems_perPageBelowZero_returnsCorrectErrorMessage
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                null,
                                null,
                                null),
                        1,
                        -10,
                        "default",
                        "asc",
                        "Cannot return page with 0 or less contents"
                ),

                // findSaleItems_latestClosingDateEarlierThanEarliestClosingDate_returnsCorrectErrorMessage
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                null,
                                "2020-05-04",
                                "2020-05-03"
                                ),
                        1,
                        100,
                        "default",
                        "asc",
                        "Latest closing date cannot be before earliest closing date"
                ),

                //  findSaleItems_latestClosingDateEarlierThanEarliestClosingDate_returnsCorrectErrorMessage
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                null,
                                        "2020-01-01",
                                "2019-05-03"),
                        1,
                        100,
                        "default",
                        "asc",
                        "Latest closing date cannot be before earliest closing date"
                ),

                // findSaleItems_maxPriceLessThanMinPrice_returnsCorrectErrorMessage
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                1.0,
                                2.0,
                                null,
                                null),
                        0,
                        100,
                        "default",
                        "asc",
                        "Maximum price cannot be less than minimum price"
                ),

                // findSaleItems_maxPriceBelowZero_returnsCorrectErrorMessage
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                -2.0,
                                null,
                                null,
                                null),
                        0,
                        100,
                        "default",
                        "asc",
                        "Minimum or maximum price should not be negative"
                ),

                // findSaleItems_minPriceBelowZero_returnsCorrectErrorMessage
                Arguments.of(
                        "",
                        new SaleItemController.FilterQuery(
                                null,
                                null,
                                -2.0,
                                null,
                                null),
                        0,
                        100,
                        "default",
                        "asc",
                        "Minimum or maximum price should not be negative"
                )
        );
    }

    @ParameterizedTest
    @MethodSource
    void findSaleItems_withInvalidInput_throwsException(String searchString,
                                                             SaleItemController.FilterQuery filterQuery,
                                                             Integer pageNum,
                                                             Integer perPage,
                                                             String sortBy,
                                                             String orderBy,
                                                             String message) {
        List<SearchToken> tokens = SearchParamsParser.parse(searchString);

        try {
            saleItemService.searchSaleListings(tokens, filterQuery, pageNum, perPage, sortBy, orderBy);
        } catch (IllegalArgumentException exception) {
            Assertions.assertTrue(exception.getMessage().contains(message));
        }
        Mockito.verify(saleItemSearchService, Mockito.never()).find(tokens, filterQuery, pageNum, perPage, sortBy, orderBy);
    }
}
