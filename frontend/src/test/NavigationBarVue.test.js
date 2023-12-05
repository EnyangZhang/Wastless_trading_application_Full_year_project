import {createLocalVue, shallowMount} from "@vue/test-utils";
import NavigationBar from "../components/NavigationBar";
import BootstrapVue from "bootstrap-vue";

let localVue = createLocalVue();
localVue.use(BootstrapVue);
jest.mock("../Api");
jest.mock('vue-router');
jest.mock("../javascript_modules/storage_util")

const $router = {
    push: jest.fn()
}
const $route = {
}

describe('Navigation bar dropdown is mounted', function () {

    let wrapper = shallowMount(NavigationBar, {
        localVue,
        propsData: {},
        data() {
            return {
                options: [{name: "Hello", data: "hello"}],
                actingAs: "ACTING_AS_CURRENT_USER"
            }
        },
        mocks: {
            $route,
            $router
        },
        watch: {
            actingAs: () => {}
        },
        stubs: {},
        methods: {},
        computed: {
            loggedIn() {
                return true;
            }
        }
    });
    let navbar = wrapper.find('#navigationBar');
    let profileButton = wrapper.find('#profileButton');
    let homeButton = wrapper.find('#homeButton');
    let searchButton = wrapper.find('#searchButton');
    let marketplaceButton = wrapper.find('#marketplaceButton')
    let dropdown = wrapper.find("#accounts");
    let manageBusinessButton = wrapper.find('#manageBusiness');
    let addBusinessButton = wrapper.find('#addBusinessButton');


    test('Navigation bar renders', async () => {
        expect(navbar.exists()).toBeTruthy()
    });

    test('Navigation bar profile button renders', () => {
        expect(profileButton.exists()).toBeTruthy();
    });

    test('Navigation bar home button renders', () => {
        expect(homeButton.exists()).toBeTruthy();
    });

    test('Navigation bar search button renders', () => {
        expect(searchButton.exists()).toBeTruthy();
    });

    test('Navigation bar community marketplace button renders', () => {
        expect(marketplaceButton.exists()).toBeTruthy();
    });

    test('Navigation bar create business button renders', () => {
        expect(addBusinessButton.exists()).toBeTruthy();
    });

    test('Navigation bar dropdown renders', async () => {
        expect(dropdown.exists()).toBeTruthy()
    });

    test('The number of options in the dropdown equals the length of options array plus 1', async () => {
        const items = wrapper.findAllComponents({name: "b-dropdown-item"});
        const expectedLength = wrapper.vm.$data.options.length + 1;
        expect(items.length).toBe(expectedLength);
    });

    test('Current user avatar renders', () => {
        let avatar = wrapper.find('#currentUserAvatar');
        expect(avatar.exists()).toBeTruthy();
    });

    test('The total number of avatars equals the length of the options array plus 1', () => {
        let avatars = wrapper.findAll('.dropdown-avatar');
        const expectedLength = wrapper.vm.$data.options.length + 1
        expect(avatars.length).toBe(expectedLength);
    });

    test('There are two instances of dropdownText in the dropdown', () => {
        let texts = dropdown.findAll('.dropdown-text');
        expect(texts.length).toBe(2);
    });

    test('A b-dropdown-divider exists in the dropdown', () => {
        let divider = dropdown.find('#dropdownAccountSeparator');
        expect(divider.exists()).toBeTruthy();
    });

    // The below tests are are for when the user is not an admin of any business

    test('There is one instance of dropdownText in the dropdown', async () => {
        await wrapper.vm.$data.options.pop()
        let texts = dropdown.findAll('.dropdown-text');
        expect(texts.length).toBe(1);
    });

    test('A b-dropdown-divider does not exist in the dropdown', async () => {
        await wrapper.vm.$data.options.pop()
        let divider = dropdown.find('#dropdownAccountSeparator');
        expect(divider.exists()).toBeFalsy();
    });

    test('When acting as a user the manage business button does not exist', () => {
        expect(manageBusinessButton.exists()).toBeFalsy();
    });

    test("Switching to acting as a business causes the manage business button to appear", async () => {
        wrapper.vm.$data.actingAs = 1;
        await wrapper.vm.$nextTick();
        manageBusinessButton = wrapper.find('#manageBusiness');
        expect(manageBusinessButton.exists()).toBeTruthy();
    })

    test('When acting as a business the add business button is not available', async () => {
        wrapper.vm.$data.actingAs = 1;
        await wrapper.vm.$nextTick();
        addBusinessButton = wrapper.find('#addBusinessButton');
        expect(addBusinessButton.exists()).toBeFalsy();
    })

    test('When not acting as a business the add business button is visible', async () => {
        wrapper.vm.$data.actingAs = "ACTING_AS_CURRENT_USER";
        await wrapper.vm.$nextTick();
        addBusinessButton = wrapper.find('#addBusinessButton')
        expect(addBusinessButton.exists()).toBeTruthy();
    })

});

