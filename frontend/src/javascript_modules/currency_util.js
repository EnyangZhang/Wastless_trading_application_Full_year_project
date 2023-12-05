import Api from "../Api";

/**
 * Function to send given country string to restCountriesApi to obtain country's currency information.
 * If there is an error in the process, the default currency is $USD.
 * Returns currency symbol and code as separate string variables, along with a feedback message detailing th currency,
 * where it came from, or if the defaults are being used for an unknown country.
 * @param country
 * @returns {Promise<{feedback: string, symbol: string, code: string}>}
 */
export async function getCurrency (country) {
    if (country === "United States") {
        return {
            symbol: "$",
            code: "USD",
            feedback: `Your business's home country is UNITED STATES. The currency is $USD.`
        }
    }
    const defaultCurrency = "USD";
    const defaultSymbol = "$";
    let currencyCode = defaultCurrency;
    let currencySymbol = defaultSymbol;
    const invalidFeedback = `Your business's home country is unknown. The default currency is ${defaultSymbol}${defaultCurrency}`;
    let feedback = invalidFeedback;
    try {
        let response = await Api.getCountryCurrency(country);
        if (response.status !== 404) {
            let countryInfo = response.data
            let symbol = countryInfo[0].currencies[0].symbol
            let code = countryInfo[0].currencies[0].code
            if (symbol !== null && code !== null) {
                currencyCode = code
                currencySymbol = symbol
                feedback = `Your business's home country is ${country.toUpperCase()}. The currency is ${currencySymbol}${currencyCode}.`;
            } else {
                feedback = invalidFeedback;
            }
        }
        return {
            symbol: currencySymbol,
            code: currencyCode,
            feedback: feedback
        }
    } catch {
            return {
                symbol: currencySymbol,
                code: currencyCode,
                feedback: invalidFeedback
        }
    }
}

/**
 * Takes price, currency symbol and code.
 * Returns price formatted into the string if price exists, otherwise price is 0.
 */
export function displayPrice(price, symbol, code) {
    price = price ? price : 0;
    return `${symbol}${price.toFixed(2)} ${code}`;
}