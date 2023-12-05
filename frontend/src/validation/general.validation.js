/**
 * This function validates a dropdown / combo-box. Options: null (hasn't been touched yet), false (invalid), true (valid)
 * @param {boolean} clicked whether or not the drop down has been clicked
 * @param dropdownVariable the variable modelling the current dropdown selection
 * @returns {null|boolean}
 */
function validDropdown(clicked, dropdownVariable) {
    if (clicked === false && dropdownVariable === null) {
        return null;
    } else if (clicked === false && dropdownVariable !== null) {
        return true;
    } else {
        return !(clicked === true && dropdownVariable === null);
    }
}

/**
 * Take a substring of the string if it's over 20 characters long
 * @param string
 * @return possibly trimmed string
 */
function truncateString(string) {
    if (string.length > 20) {
        return string.substring(0, 20) + "...";
    }
    return string;
}

export default {
    validDropdown,
    truncateString
}