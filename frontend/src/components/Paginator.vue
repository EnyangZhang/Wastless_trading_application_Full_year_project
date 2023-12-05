<template>
  <div id="pagination-component">
    <div id="page">Page: {{ displayPage }} / {{ calculateTotalPage }}</div>
    <div class="btn-group" role="group" id="paginationBtn">
      <b-button id="prev"
                type="button"
                class="btn btn-success"
                @click=previousPage
                :disabled="disablePrev || buttonDisabled">
        Previous
      </b-button>
      <b-button id="next"
                type="button"
                class="btn btn-success"
                @click=nextPage
                :disabled="disableNext || buttonDisabled">
        Next
      </b-button>
    </div>
  </div>
</template>

<script>
export default {
  name: "Paginator",
  props: {
    itemsLength: Number,
    page: Number,
    perPage: Number,
    buttonDisabled: Boolean
  },
  emits: ["changePage"],
  data() {
    return {
      currentPage: this.page
    }
  },
  watch: {
    page() {
      this.currentPage = this.page;
    }
  },
  methods: {
    /**
     * Called when the 'next' button is pressed. Increment the current page by 1 and emits the 'changePage' event
     * to update parent component.
     */
    nextPage() {
      this.currentPage += 1;
      this.$emit("changePage", this.currentPage);
    },
    /**
     * Called when the 'previous' button is pressed. Decrements the current page by 1 and emits the 'changePage' event
     * to update parent component.
     */
    previousPage() {
      this.currentPage -= 1;
      this.$emit("changePage", this.currentPage);
    },
  },
  computed: {
    /**
     * If the current page is 1 (or less) return true.
     * */
    disablePrev() {
      return this.currentPage <= 1;
    },
    /**
     * If the current page is the final page based on the number of items per page and the total number of items in
     * itemsLength prop, return true.
     */
    disableNext() {
      return (this.currentPage) * this.perPage >= this.itemsLength;
    },

    /**
     * Calculates display value of current page
     */
    displayPage() {
      return this.currentPage;
    },

    /**
     * Calculate total page regards to number of items to show.
     * 1 is added to the total as the back-end starting page is 0.
     */
    calculateTotalPage() {
      const total = Math.ceil(this.itemsLength / this.perPage);
      if (total === 0) {
        return 1;
      }
      return total;
    },
  }
}
</script>

<style scoped>

#page {
  float: right;
  font-size: 15px;
  padding-left: 5px;
  padding-right: 5px;
  padding-top: 6px;
}

#paginationBtn {
  float: right;
}

</style>