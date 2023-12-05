<template>
  <div class="page">
    <b-card fluid="true">

      <DataView id="sale-item-data-view"
                :value="results"
                :layout="layout">
        <template #header>
          <div class="col">
            <div class="row">
              <div id="filter-column" class="col">

                <div class="row">
                  <div class="col-md">
                    <b-input-group>
                      <b-form-input id="search-field"
                                    v-model="searchStringInput"
                                    @keydown.enter="searchClick()"
                                    placeholder="Search Sale Listings"/>
                      <b-input-group-append>
                        <b-button id="search-btn" @click="searchClick()">Search</b-button>
                      </b-input-group-append>
                    </b-input-group>
                  </div>

                  <div class="col-md align-content-lg-start">
                    <sort-selection
                        :sort-options="sortOptions"
                        :selected="sortOptions[0]"
                        @sortChange="sortChange($event, false)"
                    />
                  </div>
                </div>

                <FilterSelector
                    class="row-md-auto align-content-lg-start"
                    :filters="filters"
                    style="padding: 10px 0 0;"
                    @filterFieldChange="filterUpdate($event, false)"
                    @filterFieldInitialise="filterUpdate($event, true)"
                />
              </div>

              <div id="layout-switch" class="col-lg-auto">
                <br>
                <span class="p-buttonset">
                  <Button
                      id="grid-layout-button"
                      class="p-button-lg"
                      :disabled="layout === layouts['grid']"
                      icon="pi pi-th-large"
                      @click="layout = layouts['grid']"/>
                  <Button
                      id="list-layout-button"
                      class="p-button-lg"
                      :disabled="layout === layouts['list']"
                      icon="pi pi-list"
                      @click="layout = layouts['list']"/>
                </span>
              </div>
            </div>
            <div id="error-box" class="row-auto" v-if="error.length > 0">
              <br>
              <b-alert :show="true">{{error}}</b-alert>
            </div>
          </div>
        </template>
        <template #list="listing">
          <div class="p-col-12" :key="listing.data.id">
            <SaleItemCard :event-bus="eventBus" :sale-listing="listing.data"/>
          </div>
        </template>

        <template #grid="listing">
          <div class="row-md-auto mx-auto" :key="listing.data.id">
            <SaleItemCard :event-bus="eventBus" :sale-listing="listing.data" is-grid/>
          </div>
        </template>
      </DataView>
      <SaleItemViewDialog
          :sale-listing="currentDetailsShowing"
          @purchaseClicked="handlePurchase"
          @likeClicked="getSaleItems"
          v-if="showModal"
          ref="saleItemDetails">
        <router-view name="fullListing"></router-view>
      </SaleItemViewDialog>

      <Paginator
          id="search-paginator"
          :items-length="totalPages"
          :page="currentPage"
          :per-page="itemsPerPage"
          :buttonDisabled="disablePaginator"
          @changePage="changePage($event)"
      />
    </b-card>
  </div>
</template>

<script>
import Api from "@/Api";
import SaleItemCard from "@/components/sale-item-search/SaleItemCard";
import sortSelection from "@/components/SortSelection";
import FilterSelector from "@/components/filter/FilterSelector";
import Paginator from "@/components/Paginator";
import SaleItemViewDialog from "@/components/sale-item-search/SaleItemViewDialog";
import Vue from "vue";
import {getCurrency} from "../../javascript_modules/currency_util";

