<template>
  <div id="inventory">
    <b-container id="inventory-container" fluid="true">
      <!-- Rows and columns centre the form in the middle of the page -->
      <b-row class="row justify-content-center">
        <b-col md="5" cols="auto" align-self="center">
          <b-card id="add-inventory-pane">
            <b-card-title class="justify-content-center">{{cardTitle}}</b-card-title>
            <b-form @submit="onSubmit" v-if="show">
              <div class="productId">
                <b-form-select v-model="form.productId"
                              :options="products"
                               title="Please select the product"
                               v-on:change="fillInputs"
                               :state="productIdValidation">
                </b-form-select>
                <b-form-invalid-feedback :state="productIdValidation">
                  {{error.productId}}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="productIdValidation "></b-form-valid-feedback>
              </div>
              <br/>
              <!-- Required input field for quantity  -->
              <div class="id">
                <b-form-input block
                              type="number"
                              name="quantity"
                              v-model="form.quantity"
                              title="Please enter a quantity"
                              placeholder="Quantity*"
                              :state="quantityValidation">
                </b-form-input>
                  <b-form-invalid-feedback :state="quantityValidation">
                    {{error.quantity}}
                  </b-form-invalid-feedback>
                  <b-form-valid-feedback :state="quantityValidation "></b-form-valid-feedback>
              </div>
              <br/>
              <!-- Required input field for price per item -->
              <div class="pricePerItem">
                <b-input-group :prepend="`${currencySymbol}`" :append="`${currencyCode}`">
                  <b-form-input block
                                type="number"
                                step="0.01"
                                min="0"
                                name="pricePerItem"
                                title="Please enter the price per item"
                                v-model="form.pricePerItem"
                                :state="pricePerItemValidation"
                                :placeholder="`Price Per Item`"/>
                </b-input-group>
                <b-form-invalid-feedback :state="pricePerItemValidation">
                  {{error.pricePerItem}}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="pricePerItemValidation "></b-form-valid-feedback>
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
                                v-model="form.totalPrice"
                                :placeholder="`Total Price`"
                                :state="totalPriceValidation"/>
                </b-input-group>
                <b-form-invalid-feedback :state="totalPriceValidation">
                  {{error.totalPrice}}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="totalPriceValidation "></b-form-valid-feedback>
              </div>
              <br/>
              <!-- Required input field for manufactured -->
              <div class="manufactured">
                <b-form-input block
                              type="text"
                              name="manufactured"
                              v-model="form.manufactured"
                              placeholder="Manufactured"
                              title="Please enter the manufactured date"
                              onfocus="(this.type='date')"
                              :state="manufacturedValidation"/>
                <b-form-invalid-feedback :state="manufacturedValidation">
                  {{error.manufactured}}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="manufacturedValidation"></b-form-valid-feedback>
              </div>
              <br/>
              <!-- Required input field for sellBy -->
              <div class="sellBy">
                <b-form-input block
                              type="text"
                              name="sellBy"
                              v-model="form.sellBy"
                              placeholder="Sell by"
                              title="Please enter the sell by date"
                              onfocus="(this.type='date')"
                              :state="sellByValidation"/>
                <b-form-invalid-feedback :state="sellByValidation">
                  {{error.sellBy}}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="sellByValidation"></b-form-valid-feedback>
              </div>
              <br/>
              <!-- Required input field for bestBefore -->
              <div class="bestBefore">
                <b-form-input block
                              type="text"
                              name="bestBefore"
                              v-model="form.bestBefore"
                              placeholder="Best before"
                              title="Please enter the best before date"
                              onfocus="(this.type='date')"
                              :state="bestBeforeValidation"/>
                <b-form-invalid-feedback :state="bestBeforeValidation">
                  {{error.bestBefore}}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="bestBeforeValidation"></b-form-valid-feedback>

              </div>
              <br/>
                <!-- Required input field for expires -->
                <div class="expires">
                  <b-form-input block
                                type="text"
                                name="expires"
                                v-model="form.expires"
                                placeholder="Expiry date*"
                                title="Please enter the expiry date"
                                onfocus="(this.type='date')"
                                :state="expiryValidation"/>
                  <b-form-invalid-feedback :state="expiryValidation">
                    {{error.expires}}
                  </b-form-invalid-feedback>
                  <b-form-valid-feedback :state="expiryValidation"></b-form-valid-feedback>
                </div>
                <br/>
                <div id="buttons" class="row">
                  <div class="col">
                    <b-button block
                              id="cancelButton"
                              name="cancel"
                              @click="goBackToInventory">Cancel</b-button>
                  </div>
                  <div class="col">
                    <b-button block
                              id="submitButton"
                              type="submit"
                              name="submit"
                              variant="success">{{submitButton}}</b-button>
                  </div>
                </div>
                <br/>
                <b-alert :show="true">{{currencyFeedback}}</b-alert>
                <b-alert :show="error.length > 0" variant="danger">{{error}}</b-alert>
              </div>
            </b-form>
          </b-card>
        </b-col>

      </b-row>

    </b-container>
  </div>
