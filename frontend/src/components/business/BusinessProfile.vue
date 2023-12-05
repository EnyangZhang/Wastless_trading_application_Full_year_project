<template>
  <div id="BusinessProfile">
    <b-container fluid="true">
      <b-row class="row justify-content-center mb-2">
        <b-col cols="auto" align-self="center">
          <!-- Business profile display card -->
          <b-card id="business-profile-pane" fluid>

          <!--Business Name-->
          <b-card-title>{{ businessName }}</b-card-title>
            <div id="bue-info">
              <b-list-group>
                <!-- Business Type -->
                <b-list-group-item><strong><em>Business Type: </em></strong><br/>
                  {{ businessType }}
                </b-list-group-item>

                <!--Business address-->
                <b-list-group-item>
                  <strong><em>Address: </em></strong>
                  <formatted-address :address="businessAddress"/>
                </b-list-group-item>

                <!--Date Registered-->
                <b-list-group-item><strong><em>Date Registered: </em></strong> {{ businessDate }}</b-list-group-item>

                <!--Description-->
                <b-list-group-item><strong><em>Description: </em></strong><br/>{{ businessDescription }}</b-list-group-item>
              </b-list-group>
            </div>
          </b-card>

          <!--Link to business's sale listings-->
          <b-card v-if="showListings" title="Listings">
            <b-button variant="success" @click="$router.push({name: 'MyBusinessListings', params:{id:$route.params.id}})">View Sale Listings</b-button>
          </b-card>

          <!--Administrators display-->
          <b-card id="business-admins-pane" title="Administrators">
              <b-list-group>
                <b-list-group-item class="user" v-for="admin in businessAdmins" :key="admin.id">
                  <user :user="admin" :isPrimaryAdmin="isPrimary(admin.id)"></user>
                </b-list-group-item>

              </b-list-group>
          </b-card>

        </b-col>
      </b-row>
    </b-container>
  </div>

</template>

<script>
import api from "../../Api"
import {timeFormat} from "../../javascript_modules/user_profile";
import storage_util from "../../javascript_modules/storage_util";
import User from "../../components/ViewUser";
import FormattedAddress from "../../components/address/FormattedAddress";

const BusinessProfile = {
  components: {FormattedAddress, User},
  data() {
    return {
      businessName: "Business Name Here",
      businessType: "Business Type Here",
      businessAddress: {
        streetNumber: "",
        streetName: "",
        suburb: "",
        city: "",
        region: "",
        country: "",
        postcode: ""
      },
      businessPrimaryAdminId: '',
      businessDescription: "",
      businessDate: "Business Date of Registration Here",

      businessOwners: [],

      businessAdmins: [],

      businessMaintainers: [],

      showListings: false
    }
  },
  created() {
    this.getBusinessInfo();
  },
  mounted: function() {
    this.$watch(() => this.$route, (to, from) => {
      if (to.name === 'BusinessProfile' && to.params !== from.params) {
        this.getBusinessInfo();
      }
    })
  },
  methods: {

    /**
     * Function to call the method to get data that the parent is able to call for any related component.
     **/
    refreshData() {
      this.getBusinessInfo();
    },

    /**
     * Function that gets business by sending request to API given bueId, and sets data fields
     * according to response.
     **/
    getBusinessInfo: function() {
      let id;
      if (this.$route.name === "ManageBusiness") {
        id = storage_util.getActingAs();
      } else {
        id = this.$route.params.id;
        this.showListings = true;
      }
      api.getBusiness(id)
      .then((response) => {
        let businessInfo = response.data;
        this.businessName = businessInfo.name;
        this.businessType = businessInfo.businessType;
        this.businessAddress = businessInfo.address;
        this.businessDescription = businessInfo.description;
        this.businessPrimaryAdminId = businessInfo.primaryAdminId;
        this.businessDate = timeFormat(businessInfo.registrationDate);
        this.businessAdmins = businessInfo.admins;
      })
    },

    isPrimary(id) {
      return this.businessPrimaryAdminId === id;
    }
  }
}

export default BusinessProfile;
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

  #business-profile-pane {
    padding: 0 0 0 0;
    margin: 5vh 0 5vh 0;
  }

  #business-admins-pane {
    padding: 0 0 0 0;
    margin: 5vh 0 5vh 0;
  }

  .user {
    align-content: center;
  }

</style>