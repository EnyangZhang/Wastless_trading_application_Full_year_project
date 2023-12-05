<template>
  <div v-if="actingAsBusiness">
    <b-card id="card"
            fluid="true">

      <b-card id="businessInventory" fluid>
        <b-card-title v-bind:title="businessName"/>
        <b-table id="table"
                 class="justify-content-center"
                 striped
                 hover
                 :responsive="true"
                 :items="items"
                 :fields="fields"
                 sort-icon-left
                 :per-page="perPage"
                 :current-page="currentPage">
          <template #cell(image)="data">
            <img id="item-image"
                 :src="data.item.image || require('../../../public/default.png')"
                 class="img-thumbnail"
                 alt="Default inventory image">
          </template>
          <template #cell(manufactured)="data">
            {{formattedDate(data.item.manufactured)}}
          </template>
          <template #cell(created)="data">
            {{formattedDate(data.item.created)}}
          </template>
          <template #cell(sellBy)="data">
            {{formattedDate(data.item.sellBy)}}
          </template>
          <template #cell(expiry)="data">
            {{formattedDate(data.item.expiry)}}
          </template>
        </b-table>
      </b-card>

      <b-row id="item">
        <b-col>
          <paginator :page="currentPage"
                     :items-length="items.length"
                     :per-page="perPage"
                     @changePage="changePage"/>
          <b-button id="new-inventory-button"
                    variant="success"
                    @click="addInventory">Add Inventory</b-button>
        </b-col>

      </b-row>
    </b-card>
  </div>

  <div v-else>
  </div>

</template>

<script>
import api from "../../Api";
import storage_util from "../../javascript_modules/storage_util";
import {displayPrice, getCurrency} from "@/javascript_modules/currency_util";
import {formatDateString} from "@/javascript_modules/date_util";
import Paginator from "../Paginator";
import {getImageURL, getPrimaryImage} from "@/javascript_modules/image_util";
import Validation from '@/validation/general.validation';

export default {
  name: "InventoryManager",
  components: {
    Paginator
  },
  data() {
    return {
      currentPage: 1,      // current page of search results
      perPage: 5,         // number of search results displayed per page
      totalPage: null,

      fields: [
        {
          key: "image",
          label: ""
        },
        {
          key: 'product',
          sortable: true,
        },
        {
          key: 'productId',
          sortable: true
        },
        {
          key: 'quantity',
          sortable: true
        },
        {
          key: 'pricePerItem',
          label: 'Price per item',
          sortable: true
        },
        {
          key: 'totalPrice',
          sortable: true
        },
        {
          key: 'manufactured',
          sortable: true
        },
        {
          key: 'sellBy',
          sortable: true
        },
        {
          key: 'expiry',
          sortable: true
        },
        {
          key: 'created',
          label: "Created Date",
          sortable: true
        }
      ],
      items: [],
      currentCatalogueId: '',
      businessName: '',
      businessCountry: null,
      currency: null,
      actingAsBusiness: false,
      actingAs: null,

      //validation
      nameState: null,
      descriptionState: null,
      manufacturerState: null,

      //Testing purpose, waiting for business switching role feature finish.
      businessId: 1,

      selectedProductCatalogue: {
        catalogueId: '',
        name: '',
        description: '',
        manufacturer: '',
        RRP: '',
      },
    }
  },
  async mounted() {
    await this.getCurrentUserActAs();
    await this.getBusinessDetails();
    await this.getInventory();
  },

  methods: {
    /**
     * Function to call the method to get data that the parent is able to call for any related component.
     **/
    refreshData() {
      this.getInventory();
    },

    /**
     * Redirect to add Inventory page.
     */
    addInventory() {
      this.$router.push("/business/" + this.businessId + "/add-inventory");
    },

    /**
     * Gets the current account that user acting as and sets the business ID.
     */
    getCurrentUserActAs() {
      this.actingAs = storage_util.getActingAs();
      this.actingAsBusiness = this.actingAs !== storage_util.ACTING_AS_CURRENT_USER
      this.getBusinessId();
    },

    /**
     * Sets the current business ID that the user acting as if the user is acting as a business.
     */
    getBusinessId() {
      if (this.actingAsBusiness === true) {
        this.businessId = this.actingAs;
      }
    },

    /**
     * Update table view of the product catalogue.
     */
    getInventory() {
      if (this.actingAsBusiness === true) {
        api.getInventory(this.businessId)
            .then((response) => {
              this.items = [];
              let businessInventory = response.data;
              for (let inventory of businessInventory) {
                let id = inventory.id;
                let product = Validation.truncateString(inventory.product.name);
                let productId = inventory.product.id;
                let quantity = inventory.quantity;
                let pricePerItem = displayPrice(inventory.pricePerItem, this.currency.symbol, this.currency.code)
                let totalPrice = displayPrice(inventory.totalPrice, this.currency.symbol, this.currency.code);
                const manufactured = inventory.manufactured;
                const sellBy = inventory.sellBy;
                const expiry = inventory.expires;
                const created = inventory.created;

                const primaryImage = inventory.product.images ?  getPrimaryImage(inventory.product.images) : undefined;
                const primaryImageURL = primaryImage
                    ? getImageURL(this.businessId, productId, primaryImage.productImageId, true)
                    : undefined

                let item = {
                  image: primaryImageURL,
                  id: id,
                  product: product,
                  productId: productId,
                  quantity: quantity,
                  pricePerItem: pricePerItem,
                  totalPrice:totalPrice,
                  manufactured: manufactured,
                  sellBy: sellBy,
                  expiry: expiry,
                  created: created
                };
                this.items.push(item);
              }
              this.calculateTotalPage();
            });
      }
    },

    /**
     * Get business name that the catalogue user is viewing.
     */
    async getBusinessDetails() {
      if(this.actingAsBusiness === true) {
        let response = await api.getBusiness(this.businessId)

        let business = response.data
        this.businessName = business.name
        this.businessCountry = business.address.country;
        this.currency = await getCurrency(this.businessCountry)

      }
    },

    /**
     * Handler for the changePage event of paginator.
     * Updates the current page attribute to the value passed in by the paginator, when the event is called.
     */
    changePage(page) {
      this.currentPage = page;
    },

    /**
     * Calculate total page regards to number of items to show.
     */
    calculateTotalPage() {
      this.totalPage = Math.ceil(this.items.length / this.perPage)
      if (this.totalPage === 0) {
        this.totalPage = 1
      }
    },


    /**
     * Do nothing but just maps to cancel handler.
     */
    cancelChanges() {
      // This function is intended to do nothing
    },

    /**
     * Calls format date function from module. If date is undefined, returns undefined without parsing.
     */
    formattedDate(date) {
      return date ? formatDateString(date) : undefined;
    }
  },
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

#item {
  padding: 20px;
}

#item-image {
  width: 100%;
  height: 100%;
  min-width: 125px;
  min-height: 125px;
  max-width: 125px;
  max-height: 125px;
}

</style>