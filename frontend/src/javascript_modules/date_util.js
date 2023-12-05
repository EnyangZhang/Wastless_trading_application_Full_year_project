const months = ["January", "February", "March", "April", "May", "June",
    "July", "August", "September", "October", "November", "December"];
const shortMonths = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec"];
/**
 * Time formatter that convert date information from api to desired format.
 * @returns {string} formatted date.
 * @param created Created time from backend respond.
 */
export function formatDateString (created) {
    let formattedResult = '';
    let day = 2;
    let month = 1;
    let year = 0
    let processedDate = created.split("-");
    processedDate[day] = parseInt(processedDate[day].slice(0, 2));
    processedDate[month] = months[parseInt(processedDate[month]) - 1];
    processedDate[year] = parseInt(processedDate[year])

    formattedResult += processedDate[day] + " " + processedDate[month] + " " + processedDate[year]

    return formattedResult
}

/**
 * Time formatter that converts date time information from api to a short date and time combo string.
 * @returns {string} formatted date.
 * @param date Created time from backend respond.
 */
export function formatShortDateTimeString (date) {
    const dateObject = new Date(date);
    const day = dateObject.getDate();
    const month = shortMonths[dateObject.getMonth()];
    const year = dateObject.getFullYear();

    const hour = dateObject.getHours();
    const minutes = dateObject.getMinutes();
    return `${(hour % 12 || 12)}:${minutes.toString().padStart(2, '0')}${(hour >= 12)? "pm" : "am"} - ${day} ${month} ${year}`
}