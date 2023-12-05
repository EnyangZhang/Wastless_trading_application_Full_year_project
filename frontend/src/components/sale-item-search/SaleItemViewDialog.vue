<template>
  <div id="sale-view-dialog">
    <b-modal id="view-dialog-modal"
             ref="modal"
             @hide="handleModalClosure"
             @hidden="confirmDialogCanceled"
             hide-footer
             size="md">
      <b-overlay :show="display" rounded="lg" opacity="0.8" style="margin: -1rem">
        <div style="padding: 1rem;">
          <b-carousel
              id="carousel-1"
              :interval="4000"
              controls
              indicators
              :background="'#ababab'"
              style="text-shadow: 1px 1px 2px #333; margin-right: -1rem; margin-left: -1rem; margin-top: -1rem">
            <b-carousel-slide v-for="(image, idx) in images" v-bind:key="idx">
              <template #img>
                <img class="d-block img-fluid w-100" :src="image" alt="Product Image"
                     style="max-height: 400px; min-height: 400px; object-fit: contain">
              </template>
            </b-carousel-slide>
          </b-carousel>

          <h1>{{ productName }}</h1>
          <hr/>
          <div class="row">
            <div class="col my-auto align-self-start">
              <div class="row-cols">
                <h4>{{currency.symbol}}{{price}} {{currency.code}}</h4>
              </div>
              <div class="row-cols">
                <h6>Lot Quantity: {{quantity}}</h6>
              </div>
              <div class="row-cols">
                (Price Per Item: {{currency.symbol}}{{pricePerItem}} {{currency.code}})
              </div>
            </div>
            <div class="col-auto align-self-end my-auto">
              <div class="row-cols">
                <b-button v-if="actingAsCurrentUser && !isSold" variant="primary" size="lg" @click="purchaseConfirm">Purchase</b-button>
                <b-button v-else-if="isSold" variant="primary" size="lg" disabled>Already Purchased</b-button>
                <b-button v-else class="col" variant="primary" size="lg" disabled>Cannot Purchase as Business</b-button>
              </div>
              <div class="row-cols">
                <div v-if="!likedButtonLoading">
                  <b-button variant="primary"
                            size="lg"
                            class="like-btn"
                            ref="like-btn"
                            v-b-tooltip.hover.left
                            :title="liked ? 'Unlike Listing' : 'Like Listing'"
                            :disabled="!actingAsCurrentUser || isSold"
                            :pressed="liked"
                            @click="handleLikeClicked"
                            block>
                      {{this.likedByUsers.length}}
                    <b-icon-bookmark-heart-fill v-if="liked"/>
                    <b-icon-bookmark-heart v-else/>
                  </b-button>
                </div>
                <div v-else>
                  <b-button variant="primary"
                            size="lg"
                            class="like-btn"
                            ref="like-btn"
                            disabled
                            block>
                    <b-spinner small></b-spinner>
                  </b-button>
                </div>
              </div>
            </div>
          </div>
          <div id="productDetailsSection">
            <hr/>
            <h5>Manufacturer:</h5>
            <p>{{productManufacturer}}</p>
            <h5>Description:</h5>
            <p>{{productDescription}}</p>
          </div>
          <div>
            <hr>
            <h5>More Info:</h5>
            {{moreInfo}}
          </div>
          <hr/>


          <h5>Seller:</h5>
          <b-button @click="navigateToBusiness">
             {{sellerName}}
          </b-button>
          <br>
          <h5>Seller address:</h5>
          {{address}}
          <hr/>
          <h6>Date Created: {{createdDate}}</h6>
          <h6>Closing Date: {{closingDate}}</h6>
        </div>
        <template #overlay>
          <div class="text-center">
            <p><strong id="delete-confirmation-label">Purchase this listing?</strong></p>
          </div>
          <div class="d-flex">
            <b-button
                class="mr-3"
                variant="secondary"
                @click="confirmDialogCanceled">Cancel
            </b-button>
            <b-button
                variant="success"
                @click="handlePurchaseClicked">Purchase
            </b-button>
          </div>
        </template>
      </b-overlay>
    </b-modal>
  </div>
</template>

<script>
import {getImageURL} from "@/javascript_modules/image_util";
import {formatDateString} from "@/javascript_modules/date_util";
import Api from "@/Api";
import {formatAddress} from "@/javascript_modules/address_util";
import storage_util from "@/javascript_modules/storage_util";
import {getCurrency} from "@/javascript_modules/currency_util";