</template>

<script>
import api from "../Api.js"
import storage_util from "../javascript_modules/storage_util";
import StorageUtil from "@/javascript_modules/storage_util";
import {getCurrency} from "@/javascript_modules/currency_util";
import Validation from '../validation/general.validation';

export default {
  name: "Inventory",

  data: function () {

    return {
      form: {
        quantity: "",
        productId: '',
        pricePerItem: "",
        totalPrice: "",
        manufactured: "",
        sellBy: "",
        bestBefore: "",
        expires: ""
      },
      error: {
        quantity: null,
        productId: "",
        pricePerItem: "",
        totalPrice: "",
        manufactured: "",
        sellBy: "",
        bestBefore: "",
        expires: ""
      },
      businessCountry: '',
      businessId: '',
      currencyFeedback: "Your business's home country is unknown. The default currency is $USD",
      currencySymbol: '$',
      currencyCode: 'USD',
      now: Date.now(),

      products: [
      ],

      cardTitle: "Add inventory",
      submitButton: "Confirm",

      show : true,
      validCode: false,
      editing: false,
      addInventoryClicked: false,
    }
  },


  async mounted () {
    await this.getCurrency();
    await this.getProducts();
  },

  methods: {
    async getCurrency() {
      let businessId = StorageUtil.getActingAs();
      if (businessId !== 'ACTING_AS_CURRENT_USER') {
        api.getBusiness(businessId)
            .then(async (response) => {
              this.businessCountry = response.data.address.country;
              const currencyInfo = await getCurrency(this.businessCountry);
              this.currency = `${currencyInfo.symbol}${currencyInfo.code}`;
              this.currencyCode = currencyInfo.code;
              this.currencySymbol = currencyInfo.symbol;
              this.currencyFeedback = currencyInfo.feedback
            }).catch();
      }
    },

    async getBusinessId() {
      this.businessId = await storage_util.getActingAs();
      if (this.businessId === null) {
        await this.$router.push('/home')
      }
    },

    /**
     * Get products for dropdown options
     */
    async getProducts() {
      if (this.businessId === '') {
        await this.getBusinessId()
      }
      if (this.businessId !== 'ACTING_AS_CURRENT_USER') {
        api.getBusinessCatalogue(this.businessId)
            .then((response) => {
              this.products = []
              let businessCatalogue = response.data
              for (let catalogue of businessCatalogue) {
                let value = catalogue.id
                let text = `${Validation.truncateString(catalogue.name)} - ${catalogue.id}`
                let price = catalogue.recommendedRetailPrice
                let item = {
                  value: value,
                  text: text,
                  price: price
                }
                this.products.push(item)
              }
            })
        .catch()
        }
    },


    /**
     * Validator triggered by submit button.
     * @param event
     */
    onSubmit(event) {
      this.addInventoryClicked = true;
      // When clicked checks if all fields are valid and submits data if true else does nothing and alerts the user
      if (this.canSubmit()) {
        event.preventDefault();
        let inventory = Object.assign({}, this.form)
          api.addInventoryItem(this.$route.params.businessId, inventory)
              .then((response) => {
                if (response.status === 201) {
                  this.$router.push({name:"ManageBusiness", hash:"#inventory"})
                }
              })
              .catch((err) => {
                if (err.response.status === 400 || err.response.status === 403) {
                  event.preventDefault();
                }
                event.preventDefault();
              })
      } else {
        event.preventDefault();
      }

    },

    canSubmit() {
      return this.isExpiryValid() &&
          this.isQuantityValid() &&
          this.isProductIdValid()
    },

    isQuantityValid() {
      if (!this.form.quantity) {
        if (!this.addInventoryClicked) {
          return null
        }
        this.error.quantity = "Cannot be empty."
        return false
      } else if (this.form.quantity < 1) {
        this.error.quantity = "Quantity must be at least 1."
        return false
      }
      return true
    },

    isPricePerItemValid() {
      if (this.form.pricePerItem === undefined || this.form.pricePerItem === null || this.form.pricePerItem === "") {
        return null;
      }

      if (this.form.pricePerItem < 0) {
        this.error.pricePerItem = "The price per item must be more than 0.";
        return false;
      }

      return true;
    },

    isTotalPriceValid() {
      if (this.form.totalPrice === undefined || this.form.totalPrice === null || this.form.totalPrice === "") {
        return null;
      }

      if (this.form.totalPrice < 0) {
        this.error.totalPrice = "The total price must be more than 0.";
        return false;
      }

      return true;
    },

    isManufacturedValid() {
      if (!this.form.manufactured) {
        return null
      }
      let manufactured = new Date(this.form.manufactured).getTime()
      let yesterday = new Date(this.now)
      yesterday.setDate(yesterday.getDate() - 1)
      if (manufactured > yesterday) {
        this.error.manufactured = "Manufactured date must not be in the future or today's date."
        return false
      }
      return true
    },

    isProductIdValid() {
      if (!this.form.productId) {
        if (!this.addInventoryClicked) {
          return null
        }
        this.error.productId = "Cannot be empty."
        return false
      }
      return (this.form.productId !== "")
    },

    isSellByValid() {
      if (!this.form.sellBy) {
        return null
      }
      return true
    },
    isBestBeforeValid() {
      if (!this.form.bestBefore) {
        return null
      }
      return true
    },
    isExpiryValid() {
      if (!this.form.expires) {
        if (!this.addInventoryClicked) {
          return null
        }
        this.error.expires = "Cannot be empty.";
        return false;
      }
      return true;
    },
    fillInputs() {
      for (let product of this.products) {
        if (product.value === this.form.productId) {
          this.form.pricePerItem = product.price
        }
      }
    },

    /**
     * Called when clicking cancel button on the add inventory item form, routes user to my inventory tab on the manage business page
     */
    goBackToInventory() {
      this.$router.push({name:"ManageBusiness", hash:"#inventory"});
    },
  },

  computed: {
    // The computed functions below update reactively to user input to check whether or not the input fields are correct
    productIdValidation() {
      return this.isProductIdValid()
    },

    quantityValidation() {
      return this.isQuantityValid()
    },

    pricePerItemValidation() {
      return this.isPricePerItemValid()
    },
    totalPriceValidation() {
      return this.isTotalPriceValid()
    },
    manufacturedValidation() {
      return this.isManufacturedValid()
    },
    sellByValidation() {
      return this.isSellByValid()
    },
    bestBeforeValidation() {
      return this.isBestBeforeValid()
    },
    expiryValidation() {
      return this.isExpiryValid()
    },
  }
}
</script>

<style scoped>

#add-inventory-pane {
  margin-top: 5vh;
  margin-bottom: 5vh;
}

</style>