package wasteless.controller.jsonobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AddressJson {

    private String streetNumber;

    private String streetName;

    private String suburb;

    private String city;

    private String region;

    private String country;

    private String postcode;

}
