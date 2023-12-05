package wasteless.controller.userController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@Sql(scripts = {"classpath:/testData/CreateDBTables.sql", "classpath:/testData/CreateUserData.sql"})
@AutoConfigureMockMvc
public class UserControllerMethodTest {

  protected String user;
  protected String firstName,
      middleName,
      lastName,
      nickName,
      bio,
      email,
      dateOfBirth,
      phoneNumber,
      password;
  protected String streetNumber, streetName, suburb, city, region, country, postcode;

  @Autowired protected MockMvc mockMvc;
  protected MvcResult mvcResult;

  public String constructTestUser() {
    return String.format(
        "{\n"
            + "  \"firstName\": %s,"
            + "  \"lastName\": %s,"
            + "  \"middleName\": %s,"
            + "  \"nickname\": %s,"
            + "  \"bio\": %s,"
            + "  \"email\": %s,"
            + "  \"dateOfBirth\": %s,"
            + "  \"phoneNumber\": %s,"
            + "  \"homeAddress\": {"
            + "    \"streetNumber\": %s,"
            + "    \"streetName\": %s,"
            + "    \"suburb\": %s,"
            + "    \"city\": %s,"
            + "    \"region\": %s,"
            + "    \"country\": %s,"
            + "    \"postcode\": %s"
            + "  },"
            + "  \"password\": %s"
            + "}",
        firstName,
        lastName,
        middleName,
        nickName,
        bio,
        email,
        dateOfBirth,
        phoneNumber,
        streetNumber,
        streetName,
        suburb,
        city,
        region,
        country,
        postcode,
        password);
  }
}
