import {createLocalVue, mount} from "@vue/test-utils";
import BootstrapVue, {BootstrapVueIcons} from "bootstrap-vue";

import SaleItemCard from "../components/sale-item-search/SaleItemCard";
import PrimeVue from "primevue/config";
import DataView from "primevue/dataview/DataView";
import storage_util from "../javascript_modules/storage_util";
import {getCurrency} from "@/javascript_modules/currency_util"

let localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons)
localVue.use(PrimeVue);
localVue.component('DataView', DataView);

jest.mock("../Api");
jest.mock("../javascript_modules/storage_util");
jest.mock("../javascript_modules/image_util");
jest.mock("../javascript_modules/currency_util")
jest.mock('vue-router');
jest.mock("../Api")

const currencySymbol = '$';
const currencyCode = 'NZD';

let wrapper;

const sampleData = {
    "saleItem": {
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
    "currency": {
        "symbol": currencySymbol,
        "code": currencyCode
    }
}

describe("An individual sale listing is mounted" ,  () => {
    const confirmTrigger = jest.spyOn(SaleItemCard.methods, "purchaseConfirm").mockImplementation(() => {
    });

    beforeEach( () => {
        jest.clearAllMocks();

        storage_util.getCurrentUser.mockReturnValue(1);

        getCurrency.mockImplementation(() => Promise.resolve({
            symbol: '$',
            code: 'NZD'
        }))


        wrapper = mount(SaleItemCard, {
            localVue,
            propsData: {
                saleListing: sampleData.saleItem,
                isGrid: true,
                isModal: false
            },
            stubs: ['router-link']

        });
    })

    afterEach(() => {
        wrapper.destroy();
    });

    test("componentRenders", () => {
        wrapper.vm.$nextTick()
        expect(wrapper.exists()).toBeTruthy();
    });

    test("b-overLayRenders", async () => {
        await wrapper.vm.setData()
        await wrapper.vm.$nextTick();
        const confirmPurchaseOverlay = wrapper.find("#confirm-purchase-overlay");

        expect(confirmPurchaseOverlay.exists()).toBeTruthy();
    });

    test("cancelPurchaseButtonRenders", async () => {
        wrapper.vm.$data.display = true
        await wrapper.vm.$nextTick()
        const cancelPurchaseButton = wrapper.find("#cancel-purchase-button");
        expect(cancelPurchaseButton.exists()).toBeTruthy();

    })

    test("confirmPurchaseButtonRenders", async () => {
        wrapper.vm.$data.display = true
        await wrapper.vm.$nextTick()
        const confirmPurchaseButton = wrapper.find("#confirm-purchase-button");
        expect(confirmPurchaseButton.exists()).toBeTruthy();
    })

    test("purchaseCard_cancelPurchase_overlayHidden", async () => {
        wrapper.vm.$data.display = true
        await wrapper.vm.$nextTick()
        const cancelPurchaseButton = wrapper.find("#cancel-purchase-button");
        await cancelPurchaseButton.trigger("click");
        await wrapper.vm.confirmDialogCanceled();
        const confirmPurchaseOverlay = wrapper.find("#confirm-purchase-overlay");
        expect(confirmPurchaseOverlay.props().show).toBe(false);
    });

    test("purchaseCard_confirmPurchase_confirmMethodCalled", async () => {
        wrapper.vm.$data.display = true
        await wrapper.vm.$nextTick()
        const confirmPurchaseButton = wrapper.find("#confirm-purchase-button");
        await confirmPurchaseButton.trigger("click");
        await wrapper.vm.$nextTick()

        expect(confirmTrigger).toBeCalled();
    });
});
