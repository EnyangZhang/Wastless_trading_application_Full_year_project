import {createLocalVue, shallowMount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";
import MyListings from "../views/SaleListingManager";
import StorageUtil from "../javascript_modules/storage_util"
import Api from "../Api";
import {displayPrice, getCurrency} from '@/javascript_modules/currency_util';
import {getImageURL, getPrimaryImage} from "@/javascript_modules/image_util";

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
    component: MyListings,
    meta: {
        requiresAuth: true
    }
}
const options = {
    localVue,
    data() {
        return {
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
                    label: 'Likes',
                    key: 'likes',
                    sortable: true
                },
                {
                    key: 'listed',
                    sortable: true
                },
                {
                    key: 'closes',
                    sortable: true
                }],
            items: [],
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

const fakeBusiness = {
    id: 1,
    name: "test",
    address: {
        country: "New Zealand"
    }
};
const fakeListing = {
    id: 1,
    inventoryItem: {
        product: {
            id: "WATT-420-BEAN",
            name: "Fake Product",
            images: []
        },
        quantity: 10,
        totalPrice: 20
    },
    quantity: 2,
    price: 10,
    totalPrice: 10,
    likedByUsers: ["user"],
    created: "2021-05-21T15:23:48.845892",
    closes: "2021-07-21T23:59:00",
    moreInfo: "More Info"
};
const currencySymbol = '$';
const currencyCode = 'NZD';

let wrapper;

beforeAll(() => {
    Api.getBusiness.mockImplementation(() => Promise.resolve({data: fakeBusiness}));
    Api.getSaleListings.mockImplementation(() => Promise.resolve({data: [fakeListing]}))

    getCurrency.mockImplementation(() => Promise.resolve({symbol: currencySymbol, code: currencyCode}));
    displayPrice.mockImplementation(() => `${currencySymbol}${fakeListing.price} ${currencyCode}`);

    getImageURL.mockImplementation(() => undefined);
    getPrimaryImage.mockImplementation(() => undefined);

    // Stub StorageUtil to always return true when isLoggedIn is called on it
    StorageUtil.isLoggedIn.mockImplementation(() => Promise.resolve(true));
    StorageUtil.getActingAs.mockImplementation(() => Promise.resolve(fakeBusiness.id));
});

describe('When my listings is mounted when user is logged in and acting as business with one listing', () => {

    let myListings;
    let listingsTable;

    beforeEach(() => {
        wrapper = shallowMount(MyListings, options);
        myListings = wrapper.find('#myListing');
        listingsTable = myListings.find('#listingsTable');
    });

    test('The page myListing exists', () => {
        expect(myListings.exists()).toBeTruthy();
    });

    test("Listings table exists", () => {
        expect(listingsTable.exists()).toBeTruthy();
    });

    test("Business listings card title displays correct business name", () => {
        const titleComponent = wrapper.find('#businessListingsTitle');

        // attributes() is needed for mounted functional components, cannot call props()
        const title = titleComponent.attributes().title;
        expect(title).toContain(fakeBusiness.name);
        expect(title).toContain(fakeBusiness.name);
    });

    test("Listings table component has a property called fields that exists", () => {
        const fields = listingsTable.props().fields;
        expect(fields).toBeTruthy();
    });

    test("Listings table has fields property, whose length is the length of data in fields data property",
        () => {
        const expectedLength = wrapper.vm.$data.fields.length;
        const fields = listingsTable.props().fields;
        expect(fields.length).toBe(expectedLength);
    });

    test("Listing table has a property called items that exits", () => {
       const items = listingsTable.props().items;
       expect(items).toBeTruthy();
    });

    test("Listing table has a property called items with a length equal to the length of items array", () => {
        const expectedLength = wrapper.vm.$data.items.length;
        const items = listingsTable.props().items;
        expect(items.length).toBe(expectedLength);
    });

    test("When getListings is called, the correct item is returned", async () => {
        await wrapper.vm.$nextTick()
        const expectedListing = {
                image: undefined,
                id: fakeListing.id,
                product: fakeListing.inventoryItem.product,
                productId: fakeListing.inventoryItem.product.id,
                quantity: fakeListing.quantity,
                totalPrice: `${currencySymbol}${fakeListing.price} ${currencyCode}`,
                created: "2021-05-21T15:23:48.845892",
                closes: "2021-07-21T23:59:00",
                moreInfo: fakeListing.moreInfo,
                images: [],
                likes: 1,
        };
        expect(wrapper.vm.$data.items).toContainEqual(expectedListing);
    });

    test("When business adds 1 new listing, the listing table property length increases by 1", async () => {
        await wrapper.vm.$nextTick()
        const expectedLength = wrapper.vm.$data.items.length + 1;
        await wrapper.vm.addListing();
        await wrapper.vm.$data.items.push({name: 'Test listing'});
        await wrapper.vm.$nextTick();
        const items = listingsTable.props().items;
        expect(items.length).toBe(expectedLength);
    });
});

describe('Component is mounted, but the API call to business and listings return error', function () {
    const businessId = 1;

    let wrapper;
    let myListings;
    let listingsTable;

    beforeEach(() => {
        Api.getBusiness.mockImplementation(() => Promise.reject(() => {}));
        Api.getSaleListings.mockImplementation(() => Promise.reject(() => {}));

        wrapper = shallowMount(MyListings, options);

        // Stub StorageUtil to always return true when isLoggedIn is called on it
        StorageUtil.isLoggedIn.mockImplementation(() => Promise.resolve(true));
        StorageUtil.getActingAs.mockImplementation(() => Promise.resolve(businessId));

        myListings = wrapper.find('#myListing');
        listingsTable = myListings.find('#listingsTable');
    });

    test("The table name will now display 'Business Listings'", () => {
        const titleComponent = wrapper.find('#businessListingsTitle');
        const expectedTitle = "Business's Listings"

        // attributes() is needed for mounted functional components, cannot call props()
        const title = titleComponent.attributes().title;
        expect(title).toBe(expectedTitle);
    });

    test("Listings table does not display any products", () => {
        const expectedLength = 0;
        const items = listingsTable.props().items;
        expect(items.length).toBe(expectedLength);
    });
});

describe('When my listings is mounted when user is logged in and acting as business with 6 listings', () => {
    const numberOfListings = 6;
    let listings = [];
    let wrapper;

    beforeEach(() => {
        for (let i = 0; i < numberOfListings; i++) {
            listings.push(fakeListing);
        }
        Api.getSaleListings.mockImplementation(() => Promise.resolve({data: listings}))

        wrapper = shallowMount(MyListings, options);
    });

    test("When the changePage event is emitted with a number between 2 and 100 currentPage is updated", () => {
        const pageNumber = Math.floor(Math.random() * (100 - 2) + 2);
        const paginator = wrapper.find("#paginator")
        paginator.vm.$emit("changePage", pageNumber);
        const currentPage = wrapper.vm.$data.currentPage;
        expect(currentPage).toBe(pageNumber);
    });
});

describe("Component is mounted while the user is not acting as a business", () => {

    beforeEach(() => {
        $route.name = "ViewSaleListings";
        $route.params = {
            id: 1
        }
        wrapper = shallowMount(MyListings, options);
    });

    test("wrapperExists", () => {
        expect(wrapper.exists()).toBeTruthy();
    });
});