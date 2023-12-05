import {createLocalVue, shallowMount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";
import ClickableImage from "@/components/ClickableImage";

let localVue = createLocalVue();
localVue.use(BootstrapVue);

let wrapper;
let options;

beforeEach(() => {
    options = {
        localVue,
        props: {
            src: [ArrayBuffer, String],
            overlayIcon: String,
            overlayMessage: String,
            readOnly: Boolean
        },
        propsData: {
            src: (new ArrayBuffer(8).toString()),
            overlayMessage: "TestMessage",
            readOnly: false
        },
        data() {
            return {
                isHovered: false
            }

        }
    };
});

describe("Clickable Image component is mounted with no prop data, and read-only is false", () => {

    beforeEach(() => {
        wrapper = shallowMount(ClickableImage, options);
    });

    test("clickableImage_componentMounted_componentRenders", () => {
        expect(wrapper.exists()).toBeTruthy();
    });

    test("clickableImage_findOverlay_elementRenders", () => {
        const overlay = wrapper.find("#image-container");
        expect(overlay.exists()).toBeTruthy();
    });

    test("overlay_findImage_elementRenders", () => {
        const image = wrapper.find("#image");
        expect(image.exists()).toBeTruthy();
    });

    test("component_clicked_emitsEvent", async () => {
        wrapper.vm.handleClicked();
        await wrapper.vm.$nextTick();
        expect(wrapper.emitted()["imageClicked"]).toEqual([["[object ArrayBuffer]"]]);
    });

    test("removeButton_clicked_emitsEvent", async () => {
        wrapper.vm.handleRemove();
        await wrapper.vm.$nextTick();
        expect(wrapper.emitted()["imageRemoved"]).toEqual([["[object ArrayBuffer]"]]);
    });

    test("component_mouseOver_setsHoverToTrue", async () => {
        wrapper.trigger("mouseover");
        const isHovered = wrapper.vm.$data.isHovered;
        expect(isHovered).toBe(true)
    });

    test("component_handleHover(false)_setsHoverToFalse", async () => {
        wrapper.vm.$data.isHovered = true;
        wrapper.trigger("mouseleave");
        const isHovered = wrapper.vm.$data.isHovered;
        expect(isHovered).toBe(false);
    });

    test("overlay_notHovering_isNotVisible", async () => {
        wrapper.trigger("mouseleave");
        await wrapper.vm.$nextTick();

        const overlay = wrapper.find('#image-container');
        const isVisible = overlay.vm.$options.propsData.show;
        expect(isVisible).toBe(false);
    });

    test("component_hoverOn_overlayVisible", async () => {
        wrapper.trigger("mouseover")
        await wrapper.vm.$nextTick();

        const overlay = wrapper.find("#image-container");
        const isVisible = overlay.vm.$options.propsData.show;

        expect(isVisible).toBe(true);
    });
});

describe("ClickableImage is mounted when read-only is true", () => {
    beforeEach(() => {
        options.propsData.readOnly = true;
        wrapper = shallowMount(ClickableImage, options);
    });

    test("clickableImage_componentMounted_componentRenders", () => {
        expect(wrapper.exists()).toBeTruthy();
    });

    test("component_whenClicked_doesNotEmitEvent", () => {
        wrapper.trigger("click");
        expect(wrapper.emitted()["imageClicked"]).not.toEqual([["[object ArrayBuffer]"]]);
    });

    test("component_mouseOver_hoverIsNotTrue", () => {
        wrapper.trigger("mouseover");
        const isHovered = wrapper.vm.$data.isHovered;
        expect(isHovered).toBe(false);
    });

    test("component_handleHover(false)_hoverIsFalse", async () => {
        wrapper.vm.$data.isHovered = true;
        wrapper.trigger("mouseleave");
        const isHovered = wrapper.vm.$data.isHovered;
        expect(isHovered).toBe(false);
    });

    test("overlay_notHovering_isNotVisible", async () => {
        wrapper.trigger("mouseleave")
        await wrapper.vm.$nextTick();

        const overlay = wrapper.find('#image-container');
        const isVisible = overlay.vm.$options.propsData.show;
        expect(isVisible).toBe(false);
    });

    test("component_hoverOn_overlayNotVisible", async () => {
        wrapper.trigger("mouseover");
        await wrapper.vm.$nextTick();

        const overlay = wrapper.find("#image-container");
        const isVisible = overlay.vm.$options.propsData.show;

        expect(isVisible).toBe(false);
    });
});