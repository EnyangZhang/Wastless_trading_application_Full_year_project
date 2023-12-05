import {createLocalVue, mount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";

import SaleItemDataView from "@/components/sale-item-search/SaleItemDataView";
import SaleItemCard from "@/components/sale-item-search/SaleItemCard";
import storage_util from "@/javascript_modules/storage_util";
import api from "@/Api";
import PrimeVue from "primevue/config";
import DataView from "primevue/dataview/DataView";

jest.mock("../Api");
jest.mock("../javascript_modules/storage_util");
jest.mock("../javascript_modules/image_util");
jest.mock('vue-router');

let wrapper;

const sampleData = [
    {
        "inventoryItem": {
            "inventoryItemId": 1,
            "product": {
                "name": "Blazarus",
                "description": "The agile Blazarus. Buy it today.",
                "manufacturer": "Little, Lewis and Hackett",
                "recommendedRetailPrice": 209.0,
                "created": "2021-08-09",
                "images": [],
                "primaryProductImage": null,
                "id": "Bs973"
            },
            "quantity": 2503,
            "pricePerItem": 139.0,
            "totalPrice": 347917.0,
            "manufactured": "2019-10-11",
            "sellBy": "2025-08-07",
            "bestBefore": "2025-08-07",
            "expires": "2025-08-07"
        },
        "quantity": 1655,
        "price": 139.0,
        "moreInfo": "Buy me now, I am a cool product. Buy Me please!",
        "created": "2021-08-09T15:42:54.982161",
        "closes": "2025-08-07T00:00:00",
        "id": 1,
        "sold": false,
        "businessId": 1,
        "country": "United States"
    },
    {
        "inventoryItem": {
            "inventoryItemId": 2,
            "product": {
                "name": "GrooveClear",
                "description": "The rain GrooveClear. Buy it today.",
                "manufacturer": "Little, Lewis and Hackett",
                "recommendedRetailPrice": 75.0,
                "created": "2021-08-09",
                "images": [],
                "primaryProductImage": null,
                "id": "Gr549"
            },
            "quantity": 263,
            "pricePerItem": 154.0,
            "totalPrice": 40502.0,
            "manufactured": "2019-08-12",
            "sellBy": "2023-10-24",
            "bestBefore": "2023-10-24",
            "expires": "2023-10-24"
        },
        "quantity": 26,
        "price": 154.0,
        "moreInfo": "Buy me now, I am a cool product. Buy Me please!",
        "created": "2021-08-09T15:42:54.982168",
        "closes": "2023-10-24T00:00:00",
        "id": 2,
        "sold": false,
        "businessId": 1,
        "country": "United States"
    }
]

let localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(PrimeVue);
localVue.component('DataView', DataView);

const $route = {
    path: '/some/path',
    meta: {
        showModal: undefined
    }
}

describe("Searching Sale listings", () => {
    beforeEach(async () => {
        storage_util.getActingAs.mockImplementation(() => Promise.resolve(1));
        api.searchSaleListing.mockResolvedValue({
                data: sampleData,
                status: 200,
                headers: {
                    "total-length": sampleData.length
                }
        });
        wrapper = await mount(SaleItemDataView, {
            localVue,
            mocks: {
                $route
            },
            stubs: ['router-link']
        });
    });

    afterEach(async () => {
        jest.clearAllMocks();
    })

    test("saleItemsAreReceived_viewingPage2_correctApiRequestIsSent", async () => {
        await wrapper.vm.$nextTick();
        jest.clearAllMocks();
        wrapper.vm.$data.currentPage = 2;
        await wrapper.vm.$nextTick();
        await wrapper.vm.getSaleItems();
        expect(api.searchSaleListing).toBeCalledWith(null, null, null, null, null, null, 2, 12, "name", "asc");
    });

    test("whenNextPageButtonClick_apiIsCalled", async () => {
        wrapper.vm.changePage(2);
        expect(api.searchSaleListing).toBeCalledTimes(2);
    });

    test("whenFiltersUpdated_apiIsCalled", async () => {
        let filters = wrapper.vm.$data.filterOptions
        wrapper.vm.filterUpdate(filters, false);
        expect(api.searchSaleListing).toBeCalledTimes(2);
    })

    test("whenSortingUpdated_apiIsCalled", async () => {
        let option = {sort: "name", direction: "asc"};
        wrapper.vm.sortChange(option, false);
        expect(api.searchSaleListing).toBeCalledTimes(2);
    })

    test("saleItemsAreReceived_filtersAreApplied_apiIsCalledWithFilters", async () => {
        await wrapper.vm.$nextTick();
        wrapper.vm.$data.filterOptions.closingdate.min = "date";
        wrapper.vm.$data.filterOptions.businesstype = "type";
        await wrapper.vm.$nextTick();
        wrapper.vm.getSaleItems();
        expect(api.searchSaleListing).toBeCalledWith(null, "type", null, null, "date", null, 1, 12, "name", "asc");
    });

    test("saleItemsAreReceived_orderIsApplied_apiIsCalledWithCorrectOrder", async () => {
        await wrapper.vm.$nextTick();
        wrapper.vm.$data.selectedSort = {value: "date"};
        wrapper.vm.$data.selectedDirection = {value: "asc"};
        wrapper.vm.getSaleItems();
        expect(api.searchSaleListing).toBeCalledWith(null, null, null, null, null, null, 1, 12, "date", "asc");
    });

    test("userHasTypedSearchQuery_searchIsClicked_apiIsCalledWithSearch", async () => {
        await wrapper.vm.$nextTick();
        let searchButton = wrapper.find("#search-btn");
        let searchField = wrapper.find("#search-field");
        searchField.setValue("search terms");
        searchButton.trigger("click");
        await wrapper.vm.$nextTick();
        expect(api.searchSaleListing).toBeCalledWith("search terms", null, null, null, null, null, 1, 12, "name", "asc");
    });

    test("whenRequestIsSuccess_errorMessageNotShown", async () => {
        let errorBox = wrapper.find("#error-box");
        wrapper.vm.getSaleItems();
        expect(errorBox.exists()).toBeFalsy();
    });

    test("whenRequestIsRejected_errorCodeIs400_showErrorMessage", async () => {
        api.searchSaleListing.mockRejectedValue({
                response: {
                    status: 400
            }
        });
        wrapper.vm.getSaleItems();
        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$data.error).toBe("Invalid Query");
    });

    test("whenRequestIsRejected_errorCodeIs500_showUnknownErrorMessage", async () => {
        api.searchSaleListing.mockRejectedValue({
                response: {
                    status: 500
            }
        });
        wrapper.vm.getSaleItems();
        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$data.error).toBe("Unknown Error");
    });

    test("whenRequestIsRejected_errorCodeIs404_showCouldNotFindServerError", async () => {
        api.searchSaleListing.mockRejectedValue({
            response: {
                status: 404
            }
        });
        wrapper.vm.getSaleItems();
        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$data.error).toBe("Could not find server");
    });

    test("retrievedSaleItems_2ItemsShowing", async () => {
        const numberOfCards = wrapper.findAllComponents(SaleItemCard).length;
        expect(numberOfCards).toBe(sampleData.length);
    });
});