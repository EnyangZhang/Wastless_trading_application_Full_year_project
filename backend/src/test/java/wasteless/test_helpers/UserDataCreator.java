/**
 * This class contains methods to create test data for various model classes. Essentially the methods are constructors,
 * but only the 'relevant' attributes are inputs (e.g. email and userId) and the rest are set to defaults.
 */

package wasteless.test_helpers;

import wasteless.model.Address;
import wasteless.model.User;

import java.time.LocalDate;

public class UserDataCreator {

    /**
     * Returns a dummy address
     * @return an Address object
     */
    public static  Address createAddress() {
        return new Address("123", "Madeup Road", "Ilam", "Christchurch",
                "Canterbury", "New Zealand", "8041");
    }

    /**
     * Returns a dummy user
     * @return a User object
     */
    public static User createUser() {
        User user = new User(
                "John",
                "Hector",
                "Doe",
                "Nickname",
                "Definitely a real person",
                "johndoe@gmail.com",
                LocalDate.parse("1990-01-01"),
                "12345678",
                createAddress(),
                "password123");
        return user;
    }

    /**
     * Returns a dummy user
     * @param email email of the user
     * @param role the role of the user: {user, global_admin, default_global_admin}
     * @return a User object
     */
    public static User createUser(String email, String role) {
        User user = new User(
                "John",
                "Hector",
                "Doe",
                "Nickname",
                "Definitely a real person",
                email,
                LocalDate.parse("1990-01-01"),
                "12345678",
                createAddress(),
                "password123");
        user.setRole(role);
        return user;
    }

}
