/**
 * Declare all available services here
 */
import axios from 'axios'
import storage_util from "./javascript_modules/storage_util";
import Router from "./Router";

export const SERVER_URL = process.env.VUE_APP_SERVER_ADD;
const OSM_KEY_FILTERS = "waterway|geological|barrier|boundary|highway|natural|railway"
const UNAUTHORIZED = 401;

const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
        "Access-Control-Allow-Headers": "Origin, X-Requested-With, ContentType, Accept",
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'POST, GET, OPTIONS, DELETE, PUT'
    },
    withCredentials: true
});

const instance_address = axios.create({
    baseURL: 'https://photon.komoot.io/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
        "Access-Control-Allow-Headers": "Origin, X-Requested-With, ContentType, Accept",
        'Access-Control-Allow-Origin': 'https://photon.komoot.io/api',
        'Access-Control-Allow-Methods': 'GET',
    },
    withCredentials: false
});

const instance_currency = axios.create({
    baseURL: 'https://restcountries.com/v2/name',
    timeout: 10000,
    withCredentials: false
});

// Adds interceptor to catch unauthorized responses and log the user out on the frontend.
instance.interceptors.response.use(undefined, function (err) {
    if (err.response && err.response.status === UNAUTHORIZED && storage_util.isLoggedIn()) {
        storage_util.setLoggedOut()
        Router.push({name: "Login"})
    }
    return Promise.reject(err);
})

