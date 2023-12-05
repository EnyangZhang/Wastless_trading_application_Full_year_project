package wasteless.security;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import wasteless.model.User;
import wasteless.repository.UserRepository;

/** Service to allow Spring Security to interface with database repositories. */
@Service
public class MyUserDetailsService implements UserDetailsService {
  //    @Autowired
  //    private UserService userService;

  @Autowired private UserRepository userRepository;
  /**
   * Loads User as seng302.team13.wasteless.presentation_layer.security.MyUserDetails for Spring
   * Security.
   *
   * @param email The email of the user to be loaded.
   * @return UserDetails of the user that gets loaded
   * @throws UsernameNotFoundException Throws in case that user does not exist.
   */
  @SneakyThrows
  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(
                () -> new UsernameNotFoundException("User with given email does not exist"));
    return new MyUserDetails(user);
  }
}
