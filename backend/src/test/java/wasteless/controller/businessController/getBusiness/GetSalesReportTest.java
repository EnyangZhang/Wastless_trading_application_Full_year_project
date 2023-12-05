package wasteless.controller.businessController.getBusiness;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import wasteless.controller.jsonobjects.SalesReportJson;
import wasteless.exception.BadRequestException;
import wasteless.exception.ForbiddenException;
import wasteless.model.Business;
import wasteless.service.BusinessService;
import wasteless.test_helpers.BusinessDataCreator;

import java.time.LocalDate;
import java.util.Collections;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;


@SpringBootTest
@AutoConfigureMockMvc
class GetSalesReportTest {

    @Autowired
    private MockMvc mockMvc;

    private Business business;

    @MockBean
    private BusinessService businessService;

    @BeforeEach
    void setup() {
        business = BusinessDataCreator.createBusiness(1L);
    }

    @Test
    void getSalesReport_notBusinessAdminOrGaa_responds403() throws Exception {
        Mockito.when(businessService.getSalesReport(
                        any(Business.class), any(LocalDate.class), any(LocalDate.class), Mockito.anyString()))
                .thenThrow(ForbiddenException.class);
        Mockito.when(businessService.getBusiness(Mockito.anyLong())).thenReturn(business);

        mockMvc.perform(MockMvcRequestBuilders.get(
                        "/businesses/" + business.getBusinessId() + "/sales-report?periodStart=2000-01-01&periodEnd=2001-01-01&granularity=TOTAL"))
                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }

    @Test
    void getSalesReport_invalidBusinessId_responds406() throws Exception {
        Mockito.when(businessService.getBusiness(Mockito.anyLong())).thenThrow(NoSuchElementException.class);

        mockMvc.perform(MockMvcRequestBuilders.get(
                        "/businesses/" + 99 + "/sales-report?periodStart=2000-01-01&periodEnd=2001-01-01&granularity=TOTAL"))
                .andExpect(MockMvcResultMatchers.status().isNotAcceptable());
    }

    @Test
    void getSalesReport_invalidRequestParams_responds400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(
                        "/businesses/" + business.getBusinessId() + "/sales-report?periodStart=sdfsdf&periodEnd=sdfs&granularity=sfsdf"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getSalesReport_invalidGranularity_responds400() throws Exception {
        Mockito.when(businessService.getSalesReport(
                        any(Business.class), any(LocalDate.class), any(LocalDate.class), Mockito.anyString()))
                .thenThrow(BadRequestException.class);
        Mockito.when(businessService.getBusiness(Mockito.anyLong())).thenReturn(business);

        mockMvc.perform(MockMvcRequestBuilders.get(
                        "/businesses/" + business.getBusinessId() + "/sales-report?periodStart=2000-01-01&periodEnd=2001-01-01&granularity=invalid"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getSalesReport_validInputs_responds200() throws Exception {
        LocalDate date = LocalDate.now();
        SalesReportJson salesReport = new SalesReportJson(date, date, Collections.emptyList(), business);
        Mockito.when(businessService.getSalesReport(
                        any(Business.class), any(LocalDate.class), any(LocalDate.class), Mockito.anyString()))
                .thenReturn(salesReport);
        Mockito.when(businessService.getBusiness(Mockito.anyLong())).thenReturn(business);

        mockMvc.perform(MockMvcRequestBuilders.get(
                        "/businesses/" + business.getBusinessId() + "/sales-report?periodStart=2000-01-01&periodEnd=2001-01-01&granularity=TOTAL"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
