package wasteless.service.businessServiceTests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import wasteless.controller.jsonobjects.SalesReportJson;
import wasteless.controller.jsonobjects.SalesReportSection;
import wasteless.exception.ForbiddenException;
import wasteless.model.Business;
import wasteless.model.SaleItem;
import wasteless.model.User;
import wasteless.repository.SaleItemRepository;
import wasteless.security.AuthUtil;
import wasteless.service.BusinessService;
import wasteless.test_helpers.BusinessDataCreator;
import wasteless.test_helpers.UserDataCreator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class GetSalesReportTest {

    @MockBean
    private SaleItemRepository saleItemRepository;

    @Autowired
    private BusinessService businessService;

    @MockBean
    private AuthUtil authUtil;


    @Test
    void getSectionsTotalGranularity_emptyListings_returnsZeroSales() {
        List<SaleItem> noItems = Collections.emptyList();
        LocalDate periodStart = LocalDate.parse("2020-12-19");
        LocalDate periodEnd = LocalDate.parse("2021-12-19");
        List<SalesReportSection> actualSections = businessService.getSectionsTotalGranularity(noItems, periodStart, periodEnd);
        int expectedSectionsLength = 1;
        SalesReportSection expectedSection = new SalesReportSection("TOTAL", periodStart, periodEnd, 0, 0.00);
        Assertions.assertEquals(expectedSectionsLength, actualSections.size());
        Assertions.assertEquals(expectedSection.toString(), actualSections.get(0).toString());
    }

    @Test
    void getSectionsTotalGranularity_noDateOverlap_returnsNoSections() {
        SaleItem item = BusinessDataCreator.createSaleItem(1L);
        item.setPurchased(LocalDateTime.parse("2022-06-03T10:15:30"));
        List<SaleItem> items = List.of(item);
        LocalDate periodStart = LocalDate.parse("2022-12-19");
        LocalDate periodEnd = LocalDate.parse("2021-12-19");
        List<SalesReportSection> actualSections = businessService.getSectionsTotalGranularity(items, periodStart, periodEnd);
        int expectedSectionsLength = 1;
        SalesReportSection expectedSection = new SalesReportSection("TOTAL", periodStart, periodEnd, 0, 0.00);
        Assertions.assertEquals(expectedSectionsLength, actualSections.size());
        Assertions.assertEquals(expectedSection.toString(), actualSections.get(0).toString());
    }

    @Test
    void getSectionsTotalGranularity_validInputs_returnsCorrectSections() {
        // Create list of SaleItems with different purchase dates
        SaleItem item1 = BusinessDataCreator.createSaleItem(1L);
        item1.setPurchased(LocalDateTime.parse("2020-12-18T10:15:30"));
        SaleItem item2 = BusinessDataCreator.createSaleItem(2L);
        item2.setPurchased(LocalDateTime.parse("2020-12-19T10:15:30"));
        SaleItem item3 = BusinessDataCreator.createSaleItem(3L);
        item3.setPurchased(LocalDateTime.parse("2021-12-19T10:15:30"));
        SaleItem item4 = BusinessDataCreator.createSaleItem(34L);
        item4.setPurchased(LocalDateTime.parse("2021-12-20T10:15:30"));
        List<SaleItem> items = List.of(item1, item2, item3, item4);

        LocalDate periodStart = LocalDate.parse("2020-12-19");
        LocalDate periodEnd = LocalDate.parse("2021-12-19");
        List<SalesReportSection> actualSections = businessService.getSectionsTotalGranularity(items, periodStart, periodEnd);

        int expectedSectionsLength = 1;
        int expectedNumberOfSales = 2;
        double expectedValueOfSales = item2.getPrice() + item3.getPrice();
        SalesReportSection expectedSection =
                new SalesReportSection("TOTAL", periodStart, periodEnd, expectedNumberOfSales, expectedValueOfSales);

        Assertions.assertEquals(expectedSectionsLength, actualSections.size());
        Assertions.assertEquals(expectedSection.toString(), actualSections.get(0).toString());
    }

    @Test
    void getSalesReport_nonAdmin_returns403() {
        Business business = BusinessDataCreator.createBusiness(1L);
        User nonAdmin = UserDataCreator.createUser();
        nonAdmin.setUserId(business.getPrimaryAdminId() + 5); // make sure that this user's ID is not the primary admin ID
        Mockito.when(authUtil.getCurrentUser()).thenReturn(nonAdmin);
        LocalDate localDate = LocalDate.now();
        Assertions.assertThrows(ForbiddenException.class,
                () -> businessService.getSalesReport(business, localDate, localDate, "TOTAL"));
    }

    @Test
    void getSalesReport_validInputs_returnsCorrectSalesReport() {
        Business business = BusinessDataCreator.createBusiness(1L);
        SaleItem saleItem = BusinessDataCreator.createSaleItem(1L);
        LocalDate periodStart = LocalDate.parse("2018-12-12");
        LocalDate periodEnd = LocalDate.parse("2020-12-12");
        saleItem.setPurchased(LocalDateTime.parse("2019-12-12T10:15:30"));
        SalesReportSection section = new SalesReportSection("TOTAL", periodStart, periodEnd, 1, saleItem.getPrice());

        User nonAdmin = UserDataCreator.createUser();
        nonAdmin.setUserId(business.getPrimaryAdminId()); // make sure that this user's ID is not the primary admin ID
        Mockito.when(authUtil.getCurrentUser()).thenReturn(nonAdmin);

        Mockito.when(saleItemRepository.findSoldListingsByBusiness(Mockito.anyLong()))
                .thenReturn(List.of(saleItem));

        SalesReportJson actualSalesReport = businessService.getSalesReport(business, periodStart, periodEnd, "TOTAL");
        SalesReportJson expectedSalesReport = new SalesReportJson(periodStart, periodEnd, List.of(section), business);

        Assertions.assertEquals(expectedSalesReport.toString(), actualSalesReport.toString());

    }

    @Test
    void getSectionsMonthlyGranularity_emptyListings_returnsZeroSales() {
        List<SaleItem> noItems = Collections.emptyList();
        LocalDate periodStart = LocalDate.parse("2021-11-01");
        LocalDate periodEnd = LocalDate.parse("2021-11-30");
        List<SalesReportSection> actualSections = businessService.getSectionsMonthlyGranularity(noItems, periodStart, periodEnd);
        String expectedSectionName1 = "11/2021";
        int expectedNumberOfSections = 1;
        SalesReportSection expectedSection1 = new SalesReportSection(expectedSectionName1, periodStart, periodEnd, 0, 0.00);
        Assertions.assertEquals(expectedNumberOfSections, actualSections.size());
        Assertions.assertEquals(expectedSection1.toString(), actualSections.get(0).toString());
    }

    @Test
    void getSectionsMonthlyGranularity_septemberToDecemberPeriod_returnsCorrectNumberOfListings() {
        List<SaleItem> noItems = Collections.emptyList();
        LocalDate periodStart = LocalDate.parse("2020-09-01");
        LocalDate periodEnd = LocalDate.parse("2020-12-31");
        List<SalesReportSection> actualSections = businessService.getSectionsMonthlyGranularity(noItems, periodStart, periodEnd);
        int expectedNumberOfSections = 4;
        SalesReportSection expectedSection1 = new SalesReportSection("9/2020", periodStart, LocalDate.parse("2020-09-30"), 0, 0.00);
        SalesReportSection expectedSection2 = new SalesReportSection("10/2020", LocalDate.parse("2020-10-01"), LocalDate.parse("2020-10-31"), 0, 0.00);
        SalesReportSection expectedSection3 = new SalesReportSection("11/2020", LocalDate.parse("2020-11-01"), LocalDate.parse("2020-11-30"), 0, 0.00);
        SalesReportSection expectedSection4 = new SalesReportSection("12/2020", LocalDate.parse("2020-12-01"), periodEnd, 0, 0.00);
        Assertions.assertEquals(expectedNumberOfSections, actualSections.size());
        Assertions.assertEquals(expectedSection1.toString(), actualSections.get(0).toString());
        Assertions.assertEquals(expectedSection2.toString(), actualSections.get(1).toString());
        Assertions.assertEquals(expectedSection3.toString(), actualSections.get(2).toString());
        Assertions.assertEquals(expectedSection4.toString(), actualSections.get(3).toString());
    }



    @Test
    void getSectionsMonthlyGranularity_fourSoldListingsInPeriod_returnsCorrectSections() {
        LocalDate periodStart = LocalDate.parse("2020-09-19");
        LocalDate periodEnd = LocalDate.parse("2020-12-29");
        SaleItem item1 = BusinessDataCreator.createSaleItem(1L);
        item1.setPurchased(LocalDateTime.parse("2020-09-23T10:15:30"));
        SaleItem item2 = BusinessDataCreator.createSaleItem(2L);
        item2.setPurchased(LocalDateTime.parse("2020-11-19T10:15:30"));
        SaleItem item3 = BusinessDataCreator.createSaleItem(3L);
        item3.setPurchased(LocalDateTime.parse("2020-11-19T10:15:30"));
        SaleItem item4 = BusinessDataCreator.createSaleItem(4L);
        item4.setPurchased(LocalDateTime.parse("2020-12-20T10:15:30"));
        List<SaleItem> items = List.of(item1, item2, item3, item4);
        SalesReportSection expectedSection1 = new SalesReportSection("9/2020", periodStart, LocalDate.parse("2020-09-30"), 1, 4.00);
        SalesReportSection expectedSection2 = new SalesReportSection("10/2020", LocalDate.parse("2020-10-01"), LocalDate.parse("2020-10-31"), 0, 0.00);
        SalesReportSection expectedSection3 = new SalesReportSection("11/2020", LocalDate.parse("2020-11-01"), LocalDate.parse("2020-11-30"), 2, 8.00);
        SalesReportSection expectedSection4 = new SalesReportSection("12/2020", LocalDate.parse("2020-12-01"), periodEnd, 1, 4.00);

        List<SalesReportSection> actualSections = businessService.getSectionsMonthlyGranularity(items, periodStart, periodEnd);
        Assertions.assertEquals(expectedSection1.toString(), actualSections.get(0).toString());
        Assertions.assertEquals(expectedSection2.toString(), actualSections.get(1).toString());
        Assertions.assertEquals(expectedSection3.toString(), actualSections.get(2).toString());
        Assertions.assertEquals(expectedSection4.toString(), actualSections.get(3).toString());
    }

    @Test
    void getSectionsMonthlyGranularity_noDateOverlap_returnsNoSections() {
        SaleItem item = BusinessDataCreator.createSaleItem(1L);
        item.setPurchased(LocalDateTime.parse("2022-06-03T10:15:30"));
        List<SaleItem> items = List.of(item);
        LocalDate periodStart = LocalDate.parse("2022-12-19");
        LocalDate periodEnd = LocalDate.parse("2021-12-19");
        List<SalesReportSection> actualSections = businessService.getSectionsMonthlyGranularity(items, periodStart, periodEnd);
        int expectedSectionsLength = 0;
        Assertions.assertEquals(expectedSectionsLength, actualSections.size());
    }
}
