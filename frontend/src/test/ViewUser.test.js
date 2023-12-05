import {createLocalVue, shallowMount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";
import {getUser} from "./resources/TestUsers";
import User from "../components/ViewUser";

const localVue = createLocalVue();
localVue.use(BootstrapVue);
jest.mock('vue-router');

const $router = {
    push: jest.fn()
}
const $route = {

}

describe("The user is mounted with appropriate user data (primary admin badge is not present)", () => {

    const options = {
        localVue,
        props: {
            user: Object,
            isPrimaryAdmin: Boolean
        },
        propsData: {
            user: getUser(),
            isPrimaryAdmin: false
        },
        mocks: {
            $route,
            $router
        }
    }
    const handleClick = jest.spyOn(User.methods, "handleClick");

    const wrapper = shallowMount(User, options);
    const user = wrapper.find("#user");
    const name = wrapper.find("#name");
    const avatar = wrapper.find("#avatar");
    const primaryAdminBadge = wrapper.find("#primaryAdminBadge");

    test("User renders", () => {
        expect(user.exists()).toBeTruthy();
    });

    test("User displays the user's name as 'firstName' 'lastName'", () => {
        const firstName = wrapper.vm.$props.user.firstName;
        const lastName = wrapper.vm.$props.user.lastName;
        const expected = `${firstName} ${lastName}`;
        expect(name.text()).toBe(expected);
    });

    test("Avatar renders", () => {
        expect(avatar.exists()).toBeTruthy();
    });

    test("Hover status is false", () => {
        const isHovered = wrapper.vm.$data.isHovered;
        expect(isHovered).toBeFalsy();
    });

    test("primary admin badge does not render", () => {
        expect(primaryAdminBadge.exists()).toBeFalsy();
    });

    test("Hovering over user sets hover status to true", () => {
        user.trigger("mouseover");
        const isHovered = wrapper.vm.$data.isHovered;
        expect(isHovered).toBeTruthy();
    });

    test("Clicking on the user triggers handClick method", () => {
        user.trigger("click");
        expect(handleClick).toBeCalled();
    });

    test("If the hover status is true, moving mouse away sets hover status to false", async () => {
        wrapper.vm.$data.isHovered = true;
        user.trigger("mouseleave");
        const isHovered = wrapper.vm.$data.isHovered;
        expect(isHovered).toBeFalsy();
    });

    options.propsData.isPrimaryAdmin = true;
});

describe("The component is mounted with appropriate data and the primary admin prop is set to true", () => {

    const options = {
        localVue,
        props: {
            user: Object,
            isPrimaryAdmin: Boolean
        },
        propsData: {
            user: getUser(),
            isPrimaryAdmin: true
        },
        mocks: {
            $route,
            $router
        }
    }

    const wrapper = shallowMount(User, options);
    const primaryAdminBadge = wrapper.find("#primaryBadge");

    test("The primary admin badge renders", () => {
        expect(primaryAdminBadge.isVisible()).toBeTruthy();
    });
});