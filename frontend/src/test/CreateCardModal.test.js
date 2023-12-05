import {createLocalVue, shallowMount} from "@vue/test-utils";
import CommunityMarketplace from "../views/CommunityMarketplace";
import BootstrapVue from "bootstrap-vue";
import PrimeVue from "primevue/config"
import MultiSelect from "primevue/multiselect/MultiSelect";
import Api from "@/Api";
import StorageUtil from "../javascript_modules/storage_util";
import {getCards} from "@/test/resources/TestCards";
import CreateCardModal from "../components/community-marketplace/CreateCardModal";

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
describe("Marketplace for sale tab displays the create card button and feature shown in new dialog popped up", () => {
    let wrapper;
    const expectedOptions =   [
        {name: 'Selling', id: 1},
        {name: 'Swapping', id: 2},
        {name: 'Food', id: 3},
    ];

    beforeEach(() => {
        // Stub StorageUtil to always return true when isLoggedIn is called on it
        StorageUtil.isLoggedIn.mockImplementation(() => Promise.resolve(true));
        StorageUtil.getCurrentUserInfo.mockImplementation(() => Promise.resolve({homeAddress: {suburb: "", city: ""}}));
        Api.getCards.mockImplementation(() => Promise
            .resolve({data: getCards.forSaleCards, headers: {"Total-Pages": getCards().forSaleCards.length}}));
        wrapper = shallowMount(CreateCardModal, options);
    });

    test('createCardButton_underForSale_rendered', () => {
        const createCardButton = wrapper.find('#submitButton')
        expect(createCardButton.exists()).toBeTruthy();
    });

    test('dialogForm_underCreateCardButton_rendered', ()=> {
        const createCardDialog = wrapper.find('#createCardForm')
        expect(createCardDialog.exists()).toBeTruthy();
    });

    test('keywordTitle_underDialog_rendered', ()=> {
        const keywordsTitle = wrapper.find('#keywords')
        expect(keywordsTitle.exists()).toBeTruthy();
    });

    test('multiSelect_underDialog_rendered', ()=> {
        const keywordsMultiSelect = wrapper.find('#keyword-select')
        expect(keywordsMultiSelect.exists()).toBeTruthy();
    });

    test('multiselect_underDialog_has3ExampleOptions', ()=> {
        const multiSelect = wrapper.find('#keyword-select')
        const options = multiSelect.vm.$props.options
        expect(options.length).toBe(3);
    });

    test('multiselect_underDialog_hasExpectedExampleContains', ()=> {
        const multiSelect = wrapper.find('#keyword-select')
        const options = multiSelect.vm.$props.options
        expect(options).toStrictEqual(expectedOptions);
    });
});




describe("Events emitted by create card dialogue are handled", () => {
    let wrapper;

    beforeEach(() => {
        wrapper = shallowMount(CreateCardModal, options);
    });

    test("getKeywordIds_threeKeywordsSelected_returnsCorrectList", () => {
        wrapper.vm.getKeywordIds();
        expect(wrapper.vm.form.keywordIds).toStrictEqual([1, 2, 3]);
    })

    test("getKeywordIds_twoKeywordsSelected(OnePopped)_returnsCorrectList", () => {
        wrapper.vm.selectedKeywords.pop();
        wrapper.vm.getKeywordIds();
        expect(wrapper.vm.form.keywordIds).toStrictEqual([1, 2])
    })

    test("getKeywordIds_oneKeywordSelected(TwoPopped)_returnsCorrectList", () => {
        wrapper.vm.selectedKeywords.pop();
        wrapper.vm.selectedKeywords.pop();
        wrapper.vm.getKeywordIds();
        expect(wrapper.vm.form.keywordIds).toStrictEqual([1])
    })

    test("getKeywordIds_zeroKeywordSelected(ThreePopped)_returnsCorrectList", () => {
        wrapper.vm.selectedKeywords.pop();
        wrapper.vm.selectedKeywords.pop();
        wrapper.vm.selectedKeywords.pop();
        wrapper.vm.getKeywordIds();
        expect(wrapper.vm.form.keywordIds).toStrictEqual([])
    })
});

describe("canSubmit function returns correct statements when called", () => {
    let wrapper;

    beforeEach(() => {
        options = {
            localVue,
            propsData: {},
            data() {
                return {
                    selectedKeywords: [
                        {name: 'Example1', id: 1},
                        {name: 'Example2', id: 2},
                        {name: 'Example3', id: 3}
                    ],
                    form: {
                        title: "My Card",
                        section: "For Sale",
                        description: "Hey I'm selling my card. Buy it please!",
                        keywordIds: [1, 2, 3]
                    }
                }
            }
        }

        wrapper = shallowMount(CreateCardModal, options);
    });

    test("canSubmit_allFieldsValid_returnsTrue", () => {
        expect(wrapper.vm.canSubmit()).toBeTruthy();
    });

    test("canSubmit_emptyTitleAllOtherFieldsValid_returnsFalse", ()=> {
        wrapper.vm.form.title = ""
        expect(wrapper.vm.canSubmit()).toBeFalsy();
    });

    test("canSubmit_noSectionSelectedAllOtherFieldsValid_returnsFalse", () => {
        wrapper.vm.form.section = null;
        expect(wrapper.vm.canSubmit()).toBeFalsy();
    });

    test("canSubmit_emptyKeywordIdsAllOtherFieldsValid_returnsTrue", async () => {
        wrapper.vm.selectedKeywords = [];
        await expect(wrapper.vm.canSubmit()).toBeTruthy();
    });

    test("canSubmit_emptyDescriptionAllOtherFieldsValid_returnsTrue", () => {
        expect(wrapper.vm.canSubmit()).toBeTruthy();
    });

    test("canSubmit_emptyKeywordIdsAndDescriptionAllOtherFieldsValid_returnsTrue", () => {
        wrapper.vm.selectedKeywords = [];
        wrapper.vm.form.description = "";
        expect(wrapper.vm.canSubmit()).toBeTruthy()
    })
})

describe("Testing the onSubmit method from community marketplace", () => {

    let wrapper;

    beforeEach(() => {

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
                        title: "My Card",
                        section: "For Sale",
                        description: "Hey I'm selling my card. Buy it please!",
                        keywordIds: [1, 2, 3]
                    }
                }
            },
        }

        wrapper = shallowMount(CreateCardModal, options);
    });

    test("onSubmit_201Response_errorMessageIsEmpty", async () => {
        Api.addMarketplaceCard.mockResolvedValue({status: 201});
        const submitButton = wrapper.find("#submitButton");
        await submitButton.trigger("click");
        expect(wrapper.vm.$data.errorMessage).toBe("");
    });
});