export default {
    // ----- Users requests -----

    login: async (email, password) => {
        try {
            const response = await instance.post('login', {"email": email, "password": password})
            const user = response.data.userId;
            storage_util.setLoggedOut();
            storage_util.setLoggedIn();
            await storage_util.setCurrentUserInfo(user);
            return response;
        } catch (error) {
            return error.response;
        }
    },

    logout: () =>
        instance.post('/logout')
            .then(() => {
                storage_util.setLoggedOut();
            }),

    createUser: async (userData) => {
        try {
            const response = await instance.post('users', userData)
            const user = response.data.userId;
            storage_util.setLoggedOut();
            storage_util.setLoggedIn();
            await storage_util.setCurrentUserInfo(user);
            return response;
        } catch (error) {
            return error.response;
        }
    },

    getUser: (id) => instance.get('users/' + id),

    searchUsers: (nameQuery, pageNum, perPage, sortBy, orderBy) => instance.get('/users/search',
        {params: {searchQuery: nameQuery, pageNum: pageNum, perPage: perPage, sortBy: sortBy, orderBy: orderBy}}),


    // ----- Autocomplete requests -----
    // ----- Limit is how many results are returned

    getAutoComplete: (input) => instance_address.get('/?q=' + input
        + '&lang=en&' + 'osm_tag=!' + OSM_KEY_FILTERS + '&' + 'limit=50'),


    // ----- Business requests -----

    createBusiness: (bueData) => instance.post('businesses', bueData)
        .then(response => {
            storage_util.updateCurrentUserInfo();
            return response;
        }).catch(err => {
            return err;
        }),

    getBusinessCatalogue: (businessId) => instance.get('/businesses/' + businessId + '/products'),

    getBusiness: (businessId) => instance.get('/businesses/' + businessId),

    getCountryCurrency: (countryName) => instance_currency.get('/' + countryName),

    addProduct: (id, productData) => instance.post('businesses/' + id + '/products', productData),

    isCodeValid: (businessId, productCode) => instance.post("/business/" + businessId + "/check-product-code/" + productCode),

    modifyProductCatalogue: (businessId, productId, product) => instance.put('/businesses/' + businessId + '/products/' + productId,
        {
            "id": product.id,
            "name": product.name,
            "description": product.description,
            "manufacturer": product.manufacturer,
            "recommendedRetailPrice": product.recommendedRetailPrice
        }),

    // ----- Product Images -----
    getImage: (businessId, productId, imageId) => instance
        .get(`businesses/${businessId}/products/${productId}/images/${imageId}`,
            {responseType: "blob"}),

    getThumbnail: (businessId, productId, imageId) => instance
        .get(`businesses/${businessId}/products/${productId}/images/${imageId}/thumbnail`, {responseType: "blob"}),

    uploadImage: (businessId, productId, image) => instance
        .post(`businesses/${businessId}/products/${productId}/images`, image,
            {headers: {'Content-Type': 'multipart/form-data'}}),

    deleteImage: (businessId, productId, imageId) => instance
        .delete(`businesses/${businessId}/products/${productId}/images/${imageId}`),

    setPrimary: (businessId, productId, imageId) => instance
        .put(`businesses/${businessId}/products/${productId}/images/${imageId}/makeprimary`),

    // ----- Inventory requests -----

    getInventory: (businessId) => instance.get('businesses/' + businessId + '/inventory'),

    addInventoryItem: (businessId, inventoryItem) => instance.post('businesses/' + businessId + '/inventory',
        {
            "productId": inventoryItem.productId,
            "quantity": inventoryItem.quantity,
            "pricePerItem": inventoryItem.pricePerItem,
            "totalPrice": inventoryItem.totalPrice,
            "manufactured": inventoryItem.manufactured,
            "sellBy": inventoryItem.sellBy,
            "bestBefore": inventoryItem.bestBefore,
            "expires": inventoryItem.expires
        }),

    // ----- Business Sale item -----
    getSaleListings: (businessId, isSold) => instance.get('/businesses/' + businessId + '/listings', {params: {isSold: isSold}}),

    addSaleListing: (businessId, saleListingData) => instance.post('/businesses/' + businessId + '/listings', saleListingData),

    getSalesReport: (businessId, periodStart, periodEnd, granularity) => instance.get(
        '/businesses/' + businessId + '/sales-report?periodStart=' + periodStart + '&periodEnd=' + periodEnd + '&granularity=' + granularity),

    // ----- Search for Sale Item -----

    searchSaleListing: (searchQuery, businessType, maxPrice, minPrice, earliestClosingDate, latestClosingDate, pageNum,
                        perPage, sortBy, orderBy) => instance.get('/listings/search',
        {
            params:
                {
                    searchQuery: searchQuery,
                    businessType: businessType,
                    maxPrice: maxPrice,
                    minPrice: minPrice,
                    earliestClosingDate: earliestClosingDate,
                    latestClosingDate: latestClosingDate,
                    pageNum: pageNum,
                    perPage: perPage,
                    sortBy: sortBy,
                    orderBy: orderBy
                }
        }),

    getSaleListing: (listingId) => instance.get('/listings/' + listingId),

    // ----- Purchase Sale Item -----

    purchaseSaleListing: (businessId, saleListingId) => instance.patch('/businesses/' + businessId + '/listings/' + saleListingId),

    // ----- Like/Unlike Sale Item -----

    likeListing: (businessId, listingId) => instance.patch(`/businesses/${businessId}/listings/${listingId}/like`),

    unlikeListing: (businessId, listingId) => instance.patch(`/businesses/${businessId}/listings/${listingId}/unlike`),

    // ----- DGAA requests -----

    makeadmin: (id) => instance.put('/users/' + id + '/makeadmin'),

    revokeadmin: (id) => instance.put('/users/' + id + '/revokeadmin'),

    addAsBusinessAdmin: (businessId, targetUser) => instance.put('/businesses/' + businessId + '/makeAdministrator', targetUser),

    removeBusinessAdmin: (businessId, targetUser) => instance.put('/businesses/' + businessId + '/removeAdministrator', targetUser),

    // ----- Community Marketplace requests -----

    getCards: (section, page, size, sort, order) => instance.get('/cards', {
        params: {
            section: section,
            page: page,
            size: size,
            sort: sort,
            order: order
        }
    }),

    getUserCards: (creatorId) => instance.get('/users/' + creatorId + '/cards'),

    addMarketplaceCard: (cardData) => instance.post('/cards', {
        "creatorId": cardData.creatorId,
        "section": cardData.section,
        "title": cardData.title,
        "description": cardData.description,
        "keywordIds": cardData.keywordIds
    }),

    deleteMarketplaceCard: (cardId) => instance.delete('/cards/' + cardId),

    extendMarketplaceCard: (cardId) => instance.put('/cards/' + cardId + '/extenddisplayperiod'),

    getNotifications: (userId) => instance.get('/notifications/' + userId),

    markNotificationAsRead: (userId, notificationId) => instance.patch('/notifications/' + userId + '/read/' + notificationId),
}