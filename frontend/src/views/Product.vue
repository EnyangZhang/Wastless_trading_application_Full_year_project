<template>
  <div id="addProduct">
    <b-container fluid="true">
      <!-- Rows and columns centre the form in the middle of the page -->
      <b-row class="row justify-content-center">
        <b-col md="5" cols="auto" align-self="center">
          <b-card id="add-product-pane">
            <b-form id="form"
                    @submit="onSubmit"
                    v-if="show">
              <b-card-title class="text-center">{{cardTitle + ' Images'}}</b-card-title>

              <b-modal id="productImages"
                       scrollable
                       hide-footer>
                <image-view id="image-view"
                            :key="imageViewKey"
                            :images="productImages.images"
                            :primary-image="productImages.primaryId"
                            @removeImage="deleteImage"
                            @setAsPrimary="setPrimary"></image-view>
              </b-modal>

              <!--Clickable image component-->
              <clickable-image id="image-display"
                               :src="primaryImage"
                               v-b-modal.productImages/>

              <!-- Required input field for product image -->
              <div class="productImage">
                <b-form-file
                    id="fileSelect"
                    :state="imageValidation"
                    accept="image/*"
                    multiple
                    placeholder="Upload product images"
                    drop-placeholder="Drop images here..."
                    @change="handleImageChange"
                ></b-form-file>
                <b-form-valid-feedback :state="imageValidation"></b-form-valid-feedback>
                <b-form-invalid-feedback :state="!error.imageSizeExceeded">
                  Image Size Exceeds 500KB
                </b-form-invalid-feedback>
                <b-form-invalid-feedback :state="!error.maxImages">
                  Cannot Upload More Than 10 Images
                </b-form-invalid-feedback>
                <b-form-invalid-feedback :state="!error.invalidImage">
                  Invalid Image Format
                </b-form-invalid-feedback>
                <small style="margin-left: 10px;">
                  You can upload multiple images
                </small>
              </div>

              <hr>
              <b-card-title class="text-center">{{cardTitle + ' Details'}}</b-card-title>
              <br>

              <!-- Required input field for product name -->
              <div class="name">
                <b-form-input block
                              type="text"
                              name="name"
                              v-model="form.name"
                              placeholder="Name*"
                              :state="nameValidation"/>
                <b-form-invalid-feedback :state="nameValidation">
                  Product Name cannot be empty
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="nameValidation"></b-form-valid-feedback>
              </div>
              <br/>
              <!-- Required input field for product code -->
              <div class="productCode">
                <b-form-input block
                              type="text"
                              name="id"
                              v-model="form.id"
                              placeholder="Product Code*"
                              :state="codeValidation"/>
                <b-form-invalid-feedback :state="codeValidation">
                  {{codeFeedback}}
                </b-form-invalid-feedback>
                <b-form-valid-feedback :state="codeValidation"></b-form-valid-feedback>
              </div>
              <br/>
              <!-- Input field for description -->
              <div class="description">
                <b-form-textarea block
                                 type="text"
                                 name="description"
                                 :state="notTooLongValidation()"
                                 v-model="form.description"
                                 placeholder="Description"/>
                <b-form-invalid-feedback :state="notTooLongValidation()">
                  Please do not exceed 250 characters.
                </b-form-invalid-feedback>
              </div>
              <br/>
              <!-- Required input field for manufacturer -->
              <div class="manufacturer">
                <b-form-input block
                              type="text"
                              name="manufacturer"
                              v-model="form.manufacturer"
                              placeholder="Manufacturer"/>
              </div>
              <br/>
              <!-- Input field for recommendedRetailPrice -->
              <div class="recommendedRetailPrice">
                <b-input-group :prepend="`${currencySymbol}`" :append="`${currencyCode}`">
                  <b-form-input block
                                type="number"
                                :state="rrpValidation"
                                step="0.01"
                                min="0"
                                name="recommendedRetailPrice"
                                v-model="form.recommendedRetailPrice"
                                :placeholder="`Recommended Retail Price`"/>
                </b-input-group>
                <b-form-invalid-feedback :state="rrpValidation">
                  Recommended retail price must be at least zero.
                </b-form-invalid-feedback>

              </div>
              <br/>
              <div id="buttons" class="row">
                <div class="col">
                  <b-button block
                            id="cancelButton"
                            @click="goBackToCatalogue"
                            name="cancel">Cancel</b-button>
                </div>
                <div class="col">
                  <b-button block
                            id="submitButton"
                            type="submit"
                            name="submit"
                            variant="success">{{submitButton}}</b-button>
                </div>
              </div>
              <br/>
              <b-alert id="currency-feedback" :show="true">{{currencyFeedback}}</b-alert>
            </b-form>
          </b-card>
        </b-col>

      </b-row>

    </b-container>
  </div>
