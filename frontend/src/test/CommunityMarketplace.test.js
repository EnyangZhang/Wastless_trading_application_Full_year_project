import {createLocalVue, shallowMount} from "@vue/test-utils";
import CommunityMarketplace from "../views/CommunityMarketplace";
import BootstrapVue from "bootstrap-vue";
import PrimeVue from "primevue/config"
import MultiSelect from "primevue/multiselect/MultiSelect";
import StorageUtil from "../javascript_modules/storage_util";
import Api from "@/Api";
import {getCards} from "@/test/resources/TestCards";

let localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(PrimeVue)
localVue.component("MultiSelect",MultiSelect)
jest.mock("../Api");
jest.mock('vue-router');
jest.mock("../javascript_modules/storage_util");

const $router = {
    push: jest.fn()
}
const $route = {
    path: '/community-marketplace',
    name: 'CommunityMarketplace',
    component: CommunityMarketplace,
    meta: {
        requiresAuth: true
    }
}

let options = {
    localVue,
    propsData: {},
    data() {
        return {
            errorFlag: false,
            errorMessage: undefined,

            selectedKeywords: [
                {name: 'Example1', id: 1},
                {name: 'Example2', id: 2},
                {name: 'Example3', id: 3}
            ],
            form: {
                keywordIds: []
            }
        }
    },
    mocks: {
        $route,
        $router
    },
    stubs: {},
    methods: {

    },
    computed: {}
}

describe('Community Marketplace page is mounted and the user is logged in', () => {


    // Stub StorageUtil to always return true when isLoggedIn is called on it
    StorageUtil.isLoggedIn.mockImplementation(() => Promise.resolve(true));
    StorageUtil.getCurrentUserInfo.mockImplementation(() => Promise.resolve({homeAddress: {suburb: "", city: ""}}));
    Api.getCards.mockImplementation(() => Promise
        .resolve({data: getCards.forSaleCards, headers: {"Total-Pages": getCards().forSaleCards.length}}));

    const wrapper = shallowMount(CommunityMarketplace, options);

    const marketplacePage = wrapper.find('#communityMarketplace');
    const tabs = wrapper.find('#communityMarketplaceTabs');

    beforeAll(async () => {
        await wrapper.vm.$nextTick();
    });

    test('Community Marketplace page renders', () => {
        expect(marketplacePage.exists()).toBeTruthy();
    });

    test('The tabs render', () => {
        expect(tabs.exists()).toBeTruthy();
    });

    test('The route path is for the community marketplace page', () => {
        expect(wrapper.vm.$route.path).toBe('/community-marketplace')
    });

    test('The for sale tab content renders', () => {
        const forSaleTabContent = tabs.find("#forSaleTabContent");
        expect(forSaleTabContent.exists()).toBeTruthy();
    });

    test('The exchange tab content renders', () => {
        const exchangeTabContent = tabs.find("#exchangeTabContent");
        expect(exchangeTabContent.exists()).toBeTruthy();
    });

    test('The wanted tab content renders', () => {
        const wantedTabContent = tabs.find("#wantedTabContent");
        expect(wantedTabContent.exists()).toBeTruthy();
    });

    test("saleTab_findForSaleDataElement_elementRenders", () => {
        const forSaleTabContent = tabs.find("#forSaleTabContent");
        const forSaleDataView = forSaleTabContent.find("#for-sale-data");
        expect(forSaleDataView.exists()).toBeTruthy();
    });

    test("exchange_findExchangeDataElement_elementRenders", () => {
        const exchangeTabContent = tabs.find("#exchangeTabContent");
        const exchangeDataView = exchangeTabContent.find("#exchange-data");
        expect(exchangeDataView.exists()).toBeTruthy();
    });

    test("wanted_findWantedDataElement_elementRenders", () => {
        const wantedTabContent = tabs.find("#wantedTabContent");
        const wantedDataView = wantedTabContent.find("#wanted-data");
        expect(wantedDataView.exists()).toBeTruthy();
    });

    test("marketplace_errorFlagIsFalse", () => {
        const flag = wrapper.vm.$data.errorFlag;
        expect(flag).toBeFalsy();
    });
});

