package wasteless.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import wasteless.model.MarketplaceCard;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataJpaTest
@Sql(
    scripts = {
            "classpath:/testData/CreateDBTables.sql",
            "classpath:/testData/MarketplaceData.sql"
    })
class MarketplaceCardRepositoryTest {

  @Autowired private MarketplaceCardRepository marketplaceCardRepository;

  public static Stream<Arguments> retrievePage_pageCombinations_expectedItems() {
    return Stream.of(
        Arguments.of("ForSale",  0, 1,   new int[] {1}),
        Arguments.of("Exchange", 0, 1,   new int[] {}),
        Arguments.of("Wanted",   0, 1,   new int[] {40}),
        Arguments.of("",         0, 1,   new int[] {}),
        Arguments.of("ForSale",  0, 1,   new int[] {1}),
        Arguments.of("ForSale",  1, 1,   new int[] {2}),
        Arguments.of("ForSale",  0, 2,   new int[] {1, 2}),
        Arguments.of("ForSale",  1, 2,   new int[] {3, 4}),
        Arguments.of("ForSale",  1, 4,   new int[] {5, 6, 7}),
        Arguments.of("ForSale",  2, 4,   new int[] {})
    );
  }

  @ParameterizedTest
  @MethodSource
  void retrievePage_pageCombinations_expectedItems(String section,
                                                   int page,
                                                   int size,
                                                   int[] expectedIds) {
    List<Integer> expected = new ArrayList<>();
    List<Integer> result = new ArrayList<>();
    for (int id : expectedIds) {
      expected.add(id);
    }

    Pageable pageable = PageRequest.of(page, size);
    Page<MarketplaceCard> cards =
        marketplaceCardRepository.findMarketplaceCardsBySection(section, pageable);
    for (MarketplaceCard card : cards) {
      result.add(card.getMarketplaceCardId());
    }
    Assertions.assertEquals(expected, result);
  }

  public static Stream<Arguments> retrievePage_withSorting_expectedItems() {
    return Stream.of(
          Arguments.of(Sort.by("created").ascending(), new int[] {4, 2, 6, 5, 3, 1, 7}),
          Arguments.of(Sort.by("created").descending(), new int[] {7, 1, 3, 5, 6, 2, 4}),

            Arguments.of(Sort.by("title").ascending(), new int[] {1, 2, 3, 4, 5, 6, 7}),
            Arguments.of(Sort.by("title").descending(), new int[] {7, 6, 5, 4, 3, 2, 1}),

            Arguments.of(Sort.by("creator.homeAddress.city").ascending(), new int[] {3, 5, 2, 6, 7, 1, 4}),
            Arguments.of(Sort.by("creator.homeAddress.city").descending(), new int[] {4, 1, 7, 6, 2, 5, 3}),

            Arguments.of(Sort.by("creator.homeAddress.suburb").ascending(), new int[] {6, 2, 5, 7, 3, 1, 4}),
            Arguments.of(Sort.by("creator.homeAddress.suburb").descending(), new int[] {4, 1, 3, 7, 5, 2, 6}),

            Arguments.of(Sort.by("creator.homeAddress.country").ascending(), new int[] {4, 3, 6, 5, 7, 2, 1}),
            Arguments.of(Sort.by("creator.homeAddress.country").descending(), new int[] {1, 2, 7, 5, 6, 3, 4})
    );
  }

  @ParameterizedTest
  @MethodSource
  void retrievePage_withSorting_expectedItems(Sort sort, int[] expectedIds) {
    List<Integer> expected = new ArrayList<>();
    List<Integer> result = new ArrayList<>();
    for (int id : expectedIds) {
      expected.add(id);
    }

    Pageable pageable = PageRequest.of(0, 10, sort);
    Page<MarketplaceCard> cards =
            marketplaceCardRepository.findMarketplaceCardsBySection("ForSale", pageable);
    for (MarketplaceCard card : cards) {
      result.add(card.getMarketplaceCardId());
    }
    Assertions.assertEquals(expected, result);
  }
}
