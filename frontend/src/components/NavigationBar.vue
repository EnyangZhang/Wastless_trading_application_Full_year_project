<template>
  <div id="navigationBar">
    <b-navbar toggleable="lg"
              fluid="true"
              class="navbar-light"
              type="dark"
              :style="navbarStyle">
      <b-navbar-brand href="#">
        <img :src="require('../../public/logo_square_shadowless_transparent.png')" width="50" height="50" alt="Logo"/>
      </b-navbar-brand>
      <!-- Message to display if the user is a global admin -->
      <b-navbar-brand v-if="isAdmin && loggedIn" href="#">Administrator Mode</b-navbar-brand>
      <b-navbar-toggle target="nav-collapse"></b-navbar-toggle>
      <b-collapse id="nav-collapse" is-nav>
        <b-navbar>
          <!--Buttons for navbar-->
          <b-button class="mr-1"
                    id="homeButton"
                    variant="outline-light"
                    v-if="loggedIn"
                    :disabled="this.$route.path === '/home'"
                    @click="$router.push('/home')">Home</b-button>
          <b-button class="mr-1"
                    id="profileButton"
                    variant="outline-light"
                    v-if="loggedIn && !actingAsBusiness"
                    :disabled="this.$route.path === `/profile/${userId}`"
                    @click="toUserProfile">Profile</b-button>
          <b-button class="mr-1"
                    id="manageBusiness"
                    variant="outline-light"
                    v-if="loggedIn && actingAsBusiness"
                    :disabled="this.$route.name === 'ManageBusiness'"
                    @click="$router.push({name:'ManageBusiness'})">Manage Business</b-button>
          <b-button class="mr-1"
                    id="searchButton"
                    variant="outline-light"
                    v-if="loggedIn"
                    :disabled="this.$route.name === 'SearchUsers'"
                    @click="$router.push('/search')">Search</b-button>
          <b-button class="mr-1"
                    id="marketplaceButton"
                    variant="outline-light"
                    v-if="loggedIn"
                    :disabled="this.$route.name === 'CommunityMarketplace'"
                    @click="$router.push('/community-marketplace')">Community Marketplace</b-button>
          <b-button class="mr-1"
                    id="myCardsButton"
                    variant="outline-light"
                    v-if="loggedIn && !actingAsBusiness"
                    :disabled="$route.name === 'MyCards'"
                    @click="$router.push(`/my-cards/${userId}`)">My Cards</b-button>
        </b-navbar>
        <b-navbar class="ml-auto">
          <b-avatar id="currentUserAvatar" square variant="primary" v-if="loggedIn"></b-avatar>
          <b-dropdown class="mr-1"
                      right
                      id="accounts"
                      variant="outline-light"
                      :text="actingAsName"
                      v-if="loggedIn"
                      v-model="actingAs">

            <div id="dropdown-item">
              <!--User part of the dropdown-->
              <b-dropdown-text class="dropdown-text">User</b-dropdown-text>
              <b-dropdown-item href="#" @click="switchUser(currentUser.data)">
                <b-avatar class="dropdown-avatar" square variant="primary"></b-avatar>
                {{currentUser.text}}
              </b-dropdown-item>

              <!--Display the businesses that the user administrates with a separator-->
              <div v-if="options.length > 0">
                <b-dropdown-divider id="dropdownAccountSeparator"></b-dropdown-divider>
                <b-dropdown-text class="dropdown-text">Businesses</b-dropdown-text>
              </div>
              <b-dropdown-item :id="item.data" href="#" v-for="item in options" :key="item.data" @click="switchUser(item.data)">
                <b-avatar class="dropdown-avatar" square variant="primary"></b-avatar>
                {{ item.text }}
              </b-dropdown-item>
              <b-dropdown-item-button id="addBusinessButton" v-if="!actingAsBusiness" @click="$router.push('/business-signup')">
                <b-iconstack name="icon" square font-scale="2.5" style="vertical-align: middle">
                  <b-icon stacked icon="square"></b-icon>
                  <b-icon stacked icon="plus"></b-icon>
                </b-iconstack>
                Create business
              </b-dropdown-item-button>
            </div>
          </b-dropdown>

          <NotificationDropDown v-if="loggedIn && !actingAsBusiness"/>

          <b-button class="mr-1"
                    variant="outline-light"
                    v-if="$route.name!=='Login'"
                    @click=clickLoginOrOut>{{ rightButtonText }}</b-button>
        </b-navbar>
      </b-collapse>
    </b-navbar>
  </div>
</template>

<script>
import api from "../Api";
import storage_util from "../javascript_modules/storage_util";
import {BIcon, BIconstack} from "bootstrap-vue";
import NotificationDropDown from "./notifications/NotificationDropDown";