export default {
  name: "SaleItemDataView",
  components: {
    SaleItemViewDialog,
    FilterSelector,
    SaleItemCard,
    sortSelection,
    Paginator
  },
  mounted() {
    this.eventBus.$on("cardClicked", this.showDialog);
    this.eventBus.$on("purchaseClicked", this.handlePurchase);

    this.selectedSort = this.sortOptions[0];
    this.selectedDirection = {text: 'Ascending', value: "asc"};
    this.getSaleItems();
    this.$root.$on('listingNotificationClicked', (listingId) => {
      this.handleLikedNotificationClicked(listingId);
    });
    if (this.$route.meta.showModal) {
      this.handleLikedNotificationClicked(this.$route.params.id)
    }
  },
  data() {
    return {
      // System state
      disablePaginator: false,
      eventBus: new Vue(),

      showModal: this.$route.meta.showModal,

      error: "",
      layout: "grid",
      layouts: {list: 'list', grid: 'grid'},

      totalPages: 1,
      currentPage: 1,
      itemsPerPage: 12,

      currentDetailsShowing: null,

      results: [],

      // Sorting and filtering.
      sortOptions: [
        {id: 1, label: 'Product Name', value: 'name'},
        {id: 2, label: 'Seller', value: 'seller'},
        {id: 3, label: 'Suburb', value: 'suburb'},
        {id: 4, label: 'City', value: 'city'},
        {id: 5, label: 'Country', value: 'country'},
        {id: 6, label: 'Date Created', value: 'created'},
        {id: 7, label: 'Expiry Date', value: 'expires'},
        {id: 8, label: 'Price', value: 'price'},
        {id: 9, label: 'Quantity', value: 'quantity'}
      ],
      filters: [
        {name: "Closing Date", type: "date"},
        {name: "Price", type: "price"},
        {name: "Business Type", type: "dropdown", options: [
            {value: null, text: ""},
            {value: "Accommodation and Food Services", text: "Accommodation and Food Services"},
            {value: "Retail Trade", text: "Retail Trade"},
            {value: "Charitable Organisation", text: "Charitable Organisation"},
            {value: "Non-Profit Organisation", text: "Non-Profit Organisation"}
          ]}
      ],

      // Selected sorting and filtering options.

      selectedSort: undefined,
      selectedDirection: undefined,
      searchString: null,
      searchStringInput: null,
      filterOptions: {},
    }
  },
  watch: {
    '$route.meta' ({showModal}) {
      this.showModal = showModal
    }
  },
  methods: {
    /**
     * Callback function that triggers when the option to display more details is clicked.
     * Displays the saleItemDetails modal.
     */
    showDialog(event) {
      this.currentDetailsShowing = event;
      this.$refs.saleItemDetails.show();
    },

    /**
     * When this function is called it updates the search string
     * and refreshes the listings.
     */
    searchClick() {
      this.searchString = this.searchStringInput;
      this.currentPage = 1;
      this.getSaleItems();
    },

    /**
     * This function should be called to update the selected sorting options.
     * @param option an Object containing the sort value and the sort direction.
     */
    sortChange(option) {
      this.selectedSort = option.sort;
      this.selectedDirection = option.direction;
      this.currentPage = 1;
      this.getSaleItems();
    },

    /**
     * This function should be called to update the selected filter options.
     * @param data an object containing objects of the filters and their selected values.
     * @param initialising a Boolean that defines whether the sort change is due to
     * initialisation. If so it does not refresh the listings.
     */
    filterUpdate(data, initialising) {
      this.filterOptions = data;
      this.currentPage = 1;
      if (!initialising) {
        this.getSaleItems();
      }
    },

    /**
     * This function should be called on the page changing to refresh the
     * visible late item listings.
     * @param page the page number being switched to.
     */
    changePage(page) {
      this.currentPage = page
      this.getSaleItems()
    },

    /**
     * Retrieves the sale items with all of the selected filters and ordering options.
     */
    getSaleItems() {
      this.searchStringInput = this.searchString;
      this.disablePaginator = true
      Api.searchSaleListing(
        this.searchString,
        this.filterOptions.businesstype,
        this.filterOptions.price.max,
        this.filterOptions.price.min,
        this.filterOptions.closingdate.min,
        this.filterOptions.closingdate.max,
        this.currentPage,
        this.itemsPerPage,
        this.selectedSort.value,
        this.selectedDirection.value).then(async (response) => {
          if (response.status === 200) {
            this.results = response.data;
            this.totalPages = Number(response.headers["total-length"]);
            this.error = "";

            // Re-sets the current sale item being viewed in dialog
            // This is for when the user likes a sale item and the
            // number of likes needs to increase.
            if (this.currentDetailsShowing !== null) {
              const current = this.results.find((saleItem) => {
                return saleItem.id === this.currentDetailsShowing.saleItem.id;
              })
              if (current !== null && current !== undefined) {
                this.currentDetailsShowing = {saleItem: current};
              }
            }
          }
        this.disablePaginator = false;
      }).catch((err) => {
        if (err.response.status === 400) {
          this.error = "Invalid Query";
        } else if (err.response.status === 404) {
          this.error = "Could not find server";
        } else {
          this.error = "Unknown Error";
        }
      });

    },

    /**
     * Calls API to purchase a given sale listing. Then notifies the user
     * whether the purchase was successful. Then refreshes sale listings.
     *
     * If some error is returned isn't an internal server error, displays the error message, otherwise returns a generic
     * error message.
     */
    handlePurchase(purchasedSaleItem) {
      Api.purchaseSaleListing(purchasedSaleItem.businessId, purchasedSaleItem.id).then((response) => {
        if (response.status === 200) {
          this.$bvToast.toast(`You purchased ${purchasedSaleItem.inventoryItem.product.name}`, {
            title: 'Purchase Successful',
            variant: 'success',
            toaster: 'b-toaster-bottom-right',
            autoHideDelay: 5000,
            appendToast: true
          });
        }
        this.getSaleItems();
      }).catch((err) => {
        const message = err.response.status !== 500 && err.response.data.message
            ? `${err.response.data.message}.`
            : "Could not make purchase.";
        this.$bvToast.toast(`${message}`, {
          title: 'Purchase Unsuccessful',
          variant: 'danger',
          toaster: 'b-toaster-bottom-right',
          autoHideDelay: 5000,
          appendToast: true
        });
        this.getSaleItems();
      });
    },

    /**
     * Retrieves the specific sale listing from the backend, getting the currency from the sale listings country
     * then calls the showDialog method with an object containing the sale listing and currency
     * as the parameter
     * @param listingId Id of the sale listing being requested
     */
    handleLikedNotificationClicked(listingId) {
      Api.getSaleListing(listingId).then(async (response) => {
        if (response.status === 200) {
          let saleItem = response.data;
          let currency = await getCurrency(saleItem.country)
          let dialogData = {
            saleItem: saleItem,
            currency: currency
          }
          this.showDialog(dialogData)
        }
      }).catch((error)=> {
        const message = error.response.status !== 500 && error.response.data.message
            ? `${error.response.data.message}.`
            : "Could not find sale listing.";
        this.$bvToast.toast(`${message}`, {
          title: 'Unable to retrieve sale listing',
          variant: 'danger',
          toaster: 'b-toaster-bottom-right',
          autoHideDelay: 5000,
          appendToast: true
        });
      })
    }
  }
}
</script>