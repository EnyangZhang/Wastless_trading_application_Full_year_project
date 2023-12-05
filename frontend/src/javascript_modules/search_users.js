/**
 * This function will return 'Yes' if the user's role is an admin or 'No' if the user is a standard user. If the user's
 * role is some other value, then an error will be thrown.
 *
 * @param role  the role attribute of a given user
 * @returns {string}  Either 'Yes' or 'No'
 */
export function isAdmin (role) {
    if (role === 'global_admin' || role === 'default_global_admin') {
        return "Yes";
    } else if (role === 'user') {
        return "No";
    } else {
        throw Error("Invalid user role");
    }
}