import Product from "../views/Product"
import {createLocalVue, shallowMount} from "@vue/test-utils";

import storage_util from "@/javascript_modules/storage_util";
import {getCurrency} from "@/javascript_modules/currency_util"
import BootstrapVue from "bootstrap-vue";
import Api from "@/Api";

jest.mock("../javascript_modules/storage_util")
jest.mock("../javascript_modules/currency_util")
jest.mock("../Api")

let wrapper;

const localVue = createLocalVue()

localVue.use(BootstrapVue)

describe("Product", () => {
    describe("Test mounted functions with valid responses", () => {
        beforeAll(() => {
            storage_util.getActingAs.mockImplementation(() => Promise.resolve(1))
            getCurrency.mockImplementation(() => Promise.resolve({
                symbol: '$',
                code: 'NZD'
            }))
            Api.getBusiness.mockResolvedValue({
                data: {
                    address: {
                        country: "New Zealand"
                    }
                }
            })
        });

        describe("Test if not editing a product", () => {
            let $route = {
                path: '/',
                params: {
                    businessId: 1
                }
            }
            const $router = {
                push: jest.fn()
            }
            beforeAll(async () => {
                wrapper = await shallowMount(Product, {
                    localVue,
                    mocks: {
                        $route,
                        $router
                    }
                });
            });
            test("getBusinessCountry_withValidResponse_setsCorrectCountryAndCurrency", async () => {
                await wrapper.vm.$nextTick();
                await expect(wrapper.vm.$data.currencyCode).toStrictEqual("NZD");
                expect(wrapper.vm.$data.currencySymbol).toEqual("$");
            });

            test("checkIfEditing_whenRouteDoesNotIncludeEditProduct_setsCorrectValues", async () => {
                await wrapper.vm.$nextTick();
                await expect(wrapper.vm.$data.cardTitle).toEqual("Add Product")
                await expect(wrapper.vm.$data.submitButton).toEqual("Add Product")
            });

            test("cancelButton_findElement_elementRenders", () => {
                const cancelButton = wrapper.find("#cancelButton");
                expect(cancelButton.exists()).toBeTruthy();
            });

            test("cancelButton_click_pushesToRouter", () => {
                const cancelButton = wrapper.find("#cancelButton");
                cancelButton.trigger("click");
                expect($router.push).toBeCalledWith({name:"ManageBusiness", hash:"#catalogue"});
            });

            describe("Invalid input fields tests for methods only", () => {
                describe("With valid code entered but invalid response from server", () => {
                    beforeEach( () => {
                        Api.isCodeValid.mockImplementation(() => Promise.resolve({
                            status: 400
                        }));
                        wrapper.vm.$data.form.id = "Tax fraud is bad"
                        wrapper.vm.$data.form.name = ""
                        wrapper.vm.$data.form.description = "Decided I will no longer be paying taxes. " +
                            "What are they gonna do, tax me more? " +
                            "Go ahead. I won't pay those either. " +
                            "Oh i'm going to prison? The one paid for by my tax dollars? " +
                            "Sorry. Didn't pay em. Now there is no prison. " +
                            "I am at least 3 steps ahead of the government at all times."
                    });

                    test("isCodeValid_withInvalidCodeReturnedFromServer_toReturnFalse", () => {
                        expect(wrapper.vm.isCodeValid()).toBeFalsy()
                    });

                    test("isNameValid_withInvalidName_toReturnFalse", () => {
                        expect(wrapper.vm.isNameValid()).toBeFalsy()
                    });

                    test("notTooLongValidation_withInvalidDescription_toReturnFalse", () => {
                        expect(wrapper.vm.notTooLongValidation()).toBeFalsy()
                    });

                    test("isImageValid_withNoImageSet_toReturnFalse", () => {
                        expect(wrapper.vm.isImageValid()).toBeFalsy()
                    });

                    test("canSubmit_withInvalidInputs_toReturnTrue", () => {
                        expect(wrapper.vm.canSubmit()).toBeFalsy()
                    });
                });

                describe("With invalid code entered", () => {
                    beforeEach( () => {
                        Api.isCodeValid.mockImplementation(() => Promise.resolve({
                            status: 400
                        }));
                        wrapper.vm.$data.form.id = ""
                        wrapper.vm.$data.form.name = ""
                        wrapper.vm.$data.form.description = "Decided I will no longer be paying taxes. " +
                            "What are they gonna do, tax me more? " +
                            "Go ahead. I won't pay those either. " +
                            "Oh i'm going to prison? The one paid for by my tax dollars? " +
                            "Sorry. Didn't pay em. Now there is no prison. " +
                            "I am at least 3 steps ahead of the government at all times."
                    });

                    test("isCodeValid_withInvalidId_toReturnFalse", () => {
                        expect(wrapper.vm.isCodeValid()).toBeFalsy();
                    });
                });
            });

            describe("Valid input fields tests for methods only", () => {
                beforeEach( () => {
                    Api.isCodeValid.mockImplementation(() => Promise.resolve({
                        status: 200
                    }));
                    wrapper.vm.$data.form.id = "His Highness";
                    wrapper.vm.$data.form.name = "King";
                    wrapper.vm.$data.form.description = "Tripod Nev";
                    wrapper.vm.$data.form.recommendedRetailPrice = 1000000000;
                });

                test("isCodeValid_withValidCodeReturnedFromServer_toReturnTrue", () => {
                    expect(wrapper.vm.isCodeValid()).toBeTruthy();
                });

                test("isNameValid_withValidName_toReturnTrue", () => {
                    expect(wrapper.vm.isNameValid()).toBeTruthy();
                });

                test("notTooLongValidation_withValidDescription_toReturnTrue", () => {
                    expect(wrapper.vm.notTooLongValidation()).toBeTruthy();
                });

                test("canSubmit_withValidInputs_toReturnTrue", () => {
                    expect(wrapper.vm.canSubmit()).toBeTruthy();
                });
            });
        });

        // Define route to include editing information
        let $route = {
            path: '/edit-product',
            params: {
                businessId: 1,
                productId: "WATT-420-BEANS"
            },
            query: {
                code: 'Item',
                name: 'A product',
                manufacturer: 'A manufacturer',
                description: 'A description',
                rrp: '2 USD',
                images: ["0", "1"]
            }
        }

        describe("Test if editing a product", () => {
            beforeAll(async () => {
                Api.isCodeValid.mockImplementation(() => Promise.resolve({status: 200}));
                wrapper = await shallowMount(Product, {
                    localVue,
                    mocks: {
                        $route,
                    }
                });
            });
            test("checkIfEditing_whenRouteDoesEditProduct_setsCorrectValues", async () => {
                await wrapper.vm.$nextTick();
                await expect(wrapper.vm.$data.cardTitle).toEqual("Edit A product Product");
                await expect(wrapper.vm.$data.submitButton).toEqual("Save Product");
                await expect(wrapper.vm.$data.addProductClicked).toBeTruthy();
                await expect(wrapper.vm.$data.form.id).toEqual('Item');
                await expect(wrapper.vm.$data.form.name).toEqual('A product');
                await expect(wrapper.vm.$data.form.manufacturer).toEqual('A manufacturer');
                await expect(wrapper.vm.$data.form.description).toEqual('A description');
                await expect(wrapper.vm.$data.form.recommendedRetailPrice).toEqual("2 USD");
            });

            test("editingProduct_formValid_codeValid", () => {
                wrapper.setData({
                    form: {
                        id: "WATT-420-BEANS"
                    },
                    editingAProduct: true
                });
                expect(wrapper.vm.isCodeValid()).toBe(true);
            });
        });
    });
    afterAll(() => {
        jest.clearAllMocks();
        wrapper.destroy();
    });
});

