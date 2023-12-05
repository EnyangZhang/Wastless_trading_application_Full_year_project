package wasteless.security;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * This filter extends UsernamePasswordAuthenticationFilter to allow login details in JSON body
 * rather than Form for formLogin
 */
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
  /**
   * Constructor required to set AuthenticationManager
   *
   * @param authenticationManager AuthenticationManager
   */
  public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
    this.setAuthenticationManager(authenticationManager);
    setAuthenticationSuccessHandler(new AuthLoginSuccessHandler());
  }

  /**
   * Overrides method that authenticates user
   *
   * @param request HTTP request
   * @param response HTTP response
   * @return Returns Authentication of user
   * @throws AuthenticationException Thrown in case that user cannot be authenticated.
   */
  @Override
  public Authentication attemptAuthentication(
      HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
    if (!request.getMethod().equals("POST")) {
      throw new AuthenticationServiceException(
          "Authentication method not supported: " + request.getMethod());
    }

    try {
      // Creating login details
      LoginDetails loginRequest = getLoginDetails(request);

      UsernamePasswordAuthenticationToken authRequest =
          new UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password);
      setDetails(request, authRequest);

      return this.getAuthenticationManager().authenticate(authRequest);
    } catch (Exception e) {
      logger.warn(e);
      throw new AuthenticationServiceException("Malformed request");
    }
  }

  /**
   * Gets login details from the HttpServletRequest request body.
   *
   * @param request Request to parse username and password from.
   * @return User details object containing username and password.
   * @throws IOException Thrown in case that JSON or request are invalid.
   */
  private LoginDetails getLoginDetails(HttpServletRequest request) throws IOException {
    StringBuffer buffer = new StringBuffer();
    String line;
    try {
      BufferedReader reader = request.getReader();
      // Reads JSON body
      while ((line = reader.readLine()) != null) buffer.append(line);
    } catch (Exception e) {
      logger.warn(e);
    }

    try {
      // Maps the JSON body to LoginDetails
      String jsonString = buffer.toString();
      ObjectMapper objectMapper = new ObjectMapper();
      LoginDetails loginDetails = objectMapper.readValue(jsonString, LoginDetails.class);
      return loginDetails;
    } catch (JSONException e) {
      throw new IOException("Malformed login request");
    }
  }

  /** Json object class for Jackson to use to parse JSON from requests. */
  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class LoginDetails {
    @JsonProperty("email")
    public String username;

    @JsonProperty public String password;
  }
}
