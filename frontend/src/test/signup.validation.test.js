import validation from "../validation/signup.validation"

describe("signup.validation", () => {

    describe("name validation", () => {

        test.each([
            [true, 'Test', true],
            [false, '', null],
            [true, '', false],
            [true, 'naïve\'', true],
            [true, 'résumé', true],
            [true, '   Test Spaces   ', true],
            [true, 'ABC123', false],
            [true, 'ABC%CDE', false],
            [true, 'ABC*CDE', false],
            [true, 'ABC+CDE', false],
            [true, 'ABC~CDE', false],
            [true, 'ABC`CDE', false],
            [true, '🥺', false],
            [true, '💘', false],
            [true, '🌀', false],
            [true, 'ABC`CDE', false],
            [true, 'ABC`CDE', false],
            [true, 'অ আ কা ক & ি কী উ', false],
            [true, '张', true],
            [true, 'أَلِفْبَائِي', true],
            [true, 'hijā’ī هِجَائِي', false],
            [true, 'àèéëïĳ', true],
            [true, 'áêéèëïíîôóúû', true],
            [true, 'êôúû', true],
            [true, 'ÆØÅæøå', true],
            [true, 'ÄÖÜẞäöüß', true],
            [true, 'অ আ কা কি কী উ', true],
            [true, 'abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuv', true],
            [true, 'abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvx', false]
        ])(
            'validName_submitted=%sAndName=%s_returns%s',
            (submitted, inputName, expectedResult) => {
                expect(validation.validName(submitted, inputName)).toBe(expectedResult);
            })

        test.each([
            [true, 'Test', true],
            [false, '', null],
            [true, '', false],
            [true, 'naïve\'', true],
            [true, 'résumé', true],
            [true, '   Test Spaces   ', true],
            [true, 'ABC123', null],
            [true, 'ABC%CDE', null],
            [true, 'ABC*CDE', null],
            [true, 'ABC+CDE', null],
            [true, 'ABC~CDE', null],
            [true, 'ABC`CDE', null],
            [true, 'অ আ কা ক & ি কী উ', null],
            [true, '张', true],
            [true, 'أَلِفْبَائِي', true],
            [true, 'hijā’ī هِجَائِي', null],
            [true, 'àèéëïĳ', true],
            [true, 'áêéèëïíîôóúû', true],
            [true, 'êôúû', true],
            [true, 'ÆØÅæøå', true],
            [true, 'ÄÖÜẞäöüß', true],
            [true, 'অ আ কা কি কী উ', true],
            [true, 'abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuv', true],
            [true, 'abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvx', false]
        ])(
            'isNotValidName_submitted=%sAndName=%s_returns%s',
            (submitted, inputName, expectedResult) => {
                expect(validation.isNotInvalidName(submitted, inputName)).toBe(expectedResult);
            })

        test.each([
            ['Test', false],
            ['', false],
            ['naïve\'', false],
            ['   Test Spaces   ', false],
            ['ABC123', true],
            ['ABC%CDE', true],
            ['ABC*CDE', true],
            ['ABC+CDE', true],
            ['ABC~CDE', true],
            ['ABC`CDE', true],
            ['অ আ কা ক & ি কী উ', true],
            ['张', false],
            ['أَلِفْبَائِي', false],
            ['hijā’ī هِجَائِي', true],
            ['àèéëïĳ', false],
            ['áêéèëïíîôóúû', false],
            ['êôúû', false],
            ['ÆØÅæøå', false],
            ['ÄÖÜẞäöüß', false],
            ['অ আ কা কি কী উ', false],
            ['abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuv', false],
            ['abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvx', false],
            ['#^#^ abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvx', false]
        ])(
            'containsSpecialChars_submitted=%sAndName=%s_returns%s',
            (inputName, expectedResult) => {
                expect(validation.containsSpecialChars(inputName)).toBe(expectedResult);
            })

    });

    describe("middlename / nickname validation", () => {

        test.each([
            ['Test', true],
            ['', null],
            ['naïve\'', true],
            ['résumé', true],
            ['   Test Spaces   ', true],
            ['ABC123', false],
            ['ABC%CDE', false],
            ['ABC*CDE', false],
            ['ABC+CDE', false],
            ['ABC~CDE', false],
            ['ABC`CDE', false],
            ['অ আ কা ক & ি কী উ', false],
            ['张', true],
            ['أَلِفْبَائِي', true],
            ['hijā’ī هِجَائِي', false],
            ['àèéëïĳ', true],
            ['áêéèëïíîôóúû', true],
            ['êôúû', true],
            ['ÆØÅæøå', true],
            ['ÄÖÜẞäöüß', true],
            ['অ আ কা কি কী উ', true],
            ['abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuv', true],
            ['abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvx', false]
        ])(
            'validOptionalName_%s_returns%s',
            (input, expectedResult) => {
            expect(validation.validOptionalName(input)).toBe(expectedResult);
        })

        test.each([
            ['Test', true],
            ['', null],
            ['naïve\'', true],
            ['résumé', true],
            ['   Test Spaces   ', true],
            ['ABC123', null],
            ['ABC%CDE', null],
            ['ABC*CDE', null],
            ['ABC+CDE', null],
            ['ABC~CDE', null],
            ['ABC`CDE', null],
            ['অ আ কা ক & ি কী উ', null],
            ['张', true],
            ['أَلِفْبَائِي', true],
            ['hijā’ī هِجَائِي', null],
            ['àèéëïĳ', true],
            ['áêéèëïíîôóúû', true],
            ['êôúû', true],
            ['ÆØÅæøå', true],
            ['ÄÖÜẞäöüß', true],
            ['অ আ কা কি কী উ', true],
            ['abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuv', true],
            ['abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvx', false]
        ])(
            'isNotInvalidOptionalName_%s_returns%s',
            (input, expectedResult) => {
            expect(validation.isNotInvalidOptionalName(input)).toBe(expectedResult);
        })
    });

    describe("password validation", () => {

        test('passwordValidation_notSubmitted_returnNull', () => {
            expect(validation.password(false, '')).toBe(null);
        })

        test('passwordValidation_noInputSubmitted_returnFalse', () => {
            expect(validation.password(true, '')).toBe(false);
        })

        test('passwordValidation_7charactersSubmitted_returnFalse', () => {
            expect(validation.password(true, '1234567')).toBe(false);
        })

        test('passwordValidation_8charactersSubmitted_returnTrue', () => {
            expect(validation.password(true, '12345678')).toBe(true);
        })
    });

    describe('email validation', () => {

        test('emailValidation_noInputNotSumbitted_returnNull', () => {
            expect(validation.email(false, '')).toBe(null);
        })

        test('emailValidation_noInputSumbitted_returnFalse', () => {
            expect(validation.email(true, '')).toBe(false);
        })

        test('emailValidation_no@_returnFalse', () => {
            expect(validation.email(true, 'abc.com')).toBe(false);
        })

        test('emailValidation_no.after@_returnFalse', () => {
            expect(validation.email(true, 'abc@gmail')).toBe(false);
        })

        test('emailValidation_nothingBefore@_returnFalse', () => {
            expect(validation.email(true, '@gmail.com')).toBe(false);
        })

        test('emailValidation_validEmail_returnTrue', () => {
            expect(validation.email(true, 'test@gmail.com')).toBe(true);
        })
    });

    describe('phone number validation', () => {

        test('phoneNumberValidation_noInput_returnNull', () => {
            expect(validation.phoneNumber('')).toBe(null);
        })

        test('phoneNumberValidation_6Numbers_returnFalse', () => {
            expect(validation.phoneNumber('123456')).toBe(false);
        })

        test('phoneNumberValidation_7Numbers_returnTrue', () => {
            expect(validation.phoneNumber('1234567')).toBe(true);
        })

        test('phoneNumberValidation_15Numbers_returnTrue', () => {
            expect(validation.phoneNumber('123456789123456')).toBe(true);
        })

        test('phoneNumberValidation_16Numbers_returnFalse', () => {
            expect(validation.phoneNumber('1234567891234567')).toBe(false);
        })

        test('phoneNumberValidation_startsWithPlus_returnTrue', () => {
            expect(validation.phoneNumber('+123456789123456')).toBe(true);
        })

        test('phoneNumberValidation_plusNotAtStart_returnFalse', () => {
            expect(validation.phoneNumber('123+89123456')).toBe(false);
        })

        test('phoneNumberValidation_includesSpaces_returnTrue', () => {
            expect(validation.phoneNumber('123 456 7891 234 56')).toBe(true);
        })

        test('phoneNumberValidation_startsWith#_returnFalse', () => {
            expect(validation.phoneNumber('#123456789123456')).toBe(false);
        })

        test('phoneNumberValidation_containsLetters_returnFalse', () => {
            expect(validation.phoneNumber('123ABC123')).toBe(false);
        })
    })

    describe('date of birth validation', () => {

        test('dateOfBirthValidation_notInput_returnsNull', () => {
            expect(validation.dob('', false, new Date('1/1/1900'), new Date('1/1/2012')))
                .toStrictEqual({"showTooOld": false, "showTooYoung": false, "valid": null});
        })

        test('dateOfBirthValidation_aboveMax_returnsFalse', () => {
            expect(validation.dob('2/1/2012', true, new Date('1/1/1900'), new Date('1/1/2012')))
                .toStrictEqual({"showTooOld": false, "showTooYoung": true, "valid": false});
        })

        test('dateOfBirthValidation_belowMin_returnsFalse', () => {
            expect(validation.dob('1/1/1900', true,  new Date('2/1/1900'), new Date('1/1/2012')))
                .toStrictEqual({"showTooOld": true, "showTooYoung": false, "valid": false});
        })

        test('dateOfBirthValidation_lowerLimit_returnsTrue', () => {
            expect(validation.dob('1/1/1900', true,  new Date('1/1/1900'), new Date('1/1/2012')))
                .toStrictEqual({"showTooOld": false, "showTooYoung": false, "valid": true});
        })

        test('dateOfBirthValidation_upperLimit_returnsFalse', () => {
            expect(validation.dob('1/1/1900', true,  new Date('1/1/1900'), new Date('1/1/2012')))
                .toStrictEqual({"showTooOld": false, "showTooYoung": false, "valid": true});
        })
    })

    describe('bio validation', () => {

        test('bioValidation_empty_returnNull', () => {
            expect(validation.lengthValidation('')).toBe(null)
        })

        test('bioValidation_500characters_returnTrue', () => {
            expect(validation.lengthValidation(
                'D7aMRMbuevumc@zrYWtKbm9xQNiaRThSv73jFB2KTpQjlmJzhr0HbShXG6y4A2iG4Hb8t1y2kBzvO0NkylSaN0a9K' +
                'FgM7ttGISp2X72SWmRui11ic86O4s8b 7QUNp6ziTeeZ7 UVMSwn77QrI4GvmVSyhF9d2LSiH77NqhgDG50JmYrbUfP0swT7fg' +
                'Gzqz0NgGBQdcfA22ds UO3ZrnucCRhzS2CGsIygOzjLRkQjiGk0K oz6suXf7rWZ4oPVE3Bu4FdXabzqhUyeEB2HrAXrRIEiDX' +
                'xWB4RdXRswxeZd64F6WJxFVXzlTZ7Pj1GuenkkF6y3UTibkcp89oER8PERsxzSCq55H3KIATj8cHgaoNarz1l7pyNIrNXjHxdo' +
                'jUiOdaHtb4XQT9uhgnuM3hrB1%NChpTe8wetTyeKELkf0YyjWTbZBKYROLbex8mwnWd6NUjcZeyaee7TDsK6amVjumbkyUGZNN' +
                'F00bYum25yTcBL2kmbW', 500
            )).toBe(true)
        })

        test('bioValidation_501characters_returnFalse', () => {
            expect(validation.lengthValidation(
                'D7aMRMbuevumcGzrYWtKbm9xQNiaRThSv73jFB2KTpQjlmJzhr0HbShXG6y4A2iG4Hb8t1y2kBzvO0NkylSaN0a9K' +
                'FgM7ttGISp2X72SWmRui11ic86O4s8bR7QUNp6ziTeeZ7DUVMSwn77QrI4GvmVSyhF9d2LSiH77NqhgDG50JmYrbUfP0swT7fg' +
                'Gzqz0NgGBQdcfA22ds1UO3ZrnucCRhzS2CGsIygOzjLRkQjiGk0KGoz6suXf7rWZ4oPVE3Bu4FdXabzqhUyeEB2HrAXrRIEiDX' +
                'xWB4RdXRswxeZd64F6WJxFVXzlTZ7Pj1GuenkkF6y3UTibkcp89oER8PERsxzSCq55H3KIATj8cHgaoNarz1l7pyNIrNXjHxdo' +
                'jUiOdaHtb4XQT9uhgnuM3hrB1yNChpTe8wetTyeKELkf0YyjWTbZBKYROLbex8mwnWd6NUjcZeyaee7TDsK6amVjumbkyUGZNN' +
                'F00bYum25yTcBL2kmbWa', 500
            )).toBe(false)
        })
    })

});