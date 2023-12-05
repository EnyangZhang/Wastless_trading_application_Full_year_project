import Inventory from "../views/Inventory";
import {createLocalVue, shallowMount} from "@vue/test-utils";

import storage_util from "../javascript_modules/storage_util";
import BootstrapVue from "bootstrap-vue";
import api from "../Api.js";

jest.mock("../javascript_modules/storage_util");
jest.mock("../Api");
jest.mock("../javascript_modules/image_util");

const getBusinessIdSuccess = 1;

let wrapper;

const $route = {
    path: '/'
};

const localVue = createLocalVue();

localVue.use(BootstrapVue);

describe("Inventory", () => {
    describe("Test mounted functions with valid responses", () => {
        beforeAll(async () => {
            storage_util.getActingAs.mockImplementation(() => Promise.resolve(1))
            api.getBusiness.mockResolvedValue({
                data: {
                    address: {
                        country: "New Zealand"
                    }
                }
            });
            api.getBusinessCatalogue.mockResolvedValue({
                    data: [{
                        id: "MING_A_BING",
                        name: "Ming-a-Bing",
                        recommendedRetailPrice: 3.14
                    }]
                }
            );
            wrapper = await shallowMount(Inventory, {
                localVue,
                mocks: {
                    $route,
                }
            });
        });

        test("getBusinessCountry_withValidResponse_setsCorrectCountry", async () => {
            await wrapper.vm.$nextTick();
            await expect(wrapper.vm.$data.businessCountry).toBe("New Zealand");
        });

        test("getProducts_withValidResponse_setsCorrectProducts", async () => {
            await wrapper.vm.$nextTick();
            await expect(wrapper.vm.$data.products).toEqual(
                [
                    {
                        "text": "Ming-a-Bing - MING_A_BING",
                        "value": "MING_A_BING",
                        "price": 3.14
                    }]);
        });

        test("getBusinessId_withValidResponse_setsCorrectBusinessId", async () => {
            await wrapper.vm.$nextTick();
            await expect(wrapper.vm.$data.businessId).toBe(getBusinessIdSuccess);
        });

        describe("Invalid input fields tests", () => {
            beforeEach( () => {
                wrapper.vm.$data.form.quantity = -1;
                wrapper.vm.$data.form.pricePerItem = -1;
                wrapper.vm.$data.form.totalPrice = -1;
            });

            test("isQuantityValid_withInvalidQuantity_toReturnFalse", () => {
                expect(wrapper.vm.isQuantityValid()).toBeFalsy();
            });

            test("isPricePerItemValid_withInvalidPricePerItem_toReturnFalse", () => {
                expect(wrapper.vm.isPricePerItemValid()).toBeFalsy();
            });

            test("isTotalPriceValid_withInvalidTotalPrice_toReturnFalse", () => {
                expect(wrapper.vm.isTotalPriceValid()).toBeFalsy();
            });
        });

        describe("Valid input fields tests", () => {
            beforeEach( () => {
                wrapper.vm.$data.form.quantity = 3;
                wrapper.vm.$data.form.pricePerItem = 5;
                wrapper.vm.$data.form.totalPrice = 15;
            });

            test("isQuantityValid_withValidQuantity_toReturnTrue", () => {
                expect(wrapper.vm.isQuantityValid()).toBeTruthy();
            });

            test("isPricePerItemValid_withValidPricePerItem_toReturnTrue", () => {
                expect(wrapper.vm.isPricePerItemValid()).toBeTruthy();
            });

            test("isTotalPriceValid_withValidTotalPrice_toReturnTrue", () => {
                expect(wrapper.vm.isTotalPriceValid()).toBeTruthy();
            });
        });

        describe("Past dates inputs test", () => {
            beforeEach(() => {
                wrapper.vm.$data.form.manufactured = "2000-1-1";
                wrapper.vm.$data.form.sellBy = "1900-1-1";
                wrapper.vm.$data.form.bestBefore = "1900-1-1";
                wrapper.vm.$data.form.expires = "1900-1-1";
            });

            test("isManufacturedValid_withValidManufacturedDate_toBeTrue", () => {
                expect(wrapper.vm.isManufacturedValid()).toBeTruthy();
            });

            test("isSellByValid_withPastSellByDate_toBeTrue", () => {
                expect(wrapper.vm.isSellByValid()).toBeTruthy();
            });

            test("isBestBeforeValid_withPastBestBeforeDate_toBeTrue", () => {
                expect(wrapper.vm.isBestBeforeValid()).toBeTruthy();
            });

            test("isExpiryValid_withPastExpiryDate_toBeTrue", () => {
                expect(wrapper.vm.isExpiryValid()).toBeTruthy();
            });
        });

        describe("Valid dates inputs test", () => {
            beforeEach(() => {
                wrapper.vm.$data.form.manufactured = "2100-1-1"
                wrapper.vm.$data.form.sellBy = "2100-1-1"
                wrapper.vm.$data.form.bestBefore = "2100-1-1"
                wrapper.vm.$data.form.expires = "2100-1-1"
            });

            test("isManufacturedValid_withPastManufacturedDate_toBeFalse", () => {
                expect(wrapper.vm.isManufacturedValid()).toBeFalsy();
            });

            test("isSellByValid_withValidSellByDate_toBeTrue", () => {
                expect(wrapper.vm.isSellByValid()).toBeTruthy();
            });

            test("isBestBeforeValid_withValidBestBeforeDate_toBeTrue", () => {
                expect(wrapper.vm.isBestBeforeValid()).toBeTruthy();
            });

            test("isExpiryValid_withValidExpiryDate_toBeTrue", () => {
                expect(wrapper.vm.isExpiryValid()).toBeTruthy();
            });
        });
    });
});