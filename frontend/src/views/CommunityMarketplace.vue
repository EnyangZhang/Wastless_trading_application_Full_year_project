<template>
  <div id="communityMarketplace">
    <b-card no-body>

      <div id="createCardModal">
        <create-card-modal/>
      </div>

      <b-tabs id="communityMarketplaceTabs" justified card>
        <b-tab id="forSaleTab" lazy>
          <template #title>
            <strong>For Sale</strong>
          </template>
          <div id="forSaleTabContent">
            <marketplace-data-view id="for-sale-data"
                                   :cards="sections.forSale.items"
                                   @changeSorting="changeSorting(sections.forSale, $event)"
                                   @cardDeleted="cardDeleted"
                                   @changeOrder="changeOrder(sections.forSale, $event)"
                                   @cardCreated="handleCardCreate"/>
            <paginator id="for-sale-paginator"
                       :itemsLength="sections.forSale.totalPages"
                       :page="sections.forSale.page"
                       :perPage="sections.forSale.size"
                       @changePage="changePage(sections.forSale, $event)"/>
          </div>
        </b-tab>

        <b-tab id="exchangeTab" lazy>
          <template #title>
            <strong>Exchange</strong>
          </template>
          <div id="exchangeTabContent">
            <marketplace-data-view id="exchange-data"
                                   :cards="sections.exchange.items"
                                   @changeSorting="changeSorting(sections.exchange, $event)"
                                   @cardDeleted="cardDeleted"
                                   @changeOrder="changeOrder(sections.exchange, $event)"
                                   @cardCreated="handleCardCreate"/>
            <paginator id="exchange-paginator"
                       :itemsLength="sections.exchange.totalPages"
                       :page="sections.exchange.page"
                       :perPage="sections.exchange.size"
                       @changePage="changePage(sections.exchange, $event)"/>
          </div>
        </b-tab>

        <b-tab id="wantedTab" lazy>
          <template #title>
            <strong>Wanted</strong>
          </template>
          <div id="wantedTabContent">
            <marketplace-data-view id="wanted-data" :cards="sections.wanted.items"
                                   @changeSorting="changeSorting(sections.wanted, $event)"
                                   @cardDeleted="cardDeleted"
                                   @changeOrder="changeOrder(sections.wanted, $event)"
                                   @cardCreated="handleCardCreate"/>
            <paginator id="wanted-paginator"
                       :itemsLength="sections.wanted.totalPages"
                       :page="sections.wanted.page"
                       :perPage="sections.wanted.size"
                       @changePage="changePage(sections.wanted, $event)"/>
          </div>
        </b-tab>
      </b-tabs>
    </b-card>
  </div>
</template>

<script>
import MarketplaceDataView from "../components/community-marketplace/CommunityMarketplaceDataView";
import Paginator from "../components/Paginator";
import api from "../Api";
import User from "../components/ViewUser";
import FormattedAddress from "../components/address/FormattedAddress";
import CreateCardModal from "../components/community-marketplace/CreateCardModal";

const CommunityMarketplace = {
  name: "CommunityMarketplace",
  components: {CreateCardModal, MarketplaceDataView, Paginator, FormattedAddress, User},
  data() {
    return {
      errorFlag: false,
      errorMessage: undefined,

      sections: {
        forSale: {
          label: "For Sale",
          param: "For Sale",
          page: 1,
          size: 9,
          totalPages: 0,
          items: [],
          sort: undefined,
          order: undefined
        },
        exchange: {
          label: "Exchange",
          param: "Exchange",
          page: 1,
          size: 9,
          totalPages: 0,
          items: [],
          sort: undefined,
          order: undefined
        },
        wanted: {
          label: "Wanted",
          param: "Wanted",
          page: 1,
          size: 9,
          totalPages: 0,
          items:[],
          sort: undefined,
          order: undefined
        }

      }
    }
  },
  async mounted() {
    this.getAllSectionCards();
  },
  methods: {
    /**
     * For each of the defined sections in the component, calls getCards function with the section as an argument.
     */
    getAllSectionCards() {
      for (const key in this.sections) {
        const section = this.sections[key];
        this.getCards(section);
      }
    },

    /**
     * Gets all marketplace cards given a specific section string.
     * From the section passed in as argument, calls the api with the name of the section, section page
     *
     * Note: Section page starts at 1 in the frontend, while the server treats page 0 as the first page for pagination,
     * so the frontend subtracts 1 from its page number before sending it to the server.
     * @param section
     */
    getCards(section) {
      api.getCards(section.param, section.page - 1, section.size, section.sort, section.order).then((response) => {
        section.totalPages = Number(response.headers["total-length"]);
        section.items = response.data;
      }).catch((err) => {
        this.errorFlag = true;
        this.errorMessage = err;
      });
    },

    /**
     * Callback to handle the changing of pages from the paginator changePage event.
     * Updates the page number of the appropriate section given, and calls the getCards function for the same section,
     * with the updated page number.
     * @param updateSection
     * @param updatedPage
     */
    changePage(updateSection, updatedPage) {
      updateSection.page = updatedPage;
      this.getCards(updateSection);
    },

    /**
     * Callback to handle the changing of sorting.
     * Updates the sorting column and order of the specific section that is given, and calls the getCards function
     * for that section.
     * @param updateSection
     * @param updatedSort
     */
    changeSorting(updateSection, updatedSort) {
      updateSection.sort = updatedSort.sort;
      updateSection.order = updatedSort.order;
      this.getCards(updateSection);
    },



    /**
     * This method dynamically removes a deleted card without having to re-request cards from the backend
     * @param event in the form {id: <number>, section: <string>}
     */
    cardDeleted(event) {
      if (event.section === this.sections.forSale.label) {
        this.sections.forSale.items = this.sections.forSale.items.filter(item => item.marketplaceCardId !== event.id);
      } else if (event.section === this.sections.exchange.label) {
        this.sections.exchange.items = this.sections.exchange.items.filter(item => item.marketplaceCardId !== event.id);
      } else if (event.section === this.sections.wanted.label) {
        this.sections.wanted.items = this.sections.wanted.items.filter(item => item.marketplaceCardId !== event.id);
      }
    },

    /**
     * Callback to handle the changing of ordering specifically.
     * Updates the ordering attribute of the specific section that is given, and calls the getCards function for that
     * section.
     * @param updateSection
     * @param updatedOrder
     */
    changeOrder(updateSection, updatedOrder) {
      updateSection.order = updatedOrder;
      this.getCards(updateSection);
    },

    handleCardCreate() {
      this.getAllSectionCards()
    }
  }
}

export default CommunityMarketplace;

</script>
