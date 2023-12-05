import {createLocalVue, shallowMount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";
import Paginator from "@/components/Paginator";

let localVue = createLocalVue();
localVue.use(BootstrapVue);

const options = {
    localVue,
    props: {
        itemsLength: Number,
        perPage: Number,
        page: Number
    },
    propsData: {
        itemsLength: 1,
        perPage: 5,
        page: 0
    }
}

describe("The Paginator component is mounted with data of length 1 and a limit of 5 items per page", () => {

    let wrapper;

    beforeEach(() => {
        let thisOptions = options;
        thisOptions.propsData.itemsLength = 1;
        wrapper = shallowMount(Paginator, thisOptions);
    });

    test("paginator_calculatePages_onePage", async() => {
        const pages = await wrapper.vm.calculateTotalPage;
        expect(pages).toBe(1);
    });

    test("paginator_callNextPage_incrementCurrentPage", async () => {
        const expected = wrapper.vm.$data.currentPage + 1;
        await wrapper.vm.nextPage()
        expect(wrapper.vm.$data.currentPage).toBe(expected);
    });

    test("paginator_callPreviousPage_decrementCurrentPage", async () => {
        const expected = wrapper.vm.$data.currentPage - 1;
        await wrapper.vm.previousPage()
        expect(wrapper.vm.$data.currentPage).toBe(expected);
    });

    test("prevButton_atPage0_elementDisabled", () => {
        const prevButton = wrapper.find('#prev');
        wrapper.vm.$data.currentPage = 0;
        expect(prevButton.attributes().disabled).toBeTruthy();
    });

    test("prevButton_page>1_elementEnabled", async () => {
        const prevButton = wrapper.find('#prev');
        wrapper.vm.$data.currentPage = 2;
        await wrapper.vm.$nextTick();
        expect(prevButton.attributes().disabled).toBeFalsy();
    });
});

describe("The paginator is mounted with data of length 0 and a limit of 10 items", () => {

    let wrapper;
    let nextButton;

    beforeEach(() => {
        let thisOptions = options;
        thisOptions.propsData.itemsLength = 0;
        wrapper = shallowMount(Paginator, thisOptions);
        nextButton = wrapper.find("#next");
    });

    test("itemsLengthProp_noData_valueIs0", () => {
        const itemsLength = wrapper.vm.$props.itemsLength
        expect(itemsLength).toBe(0);
    });

    test("paginator_totalPages_1Page", () => {
        const pages = wrapper.vm.calculateTotalPage;
        expect(pages).toBe(1);
    });

    test("nextButton_noData_elementDisabled", () => {
        expect(nextButton.attributes().disabled).toBeTruthy();
    });
});

describe("The paginator is mounted with data of length 6 and a limit of 5 items", () => {

    let wrapper;
    let nextButton;

    const itemsLength = 6;
    const perPage = 5;

    beforeEach(() => {
        let thisOptions = options;
        thisOptions.propsData.itemsLength = itemsLength;
        thisOptions.propsData.perPage = perPage;
        wrapper = shallowMount(Paginator, thisOptions);
        nextButton = wrapper.find("#next");
    });

    test("itemLength_6Items_propValueIs6", () => {
        const items = wrapper.vm.$props.itemsLength;
        expect(items).toBe(6);
    });

    test("perPageProp_perPageIs5_propValueIs5", () => {
        const limit = wrapper.vm.$props.perPage;
        expect(limit).toBe(5);
    });

    test("paginator_calculateTotalPage_2Pages", () => {
        const pages = wrapper.vm.calculateTotalPage;
        expect(pages).toBe(2);
    });

    test("nextButton_currentPage1_elementEnabled", async () => {
        wrapper.vm.$data.currentPage = 1;
        await wrapper.vm.$nextTick();
        expect(nextButton.attributes().disabled).toBeFalsy();
    });

    test("nextButton_currentPageMax_elementDisabled", async () => {
        wrapper.vm.$data.currentPage = wrapper.vm.calculateTotalPage;
        await wrapper.vm.$nextTick();
        expect(nextButton.attributes().disabled).toBeTruthy();
    });
})