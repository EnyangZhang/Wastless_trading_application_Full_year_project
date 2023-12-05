<template>
  <div v-if="actingAsBusiness">
    <b-card id="resultsCard"
            fluid="true">

      <b-card id="businessCatalogue" fluid>
        <b-card-title v-bind:title="businessName"/>
        <b-table striped
                 hover
                 responsive="sm md lg"
                 :items="items"
                 :fields="fields"
                 sort-icon-left
                 :per-page="perPage"
                 :current-page="currentPage"
                 @row-clicked="rowClickHandler">
          <template #cell(image)="data">
            <img id="item-image"
                 :src="data.item.image || require('../../../public/default.png')"
                 class="img-thumbnail"
                 alt="Default inventory image">
          </template>
          <template #cell(created)="data">
            {{formattedDate(data.item.created)}}
          </template>
        </b-table>
      </b-card>
      <b-row id="item">
        <b-col>
          <paginator :page="currentPage"
                     :items-length="items.length"
                     :per-page="perPage"
                     @changePage="changePage"/>
          <b-button id="new-product-button"
                    variant="success"
                    @click="addProduct">Add Product</b-button>
        </b-col>
      </b-row>
    </b-card>

  </div>

  <div v-else>
  </div>
</template>

<script>
import Api from "../../Api";
import {formatDateString} from "../../javascript_modules/date_util";
import StorageUtil from "../../javascript_modules/storage_util";
import {displayPrice, getCurrency} from "../../javascript_modules/currency_util";
import Paginator from "@/components/Paginator";
import ClickableImage from "@/components/ClickableImage"
import Validation from '@/validation/general.validation';

