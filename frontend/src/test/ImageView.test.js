import {createLocalVue, shallowMount} from "@vue/test-utils";
import ImageView from "@/components/ImageView";
import BootstrapVue from "bootstrap-vue";


const localVue = createLocalVue();
localVue.use(BootstrapVue);

let wrapper;

describe("ImageView Component is mounted with an array of images", () => {

    let options = {
        localVue,
        props: {
            images: Array
        },
        propsData: {
            images: [
                'https://picsum.photos/1024/480/?image=52',
                'https://picsum.photos/1024/480/?image=54',
                'https://picsum.photos/1024/480/?image=58'
            ]
        }
    };

    beforeEach(() => {
        wrapper = shallowMount(ImageView, options);
    });

    test("imageView_mounted_componentRenders", () => {
        expect(wrapper.exists()).toBeTruthy();
    });

    test("imageView_findErrorMessage_elementNotVisible", () => {
        const errorMessage = wrapper.find("#error-message");
        expect(errorMessage.exists()).toBeFalsy();
    });

    test("imageView_findDisplay_componentRenders", () => {
        const carousel = wrapper.find("#image-display");
        expect(carousel.exists()).toBeTruthy()
    });

    test("imageView_findImages_correctNumber", () => {
        const images = wrapper.findAllComponents({name: "clickable-image"});
        const expectedNumber = wrapper.vm.$props.images.length;
        expect(images.length).toBe(expectedNumber);
    });

    test("imageView_findDefault_componentDoesNotRender", () => {
        const defaultSlide = wrapper.find("#default");
        expect(defaultSlide.exists()).toBeFalsy();
    });

    test("imageView_triggerImageClicked_emitsEventAndImageId", async () => {
        await wrapper.vm.handleImageClicked(0);
        expect(wrapper.emitted()["setAsPrimary"]).toEqual([[0]])
    });
});

describe("ImageView Component is mounted with an empty array", () => {

    let options = {
        localVue,
        props: {
            images: Array
        },
        propsData: {
            images: []
        }
    };

    beforeEach(() => {
        wrapper = shallowMount(ImageView, options);
    });

    test("imageView_mounted_componentRenders", () => {
        expect(wrapper.exists()).toBeTruthy();
    });

    test("imageView_findErrorMessage_elementVisible", () => {
        const errorMessage = wrapper.find("#error-message");
        expect(errorMessage.exists()).toBeTruthy();
    });

    test("imageView_findErrorMessage_containsCorrectMessage", () => {
        const expectedMessage = "No images to display";
        const errorMessage = wrapper.find("#error-message");
        expect(errorMessage.text()).toBe(expectedMessage)
    });

    test("imageView_findCarousel_componentDoesNotRender", () => {
        const carousel = wrapper.find("#image-display");
        expect(carousel.exists()).toBeFalsy();
    });

    test("imageView_findDefault_componentRenders", () => {
        const defaultSlide = wrapper.find("#default");
        expect(defaultSlide.exists()).toBeTruthy();
    });
});

describe("Image view is mounted with some hypothetical erroneous data", () => {

        let options = {
            localVue,
            props: {
                images: Array
            },
            propsData: {
                images: ["Some bad data (but not an empty array)"]
            },
            computed: {
                errorFlag() {
                    return true;
                }
            }
        };

        beforeEach(() => {
            wrapper = shallowMount(ImageView, options);
        });

        test("imageView_findError_elementRenders", () => {
            const errorMessage = wrapper.find("#error-message");
            expect(errorMessage.exists()).toBeTruthy();
        });

        test("errorMessage_messageText_expected", () => {
            const errorMessage = wrapper.find("#error-message");
            const expectedMessage = "Can't display images (unknown error)";
            expect(errorMessage.text()).toBe(expectedMessage);
        });
});