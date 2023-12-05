import {getCurrency} from "@/javascript_modules/currency_util";
import Api from "@/Api";

jest.mock("../Api");


describe("Function is called with valid country strings", () => {

    beforeAll(() => {
        Api.getCountryCurrency.mockResolvedValue(
            {
                status: 200,
                data: [{currencies: [{code: "NZD", symbol: "$"}]}]
            }
        );
    });

    test("getCurrency_validCountry_returnsObjectWithCode", async () => {
        const country = "New Zealand";
        const response = await getCurrency(country);
        expect(response.code).toBe("NZD");
    });

    test("getCurrency_validCountry_returnsObjectWithSymbol", async () => {
        const country = "New Zealand";
        const response = await getCurrency(country);
        expect(response.symbol).toBe("$")
    });

    test("getCurrency_validCountry_returnsObjectWithFeedback", async () => {
        const country = "New Zealand";
        const response = await getCurrency(country);
        expect(response.feedback).toBe(`Your business's home country is ${country.toUpperCase()}. The currency is $NZD.`);
    });
});

describe("Function is called, API returns 404", () => {

    const defaultCode = "USD";
    const defaultSymbol = "$";

    beforeAll(() => {
        Api.getCountryCurrency.mockResolvedValue(
            {
                status: 404
            }
        );
    });

    test("getCurrency_invalidCountry_returnsDefaultSymbol", async () => {
        const country = "";
        const response = await getCurrency(country);
        expect(response.symbol).toBe(defaultSymbol);
    });

    test("getCurrency_invalidCountry_returnsDefaultCode", async () => {
        const country = "";
        const response = await getCurrency(country);
        expect(response.code).toBe(defaultCode);
    });

    test("getCurrency_invalidCountry_returnsDefaultFeedback", async () => {
        const country = "";
        const response = await getCurrency(country);
        expect(response.feedback).toBe(`Your business's home country is unknown. The default currency is ${defaultSymbol}${defaultCode}`)
    });
});