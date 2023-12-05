import {createLocalVue, shallowMount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";
import PrimeVue from "primevue/config"
import Api from "../Api";
import {getCards} from "./resources/TestCards";
import MyCardsView from "../views/MyCardsView";

let localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(PrimeVue)
jest.mock("../Api");
jest.mock('vue-router');
jest.mock("../javascript_modules/storage_util");

const $router = {
    push: jest.fn()
}
const $route = {
    path: "/my-cards/:id",
    name: "MyCards",
    component: MyCardsView,
    meta: {
        requiresAuth: true,
        actingAsUser: true
    },
    params: {
        id: 2
    }
}

let options = {
    localVue,
    data() {
        return {
            errorFlag: false,
            errorMessage: undefined,

            currentUserId: null,
            userCards: {
                page: 1,
                size: 2,
                currentDisplayItemStart: null,
                currentDisplayItemEnd: null,
                items: [],
            },

            currentPageItems: []

        }
    },
    mocks: {
        $route,
        $router
    },
}

describe('MyCards page is rendered and initial value are all correct', () => {


    // Stub StorageUtil to always return true when isLoggedIn is called on it
    Api.getUserCards.mockResolvedValue({
        data: getCards().forSaleCards,
        status: 200
    })

    const wrapper = shallowMount(MyCardsView, options);

    const myCardPage = wrapper.find('#user-marketplace-card');
    const paginator = wrapper.find('#user-cards-paginator')
    const userCardsDataView = wrapper.find('#user-cards')
    const userCards = wrapper.vm.$data.userCards

    beforeAll(async () => {
        await wrapper.vm.$nextTick();
    });

    test('My Cards page renders', () => {
        expect(myCardPage.exists()).toBeTruthy();
    });

    test('The paginator is renders', () => {
        expect(paginator.exists()).toBeTruthy();
    });

    test('The user cards data view renders', () => {
        expect(userCardsDataView.exists()).toBeTruthy();
    });

    test('The route path is for the MyCards Page', () => {
        expect(wrapper.vm.$route.path).toBe('/my-cards/:id')
    });

    test('The user id in route path can be retrieved correctly', ()=>{
        expect(wrapper.vm.$route.params.id).toBe(2)
    })

    test('User\'s marketplace cards are retrieved correctly', () => {
        expect(userCards.items.length).toBe(4)
    })

    test('Correct display item start index when the page load', () => {
        expect(userCards.currentDisplayItemStart).toBe(0)
    })

    test('Correct display item end index when the page load', () => {
        expect(userCards.currentDisplayItemEnd).toBe(2)
    })

    test('Correct first card displays on page one', () => {
        const firstCardsId = wrapper.vm.$data.currentPageItems[0].id
        const expectedDisplayCardsId = getCards().forSaleCards[0].id
        expect(firstCardsId).toBe(expectedDisplayCardsId)
    })

    test('Correct first card displays on page two', () => {
        wrapper.vm.changePage(2)
        wrapper.vm.$nextTick()
        const firstCardsId = wrapper.vm.$data.currentPageItems[0].id
        const expectedDisplayCardsId = getCards().forSaleCards[2].id
        expect(firstCardsId).toBe(expectedDisplayCardsId)
    })
});

describe('Empty return from API', () => {
    Api.getUserCards.mockResolvedValue({
        data: [],
        status: 200
    })
    const wrapper = shallowMount(MyCardsView, options);

    beforeAll(async () => {
        await wrapper.vm.$nextTick();
    });

    test('When api return empty data then nothing should show', () => {
        expect(wrapper.vm.$data.userCards.items).toStrictEqual([])
    })
})

describe('Paginator handles card deletion', () => {
    Api.getUserCards.mockResolvedValue({
        data: getCards().forSaleCards.slice(0, 2),
        status: 200
    })

    const wrapper = shallowMount(MyCardsView, options);
    const userCards = wrapper.vm.$data.userCards

    test('When delete the only item on page two, the paginator should redirect to page one', () => {
        wrapper.vm.changePage(2)
        wrapper.vm.$nextTick()
        wrapper.vm.cardDeleted()
        wrapper.vm.$nextTick()
        expect(userCards.page).toBe(1)
    })
})

describe('Test if getUserCards method handles error message', () => {
    Api.getUserCards.mockImplementation(() => Promise.reject({status: 400, data: {message: "Bad request"}}))

    const wrapper = shallowMount(MyCardsView, options);

    beforeAll(async () => {
        await wrapper.vm.$nextTick();
    });

    test('When api response status 400 then errorFlag should set to true', () => {
        expect(wrapper.vm.$data.errorFlag).toBeTruthy()
    })

    test('When api response status 400 then error message should set', () => {
        expect(wrapper.vm.$data.errorMessage.data.message).toBe("Bad request")
    })

    test('When api response status 400 then 400 status received', () => {
        expect(wrapper.vm.$data.errorMessage.status).toBe(400)
    })
})
