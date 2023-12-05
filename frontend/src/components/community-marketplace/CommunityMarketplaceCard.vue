<template>
  <div id="community-marketplace-card">
    <b-overlay id="confirm-delete-overlay" :show="deleteDialogue">
      <div id="item-card"
           :class="cardClass">
        <button id="clickable-region"
                class="astext"
                v-b-hover="handleHover"
                @click="viewCardDetails">
          <b-card-header>
            <b-card-title v-if="isModal"
                          id="details-title"
                          class="text-center">
              {{card.title}}
            </b-card-title>
            <b-card-title v-else
                          id="card-title"
                          class="text-center">
              {{card.title}}
            </b-card-title>
          </b-card-header>
          <b-card-body>
            <b-row>
              <!--Card Image-->
              <b-col id="card-image-container">
                <img id="card-image"
                     alt="image"
                     :src="require('../../../public/default.png')"/>
              </b-col>
              <b-col>
                <!--Creator Info-->
                <b-card-text class="card-info" id="creator-info">
                  <b-card-sub-title>Created By</b-card-sub-title>
                  <User :user="card.creator" ref="user" />
                </b-card-text>

                <!--Creator Location Info-->
                <b-card-text class="card-info" id="creator-location">
                  <b-card-sub-title>Location</b-card-sub-title>
                  <formatted-address full-address :address="addressInfo"/>
                </b-card-text>
              </b-col>
            </b-row>

            <b-row>
              <b-col>
                <!--Card description-->
                <b-card-text class="card-info" id="card-description">
                  <b-card-sub-title>Info</b-card-sub-title>
                  <div id="no-description" v-if="!card.description">
                    <em>No Description</em>
                  </div>
                  <div id="full-description" v-if="isModal">
                    {{card.description}}
                  </div>
                  <div id="shortened-description" v-else>
                    <span>{{card.description}}</span>
                  </div>
                </b-card-text>

                <!--Card Keywords-->
                <b-card-text class="card-info" id="keywords">
                  <b-card-sub-title>Keywords</b-card-sub-title>
                  <keywords :keywords="card.keywords"/>
                </b-card-text>
              </b-col>
            </b-row>
          </b-card-body>
        </button>

        <b-card-body>
          <!--Delete card button-->
          <div id="delete-card-show"
               :disabled="deleteDisabled"
               v-if="canDelete">
            <b-button id="delete-card" variant="outline-danger" @click="deleteTrigger">
              <b-icon name="b-icon"
                      icon="b-icon-trash"/>
            </b-button>
          </div>
        </b-card-body>

        <b-card-footer>
          <div id="created-time" class="small text-sm-right">
            <em>Created on {{formatDate(card.created)}}</em>
          </div>
          <div id="expires-time" class="small text-sm-right">
            <em>Expires on {{formatDate(card.displayPeriodEnd)}}</em>
          </div>
        </b-card-footer>
      </div>

      <template #overlay>
        <!--Confirm/Cancel delete buttons-->
        <div class="text-center">
          <p><strong id="delete-confirmation-label">Delete this Card?</strong></p>
          <div class="d-flex">
            <b-button id="cancel-delete-button"
                      class="mr-3"
                      variant="secondary"
                      @click="deleteCancel">Cancel</b-button>
            <b-button id="confirm-delete-button"
                      variant="danger"
                      @click="deleteCard">Delete</b-button>
          </div>
        </div>
      </template>
    </b-overlay>

    <b-modal id="details-modal"
             ref="details"
             size="lg"
             hide-footer>
      <community-marketplace-card :is-modal="true"
                                  @cardDeleted="emitCardDeleted"
                                  :card="card"/>
    </b-modal>
  </div>
</template>

<script>
import User from "../../components/ViewUser";
import FormattedAddress from "../../components/address/FormattedAddress";
import Keywords from "../../components/community-marketplace/Keywords";
import storage_util from "../../javascript_modules/storage_util";
import {formatDateString} from "../../javascript_modules/date_util";
import roles from "../../validation/roles.validation";

import Api from "../../Api";

