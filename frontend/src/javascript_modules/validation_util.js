/**
 *  Validation util provides a location for all validation functions to exist and be referenced
 *
 * Max Card title Length = 32
 * Max Description Length = 255
 * Max Standard input Length = 100
 *
 */

/**
 * Community Marketplace card title validator
 * Checks the length of the title is in the required range
 * @param cardTitle
 * @returns {boolean}
 */
function isCardTitleValid(cardTitle) {
    return !!(cardTitle.trim().length > 0 && cardTitle.trim().length <= 32);
}

/**
 * Standard non-required Description validator
 *
 * @param description
 * @returns
 */
function isNRDescriptionValid(description) {
    if (description.length === 0) {
        return null;
    } else {
        return description.trim().length <= 250;
    }

}

/**
 * Standard form select validation function, returns true if a section has been selected.
 * @param selection
 * @returns {boolean}
 */
function isFormSelectValid(selection) {
    return selection !== '' && selection !== null;
}

/**
 * Standard form multiselect validation function, returns true if an option has been selected.
 */
function isMultiSelectValid(selection) {
    if (selection.length === 0) {
        return null;
    } else {
        return selection.length > 0;
    }
}

// Exports functions that will be visible outside the module.
export default {

    isCardTitleValid,
    isNRDescriptionValid,
    isFormSelectValid,
    isMultiSelectValid
}