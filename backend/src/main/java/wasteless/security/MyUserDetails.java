package wasteless.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import wasteless.model.User;

import java.util.Collection;

/** MyUserDetails stores user information to interface with Spring Security */
public class MyUserDetails implements UserDetails {
  private String username;
  private String password;
  private long userId;

  /**
   * Constructor for setting user information.
   *
   * @param user The User this object will represent.
   */
  public MyUserDetails(User user) {
    this.username = user.getEmail();
    this.password = user.getPassword();
    this.userId = user.getUserId();
  }

  public MyUserDetails() {}

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  public long getUserId() {
    return this.userId;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
