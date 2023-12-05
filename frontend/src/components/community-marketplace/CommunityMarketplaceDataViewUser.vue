<template>
  <div id="MarketplaceDataViewUser">
    <DataView id="data-view"
              :value="cards"
              :layout="layout">

      <template #grid="card">
        <div class="p-col-12" :key="card.data.marketplaceCardId" style="width: 30rem; margin: 20px auto;">
          <community-marketplace-card :card="card.data" @cardDeleted="cardDeleted" />
        </div>
      </template>
    </DataView>
  </div>
</template>

<script>
import CommunityMarketplaceCard from "../../components/community-marketplace/CommunityMarketplaceCard";
import Paginator from "../../components/Paginator";

const CommunityMarketplaceDataViewUser = {
  name: "MarketplaceDataViewUser",
  components: {CommunityMarketplaceCard, Paginator},
  emits: ["cardDeleted"],
  props: {
    cards: Array
  },
  data() {
    return {
      layout: 'grid',
    }
  },
  methods: {
    /**
     * This method passes the information that a card has been deleted from CommunityMarketplaceCard.vue
     * to CommunityMarketplace.vue
     * @param event in the form: {id: <number>, section: <string>}
     */
    cardDeleted(event) {
      this.$emit("cardDeleted", event);
    }
  }
}

export default CommunityMarketplaceDataViewUser;

</script>