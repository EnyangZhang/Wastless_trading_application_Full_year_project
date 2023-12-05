<template>
  <div v-if="actingAsBusiness">
    <b-card id="resultsCard"
            fluid="true">
      <b-card id="salesHistory"
              fluid="true">
        <b-card-title :title="businessName"
                      id="salesHistoryTitle"/>
        <b-table id="salesHistoryTable"
                 striped
                 hover
                 :responsive="true"
                 :items="items"
                 :fields="fields"
                 sort-icon-left
                 :per-page="perPage"
                 :current-page="currentPage">
          <template #cell(image)="data">
            <img id="item-image"
                 :src="data.item.image || require(`../../../public/default.png`)"
                 class="img-thumbnail"
                 alt="Default inventory image">
          </template>
          <template #cell(created)="data">
            {{formattedDate(data.item.created)}}
          </template>
          <template #cell(purchaseDate)="data">
            {{formattedDate(data.item.purchaseDate)}}
          </template>
        </b-table>
      </b-card>
      <b-row id="item">
        <b-col>
          <paginator :page="currentPage"
                     :items-length="items.length"
                     :per-page="perPage"
                     @changePage="changePage"/>
        </b-col>
      </b-row>
    </b-card>

  </div>

  <div v-else>
  </div>
</template>

<script>
import {displayPrice, getCurrency} from "@/javascript_modules/currency_util";
import Paginator from "@/components/Paginator";
import storage_util from "../../javascript_modules/storage_util";
import api from "../../Api";
import {formatDateString} from "@/javascript_modules/date_util";
import {getImageURL, getPrimaryImage} from "@/javascript_modules/image_util";

