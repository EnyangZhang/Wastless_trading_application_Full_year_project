import {timeFormat} from "../javascript_modules/user_profile";
import {createLocalVue, mount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";
import UserProfile from "../views/UserProfile";
import Api from "../Api";
import storage_util from "../javascript_modules/storage_util";

jest.mock("../Api");
jest.mock("../javascript_modules/storage_util");
jest.mock("../javascript_modules/date_util");

let localVue = createLocalVue();
localVue.use(BootstrapVue);


/**
 * Testing the timeFormat method which is used in the UserProfile and SearchUser components.
 */
beforeAll(() => {
    jest.useFakeTimers('modern');
    jest.setSystemTime(new Date(2021, 2, 17));
});

afterAll(() => {
    jest.useRealTimers();
});

describe('Time formatter', () => {
    test('Does time formatter formats the time correctly(past)', () => {
        const memberDate = "2020-07-14T14:32:00Z"
        const output = "14 July 2020 (8 months)"
        expect(timeFormat(memberDate)).toBe(output);
    });

    test('Does time formatter formats the time correctly for another date(past)', () => {
        const memberDate = "2021-03-3T20:32:00Z"
        const output = "3 March 2021 (0 months)"
        expect(timeFormat(memberDate)).toBe(output);
    });

    test('Does time formatter formats the time correctly for another date(past)', () => {
        const memberDate = "2021-03-15T20:32:00Z"
        const output = "15 March 2021 (0 months)"
        expect(timeFormat(memberDate)).toBe(output);
    });

    test('Does time formatter formats the time correctly for current date', () => {
        const memberDate = "2021-03-17T01:00:00Z"
        const output = "17 March 2021 (0 months)"
        expect(timeFormat(memberDate)).toBe(output);
    });

    test('Does time formatter formats the time correctly for tomorrow', () => {
        const memberDate = "2021-03-18T01:00:00Z"
        const output = "Unexpected date."
        expect(timeFormat(memberDate)).toBe(output);
    });

    test('Does time formatter formats the time correctly for the day after tomorrow', () => {
        const memberDate = "2021-03-19T01:00:00Z"
        const output = "Unexpected date."
        expect(timeFormat(memberDate)).toBe(output);
    });

    test('Does time formatter formats the time correctly for 2 days tomorrow', () => {
        const memberDate = "2021-03-20T01:00:00Z"
        const output = "Unexpected date."
        expect(timeFormat(memberDate)).toBe(output);
    });
});

const admin = {
    userId: 1,
    firstName: "John",
    lastName: "Smith",
    middleName: "",
    nickname: "",
    bio: "",
    email: "test@test.com",
    dateOfBirth: "2020-07-14T14:32:00Z",
    phoneNumber: "0800123123",
    homeAddress: {
        streetName: "Test street",
        streetNumber: "123",
        suburb: "Test Suburb",
        city: "Test City",
        country: "Test Country",
        region: "Test Region",
        postcode: "Test Postcode"
    },
    businessesAdmin: [],
    role: "global_admin",
    created: "2020-07-14T14:32:00Z"
}

const user = {
    userId: 2,
    firstName: "Jimmy",
    lastName: "Jones",
    middleName: "",
    nickname: "",
    bio: "",
    email: "jimmjones@test.com",
    dateOfBirth: "2020-07-14T14:32:00Z",
    phoneNumber: "1234560",
    homeAddress: {
        streetName: "Test street",
        streetNumber: "123",
        suburb: "Test Suburb",
        city: "Test City",
        country: "Test Country",
        region: "Test Region",
        postcode: "Test Postcode"
    },
    businessesAdmin: [],
    role: "user",
    created: "2020-07-14T14:32:00Z"
}


describe("User profile is mounted - I am viewing my own profile (ID = 2)", () => {

    let $route = {
        params: {
            id: 2
        }
    }

    const $router = {
        push: jest.fn()
    };

    let options = {
        localVue,
        mocks: {
            $route,
            $router
        }
    };
    let wrapper;
    let userDetails;
    let toMarketplaceCards;

    beforeEach(async () => {
        Api.getUser.mockImplementation(() => Promise.resolve({status: 200, data: user}));
        storage_util.getCurrentUser.mockImplementation(() => {return 2});
        storage_util.getCurrentUserInfo.mockImplementation(() => {return user});
        wrapper = mount(UserProfile, options);
        toMarketplaceCards = wrapper.find("#to-marketplace-cards-btn");
        userDetails = wrapper.find("#user-details");
        await wrapper.vm.getUserInfo();
        await wrapper.vm.$nextTick();
        await wrapper.vm.$nextTick();
    });

    afterEach(() => {
        wrapper.destroy();
        jest.clearAllMocks();
    });

    test("toMarketplaceCards_findElement_elementRenders", () => {
        expect(toMarketplaceCards.exists()).toBeTruthy();
    });

    const allDetailsVisibleTest = () => {
        test("userProfile_componentMounted_componentRenders", () => {
            expect(wrapper.exists()).toBeTruthy();
        });

        test("userProfile_componentMounted_canViewTrue", async () => {
            expect(await wrapper.vm.canView).toBe(true)
        });

        test("userDetails_findElement_dateOfBirthVisible", () => {
            expect(userDetails.text()).toContain(user.dateOfBirth);
        });

        test("userDetails_findElement_PhoneNumberVisible", () => {
            expect(userDetails.text()).toContain(user.phoneNumber);
        });

        test("userDetails_findElement_roleVisible", () => {
            expect(userDetails.text()).toContain("User");
        });

        test("userDetails_findElement_fullAddressVisible", () => {
            for (const key in user.homeAddress) {
                expect(userDetails.text()).toContain(user.homeAddress[key]);
            }
        });

        test("toMarketplaceCards_triggerClick_routerIsCalled", () => {
            toMarketplaceCards.trigger("click");
            expect(wrapper.vm.$router.push).toBeCalledWith(`/my-cards/${user.userId}`)
        });
    }

    describe("I am viewing my own profile: ID = 2", () => {
        allDetailsVisibleTest();
    });

    describe("I am viewing someone else's profile (ID=2) as an admin", () => {

        beforeEach(async () => {
            storage_util.getCurrentUser.mockImplementation(() => {return 1});
            storage_util.getCurrentUserInfo.mockImplementation(() => {return admin});
            wrapper = mount(UserProfile, options);
            await wrapper.vm.$nextTick();
        });

        test("viewingAsAdmin_getValue_valueIsTrue", async () => {
            expect(await wrapper.vm.viewingAsAdmin).toBe(true);
        });

        allDetailsVisibleTest();
    });

    describe("I am viewing someone else's profile (ID = 1)", () => {

        beforeEach(async () => {
            $route.params.id = 1;
            Api.getUser.mockImplementation(() => Promise.resolve({status: 200, data: admin}));
            wrapper = mount(UserProfile, options)
            await wrapper.vm.$nextTick()
        });

        test("userProfile_componentMounted_canViewTrue", async () => {
            expect(await wrapper.vm.canView).not.toBe(true)
        });

        test("userProfile_componentMounted_componentRenders", () => {
            expect(wrapper.exists()).toBeTruthy();
        });

        test("userDetails_findElement_dateOfBirthVisible", () => {
            expect(userDetails.text()).not.toContain(admin.dateOfBirth);
        });

        test("userDetails_findElement_PhoneNumberVisible", () => {
            expect(userDetails.text()).not.toContain(admin.phoneNumber);
        });

        test("userDetails_findElement_roleVisible", () => {
            expect(userDetails.text()).not.toContain("User");
        });

        test("userDetails_findElement_streetNumberNotVisible", () => {
            expect(userDetails.text()).not.toContain(admin.homeAddress.streetNumber);
        });

        test("userDetails_findElement_streetNameNotVisible", () => {
            expect(userDetails.text()).not.toContain(admin.homeAddress.streetName);

        });

        test("userDetails_findElement_suburbNotVisible", () => {
            expect(userDetails.text()).not.toContain(admin.homeAddress.suburb);
        });

        test("userDetails_findElement_postcodeNotVisible", () => {
            expect(userDetails.text()).not.toContain(admin.homeAddress.postcode);
        });

        test("userDetails_findElement_cityVisible", () => {
            expect(userDetails.text()).toContain(admin.homeAddress.city)
        });

        test("userDetails_findElement_regionVisible", () => {
            expect(userDetails.text()).toContain(admin.homeAddress.region)
        });

        test("userDetails_findElement_countryVisible", () => {
            expect(userDetails.text()).toContain(admin.homeAddress.country)
        });

        test("toMarketplaceElement_triggerClick_routerIsCalled", () => {
            toMarketplaceCards.trigger("click");
            expect(wrapper.vm.$router.push).toBeCalledWith(`/user-cards/${admin.userId}`)
        });
    });
});
