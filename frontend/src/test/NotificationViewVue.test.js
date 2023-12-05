import {createLocalVue, mount, shallowMount} from "@vue/test-utils";
import BootstrapVue, {BootstrapVueIcons} from "bootstrap-vue";
import NotificationView from "../views/NotificationView";
import storage_util from "@/javascript_modules/storage_util";
import PrimeVue from "primevue/config";
import api from "../Api";

let localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);
localVue.use(PrimeVue);

jest.mock('vue-router');
jest.mock('../Api')
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
            notifications: [],
            totalPages: null
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

describe('Notification View is mounted', () => {

    const wrapper = mount(NotificationView, options);

    const notificationContainer = wrapper.find('#notificationContainer');
    const notificationViewTitle = wrapper.find('#notificationViewTitle');

    beforeAll(async () => {
        await wrapper.vm.$nextTick();
    });

    test('notificationsContainer_findNotificationsContainer_elementExists', () => {
        expect(notificationContainer.exists()).toBeTruthy();
    });

    test('notificationViewTitle_findTitle_elementExists', () => {
        expect(notificationViewTitle.exists()).toBeTruthy();
    })

});

test('showNoNotifications_withNoNotifications_returnsTrue', () => {
    const localNotificationView = {notifications: [] }

    expect(NotificationView.computed.showNoNotifications.call(localNotificationView)).toBeTruthy()
})

describe('Notification view is mounted by a logged in user with 1 notification', () => {
    let wrapper;
    beforeEach(async () => {

        storage_util.isLoggedIn.mockImplementation(() => Promise.resolve(true));
        storage_util.getCurrentUser.mockImplementation(() => 1)
        api.getNotifications.mockImplementation(() => Promise.resolve({data: [fakeNotification]}))
        wrapper = shallowMount(NotificationView, options);
        await wrapper.vm.$nextTick();
    });

    test("When getNotifications is called, the correct item is returned", () => {
        expect(wrapper.vm.$data.notifications).toContainEqual(fakeNotification);
    })
})