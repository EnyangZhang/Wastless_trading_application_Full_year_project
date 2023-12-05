<template>
  <div id="product-notification-info">
    <div id="price-info">
      <strong>Payment: </strong>
      <em>{{currencySymbol}}{{price}} {{currencyCode}}</em>
    </div>
    <div id="location-info">
      <strong>You can pick up your product at: </strong>
      <formatted-address v-if="sellerAddress" full-address :address="sellerAddress"/>
      <div v-else><em>No Address Available</em></div>
    </div>
  </div>
</template>

<script>
import Api from "../../Api";
import {getCurrency} from "../../javascript_modules/currency_util";
import FormattedAddress from "../address/FormattedAddress";

export default {
  name: "ProductNotificationInfo",
  components: {FormattedAddress},
  props: {
    saleItemId: Number
  },
  data() {
    return {
      saleItem: undefined,
      price: undefined,
      currencyCode: undefined,
      currencySymbol: undefined,
      sellerAddress: undefined
    }
  },
  async mounted() {
    await this.getSaleItem();
    await this.getCurrencyInfo();
    await this.getSellerAddress();
  },
  methods: {
    /**
     * Calls the API to retrieve the sale item from the server by the component's prop saleItemId.
     * Sets the saleItem data attribute.
     **/
    async getSaleItem() {
      try {
        const response = await Api.getSaleListing(this.saleItemId)
        this.saleItem = response.data
        this.price = response.data.price;
      } catch (error) {
        // Do nothing
      }
    },

    /**
     * Called once saleItem is retrieved from the server. Retrieves the country form the sale item's business and sends
     * it to currency_util to obtain the currency symbol and code for the product price.
     */
    async getCurrencyInfo() {
      const businessCountry = this.saleItem.country
      const currencyInfo = await getCurrency(businessCountry);
      this.currencyCode = currencyInfo.code;
      this.currencySymbol = currencyInfo.symbol;
    },

    /**
     * Sends request for seller address.
     */
    async getSellerAddress() {
      try {
        const response = await Api.getBusiness(this.saleItem.businessId);
        this.sellerAddress = response.data.address;
      } catch (error) {
        // Do nothing
      }
    }
  }
}
</script>

<style scoped>

</style>