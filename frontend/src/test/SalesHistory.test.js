import {createLocalVue, shallowMount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";
import SalesHistory from "../components/business/SalesHistory";
import StorageUtil from "../javascript_modules/storage_util";
import {displayPrice, getCurrency} from '../javascript_modules/currency_util';
import {getImageURL, getPrimaryImage} from "../javascript_modules/image_util";
import Api from "../Api";

let localVue = createLocalVue();
localVue.use(BootstrapVue);
jest.mock("../Api");
jest.mock('vue-router');
jest.mock('../javascript_modules/storage_util');
jest.mock('../javascript_modules/currency_util');
jest.mock("../javascript_modules/image_util");

const $router = {
    push: jest.fn()
}

let $route = {
    path: '/business/manage',
    name: 'ManageBusiness',
    component: SalesHistory,
    meta: {
        requiresAuth: true
    }
}

let options = {
    localVue,
    data() {
        return {
            title: "",
            currentPage: 1,
            perPage: 5,
            totalPage: null,
            fields: [{
                key: 'id',
                sortable: true
            },
                {
                    key: 'name',
                    sortable: true
                },
                {
                    key: 'quantity',
                    sortable: true
                },
                {
                    key: 'price',
                    sortable: true
                },
                {
                    key: 'likes',
                    sortable: true
                },
                {
                    key: 'listed',
                    sortable: true
                },
                {
                    key: 'Sold',
                    sortable: true
                }],
            businessId: undefined,
            businessName: undefined
        }
    },
    mocks: {
        $route,
        $router
    },
    stubs: {
    },
    computed: {
        itemsLength : jest.fn()
    }
}

const testBusiness = {
    id: 1,
    name: "testing business",
    address: {
        country: "New Zealand"
    }
};
const testSoldListing = {
    id: 1,
    inventoryItem: {
        product: {
            id: "WATT-420-BEAN",
            name: "test Product",
            images: []
        },
        quantity: 10,
        totalPrice: 20
    },
    quantity: 2,
    price: 10,
    created: "2021-05-21T15:23:48.845892",
    closes: "2021-07-21T23:59:00",
    purchased: "2021-07-21T23:56:00",
    moreInfo: "More Info",
    sold: true,
    likedByUsers: ["hello"],
};
const testUnsoldListing = {
    id: 2,
    inventoryItem: {
        product: {
            id: "WATT-420-BEAN",
            name: "test Product",
            images: []
        },
        quantity: 10,
        totalPrice: 20
    },
    quantity: 2,
    price: 10,
    created: "2021-05-21T15:23:48.845892",
    closes: "2021-07-21T23:59:00",
    moreInfo: "More Info",
    sold: false,
    likedUsers: ["hello"],
};

const currencySymbol = '$';
const currencyCode = 'NZD';

let wrapper;

beforeAll(() => {
    Api.getBusiness.mockImplementation(() => Promise.resolve({data: testBusiness}));
    Api.getSaleListings.mockImplementation(() => Promise.resolve({data: [testSoldListing]}));

    getCurrency.mockImplementation(() => Promise.resolve({symbol: currencySymbol, code: currencyCode}));
    displayPrice.mockImplementation(() => `${currencySymbol}${testSoldListing.price} ${currencyCode}`);

    getImageURL.mockImplementation(() => undefined);
    getPrimaryImage.mockImplementation(() => undefined);

    // Stub StorageUtil to always return true when isLoggedIn is called on it
    StorageUtil.isLoggedIn.mockImplementation(() => Promise.resolve(true));
    StorageUtil.getActingAs.mockImplementation(() => Promise.resolve(testBusiness.id));
})

describe('When sales history is mounted when user is logged in and acting as business with one sold listing', () => {

    let mySalesHistory;
    let salesHistoryTable;

    beforeEach( () => {
        wrapper = shallowMount(SalesHistory, options);
    });

    test('The page Sales History exists', () => {
        mySalesHistory = wrapper.find('#salesHistory');
        expect(mySalesHistory.exists()).toBeTruthy();
    })

    test("Sales history table exists", () => {
        mySalesHistory = wrapper.find('#salesHistory');
        salesHistoryTable = mySalesHistory.find('#salesHistoryTable');
        expect(salesHistoryTable.exists()).toBeTruthy();

    })

    test("Business sales history card title displays correct business name", () => {
        mySalesHistory = wrapper.find('#salesHistory');
        const titleComponent = mySalesHistory.find('#salesHistoryTitle');

        const title = titleComponent.attributes().title;
        expect(title).toContain(testBusiness.name);
    });

    test("Sales history table component has a property called fields that exists", () => {
        const fields = salesHistoryTable.props().fields;
        expect(fields).toBeTruthy();
    });

    test("Sales history table has fields property, whose length is the length of data in fields data property",
        () => {
            mySalesHistory = wrapper.find('#salesHistory');
            salesHistoryTable = mySalesHistory.find('#salesHistoryTable');
            const expectedLength = wrapper.vm.$data.fields.length;
            const fields = salesHistoryTable.props().fields;
            expect(fields.length).toBe(expectedLength);
        });

    test("Sales history table has a property called items that exits", () => {
        mySalesHistory = wrapper.find('#salesHistory');
        salesHistoryTable = mySalesHistory.find('#salesHistoryTable');
        const items = salesHistoryTable.props().items;
        expect(items).toBeTruthy();
    });

    test("Sales history table has a property called items with a length equal to the length of items array", () => {
        mySalesHistory = wrapper.find('#salesHistory');
        salesHistoryTable = mySalesHistory.find('#salesHistoryTable');
        const expectedLength = wrapper.vm.$data.items.length;
        const items = salesHistoryTable.props().items;
        expect(items.length).toBe(expectedLength);
    });

    test("When getSoldListings is called the sold sale item is returned", async () => {
        await wrapper.vm.$nextTick();
        const expectedListing = {
            image: undefined,
            id: testSoldListing.id,
            product: testSoldListing.inventoryItem.product,
            productId: testSoldListing.inventoryItem.product.id,
            quantity: testSoldListing.quantity,
            totalPrice: `${currencySymbol}${testSoldListing.price} ${currencyCode}`,
            created: "2021-05-21T15:23:48.845892",
            purchaseDate: "2021-07-21T23:56:00",
            moreInfo: testSoldListing.moreInfo,
            sold: true,
            images: [],
            likes: 1,
        };
        expect(wrapper.vm.$data.items).toContainEqual(expectedListing);
    });

    test("When getSoldListings is called the unsold sale item is not returned", async () => {
        await wrapper.vm.$nextTick();
        const expectedListing = {
            image: undefined,
            id: testUnsoldListing.id,
            product: testUnsoldListing.inventoryItem.product,
            productId: testUnsoldListing.inventoryItem.product.id,
            quantity: testUnsoldListing.quantity,
            price: `${currencySymbol}${testUnsoldListing.price} ${currencyCode}`,
            totalPrice: `${currencySymbol}${testUnsoldListing.price * testUnsoldListing.quantity} ${currencyCode}`,
            created: "21 May 2021",
            closes: "21 July 2021",
            moreInfo: testUnsoldListing.moreInfo,
            sold: true,
            images: []
        };
        expect(wrapper.vm.$data.items).not.toContainEqual(expectedListing);
    });

    test("changePage updates the page number correctly", () => {
        wrapper.vm.changePage(2);
        expect(wrapper.vm.$data.currentPage).toEqual(2);
    });

    test("calculateTotalPage sets totalPage to the correct number, 0 items", () => {
        wrapper.vm.$data.items = [];
        //perPage is set to 5
        wrapper.vm.calculateTotalPage()
        expect(wrapper.vm.$data.totalPage).toEqual(1);
    });

    test("calculateTotalPage sets totalPage to the correct number, 5 items", () => {
        wrapper.vm.$data.items = [];
        for (let i = 0; i < 5; i++) {
            wrapper.vm.$data.items.push({name: "testItem"})
        }
        //perPage is set to 5
        wrapper.vm.calculateTotalPage()
        expect(wrapper.vm.$data.totalPage).toEqual(1);
    });


    test("calculateTotalPage sets totalPage to the correct number, 6 items", () => {
        wrapper.vm.$data.items = [];
        for (let i = 0; i < 6; i++) {
            wrapper.vm.$data.items.push({name: "testItem"})
        }
        //perPage is set to 5
        wrapper.vm.calculateTotalPage()
        expect(wrapper.vm.$data.totalPage).toEqual(2);
    });

    test("calculateTotalPage sets totalPage to the correct number, 500 items", () => {
        wrapper.vm.$data.items = [];
        for (let i = 0; i < 500; i++) {
            wrapper.vm.$data.items.push({name: "testItem"})
        }
        //perPage is set to 5
        wrapper.vm.calculateTotalPage()
        expect(wrapper.vm.$data.totalPage).toEqual(100);
    });
})

describe('Component is mounted, but the API call to get sold listings returns error', function () {
    const businessId = 1;

    let wrapper;
    let mySalesHistory;
    let salesHistoryTable;

    beforeEach(() => {
        Api.getBusiness.mockImplementation(() => Promise.resolve({data: testBusiness}))
        Api.getSaleListings.mockImplementation(() => Promise.reject(() => {}));

        wrapper = shallowMount(SalesHistory, options);

        // Stub StorageUtil to always return true when isLoggedIn is called on it
        StorageUtil.isLoggedIn.mockImplementation(() => Promise.resolve(true));
        StorageUtil.getActingAs.mockImplementation(() => Promise.resolve(businessId));

    });

    test("Listings table does not display any products", () => {
        mySalesHistory = wrapper.find('#salesHistory');
        salesHistoryTable = mySalesHistory.find('#salesHistoryTable');
        const expectedLength = 0;
        const items = salesHistoryTable.props().items;
        expect(items.length).toBe(expectedLength);
    });
})