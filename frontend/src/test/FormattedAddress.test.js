import {createLocalVue, shallowMount} from "@vue/test-utils";
import FormattedAddress from "../components/address/FormattedAddress";

const localVue = createLocalVue();

let options;
let wrapper;
let formattedAddress;

beforeEach(() => {
    options = {
        localVue,
        props: {
            address: Object,
            fullAddress: Boolean
        },
        propsData: {
            address: undefined,
            fullAddress: true
        }
    }
});

describe("The component is mounted with an address that contains all address details", () => {

    const testAddress = {
        "streetNumber": "3/24",
        "streetName": "Ilam Road",
        "suburb": "Upper Riccarton",
        "city": "Christchurch",
        "region": "Canterbury",
        "country": "New Zealand",
        "postcode": "90210"
    };

    beforeEach(() => {
        options.propsData.address = testAddress;
        wrapper = shallowMount(FormattedAddress, options);
        formattedAddress = wrapper.find("#formatted-address");
    });

    test("address_componentMounted_wrapperRenders", () => {
        expect(wrapper.exists()).toBeTruthy();
    });

    test("address_findElement_elementRenders", () => {
        expect(formattedAddress.exists()).toBeTruthy();
    });

    test("addressText_withAllDetails_inCorrectOrder", () => {
        const expectedFormat = `${testAddress.streetNumber}, `+
                               `${testAddress.streetName}, ` +
                               `${testAddress.suburb}, ` +
                               `${testAddress.city}, ` +
                               `${testAddress.postcode}, ` +
                               `${testAddress.region}, ` +
                               `${testAddress.country}`;
        expect(formattedAddress.text()).toBe(expectedFormat);
    });
});

describe("The component is mounted with various fields invalid", () => {

    let testAddress;

    beforeEach(() => {
        testAddress = {
            "streetNumber": "3/24",
            "streetName": "Ilam Road",
            "suburb": "Upper Riccarton",
            "city": "Christchurch",
            "region": "Canterbury",
            "country": "New Zealand",
            "postcode": "90210"
        };
    });

    function mount() {
        options.propsData.address = testAddress;
        wrapper = shallowMount(FormattedAddress, options);
        formattedAddress = wrapper.find("#formatted-address");
    }

    test("addressText_propFieldNull_formatCorrect", () => {
        const expectedFormat = `${testAddress.streetName}, ` +
                               `${testAddress.suburb}, ` +
                               `${testAddress.city}, ` +
                               `${testAddress.postcode}, ` +
                               `${testAddress.region}, ` +
                               `${testAddress.country}`;
        testAddress.streetNumber = null;
        mount();
        expect(formattedAddress.text()).toBe(expectedFormat);
    });

    test("addressText_propFieldUndef_formatCorrect", () => {
        const expectedFormat = `${testAddress.streetName}, ` +
                                `${testAddress.suburb}, ` +
                                `${testAddress.city}, ` +
                                `${testAddress.postcode}, ` +
                                `${testAddress.region}, ` +
                                `${testAddress.country}`;
        testAddress.streetNumber = undefined;
        mount();
        expect(formattedAddress.text()).toBe(expectedFormat);
    });

    test("addressText_propFieldMissing_formatCorrect", () => {
        const expectedFormat = `${testAddress.streetName}, ` +
            `${testAddress.suburb}, ` +
            `${testAddress.city}, ` +
            `${testAddress.postcode}, ` +
            `${testAddress.region}, ` +
            `${testAddress.country}`;
        delete testAddress.streetNumber;
        mount();
        expect(formattedAddress.text()).toBe(expectedFormat);
    });

    test("addressText_fieldInMiddleInvalid_formatCorrect", () => {
        const expectedFormat = `${testAddress.streetNumber}, `+
            `${testAddress.streetName}, ` +
            `${testAddress.suburb}, ` +
            `${testAddress.postcode}, ` +
            `${testAddress.region}, ` +
            `${testAddress.country}`;
        delete testAddress.city;
        mount();
        expect(formattedAddress.text()).toBe(expectedFormat);
    });

    test("addressText_startAndMiddleFieldInvalid_formatCorrect", () => {
        const expectedFormat = `${testAddress.streetName}, ` +
            `${testAddress.suburb}, ` +
            `${testAddress.city}, ` +
            `${testAddress.region}, ` +
            `${testAddress.country}`;
        delete testAddress.streetNumber;
        delete testAddress.postcode;
        mount();
        expect(formattedAddress.text()).toBe(expectedFormat);
    });

    test("addressText_endFieldInvalid_formatCorrect", () => {
        const expectedFormat = `${testAddress.streetNumber}, `+
            `${testAddress.streetName}, ` +
            `${testAddress.suburb}, ` +
            `${testAddress.city}, ` +
            `${testAddress.postcode}, ` +
            `${testAddress.region}`;
        delete testAddress.country;
        mount();
        expect(formattedAddress.text()).toBe(expectedFormat);
    });
});

describe("The component is mounted with the fullAddress prop set to false (or is not set)", () => {

    const testAddress = {
        "streetNumber": "3/24",
        "streetName": "Ilam Road",
        "suburb": "Upper Riccarton",
        "city": "Christchurch",
        "region": "Canterbury",
        "country": "New Zealand",
        "postcode": "90210"
    };

    beforeEach(() => {
        options.propsData.fullAddress = false;
        options.propsData.address = testAddress;
        wrapper = shallowMount(FormattedAddress, options);
        formattedAddress = wrapper.find("#formatted-address");
    });

    test("addressText_withAllDetails_displaysNoStreetNumber", () => {
        expect(formattedAddress.text()).not.toContain(testAddress.streetNumber);
    });

    test("addressText_withAllDetails_displaysNoStreetName", () => {
        expect(formattedAddress.text()).not.toContain(testAddress.streetName);
    });

    test("addressText_withAllDetails_displaysNoSuburb", () => {
        expect(formattedAddress.text()).not.toContain(testAddress.suburb);
    });

    test("addressText_withAllDetails_displaysNoPostcode", () => {
        expect(formattedAddress.text()).not.toContain(testAddress.postcode);
    });

    test("addressText_withAllDetails_displaysRegion", () => {
        expect(formattedAddress.text()).toContain(testAddress.region);
    });

    test("addressText_withAllDetails_displaysCity", () => {
        expect(formattedAddress.text()).toContain(testAddress.city);
    });

    test("addressText_withAllDetails_displaysCountry", () => {
        expect(formattedAddress.text()).toContain(testAddress.country);
    });
});