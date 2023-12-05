package wasteless.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import wasteless.model.User;
import wasteless.repository.UserRepository;

import java.util.Arrays;
import java.util.Optional;

/** Authorization util class using JWT */
@Component
public class AuthUtil {
  @Autowired MyUserDetailsService myUserDetailsService;
  @Autowired AuthenticationManager authenticationManager;
  @Autowired private UserRepository userRepository;

  /**
   * Authenticates user given username and password
   *
   * @param email The users email
   * @param password The users password
   */
  public void authenticate(String email, String password) {
    try {
      UserDetails person = myUserDetailsService.loadUserByUsername(email);
      SecurityContextHolder.getContext()
          .setAuthentication(
              new UsernamePasswordAuthenticationToken(person, password, person.getAuthorities()));
    } catch (Exception e) {
      throw new ResponseStatusException(
          HttpStatus.UNAUTHORIZED, "Failed login attempt, email or password incorrect");
    }
  }

  /**
   * Gets current user from SecurityContext
   *
   * @return Current user. Null if not found or not authenticated.
   */
  public User getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return getUserFromAuthentication(authentication);
  }

  public boolean isCurrentUserGlobalAdmin() {
    return Arrays.asList("global_admin", "default_global_admin").contains(getCurrentUser().getRole());
  }

  /**
   * Gets current user given Authentication
   *
   * @param authentication Authentication to retrieve user from
   * @return User from authentication. Null if not found or not authenticated.
   * @throws ResponseStatusException 401 if user does not exist, no authentication token or invalid
   *     token
   */
  public User getUserFromAuthentication(Authentication authentication)
      throws ResponseStatusException {
    if (authentication != null) {
      Object principal = authentication.getPrincipal();
      if (principal instanceof UserDetails) {
        String username = ((UserDetails) principal).getUsername();
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
          return user.get();
        }
      }
    }
    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
  }
}
