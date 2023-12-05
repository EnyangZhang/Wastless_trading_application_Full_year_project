package wasteless.controller.jsonobjects;

import lombok.*;

import java.time.LocalDate;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserJson {

    private long id;

    private String firstName;

    private String lastName;

    private String middleName;

    private String nickname;

    private String bio;

    private String email;

    private LocalDate dateOfBirth;

    private String phoneNumber;

    private AddressJson homeAddress;

    private String password;

}
