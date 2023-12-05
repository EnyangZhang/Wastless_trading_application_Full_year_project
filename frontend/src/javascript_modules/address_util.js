/**
 * Extracts details into an ordered list, removes null or undefined details and returns a string of details separated
 * by comma.
 *
 * takes an additional parameter fullAddress that displays a reduced details with only city, region and country if it
 * is false.
 *
 * @param address object
 * @param fullAddress
 * @returns {string}
 */
export function formatAddress(address, fullAddress) {
    let details;

    if (fullAddress) {
        details = [
            address.streetNumber,
            address.streetName,
            address.suburb,
            address.city,
            address.postcode,
            address.region,
            address.country
        ];
    } else {
        details = [
            address.city,
            address.region,
            address.country
        ];
    }

    details = details.filter((detail) => {
        return detail;
    });

    return details.join(", ");
}