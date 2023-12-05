import {createLocalVue, shallowMount} from "@vue/test-utils";
import BootstrapVue from "bootstrap-vue";

import SaleListing from "../views/SaleListing";
import api from "../Api";
import storage_util from "../javascript_modules/storage_util";
import {getImageURL, getPrimaryImage} from "@/javascript_modules/image_util";

jest.mock("../Api");
jest.mock("../javascript_modules/storage_util");
jest.mock("../javascript_modules/image_util");


const getBusinessIdSuccess = 1;

let wrapper;

let $router = {
    push: jest.fn()
}

let $route = {
    path: '/',
    params: {
        businessId: 1
    }
};


const localVue = createLocalVue();
localVue.use(BootstrapVue);

describe("Sale listing", () => {
    describe("test mounted functions with valid responses", () => {

        const primaryImageURL = `http://localhost:9499/businesses/1/products/1A/images/1`;


        beforeAll(async () => {
            storage_util.getActingAs.mockImplementation(() => Promise.resolve(1));
            api.getBusiness.mockResolvedValue({
                data: {
                    address: {
                        country: "New Zealand"
                    },
                    form: {
                        inventoryItemId: "",
                        quantity: "",
                        price: "",
                        moreInfo: "",
                        closes: ""
                    },
                    sellingQuantity: 0
                }
            });

            let fakeInventory = [{
                inventoryItemId: "1A",
                product: {
                    name: "Name",
                    id: "1A",
                    images: {
                        productImageId: 1,
                        primary: true
                    }
                },
                quantity: 100,
                pricePerItem: 10,
                totalPrice: 1000,
                expires: '2030-07-14'
            }];

            const fakeSaleListings = [
                {
                    inventoryItem: fakeInventory[0],
                    id: 1,
                    quantity: 5
                },
                {
                    inventoryItem: fakeInventory[0],
                    id: 2,
                    quantity: 5
                }
            ]

            api.getInventory.mockResolvedValue({
                data: fakeInventory
            });

            api.getSaleListings.mockResolvedValue({
                data: fakeSaleListings
            });

            getPrimaryImage.mockImplementation(() => fakeInventory[0].product.images);
            getImageURL.mockImplementation(() => primaryImageURL);

            wrapper = await shallowMount(SaleListing, {
                localVue,
                mocks: {
                    $route,
                    $router
                }
            });
        });


        test("getBusinessCountry_withValidResponse_setsCorrectCountry", async () => {
            await wrapper.vm.$nextTick();
            await expect(wrapper.vm.$data.businessCountry).toBe("New Zealand");
        });


        test("getInventoryItems_withValidResponse_setsCorrectInventoryItems", async () => {
            await wrapper.vm.$nextTick();
            await expect(wrapper.vm.$data.inventoryItems).toEqual(
                [
                    {
                        "expires": '2030-07-14',
                        "quantity": 90,
                        "pricePerItem": 10,
                        "text": "Name - 1A - Price Per Item: $10.00, Total Price: $1000.00 - Available Quantity: 90",
                        "totalPrice": 1000,
                        "value": "1A",
                        primaryImage: primaryImageURL
                    }
                ]);
        });

        test("getBusinessId_withValidResponse_setsCorrectBusinessId", async () => {
            await wrapper.vm.$nextTick();
            await expect(wrapper.vm.$data.businessId).toBe(getBusinessIdSuccess);
        });

        test('fillInputs_withValidInventoryItem_setCorrectTotalPrice', async () => {
            wrapper.vm.$data.form.inventoryItemId = '1A';
            await wrapper.vm.fillInputs();
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.$data.form.price).toBe(10);
        });

        test('fillInputs_withValidInventoryItem_setCorrectDate', () => {
            wrapper.vm.$data.form.inventoryItemId = '1A';
            wrapper.vm.fillInputs();
            expect(wrapper.vm.$data.form.closes).toBe('2030-07-14T12:00');
        });

        test('calculateTotalPrice_positiveTotal_setCorrectTotalPrice', () => {
            wrapper.vm.$data.form.quantity = 89;
            wrapper.vm.calculateTotalPrice();
            expect(wrapper.vm.$data.form.price).toEqual(890)
        })

        test('calculateTotalPrice_negativeTotal_setTotalPriceToZero', () => {
            wrapper.vm.$data.form.quantity = -10;
            wrapper.vm.calculateTotalPrice();
            expect(wrapper.vm.$data.form.price).toEqual(0)
        })

        test('calculateTotalPrice_zeroQuantity_setCorrectTotalPrice', () => {
            wrapper.vm.$data.form.quantity = 0;
            wrapper.vm.calculateTotalPrice();
            expect(wrapper.vm.$data.form.price).toEqual(0)
        })

        test('calculateTotalPrice_quantityGreaterThanInventoryQuantity_setCorrectTotalPrice', () => {
            wrapper.vm.$data.form.quantity = 5000;
            wrapper.vm.calculateTotalPrice();
            expect(wrapper.vm.$data.form.price).toEqual(900)
        })

        test("getSaleListingsQuantity_inventoryItemExists_setCorrectSellingQuantity", async () => {
            await wrapper.vm.getSaleListingsQuantity("1A");
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.$data.sellingQuantity).toEqual(10)
        })

        test("getSaleListingsQuantity_inventoryItemDoesNotExist_setCorrectSellingQuantity", async () => {
            await wrapper.vm.getSaleListingsQuantity("100");
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.$data.sellingQuantity).toEqual(0)
        })

        describe("Valid input field tests", () => {
            test('isQuantityValid_withValidQuantity_toReturnTrue', () => {
                wrapper.vm.$data.form.quantity = 10;
                expect(wrapper.vm.isQuantityValid()).toBeTruthy();
            });

            test('isClosingDateValid_withValidClosingDate_toReturnTrue', () => {
                wrapper.vm.$data.form.closes = '2030-05-05T12:00';
                expect(wrapper.vm.isClosingDateValid()).toBeTruthy();
            });
        });

        describe("Invalid input field tests", () => {
            test('isQuantityValid_withInvalidQuantity_toReturnFalse', () => {
                wrapper.vm.$data.form.quantity = -111111111111111111111;
                expect(wrapper.vm.isQuantityValid()).toBeFalsy();
            });

            describe("onSubmit function valid form tests", () => {
                let form;

                beforeAll(() => {
                    wrapper.vm.$data.form = {
                        inventoryItemId: "1A",
                        quantity: "10",
                        price: "10",
                        totalPrice: "100",
                        moreInfo: "Hello",
                        closes: "2030-05-05T12:00"
                    }
                    api.addSaleListing.mockImplementation(() => Promise.resolve({status: 201}))
                });

                test("form_findElement_elementExists", () => {
                    form = wrapper.find("#sale-listing-form")
                    expect(form.exists()).toBeTruthy();
                });

                test("form_onSubmit_callsApi", () => {
                    form.trigger("submit");
                    expect(api.addSaleListing).toBeCalled();
                });

                test("form_onSubmit_routerPushed", async () => {
                    await form.trigger("submit");
                    await wrapper.vm.$nextTick();
                    expect($router.push).toBeCalled();
                });

                afterAll(jest.clearAllMocks);
            });

            describe("Api fails test (quantity too high)", () => {
                let form;

                beforeAll(() => {
                    wrapper.vm.$data.form = {
                        inventoryItemId: "1A",
                        quantity: "10000000",
                        price: "10",
                        moreInfo: "Hello",
                        closes: "2030-05-05T12:00"
                    }
                    api.addSaleListing.mockImplementation(() => Promise.reject(
                        {status: 400, data: {message: "Quantity exceeded inventory item quantity."}}));
                });

                test("form_findElement_elementExists", () => {
                    form = wrapper.find("#sale-listing-form");
                    expect(form.exists()).toBeTruthy();
                });
            });
        });
    });
});