<template>
  <div id="image-view">

    <!--Backup default display-->
    <div id="default"
         v-if="errorFlag">
      <img id="default-image"
           src="../../public/default.png"
           alt="defaultImage"/>
    </div>

    <!--Main Display-->
    <div id="image-display"
         v-if="!errorFlag">

      <clickable-image v-for="(image, i) in images"
                       v-bind:key="i"
                       :src="image.data"
                       :overlay-icon="image.id === primaryImage ? 'star-fill' : 'star'"
                       :overlay-message="image.id === primaryImage ? 'Primary Image': 'Set as Primary'"
                       :in-edit-mode="true"
                       :is-primary="image.id === primaryImage"
                       :read-only="readOnly"
                       @imageClicked="handleImageClicked(image.id)"
                       @imageRemoved="handleRemoveImage(image.id)"></clickable-image>
    </div>

    <!--Error Message-->
    <div id="error-message"
         v-if="errorFlag">
      <b-alert id="alert" :show="errorFlag">{{errorMessage}}</b-alert>
    </div>
  </div>
</template>

<script>
import ClickableImage from "@/components/ClickableImage";
export default {
  name: "ImageView",
  components: {ClickableImage},
  props: {
    images: Array,
    readOnly: Boolean,
    primaryImage: [String, Number]
  },
  emits: ["setAsPrimary"],
  data() {
    return {
      slide: 0
    }
  },
  methods: {
    handleImageClicked(id) {
      this.$emit("setAsPrimary", id);
    },
    handleRemoveImage(id) {
      this.$emit("removeImage", id);
    }
  },
  computed: {
    errorFlag() {
      const undefinedProp = !this.images;
      const noImages = this.images.length === 0;
      return undefinedProp || noImages;
    },
    errorMessage() {
      if (this.images.length === 0) {
        return "No images to display";
      }
      return "Can't display images (unknown error)";
    }
  }
}
</script>

<style scoped>

#default {
  text-align: center;
}

#error-message {
  padding: 20px;
}

</style>