import {formatDateString} from "@/javascript_modules/date_util"; // name of your Vue component
/**
 * Testing the timeFormat method which is used in the UserProfile and SearchUser components.
 */

describe('Time formatter', () => {
    test('Does time formatter formats the time correctly(past)', () => {
        const memberDate = "2020-07-14T14:32:00Z"
        const output = "14 July 2020"
        expect(formatDateString(memberDate)).toBe(output);
    });

});



