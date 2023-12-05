import {createLocalVue, shallowMount} from '@vue/test-utils';
import Homepage from '../views/HomePage'; // name of your Vue component
import BootstrapVue from 'bootstrap-vue'
import Api from "../Api";

let localVue = createLocalVue();
localVue.use(BootstrapVue);
jest.mock("../Api")
Api.getBusinessCatalogue.mockImplementation(() => Promise.resolve({data: []}));
Api.getBusiness.mockImplementation(() => Promise.resolve({data: []}));


let wrapper = shallowMount(Homepage, {localVue,
                                                propsData: {},
                                                mocks: {},
                                                stubs: {},
                                                methods: {}});

describe('Homepage', () => {
    test('Is businessCatalogue table view exists.',  async () => {
            wrapper.vm.$data.actingAsBusiness = true;
            await wrapper.vm.$nextTick();
            let home = wrapper.find("#home");
            expect(home.exists()).toBeTruthy();
    });
});