import { countries } from "../../public/country_names/country_names";
const addressRegex = /[\t\n./<>?;:~"`!@#$%[^&*()\]{}_+=|\\]/;
const emojiRegex = /(\u00a9|\u00ae|[\u2000-\u3300]|\ud83c[\ud000-\udfff]|\ud83d[\ud000-\udfff]|\ud83e[\ud000-\udfff])/;


/**
 * Checks the validity of Street Number field.
 * @returns {null|boolean}
 **/
function streetNumberValidation(clicked, streetNumber) {
    if (clicked === false && streetNumber.length === 0) {
        return null;
    } else {
        const regex = /^([0-9]+[/|-])?([0-9]+[A-Za-z]?)$/;
        return regex.test(String(streetNumber).toLowerCase()) && String(streetNumber).length <= 50;
    }
}

/**
 * Returns the invalid message for street number
 * @returns {string}
 */
function streetNumberInvalidMessage() {
    return "Invalid street number. The following formats are acceptable: 12, 1/12, 2|12, 2-12, or 12A (no more than 50 characters)"
}

/**
 * Checks the validity of street name field.
 * @returns {null|boolean}
 **/
function streetNameValidation(clicked, streetName) {
    if (clicked === false && streetName.length === 0) {
        return null;
    } else {
        return streetName.trim().length > 0 && streetName.trim().length <= 50;
    }
}

/**
 * Returns the invalid message for street name
 * @returns {string}
 */
function streetNameInvalidMessage() {
    return "Invalid street name. Must be 1-50 characters"
}

/**
 * Returns true if a name contains numbers or special characters and is not more than 100 characters. This is used to determine
 * if an error message colour should be changed to yellow (a warning) rather than red (an error)
 * @param field
 * @returns {boolean}
 */
function containsSpecialChars(field) {
    return (addressRegex.test(field) || emojiRegex.test(field)) && field.trim().length < 100
}

/**
 * Returns null if nothing has been submitted.
 * Returns true if and only if the field is completely valid (not too long AND no special characters) otherwise returns
 * false.
 * This is used to determine if any kind of message should be displayed at all. Whether the message is a warning
 * (yellow) or an error (red) is then determined by 'containsSpecialChars'.
 * @returns {null|boolean}
 */
function validField(submitted, field) {
    if (submitted === false && field.length === 0) {
        return null;
    } else {
        return !addressRegex.test(field) && !emojiRegex.test(field) && field.trim().length > 0 && field.trim().length <= 100;
    }
}

/**
 * Returns false if field is not entered or more than 100 characters
 * Returns true if field does not include numbers or special characters
 * Returns null if field does include special characters (such a name is not invalid, however we want to warn the user
 * that the name may be incorrect, so we return null to make the green tick go away from the input box)
 * @param submitted
 * @param field
 * @returns {null|boolean}
 */
function isNotInvalidField(submitted, field) {
    if (submitted === false && field.length === 0) {
        return null;
    } else if ((addressRegex.test(field) || emojiRegex.test(field)) && field.trim().length > 0 && field.trim().length <= 100) {
        return null;
    } else {
        return field.trim().length > 0 && field.trim().length <= 100;
    }
}


/**
 * Returns the invalid message for an address field
 * @param {string} fieldName the name of the field that is invalid
 * @param field
 * @returns {string}
 */
function fieldMessage(fieldName, field) {
    if ((addressRegex.test(field) || emojiRegex.test(field)) && field.trim().length > 0 && field.trim().length <= 100) {
        return "Did you mean to add special characters? If you did that's ok, you can still continue"
    } else if (field.trim().length === 0) {
        return `${fieldName} is required`;
    } else if (field.trim().length > 100) {
        return "Limit of 100 characters exceeded"
    }}


/**
 * Checks the validity of postcode
 *
 * @param clicked whether or not the field has been clicked
 * @param postcode takes the model of the input field as a param
 * @returns {null|boolean}
 */
function postcodeValidation(clicked, postcode) {
    if (clicked === false && postcode === '') {
        return null;
    } else if (postcode.trim() === '') {
        return false;
    } else if (postcode.length > 20) {
        return false;
    } else {
        const regex = /^[A-Za-z0-9\-,\s]*$/;
        return regex.test(String(postcode).toLowerCase()) && postcode.trim().length > 0;
    }
}

/**
 * Returns the invalid message for postcode
 * @returns {string}
 */
function postcodeInvalidMessage() {
    return `Invalid postcode. Must be 1-20 alphanumeric characters (space, comma, and dash are also allowed)`;
}

function countryValidation(clicked, input_country) {
    if (clicked === false && input_country === '') {
        return null;
    } else if (clicked === true && input_country.trim() === '') {
        return false;
    } else {
        for (let country of countries) {
            if (country.toLowerCase() === input_country.toLowerCase()) {
                return true;
            }
        }
        return false;
    }
}

function countryMessage() {
    return "Invalid country name. Try using the address autocomplete box."
}

export default {
    streetNumber: streetNumberValidation,
    streetNumberMessage: streetNumberInvalidMessage,
    streetName: streetNameValidation,
    streetNameMessage: streetNameInvalidMessage,

    validField,
    isNotInvalidField,
    containsSpecialChars,
    fieldMessage,

    postcode: postcodeValidation,
    postcodeMessage: postcodeInvalidMessage,
    countryValidation,
    countryMessage,
}