<!-- This component handles an input box, and will show a list of suggestions
based on what the user has inputted. It uses a prop suggestions which is provided
by the parent class to allow for custom suggestions -->

<template>
  <div class="bue-input">
    <b-form-input
        type="text"
        id="autocomplete"
        v-model="textInput"
        placeholder="Address autocomplete"
        autocomplete="nope"
        @click="setInvisible"
        @blur=setInvisible
        @focus=setVisible
    ></b-form-input>

    <div class="suggestions"
         @mouseover="overSuggestionBox = true"
         @mouseleave="overSuggestionBox = false"
         v-if="visible && suggestions"> 
      <b-list-group block>
        <b-list-group-item
            variant="light"
            v-for="(suggestion, i) in suggestions"
            :key="i"
            @click="setSuggestion(suggestion)">
          <template v-if="suggestion.streetNumber !== ''">{{suggestion.streetNumber}}, </template>
          <template v-if="suggestion.streetName !== ''">{{suggestion.streetName}}, </template>
          <template v-if="suggestion.suburb !== ''">{{suggestion.suburb}}, </template>
          <template v-if="suggestion.city !== ''">{{suggestion.city}}, </template>
          <template v-if="suggestion.region !== ''">{{suggestion.region}}, </template>
          <template v-if="suggestion.country !== ''">{{suggestion.country}}, </template>
          <template v-if="suggestion.postcode !== ''">{{suggestion.postcode}}</template>
        </b-list-group-item>
      </b-list-group>
    </div>
  </div>
</template>

<script>

const INPUT_LENGTH = 3;

export default {
  name: 'AutoCompleteInput',
  props: {
    suggestions: Array,
    selection: {
      type: String,
      twoWay: true
    },
  },

  data: function () {
    return {
      textInput: '',
      visible: false,
      overSuggestionBox: false,
      hovered: false,
    }
  },

  watch: {
    /*
    Watches the text input, if length is above constant
    INPUTLENGTH, sends a request to fill the list of suggestions.
    Else hides and clears suggestions
    */
    textInput() {
      if (this.textInput.length >= INPUT_LENGTH) {
        // Get list of suggestions from parent class
        this.$emit('getSuggestions', this.textInput)
        this.visible = true
      } else {
        // If there is less than 3 characters, clear suggestions
        this.$emit('clearSuggestions')
        this.visible = false
      }
    },


  },

  methods : {
    /*
      Change value of input to be selected suggestion,
      then hide and clear the suggestions
       */
    setSuggestion(suggestion) {
      this.$emit('setSuggestion', suggestion)
      this.textInput = ''
      this.visible = false
      this.$emit('clearSuggestions')
    },

    /** Set the address suggestion box to invisible, unless the mouse is hovering over the box */
    setInvisible () {
      if (!this.overSuggestionBox) {
        this.$emit('clearSuggestions')
        this.visible = false;
      }
    },

    /** Set the address suggestion box to visible */
    setVisible () {
      this.visible = true;
    },

    fetchData () {
      return this.textInput
    }
  }
}
</script>

<style scoped>

.bue-input {
  margin: 0px;
}

.suggestions {
  position: absolute;
  z-index: 9999;
}

</style>