import {createLocalVue, shallowMount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";
import SalesReport from "../components/business/SalesReport";
import storage_util from "../javascript_modules/storage_util";
import api from "../Api";
import PrimeVue from "primevue/config";
import Calendar from "primevue/calendar/Calendar";

let localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(PrimeVue);
localVue.component('Calendar', Calendar);
jest.mock("../Api");
jest.mock('vue-router');
jest.mock('../javascript_modules/storage_util');
jest.mock('../javascript_modules/currency_util');
jest.mock("../javascript_modules/image_util");

const $router = {};

const $route = {
    path: "/business/manage",
    name: "ManageBusiness",
    component: SalesReport,
    meta: {
        requiresAuth: true
    }
}

let options = {
    localVue,
    mocks: {
        $router,
        $route
    }
}

const testBusiness = {
    id: 1,
    name: "testing business",
    address: {
        country: "New Zealand"
    }
};

describe("Component is mounted", () => {

    let wrapper;

    beforeEach(() => {
        storage_util.isLoggedIn.mockImplementation(() => Promise.resolve(true));
        storage_util.getActingAs.mockImplementation(() => Promise.resolve(testBusiness.id));

        const result = {
            "data": {
                "periodStart": "2020-12-18",
                "periodEnd": "2021-12-18",
                "sections": [
                    {
                        "name": "TOTAL",
                        "sectionStart": "2020-12-18",
                        "sectionEnd": "2021-12-18",
                        "numberOfSales": 0,
                        "valueOfSales": 0.00
                    }
                ],
                "business": {
                    "businessId": 1,
                    "name": "Graham - Larson",
                    "description": "Welcome to Graham - Larson! We are currently setting up shop so check back soon for progress!",
                    "businessType": "Charitable Organisation",
                    "primaryAdminId": 2,
                    "registrationDate": "2021-09-19"
                }
            }
        };
        api.getSalesReport.mockImplementation(() => Promise.resolve(result));

        wrapper = shallowMount(SalesReport, options);
    });

    test("componentMounted_findWrapper_wrapperRenders", () => {
        expect(wrapper.exists()).toBeTruthy();
    });

    test("clickGenerateReport_validInputs_callsApiMethod", () => {
        wrapper.vm.$data.reportPeriod = "Year";
        wrapper.vm.$data.selectedYear = 2021;
        wrapper.vm.$data.granularity = "Total";
        wrapper.vm.generateReport();
        expect(api.getSalesReport).toBeCalled();
    })

    test("disableGenerateButton_nullYear_returnsTrue", async () => {
        wrapper.vm.$data.reportPeriod = "Year";
        wrapper.vm.$data.selectedYear = null;
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.disableGenerateButton).toBeTruthy();
    })

    test("disableGenerateButton_validYear_returnsFalse", async () => {
        wrapper.vm.$data.reportPeriod = "Year";
        wrapper.vm.$data.selectedYear = 2021;
        wrapper.vm.$data.granularity = "Total";
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.disableGenerateButton).toBeFalsy();
    })

    test("getPeriodStartAndEnd_yearSelected_returnsCorrectPeriod", async () => {
        wrapper.vm.$data.reportPeriod = "Year";
        wrapper.vm.$data.selectedYear = 2021;
        await wrapper.vm.$nextTick();
        const result = wrapper.vm.getPeriodStartAndEnd();
        // when dates are converted to strings they are shifted back 1 day, so all the dates below are 1 higher than expected
        expect(result[0].getFullYear()).toBe(2021);
        expect(result[0].getMonth()).toBe(0);
        expect(result[0].getDate()).toBe(2);
        expect(result[1].getFullYear()).toBe(2022);
        expect(result[1].getMonth()).toBe(0);
        expect(result[1].getDate()).toBe(1);
    })

    test("getPeriodStartAndEnd_monthSelected_returnsCorrectPeriod", async () => {
        wrapper.vm.$data.reportPeriod = "Month";
        wrapper.vm.$data.selectedMonth = new Date(1999, 4, 7);
        await wrapper.vm.$nextTick();
        const result = wrapper.vm.getPeriodStartAndEnd();
        // when dates are converted to strings they are shifted back 1 day, so all the dates below are 1 higher than expected
        expect(result[0].getFullYear()).toBe(1999);
        expect(result[0].getMonth()).toBe(4);
        expect(result[0].getDate()).toBe(2);
        expect(result[1].getFullYear()).toBe(1999);
        expect(result[1].getMonth()).toBe(5);
        expect(result[1].getDate()).toBe(1);
    })

    test("getPeriodStartAndEnd_weekSelected_returnsCorrectPeriod", async () => {
        wrapper.vm.$data.reportPeriod = "Week";
        wrapper.vm.$data.selectedWeekStart = new Date(2021, 8, 26);
        await wrapper.vm.$nextTick();
        const result = wrapper.vm.getPeriodStartAndEnd();
        // when dates are converted to strings they are shifted back 1 day, so all the dates below are 1 higher than expected
        expect(result[0].getFullYear()).toBe(2021);
        expect(result[0].getMonth()).toBe(8);
        expect(result[0].getDate()).toBe(27);
        expect(result[1].getFullYear()).toBe(2021);
        expect(result[1].getMonth()).toBe(9);
        expect(result[1].getDate()).toBe(3);
    })

    test("getPeriodStartAndEnd_daySelected_returnsCorrectPeriod", async () => {
        wrapper.vm.$data.reportPeriod = "Day";
        wrapper.vm.$data.selectedDay = new Date(2012, 0, 1);
        await wrapper.vm.$nextTick();
        const result = wrapper.vm.getPeriodStartAndEnd();
        // when dates are converted to strings they are shifted back 1 day, so all the dates below are 1 higher than expected
        expect(result[0].getFullYear()).toBe(2012);
        expect(result[0].getMonth()).toBe(0);
        expect(result[0].getDate()).toBe(2);
        expect(result[1].getFullYear()).toBe(2012);
        expect(result[1].getMonth()).toBe(0);
        expect(result[1].getDate()).toBe(2);
    })

    test("getPeriodStartAndEnd_customPeriodSelected_returnsCorrectPeriod", async () => {
        wrapper.vm.$data.reportPeriod = "Custom Period";
        wrapper.vm.$data.selectedCustomRangeStart = new Date(2012, 0, 1);
        wrapper.vm.$data.selectedCustomRangeEnd = new Date(2012, 7, 5);
        await wrapper.vm.$nextTick();
        const result = wrapper.vm.getPeriodStartAndEnd();
        // when dates are converted to strings they are shifted back 1 day, so all the dates should be 1 day higher than the user input
        expect(result[0].getFullYear()).toBe(2012);
        expect(result[0].getMonth()).toBe(0);
        expect(result[0].getDate()).toBe(2);
        expect(result[1].getFullYear()).toBe(2012);
        expect(result[1].getMonth()).toBe(7);
        expect(result[1].getDate()).toBe(6);
    })

    test("setYears_validInputs_returnsCorrectYears", async () => {
        const today = new Date();
        await wrapper.vm.$nextTick();
        wrapper.vm.setYears();

        const expectedYearsLength = today.getFullYear() - 1900 + 1;
        expect(wrapper.vm.$data.years.length).toBe(expectedYearsLength);
        expect(wrapper.vm.$data.selectedYear).toBe(today.getFullYear());
        expect(wrapper.vm.$data.yearRange).toBe(1900 + ":" + today.getFullYear());
    })

    test("formatDate_validInput_returnsCorrectString", () => {
        const result = wrapper.vm.formatDate("1994-05-24");
        expect(result).toBe("24/05/94");
    })

    test("getLastSunday_wednesdayInput_returnsASunday", () => {
        const wednesday = new Date(2021, 8, 22);
        const sunday = new Date(2021, 8, 19);
        const result = wrapper.vm.lastSunday(wednesday);
        expect(result.getFullYear()).toBe(sunday.getFullYear());
        expect(result.getMonth()).toBe(sunday.getMonth());
        expect(result.getDate()).toBe(sunday.getDate());
    })
});