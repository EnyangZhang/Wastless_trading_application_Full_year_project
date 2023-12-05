<template>
  <div id="addSaleListing">
    <b-container fluid="true">
      <!-- Rows and columns centre the form in the middle of the page -->
      <b-row class="row justify-content-center">
        <b-col md="5" cols="auto" align-self="center">
          <b-card id="addSaleListingPane">
            <b-card-title class="justify-content-center">{{cardTitle}}</b-card-title>
            <clickable-image id="img-preview" read-only :src="currentImage"/>
            <b-form id="sale-listing-form" @submit="onSubmit">
              <!-- Required input field for inventory item id -->
              <div id="inventoryItemId">
                <b-form-select block
                               v-model="form.inventoryItemId"
                               :options="inventoryItems"
                               v-on:change="fillInputs"
                               :state="inventoryItemIdValidation"/>
                <b-form-invalid-feedback :state="inventoryItemIdValidation">
                  {{error.inventoryItemId}}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="inventoryItemIdValidation "></b-form-valid-feedback>
              </div>
              <br/>
              <!-- Required input field for quantity populates when inventory item is selected -->
              <div id="quantity">
                <b-form-input block
                              type="number"
                              name="quantity"
                              v-model="form.quantity"
                              placeholder="Quantity*"
                              :state="quantityValidation"
                              v-on:input="calculateTotalPrice">
                </b-form-input>
                <b-form-invalid-feedback :state="quantityValidation">
                  {{error.quantity}}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="quantityValidation "></b-form-valid-feedback>
              </div>
              <br/>
              <!-- Required Input field for total price -->
              <div class="totalPrice">
                <b-input-group :prepend="`${currencySymbol}`" :append="`${currencyCode}`">
                  <b-form-input block
                                type="number"
                                name="totalPrice"
                                step="0.01"
                                min="0"
                                title="Please enter the total price"
                                v-model="form.price"
                                placeholder="Total Price*"
                                :state="totalPriceValidation"/>
                </b-input-group>
                <b-form-invalid-feedback :state="totalPriceValidation">
                  {{error.price}}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="totalPriceValidation "></b-form-valid-feedback>
              </div>
              <br/>
              <!-- Non-required input field for more sale listing info -->
              <div id="moreInfo">
                <b-form-textarea block
                                 type="text"
                                 name="moreInfo"
                                 v-model="form.moreInfo"
                                 placeholder="More info"
                                 :state="notTooLongValidation()">
                </b-form-textarea>
                <b-form-invalid-feedback :state="notTooLongValidation()">
                  Please do not exceed 250 characters.
                </b-form-invalid-feedback>
              </div>
              <br/>
              <!-- Required input field for closing date populates when inventory item is selected -->
              <div id="closes">
                <b-form-input block
                              type="text"
                              name="closes"
                              ref="closing"
                              v-model="form.closes"
                              placeholder="Closing Date*"
                              onfocus="(this.type='datetime-local')"
                              :state="closingDateValidation">
                </b-form-input>
                <b-form-invalid-feedback :state="closingDateValidation">
                  {{error.closes}}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="closingDateValidation"></b-form-valid-feedback>
              </div>
              <br/>
              <b-alert :show="error.length > 0" variant="danger">{{error}}</b-alert>
              <div id="buttons" class="row">
                <div class="col">
                  <b-button block
                            id="cancelButton"
                            name="cancel"
                            @click="goBackToListings">Cancel</b-button>
                </div>
                <div class="col">
                  <b-button block
                            id="submitButton"
                            type="submit"
                            name="submit"
                            variant="success">Add Sale listing</b-button>
                </div>
              </div>
              <br/>
              <b-alert :show="true">{{currencyFeedback}}</b-alert>
            </b-form>
          </b-card>
        </b-col>

      </b-row>

    </b-container>
  </div>

</template>

<script>

import api from "../Api"
import storage_util from "../javascript_modules/storage_util";
import {getCurrency} from "@/javascript_modules/currency_util";
import StorageUtil from "@/javascript_modules/storage_util";
import ClickableImage from "@/components/ClickableImage";
import {getImageURL, getPrimaryImage} from "@/javascript_modules/image_util";
import Validation from '../validation/general.validation';

