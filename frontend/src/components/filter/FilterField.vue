<template>
  <div>
    <div class="row justify-content-start" v-if="type === 'date'">
      <label class="col-md-2 my-auto">{{name}}</label>
      <div class="col-lg">
        <b-input-group>
          <b-datepicker v-model="minimum" :max="maximum" reset-button/>
          <template #append>
            <b-btn @click="minimum = ''">Clear</b-btn>
          </template>
        </b-input-group>

      </div>
      <label class="col-sm-1 my-auto">to</label>
      <div class="col-lg">
        <b-input-group>
          <b-datepicker v-model="maximum" :min="minimum"/>
          <template #append>
            <b-btn @click="maximum = ''">Clear</b-btn>
          </template>
        </b-input-group>
      </div>
    </div>

    <div class="row justify-content-start" v-else-if="type === 'price'">
      <label class="col-md-2 my-auto">{{name}}</label>
      <div class="col-lg">
        <b-form-input v-model="minimum" :state="rangeState" type="number" step="5"/>
      </div>
      <label class="col-sm-1 my-auto">to</label>
      <div class="col-lg">
        <b-form-input v-model="maximum" :state="rangeState" type="number" step="5"/>
      </div>
    </div>

    <div class="row justify-content-start" v-else-if="type === 'dropdown'">
      <label class="col-md-2 my-auto">{{name}}</label>
      <div class="col-lg">
        <b-form-select v-model="selectedOption" :options="options"/>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "FilterField",
  props: {
    filterOption: Object
  },
  mounted() {
    this.name = this.filterOption.name;
    this.type = this.filterOption.type;
    this.options = this.filterOption.options;
  },
  watch: {
    /**
     * Checks if a selected option has changed and then triggers
     * function to update parent.
     */
    selectedOption() {
      this.makeSelection()
    },

    /**
     * If the mode is "price" this function checks if the input maximum value is
     * greater than the minimum. It will then set the range state to:
     * null if VALID
     * and
     * false if INVALID
     *
     * This will always be valid if the opposite field is empty.
     */
    maximum() {
      if (this.type === "price" && this.minimum.length > 0 && parseFloat(this.maximum) <= parseFloat(this.minimum)) {
        this.rangeState = false;
      } else {
        this.rangeState = null;
        this.makeSelection();
      }
    },

    /**
     * If the mode is "price" this function checks if the input minimum value is
     * less than the maximum. It will then set the range state to:
     * null if VALID
     * and
     * false if INVALID
     * This will always be valid if the opposite field is empty.
     */
    minimum() {
      if (this.type === "price" && this.maximum.length > 0 && parseFloat(this.maximum) <= parseFloat(this.minimum)) {
        this.rangeState = false;
      } else {
        this.rangeState = null;
        this.makeSelection()
      }
    }
  },
  data() {
    return {
      name: null,
      type: null,
      options: [],
      selectedOption: null,
      maximum: "",
      minimum: "",

      rangeState: null,
    }
  },
  methods: {
    /**
     * Creates an object to return filter data.
     *
     * If the mode is dropdown then it returns:
     *     {name, value}
     * Where value is a string.
     *
     * If the mode is date or price then it returns:
     *     {name, {min, max}}
     * Where "min" is the minimum value and "max" is the maximum value.
     *
     * NOTE: The name value that is returned is always the name of the
     *       field in lower-case with all spaces removed.
     */
    makeSelection() {
      const formattedName = this.name.replace(/\s/g, '').toLowerCase();
      let output = {name: formattedName, value: null}

      if (["dropdown"].includes(this.type)) {
        output.value = this.selectedOption;
      }
      else if (["date", "price"].includes(this.type) && this.rangeState == null) {
        // RangeState is null when the min and max are valid
        let value = {max: null, min: null};

        if (this.maximum !== undefined) {
          value.max = this.maximum;
        }
        if (this.minimum !== undefined) {
          value.min = this.minimum;
        }
        output.value = value;
      }
      this.$emit('filterUpdate', output);
    }
  }
}
</script>