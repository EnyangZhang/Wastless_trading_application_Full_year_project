import {isAdmin} from "../javascript_modules/search_users"

describe('isAdminTest', () => {
    test('Does the isAdmin return yes for GAA', () => {
        let role = "global_admin"
        expect(isAdmin(role)).toBe("Yes")
    })

    test('Does the isAdmin return yes for DGAA', () => {
        let role = "default_global_admin"
        expect(isAdmin(role)).toBe("Yes")
    })

    test('Does the isAdmin return no for user', () => {
        let role = "user"
        expect(isAdmin(role)).toBe("No")
    })

    test('Does the isAdmin return invalid user role for an invalid role', () => {
        let role = "some_invalid_role"
        try {
            isAdmin(role)
            // Fail test if above expression doesn't throw anything.
            expect(true).toBe(false);
        } catch (e) {
            expect(e.message).toBe("Invalid user role");
        }
    })
})