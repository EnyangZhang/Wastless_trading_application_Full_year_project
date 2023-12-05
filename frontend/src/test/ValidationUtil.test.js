import validation_util from "../javascript_modules/validation_util";

describe('Community marketplace card title validator test', () => {
    test('isCardTitleValid_emptyString_returnsFalse', () => {
        let cardTitle = '';
        expect(validation_util.isCardTitleValid(cardTitle)).toBeFalsy();
    })

    test('isCardTitleValid_validString(length=1)_returnsTrue', () => {
        let cardTitle = 'A';
        expect(validation_util.isCardTitleValid(cardTitle)).toBeTruthy();
    })

    test('isCardTitleValid_validString(length=32)_returnsTrue', () => {
        let cardTitle = 'A'.repeat(32);
        expect(validation_util.isCardTitleValid(cardTitle)).toBeTruthy();
    })

    test('isCardTitleValid_invalidString(length=33)_returnsFalse', () => {
        let cardTitle = 'A'.repeat(33);
        expect(validation_util.isCardTitleValid(cardTitle)).toBeFalsy();
    })

    test('isCardTitleValid_invalidString(length=500)_returnsFalse', () => {
        let cardTitle = 'A'.repeat(500);
        expect(validation_util.isCardTitleValid(cardTitle)).toBeFalsy();
    })
    test('isCardTitleValid_invalidString("          ")_returnsFalse', () => {
        let cardTitle = ' '.repeat(10)
        expect(validation_util.isCardTitleValid(cardTitle)).toBeFalsy();
    })
})

describe('Non-required description validation testing', () => {
    test('isNRDescriptionValid_emptyString_returnsFalse', () => {
        let description = '';
        expect(validation_util.isNRDescriptionValid(description)).toBeNull();
    })

    test('isNRDescriptionValid_validString(length=1)_returnsTrue', () => {
        let description = 'A';
        expect(validation_util.isNRDescriptionValid(description)).toBeTruthy();
    })

    test('isNRDescriptionValid_validString(length=250)_returnsTrue', () => {
        let description = 'A'.repeat(250);
        expect(validation_util.isNRDescriptionValid(description)).toBeTruthy();
    })

    test('isNRDescriptionValid_invalidString(length=251)_returnsFalse', () => {
        let description = 'A'.repeat(251);
        expect(validation_util.isNRDescriptionValid(description)).toBeFalsy();
    })

    test('isNrDescriptionValid_invalidString(length=500)_returnsFalse', () => {
        let description = 'A'.repeat(500);
        expect(validation_util.isNRDescriptionValid(description)).toBeFalsy();
    })
})

describe('Form select validation testing', () => {
    test('isFormSelectValid_nullValue_returnsFalse', () => {
        let selection = null;
        expect(validation_util.isFormSelectValid(selection)).toBeFalsy();
    })

    test('isFormSelectValid_emptyString_returnsFalse', () => {
        let selection = '';
        expect(validation_util.isFormSelectValid(selection)).toBeFalsy();
    })

    test('isFormSelectValid_nonEmptyString_returnsTrue', () => {
        let selection = 'You selected me';
        expect(validation_util.isFormSelectValid(selection)).toBeTruthy();
    })

    test('isFormSelectValid_emptyDictionary_returnsTrue', () => {
        let selection = {};
        expect(validation_util.isFormSelectValid(selection)).toBeTruthy();
    })

    test('isFormSelectValid_integer0_returnsTrue', ()  => {
        let selection = 0;
        expect(validation_util.isFormSelectValid(selection)).toBeTruthy();
    })

    test('isFormSelectValid_randomPositiveInteger_returnsTrue', ()  => {
        let selection = 27;
        expect(validation_util.isFormSelectValid(selection)).toBeTruthy();
    })
})

describe('MultiSelect validation testing', () => {
    test('isMultiSelectValid_emptyList_returnsNull', () => {
        let selectionList = [];
        expect(validation_util.isMultiSelectValid(selectionList)).toBeNull();
    })

    test('isMultiSelectValid_nonEmptyList_returnsTrue', () => {
        let selectionList = [1, 2, 3, 4, 5]
        expect(validation_util.isMultiSelectValid(selectionList)).toBeTruthy();
    })

    test('isMultiSelectValid_emptyString_returnsNull', () => {
        let multiSelectString = ''
        expect(validation_util.isMultiSelectValid(multiSelectString)).toBeNull();
    })

    test('isMultiSelectValid_nonEmptyString_returnsTrue', () => {
        let multiSelectString = 'MultiSelect'
        expect(validation_util.isMultiSelectValid(multiSelectString)).toBeTruthy();
    })

    test('isMultiSelectValid_integer_returnsFalse', () => {
        let multiSelectInteger = 42;
        expect(validation_util.isMultiSelectValid(multiSelectInteger)).toBeFalsy();
    })

})