import {getCards} from "@/test/resources/TestCards";
import BootstrapVue, {BootstrapVueIcons} from "bootstrap-vue";
import {createLocalVue, mount} from "@vue/test-utils";
import CommunityMarketplaceCard from "../components/community-marketplace/CommunityMarketplaceCard";
import {formatDateString} from "@/javascript_modules/date_util";
import storage_util from "../javascript_modules/storage_util";
import Api from "../Api";
import roles from "../validation/roles.validation";

const localVue = createLocalVue();
localVue.use(BootstrapVue);
localVue.use(BootstrapVueIcons);

jest.mock("../Api");
jest.mock("../javascript_modules/storage_util");
jest.mock('vue-router');

const $router = {
    push: jest.fn()
}

let $route = {
    path: '/community-marketplace',
    name: 'CommunityMarketplace',
    meta: {
        requiresAuth: true
    }
}

const options = {
    localVue,
    props: {
        card: Object
    },
    propsData: {
        card: getCards().forSaleCards[0]
    },
    mocks: {
        $route,
        $router
    }
}

describe("Community Marketplace card - all tests", () => {

    jest.mock("../Api");
    Api.deleteMarketplaceCard = jest.fn();
    Api.deleteMarketplaceCard.mockResolvedValue({status: 200});

    const viewCardDetails = jest.spyOn(CommunityMarketplaceCard.methods, "viewCardDetails");
    const deleteTrigger = jest.spyOn(CommunityMarketplaceCard.methods, "deleteTrigger");

    // stub storage util to return information about the current user
    storage_util.getActingAs.mockImplementation(() => {return storage_util.ACTING_AS_CURRENT_USER});
    storage_util.getCurrentUser.mockImplementation(() => {return 1});
    storage_util.getCurrentUserInfo.mockImplementation(() => {return {role: roles.DEFAULT_GLOBAL_APPLICATION_ADMIN}});

    const wrapper = mount(CommunityMarketplaceCard, options);
    const title = wrapper.find("#card-title");
    const image = wrapper.find("#card-image");
    const creator = wrapper.find("#creator-info");
    const location = wrapper.find("#creator-location");
    const description = wrapper.find("#shortened-description");
    const keywords = wrapper.find("#keywords");
    const created = wrapper.find("#created-time");
    const expires = wrapper.find("#expires-time");
    const deleteButton = wrapper.find("#delete-card");
    const clickableRegion = wrapper.find("#clickable-region");
    const confirmDeleteOverlay = wrapper.find("#confirm-delete-overlay");
    const card = wrapper.vm.$props.card;

    describe("Components correctly render", () => {

        test("card_mountComponent_wrapperRenders", () => {
            expect(wrapper.exists()).toBeTruthy();
        });

        test("title_findElement_elementRenders", () => {
            expect(title.exists()).toBeTruthy();
        });

        test("title_elementText_matchesCardTitle", () => {
            expect(title.text()).toBe(card.title);
        });

        test("image_findElement_elementRenders", () => {
            expect(image.exists()).toBeTruthy();
        });

        test("creator_findElement_elementRenders", () => {
            expect(creator.exists()).toBeTruthy();
        });

        test("creator_elementText_matchesCreatorName", () => {
            const name = `${card.creator.firstName} ${card.creator.lastName}`
            expect(creator.text()).toContain(name);
        });

        test("location_findElement_elementRenders", () => {
            expect(location.exists()).toBeTruthy();
        });

        test("confirmDeleteOverlay_findElement_elementRenders", () => {
            expect(confirmDeleteOverlay.exists()).toBeTruthy();
        });

        test("confirmDeleteOverlay_elementProperties_isNotVisible", () => {
            expect(confirmDeleteOverlay.props().show).toBe(false);
        });

        test("location_elementText_containsCreatorSuburb", () => {
            const homeAddress = card.creator.homeAddress;
            expect(location.text()).toContain(homeAddress.suburb);
        });

        test("location_elementText_containsCreatorCity", () => {
            const homeAddress = card.creator.homeAddress;
            expect(location.text()).toContain(homeAddress.city);
        })

        test("description_elementText_matchesCardDescription", () => {
            expect(description.exists()).toBeTruthy();
        });

        test("description_elementText_matchesCardDescription", () => {
            expect(description.text()).toBe(card.description);
        })

        test("keywords_findElement_elementRenders", () => {
            expect(keywords.exists()).toBeTruthy();
        });

        test("keywords_elementText_matchesCardKeywords", () => {
            card.keywords.forEach((keyword) => {
                expect(keywords.text()).toContain(keyword.name);
            });
        });

        test("createdDate_findElement_elementRenders", () => {
            expect(created.exists()).toBeTruthy();
        });

        test("createdDate_elementText_matchesCardCreatedDate", () => {
            const formattedDate = formatDateString(card.created);
            expect(created.text()).toContain(formattedDate);
        });

        test("expiryDate_elementText_matchesCardExpiryDate", () => {
            const formattedDate = formatDateString(card.displayPeriodEnd);
            expect(expires.text()).toContain(formattedDate);
        });
    });

    describe("Additional information is accessible", () => {

        test("handleHover_notTriggered_isHoveredFalse", () => {
            expect(wrapper.vm.$data.isHovered).toBe(false);
        });

        test("hoverTriggered_hoverTrue_isHoveredSet", async () => {
            await wrapper.vm.handleHover(true);
            expect(wrapper.vm.$data.isHovered).toBe(true);
        });

        test("handleHover_hoverFalse_isHoveredSet", async () => {
            await wrapper.vm.handleHover(false);
            expect(wrapper.vm.$data.isHovered).toBe(false);
        });

        test("clickableRegion_checkExists_elementExists", () => {
            expect(clickableRegion.exists()).toBeTruthy();
        });

        test("title_clickEvent_callbackCalled1Time", () => {
            clickableRegion.trigger("click");
            expect(viewCardDetails).toBeCalledTimes(1);
        });
    });

    describe("Deleting a card", () => {

        Api.deleteMarketplaceCard.mockImplementation(() =>
            Promise.resolve({status: 200, data: "card has been deleted"}));
        window.alert = () => {};


        test("deleteCard_clickDelete_callbackCalled", async () => {
            await deleteButton.trigger("click");
            expect(deleteTrigger).toBeCalled();
        });

        test("deleteCard_clickDelete_overlayIsVisible", async () => {
            await deleteButton.trigger("click");
            expect(confirmDeleteOverlay.props().show).toBe(true);
        });

        test("deleteCard_cancelDelete_overlayHidden", async () => {
            await deleteButton.trigger("click");
            await wrapper.vm.deleteCancel();
            expect(confirmDeleteOverlay.props().show).toBe(false);
        });

        test("deleteCard_confirmDelete_callsApi", async () => {
            await wrapper.vm.deleteCard();
            expect(Api.deleteMarketplaceCard).toBeCalled();
        });

        test("deleteCard_clickDelete_apiCallMade", async () => {
            await deleteButton.trigger("click");
            expect(Api.deleteMarketplaceCard).toBeCalled();
        });

        test("deleteCard_clickDelete_cardDeletedEventEmitted", async () => {
            await deleteButton.trigger("click");
            expect(wrapper.emitted().cardDeleted).toBeTruthy()
        });
    });
});