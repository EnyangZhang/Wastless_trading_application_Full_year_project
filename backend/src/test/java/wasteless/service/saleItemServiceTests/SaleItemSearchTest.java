package wasteless.service.saleItemServiceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wasteless.controller.SaleItemController;
import wasteless.model.Searchable;
import wasteless.service.searching_service.SaleItemSearchService;
import wasteless.service.searching_service.SearchParamsParser;
import wasteless.service.searching_service.SearchToken;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/SaleItemSearchTestData.sql"})
@SpringBootTest
public class SaleItemSearchTest {

    @Autowired
    private SaleItemSearchService saleItemSearchService;

    private List<Long> actualSaleItemIds;
    private SaleItemController.FilterQuery filterQuery;

    @BeforeEach
    void setUp() {
        actualSaleItemIds = new ArrayList<>();
        filterQuery = new SaleItemController.FilterQuery(null, 1000.0, 0.0, null, null);
    }

    @Test
    void findSaleItems_withEmptyStringAsSearchParam_returnsCorrectResultLength() {
        Long expectedLength = 6L;
        Long resultsLength = saleItemSearchService.find(
                SearchParamsParser.parse(""), filterQuery, 1, 1000, "default", "asc").getResultsLength();
        Assertions.assertEquals(expectedLength, resultsLength);
    }

    public static Stream<Arguments> findSaleItems_withSearchParams_retrievesExpectedItems() {
    return Stream.of(
        // #1 findAllListings_noFilters_retrievesAllItemsInCreatedOrder
        Arguments.of(
            "", // Search Terms
            new SaleItemController.FilterQuery( // Filters
                null, 1000.0, 0.0, null, null),
            1, // PageNum
            1000, // PerPage
            "default", // Sorting Column
            "asc", // Ordering
            List.of(4L, 2L, 6L, 5L, 3L, 1L) // Expected Result
            ),

        // #2 findListing[bean]_noFilters_retrievesCorrectResults
        Arguments.of(
            "bean",
            new SaleItemController.FilterQuery(null, 1000.0, 0.0, null, null),
            1,
            1000,
            "default",
            "asc",
            List.of(5L, 3L, 1L)),

        // #3 findResult[bean]_filterByType_returnsOnlyType
        Arguments.of(
            "bean",
            new SaleItemController.FilterQuery(
                "Accommodation and Food Services", 1000.0, 0.0, null, null),
            1,
            1000,
            "default",
            "asc",
            List.of(1L)),

        // #4 findResult[chrome]_noFilter_returnsCorrectResults
        Arguments.of(
            "chrome",
            new SaleItemController.FilterQuery(null, 1000.0, 0.0, null, null),
            1,
            1000,
            "default",
            "asc",
            List.of(4L, 2L, 5L, 3L)),

        // #5 findResult[chrome AND beans]_noFilter_returnsCorrectResults
        Arguments.of(
            "chrome AND beans",
            new SaleItemController.FilterQuery(null, 1000.0, 0.0, null, null),
            1,
            1000,
            "default",
            "asc",
            List.of(5L, 3L)),

        // #6 findResult[beans or chrome]_noFilter_returnsCorrectResults
        Arguments.of(
            "beans or chrome",
            new SaleItemController.FilterQuery(null, 1000.0, 0.0, null, null),
            1,
            1000,
            "default",
            "asc",
            List.of(4L, 2L, 5L, 3L, 1L)),

        // #7 findResult_noMatches_returnsEmptyList
        Arguments.of(
            "this product name must never exist",
            new SaleItemController.FilterQuery(null, 1000.0, 0.0, null, null),
            1,
            1000,
            "default",
            "asc",
            List.of()),

        // #8 findListings["Watties Baked Beans - 420g can"]_noFilters_findExpectedResult
        Arguments.of(
            "\"Watties Baked Beans - 420g can\"",
            new SaleItemController.FilterQuery(null, 1000.0, 0.0, null, null),
            1,
            1000,
            "default",
            "asc",
            List.of(1L)),

        // #9 findListings[CHROME]_noFilters_ignoresUppercase
        Arguments.of(
            "CHROME",
            new SaleItemController.FilterQuery(null, 1000.0, 0.0, null, null),
            1,
            1000,
            "default",
            "asc",
            List.of(4L, 2L, 5L, 3L)),

        // #10 findListings[CHROME BEANS]_noFilters_ignoresUppercase
        Arguments.of(
            "CHROME BEANS",
            new SaleItemController.FilterQuery(
                    null,
                    1000.0,
                    0.0,
                    null,
                    null),
            1,
            1000,
            "default",
            "asc",
            List.of(5L, 3L)),

        // #11 findListings[UC]_matchesBusiness_returnsCorrectListings
        Arguments.of(
            "UC",
            new SaleItemController.FilterQuery(
                    null,
                    1000.0,
                    0.0,
                    null,
                    null),
            1,
            1000,
            "default",
            "asc",
            List.of(4L, 2L, 1L)),

        // #12 findAllResults_page1_2PerPage_returnsFirst2Results
        Arguments.of(
            "",
            new SaleItemController.FilterQuery(
                    null,
                    1000.0,
                    0.0,
                    null,
                    null),
            1,
            2,
            "default",
            "asc",
            List.of(4L, 2L)),
        // #13 findAllResults_page2_2PerPage_returnsNext2Results
        Arguments.of(
            "",
            new SaleItemController.FilterQuery(
                    null,
                    1000.0, 0.0,
                    null,
                    null),
            2,
            2,
            "default",
            "asc",
            List.of(6L, 5L)),
        // #14 findAllListings_page3_2PerPage_returnsLast2Results
        Arguments.of(
            "",
            new SaleItemController.FilterQuery(null, 1000.0, 0.0, null, null),
            3,
            2,
            "default",
            "asc",
            List.of(3L, 1L)),

        // #15 findAllResults_page4_2PerPage_returnsNoItems
        Arguments.of(
            "",
            new SaleItemController.FilterQuery(null, 1000.0, 0.0, null, null),
            4,
            2,
            "default",
            "asc",
            List.of()),

        // #16 findAllResults_filterByStartDate_returnsCorrectResults
        Arguments.of(
            "",
            new SaleItemController.FilterQuery(
                null,
                1000.0,
                0.0,
                "2019-03-30",
                null),
            1,
            1000,
            "default",
            "asc",
            List.of(4L, 2L, 5L, 3L, 1L)),

            // #17 findAllResults_filterByStartDate_returnsCorrectResults
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            1000.0,
                            0.0,
                            "2019-03-30",
                            "2020-05-03"),
                    1,
                    1000,
                    "default",
                    "asc",
                    List.of(4L, 2L, 3L, 1L)),