const SaleListing = {
  name: "SaleListing",
  components: {ClickableImage},
  data() {
    return {
      currentImage: undefined,
      form: {
        inventoryItemId: "",
        quantity: "",
        price: "",
        moreInfo: "",
        closes: ""
      },

      error: {
        inventoryItemId: null,
        quantity: null,
        price: null,
        moreInfo: null,
        closes: null
      },
      inventoryItems: [],

      cardTitle: "Create New Sale Listing",
      addSaleListingClicked: null,

      businessCountry: '',
      businessId: '',
      currencyFeedback: "Your business's home country is unknown. The default currency is $USD",
      currencySymbol: '$',
      currencyCode: 'USD',

      inventoryQuantity: 0,
      inventoryPrice: 0,
      inventoryTotalPrice: 0,
      sellingQuantity: 0 // Used in calculation for the available quantity of an inventory item
    }
  },

  async mounted() {
    await this.getCurrency();
    await this.getInventoryItems();
  },

  methods: {
    /**
     * Get the business country for calculating currency
     */
    async getCurrency() {
      let businessId = StorageUtil.getActingAs();
      if (businessId !== 'ACTING_AS_CURRENT_USER') {
        await api.getBusiness(businessId)
            .then(async (response) => {
              this.businessCountry = response.data.address.country;
              const currencyInfo = await getCurrency(this.businessCountry);
              this.currencySymbol = currencyInfo.symbol;
              this.currencyCode = currencyInfo.code;
              this.currencyFeedback = currencyInfo.feedback
            });
      }
    },

    /**
     * Get the business Id
     */
    async getBusinessId() {
      this.businessId = await storage_util.getActingAs();
      if (this.businessId === null) {
        await this.$router.push('/home')
      }
    },

    notTooLongValidation() {
      if (this.form.moreInfo) {
        return this.form.moreInfo.length <= 250;
      }
    },

    /**
     * Get the inventory Items for dropdown options
     */
    async getInventoryItems() {
      if (this.businessId === '') {
        await this.getBusinessId();
      }
      if (this.businessId !== 'ACTING_AS_CURRENT_USER') {
        api.getInventory(this.businessId)
            .then((response) => {
              this.inventoryItems = [];
              const inventory = response.data;

              inventory.forEach(async (inventoryItem) => {
                await this.getSaleListingsQuantity(inventoryItem.inventoryItemId)
                const product = inventoryItem.product;
                const primaryImage = getPrimaryImage(product.images);
                const imageURL = primaryImage
                    ? getImageURL(this.businessId, product.id, primaryImage.productImageId, true)
                    : undefined;
                const pricePerItem = inventoryItem.pricePerItem
                    ? `${this.currencySymbol}${Number(inventoryItem.pricePerItem).toFixed(2)}`
                    : `${this.currencySymbol}${Number(0).toFixed(2)}`;
                const totalPrice = inventoryItem.totalPrice
                    ?`${this.currencySymbol}${Number(inventoryItem.totalPrice).toFixed(2)}`
                    : `${this.currencySymbol}${Number(0).toFixed(2)}`;
                const availableQuantity = inventoryItem.quantity - this.sellingQuantity
                const item = {
                  value: inventoryItem.inventoryItemId,
                  text: `${Validation.truncateString(inventoryItem.product.name)} - ${inventoryItem.product.id} - Price Per Item: ${pricePerItem}, Total Price: ${totalPrice} - Available Quantity: ${availableQuantity}`,
                  quantity: availableQuantity,
                  pricePerItem: inventoryItem.pricePerItem,
                  totalPrice: inventoryItem.totalPrice,
                  expires: inventoryItem.expires,
                  primaryImage: imageURL
                }
                if (item.quantity > 0) {
                  this.inventoryItems.push(item);
                }
              })
            })
            .catch()
      }
    },

    /**
     * Method that goes through all unsold sale items for a business and
     * gets the total quantity of all unsold sale items these are then used
     * to calculate the available quantity of an inventory item.
     */
    async getSaleListingsQuantity(inventoryItemId) {
      let totalQuantity = 0;
      if (this.businessId !== 'ACTING_AS_CURRENT_USER') {
        await api.getSaleListings(this.businessId, false)
            .then((response) => {
              let saleListings = response.data;
              saleListings.forEach((saleListing) => {
                if (inventoryItemId === saleListing.inventoryItem.inventoryItemId) {
                    totalQuantity += saleListing.quantity;
                  }
              })
              this.sellingQuantity = totalQuantity;
        }).catch(() => {
          this.sellingQuantity = 0;
        })
      }
    },

    /**
     * Autofill input fields when an inventory item is selected in the dropdown
     */
    fillInputs() {
      for (let inventory of this.inventoryItems) {
        if (inventory.value === this.form.inventoryItemId) {
          this.form.quantity = 1;
          this.form.price = inventory.pricePerItem ? parseFloat(inventory.pricePerItem) : 0;
          this.$refs.closing.$el.focus();
          this.form.closes = inventory.expires + 'T12:00';
          this.currentImage = inventory.primaryImage;
          this.inventoryPrice = parseFloat(inventory.pricePerItem);
          this.inventoryTotalPrice = inventory.totalPrice ? parseFloat(inventory.totalPrice) : 0;
          this.inventoryQuantity = parseInt(inventory.quantity);
        }
      }
    },

    /**
     * Checks that all fields are correctly validated for when the add listing button is clicked
     *
     * @returns {boolean}
     */
    canSubmit() {
      return this.isQuantityValid()
          && this.isTotalPriceValid()
          && this.isInventoryIdValid()
          && this.isClosingDateValid()
          && this.isInventorySelected();
    },

    /**
     * Sends post request to the server if all fields are validated
     *
     * @param event
     */
    onSubmit(event) {
      this.addSaleListingClicked = true;
      event.preventDefault()
      if (this.canSubmit()) {
        event.preventDefault();
        let saleListing = Object.assign({}, this.form)
        api.addSaleListing(this.$route.params.businessId, saleListing)
            .then((response) => {
              if (response.status === 201) {
                this.$router.push({name: "ManageBusiness", hash: "#sales"});
              }
              this.error = {
                    inventoryItemId: null,
                    quantity: null,
                    price: null,
                    moreInfo: null,
                    closes: null
              };
            })
            .catch((error) => {
              if (error.response.status === 400 && error.response.data.message === "Quantity exceeded inventory item quantity.") {
                this.error.quantity = error.response.data.message;
              }
              event.preventDefault();
            })
      } else {
        event.preventDefault();
      }
    },

    /**
     * Called when clicking cancel button on the add listing form, routes user to my listings tab on the manage business page
     */
    goBackToListings() {
      this.$router.push({name:"ManageBusiness", hash:"#sales"});
    },

    /**
     * The following functions are to validate the inputs on the form
     */

    /**
     * Checks if a quantity of at least one or at most the available quantity is entered
     * @returns {null|boolean}
     **/
    isQuantityValid() {
      if (!this.form.quantity) {
        if (!this.addSaleListingClicked) {
          return null
        }
        this.error.quantity = "Cannot be empty."
        return false
      } else if (this.form.quantity < 1) {
        this.error.quantity = "Quantity must be at least 1."
        return false
      } else if (this.form.quantity > this.inventoryQuantity) {
        this.error.quantity = "Quantity cannot be more than the available quantity."
        return false
      }
      return true
    },

    /**
     *Asserts that the inventory item id selected is not empty, unless the add sale listing button has not yet been
     * clicked.
     */
    isInventoryIdValid() {
      if (!this.form.inventoryItemId) {
        if (!this.addSaleListingClicked) {
          return null
        }
        this.error.inventoryItemId = "Cannot be empty."
        return false
      }
      return true
    },

    /**
     * Checks if a closing date has been entered
     * @returns {null|boolean}
     **/
    isClosingDateValid() {
      if (this.form.closes) {
        return true
      } else {
        if (!this.addSaleListingClicked) {
          return null;
        }
        this.error.closes = "Cannot be empty.";
        return false;
      }
    },
    /**
     * Checks if the total price is valid if it exists
     * A valid total price is a value greater than zero
     * @returns {null|boolean}
     */
    isTotalPriceValid() {
      if (this.form.price === undefined || this.form.price === "") {
        if (!this.addSaleListingClicked) {
          return null;
        }
        this.error.price = "The sale price must not be empty"
        return false;
      }
      if (this.form.price < 0) {
        this.error.price = "The sale price must be 0 or more.";
        return false;
      }
      return true;
    },

    /**
     * Checks if an inventory item has been selected
     * @returns boolean
     **/
    isInventorySelected() {
      return this.form.inventoryItemId !== null
    },
    /**
     * Called when either the quantity or price field is updated
     * Auto-calculates the total price for a sale item
     */
    calculateTotalPrice() {
      let totalPrice = 0
      if (this.form.quantity > this.inventoryQuantity) {
        totalPrice = this.inventoryQuantity * this.inventoryPrice
      } else if (parseFloat(this.form.quantity) === this.inventoryQuantity) {
        totalPrice = this.inventoryTotalPrice;
      } else {
        totalPrice = this.form.quantity * this.inventoryPrice
      }
      if (totalPrice <= 0) {
        this.form.price = 0
      } else {
        this.form.price = totalPrice
      }
    }

  },

  computed: {
    // The computed functions below update reactively to user input to check whether or not the input fields are correct
    /**
     * Computes the validation state for inventory ID selection form field.
     * @returns {boolean}
     */
    inventoryItemIdValidation() {
      return this.isInventoryIdValid()
    },

    /**
     * Computes validation state for sale listing price form field.
     * @returns {null|boolean}
     */
    totalPriceValidation() {
      return this.isTotalPriceValid()
    },

    /**
     * Computes the validation state for the quantity form field of sale listing.
     * @returns {null|boolean}
     */
    quantityValidation() {
      return this.isQuantityValid()
    },

    /**
     * Computes the validation state for the closing date form field of sale listing.
     * @returns {null|boolean}
     */
    closingDateValidation() {
      return this.isClosingDateValid()
    }
  }
}

export default SaleListing;

</script>

<style scoped>

#addSaleListingPane {
  margin-top: 5vh;
  margin-bottom: 5vh;
}


</style>