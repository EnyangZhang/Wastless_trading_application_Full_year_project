<template>
  <div>
    <SaleItemDataView/>
  </div>
</template>

<script>
import SaleItemDataView from "@/components/sale-item-search/SaleItemDataView";
export default {
  name: "SearchSaleListings",
  props: {
    saleItem: Object,
    isList: Boolean,
    isModal: Boolean
  },
  components: {SaleItemDataView}
}
</script>