describe("The product form component is mounted with product and images. Primary image is image 0", () => {

    let wrapper;
    let $router;
    let $route;

    const fakeFile = new File(["a".repeat(24 * (10**6))], "TooBigFile", {type: "image/png"});

    beforeEach(() => {
        $router = {
            push: jest.fn()
        }
        $route = {
            path: "/business/:businessId/add-product",
            params: {
                businessId: 1
            },
            query: {
                code: ""
            }
        }
        const images = [
            {id: "0", data: "image1", file: fakeFile, preExisting: false},
            {id: "1", data: "image2", file: fakeFile, preExisting: false}
        ]
        const options = {
            localVue,
            attachTo: document.body.appendChild(document.createElement("div")),
            mocks: {
                $router,
                $route,
                handleImageClicked: jest.fn()
            },
            data() {
                return {
                    form: {
                        id: "WATT_420_BEANS",
                        name: "Baked Beans",
                        description: "Description of baked beans",
                        manufacturer: "Manufacturer of Baked beans",
                        recommendedRetailPrice: ""
                    },
                    productImages: {
                        images: images,
                        primaryId: 0,
                        imageSizeExceeded: false
                    },
                    editingAProduct: false
                }
            }
        }
        Api.getBusiness.mockResolvedValue({
            data: {
                address: {
                    country: "New Zealand"
                }
            }
        });
        Api.isCodeValid.mockImplementation(() => Promise.resolve({status: 200}));
        Api.uploadImage = jest.fn();
        wrapper = shallowMount(Product, options);
        wrapper.vm.$data.productImages.images = images;
    });


    test("productForm_componentMounted_componentRenders", () => {
        expect(wrapper.exists()).toBeTruthy();
    });

    test("image_findElement_elementRenders", () => {
        const image = wrapper.find("#image-display");
        expect(image.exists).toBeTruthy();
    });

    test("productForm_findForm_elementRenders", () => {
        const form = wrapper.find("#form");
        expect(form.exists()).toBeTruthy();
    });

    test("fileSelect_findElement_elementRenders", () => {
        const fileSelect = wrapper.find("#fileSelect");
        expect(fileSelect.exists).toBeTruthy();
    });

    test("fileSelect_selectFile[1]_setsFormValue", () => {
        const fileSelect = wrapper.find("#fileSelect")
        fileSelect.trigger("change", {});
    });

    test("addProduct_onSubmit_apiCallsForEachImage", async () => {
        Api.addProduct.mockImplementation(() => Promise.resolve({status: 201}));
        const expectedCalls = wrapper.vm.$data.productImages.images.length;
        const form = wrapper.find("#form");
        await form.trigger("submit");
        expect(Api.uploadImage).toBeCalledTimes(expectedCalls);
    });

    test("addProduct_onSubmit_apiCallsWithRightData", async () => {
        Api.addProduct.mockImplementation(() => Promise.resolve({status: 201, data: "WATT_420_BEANS"}));

        const form = wrapper.find("#form");
        await form.trigger("submit");

        const expectedBusinessId = $route.params.businessId;

        const calls = Api.uploadImage.mock.calls;

        calls.forEach((call) => {
            expect(call).toContainEqual(expectedBusinessId);
        });
    });

    test("addProduct_onSubmit_apiCallsWithProductId", async () => {
        Api.addProduct.mockImplementation(() => Promise.resolve({status: 201, data: "WATT_420_BEANS"}));

        const form = wrapper.find("#form");
        await form.trigger("submit");
        const expectedProductId = wrapper.vm.$data.form.id;

        const calls = Api.uploadImage.mock.calls;

        calls.forEach((call) => {
            expect(call).toContain(expectedProductId);
        });
    });

    test("addProduct_onSubmit_apiCallsWithFileData", async () => {
        Api.addProduct.mockImplementation(() => Promise.resolve({status: 201}));

        const form = wrapper.find("#form");
        await form.trigger("submit");

        const calls = Api.uploadImage.mock.calls;

        let expectedFormData = new FormData();
        expectedFormData.append("filename", fakeFile);

        calls.forEach((call) => {
            expect(call[2].get("filename")).toEqual(fakeFile);
        });
    });

    test("addProduct_onSubmit_routerPush", async () => {
        Api.addProduct.mockImplementation(() => Promise.resolve({status: 201, data: "WATT_420_BEANS"}))
        const form = wrapper.find("#form");
        await form.trigger("submit");
        await wrapper.vm.$nextTick();
        expect($router.push).toBeCalled();
    });

    test("addProduct_onResolve400_noRouterPush", async () => {
        Api.addProduct.mockImplementation(() => Promise.resolve({status: 400}));
        const form = wrapper.find("#form");
        await form.trigger("submit");
        expect($router.push).not.toBeCalled();
    });

    test("addProduct_onResolveOther_noRouterPush", async () => {
        Api.addProduct.mockImplementation(() => Promise.resolve({status: 500}));
        const form = wrapper.find("#form");
        await form.trigger("submit");
        expect($router.push).not.toBeCalled();
    });

    test("addProduct_onReject_noRouterPush", async () => {
        Api.addProduct.mockImplementation(() => Promise.reject({status: 500}));
        const form = wrapper.find("#form");
        await form.trigger("submit");
        expect($router.push).not.toBeCalled();
    });

    test("fileSelect_changeFile_callsFileReader", async () => {
        const spy = jest.spyOn(FileReader.prototype, 'readAsDataURL');
        const file = new File(["a".repeat(24 * (10**4))], "My File", {type: "image/png"});
        const event = {target: {files: [file]}};

        await wrapper.vm.handleImageChange(event);
        await wrapper.vm.$nextTick();
        expect(spy).toHaveBeenCalledWith(event.target.files[0]);
    });

    test("fileSelect_changeFileDrag_callsFileReader", async () => {
        const spy = jest.spyOn(FileReader.prototype, 'readAsDataURL');
        const file = new File(["a".repeat(24 * (10**4))], "My File", {type: "image/png"});
        const event = {dataTransfer: {files: [file]}, type: 'drop'};

        await wrapper.vm.handleImageChange(event);
        await wrapper.vm.$nextTick();
        expect(spy).toHaveBeenCalledWith(event.dataTransfer.files[0]);
    });

    test("fileSelect_fileSizeExceeded_fileReaderNotCalled", async () => {
        const spy = jest.spyOn(FileReader.prototype, "readAsDataURL");
        const file = new File(["a".repeat(25 * (10**6))], "TooBigFile", {type: "image/png"});
        const event = {target: {files: [file]}};

        await wrapper.vm.handleImageChange(event);
        expect(spy).not.toBeCalled();
    });

    test("fileSelect_initialFiles2Upload8Files_fileReaderCalled", async () => {
        const times = 8
        const spy = jest.spyOn(FileReader.prototype, "readAsDataURL");

        const file = new File(["a".repeat(24 * (10**4))], "TooBigFile", {type: "image/png"});
        let files = []
        for (let i = 0; i < times; i++) {
            files.push(file);
        }
        const event = {target: {files: files}}
        await wrapper.vm.handleImageChange(event);
        expect(spy).toBeCalledTimes(times);
    });

    test("fileSelect_Upload11Files_fileReaderNotCalled", async () => {
        const times = 11;
        const spy = jest.spyOn(FileReader.prototype, "readAsDataURL");
        const file = new File(["a".repeat(24 * (10**6))], "TooBigFile", {type: "image/png"});
        let files = []
        for (let i = 0; i < times; i++) {
            files.push(file);
        }
        const event = {target: {files: files}}
        await wrapper.vm.handleImageChange(event);
        expect(spy).not.toBeCalled();
    });

    test("fileSelect_fileFormatNotValid_fileReaderNotCalled", async () => {
        const spy = jest.spyOn(FileReader.prototype, "readAsDataURL");
        const file = new File(["a".repeat(24 * (10**6))], "TooBigFile", {type: "application/pdf"});
        const event = {target: {files: [file]}}
        await wrapper.vm.handleImageChange(event);
        expect(spy).not.toBeCalled();
    });

    test("changePrimary_validId_changesPrimaryId", async () => {
        wrapper.vm.$data.productImages.images.push({id: 1, file: fakeFile, data: 'file'});
        await wrapper.vm.setPrimary(1);
        expect(wrapper.vm.$data.productImages.primaryId).toBe(1);
    });

    test("changePrimary_invalidId_doesNotChange", async () => {
        await wrapper.vm.setPrimary(2);
        expect(wrapper.vm.$data.productImages.primaryId).toBe(0);
    });

    test("changePrimary_sameImage_doesNotChange", async () => {
        await wrapper.vm.setPrimary(0);
        expect(wrapper.vm.$data.productImages.primaryId).toBe(0);
    });

    test("deleteImage[0]_removesImage", () => {
        const images = wrapper.vm.$data.productImages.images;
        const toDelete = wrapper.vm.$data.productImages.images[0]
        wrapper.vm.deleteImage(toDelete.id);
        let exists = false;
        images.forEach(image => {
            exists = image.id === toDelete.id;
        });
        expect(exists).toBe(false)
    });

    test("deleteImage[-1]_doesNotRemoveAnyImage", () => {
        const images = wrapper.vm.$data.productImages.images;
        wrapper.vm.deleteImage(-1);
        const newImages = wrapper.vm.$data.productImages.images;
        expect(newImages).toStrictEqual(images);
    });

    afterEach(() => {
        jest.clearAllMocks();
        wrapper.destroy();
    });
});

