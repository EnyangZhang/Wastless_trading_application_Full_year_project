<template>
    <div class="col justify-content-start">
      <b-button class="col-lg-auto" v-b-toggle.collapse-1 variant="primary">Filters</b-button>
      <b-collapse id="collapse-1" class="card col-md-auto" style="margin-top: 5px; padding: 5px">
        <FilterField
            v-for="filter in filters"
            v-bind:key="filter.name"
            :filter-option="filter"
            @filterUpdate="filterUpdate($event)"
        />
      </b-collapse>
    </div>
</template>

<script>
import FilterField from "@/components/filter/FilterField";

export default {
  name: "FilterSelector",
  components: {FilterField},
  props: {
    filters: Array
  },
  mounted() {
    // Sets all the filters to their empty values
    for (let filter in this.filters) {
      filter = this.filters[filter];
      let filterName = filter.name.replace(/\s/g, '').toLowerCase();

      if (["dropdown"].includes(filter.type)) {
        this.filterOptions[filterName] = null;
      } else if (["date", "price"].includes(filter.type)) {
        this.filterOptions[filterName] = {max: null, min: null};
      }
    }
    this.$emit("filterFieldInitialise", this.filterOptions);
  },
  data() {
    return {
      filterOptions: {}
    }
  },
  methods: {
    /**
     * This is triggered when the filters have been updated and emits the
     * current filter options.
     * This will return data in the form:
     *
     * {
     *   nameoffilter: "filterValueString" OR {max: maxValue, min: minValue},
     *   ...
     * }
     *
     * NOTE: nameoffilter is a string created from the field name with all of the
     *       whitespace removed and converted to lower-case.
     *
     * @param data The filter option that has been returned from a FilterField.
     */
    filterUpdate(data) {
      this.filterOptions[data.name] = data.value;
      this.$emit("filterFieldChange", this.filterOptions);
    }
  }
}
</script>