export default {
  name: "NavigationBar",
  data() {
    return {
      userId: null,
      fName: '',
      lName: '',
      currentUser: {
        text: '',
        data: undefined
      },
      options: [],
      logged: false,
      actingAs: null,
      actingAsName: null,
      isAdmin: false
    }
  },
  components: {
    NotificationDropDown,
    'b-icon' : BIcon,
    'b-iconstack': BIconstack
  },
  mounted: function() {
    this.getUserInfo();
    this.setDropDownText(this.actingAs);
    window.addEventListener(storage_util.UPDATED_USER_DETAILS_EVENT, this.getUserInfo);
    window.addEventListener(storage_util.SWITCHED_ACCOUNT_EVENT, this.getUserInfo);

    this.$root.$on('logout', () => {
      this.isAdmin = false;
    });
  },
  watch: {
    /**
     * Sets which account the user is acting as and changes
     * visible name on dropdown to selected account name.
     */
    actingAs: function (val) {
      // Sets acting as account
      storage_util.setActingAs(val);
    }
  },
  computed: {
    /** Should show profile button unless on login, signup, or profile pages. */
    loggedIn () {
      let onSignUp = this.$route.name === 'SignUp';
      let onLogin = this.$route.name === 'Login';
      return !onSignUp && !onLogin;
    },

    navbarStyle () {
      if (this.isAdmin && this.loggedIn) {
        return "background-color: teal";
      } else {
        return "background-color: green";
      }
    },

    /**
     * If on the signup page the button on the right should say 'Login', otherwise it should say 'Search'
     * Note that this button is invisible on the 'Login' page.
     */
    rightButtonText () {
      if (this.$route.name === 'SignUp') {
        return 'Login';
      } else {
        return 'Log out';
      }
    },
    /**
     * Computed property that returns true if current user is acting as a business they administrate,
     * and false otherwise
     */
    actingAsBusiness () {
      return this.actingAs !== 'ACTING_AS_CURRENT_USER' && this.actingAs !== null;
    }
  },
  methods: {
    toUserProfile () {
      const id = storage_util.getCurrentUser();
      const path = `/profile/${id}`;
      if (id !== undefined && this.$route.path !== path) {
          this.$router.push({name:'Profile', params:{id:id}})
      }
    },
    /**
     * Switch page to login or to logout page.
     * Clear the signup fields when doing so
     */
    clickLoginOrOut () {
      if (this.$route.name === 'Signup') {
        this.$root.$emit('resetSignup');
        this.$router.push('/login');
      } else {
        api.logout();
        //Call the reset signup field method from the Signup.vue module
        this.$root.$emit('resetSignup');
        this.$root.$emit('logout');
        this.$router.push('/logout');
      }
    },

    /**
     * Sets the title of the dropdown menu to an item in the options list.
     * @param actAs takes a string or integer corresponding to the data attribute of items in the options list.
     */
    setDropDownText(actAs) {
      if (actAs == null) {
        this.actingAsName = "Accounts";
      } else {
        // Set visible name
        for (const i in this.options) {
          if (this.options[i].data === parseInt(this.actingAs)) {
            this.actingAsName = this.options[i].text;
            break;
          }
        }
        if (this.currentUser.data === this.actingAs) {
          this.actingAsName = this.currentUser.text;
        }
      }
    },

    switchUser: function (user) {
      // Routes to homepage
      this.actingAs = user;
      if (this.$router.currentRoute.name !== 'HomePage') this.$router.push({name:'HomePage'});
    },

    /**
     * Get user information from StorageUtil
     */
    getUserInfo: function() {
      if (storage_util.isLoggedIn()) {
        // Gets current user
        let currentUser = storage_util.getCurrentUserInfo();
        let businesses = currentUser.businessesAdministered;
        this.userId = storage_util.getCurrentUser()
        this.fName = currentUser.firstName;
        this.lName = currentUser.lastName;

        // Sets current user as an option for acting as
        this.currentUser = {
          text: this.fName + " " + this.lName,
          data: storage_util.ACTING_AS_CURRENT_USER
        };

        // Creates list of businesses to act as
        this.options = []
        for (const x in businesses) {
          this.options.push({
            text: businesses[x].name,
            data: businesses[x].businessId
          });
        }
        // Sets the current account the user is acting as
        this.actingAs = storage_util.getActingAs();
        this.setDropDownText(this.actingAs);
        // Set if user is global admin
        this.isAdmin = currentUser.role === 'global_admin' || currentUser.role === 'default_global_admin';
      }
    }
  }
}
</script>

<style scoped>

.dropdown-text {
  font-size: small;
  color: green;
}

#accounts {
  margin-left: 10px;
  margin-right: 10px;
}

#dropdown-item {
  padding: 1px;
}

</style>