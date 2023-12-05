import {createLocalVue, mount, shallowMount} from "@vue/test-utils";
import {BootstrapVue, BootstrapVueIcons} from "bootstrap-vue";
import NotificationCard from "@/components/notifications/NotificationCard";
import PrimeVue from "primevue/config";
import storage_util from "@/javascript_modules/storage_util";
import Api from "@/Api";

jest.mock("../Api");
jest.mock("../javascript_modules/storage_util");

let wrapper;
let localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(PrimeVue);

Api.deleteMarketplaceCard.mockResolvedValue({status: 200});
Api.extendMarketplaceCard.mockResolvedValue({status: 200});
Api.markNotificationAsRead.mockResolvedValue({status: 200});

let testData;

describe("Expiry notifications", () => {
    beforeEach(async () => {
        jest.clearAllMocks();

        storage_util.getCurrentUser.mockReturnValue(50);

        testData = {
            id: 1,
            title: "Card Expired!",
            message: "Your card has expired!",
            read: false,
            category: "CARD_EXPIRY_WARNING",
            expiry: "15612-09-29T05:10:00Z",
            actionId: 4
        }

        wrapper = await shallowMount(NotificationCard, {
            localVue,
            propsData: {
                notification: testData,
                compact: false
            }
        });
    });

    test("expiryNotification_viewingNotification_deleteButtonShown", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#deleteCardButton");
        expect(button.exists()).toBeTruthy();
    });

    test("expiryNotification_viewingNotification_extendButtonShown", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#extendCardButton");
        expect(button.exists()).toBeTruthy();
    });

    test("expiryNotification_viewingNotification_markAsReadButtonNotShown", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#markAsReadButton");
        expect(button.exists()).toBeFalsy();
    });

    test("expiryNotification_clickingDeleteButton_deleteCardEndpointIsCalledWithCorrectArgs", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#deleteCardButton");
        button.trigger("click");
        await wrapper.vm.$nextTick();
        expect(Api.deleteMarketplaceCard).toBeCalledWith(4);
    });

    test("expiryNotification_clickingDeleteButton_markAsReadEndpointIsCalledWithCorrectArgs", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#deleteCardButton");
        button.trigger("click");
        await wrapper.vm.$nextTick();
        expect(Api.markNotificationAsRead).toBeCalledWith(50, 1);
    });

    test("expiryNotification_clickingDeleteButton_deleteCardEndpointIsCalledExactlyOnce", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#deleteCardButton");
        button.trigger("click");
        await wrapper.vm.$nextTick();
        expect(Api.deleteMarketplaceCard).toBeCalledTimes(1);
    });

    test("expiryNotification_clickingDeleteButton_markAsReadEndpointIsCalledExactlyOnce", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#deleteCardButton");
        button.trigger("click");
        await wrapper.vm.$nextTick();
        expect(Api.markNotificationAsRead).toBeCalledTimes(1);
    });

    test("expiryNotification_clickingExtendButton_extendCardEndpointIsCalledWithCorrectArgs", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#extendCardButton");
        button.trigger("click");
        await wrapper.vm.$nextTick();
        expect(Api.extendMarketplaceCard).toBeCalledWith(4);
    });

    test("expiryNotification_clickingExtendButton_markAsReadEndpointIsCalledWithCorrectArgs", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#extendCardButton");
        button.trigger("click");
        await wrapper.vm.$nextTick();
        expect(Api.markNotificationAsRead).toBeCalledWith(50, 1);
    });

    test("expiryNotification_clickingExtendButton_extendCardEndpointIsCalledExactlyOnce", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#extendCardButton");
        button.trigger("click");
        await wrapper.vm.$nextTick();
        expect(Api.extendMarketplaceCard).toBeCalledTimes(1);
    });

    test("expiryNotification_clickingExtendButtonAndExtendCardFails_markAsReadEndpointIsNotCalled", async () => {
        Api.extendMarketplaceCard.mockRejectedValue({response: {status: 500}});
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#extendCardButton");
        button.trigger("click");
        await wrapper.vm.$nextTick();
        expect(Api.markNotificationAsRead).toBeCalledTimes(0);
    });

    test("expiryNotification_clickingDeleteButtonAndDeleteCardFails_markAsReadEndpointIsNotCalled", async () => {
        Api.deleteMarketplaceCard.mockRejectedValue({response: {status: 500}});
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#deleteCardButton");
        button.trigger("click");
        await wrapper.vm.$nextTick();
        expect(Api.markNotificationAsRead).toBeCalledTimes(0);
    });
});

describe("Other notification categories", () => {
    beforeEach(async () => {
        jest.clearAllMocks();

        storage_util.getCurrentUser.mockReturnValue(51);

        testData = {
            id: 2,
            title: "Other type of card",
            message: "A message goes here",
            read: false,
            category: "",
            expiry: "15612-09-29T05:10:00Z",
            actionId: 4
        }

        wrapper = await mount(NotificationCard, {
            localVue,
            propsData: {
                notification: testData,
                compact: false
            }
        });
    });

    test("otherNotification_viewingNotification_deleteButtonNotShown", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#deleteCardButton");
        expect(button.exists()).toBeFalsy();
    });

    test("otherNotification_viewingNotification_extendButtonNotShown", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#extendCardButton");
        expect(button.exists()).toBeFalsy();
    });

    test("otherNotification_viewingNotification_markAsReadButtonShown", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#markAsReadButton");
        expect(button.exists()).toBeTruthy();
    });

    test("otherNotification_clickingMarkAsReadButton_markAsReadEndpointIsCalledExactlyOnce", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#markAsReadButton");
        button.trigger("click");
        await wrapper.vm.$nextTick();
        expect(Api.markNotificationAsRead).toBeCalledTimes(1);
    });

    test("otherNotification_clickingMarkAsReadButton_markAsReadEndpointIsCalledWithCorrectArgs", async () => {
        await wrapper.vm.$nextTick();
        let button = wrapper.find("#markAsReadButton");
        button.trigger("click");
        await wrapper.vm.$nextTick();
        expect(Api.markNotificationAsRead).toBeCalledWith(51, 2);
    });
});
