<template>
  <div id="manageBusinessTabs" v-if="actingAsBusiness">
    <b-card-header>Business Management - {{businessName}}</b-card-header>
    <span>
    <b-card no-body style="height: 100%;">
      <b-tabs fill
              justified
              pills
              card
              vertical
              nav-wrapper-class=""
              style="height: 100%"
              v-model="tabIndex">
        <b-tab title="Profile">
          <div>
            <business-profile ref="profileManager"/>
          </div>
        </b-tab>
        <b-tab title="Product Catalogue">
          <div>
            <catalogue-manager ref="catalogueManager"/>
          </div>
        </b-tab>
        <b-tab title="Product Inventory">
          <div>
            <inventory-manager ref="inventoryManager"/>
          </div>
        </b-tab>
        <b-tab title="Sales Management">
          <div>
            <my-listings ref="salesManager"/>
          </div>
        </b-tab>
        <b-tab title="Sales History">
          <div>
            <SalesHistory ref="historyManager"/>
          </div>
        </b-tab>
        <b-tab title="Sales Report">
          <div>
            <SalesReport ref="salesReportManager"/>
          </div>
        </b-tab>
      </b-tabs>
    </b-card>
    </span>
  </div>
  <div id="cannotShow" v-else>
    <b-container fluid="true">
      <b-row class="row justify-content-center mb-2">
        <b-col cols="auto" align-self="center">
          <b-card fluid class="text-center">
            <b-card-title>You must be acting as a business to access this page.</b-card-title>
            <b-button @click="$router.push({name:'HomePage'})">Home</b-button>
          </b-card>
        </b-col>
      </b-row>
    </b-container>
  </div>
</template>

<script>
import storage_util from "../javascript_modules/storage_util";
import BusinessProfile from "../components/business/BusinessProfile";
import CatalogueManager from "../components/business/CatalogueManager"
import MyListings from "../views/SaleListingManager";
import InventoryManager from "../components/business/InventoryManager";
import SalesHistory from "../components/business/SalesHistory";
import SalesReport from "../components/business/SalesReport";

export default {
  name: "BusinessManagement",
  mounted() {
    try {
      this.tabIndex = this.tabs.findIndex(tab => tab === this.$route.hash);
    } catch (err) {
      this.tabIndex = 0;
    }
    this.updateDetails();
    window.addEventListener(storage_util.SWITCHED_ACCOUNT_EVENT, () => {
      this.updateDetails();
    });
  },
  data: function () {
    return {
      actingAs: null,
      actingAsBusiness: false,
      business: null,
      businessName: "",
      tabIndex: 0,
      tabs: ['#profile', '#catalogue', '#inventory', '#sales', '#salesManager', '#history', '#salesReport'],
      tabRefs: ['profileManager', 'catalogueManager', 'inventoryManager', 'salesManager', 'historyManager', 'salesReportManager']
    }
  },
  components: {
    SalesReport,
    SalesHistory,
    MyListings,
    BusinessProfile,
    CatalogueManager,
    InventoryManager
  },
  methods: {
    /**
     * Gets the current status of action.
     * If the user is currently acting as an existing business, the actingAsBusiness property is set to true. This
     * allows the user to view and access the controls to manage their selected business. The name of the selected
     * business is also retrieved from storage.
     *
     * Otherwise, actingAsBusiness will be set to false, preventing the user from
     * seeing and accessing the controls to manage a business.
     */
    updateDetails() {
      this.actingAs = storage_util.getActingAs();
      if (this.actingAs !== storage_util.ACTING_AS_CURRENT_USER && this.actingAs !== null) {
        this.actingAsBusiness = true;
        this.business = storage_util.getAvailableBusinesses()[this.actingAs];
        this.businessName = this.business.name;
      } else {
        this.actingAsBusiness = false;
      }
    },
  },
  watch: {
    /**
     * Triggers when there is a change in value of tab index, meaning when the user switches between tabs.
     * Finds the corresponding ref value and calls refreshData() function for that component.
     * @param val
     */
    tabIndex(val) {
      if (val >= 0) {
        const ref = this.tabRefs[val];
        this.$refs[ref].refreshData();
      }
    }
  }
}
</script>

<style scoped>
.tab-pane {
  height: 100%;
}

.my-tab-content {
  min-height: 100%;
  background: rgba(80, 10, 10, 0.5);
}
</style>