<template>
  <div id="MarketplaceDataView">
    <DataView id="data-view"
              :value="cards"
              :layout="layout">
      <!--Header contains sorting options-->
      <template #header>
        <div class="p-grid p-justify-start">
          <div class="p-col-12 p-md-6 p-lg-4">
            <h5>Sort By:</h5>
            <b-dropdown id="card-sorting-menu"
                        :text="selected.label">
              <b-dropdown-item v-for="sortOption in sortOptions"
                               :key="sortOption.id"
                               :id="sortOption.label"
                               @click="sortChange(sortOption)">
                {{sortOption.label}}
              </b-dropdown-item>
            </b-dropdown>
          </div>

          <div class="p-col-12 p-md-6 p-lg-4">
            <h5>Sorting Order:</h5>
            <b-form-group>
              <b-form-radio-group id="sorting-order"
                                  v-model="sortOrder"
                                  :options="sortOrders"
                                  @change="$emit('changeOrder', sortOrder)"/>
            </b-form-group>
          </div>

          <div class="p-col-12 pr-md-6 p-lg-4">
            <br/>
            <b-button @click="handleOpenCreate" :disabled="isBusiness" variant="success" block>{{isBusiness ?
                "Cannot Create Card as Business" : "Create Card"}}</b-button>
          </div>
        </div>
      </template>

      <!--Main data body area-->
      <template #grid="card">
        <div class="p-col-12" :key="card.data.marketplaceCardId" style="width: 30rem; margin: 20px auto;">
          <community-marketplace-card :card="card.data" @cardDeleted="cardDeleted"/>
        </div>
      </template>
    </DataView>
    <CreateCardModal ref="modal" @createdCard="handleCardCreate"/>
  </div>
</template>

<script>
import CommunityMarketplaceCard from "../../components/community-marketplace/CommunityMarketplaceCard";
import Paginator from "../../components/Paginator";
import CreateCardModal from "@/components/community-marketplace/CreateCardModal";
import storage_util from "@/javascript_modules/storage_util";

const CommunityMarketplaceDataView = {
  name: "MarketplaceDataView",
  components: {CreateCardModal, CommunityMarketplaceCard, Paginator},
  emits: ["changeSorting", "changeOrder", "cardDeleted"],
  props: {
    cards: Array
  },
  mounted() {
    this.selected = this.sortOptions[0];
    this.sortOrder = "desc";
    window.addEventListener(storage_util.SWITCHED_ACCOUNT_EVENT, this.setIsBusiness);
    this.setIsBusiness();
  },
  data() {
    return {
      isBusiness: false,
      layout: 'grid',
      showCard: false,
      selected: {id: undefined, label: undefined, value: undefined},
      sortOptions: [
        {id: 1, label: 'Created', value: 'created'},
        {id: 2, label: 'Title', value: 'title'},
        {id: 3, label: 'Suburb', value: 'creator.homeAddress.suburb'},
        {id: 4, label: 'City', value: 'creator.homeAddress.city'},
      ],
      sortOrder: undefined,
      sortOrders: [
        {text: 'Ascending', value: "asc"},
        {text: 'Descending', value: "desc"}
      ]
    }
  },
  methods: {
    /**
     * Sets whether the current user is a business
     */
    setIsBusiness() {
      this.isBusiness = storage_util.getActingAs() !== storage_util.ACTING_AS_CURRENT_USER;
    },
    /**
     * Callback function for the click event for each dropdown item.
     * Takes the selected option and emits it as an object along with the order in the 'changeSorting' event to the
     * parent.
     * @param option
     */
    sortChange(option) {
      this.selected = option;
      this.$emit("changeSorting", {sort: this.selected.value, order: this.sortOrder});
    },

    /**
     * This method passes the information that a card has been deleted from CommunityMarketplaceCard.vue
     * to CommunityMarketplace.vue
     * @param event in the form: {id: <number>, section: <string>}
     */
    cardDeleted(event) {
      this.$emit("cardDeleted", event);
    },

    /**
     * Handles the button click that opens the card creation dialog.
     */
    handleOpenCreate() {
      this.$refs.modal.show();
    },

    handleCardCreate() {
      this.$emit("cardCreated");
    }
  }
}

export default CommunityMarketplaceDataView;

</script>

<style scoped>

#card-sorting-menu {
  width: 100%;
}

</style>