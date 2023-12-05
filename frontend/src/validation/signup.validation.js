const nameRegex = /[0-9\t\n./<>?;:~"`!@#$%[^&*()\]{}_+=|\\]/;

const emojiRegex = /([\u0000-\u001f]|[\u007f-\u00bf]|[\u2000-\u2bff]|[\u3000-\u301f]|\ud83c[\ud000-\udfff]|\ud83d[\ud000-\udfff]|\ud83e[\ud000-\udfff])/;



/**
 * Returns true if a name contains numbers or special characters and is not more than 100 characters. This is used to determine
 * if an error message colour should be changed to yellow (a warning) rather than red (an error)
 * @param name
 * @returns {boolean}
 */
function containsSpecialChars(name) {
    return (nameRegex.test(name) || emojiRegex.test(name)) && name.trim().length < 100
}

/**
 * Returns null if nothing has been submitted.
 * Returns true if and only if the name is completely valid (not too long AND no special characters) otherwise returns
 * false.
 * This is used to determine if any kind of message should be displayed at all. Whether the message is a warning
 * (yellow) or an error (red) is then determined by 'containsSpecialChars'.
 * @returns {null|boolean}
 */
function validName(submitted, name) {
    if (submitted === false && name.length === 0) {
        return null;
    } else {
        return !nameRegex.test(name) && !emojiRegex.test(name) && name.trim().length > 0 && name.trim().length <= 100;
    }
}

/**
 * Returns false if name is not entered or more than 100 characters
 * Returns true if name does not include numbers or special characters
 * Returns null if name does include special characters (such a name is not invalid, however we want to warn the user
 * that the name may be incorrect, so we return null to make the green tick go away from the input box)
 * @param submitted
 * @param name
 * @returns {null|boolean}
 */
function isNotInvalidName(submitted, name) {
    if (submitted === false && name.length === 0) {
        return null;
    } else if ((nameRegex.test(name) || emojiRegex.test(name)) && name.trim().length > 0 && name.trim().length <= 100) {
        return null;
    } else {
        return name.trim().length > 0 && name.trim().length <= 100;
    }
}

/**
 * Returns the correct warning/error message for the name
 * @param name
 * @returns {string}
 */
function nameMessage(name) {
    if (name.trim().length === 0) {
        return "This field is required";
    } else if (name.trim().length > 100) {
        return "Limit of 100 characters exceeded"
    } else if (nameRegex.test(name) || emojiRegex.test(name)) {
        return "Did you mean to add numbers/special characters? If you did that's ok, just press 'Sign Up'"
    } else {
        return "Invalid input"
    }
}





/**
 * Returns null if the name has not been entered.
 * Returns null if the name contains special characters or numbers but length is ok (lesson than 100 characters)
 * Returns true if the name does not contain special characters or numbers and length is ok.
 * Used to determine whether to highlight the input box green, red or nothing
 * @returns {null|boolean}
 */
function isNotInvalidOptionalName(name) {
    if (name.length === 0) {
        return null;
    } else if ((nameRegex.test(name) || emojiRegex.test(name)) && name.trim().length > 0 && name.trim().length <= 100) {
        return null;
    } else {
        return name.trim().length > 0 && name.trim().length <= 100;
    }
}


/**
 * Returns true if the name is completely valid (no numbers/special characters and less than 100 characters)
 * @param name
 * @returns {null|boolean}
 */
function validOptionalName(name) {
    if (name.length === 0) {
        return null;
    } else {
        return !nameRegex.test(name) && !emojiRegex.test(name) && name.trim().length > 0 && name.trim().length <= 100;
    }
}




/**
 * Method to validate password field.
 * Checks if the signup button has been pressed, so false is not returned straight away, causing
 * the input box isn't highlighted red when the input box is empty and the signup button hasn't been pressed.
 * @returns {null|boolean}
 */
function passwordValidation(submitted, password) {
    if (submitted === false && password.length === 0) {
        return null;
    } else if (password.length >= 8 && password.length <= 250) {
        return true;
    } else {
        return false
    }
}


function emailValidation(submitted, email) {
    if (submitted === false && email === '') {
        return null
    } else {
        if (email.trim().length > 0 && email.trim().length <= 250) {
            const regex = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3})|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            return regex.test(String(email).toLowerCase());
        } else {
            return false
        }
    }
}


/**
 * Method to validate phone number
 * Not a required field so Null is returned if field is empty
 * @returns {null|boolean}
 */
function phoneValidation(phoneNumber) {
    if (phoneNumber === null || phoneNumber === '') {
        return null
    } else if (phoneNumber.length > 20) {
        return false
    } else {
        const regex = /^\+?[0-9]{7,15}$/;
        return regex.test(phoneNumber.toString().split(' ').join(''));
    }
}


/**
 * Method to validate date of birth
 * Includes setters for boolean values which are used to set dynamic error messages depending on the users input
 * @param dateOfBirth the entered date of birth
 * @param submitted whether the form has been submitted or not
 * @param min minimum allowable date
 * @param max maximum allowable date
 * @returns {{valid: boolean, showTooYoung: boolean, showTooOld: boolean}}
 */
function dateOfBirthValidation(dateOfBirth, submitted, min, max) {
    let showTooYoung = false;
    let showTooOld = false;
    let valid;

    if (dateOfBirth) {
        dateOfBirth = new Date(dateOfBirth)
        if (dateOfBirth != null && dateOfBirth >= min && dateOfBirth <= max) {
            valid = true;
        } else if (dateOfBirth != null && dateOfBirth < min) {
            showTooOld = true;
            valid = false;
        } else if (dateOfBirth != null && dateOfBirth > max) {
            showTooYoung = true;
            valid = false;
        }
    } else {
        if (submitted === false) {
            valid = null;
        } else {
            valid = false;
        }
    }
    return {valid, showTooYoung, showTooOld}
}


/**
 * Method to check if the input in fields is too long
 * @returns {null|boolean}
 */
function lengthValidation(inputField, length) {
    if (inputField.length > length) {
        return false;
    } else if (inputField.length === 0) {
        return null;
    } else {
        return true;
    }
}


export default {
    validName,
    nameMessage,
    isNotInvalidName,
    containsSpecialChars,

    validOptionalName,
    isNotInvalidOptionalName,

    password: passwordValidation,
    email: emailValidation,
    phoneNumber: phoneValidation,
    dob: dateOfBirthValidation,
    lengthValidation,
}
