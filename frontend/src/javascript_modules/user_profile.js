/**
 * Time formatter that convert date information from api to desired format.
 * @param memberDate the date from api.
 * @param includeMonthsSince if true add in brackets the number of months since the given date (defaults to true)
 * @returns {string} formatted date.
 */
export function timeFormat (memberDate, includeMonthsSince) {

    // If includeMonthsSince is undefined, set it to true
    includeMonthsSince = (typeof includeMonthsSince !== 'undefined') ?  includeMonthsSince : true

    let months = ["January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"];
    let formattedResult = '';
    let day = 2;
    let month = 1;
    let year = 0
    let processedDate = memberDate.split("-");
    let createdMonth = processedDate[month];
    processedDate[day] = parseInt(processedDate[day].slice(0, 2));
    processedDate[month] = months[parseInt(processedDate[month]) - 1];
    processedDate[year] = parseInt(processedDate[year])

    let currentDate = new Date();
    // Date.getMonth starts from 0.
    let m = currentDate.getMonth() + 1;
    let y = currentDate.getFullYear()
    let d = currentDate.getDate()
    let memberSince = (y - processedDate[year]) * 12 + m - createdMonth

    formattedResult += processedDate[day] + " " + processedDate[month] + " " + processedDate[year];

    if (includeMonthsSince) {
        formattedResult += " (" + memberSince + " months)";
    }

    //Catch unexpected date which is future date.
    if(processedDate[year] > y || processedDate[year] === y && parseInt(memberDate.split("-")[month]) > m ||
        processedDate[year] === y && parseInt(memberDate.split("-")[month]) === m && processedDate[day] > d){
        return "Unexpected date."
     }

    return formattedResult
}