export default {
  name: "SalesHistory",
  components: {Paginator},
  data() {
    return {
      currentPage: 1,      // current page of search results
      perPage: 5,         // number of search results displayed per page
      totalPage: null,

      actingAs: null,
      actingAsBusiness: false,

      fields: [
        {
          key: "image",
          label: ""
        },
        {
          label: 'Product',
          key: 'product.name',
          sortable: true
        },
        {
          label: 'Product ID',
          key: 'productId',
          sortable: true
        },
        {
          key: 'quantity',
          sortable: true
        },
        {
          label: 'Total Price',
          key: 'totalPrice',
          sortable: true
        },
        {
          label: 'Likes',
          key: 'likes',
          sortable: true
        },
        {
          label: 'Listed Date',
          key: 'created',
          sortable: true
        },
        {
          label: 'Sold Date',
          key: 'purchaseDate',
          sortable: true
        },
        {
          label: 'More Info',
          key: 'moreInfo'
        }
      ],
      items: [],
      ifHide: false,
      // RestCountries variables
      currency: undefined,

      // Meta information regarding current business
      businessId: undefined,
      businessName: undefined,
      businessCountry: undefined,


      currentImages: [],

      error: {
        flag: false,
        message: undefined
      }
    }
  },
  async mounted() {
    await this.getCurrentUserActAs();
    await this.getBusinessDetails();
    this.getSoldListings();
  },
  methods: {
    /**
     * Function to call the method to get data that the parent is able to call for any related component.
     **/
    refreshData() {
      this.getSoldListings();
    },

    /**
     * Updates the current page to control the results in the table
     */
    changePage(page) {
      this.currentPage = page;
    },

    /**
     * Calculate total page regards to number of items to show.
     */
    calculateTotalPage() {
      this.totalPage = Math.ceil(this.items.length / this.perPage)
      if (this.totalPage === 0) {
        this.totalPage = 1
      }
    },

    /**
     * Gets the current account that user acting as and sets the business ID.
     */
    getCurrentUserActAs() {
      this.actingAs = storage_util.getActingAs();
      this.actingAsBusiness = this.actingAs !== storage_util.ACTING_AS_CURRENT_USER
      this.getBusinessId();
    },

    /**
     * Sets the current business ID that the user acting as if the user is acting as a business.
     */
    getBusinessId() {
      if (this.actingAsBusiness === true) {
        this.businessId = this.actingAs;
      }
    },

    /**
     * Get business name that the catalogue user is viewing.
     */
    async getBusinessDetails() {
      if (this.actingAsBusiness === true) {
        let response = await api.getBusiness(this.businessId)

        let business = response.data
        this.businessName = business.name + "'s Sales History"
        this.businessCountry = business.address.country;
        this.currency = await getCurrency(this.businessCountry)

      }
    },
    /**
     * If the listing quantity is equal to total quantity, return set total price otherwise calculates it.
     * Calculates the total price of the listing if the listing quantity does not exceed inventory stock
     * and price is not invalid.
     */
    totalPrice(price, listingQ, itemQ, totalPrice) {
      if (price && totalPrice) {
        if (listingQ < itemQ) {
          return `${this.currency.symbol}${Number((price * listingQ).toFixed(2))} ${this.currency.code}`;
        } else if (listingQ === itemQ) {
          return `${this.currency.symbol}${Number((totalPrice).toFixed(2))} ${this.currency.code}`;
        } else {
          return "Quantity Exceed Stock";
        }
      } else {
        return "Invalid Price";
      }
    },

    /**
     * Loops through image and generates a URL to send to the server to request for a particular image. Each URL is
     * pushed into a list as an object with the url and product image id as attributes. If an image is a primary image,
     * it is sent alongside the list of urls.
     * @image List of image objects of each product. They contain the image ID and a boolean of if they are primary.
     * @productId ID of the product that the list of images come from.
     */
    getProductImages(productImages, productId) {
      let productImageURLs = [];
      productImages.forEach((image) => {
        const imageObject = {
          id: image.productImageId,
          data: getImageURL(this.businessId, productId, image.productImageId, false)
        };
        productImageURLs.push(imageObject);
      });
      return productImageURLs;
    },
    /**
     * Gets all the sale listings for a business via an api call.
     * Adds all sold listings to the list displayed in the table
     */
    getSoldListings() {
      if (this.actingAsBusiness === true) {
        api.getSaleListings(this.businessId, true).then((response) => {
          this.items = [];
          const data = response.data;
          data.forEach(async (item) => {
            const inventoryItem = item.inventoryItem;
            const price = item.price
            const likes = item.likedByUsers.length;
            const productId = inventoryItem.product.id;
            const productImages = inventoryItem.product.images;
            const primaryImage = getPrimaryImage(productImages);
            const primaryImageURL = primaryImage ?
                getImageURL(this.businessId, inventoryItem.product.id, primaryImage.productImageId, true) : undefined;

            const productImageURLs = this.getProductImages(productImages, inventoryItem.product.id);
            const displayItem = {
              image: primaryImageURL,
              id: item.id,
              product: inventoryItem.product,
              productId: productId,
              quantity: item.quantity,
              totalPrice: displayPrice(price, this.currency.symbol, this.currency.code),
              likes: likes,
              created: item.created,
              moreInfo: item.moreInfo,
              sold: item.sold,
              purchaseDate: item.purchased,
              images: productImageURLs
            }
            this.items.push(displayItem);
          });
        }).catch((error) => {
          this.error.flag = true;
          this.error.listings = `Error getting sales history: ${error}`;
        })
      }
    },

    /**
     * Calls format date function from module. If date is undefined, return undefined without parsing.
     * @param date
     * @returns {*|undefined}
     */
    formattedDate(date) {
      return date ? formatDateString(date) : undefined
    }
  }
}
</script>

<style scoped>

* {
  box-sizing: border-box;
}

#item {
  padding: 20px;
}

#item-image {
  width: 100%;
  height: 100%;
  min-width: 125px;
  min-height: 125px;
  max-width: 125px;
  max-height: 125px;
}

</style>