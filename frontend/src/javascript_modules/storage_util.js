/**
 * StorageUtil provides controlled access to localstorge variables used
 * for storing the application state.
 */

import Api from "../Api";

// Event constant strings
const UPDATED_USER_DETAILS_EVENT = "UPDATED_USER_DETAILS_EVENT";
const SWITCHED_ACCOUNT_EVENT = "SWITCHED_ACCOUNT_EVENT"

// Constant strings
const ACTING_AS_CURRENT_USER = "ACTING_AS_CURRENT_USER";

/**
 * Triggers UPDATED_USER_DETAILS_EVENT event to notify to update fields that rely
 * on user details cache.
 */
function triggerUpdatedDetailsEvent() {
    if (isLoggedIn()) {
        const event = new Event(UPDATED_USER_DETAILS_EVENT);
        window.dispatchEvent(event);
    }
}

/**
 * Triggers SWITCHED_ACCOUNT_EVENT event to notify to the user switched account.
 */
function triggerSwitchedAccountEvent() {
    if (isLoggedIn()) {
        const event = new Event(SWITCHED_ACCOUNT_EVENT);
        window.dispatchEvent(event);
    }
}

/**
 * Sets the current user cache.
 * @param user The user to set the cached details to. This should be the currently logged in user.
 */
async function setCurrentUserInfo(user) {
    try {
        const response = await Api.getUser(user)
        localStorage.setItem("loggedInUser", JSON.stringify(response.data));
        if (getActingAs() == null) {
            setActingAs(ACTING_AS_CURRENT_USER)
            triggerSwitchedAccountEvent()
        }
        triggerUpdatedDetailsEvent();
    } catch {
        // Do nothing
    }
}


/**
 * Reloads the user details cache. This should be called when a making a POST or PUT
 * request to ensure that the cache is in sync.
 */
function updateCurrentUserInfo() {
    setCurrentUserInfo(getCurrentUser());
}

/**
 * Gets the currently logged in user.
 * @returns {int} The ID of the currently logged in user.
 */
function getCurrentUser() {
    return JSON.parse(localStorage.getItem("loggedInUser")).id;
}

/**
 * Gets the current user cache.
 * @returns {object} user object.
 */
function getCurrentUserInfo() {
    return JSON.parse(localStorage.getItem("loggedInUser"));
}

/**
 * Gets businesses owned by current cached user.
 *
 * @returns {{}} Dictionary of businesses
 * the key of the dictionary is ID and the value is the business object.
 */
function getAvailableBusinesses() {
    let businesses = JSON.parse(localStorage.getItem("loggedInUser")).businessesAdministered;
    let businessList = {};
    for (let i = 0; i < businesses.length; i++) {
        businessList[businesses[i].businessId] = businesses[i];
    }
    return businessList;
}

/**
 * Sets which account the user is currently acting as.
 * @param account the business ID of the business the user is acting as or null if acting as current user.
 */
function setActingAs(account) {
    if (account == null) {
        localStorage.setItem("actingAs", ACTING_AS_CURRENT_USER)
    } else {
        localStorage.setItem("actingAs", account)
    }
    triggerSwitchedAccountEvent()
}

/**
 * Returns what account the user is acting as.
 * @returns {string}
 */
function getActingAs() {
    return localStorage.getItem("actingAs")
}

/**
 * Sets the application state to logged in.
 */
function setLoggedIn() {
    localStorage.setItem("loggedIn", "true");
}

/**
 * Sets the application state to logged out and clears user cache.
 */
function setLoggedOut() {
    localStorage.setItem("loggedIn", "false");
    localStorage.removeItem("loggedInUser");
    localStorage.removeItem("actingAs");
    triggerUpdatedDetailsEvent();
}

/**
 * Returns whether the application state is logged in.
 * @returns {boolean}
 */
function isLoggedIn() {
        return localStorage.getItem("loggedIn") === "true";
}

// Exports functions that will be visible outside the module.
export default {
    // Available constants
    UPDATED_USER_DETAILS_EVENT,
    ACTING_AS_CURRENT_USER,
    SWITCHED_ACCOUNT_EVENT,

    // Available functions
    setCurrentUserInfo,
    updateCurrentUserInfo,
    getCurrentUser,
    getCurrentUserInfo,

    getAvailableBusinesses,

    setActingAs,
    getActingAs,

    setLoggedIn,
    setLoggedOut,
    isLoggedIn
};