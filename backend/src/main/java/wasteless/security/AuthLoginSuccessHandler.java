package wasteless.security;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Prevents backend attempting redirect on login. Both onAuthenticationSuccess functions do nothing,
 * this prevents redirection.
 */
@Configurable
@ComponentScan(basePackages = "seng302.team13.wasteless.presentation_layer.security")
public class AuthLoginSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain chain,
      Authentication authentication)
      throws IOException, ServletException {
    AuthenticationSuccessHandler.super.onAuthenticationSuccess(
        request, response, chain, authentication);
  }

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    PrintWriter writer = response.getWriter();
    writer.write("{ \"userId\": " + ((MyUserDetails) authentication.getPrincipal()).getUserId() + " }");
    writer.flush();
  }
}
