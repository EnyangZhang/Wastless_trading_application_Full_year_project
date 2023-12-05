import {createLocalVue, shallowMount} from "@vue/test-utils";
import {BootstrapVue, BootstrapVueIcons} from "bootstrap-vue";
import PrimeVue from "primevue/config";
import Api from "@/Api";
import ProductNotificationInfo from "../components/notifications/ProductNotificationInfo";
import {getCurrency} from "../javascript_modules/currency_util";

jest.mock("../Api");
jest.mock("../javascript_modules/storage_util");
jest.mock("../javascript_modules/currency_util");

let localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(PrimeVue);

Api.deleteMarketplaceCard.mockResolvedValue({status: 200});
Api.extendMarketplaceCard.mockResolvedValue({status: 200});
Api.markNotificationAsRead.mockResolvedValue({status: 200});


const testBusiness = {
    businessId: 1,
    address: {
        streetName: "Test Name",
        streetNumber: "123",
        suburb: "Test Suburb",
        city: "Test City",
        country: "Test Country",
        region: "Test Region",
        postcode: "12345"
    }
}

const testListing = {
    businessId: 1,
    businessCountry: "Test Country",
    price: 123
}

const currencySymbol = "$";
const currencyCode = "NZD";

describe("Notification information is mounted", () => {

    let wrapper;
    let options = {
        localVue
    }

    beforeEach(async () => {
        Api.getBusiness.mockImplementation(() => Promise.resolve({status: 200, data: testBusiness}));
        Api.getSaleListing.mockImplementation(() => Promise.resolve({status: 200, data: testListing}));
        getCurrency.mockImplementation(() => Promise.resolve({symbol: currencySymbol, code: currencyCode}));
        wrapper = shallowMount(ProductNotificationInfo, options);
        await wrapper.vm.$nextTick();
    });

    test("notificationPurchase_componentMounted_componentRenders", () => {
        expect(wrapper.exists()).toBeTruthy();
    });

    test("priceInfo_findElement_elementRenders", () => {
        const priceInfo = wrapper.find("#price-info");
        expect(priceInfo.exists()).toBeTruthy();
    });

    test("locationInfo_findElement_elementRenders", () => {
        const locationInfo = wrapper.find("#location-info");
        expect(locationInfo.exists()).toBeTruthy();
    });

    test("notificationPurchase_findPriceInfo_containsCorrectPrice", () => {
        const priceInfo = wrapper.find("#price-info");
        expect(priceInfo.text()).toContain(`${currencySymbol}${testListing.price} ${currencyCode}`);
    });
});