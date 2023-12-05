<template>
  <div id="clickable-image"
       @mouseover="handleImageHover"
       @mouseleave="handleImageHover">
    <b-overlay id="image-container"
               :show="isHovered">
      <template #overlay>
        <b-icon id="overlay-icon"
                v-if="!inEditMode"
                @click="handleClicked"
                name="b-icon"
                icon="camera"
                font-scale="2"/>

        <b-button variant="outline-secondary"
                  v-if="inEditMode"
                  id="primary-button"
                  @click="handleClicked"
                  :disabled="isPrimary"
                  style="width: 120%; text-align: left"
                  block>
          <b-icon name="b-icon"
                  :icon="overlayIcon"
                  font-scale="2"/> {{overlayMessage}}
        </b-button>
        <b-button variant="outline-secondary"
                  v-if="inEditMode"
                  id="remove-button"
                  @click="handleRemove"
                  style="width: 120% !important; text-align: left"
                  block>
          <b-icon id="remove-icon"
                  name="b-icon"
                  :icon="'x-circle'"
                  font-scale="2"/> Remove
        </b-button>
      </template>
      <b-img-lazy id="image"
             thumbnail
             center
             fluid
             :src="this.src || require('../../public/default.png')"
             alt="product image">
      </b-img-lazy>
    </b-overlay>
  </div>
</template>

<script>
export default {
  name: "ClickableImage",
  props: {
    src: String,
    overlayIcon: String,
    overlayMessage: String,
    inEditMode: Boolean,
    isPrimary: Boolean,
    readOnly: Boolean
  },
  emits: ["imageClicked"],
  data() {
    return {
      isHovered: false
    }
  },
  methods: {
    handleClicked() {
      if (!this.readOnly) {
        this.$emit("imageClicked", this.src);
      }
    },
    handleImageHover(event) {
      if (!this.readOnly) {
        event.type === "mouseover" ? this.isHovered = true : this.isHovered = false;
      } else {
        this.isHovered = false;
      }
    },
    handleRemove() {
      this.$emit("imageRemoved", this.src);
    }

  },
  computed: {
  }
}
</script>

<style scoped>

#image-container {
  width: 300px;
  margin: auto;
  text-align: center;
}

#image {
  text-align: center;
  width: 100%;
  height: 100%;
  margin: 25px;
  padding: 10px;
}

</style>