import {getCards, getNCards} from "./resources/TestCards";
import {createLocalVue, mount} from "@vue/test-utils";
import CommunityMarketplaceDataView from "../components/community-marketplace/CommunityMarketplaceDataView";
import StorageUtil from "../javascript_modules/storage_util";
import storage_util from "../javascript_modules/storage_util";
import BootstrapVue, {BootstrapVueIcons} from "bootstrap-vue";
import PrimeVue from "primevue/config";
import DataView from "primevue/dataview/DataView";

let localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(PrimeVue);
localVue.component('DataView', DataView);

jest.mock("../Api");
jest.mock('vue-router');
jest.mock("../javascript_modules/storage_util");


storage_util.getCurrentUserInfo.mockResolvedValue({homeAddress:{suburb: "suburb", city: "city"}})


describe("For Sale Data View is mounted with 3 cards while the user is logged in", () => {
    const options = {
        localVue,
        name: "MarketplaceDataView",
        props: {
            cards: Array
        },
        propsData: {
            cards: getCards().forSaleCards
        }
    }

    const wrapper = mount(CommunityMarketplaceDataView, options);
    const dataView = wrapper.find("#data-view");
    const sortOptions = [
        {label: 'Created', value: 'created'},
        {label: 'Title', value: 'title'},
        {label: 'Suburb', value: 'creator.homeAddress.suburb'},
        {label: 'City', value: 'creator.homeAddress.city'}
    ]
    const ascending = "asc";
    const descending = "desc";

    // Stub StorageUtil to always return true when isLoggedIn is called on it
    StorageUtil.isLoggedIn.mockImplementation(() => Promise.resolve(true));

    test("dataView_findAllCards_expect4Cards", () => {
        const numberOfCards = dataView.findAll("#item-card").length ;
        expect(numberOfCards).toBe(4);
    });

    test("sortDropdown_findElement_elementRenders", () => {
        const dropdown = dataView.find("#card-sorting-menu");
        expect(dropdown.exists()).toBeTruthy();
    });

    test("sortOrderButtons_findElement_expect2Options", () => {
        const radioButtons = dataView.find("#sorting-order");
        const options = radioButtons.vm.$props.options;
        expect(options.length).toBe(2);
    });

    test("dataView_sortValue_defaultCreated", () => {
        const created = "created";
        const selected = wrapper.vm.$data.selected;
        expect(selected.value).toBe(created);
    });

    test("dataView_sortOrder_defaultDescending", () => {
        const descending = "desc";
        const sortOrder = wrapper.vm.$data.sortOrder;
        expect(sortOrder).toBe(descending);
    });

    test("sortDropdown_findRenderedOptions_containsSortOptions", () => {
        const sortOptions = wrapper.vm.$data.sortOptions;
        const dropdownOptions = wrapper.findAllComponents({name: "b-dropdown-item"}).wrappers;
        expect(dropdownOptions.length).toBe(sortOptions.length)
    });

    test("sortDropdown_findOptionForEach_allOptionsRender", () => {
        sortOptions.forEach((option) => {
            const thing = wrapper.find(`#${option.label}`);
            expect(thing.exists()).toBeTruthy()
        });
    });

    test("sortDropdown_selectOption_selectedValueSet", () => {
        sortOptions.forEach((option) => {
            const dropdownOption = wrapper.find(`#${option.label}`);
            dropdownOption.trigger("click");
            const selected = wrapper.vm.$data.selected;
            expect(selected.value).toBe(option.value);
        });
    });

    describe("Sorting order radio buttons tests", () => {
        const ascendingButton = wrapper.find("#sorting-order_BV_option_0");
        const descendingButton = wrapper.find("#sorting-order_BV_option_1");

        test("orderDesc_clickAsc_orderAsc", async () => {
            wrapper.vm.$data.sortOrder = descending;
            await ascendingButton.trigger('click');
            const sortOrder = wrapper.vm.$data.sortOrder;

            expect(sortOrder).toBe(ascending);
        });

        test("orderAsc_clickDesc_orderDesc", async () => {
            wrapper.vm.$data.sortOrder = ascending;
            await descendingButton.trigger("click");
            const sortOrder = wrapper.vm.$data.sortOrder;
            expect(sortOrder).toBe(descending);
        });

        test("orderAsc_clickAsc_orderAsc", async () => {
            wrapper.vm.$data.sortOrder = ascending;
            await ascendingButton.trigger("click");
            const sortOrder = wrapper.vm.$data.sortOrder;
            expect(sortOrder).toBe(ascending);
        });

        test("orderDesc_clickDesc_orderDesc", async () => {
            wrapper.vm.$data.sortOrder = descending;
            await descendingButton.trigger("click");
            const sortOrder = wrapper.vm.$data.sortOrder;
            expect(sortOrder).toBe(descending);
        });
    });
});


describe("Data View is rendered with different number of cards", () => {
    let options = {
        localVue,
        name: "MarketplaceDataView",
        props: {
            cards: Array
        },
        propsData: {
            cards: undefined
        }
    }

    let wrapper;

    const mountWithNCards = (n) => {
        options.propsData.cards = getNCards(n);
        wrapper = mount(CommunityMarketplaceDataView, options);
    }

    const getNumberOfCards = () => {
        return wrapper.findAll("#item-card").length;
    }

    test("dataView0Cards_findCards_0CardWrappers", () => {
        mountWithNCards(0);
        const numberOfCards = getNumberOfCards();
        expect(numberOfCards).toBe(0);
    });

    test("dataView1Card_findCards_1CardWrapper", () => {
        mountWithNCards(1);
        const numberOfCards = getNumberOfCards();
        expect(numberOfCards).toBe(1);
    });

    test("dataView10Cards_findCards_10CardWrappers", () => {
        mountWithNCards(10);
        const numberOfCards = getNumberOfCards();
        expect(numberOfCards).toBe(10);
    });

    test("dataView100Cards_findCards_100CardWrappers", () => {
        mountWithNCards(100);
        const numberOfCards = getNumberOfCards();
        expect(numberOfCards).toBe(100);
    });
});