</template>

<script>

import api from "../Api.js"
import StorageUtil from "../javascript_modules/storage_util";
import {getCurrency} from "@/javascript_modules/currency_util";
import ClickableImage from "@/components/ClickableImage";
import ImageView from "@/components/ImageView";
import {getImageURL} from "../javascript_modules/image_util";

const Product = {

  name: "Product",
  components: {ImageView, ClickableImage},
  data: function () {

    return {
      form: {
        id: '',
        name: '',
        description: '',
        manufacturer: '',
        recommendedRetailPrice: ''
      },

      imageViewKey: 1,
      productImages: {
        images: [],
        primaryId: undefined,
        imageSizeExceeded: false,
        highestId: 0,
        toDelete: []
      },

      error: {
        imageSizeExceeded: false,
        maxImages: false,
        invalidImage: false
      },

      businessId: null,

      businessCountry: '',
      currencyFeedback: "Your business's home country is unknown. The default currency is USD",
      currencySymbol: undefined,
      currencyCode: undefined,

      codeFeedback: "Product Code already in use",

      cardTitle: "Add Product",
      submitButton: "Add Product",

      show : true,
      validCode: false,
      editingAProduct: false,
      addProductClicked: false,
    }
  },


  async mounted () {
    this.productImages.images = [];
    await this.getBusinessCountry();
    this.checkIfEditing();
    this.setHighestImageId();
  },

  methods: {

    /**
     * By changing the key, the image view componenet (where you can see all the images, and set to primary)
     * will be reloaded
     */
    reloadImageView() {
      this.imageViewKey++;
    },

    /**
     * Gets the business's country and sets the currency variables accordingly
     */
    async getBusinessCountry() {
      let businessId = StorageUtil.getActingAs();
      if (businessId !== 'ACTING_AS_CURRENT_USER') {
        api.getBusiness(businessId)
          .then(async (response) => {
            this.businessCountry = response.data.address.country;
            const currencyInfo = await getCurrency(this.businessCountry);
            this.currencySymbol = currencyInfo.symbol;
            this.currencyCode = currencyInfo.code;
            this.currencyFeedback = currencyInfo.feedback
          }).catch();
      }
    },

    /**
     * This method sets the highest ID variable, so that any new images added can be assigned an ID 1 higher
     * than all other IDs
     */
    setHighestImageId() {
      for (let i = 0; i < this.productImages.images.length; i++) {
        if (Number.parseInt(this.productImages.images[i].id) > this.productImages.highestId) {
          this.productImages.highestId = (this.productImages.images[i].id + 1).toString();
        }
      }
    },


    /**
     * Sets several variables depending on if we are creating a product or editing an existing product
     * (as there are some differences in the dialog). If we are editing it will load the existing product's
     * details into all of the fields
     */
    checkIfEditing() {
      let images = []
      this.editingAProduct = this.$route.path.includes('edit-product');
      this.businessId = this.$route.params.businessId;

      if (this.editingAProduct) {
        this.addProductClicked = true;
        this.form.id = this.$route.query.code;
        this.form.name = this.$route.query.name;
        this.form.manufacturer = this.$route.query.manufacturer;
        this.form.description = this.$route.query.description;
        this.form.recommendedRetailPrice = this.$route.query.rrp;
        if (typeof this.$route.query.images === 'string') {
          images.push(this.$route.query.images);
        } else {
          images = this.$route.query.images;
        }
        this.productImages.primaryId = this.$route.query.primaryImageId;

        this.cardTitle = "Edit " + this.$route.query.name + " Product";
        this.submitButton = "Save Product";

        for (let imageId of images) {
          const url = getImageURL(this.businessId, this.form.id, imageId, false);
          this.productImages.images.push({data: url, id: imageId, preExisting: true});
          if (Number.parseInt(imageId) > this.productImages.highestId) {
            this.productImages.highestId = Number.parseInt(imageId) + 1;
          }
        }

      } else {
        this.cardTitle = "Add Product";
        this.submitButton = "Add Product";
      }
    },

    /**
     * Method triggered by submit button. Checks if the inputs are valid and then sends create business request
     * @param event
     */
    onSubmit(event) {
      this.addProductClicked = true;

      // When clicked checks if all fields are valid and submits data if true else does nothing and alerts the user
      if (this.canSubmit()) {
        event.preventDefault();
        let product = Object.assign({}, this.form)
        if (!product.recommendedRetailPrice) {
          product.recommendedRetailPrice = 0;
        }
        if (this.editingAProduct) {
          api.modifyProductCatalogue(this.businessId, this.$route.params.productId, product)
              .then((response) => {
                if (response.status === 200) {
                  this.deleteImages()
                  this.editImages()
                } else if (response.status === 400) {
                  event.preventDefault();
                } else {
                  event.preventDefault();
                }
              })
              .catch(() => {
                event.preventDefault();
              });

        } else {
          api.addProduct(this.businessId, product)
              .then((response) => {
                if (response.status === 201) {
                  this.deleteImages()
                  this.addImages(response.data);
                } else if (response.status === 400) {
                  event.preventDefault();
                } else {
                  event.preventDefault();
                }
              })
              .catch(() => {
                event.preventDefault();
              });
        }
      } else {
        event.preventDefault();
      }
    },

    /**
     * If form is set to editing a product, uploads the images not already uploaded, sets the primary image.
     * For each additional image uploaded, checks if the image was a primary image (before setting its ID to the ID
     * returned by the server), and sets the primary image ID property to primary image.
     *
     * After iteration, sends call to api to set the new primary image (which could not have changed) and pushes to
     * product manager page.
     */
    async editImages() {
      if (this.editingAProduct) {
        const productId = this.$route.params.productId;
        for (let i = 0; i < this.productImages.images.length; i++) {

          if (!this.productImages.images[i].preExisting) {
            const formData = new FormData();
            formData.append("filename", this.productImages.images[i].file);
            const response = await api.uploadImage(this.businessId, productId, formData);
            this.productImages.primaryId = this.productImages.images[i].id === this.productImages.primaryId
                ? response.data
                : this.productImages.primaryId;
            this.productImages.images[i].preExisting = true;
            this.productImages.images[i].id = response.data;
          }
        }
        if (this.productImages.primaryId !== undefined) {
          api.setPrimary(this.businessId, productId, this.productImages.primaryId).catch(() => {})
        }
      }
      this.reloadImageView();
      await this.$router.push({name: "ManageBusiness", hash: "#catalogue"});
    },

    /**
     * If not editing a product, uploads products to the server one at a time. Sets the primary image to the one
     * that is selected.
     */
    addImages: async function (productId) {
      if (!this.editingAProduct) {
        for (let i = 0; i < this.productImages.images.length; i++) {
          const formData = new FormData();
          formData.append("filename", this.productImages.images[i].file);
          const response = await api.uploadImage(this.businessId, productId, formData);
          if (this.productImages.images[i].id === this.productImages.primaryId) {
            await api.setPrimary(this.businessId, productId, response.data)
          }
        }
      }
      this.reloadImageView();
      await this.$router.push({name:"ManageBusiness", hash:"#catalogue"});
    },

    /** Description should not be longer than 250 characters */
    notTooLongValidation() {
      if (this.form.description) {
        return this.form.description.length <= 250;
      }
    },

    /** The methods below check the validity of the input fields of the add product page */
    isNameValid() {
      return this.form.name.trim().length > 0
    },

    /** Returns true if at least one image has been uploaded */
    isImageValid() {
      return Boolean(this.productImages.primaryId)
    },

    /**
     * Checks if the current business has already used this code, returns true if we can use the code, or false if its
     * a duplicate
     * @returns {boolean} for if the code is valid
     */
    isCodeValid() {
      if (this.form.id.trim().length > 0) {

        // If we are editing a product, and we haven't changed the code then it should be considered valid
        if (this.editingAProduct && this.form.id === this.$route.params.productId) {
          this.validCode = true;
        // otherwise send request to server to see if product code exists
        } else {
          api.isCodeValid(this.businessId, this.form.id).then((response) => {
            if (response.status === 200) {
              this.validCode = true;
            }
          }).catch(() => {
            this.validCode = false;
            this.codeFeedback = "Product Code already in use"
          });
        }
      } else {
        this.validCode = false;
        this.codeFeedback = "You must enter a code";
      }
      return this.validCode;
    },

    canSubmit() {
      return this.isNameValid() && this.isCodeValid();
    },

    /**
     * Called when clicking cancel button on the add/edit product form, routes user to my catalogue tab on the manage business page
     */
    goBackToCatalogue() {
      this.$router.push({name:"ManageBusiness", hash:"#catalogue"});
    },

    /**
     * Function callback that triggers upon change in file select content.
     * Performs an event type check to determine the path to find files.
     *
     * If files are found, reads each of them as data url and pushes into images form field.
     * If no primary image has been set, sets the primary image.
     * @param event
     */
    handleImageChange(event) {
      let images;

      if (event.type === "drop") {
        images = event.dataTransfer.files;
      } else {
        images = event.target.files;
      }

      this.productImages.imageSizeExceeded = false;
      this.error.imageSizeExceeded = false;
      this.error.maximages = false;
      this.error.invalidImage = false;

      if (images) {
        images.forEach((image) => {
          if (this.imageValid(image, images.length)) {
            const fileReader = new FileReader();
            fileReader.readAsDataURL(image);
            fileReader.onloadend = ((e) => {
              const newImage = {id: (++this.productImages.highestId).toString(), data: e.target.result, file: image};
              this.productImages.images.push(newImage);
              if (this.productImages.images.length === 1) {
                this.productImages.primaryId = newImage.id;
              }
            });
          }
        });
      }
      // Sets value of file select element to null, so files will always be considered new
      document.getElementById("fileSelect").value = null;
    },

    /**
     * Function to validate each image being uploaded, given the image and the total size of the array that the image
     * came in.
     * If image exceeds 500KB in size, or there are already 10 images,
     * or the total number will be more than 10
     * given the total size of the array the image came in i.e. the images to be uploaded,
     * or if the image type is not 'image/**', then returns false.
     * Otherwise return true.
     **/
    imageValid(image, inputSize) {
      let valid = true
      const size = image.size / (10**6);
      if (size >= 0.5) {
        this.productImages.imageSizeExceeded = true;
        this.error.imageSizeExceeded = true;
        valid = false
      }
      if (this.productImages.images.length >= 10 || this.productImages.images.length + inputSize > 10) {
        this.error.maxImages = true;
        valid = false
      }
      if (!image.type.includes("image/")) {
        this.error.invalidImage = true;
        valid = false
      }
      return valid
    },

    /**
     * Function callback when event is emitted to set a given image as the primary image.
     * If the given image matches an image in the form, sets the given image as new the new primary.
     * @param imageId
     */
    setPrimary(imageId) {
      for (let i = 0; i < this.productImages.images.length; i++) {
        if (this.productImages.images[i].id === imageId) {
          this.productImages.primaryId = imageId;
        }
      }
    },

    /**
     * Appends image id to the deletion queue. These images are deleted when the save button is clicked.
     * Only appends if selected image is not a 'pre-existing' image.
     *
     * If the images list is not empty and the delete image was the primary image, then sets a new default primary image
     * If the image list is empty after deletion, sets primary image property to undefined. This is checked to avoid
     * setting a primary image that does not exist.
     */
    deleteImage(imageId) {
      for (let i = 0; i < this.productImages.images.length; i++) {
        const image = this.productImages.images[i];
        if (image.id === imageId) {
          if (image.preExisting) this.productImages.toDelete.push(imageId);
          this.productImages.images.splice(i, 1);
          this.error.maxImages = false;
        }
      }

      if (this.productImages.images.length) {
        if (this.productImages.primaryId === imageId) this.productImages.primaryId = this.productImages.images[0].id;
      } else this.productImages.primaryId = undefined;
    },

    /**
     * Deletes the image by id.
     * If by route, the form is editing an image, sends a call to API to delete image and updates list if successful.
     * Otherwise removes the image by id from the list of product images.
     */
    deleteImages() {
      const businessId = this.businessId;
      const productId = this.$route.query.code;
      if (this.editingAProduct) {
        this.productImages.toDelete.forEach(async (imageId) => {
          await api.deleteImage(businessId, productId, imageId)
        });
      }
      this.reloadImageView();
    }
  },

  computed: {
    // The computed functions below update reactively to user input to check whether or not the input fields are correct
    nameValidation() {
      if (this.form.name.length === 0 && !this.addProductClicked) {
        return null;
      }
      return this.isNameValid();
    },

    // Returns null if no image uploaded, or true if at least one image is uploaded
    imageValidation() {
      if (!this.productImages.primaryId) {
        return null;
      }
      return this.isImageValid();
    },

    codeValidation() {
      if (this.form.id.length === 0 && !this.addProductClicked) {
        return null;
      }
      return this.isCodeValid();
    },

    rrpValidation() {
      if (!this.form.recommendedRetailPrice.length) {
        return null;
      }
      return this.form.recommendedRetailPrice >= 0
    },

    primaryImage() {
      if (this.productImages.images && (this.productImages.primaryId !== undefined)) {
        for (let i = 0; i < this.productImages.images.length; i++) {
          if (this.productImages.images[i].id === this.productImages.primaryId) {
            return this.productImages.images[i].data;
          }
        }
      }
      return undefined;
    }
  }
}

export default Product;

</script>

<style scoped>

#add-product-pane {
  margin-top: 5vh;
  margin-bottom: 5vh;
}

#currency-feedback {
  color: #4d4d4d;
}

</style>

