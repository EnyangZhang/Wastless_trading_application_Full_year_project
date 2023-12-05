package wasteless.service.businessServiceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import wasteless.DGAAConfig;
import wasteless.model.Searchable;
import wasteless.service.BusinessService;
import wasteless.service.searching_service.BusinessSearchImpl;
import wasteless.service.searching_service.SearchingService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class BusinessSearchTest {
    @MockBean
    private DGAAConfig dgaaConfig;

    @MockBean
    private BusinessSearchImpl businessRepositorySearch;

    @InjectMocks
    @Autowired
    private BusinessService businessService;

    @BeforeEach
    public void setup(){
        List<Searchable> listBusinesses = new ArrayList<>();
        SearchingService.SearchResult businessResult = new SearchingService.SearchResult(listBusinesses, 10);

        Mockito.when(businessRepositorySearch.find(Mockito.anyList(), Mockito.any(long.class),  Mockito.any(long.class), Mockito.any(String.class), Mockito.any(String.class))).thenReturn(businessResult);
    }

    @Test
    void searchBusinesses_emptyStringProvided_returnEmptyList(){
        List<Searchable> resultList = businessService.searchBusinesses("").getResult();
        Assertions.assertEquals(0, resultList.size());
    }

    @Test
    void searchBusinesses_calledSearch_callsRepositoryOnce(){
        businessService.searchBusinesses("");
        Mockito.verify(businessRepositorySearch, Mockito.times(1)).find(Mockito.anyList(), Mockito.any(long.class),  Mockito.any(long.class), Mockito.any(String.class), Mockito.any(String.class));
    }

}
