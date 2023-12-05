/**
 * Checks the validity of business name field.
 * @returns {null|boolean}
 **/
function businessNameValidation(clicked, name) {
    if (clicked === false && name.length === 0) {
        return null;
    } else {
        return name.trim().length > 0 && name.trim().length <= 100;
    }
}

/**
 * Returns the message for an invalid business name
 * @returns {string}
 */
function businessNameInvalidMessage() {
    return "Business name must be 1-100 characters long"
}

/**
 * Checks the validity of business type field.
 * @returns {string}
 **/
function businessTypeInvalidMessage() {
    return "Select a business type";
}

export default {
    businessNameValidation,
    businessNameInvalidMessage,
    businessTypeInvalidMessage
}