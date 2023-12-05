/*
 * Created on Wed Feb 10 2021
 *
 * The Unlicense
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or distribute
 * this software, either in source code form or as a compiled binary, for any
 * purpose, commercial or non-commercial, and by any means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors of this
 * software dedicate any and all copyright interest in the software to the public
 * domain. We make this dedication for the benefit of the public at large and to
 * the detriment of our heirs and successors. We intend this dedication to be an
 * overt act of relinquishment in perpetuity of all present and future rights to
 * this software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

/**
 * Main entry point for your Vue app
 */
import Vue from 'vue';
import App from './App';
import Router from './Router';
import VueLogger from 'vuejs-logger';
import {BootstrapVue, BootstrapVueIcons, IconsPlugin} from 'bootstrap-vue';
import VueCookies from 'vue-cookies';
import 'bootstrap/dist/css/bootstrap.css';
import 'bootstrap-vue/dist/bootstrap-vue.css';
import PrimeVue from 'primevue/config';
import DataView from "primevue/dataview/DataView";
import MultiSelect from 'primevue/multiselect';
import Button from "primevue/button";
import Calendar from 'primevue/calendar';

import Dialog from 'primevue/dialog';

Vue.config.productionTip = false;

// Make BootstrapVue available throughout project
Vue.use(BootstrapVue);
Vue.use(BootstrapVueIcons);
// Install the BootstrapVue icon components plugin
Vue.use(IconsPlugin);

const options = {
  isEnabled: true,
  logLevel : 'debug',
  stringifyArguments : false,
  showLogLevel : true,
  showMethodName : false,
  separator: '|',
  showConsoleColors: true
};

Vue.use(VueLogger, options);
Vue.use(BootstrapVue);
Vue.use(IconsPlugin);
Vue.use(VueCookies);

Vue.$cookies.config('7d');
Vue.$cookies.set('theme', 'default');
Vue.$cookies.set('hover-time', '1s');

/*Prime Vue*/
Vue.use(PrimeVue);
Vue.component('DataView', DataView);
Vue.component('MultiSelect', MultiSelect);
Vue.component('Button', Button)
Vue.component("Dialog", Dialog)
Vue.component('Button', Button);
Vue.component('Calendar', Calendar);

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router: Router,
  components: { App },
  template: '<App/>'
});