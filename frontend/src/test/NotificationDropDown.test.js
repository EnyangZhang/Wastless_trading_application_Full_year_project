import {createLocalVue, mount} from "@vue/test-utils";
import BootstrapVue, {BootstrapVueIcons} from "bootstrap-vue";
import NotificationDropDown from "../components/notifications/NotificationDropDown";
import storage_util from "@/javascript_modules/storage_util";
import PrimeVue from "primevue/config";
import Api from "../Api";

let localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(PrimeVue);
jest.mock("../Api");
jest.mock('vue-router');
jest.mock("../javascript_modules/storage_util");

const $router = {
    push: jest.fn()
}
const $route = {
}

let fakeNotification = {
    id: 1,
    title: "Card Expired!",
    message: "Your card has expired!",
    read: false,
    category: "CARD_EXPIRY_WARNING",
    created: "15612-09-29T05:10:00Z",
    actionId: 4
}

let options = {
    localVue,
    propsData: {},
    data() {
        return {
            notifications: []
        }
    },
    mocks: {
        $route,
        $router
    },
    watch: {},
    stubs: {},
    methods: {},
    computed: {},
}

describe('Notifications dropdown is mounted', () => {

    const wrapper = mount(NotificationDropDown, options);

    const notifications = wrapper.find('#notifications');
    const notificationBtn = wrapper.find('#goToAllNotificationsBtn');
    const notificationDropDown = wrapper.find('#notificationsDropDown');

    beforeAll(async () => {
        storage_util.getCurrentUser.mockImplementation(() => 1)
        await wrapper.vm.$nextTick();
    });

    test('notifications_findNotifications_elementExists', () => {
        expect(notifications.exists()).toBeTruthy();
    });

    test('notificationDropDown_findDropDown_elementExists', () => {
        expect(notificationDropDown.exists()).toBeTruthy();
    })

    test('notificationBtn_findBtn_elementExists', () => {
        expect(notificationBtn.exists()).toBeTruthy();
    })

    test('notificationBtn_whenPressed_redirectsToNotificationView' , async () => {
        notificationBtn.trigger("click");
        await wrapper.vm.$nextTick();
        expect($router.push).toBeCalledWith({name: 'Notifications', params: {id: 1}});
    })
});

describe("Dropdown is mounted by a user with 1 notifications", () => {

    let wrapper;
    beforeEach(async () => {

        Api.getNotifications.mockImplementation(() => Promise.resolve({data: [fakeNotification]}))
        storage_util.isLoggedIn.mockImplementation(() => Promise.resolve(true));
        storage_util.getCurrentUser.mockImplementation(() => 1)
        wrapper = mount(NotificationDropDown, options);
        await wrapper.vm.$nextTick();
    });

    test("When getNotifications is called, the correct item is returned", async () => {
        await wrapper.vm.$nextTick();

        expect(wrapper.vm.$data.notifications).toContainEqual(fakeNotification);
    })

})