package wasteless.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import wasteless.model.Searchable;
import wasteless.service.searching_service.SearchToken;
import wasteless.service.searching_service.SearchingService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Sql(scripts = {"classpath:testData/CreateDBTables.sql", "classpath:testData/CreateUserData.sql"})
@SpringBootTest
class JPABusinessSpecificationTest {

    @Autowired
    private SearchingService businessSearchService;

    /**
     * Process the searching result and put all the businesses' id in an array.
     * @param spec A List of string contains all words of search query.
     * @return Ids of businesses found in repository.
     */
    private List<Long> searchResultProcess(List<SearchToken> spec){
        List<Searchable> results = businessSearchService.find(spec, 1, 1000, "default", "asc").getResult();
        List<Long> resultIds = new ArrayList<>();
        for (Searchable result : results) {
            resultIds.add(result.getId());
        }

        return resultIds;
    }

    @Test
    void find_givenFullBusinessNameUpperCase_returnOneCorrectId(){
        List<Long> expectedIds = Collections.singletonList(1L);
        List<SearchToken> spec = Collections.singletonList(new SearchToken("UC", false));

        List<Long> resultIds = searchResultProcess(spec);

        Assertions.assertEquals(expectedIds, resultIds);
    }

    @Test
    void find_givenFullNameLowerCase_returnOneCorrectId() {
        List<Long> expectedIds = Collections.singletonList(1L);
        List<SearchToken> spec = Collections.singletonList(new SearchToken("uc", false));

        List<Long> resultIds = searchResultProcess(spec);

        Assertions.assertEquals(expectedIds, resultIds);
    }

    @Test
    void find_givenFullNameNotExist_returnEmptyList() {
        List<Long> expectedIds = new ArrayList<>();
        List<SearchToken> spec = Collections.singletonList(new SearchToken("UC Lib", true));

        List<Long> resultIds = searchResultProcess(spec);

        Assertions.assertEquals(expectedIds, resultIds);
    }

    @Test
    void find_givenPartialName_returnOneCorrectId() {
        List<Long> expectedIds = Collections.singletonList(1L);
        List<SearchToken> spec = Collections.singletonList(new SearchToken("U", false));

        List<Long> resultIds = searchResultProcess(spec);

        Assertions.assertEquals(expectedIds, resultIds);
    }

    @Test
    void find_givenPartialName_returnIdsThreeCorrectIds() {
        List<Long> expectedIds = Arrays.asList(2L, 3L, 4L, 5L);
        List<SearchToken> spec = Collections.singletonList(new SearchToken("M", false));

        List<Long> resultIds = searchResultProcess(spec);

        Assertions.assertEquals(expectedIds, resultIds);
    }

    @Test
    void find_givenTwoWordsWithSpaceBetween_returnCorrectIds(){
        List<Long> expectedIds = Arrays.asList(2L, 3L, 4L);
        List<SearchToken> spec = Arrays.asList(new SearchToken("Mc", false),
                new SearchToken("Ronalds", false));

        List<Long> resultIds = searchResultProcess(spec);

        Assertions.assertEquals(expectedIds, resultIds);
    }

    @Test
    void find_givenTwoWordsWithANDBetween_returnCorrectIds(){
        List<Long> expectedIds = Arrays.asList(2L, 3L, 4L);
        List<SearchToken> spec = Arrays.asList(new SearchToken("Mc", false),
                new SearchToken("AND", false), new SearchToken("Ronalds", false));

        List<Long> resultIds = searchResultProcess(spec);

        Assertions.assertEquals(expectedIds, resultIds);
    }

    @Test
    void find_givenTwoWordsWithORBetween_returnCorrectIds(){
        List<Long> expectedIds = Arrays.asList(2L, 3L, 4L, 5L);
        List<SearchToken> spec = Arrays.asList(new SearchToken("Mc", false),
                new SearchToken("OR", false), new SearchToken("Donalds", false));

        List<Long> resultIds = searchResultProcess(spec);

        Assertions.assertEquals(expectedIds, resultIds);
    }

    @Test
    void findByName_givenTwoWordsWithQuotation_returnOneCorrectId(){
        List<Long> expectedIds = Collections.singletonList(4L);
        List<SearchToken> spec = Collections.singletonList(new SearchToken("Mc Ronalds", true));

        List<Long> resultIds = searchResultProcess(spec);

        Assertions.assertEquals(expectedIds, resultIds);
    }
}