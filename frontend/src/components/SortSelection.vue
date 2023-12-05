<template>
    <div class="row justify-content-start">
      <div class="col-md-auto">
        Sort By:
        <b-dropdown id="card-sorting-menu"
                    variant="primary"
                    :text="selected.label">
          <b-dropdown-item v-for="sortOption in sortOptions"
                           :key="sortOption.id"
                           :id="sortOption.label"
                           @click="sortChange(sortOption, sortOrder)">
            {{sortOption.label}}
          </b-dropdown-item>
        </b-dropdown>
      </div>

      <div class="col-md-auto">
        Order:
        <b-dropdown id="sort-order-menu"
                    :text="sortOrder.text">
          <b-dropdown-item v-for="order in sortOrders"
                           :key="order.text"
                           :id="order.value"
                           @click="sortChange(selected, order)">
            {{order.text}}
          </b-dropdown-item>
        </b-dropdown>
      </div>
    </div>
</template>

<script>
export default {
  name: "sortSelection.vue",
  mounted() {
    this.selected = this.sortOptions[0];
    this.sortOrder = this.sortOrders[0];
  },
  props: {
    sortOptions: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      selected: Object,
      sortOrder: Object,
      sortOrders: [
        {text: 'Ascending', value: "asc"},
        {text: 'Descending', value: "desc"}
      ]
    }
  },
  methods: {
    /**
     * Alerts the parent component that the sorting options have changed.
     * This is returned in the form:
     * {
     *   sort: "sortColumn",
     *   direction: "sortOrder"
     * }
     * @param sortSelection the selected attribute to sort by.
     * @param sortOrder the selected sorting order.
     */
    sortChange(sortSelection, sortOrder) {
      if (sortSelection !== undefined) {
        this.selected = sortSelection;
      }
      if (sortOrder !== undefined) {
        this.sortOrder = sortOrder;
      }
      this.$emit('sortChange', {sort: this.selected, direction: this.sortOrder});
    }
  }
}
</script>