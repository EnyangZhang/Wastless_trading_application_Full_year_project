import BootstrapVue from "bootstrap-vue";
import InventoryManager from "../components/business/InventoryManager";
import {createLocalVue, shallowMount} from "@vue/test-utils";
import {displayPrice, getCurrency} from "@/javascript_modules/currency_util";
import StorageUtil from "../javascript_modules/storage_util";
import Api from "../Api";
import {getImageURL, getPrimaryImage} from "@/javascript_modules/image_util";

let localVue = createLocalVue();
localVue.use(BootstrapVue);
jest.mock("../Api");
jest.mock("vue-router");
jest.mock("../javascript_modules/currency_util");
jest.mock("../javascript_modules/storage_util");
jest.mock("../javascript_modules/image_util");

const fakeBusiness = {
    id: 1,
    name: "test",
    address: {
        country: "New Zealand"
    }
};

const currencySymbol = "$";
const currencyCode = "NZD";

const fakeInventory = [
    {
        id: 1,
        product: {
            id: "WATT-420-BEANS",
            name: "Watties Baked Beans",
            images: []
        },
        quantity: 12,
        pricePerItem: 14,
        totalPrice: 50,
        manufactured: "2010-05-14",
        sellBy: undefined,
        expires: "2022-01-01",
        created: "2021-08-15"
    },
    {
        id: 1,
        product: {
            name: "Corned Beef",
            id: "BEEF-2",
            images: []
        },
        quantity: 12,
        pricePerItem: 14,
        totalPrice: 50,
        manufactured: "2010-05-01",
        sellBy: undefined,
        expires: "2022-01-02",
        created: "2021-10-14"
    },
]

describe("Sale Listing Manager is mounted with some data", () => {

    let wrapper;

    beforeEach(async () => {
        getImageURL.mockImplementation(() => undefined);
        getPrimaryImage.mockImplementation(() => undefined);
        Api.getBusiness.mockImplementation(() => Promise.resolve({data: fakeBusiness}));
        Api.getInventory.mockImplementation(() => Promise.resolve({data: fakeInventory}))
        getCurrency.mockImplementation(() => Promise.resolve({symbol: currencySymbol, code: currencyCode}));
        StorageUtil.isLoggedIn.mockImplementation(() => Promise.resolve(true));
        StorageUtil.getActingAs.mockImplementation(() => Promise.resolve(fakeBusiness.id));
        displayPrice.mockImplementation(() => Promise.resolve("$500 NZD"));

        const options = {
            localVue,
            data() {
                return {
                    items: []
                }
            }
        }

        wrapper = shallowMount(InventoryManager, options);
        await wrapper.vm.$nextTick();
    });

    test("componentMounted_elementRenders", () => {
        expect(wrapper.exists()).toBeTruthy();
    });

    test("componentMounted_findTableElement_elementRenders", () => {
        const table = wrapper.find("#table");
        expect(table.exists()).toBeTruthy()
    });
});