            // #18 findResults_filterByPrice_returnsCorrectResults
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            99.0,
                            5.0,
                            null,
                            null
                            ),
                    1,
                    1000,
                    "default",
                    "asc",
                    List.of(4L, 2L, 5L, 3L)),

            // #19 findResults_orderByAscProductName_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            1000.0,
                            0.0,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "name",
                    "asc",
                    List.of(3L, 5L, 2L, 4L, 6L, 1L)),

            // #20 findResults_orderByDescProductName_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            1000.0,
                            0.0,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "name",
                    "desc",
                    List.of(1L, 6L, 2L, 4L, 3L, 5L)),

            // #21 findResults_orderByAscSellerName_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            1000.0,
                            0.0,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "seller",
                    "asc",
                    List.of(3L, 5L, 6L, 1L, 2L, 4L)),

            // #22 findResults_orderByDescSellerName_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            1000.0,
                            0.0,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "seller",
                    "desc",
                    List.of(1L, 2L, 4L, 3L, 5L, 6L)),

            // #23 findResults_orderByAscSuburb_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            1000.0,
                            0.0,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "suburb",
                    "asc",
                    List.of(3L, 5L, 6L, 1L, 2L, 4L)),

            // #24 findResults_orderByDescSuburb_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            1000.0,
                            0.0,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "suburb",
                    "desc",
                    List.of(1L, 2L, 4L, 3L, 5L, 6L)),

            // #25 findResults_orderByAscCity_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            1000.0,
                            0.0,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "city",
                    "asc",
                    List.of(1L, 2L, 4L, 3L, 5L, 6L)),

            // #26 findResults_orderByDescCity_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            1000.0,
                            0.0,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "city",
                    "desc",
                    List.of(3L, 5L, 6L, 1L, 2L, 4L)),

            // #27 findResults["B Region"]_matchesRegion_returnsCorrectResults
            Arguments.of(
                    "\"B Region\"",
                    new SaleItemController.FilterQuery(
                            null,
                            null,
                            null,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "default",
                    "asc",
                    List.of(6L, 5L, 3L)),

            // #28 findResults["B Country"]_matchesCountry_returnsCorrectResults
            Arguments.of(
                    "\"B Country\"",
                    new SaleItemController.FilterQuery(
                            null,
                            null,
                            null,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "default",
                    "desc",
                    List.of(1L, 2L, 4L)),

            // #29 findAllResults_orderAscPrice_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            null,
                            null,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "price",
                    "asc",
                    List.of(1L, 2L, 3L, 4L, 5L, 6L)),

            // #30 findAllResults_orderDescPrice_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            null,
                            null,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "price",
                    "desc",
                    List.of(6L, 5L, 4L, 3L, 2L, 1L)),

            // #31 findAllResults_orderDescQuantity_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            null,
                            null,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "quantity",
                    "desc",
                    List.of(5L, 2L, 1L, 6L, 3L, 4L)),

            // #32 findAllResults_orderDescExpiry_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            null,
                            null,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "expires",
                    "desc",
                    List.of(6L, 1L, 2L, 4L, 3L, 5L)),

            // #33 findAllResults_orderAscExpiry_returnsCorrectOrdering
            Arguments.of(
                    "",
                    new SaleItemController.FilterQuery(
                            null,
                            null,
                            null,
                            null,
                            null
                    ),
                    1,
                    1000,
                    "expires",
                    "asc",
                    List.of(3L, 5L, 2L, 4L, 1L, 6L))
        );
    }

    @ParameterizedTest
    @MethodSource
    void findSaleItems_withSearchParams_retrievesExpectedItems(String searchTerms,
                                                               SaleItemController.FilterQuery filterQuery,
                                                               Integer pageNum,
                                                               Integer perPage,
                                                               String sorting,
                                                               String ordering,
                                                               List<Long> expectedIds) {
        List<SearchToken> params = SearchParamsParser.parse(searchTerms);
        List<Searchable> searchResults = saleItemSearchService.find(
                params,
                filterQuery,
                pageNum,
                perPage,
                sorting,
                ordering
        ).getResult();
        for (Searchable saleItem: searchResults) {
            actualSaleItemIds.add(saleItem.getId());
        }
        Assertions.assertEquals(expectedIds, actualSaleItemIds);
    }
}
