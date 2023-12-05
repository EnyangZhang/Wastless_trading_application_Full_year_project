import {createLocalVue, shallowMount} from "@vue/test-utils";

import FilterField from "@/components/filter/FilterField";
import BootstrapVue from "bootstrap-vue";

let localVue = createLocalVue();
localVue.use(BootstrapVue);

describe("Filter Field validation Test", () => {
    let wrapper
    beforeEach(async () => {
        wrapper = await shallowMount(
            FilterField,
            {
                localVue,
                name: "FilterField",
                propsData: {filterOption:{name: "Price", type: "price"}}
            }
        );
    })

    test("filtersSetWithValidPrices_noErrorCondition", async () => {
        wrapper.vm.$data.maximum = "1";
        wrapper.vm.$data.minimum = "0";
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$data.rangeState).toBe(null);
    });

    test("filtersSetWithMaxLessThanMin_errorConditionTrue", async () => {
        wrapper.vm.$data.maximum = "1";
        wrapper.vm.$data.minimum = "2";
        await wrapper.vm.$nextTick();
        expect(wrapper.vm.$data.rangeState).toBe(false);
    })
})