export default {
  name: "SaleItemViewDialog",
  mounted() {
    window.addEventListener(storage_util.SWITCHED_ACCOUNT_EVENT, this.handleUserChange);
    this.handleUserChange();
  },
  props: {
    saleListing: Object
  },
  watch: {
    saleListing() {
      this.setData();
    }
  },
  data() {
    return {
      // Set b-overlay to show if display is true
      display: false,
      actingAsCurrentUser: true,
      productName: "",
      productDescription: "",
      productManufacturer: "",
      currency: {},
      price: "",
      likedByUsers: [],
      liked: false,
      moreInfo: "",
      closingDate: "",
      createdDate: "",
      sellerName: "",
      quantity: "",
      pricePerItem: "",
      address: "",
      isSold: false,
      images: [],
      businessId: undefined,
      likedButtonLoading: true
    }
  },
  methods: {
    /**
     * Set confirmation dialog display to false.
     */
    confirmDialogCanceled() {
      this.display = false;
    },

    /**
     * Sets the modal to show.
     */
    show() {
      this.likedButtonLoading = true;
      this.$refs.modal.show();
    },

    /**
     * Sets the data to show in the dialog to the prop data
     * for the sale item.
     */
    async setData() {
      const saleItem = this.saleListing.saleItem;
      this.productName = saleItem.inventoryItem.product.name;
      this.productDescription = saleItem.inventoryItem.product.description;
      this.productManufacturer = saleItem.inventoryItem.product.manufacturer;
      this.price = saleItem.price.toFixed(2);
      this.pricePerItem = (saleItem.price / saleItem.quantity).toFixed(2)
      this.closingDate = formatDateString(saleItem.closes);
      this.createdDate = formatDateString(saleItem.created);
      this.moreInfo = saleItem.moreInfo;
      this.quantity = saleItem.quantity;
      this.isSold = saleItem.sold;

      try {
        const result = await Api.getSaleListing(this.saleListing.saleItem.id);
        this.likedByUsers = result.data.likedByUsers;
        this.likedButtonLoading = false;
      } catch {
        this.likedByUsers = saleItem.likedByUsers;
        this.likedButtonLoading = false;
      }

      this.liked = false;
      for (const user of this.likedByUsers) {
        if (user.id === storage_util.getCurrentUser()) {
          this.liked = true;
          break;
        }
      }

      this.businessId = saleItem.businessId;
      this.getBusiness(this.businessId);

      this.currency = await getCurrency(saleItem.country);

      this.images = [];
      for (let imageIndex in saleItem.inventoryItem.product.images) {
        this.images.push(getImageURL(
            saleItem.businessId,
            saleItem.inventoryItem.product.id,
            saleItem.inventoryItem.product.images[imageIndex].productImageId,
            false
        ));
      }
      if (this.images.length === 0) {
        this.images.push(require('../../../public/default.png'));
      }
    },

    /**
     * Gets business and sets the business name.
     * @param businessId The id of the business to retrieve.
     */
    getBusiness(businessId) {
      Api.getBusiness(businessId).then((response) => {
        if (response.status === 200) {
          this.sellerName = response.data.name;
          this.address = formatAddress(response.data.address, true);
        }
      }).catch(() => {
        this.sellerName = "Could not retrieve seller name";
      });
    },

    /**
     * Navigates to the associated business.
     */
    navigateToBusiness() {
      const id = this.businessId;
      this.$router.push({name: 'BusinessProfile', params: {id: id}});
    },

    /**
     * Set confirmation dialog to true.
     */
    purchaseConfirm() {
      this.display = true;
    },

    /**
     * Emits on event bus that the purchase button has been clicked
     * and for which sale item.
     */
    handlePurchaseClicked() {
      this.$emit("purchaseClicked", this.saleListing.saleItem);
      this.display = false;
      this.$refs.modal.hide();
    },

    /**
     * To be implemented under another task, I made this so I could test the color change when you click on the button
     */
    handleLikeClicked() {
      switch (this.liked) {
        case false:
          Api.likeListing(this.businessId, this.saleListing.saleItem.id).then((response) => {
            if (response.status === 200) {
              this.liked = true;
              this.$emit("likeClicked");
            }
          });
          break;
        case true:
          Api.unlikeListing(this.businessId, this.saleListing.saleItem.id).then((response) => {
            if (response.status === 200) {
              this.liked = false;
              this.$emit("likeClicked");
            }
          });
          break;
      }
    },

    /**
     * Handles changing user acting as so that the purchase button can be greyed out when
     * acting as a business.
     */
    handleUserChange() {
      this.actingAsCurrentUser = storage_util.getActingAs() === storage_util.ACTING_AS_CURRENT_USER;
    },
    /**
    * Handles when the sale item view dialogue is closed, resets the url to "/home" instead of "/home/{id}"
    */
    handleModalClosure() {
      this.$router.go(-1);
    }
  }
}
</script>

<style scoped>
hr {
  margin: 1rem -1rem;
  border: 0;
  border-top: 2px solid rgba(0, 0, 0, 0.1);
}

.like-btn {
  margin-top: 5px;
}
</style>