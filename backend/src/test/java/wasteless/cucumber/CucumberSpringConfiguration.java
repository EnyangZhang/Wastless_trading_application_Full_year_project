package wasteless.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import wasteless.ObjectJSONMapper;

@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CucumberSpringConfiguration {
  @Autowired protected MockMvc mockMvc;

  @Value("${dgaa.user}")
  protected String admin_username;

  @Value("${dgaa.pass}")
  protected String admin_password;

  protected MvcResult mvcResult;
  protected ObjectJSONMapper objectJSONMapper;
  protected SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor userRequestPostProcessor = null;
}
