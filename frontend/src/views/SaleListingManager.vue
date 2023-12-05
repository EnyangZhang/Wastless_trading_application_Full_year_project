<template>
  <div id="myListing">
    <b-alert :show="true" dismissible>Click on a product to view more images</b-alert>
    <b-card id="listingsCard"
            fluid="true">
      <b-card id="businessListings"
              fluid="true">
        <b-card-title id="businessListingsTitle"
                      :title="tableName"/>
        <b-table id="listingsTable"
                 striped
                 hover
                 :responsive="true"
                 :items="items"
                 :fields="fields"
                 sort-icon-left
                 :per-page="perPage"
                 :current-page="currentPage"
                 @row-clicked="handleRowClicked">
          <template #cell(image)="data">
            <img id="item-image"
                 :src="data.item.image || require(`../../public/default.png`)"
                 class="img-thumbnail"
                 alt="Default inventory image">
          </template>
          <template #cell(created)="data">
            {{formattedDate(data.item.created)}}
          </template>
          <template #cell(closes)="data">
            {{formattedDate(data.item.closes)}}
          </template>
        </b-table>

      </b-card>

      <b-row id="item">
        <b-col>
          <paginator id="paginator"
                     :page="currentPage"
                     :items-length="items.length"
                     :per-page="perPage"
                     @changePage="changePage"/>
          <b-button id="new-product-button"
                    variant="success"
                    :hidden="ifHide"
                    @click="addListing">Add New Listing</b-button>
        </b-col>
      </b-row>
    </b-card>
    <b-modal id="images-modal"
             scrollable
             hide-footer>
      <image-view id="product-images"
                  :images="currentImages"
                  read-only/>
    </b-modal>
  </div>
</template>

<script>
import StorageUtil from "../javascript_modules/storage_util";
import Api from "../Api";
import {formatDateString} from "@/javascript_modules/date_util";
import {displayPrice, getCurrency} from "@/javascript_modules/currency_util";
import Paginator from "@/components/Paginator";
import ImageView from "@/components/ImageView";
import {getImageURL, getPrimaryImage} from "@/javascript_modules/image_util";
import Validation from '../validation/general.validation';

export default {
  name: "MyListings",
  components: {ImageView, Paginator},
  data() {
    return {
      currentPage: 1,
      perPage: 5,
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
          label: 'Price',
          key: 'totalPrice',
          sortable: true
        },
        {
          label: 'Likes',
          key: 'likes',
          sortable: true
        },
        {
          label: 'Created Date',
          key: 'created',
          sortable: true
        },
        {
          key: 'closes',
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
    this.getCurrentUserActAs();
    await this.getBusinessNameAndCountry();
    this.currency = await getCurrency(this.businessCountry)
    this.getListings();
  },
  methods: {
    /**
     * Function to call the method to get data that the parent is able to call for any related component.
     **/
    refreshData() {
      this.getListings();
    },

    /**
     * Gets the list of sale items that the current business has active.
     * Assumes the prices of sale listings are never null or undefined, so does not check for them.
     * Sets the data field @items
     */
    getListings() {
      Api.getSaleListings(this.businessId, false).then((response) => {
        this.items = []
        const data = response.data;
        data.forEach(async (item) => {
          const inventoryItem = item.inventoryItem;
          const totalPrice = Number((item.price).toFixed(2));
          const likes = item.likedByUsers.length;
          const productId = inventoryItem.product.id;
          const product = inventoryItem.product
          product.name = Validation.truncateString(product.name)
          const productImages = inventoryItem.product.images;
          const primaryImage = getPrimaryImage(productImages);
          const primaryImageURL = primaryImage ?
              getImageURL(this.businessId, inventoryItem.product.id, primaryImage.productImageId, true) : undefined;

          const created = item.created;
          const closes = item.closes;
          const productImageURLs = this.getProductImages(productImages, inventoryItem.product.id);

          const displayItem = {
            image: primaryImageURL,
            id: item.id,
            product: product,
            productId: productId,
            quantity: item.quantity,
            likes: likes,
            totalPrice: displayPrice(totalPrice, this.currency.symbol, this.currency.code),
            created: created,
            closes: closes,
            moreInfo: item.moreInfo,
            images: productImageURLs
          }
          this.items.push(displayItem);
        });
      }).catch((error) => {
        this.error.flag = true;
        this.error.listings = `Error getting sale listings: ${error}`;
      });
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
     * Handler for the 'add new listing' button in the manager.
     * Pushes to the router to change the view to the add sale listing form page.
     */
    addListing() {
      this.$router.push("/business/" + this.businessId + "/add-sale-listing");
    },

    /**
     * Updates the current page to control the results in the table
     */
    changePage(page) {
      this.currentPage = page;
    },

    /**
     * Gets the current account that user acting as and sets the business ID.
     */
    getCurrentUserActAs() {
      if (this.$route.name === "ManageBusiness") {
        this.actingAs = StorageUtil.getActingAs();
        this.actingAsBusiness = this.actingAs !== StorageUtil.ACTING_AS_CURRENT_USER
        this.getBusinessId();
       } else {
        this.actingAsBusiness = true
        this.ifHide = true
        this.getBusinessId();
        this.getBusinessNameAndCountry();
      }
    },

    /**
     * Sets the current business ID that the user acting as if the user is acting as a business.
     */
    getBusinessId() {
      if (this.$route.name === "ManageBusiness") {
      if (this.actingAsBusiness === true) {
        this.businessId = this.actingAs;
      }}
      else {
        this.businessId = this.$route.params.id;
      }
    },

    /**
     * Get business name that the catalogue user is viewing.
     */
    async getBusinessNameAndCountry() {
      if(this.actingAsBusiness === true) {
        await Api.getBusiness(this.businessId).then((response) => {
              let business = response.data;
              this.businessName = business.name;
              this.businessCountry = business.address.country;
            }).catch((error) => {
          this.error.flag = true;
          this.error.message = `Error getting business country: ${error}`;
        });
      }
    },

    /**
     * Callback function to handle a row in the manager being clicked.
     * Updates the current images variable to the images of the sale listing's products.
     * Displays the 'images-modal' element.
     */
    handleRowClicked(row) {
      this.currentImages = row.images;
      this.$bvModal.show("images-modal");
    },

    /**
     * Calls format date function from module. If date is undefined, returns undefined without parsing.
     */
    formattedDate(date) {
      return date ? formatDateString(date) : undefined;
    }
  },
  computed: {
    /**
     * Computes the table name from current business's name
     * e.g. [Business name]'s Listings
     * @returns {string} to be displayed on top of table component
     */
    tableName() {
      let name = "Business";
      if (this.businessName) {
        name = this.businessName;
      }
      let tmp = "'s Listings";
      return name.concat(tmp);
    },
    /**
     * Computes the length of the items data variable.
     * More so done so the tests can run
     * @returns {number} the length of the items array
     */
    itemsLength() {
      return this.items.length;
    }
  }
}
</script>

<style scoped>
* {
  box-sizing: border-box;
}

#myListing {
  color: gray;
  max-width: 90vw;
  /*  top | right | bottom | left */
  margin: 5vh 5vw 0 5vw;
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