describe("Product form component is mounted with mock product", () => {

    let wrapper;
    let $route;
    let $router;

    const fakeFile = new File(["a".repeat(24 * (10**6))], "TooBigFile", {type: "image/png"});

    beforeEach(() => {
        $router = {
            push: jest.fn()
        }
        $route = {
            path: "/business/:businessId/edit-product",
            fullPath: "/business/1/edit-product/WATT-420-BEANS?name=BakedBeans&description=Beansintomatosauce&" +
                "manufacturer=HeinzWattiesLtd&rrp=20NZD&primaryImageId=0&images=0&images=1",
            params: {
                businessId: 1,
                productId: "WATT-420-BEANS"
            },
            query: {
                code: "WATT-420-BEANS",
                name: "Baked Beans",
                description: "Beans in tomato sauce",
                manufacturer: "Heinz Watties Ltd",
                rrp: "20 NZD",
                primaryImageId: 0,
                images: ["0", "1"]
            }
        }
        const options = {
            localVue,
            mocks: {
                $router,
                $route
            },
            data() {
                return {
                    editingAProduct: true,
                    productImages: {
                        images: [
                            {id: 0, data: "image1", file: fakeFile, preExisting: true},
                            {id: 1, data: "image2", file: fakeFile, preExisting: false}
                        ],
                        primaryId: 0,
                        imageSizeExceeded: false
                    },
                }
            }
        }
        Api.getBusiness.mockResolvedValue({
            data: {
                address: {
                    country: "New Zealand"
                }
            }
        });
        Api.isCodeValid.mockImplementation(() => Promise.resolve({status: 200}));
        Api.deleteImage.mockImplementation(() => Promise.resolve({status: 200}));
        Api.setPrimary.mockImplementation(() => Promise.resolve({status: 200}))
        Api.uploadImage.mockImplementation(() => Promise.resolve({status: 200}))
        wrapper = shallowMount(Product, options);
        wrapper.vm.$data.productImages.images = [
            {id: 0, data: "image1", file: fakeFile, preExisting: true},
            {id: 1, data: "image2", file: fakeFile, preExisting: false}
        ]
    });

    test("editProduct_mounted_componentRenders", () => {
        expect(wrapper.exists()).toBeTruthy();
    });

    test("editProduct_findForm_elementRenders", () => {
        const form = wrapper.find("#form");
        expect(form.exists()).toBeTruthy();
    });

    test("editProduct_onSubmitValid_pushesToRoute", async () => {
        Api.modifyProductCatalogue.mockImplementation(() => Promise.resolve({status: 200}));
        const form = wrapper.find("#form");
        await form.trigger("submit");
        await wrapper.vm.$nextTick();
        expect($router.push).toBeCalledWith({name:"ManageBusiness", hash:"#catalogue"});
    });

    test("editProduct_onSubmitInvalid_noRouterCall", async () => {
        Api.modifyProductCatalogue.mockImplementation(() => Promise.resolve({status: 400}));
        const form = wrapper.find("#form");
        await form.trigger("submit");
        await wrapper.vm.$nextTick();
        expect($router.push).toBeCalledTimes(0);
    });

    test("editProduct_onSubmitOther_noRouterCall", async () => {
        Api.modifyProductCatalogue.mockImplementation(() => Promise.resolve({status: 500}));
        const form = wrapper.find("#form");
        await form.trigger("submit");
        await wrapper.vm.$nextTick();
        expect($router.push).toBeCalledTimes(0);
    });

    test("editProduct_onSubmitCatch_noRouterCall", async () => {
        Api.modifyProductCatalogue.mockImplementation(() => Promise.reject());
        const form = wrapper.find("#form");
        await form.trigger("submit");
        await wrapper.vm.$nextTick();
        expect($router.push).toBeCalledTimes(0);
    });

    test("editProduct_deleteImage[0]_apiCalls", () => {
        Api.deleteImage.mockImplementation(() => Promise.resolve({status: 200}));
        const images = wrapper.vm.$data.productImages.images;
        const businessId = wrapper.vm.$route.params.businessId;
        const productId = wrapper.vm.$route.query.code;
        const imageId = images[0].id
        wrapper.vm.deleteImage(imageId);
        wrapper.vm.deleteImages();
        expect(Api.deleteImage).toBeCalledWith(businessId, productId, imageId);
    });

    test("deleteImage[0]_apiCalls_imageRemoved", async () => {
        Api.deleteImage.mockImplementation(() => Promise.resolve({status: 200}));
        const images = wrapper.vm.$data.productImages.images;
        const imageId = images[0].id
        await wrapper.vm.deleteImage(imageId);

        let exists = false;
        wrapper.vm.$data.productImages.images.forEach(image => {
            exists = image.id === imageId;
        });
        expect(exists).toBe(false);
    });

    test("submitChangePrimary_validId_callsApi", async () => {
        Api.uploadImage.mockImplementation(() => Promise.resolve({status: 200, data: 190}))
        Api.setPrimary.mockImplementation(() => Promise.resolve({status: 200}));
        const images = wrapper.vm.$data.productImages.images;
        const businessId = wrapper.vm.$route.params.businessId;
        const productId = wrapper.vm.$route.query.code;
        wrapper.vm.setPrimary(images[1].id);
        await wrapper.vm.editImages();
        await wrapper.vm.$nextTick();
        expect(Api.setPrimary).toBeCalledWith(businessId, productId, images[1].id);
    });

    test("changePrimary[1]_apiReturnsOK_primaryImageChanges",async  () => {
        Api.setPrimary.mockImplementation(() => Promise.resolve({status: 200}));
        const images = wrapper.vm.$data.productImages.images;
        await wrapper.vm.setPrimary(images[1].id);
        const primaryImage = wrapper.vm.$data.productImages.primaryId;
        expect(primaryImage).toBe(1);
    });

    test("changePrimary[1]_invalidImage_noPrimaryChange", () => {
        Api.setPrimary.mockImplementation(() => Promise.resolve({status: 406}));
        const images = wrapper.vm.$data.productImages.images;
        wrapper.vm.setPrimary(-1);
        const primaryImage = wrapper.vm.$data.productImages.primaryId;
        expect(primaryImage).toBe(images[0].id);
    });

    afterEach(() => {
        jest.clearAllMocks();
        wrapper.destroy();
    });
});