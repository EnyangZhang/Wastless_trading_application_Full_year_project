import validation from "../validation/business.validation"

describe("business.validation", () => {

    describe("business name validation", () => {

        test('businessNameValidation_notSubmitted_returnNull', () => {
            expect(validation.businessNameValidation(false, '')).toBe(null);
        })

        test('businessNameValidation_submittedEmptyString_returnFalse', () => {
            expect(validation.businessNameValidation(true, '')).toBe(false);
        })

        test('businessNameValidation_alphaNumericString_returnTrue', () => {
            expect(validation.businessNameValidation(true, 'ABC123#$% naïve résumé')).toBe(true);
        })

        test('businessNameValidation_alphaNumericString_returnTrue', () => {
            expect(validation.businessNameValidation(true, 'ABC123#$% naïve résumé')).toBe(true);
        })

        test('nameValidation_100characters_returnTrue', () => {
            expect(validation.businessNameValidation(true,
                'abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuv'))
                .toBe(true);
        })

        test('nameValidation_101characters_returnFalse', () => {
            expect(validation.businessNameValidation(true,
                'abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvx'))
                .toBe(false);
        })
    });
});