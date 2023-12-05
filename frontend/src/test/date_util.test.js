import {formatShortDateTimeString} from "@/javascript_modules/date_util";

describe("Date Time format", () => {
    test('Date time formatter returns correct format for pm', () => {
        const date = "2021-09-26T18:42:40"
        const expected = "6:42pm - 26 Sept 2021"
        expect(formatShortDateTimeString(date)).toBe(expected);
    })

    test('Date time formatter returns correct format for am', () => {
        const date = "2021-09-26T06:42:40"
        const expected = "6:42am - 26 Sept 2021"
        expect(formatShortDateTimeString(date)).toBe(expected);
    })

    test('Date time formatter returns correct format for close to am', () => {
        const date = "2021-09-26T23:59:59"
        const expected = "11:59pm - 26 Sept 2021"
        expect(formatShortDateTimeString(date)).toBe(expected);
    })

    test('Date time formatter returns correct format for close to pm', () => {
        const date = "2021-09-26T11:59:59"
        const expected = "11:59am - 26 Sept 2021"
        expect(formatShortDateTimeString(date)).toBe(expected);
    })

    test('Date time formatter returns correct format for 0 minutes', () => {
        const date = "2021-09-26T11:00:00"
        const expected = "11:00am - 26 Sept 2021"
        expect(formatShortDateTimeString(date)).toBe(expected);
    })

    test('Date time formatter returns correct format for 0 minutes', () => {
        const date = "2021-09-26T12:15:00"
        const expected = "12:15pm - 26 Sept 2021"
        expect(formatShortDateTimeString(date)).toBe(expected);
    })

    test('Date time formatter returns correct format for 0 minutes', () => {
        const date = "2021-09-26T00:15:00"
        const expected = "12:15am - 26 Sept 2021"
        expect(formatShortDateTimeString(date)).toBe(expected);
    })
})