const CatalogueManager = {
  name: "HomePage",
  components: {Paginator, ClickableImage},
  data() {
    return {
      currentPage: 1,      // current page of search results
      perPage: 5,         // number of search results displayed per page
      totalPage: null,

      fields: [
        {
          key: 'image',
          label: ""
        },
        {
          label: 'Name',
          key: 'truncatedName',
          sortable: true
        },
        {
          label: 'Product ID',
          key: 'id',
          sortable: true
        },
        {
          key: 'description',
          sortable: true
        },
        {
          key: 'manufacturer',
          sortable: true
        },
        {
          key: 'recommendedRetailPrice',
          label: 'RRP',
          sortable: true,
        },
        {
          label: "Created Date",
          key: 'created',
          sortable: true
        }
      ],
      items: [],
      currentCatalogueId: '',
      businessName: '',
      businessCountry: '',
      currency: {
        symbol: undefined,
        code: undefined
      },
      isCurrencyUpDated: false,
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
    window.addEventListener(StorageUtil.SWITCHED_ACCOUNT_EVENT, async () => {
      this.getCurrentUserActAs();
      await this.getBusinessNameAndCountry();
      this.currency = await getCurrency(this.businessCountry);
      this.getCatalogue();
    });
    this.getCurrentUserActAs();
    await this.getBusinessNameAndCountry();
    this.currency = await getCurrency(this.businessCountry);
    this.getCatalogue();
  },

  methods: {
    /**
     * Function to call the method to get data that the parent is able to call for any related component.
     **/
    refreshData() {
      this.getCatalogue();
    },

    /**
     * Redirect to add Product page.
     */
    addProduct() {
      this.$router.push("/business/" + this.businessId + "/add-product");
    },

    /**
     * Gets the current account that user acting as and sets the business ID.
     */
    getCurrentUserActAs() {
      this.actingAs = StorageUtil.getActingAs();
      this.actingAsBusiness = this.actingAs !== StorageUtil.ACTING_AS_CURRENT_USER
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
    getCatalogue() {
      if (this.actingAsBusiness === true) {
        Api.getBusinessCatalogue(this.businessId)
            .then(async (response) => {
              this.items = []
              let businessCatalogue = response.data
              for (let product of businessCatalogue) {
                let id = product.id
                let truncatedName = Validation.truncateString(product.name)
                let name = product.name
                let recPrice = displayPrice(product.recommendedRetailPrice, this.currency.symbol, this.currency.code);
                const intPrice = product.recommendedRetailPrice;
                const created = product.created;
                let manufacturer = product.manufacturer
                let description = product.description

                let primaryImage;
                let imageId;
                if (product.primaryProductImage) {
                  imageId = product.primaryProductImage.productImageId;
                  primaryImage = `${process.env.VUE_APP_SERVER_ADD}/businesses/${this.businessId}/products/${id}/images/${imageId}/thumbnail`
                } else {
                  primaryImage = null
                  imageId = null
                }

                let images = product.images
                let imageIds = []
                for (let image of images) {
                  imageIds.push(image.productImageId);
                }

                let item = {
                  image: primaryImage,
                  id: id,
                  name: name,
                  truncatedName: truncatedName,
                  description: description,
                  manufacturer: manufacturer,
                  recommendedRetailPrice: recPrice,
                  created: created,
                  intPrice: intPrice,
                  images: imageIds,
                  primaryImageId: imageId
                }
                this.items.push(item)
              }
              this.calculateTotalPage()
            }).catch();
      }
    },

    /**
     * Get business name that the catalogue user is viewing.
     */
    async getBusinessNameAndCountry() {
      if (this.actingAsBusiness === true) {
        await Api.getBusiness(this.businessId)
            .then((response) => {
              let business = response.data
              this.businessName = business.name
              this.businessCountry = business.address.country;
            }).catch();
      }
    },

    /**
     * Updates the current page to control the results displayed in the table.
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

    /** This method is called when a search result row is clicked on. */
    // eslint-disable-next-line no-unused-vars
    rowClickHandler: function (record, index) {
      this.$router.push({
        path: "/business/" + this.businessId + "/edit-product/" + record.id,
        query: {
          code: record.id,
          name: record.name,
          description: record.description,
          manufacturer: record.manufacturer,
          rrp: record.intPrice,
          primaryImageId: record.primaryImageId,
          images: record.images,
        }
      });
    },

    /**
     * Reset Modal to not display error when user open the edit popup again.
     */
    resetModal() {
      this.nameState = null
      this.descriptionState = null
      this.manufacturerState = null
    },

    /**
     * Check if all fields are valid to send to backend.
     * @returns True if all fields are correct, false if any of the field is incorrect.
     */
    checkFormValidity() {
      this.$refs.form.checkValidity()
      this.nameState = this.nameValidation(this.selectedProductCatalogue.name)
      this.descriptionState = this.descriptionValidation(this.selectedProductCatalogue.description)
      this.manufacturerState = this.manufacturerValidation(this.selectedProductCatalogue.manufacturer)
      return this.nameState && this.descriptionState && this.manufacturerState
    },

    /**
     * Ok button handler to prevent the popup to be hidden when fields are not valid.
     * @param bvModalEvt
     */
    handleOk(bvModalEvt) {
      // Prevent modal from closing
      bvModalEvt.preventDefault()
      // Trigger submit handler
      this.handleSubmit()
    },

    /**
     * Send catalogue update to backend if all fields are valid, and will hide the popup.
     */
    handleSubmit() {
      if (!this.checkFormValidity()) {
        return
      }
      Api.modifyProductCatalogue(this.businessId, this.currentCatalogueId, this.selectedProductCatalogue)
          .then((response) => {
            if (response.status === 200) {
              this.getCatalogue()
            }
          })
          .catch();
      this.$nextTick(() => {
        this.$bvModal.hide('edit-catalogue-modal')
      })
    },

    /**
     * Do nothing but just maps to cancel handler.
     */
    cancelChanges() {
      // This function is intended to do nothing
    },

    /**
     * Validate name input field, return true if name field is not empty.
     */
    nameValidation(Name) {
      return !!Name;
    },

    /**
     * Validate description input field, return true if description field is not empty.
     */
    descriptionValidation(description) {
      return !!description;
    },

    /**
     * Validate manufacturer input field, return true if manufacturer field is not empty.
     */
    manufacturerValidation(manufacturer) {
      return !!manufacturer;
    },

    /**
     * Calls format date function from module. If date is undefined, returns undefined without parsing.
     */
    formattedDate(date) {
      return date ? formatDateString(date) : undefined;
    }
  },
  computed: {
    /**
     * Show current business name as title in popup.
     * @returns Business name.
     */
    viewProfileTitle() {
      return `${this.selectedProductCatalogue.catalogueId}`;
    },
  },
  watch: {
    isCurrencyUpDated() {
      this.getCatalogue()
    }
  }
}
export default CatalogueManager;
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