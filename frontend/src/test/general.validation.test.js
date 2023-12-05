import validation from "../validation/general.validation"

describe("general.validation", () => {

    describe("drop down validation", () => {

        test('generalValidation_notSubmitted_returnNull', () => {
            expect(validation.validDropdown(false, null)).toBe(null);
        })

        test('generalValidation_submittedNothing_returnFalse', () => {
            expect(validation.validDropdown(true, null)).toBe(false);
        })

        test('generalValidation_submitted_returnTrue', () => {
            expect(validation.validDropdown(true, 'option')).toBe(true);
        })
    });

    describe("product name too long stripper", () => {

        test('truncateString_withAcceptableLength_doesNotGetStripped', () => {
            expect(validation.truncateString('product')).toBe('product')
        })

        test('truncateString_withMaxLength_doesNotGetStripped', () => {
            expect(validation.truncateString('amuchsuperiorproduct')).toBe('amuchsuperiorproduct')
        })

        test('truncateString_withOneOverMaxLength_getsStripped', () => {
            expect(validation.truncateString('coolersuperiorproduct')).toBe('coolersuperiorproduc...')
        })

        test('truncateString_withFarOverMaxLength_getsStripped', () => {
            expect(validation.truncateString('' +
                'wowthisproductisjustsoepicyoujustknowyouwanttobuymesobadly'))
                .toBe('wowthisproductisjust...')
        })

        test('truncateString_emptyName_doesNotGetEclipsed' , () => {
            expect(validation.truncateString('' +
                ''))
                .toBe('')
        })
    })
});