export default {
  name: "CommunityMarketplaceCard",
  components: {Keywords, FormattedAddress, User},
  props: {
    card: Object,
    isModal: Boolean
  },
  emits: ["cardDeleted"],

  data() {
    return {
      modalVisible: false,
      isHovered: false,
      deleteDisabled: false,
      deleteDialogue: false
    }
  },
  methods: {
    /**
     * Function callback for clicking on card title. Displays the "details" modal.
     */
    viewCardDetails() {
      if (!this.isModal && (this.$route.name === "CommunityMarketplace" || this.$route.name === "MyCards" || this.$route.name === "UserCards")) {
        this.$refs["details"].show();
        this.modalVisible = true;
      }
    },
    /**
     * Function to handle the hover event over the title element.
     * If the event parameter is 'mouseover' the hovered status is set to true.
     * Otherwise the hovered status is false.
     * @param event
     */
    handleHover(event) {
      this.isHovered = event
    },
    /**
     * Takes a date as a parameter and returns the result of formatDateString for that date, using the date util module.
     * @param date
     */
    formatDate(date) {
      return formatDateString(date);
    },

    /**
     * Triggers the delete confirm state
     */
    deleteTrigger() {
      this.deleteDialogue = true;
    },

    /**
     * Cancels the delete dialog
     */
    deleteCancel() {
      this.deleteDialogue = false;
    },

    /**
     * This method sends an API request to the backend to delete a card, and then emits a cardDeleted event so that
     * the card will be removed in CommunityMarketplace.vue without having to reload the page
     */
    async deleteCard() {

      this.deleteDisabled = true;
      this.deleteDialogue = false;

      try {
        await Api.deleteMarketplaceCard(this.card.marketplaceCardId);
        if (this.isModal) {
          this.$bvModal.hide("details-modal");
          this.modalVisible = false;
        }
        this.$bvToast.toast(`Successfully deleted "${this.card.title}".`, {
          title: 'Card Deleted Successfully',
          variant: 'success',
          toaster: 'b-toaster-bottom-right',
          autoHideDelay: 5000,
          appendToast: true
        });
        this.$emit("cardDeleted", {"id": this.card.marketplaceCardId, "section": this.card.section});
      } catch {
        // do nothing for now
      }
    },

    /**
     * Method to emit the card delete method from modal view of the marketplace card
     */
    emitCardDeleted(event) {
      this.$emit("cardDeleted", event);
    }
  },

  computed: {
    /**
     * Obtains the address information from the creator attribute of the card prop, returns the relevant details found
     * in the address information. At present the relevant address values returned are suburb and city.
     **/
    addressInfo() {
      const address = this.card.creator.homeAddress;
      return {
        suburb: address.suburb,
        city: address.city
      }
    },
    /**
     * Dynamically returns the class of the card, based on if the hover state is true (and the card is not an unhoverable
     * version, such as the one seen in the modal).
     * @returns {string}
     */
    cardClass() {
      return this.isHovered && !this.isModal
          ? 'card shadow astext'
          : 'card astext';
    },

    /**
     * Returns true if the card can be deleted:
     *   - If the user is acting as themselves and not a business
     *   - If this marketplace card's creator property matches ID with the action user
     *   - Or if the user is a global application admin
     *  Otherwise returns false
     */
    canDelete() {
      const actingAs = storage_util.getActingAs();
      const creatorId = this.card.creator.id;

      if (actingAs === storage_util.ACTING_AS_CURRENT_USER) {
        const role = storage_util.getCurrentUserInfo().role;

        if (storage_util.getCurrentUser() === creatorId) {
          return true;
        }

        if (role === roles.GLOBAL_APPLICATION_ADMIN || role === roles.DEFAULT_GLOBAL_APPLICATION_ADMIN) {
          return true;
        }
      }
      return false;
    }
  }
}
</script>

<style scoped>

#item-card {
  background-color: white;
  width: 30rem;
  margin: auto;
}

/*Class styles*/
.card-info {
  background-color: white;
  padding: 10px 7px;
}

/*Element styles*/
#shortened-description {
  white-space: nowrap;
  overflow: hidden;
}

#card-title {
  user-select: none;
  white-space: nowrap;
  overflow: hidden;
}

#details-title {
  white-space: nowrap;
  overflow: hidden;
}

#card-image {
  width: 100%;
  height: 100%;
  max-width: 250px;
  max-height: 200px;
}

#card-image-container {
  width: 250px;
  height: 200px;
  margin-bottom: 10px;
  text-align: center;
}

</style>