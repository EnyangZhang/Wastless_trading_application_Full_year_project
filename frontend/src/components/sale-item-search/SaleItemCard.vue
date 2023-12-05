<template>
  <div id="sale-item-object">

    <b-overlay id="confirm-purchase-overlay" :show="display" rounded="lg" opacity="0.6" >

      <div v-if="isGrid" v-b-hover="handleHover" :class="cardHovered ? 'card shadow' : 'card' " style="width: 20rem; max-height: 36rem; margin: 1rem">
        <button class="astext" @click="handleCardClicked">
          <router-link :to="getModalLink" style="text-decoration: none; color: inherit;">


            <img class="card-img-top" :src="imageUrl || require('../../../public/default.png')" alt="Image Unavailable"
                 width="100%"/>
          </router-link>
        </button>

        <button class="astext" @click="handleCardClicked">
          <router-link :to="getModalLink" style="text-decoration: none; color: inherit;">
            <h4 class="card-title text-truncate" style="padding-top: 1rem" :style="underlineTitle">{{saleListing.inventoryItem.product.name}}</h4>
            <p class="card-text text-truncate" style="max-height: 100%">{{saleListing.inventoryItem.product.description}}</p>
          </router-link>
        </button>

        <ul class="list-group list-group-flush">
          <li class="list-group-item">Amount: {{saleListing.quantity}}</li>
        </ul>

        <div id="price-purchase" class="card-body row">
          <p class="col card-text d-inline my-auto">{{currency.symbol}}{{saleListing.price.toFixed(2)}} {{currency.code}}</p>
          <Button id="purchase-Button" v-if="actingAsCurrentUser" class="col" style="width:100%; text-align: center" @click="handlePurchaseClicked">Purchase</Button>
          <Button v-else class="col" style="width:100%; text-align: center" disabled>Cannot Purchase as Business</Button>
        </div>
      </div>
      <div v-else>
        <div>
          <div class="row flex-nowrap">
            <button class="astext" @click="handleCardClicked">
              <router-link :to="getModalLink" style="text-decoration: none; color: inherit;">

              <img class="col-auto" :src="imageUrl || require('../../../public/default.png')" alt="Image Unavailable" style="height: 150px" width="100%"/>
              </router-link>
            </button>
            <button class="card-body astext" @click="handleCardClicked">
              <router-link :to="getModalLink" style="text-decoration: none; color: inherit;">
              <div class="col-md text-wrap">
                <div class="row"><strong class="text-truncate">{{saleListing.inventoryItem.product.name}}</strong></div>
                <div class="row">Price: <strong>{{currency.symbol}}{{saleListing.price.toFixed(2)}} {{currency.code}}</strong></div>
                <div class="row">Lot quantity: <strong>{{saleListing.quantity}}</strong></div>
              </div>
              </router-link>
            </button>
            <div class="col-md-auto my-auto">
              <Button v-if="actingAsCurrentUser" style="width:150px; text-align: center" @click="handlePurchaseClicked">Purchase</Button>
              <Button v-else style="width:150px; text-align: center" disabled>Cannot Purchase as Business</Button>
            </div>
          </div>
        </div>
      </div>

      <template #overlay>
        <div class="text-center">
        <p><strong id="delete-confirmation-label">Purchase this listing?</strong></p>
        </div>
        <div class="d-flex">
          <b-button id="cancel-purchase-button"
                    class="mr-3"
                    variant="secondary"
                    @click="confirmDialogCanceled">Cancel</b-button>
          <b-button id="confirm-purchase-button"
                    variant="success"
                    @click="purchaseConfirm">Purchase</b-button>
        </div>
      </template>
    </b-overlay>
  </div>
</template>

<script>
import {getCurrency} from "@/javascript_modules/currency_util";
import {getImageURL, getPrimaryImage} from "@/javascript_modules/image_util";
import storage_util from "@/javascript_modules/storage_util";

export default {
  name: "SaleItemCard",
  props: {
    eventBus: Object,
    saleListing: Object,
    isModal: Boolean,
    isGrid: Boolean,
  },
  async mounted() {
    window.addEventListener(storage_util.SWITCHED_ACCOUNT_EVENT, this.handleUserChange);
    this.handleUserChange();

    await this.setData();
  },
  watch: {
    /**
     * This watch function is required to make sure that the data is changed
     * in the event that the parent data view reuses this component.
     */
    async saleListing() {
      await this.setData();
    }
  },
  data() {
    return {
      actingAsCurrentUser: true,
      currency: {},
      imageUrl: "",
      cardHovered: false,

      // Set b-overlay to show if display is true
      display: false
    }
  },
  methods: {
    /**
     * Sets the currency of the sale item view.
     * @returns {Promise<void>}
     */
    async setData() {
      this.currency = await getCurrency(this.saleListing.country);
      const primaryImage = getPrimaryImage(this.saleListing.inventoryItem.product.images);
      this.imageUrl = primaryImage ? getImageURL(
          this.saleListing.businessId,
          this.saleListing.inventoryItem.product.id,
          primaryImage.productImageId,
          true
      ) : undefined;
    },

    /**
     * Callback function, called when something with the v-b-hover property is hovered over.
     * Sets the cardHovered property in the data.
     * @param isHovered a value of true or false.
     */
    handleHover(isHovered) {
      this.cardHovered = isHovered;
    },

    /**
     * Called when the clickable regions of the card is clicked.
     * Emits an event called 'card-clicked' on the eventBus.
     */
    handleCardClicked() {
      this.eventBus.$emit("cardClicked", {saleItem: this.saleListing, currency: this.currency});
    },

    /**
     * Set confirmation dialog to true and assign the emitted saleItem from saleItemCard component to purchasedSaleItem.
     */
    handlePurchaseClicked() {
      this.display = true;
    },

    /**
     * Set confirmation dialog display to false.
     */
    confirmDialogCanceled() {
      this.display = false;
    },

    /**
     * Emits on event bus that the purchase button has been clicked
     * and for which sale item.
     */
    purchaseConfirm() {
      this.eventBus.$emit("purchaseClicked", this.saleListing);
      this.display = false;
    },

    /**
     * Handles changing user acting as so that the purchase button can be greyed out when
     * acting as a business.
     */
    handleUserChange() {
      this.actingAsCurrentUser = storage_util.getActingAs() === storage_util.ACTING_AS_CURRENT_USER;
    }
  },
  computed: {
    /**
     * Returns a custom style if the cardHovered property is true.
     * @returns {{"max-width": string, "text-decoration": string}|string}
     */
    underlineTitle() {
      return this.cardHovered
          ? {'max-width': '100%', 'text-decoration': 'underline'}
          : 'max-width: 100%;'
    },

    getModalLink() {
      return {name: 'FullSaleListingView', params: {id: this.saleListing.id}}
    }
  }
}
</script>