import {createLocalVue, shallowMount} from "@vue/test-utils";


import BootstrapVue from "bootstrap-vue";
import Signup from "../views/Signup";
import api from "@/Api";

jest.mock("../javascript_modules/storage_util")
jest.mock("../Api")

const $router = jest.fn();
let wrapper;


const localVue = createLocalVue()

localVue.use(BootstrapVue)

describe("Signup", () => {

    const options = {
        localVue,
        propsData: {},
        data() {
            return {
                form: {
                    password: '',
                    confirmPassword: ''
                }
            }
        },
        mocks: {
            $router
        },
        stubs: {},
        methods: {

        },
        computed: {}
    }

    beforeEach(async () => {
        wrapper = shallowMount(Signup, options);
        await wrapper.vm.$nextTick();
    })

    describe("Test mounted functions with valid responses", () => {


        test('signup page renders', () => {
            const signupPage = wrapper.find('#signup');
            expect(signupPage.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_firstNameInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidFirstName');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_lastNameInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidLastName');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_emailInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidEmail');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_passwordInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidPassword');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_confirmPasswordInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidPasswordConfirm');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_dateOfBirthInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidDateOfBirth');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_streetNumberInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidStreetNumber');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_streetNameInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidStreetName');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_suburbInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidSuburb');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_cityInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidCity');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_regionInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidRegion');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_countryInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidCountry');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('invalidFeedbackMessages_submitClickedNoInput_postcodeInvalidMessagesRendered', async () => {
            const submitButton = wrapper.find('#submit');
            submitButton.trigger('click');
            await wrapper.vm.$nextTick();
            const invalidFeedback = wrapper.find('#invalidPostcode');
            expect(invalidFeedback.exists()).toBeTruthy();
        });

        test('', async () => {
            expect(wrapper.vm.canSubmit()).toBeFalsy();
        })

    });

    describe("Test with valid responses", () => {

        beforeEach(async () => {
            wrapper.vm.$data.form.firstName = 'test name';
            wrapper.vm.$data.form.lastName = 'test name';
            wrapper.vm.$data.form.middleName = '';
            wrapper.vm.$data.form.nickname = '';
            wrapper.vm.$data.form.phoneNumber = '';
            wrapper.vm.$data.form.email = 'abc@email.com';
            wrapper.vm.$data.form.password = '12345678';
            wrapper.vm.$data.form.confirmPassword = '12345678';
            wrapper.vm.$data.form.dateOfBirth = '2000-12-12';
            wrapper.vm.$data.form.homeAddress.streetNumber = '12';
            wrapper.vm.$data.form.homeAddress.streetName = 'test';
            wrapper.vm.$data.form.homeAddress.suburb = 'test';
            wrapper.vm.$data.form.homeAddress.city = 'test';
            wrapper.vm.$data.form.homeAddress.region = 'test';
            wrapper.vm.$data.form.homeAddress.country = 'United States';
            wrapper.vm.$data.form.homeAddress.postcode = '1234';
            await wrapper.vm.$nextTick();
        })

        test ('canSubmit_validInputs_returnsTrue', async () => {
            expect(wrapper.vm.canSubmit()).toBeTruthy();
        });

        test ('clickSubmitButton_validInputs_emitsSuccessfulSignup', async () => {
            api.createUser.mockResolvedValue({
                status: 200,
                data: {userId: 1}
            })
            const signupForm = wrapper.find('#signupForm');
            signupForm.trigger('submit');
            await wrapper.vm.$nextTick();
            expect(wrapper.emitted().successfulSignup).toBeTruthy();
        });

        test ('clickSubmitButton_validInputsNotEnglish_emitsSuccessfulSignup', async () => {
            api.createUser.mockResolvedValue({
                status: 200,
                data: {userId: 1}
            })

            wrapper.vm.$data.form.firstName = "AB#&C~C&D)E";
            wrapper.vm.$data.form.middleName = "অ আ কা ক & ি কী উ";
            wrapper.vm.$data.form.nickname = "áêéèëïíîôóúû";
            wrapper.vm.$data.form.lastName = "hijā’ī ";

            const signupForm = wrapper.find('#signupForm');
            signupForm.trigger('submit');
            await wrapper.vm.$nextTick();

            expect(wrapper.emitted().successfulSignup).toBeTruthy();
        });

        test ('resetSignUpForm_validInputs_clearsData', async () => {
            wrapper.vm.resetSignUpForm();
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.$data.form.firstName).toBe('');
            expect(wrapper.vm.$data.form.homeAddress.suburb).toBe('');
            expect(wrapper.vm.$data.form.phoneNumber).toBe(null);
        });

    });

    describe("Test autocompletion", () => {

        test ('clickSubmitButton_validInputs_emitsSuccessfulSignup', async () => {
            api.getAutoComplete.mockResolvedValue({
                status: 200,
                data: {
                    features: [
                        {
                            properties: {
                                housenumber: '3',
                                street: 'Riccarton Road',
                                district: 'Ilam',
                                city: 'Christchurch',
                                state: 'Canterbury',
                                country: 'New Zealand',
                                postcode: '8000'
                            }
                        }
                    ]
                }
            })
            await wrapper.vm.searchApi("3 Riccarton Road");
            await wrapper.vm.$nextTick();
            expect(wrapper.vm.$data.suggestions.length).toBe(1);
            expect(wrapper.vm.$data.suggestions[0].streetNumber).toBe('3');
        });
    });
});