describe("Marketplace page is mounted, and an error occurs with the mounted api call", () => {
    let wrapper;

    Api.getCards.mockImplementation(() => Promise.reject());

    beforeEach(() => {
        wrapper = shallowMount(CommunityMarketplace, options);
    });

    test("marketplace_errorFlagIsTrue", () => {
        const flag = wrapper.vm.$data.errorFlag;
        expect(flag).toBeTruthy();
    });
})


describe("Events emitted by components are handled by marketplace", () => {

    let wrapper;

    beforeEach(() => {
        wrapper = shallowMount(CommunityMarketplace, options);
    });

    test("forSale_changePage_changesSectionPage",  () => {
        const section = wrapper.vm.$data.sections.forSale;
        const page = section.page;
        wrapper.vm.$options.methods.changePage(section, page + 1);
        const newPage = wrapper.vm.$data.sections.forSale.page;
        expect(newPage).toBe(page + 1);
    });

    test("forSale_changeSorting_changesSectionSort", async() => {
        const sort = {sort: "title", order: "desc"};
        const section = wrapper.vm.$data.sections.forSale;
        await wrapper.vm.$options.methods.changeSorting(section, sort);
        await wrapper.vm.$nextTick()
        const newSort = wrapper.vm.$data.sections.forSale.sort;
        expect(newSort).toBe(sort.sort);
    });

    test("exchange_changeSorting_changesSectionSort", () => {
        const sort = {sort: "creator.homeAddress.suburb", order: "desc"};
        const section = wrapper.vm.$data.sections.exchange;
        wrapper.vm.$options.methods.changeSorting(section, sort);
        const newSort = wrapper.vm.$data.sections.exchange.sort;
        expect(newSort).toBe(sort.sort);
    })

    test("exchange_changeSorting_forSaleIsSame", () => {
        const sort = {sort: "creator.homeAddress.suburb", order: "desc"};
        const section = wrapper.vm.$data.sections.exchange;
        wrapper.vm.$options.methods.changeSorting(section, sort);
        const newSort = wrapper.vm.$data.sections.exchange.sort;
        const forSale = wrapper.vm.$data.sections.forSale;
        expect(forSale).not.toBe(newSort);
    });

    test("forSale_changeOrder_changesSectionOrder", () => {
        const order = "asc";
        const section = wrapper.vm.$data.sections.forSale;
        wrapper.vm.$options.methods.changeOrder(section, order);
        const newOrder = wrapper.vm.$data.sections.forSale.order;
        expect(newOrder).toBe(order);
    });
});


describe("Deleting cards", () => {

    let wrapper;

    beforeEach(() => {

        let options = {
            localVue,
            propsData: {},
            data() {
                return {}
            },
        }

        wrapper = shallowMount(CommunityMarketplace, options);
    });

    test("deleteClicked_removeFromForSale_removedCorrectCard", async () => {
        wrapper.vm.$data.sections.forSale.items = [{marketplaceCardId: 1}];
        wrapper.vm.cardDeleted({section: "For Sale", id: 1});
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$data.sections.forSale.items.length).toBe(0);
    })

    test("deleteClicked_removeFromExchange_removedCorrectCard", async () => {
        wrapper.vm.$data.sections.exchange.items = [{marketplaceCardId: 1}, {marketplaceCardId: 2}];
        wrapper.vm.cardDeleted({section: "Exchange", id: 1});
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$data.sections.exchange.items.length).toBe(1);
    })

    test("deleteClicked_removeFromWanted_removedCorrectCard", async () => {
        wrapper.vm.$data.sections.wanted.items = [{marketplaceCardId: 1}, {marketplaceCardId: 2}];
        wrapper.vm.cardDeleted({section: "Wanted", id: 1});
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$data.sections.wanted.items.length).toBe(1);
    })
});