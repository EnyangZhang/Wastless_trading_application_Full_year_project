import {createLocalVue, shallowMount} from "@vue/test-utils";
import BusinessManagement from "../views/BusinessManagement";
import BootstrapVue from "bootstrap-vue";
import storage_util from "../javascript_modules/storage_util";

let localVue = createLocalVue();
localVue.use(BootstrapVue);
jest.mock("../Api");
jest.mock("vue-router")
jest.mock("../javascript_modules/storage_util");
let wrapper = null;

const $route = {
    hash: "#profile"
};

storage_util.getAvailableBusinesses.mockImplementation(() => {return {1: {name: "Business name"}}})
storage_util.getActingAs.mockImplementation(() => 1)

const refreshData = jest.fn();

beforeEach(() => {
    wrapper = shallowMount(BusinessManagement, {
        localVue,
        mocks: {
            $route,
            refreshData
        },
    });
});

describe("BusinessManagement", () => {
    test('Business management is not shown when not acting as a business', async () => {
        storage_util.getActingAs.mockImplementation(() => "ACTING_AS_CURRENT_USER")
        window.dispatchEvent(new Event(storage_util.SWITCHED_ACCOUNT_EVENT))

        await wrapper.vm.$nextTick();
        const manageBusinessTabs = wrapper.find('#manageBusinessTabs');
        expect(manageBusinessTabs.exists()).toBeFalsy();
    });

    test('Business management shows when acting as business', async () => {
        storage_util.getActingAs.mockImplementation(() => 1)
        window.dispatchEvent(new Event(storage_util.SWITCHED_ACCOUNT_EVENT))

        await wrapper.vm.$nextTick();
        const manageBusinessTabs = wrapper.find('#manageBusinessTabs');
        expect(manageBusinessTabs.exists()).toBeTruthy();
    });

    test('Error page is shown when user is not acting as a business', async () => {
        storage_util.getActingAs.mockImplementation(() => "ACTING_AS_CURRENT_USER")
        window.dispatchEvent(new Event(storage_util.SWITCHED_ACCOUNT_EVENT))

        await wrapper.vm.$nextTick();
        const errorCondition = wrapper.find('#cannotShow');
        expect(errorCondition.exists()).toBeTruthy();
    });

    test('Error page does not show when acting as business', async () => {
        storage_util.getActingAs.mockImplementation(() => 1)
        window.dispatchEvent(new Event(storage_util.SWITCHED_ACCOUNT_EVENT))

        await wrapper.vm.$nextTick();
        const errorCondition = wrapper.find('#cannotShow');
        expect(errorCondition.exists()).toBeFalsy();
    });

    describe("Business Management page tabs are changed", () => {

        test("tabIndex_tabChangeToCatalogue_callsCatalogueRefresh", async () => {
            const sectionRef = "catalogueManager";
            wrapper.vm.$data.tabIndex = wrapper.vm.$data.tabRefs.indexOf(sectionRef);

            await wrapper.vm.$nextTick();
            expect(wrapper.vm.$refs[sectionRef].refreshData).toBeCalled();
        });

        test("tabIndex_tabChangeToInventory_callsInventoryRefresh", async () => {
            const sectionRef = "inventoryManager";
            wrapper.vm.$data.tabIndex = wrapper.vm.$data.tabRefs.indexOf(sectionRef);

            await wrapper.vm.$nextTick();
            expect(wrapper.vm.$refs[sectionRef].refreshData).toBeCalled();
        });

        test("tabIndex_tabChangeToSaleListing_callsSaleListingRefresh", async () => {
            const sectionRef = "salesManager";

            wrapper.vm.$data.tabIndex = wrapper.vm.$data.tabRefs.indexOf(sectionRef);

            await wrapper.vm.$nextTick();
            expect(wrapper.vm.$refs[sectionRef].refreshData).toBeCalled();
        });

        test("tabIndex_tabChangeToHistoryManager_callsHistoryManagerRefresh", async () => {
            const sectionRef = "historyManager";

            wrapper.vm.$data.tabIndex = wrapper.vm.$data.tabRefs.indexOf(sectionRef);

            await wrapper.vm.$nextTick();
            expect(wrapper.vm.$refs[sectionRef].refreshData).toBeCalled();
        });

        test("tabIndex_tabChangeToProfile_callsProfileManagerRefresh", async () => {
            const sectionRef = "profileManager";

            wrapper.vm.$data.tabIndex = 1; // Must not already be on the profile page
            wrapper.vm.$data.tabIndex = wrapper.vm.$data.tabRefs.indexOf(sectionRef);

            await wrapper.vm.$nextTick();
            expect(wrapper.vm.$refs[sectionRef].refreshData).toBeCalled();
        });
    });
});