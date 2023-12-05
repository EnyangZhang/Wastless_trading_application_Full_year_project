import {createLocalVue, createWrapper, mount} from "@vue/test-utils";
import BootstrapVue, {BootstrapVueIcons} from "bootstrap-vue";


import Api from "@/Api";
import PrimeVue from "primevue/config";
import DataView from "primevue/dataview/DataView";
import SaleItemViewDialog from "../components/sale-item-search/SaleItemViewDialog";

let localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons)
localVue.use(PrimeVue);
localVue.component('DataView', DataView);

jest.mock("../Api");
jest.mock("../javascript_modules/storage_util");
jest.mock("../javascript_modules/image_util");
jest.mock('vue-router');

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
        "country": "United States",
        "likedByUsers": []
    },
    "currency": {
        "symbol": currencySymbol,
        "code": currencyCode
    }
}

describe("An individual sale listing is mounted" ,  () => {

    beforeEach(async () => {
        const div = document.createElement("div");
        div.id = "root"
        document.body.appendChild(div)

        Api.getBusiness.mockImplementation(() => Promise.resolve(
            {
                status: 200,
                data: {
                    name: "Some Business Name",
                    address: {

                    }
                }
            }));

        Api.likeListing.mockImplementation(() => Promise.resolve({status: 200}));
        Api.unlikeListing.mockImplementation(() => Promise.resolve({status: 200}))

        wrapper = mount(SaleItemViewDialog,
            {
                localVue,
                attachTo: div,
                data() {
                    return {
                        actingAsCurrentUser: true,
                        liked: false,
                    }
                },
                propsData: {
                    saleListing: sampleData
                }
            });
        await wrapper.vm.show();
        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
    });

    afterEach(() => {
        wrapper.destroy();
    });

    const getModalElement = (tag) => {
        const element = document.getElementById(tag);
        return createWrapper(element);
    }

    test("componentRenders", () => {
        expect(wrapper.exists()).toBeTruthy();
    });

    test("topmostDivRenders", () => {
        const thing = wrapper.find("#sale-view-dialog");
        expect(thing.exists()).toBeTruthy();
    });

    test("modal_findElement_elementExists", () => {
        const modal = getModalElement("view-dialog-modal")
        expect(modal.exists()).toBeTruthy();
    });

    test("carousel_findElement_elementExists", () => {
        const carousel = getModalElement("carousel-1");
        expect(carousel.exists()).toBeTruthy();
    });

    test("handleLikeClicked_notYetLiked_callsLike", async () => {
        wrapper.vm.$data.liked = false;
        await wrapper.vm.handleLikeClicked();
        expect(Api.likeListing).toBeCalled();
    });

    test("handleLikeClicked_notYetLiked_setsLikedToTrue", async () => {
        wrapper.vm.$data.liked = false;
        await wrapper.vm.handleLikeClicked();
        expect(wrapper.vm.$data.liked).toBe(true);
    });

    test("handleLikeClicked_alreadyLiked_callsUnlike", async () => {
        wrapper.vm.$data.liked = true;
        await wrapper.vm.handleLikeClicked();
        expect(Api.unlikeListing).toBeCalled();
    });

    test("handleLikeClicked_alreadyLiked_setsLikedToFalse", async () => {
        wrapper.vm.$data.liked = true;
        await wrapper.vm.handleLikeClicked();
        expect(wrapper.vm.$data.liked).toBe(false);
    });

    test("likeButton_whenActingAsUser_isNotDisabled", async () => {
        wrapper.vm.$data.actingAsCurrentUser = true;
        wrapper.vm.$data.likedButtonLoading = false;
        await wrapper.vm.$nextTick();
        const btn = wrapper.findComponent({ref: "like-btn"})
        expect(btn.element.disabled).toBeFalsy();
    });

    test("likeButton_whenNotActingAsUser_isDisabled", async () => {
        wrapper.vm.$data.actingAsCurrentUser = false;
        wrapper.vm.$data.likedButtonLoading = false;
        await wrapper.vm.$nextTick();
        const btn = wrapper.findComponent({ref: "like-btn"})
        expect(btn.element.disabled).toBeTruthy();
    });
});