describe('Navigation bar is mounted while user is logged in and acting as a user', () => {
    let wrapper = shallowMount(NavigationBar, {
        localVue,
        propsData: {},
        data() {
            return {
                options: [{name: "Hello", data: "hello"}]
            }
        },
        mocks: {
            $route,
            $router
        },
        stubs: {},
        methods: {},
        computed: {
            loggedIn() {
                return true;
            },
            actingAsBusiness() {
                return false;
            }
        }
    });

    const dropdown = wrapper.find('#navigationBar');

    test('My Business Listings button does not exist', () => {
        let listingsButton = dropdown.find("#myBusinessListings");
        expect(listingsButton.exists()).toBeFalsy();
    });
});

describe('Navigation bar is mounted in login route', () => {
    let wrapper = shallowMount(NavigationBar, {localVue,
        propsData: {},
        data() {
        return {
                options: [{name: "Hello", data: "hello"}],
                actingAs: null
            }
        },
        mocks: {
            $route,
            $router
        },
        stubs: {},
        methods: {},
        computed: {
            loggedIn() {
                return false;
            }
        }
    });
    wrapper.vm.$route.path = '/login'
    let navbar = wrapper.find('#navigationBar');
    let dropdown = wrapper.find('#accounts');
    let manageBusinessButton = wrapper.find('#manageBusiness');

    test('Route is login', () => {
        expect(wrapper.vm.$route.path === '/login').toBeTruthy()
    });

    test('Navbar renders', () => {
        expect(navbar.exists()).toBeTruthy();
    });

    test('LoggedIn computed property returns false', () => {
        const loggedIn = wrapper.vm.$options.computed.loggedIn();
        expect(loggedIn).toBe(false);
    });

    test('Dropdown menu does not exist', () => {
        expect(dropdown.exists()).toBeFalsy();
    });

    test('Any avatar does not exist', () => {
        let avatar = wrapper.findAllComponents({name: 'b-avatar'});
        expect(avatar.exists()).toBeFalsy()
    })

    test('Manage business button does not exist', () => {
        expect(manageBusinessButton.exists()).toBeFalsy();
    })

    test('Business Listing button does not exist', () => {
        let businessListingButton = wrapper.find('#myBusinessListings');
        expect(businessListingButton.exists()).toBeFalsy();
    })
});

describe('Navigation bar is mounted and the user logged in and acting as a business', () => {
    let wrapper = shallowMount(NavigationBar, {
        localVue,
        propsData: {},
        data() {
            return {
                options: [{name: "Hello", data: "hello"}],
                actingAs: null,
                actingAsName: null,
            }
        },
        mocks: {
            $route,
            $router
        },
        stubs: {},
        methods: {},
        computed: {
            loggedIn() {
                return true;
            }
        }
    });

    let manageBusinessButton = wrapper.find('#manageBusiness');

    test("User is not acting as a user", () => {
        expect(wrapper.vm.$data.actingAs === "ACTING_AS_CURRENT_USER").toBeFalsy()
    });

    test("Manage business button is hidden when actingAs is null", () => {
        expect(manageBusinessButton.exists()).toBeFalsy();
    });

    test("Switching to acting as user hides manage business button", async () => {
        wrapper.vm.$data.actingAs = "ACTING_AS_CURRENT_USER";
        await wrapper.vm.$nextTick();
        manageBusinessButton = wrapper.find('#createBusiness');
        expect(manageBusinessButton.exists()).toBeFalsy();
    });

})


