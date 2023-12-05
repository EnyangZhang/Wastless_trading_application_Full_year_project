import {SERVER_URL} from "@/Api";

/**
 * Finds the primary image given a list of image objects with a boolean variable 'primary'.
 * If there are multiple, returns the first object.
 * @param images List of image objects, assumed to have a boolean variable 'primary' that when set true indicates the
 * image is a primary image.
 * @returns {*} The first item in the list filtered for primary images.
 */
export function getPrimaryImage(images) {
    return images.filter((image) => {
        return image.primary;
    })[0];
}

/**
 * Constructs an image URL from the given parameters, and appends /thumbnail if thumbnail argument is true.
 * This image URL is a request to the server for an image.
 * @param businessId Business ID of the business to get the image from.
 * @param productId Product ID of the product to get the image from.
 * @param imageId Image ID of the image to get.
 * @param thumbnail Boolean to set request to retrieve thumbnail or not.
 * @returns {string} Complete image URL.
 */
export function getImageURL(businessId, productId, imageId, thumbnail) {
    let imageURL = `${SERVER_URL}/businesses/${businessId}/products/${productId}/images/${imageId}`;
    if (thumbnail) {
        imageURL = imageURL.concat("/thumbnail");
    }
    return imageURL;
}