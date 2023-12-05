<template>
  <div>
    <b-card no-body>
      <div id="user-marketplace-card">
        <marketplace-data-view-user id="user-cards" :cards="currentPageItems" @cardDeleted="cardDeleted"/>
        <paginator id="user-cards-paginator"
                   :itemsLength="userCards.items.length"
                   :page="userCards.page"
                   :perPage="userCards.size"
                   @changePage="changePage($event)"/>
      </div>
    </b-card>
  </div>
</template>

<script>
import MarketplaceDataViewUser from "../components/community-marketplace/CommunityMarketplaceDataViewUser";
import Paginator from "../components/Paginator";
import api from "../Api";
import User from "../components/ViewUser";
import FormattedAddress from "../components/address/FormattedAddress";

const MyCardsView = {
  name: "CommunityMarketplaceUser",
  components: {MarketplaceDataViewUser, Paginator, FormattedAddress, User},
  data() {
    return {
      errorFlag: false,
      errorMessage: undefined,
      userCardsTitle: "My Cards",
      currentUserId: null,
      userCards: {
          page: 1,
          size: 9,
          currentDisplayItemStart: null,
          currentDisplayItemEnd: null,
          items: [],
      },

      currentPageItems: []
    }
  },
  async mounted() {
    this.getUserId();
    this.getUserCards();
  },
  methods: {
    /**
     * Calls the API to get all the cards belong to the provided user id.
     * Then calls getCurrentPageItem to get a range of element in response to be
     * displayed on current page.
     */
    getUserCards() {
      api.getUserCards(this.currentUserId).then((response) => {
        this.userCards.items = response.data;
        this.getCurrentPageItem()
      }).catch((err) => {
        this.errorFlag = true;
        this.errorMessage = err;
      });
    },

    /**
     * Gets the user Id needs to display on My Cards page, it will be either my cards or another user's cards by id.
     */
    getUserId() {
        this.currentUserId = this.$route.params.id;
    },

    /**
     * Deletes the user's card and calls getUserCards to reload the user's cards
     */
    cardDeleted() {
        this.getUserCards()
    },

    /**
     * Gets Card items for current page based on which page the user is in.
     * If the current page will display nothing and current page num is not 1,
     * then redirect to previews page.
     */
    getCurrentPageItem (){
      this.getCurrentPageItemRange()
      this.currentPageItems = this.userCards.items.slice(this.userCards.currentDisplayItemStart, this.userCards.currentDisplayItemEnd)
      if (this.currentPageItems.length === 0 && this.userCards.page !== 1) {
        this.changePage(this.userCards.page - 1)
        this.getUserCards()
      }
    },

    /**
     * Sets the range of items index in userCards to be display for current page.
     */
    getCurrentPageItemRange() {
      this.userCards.currentDisplayItemStart = (this.userCards.page - 1) * this.userCards.size
      this.userCards.currentDisplayItemEnd = this.userCards.currentDisplayItemStart + this.userCards.size
    },

    /**
     * Change the page via paginator component and reload the current page items.
     * @param updatedPage new current page num after click the paginating button.
     */
    changePage(updatedPage) {
      this.userCards.page  = updatedPage;
      this.getCurrentPageItem()
    }
  },
  watch: {
    $route() {
      this.getUserId();
      this.getUserCards();
    }
  }
}

export default MyCardsView;

</script>

<style>
</style>



