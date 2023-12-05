<template>
  <div id="formatted-address">
    <address>{{formattedAddress}}</address>
  </div>
</template>

<script>
import {formatAddress} from "@/javascript_modules/address_util";

export default {
  name: "FormattedAddress",
  props: {
    address: Object,
    fullAddress: Boolean
  },
  computed: {
    /**
     * Calls formatAddress function in address_util given the details contained in the address prop.
     * @returns {string} Formatted address
     */
    formattedAddress() {
      return formatAddress(this.address, this.fullAddress);
    }
  }
}
</script>