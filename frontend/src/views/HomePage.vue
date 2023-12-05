<template>
  <div id="home">
    <SearchSaleListings/>
  </div>
</template>

<script>
import SearchSaleListings from "@/views/SearchSaleListings";
export default {
  name: "HomePage",
  components: {
    SearchSaleListings
  }
}
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

#page {
  float: bottom;
  font-size: 15px;
  padding-top: 30px;
  padding-left: 30px;
}

#paginationBtn {
  float: right;
  padding: 20px;
}

</style>