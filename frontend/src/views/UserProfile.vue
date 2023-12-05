<template>
    <div>
      <b-container>
        <b-row class="row justify-content-center">
          <b-col md="7" class="col justify-content-center">
            <b-card id="profile-pane" fluid >
              <b-card-title>User Profile</b-card-title>
              <b-list-group id="user-details">
                <b-list-group-item><em>First name:</em> {{fName}}</b-list-group-item>
                <b-list-group-item><em>Last name:</em> {{lName}}</b-list-group-item>
                <b-list-group-item><em>Middle name:</em> {{mName}}</b-list-group-item>
                <b-list-group-item><em>Nickname:</em> {{nName}}</b-list-group-item>
                <b-list-group-item><em>Bio:</em> {{bio}}</b-list-group-item>
                <b-list-group-item><em>Email:</em> {{email}}</b-list-group-item>
                <b-list-group-item v-if="canView"><em>Date of birth:</em> {{dateOfBirth}}</b-list-group-item>
                <b-list-group-item v-if="canView"><em>Phone number:</em> {{phoneNum}}</b-list-group-item>
                <b-list-group-item>
                  <strong><em>Home Address: </em></strong>
                  <formatted-address :full-address="canView" :address="homeAddress"/>
                </b-list-group-item>
                <b-list-group-item><em>Member since:</em> {{memberDate}}</b-list-group-item>
                <b-list-group-item v-if="canView"><em>Application Role:</em> {{roleString}}</b-list-group-item>
              </b-list-group>
            </b-card>

            <!--businesses administered display-->
            <b-card id="business-pane"
                    v-if="businessesAdmin && businessesAdmin.length > 0"
                    title="Businesses Administered">
              <b-list-group>
                <b-list-group-item v-for="business in businessesAdmin" :key="business.businessId">
                  <router-link :to="{name:'BusinessProfile', params:{id:business.businessId}}">
                  {{business.name}}
                  </router-link>
                </b-list-group-item>
              </b-list-group>
            </b-card>

            <!--Link to user's marketplace cards-->
            <b-card id="user-cards-pane" :title="`${fName}'s Marketplace Cards`">
              <b-button id="to-marketplace-cards-btn"
                        variant="success"
                        @click="toCards">
                View Marketplace Cards
              </b-button>
            </b-card>
          </b-col>
        </b-row>
      </b-container>

    </div>

</template>

<script>
import api from "../Api";
import {timeFormat} from "../javascript_modules/user_profile";
import FormattedAddress from "../components/address/FormattedAddress";
import storage_util from "../javascript_modules/storage_util";
import roles from "../validation/roles.validation"

const UserProfile = {
  components: {FormattedAddress},
  data() {
      return {
          UserId: null,
          fName: "",
          lName: "",
          mName: "",
          nName: "",
          bio: "",
          email: "",
          dateOfBirth: "",
          phoneNum: "",
          homeAddress: {
            streetNumber: "",
            streetName: "",
            suburb: "",
            city: "",
            region: "",
            country: "",
            postcode: ""
          },
          memberDate: "",
          roleString: "",
          businessesAdmin: [],
          userInfo: []
      }
  },
  created() {
    this.getUserInfo();
  },
  mounted: function() {
    this.$watch(() => this.$route, (to, from) => {
      if (to.name === 'Profile' && to.params !== from.params) {
        this.getUserInfo();
      }
    })
  },
  methods: {
  /**
   * Get user information from backend.
   */
    getUserInfo: function () {
        api.getUser(this.$route.params.id)
        .then((response) => {
          if (response.status === 200) {
            let userInfo = response.data;
            this.UserId = this.$route.params.id;
            this.fName = userInfo.firstName;
            this.lName = userInfo.lastName;
            this.mName = userInfo.middleName;
            this.nName = userInfo.nickname;
            this.bio = userInfo.bio;
            this.email = userInfo.email;
            this.dateOfBirth = userInfo.dateOfBirth;
            this.phoneNum = userInfo.phoneNumber;
            this.homeAddress = userInfo.homeAddress;
            this.memberDate = timeFormat(userInfo.created);
            this.businessesAdmin = userInfo.businessesAdministered;
            let isAdmin = (userInfo.role === roles.DEFAULT_GLOBAL_APPLICATION_ADMIN
                || userInfo.role === roles.GLOBAL_APPLICATION_ADMIN);
            this.roleString = isAdmin ? "Administrator" : "User";
          }
        })
    },

    /**
     * Calls the router to 'my-cards' if viewingMyself computed value is true (so I am navigating to my own cards)
     * If viewingMyself is false, I am viewing someone else's profile, and the router will push to 'user-cards' instead.
     */
    toCards() {
      this.viewingMyself
          ? this.$router.push(`/my-cards/${this.UserId}`)
          : this.$router.push(`/user-cards/${this.UserId}`);
    }
  },
  computed: {
    /**
     * returns true if the currently authenticated user is viewing their own profile (IDs of profile and auth match)
     * Otherwise returns false.
     * @returns {boolean}
     */
    viewingMyself() {
      return storage_util.getCurrentUser() === parseInt(this.UserId);
    },

    /**
     * Returns true if the currently authenticated user posesses the ADMIN role, that is if they are either a global
     * application admin, or a default global application admin.
     * @returns {boolean}
     */
    viewingAsAdmin() {
      const role = storage_util.getCurrentUserInfo().role;
      return role === roles.DEFAULT_GLOBAL_APPLICATION_ADMIN || role === roles.GLOBAL_APPLICATION_ADMIN
    },

    /**
     * Returns true if the currently authenticated user is allowed to view important details of the current user profile
     * i.e. User's full address (including street number, street name and suburb), user's phone number, date of birth.
     *
     * A user is authenticated to view all the details of the current user profile if they are either viewing their own
     * profile, or if they are an administrator.
     * @returns {boolean}
     */
    canView() {
      return this.viewingMyself || this.viewingAsAdmin;
    }
  }
}

export default UserProfile;

</script>

<style scoped>
    * {
        box-sizing: border-box;
    }

    /* Clear floats after the columns */
    .row:after {
        content: "";
        display: table;
        clear: both;
    }

#profile-pane {
  height: auto;
  width: auto;
  margin: 5vh 0 5vh 0;
}

#business-pane {
  margin: 5vh 0 5vh 0;
}

#user-cards-pane {
  margin: 5vh 0 5vh 0;
}

</style>