<template>
  <div v-b-hover="handleHover" :style="'padding: 4px 24px;' +
                                       (compact?'width: 30rem; ':'') +
                                       (notificationRead ? 'color: #9a9a9a' : '') + ';' +
                                       (cardHovered && compact? 'background-color: #dadada': '')">
    <b-row no-gutters class="flex-nowrap">
      <b-col md="auto" class="my-auto">
        <b-icon name="Icon" icon="bell"></b-icon>
      </b-col>
      <b-col>
        <b-card-body class="text-wrap" :title="notificationTitle" style="padding-bottom: 0; padding-top: 0">
          <b-card-text :class="compact? 'text-truncate': ''">
            {{notificationMessage}}
            <br>
            <small>{{notificationIssued}}</small>
          </b-card-text>

          <!--Information section belonging to notifications about purchase of sale listings-->
          <div v-if="category==='PURCHASE_OF_LISTING'">
            <product-notification-info :sale-item-id="actionId"/>
          </div>
        </b-card-body>
      </b-col>
    </b-row>
    <b-row class="offset-1" v-if="!notificationRead">
      <!-- Card actions for a notification that a card is about to expire -->
      <div v-if="category === 'CARD_EXPIRY_WARNING'">
        <Button
            id="extendCardButton"
            label="Extend"
            icon="pi pi-refresh"
            @click="extendCardAction()"/>
        <Button
            id="deleteCardButton"
            class="p-button-danger"
            icon="pi pi-trash"
            label="Delete"
            @click="deleteCardAction()"/>
      </div>
      <div v-else-if="category === 'LIKED_A_LISTING'">
        <Button id="viewListingButtonUnread"
                label="View listing"
                icon="pi pi-search-plus"
                @click="viewFullListingAction()"/>
        <Button
            id="markLikedListingAsReadButton"
            icon="pi pi-eye"
            label="Mark as read"
            @click="markNotificationAsRead()"/>

      </div>
      <!-- Card actions for a notification that a card has been deleted due to expiry -->
      <div v-else-if="category === 'CARD_EXPIRED'">
        <Button
            id="markExpiredAsReadButton"
            icon="pi pi-eye"
            label="Mark as read"
            @click="markNotificationAsRead()"/>
      </div>
      <!-- Card actions for other types of notifications -->
      <div v-else>
        <Button
            id="markAsReadButton"
            icon="pi pi-eye"
            label="Mark as read"
            @click="markNotificationAsRead()"/>
      </div>
    </b-row>
    <b-row class="offset-1" v-else>
      <div v-if="category === 'LIKED_A_LISTING'">
        <Button id="viewListingButtonRead"
                label="View listing"
                icon="pi pi-search-plus"
                @click="viewFullListingAction()"/>
      </div>
    </b-row>

  </div>
</template>

<script>

import Api from "@/Api";
import storage_util from "@/javascript_modules/storage_util";
import ProductNotificationInfo from "./ProductNotificationInfo";
import {formatShortDateTimeString} from "@/javascript_modules/date_util";

export default {
  name: "NotificationCard",
  components: {ProductNotificationInfo},
  mounted() {
    this.setup();
  },
  props: {
    notification: Object,
    compact: Boolean,
  },
  data() {
    return {
      notificationTitle: "",
      notificationMessage: "",
      notificationIssued: "",
      category: "",
      notificationRead: false,
      cardHovered: false,
      actionId: null,
    }
  },
  watch: {
    /**
     * Calls setup to set the data when the props change.
     * This is required to update when the page changes because
     * new components are not created.
     */
    notification() {
      this.setup();
    }
  },
  methods: {
    /**
     * Sets the data for the card to display.
     */
    setup() {
      this.notificationTitle = this.notification.title;
      this.notificationMessage = this.notification.message;
      this.notificationRead = this.notification.read;
      this.category = this.notification.category;
      this.actionId = this.notification.actionId;
      this.notificationIssued = formatShortDateTimeString(this.notification.created)
    },
    /**
     * Creates toast message for a given error message.
     * @param err Error response given by API.
     */
    errorToast(err) {
      let title = "";
      if (err.response.status === 404) {
        title = "Could not reach server";
      } else if (err.response.status === 406) {
        this.notificationRead = true;
        this.markNotificationAsRead();
        title = "Item does not exist";
      } else if (err.response.status === 500) {
        title = "Unknown error";
      }

      this.$bvToast.toast(`An unknown error has occurred`, {
        title: title,
        variant: 'danger',
        toaster: 'b-toaster-bottom-right',
        autoHideDelay: 10000,
        appendToast: true
      });
    },

    /**
     * Calls the API to set the related notification as read.
     * Shows toast on failure.
     * Sets to display as read on success.
     */
    markNotificationAsRead() {
      if (!this.read) {
        Api.markNotificationAsRead(storage_util.getCurrentUser(), this.notification.id).then((response) => {
          if (response.status === 200) {
            if (!this.notificationRead) {
              this.updateDropdownNumber();
            }
            this.notificationRead = true;
          }
        }).catch((err) => {
          this.errorToast(err);
        });
      }
    },

    updateDropdownNumber() {
      this.$root.$emit('notificationRead');
    },

    /**
     * Completes the action deleting card.
     * Shows toast on success and failure.
     * Sets notification as read on success.
     */
    deleteCardAction() {
      Api.deleteMarketplaceCard(this.actionId).then((response) => {
        if (response.status === 200) {
          this.markNotificationAsRead();
          this.$bvToast.toast(`Successfully deleted the marketplace card.`, {
            title: "Successful deletion",
            variant: 'success',
            toaster: 'b-toaster-bottom-right',
            autoHideDelay: 10000,
            appendToast: true
          });
        }
      }).catch((err) => {
        this.errorToast(err);
      });
    },
    /**
     * Completes the action extending a card.
     * Shows toast on success and failure.
     * Sets notification as read on success.
     */
    extendCardAction() {
      Api.extendMarketplaceCard(this.notification.actionId).then((response) => {
        if (response.status === 200) {
          this.markNotificationAsRead();
          this.$bvToast.toast(`Successfully extended the marketplace card.`, {
            title: "Successful extension",
            variant: 'success',
            toaster: 'b-toaster-bottom-right',
            autoHideDelay: 10000,
            appendToast: true
          });
        }
      }).catch((err) => {
        this.errorToast(err);
      });
    },

    /**
     * Method to take the user to the full sale listing modal in the search sale listings page
     * Emits the event to the saleItemDataViewComponent with the id of the listing as the parameter
     */
    async viewFullListingAction() {
      if (this.$route.name !== "HomePage") {
        await this.$router.push('/home');
      }
      await this.$router.push({name: 'FullSaleListingView', params: {id: this.notification.actionId}});
      if (!this.notification.read) {
        this.markNotificationAsRead();
      }
      this.$root.$emit("listingNotificationClicked", this.notification.actionId);
    },

    /**
     * Callback function, called when something with the v-b-hover property is hovered over.
     * Sets the cardHovered property in the data.
     * @param isHovered a value of true or false.
     */
    handleHover(isHovered) {
      this.cardHovered = isHovered;
    },
  }
}
</script>

<style scoped>

#viewListingButtonUnread, #extendCardButton {
  margin-right: 5px;
}
</style>
