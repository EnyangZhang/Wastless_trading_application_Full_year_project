package wasteless.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@ComponentScan
public class AuthConfig extends WebSecurityConfigurerAdapter {
  @Autowired private MyUserDetailsService myUserDetailsService;

  @Bean
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    super.configure(auth);
    auth.userDetailsService(myUserDetailsService);
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable();
    http.authorizeRequests()
            .antMatchers("/businesses/**/listings", "/businesses/search", "/cards**")
            .authenticated()

            .antMatchers(HttpMethod.POST, "/businesses/**/products/**/images")
            .authenticated()
            .antMatchers(HttpMethod.PUT, "/businesses/**/products/**/images/**/makeprimary",
                    "/cards/**/extenddisplayperiod")
            .authenticated()
            .antMatchers(HttpMethod.GET,
                    "/businesses/**/products/**/images/**",
                    "/businesses/**/products/**/images/**/thumbnail",
                    "/listings/search",
                    "/notifications/**",
                    "/listings/**"
                    )
            .authenticated()
            .antMatchers(HttpMethod.PATCH, "/businesses/**/listings/**",
                    "/notifications/**/read/**"
                    )
            .authenticated()
            .antMatchers(HttpMethod.DELETE, "/businesses/**/products/**/images/**",
                    "/cards/**")
            .authenticated()

            .antMatchers("/users/**", "/login", "/businesses/**", "/h2/**")
            .permitAll()
            .and()
            .formLogin()
            .successHandler(new AuthLoginSuccessHandler())
            .and()
            .logout()
            .logoutSuccessHandler(new AuthLogoutSuccessHandler())
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);
    http.headers().frameOptions().disable();
    http.addFilterBefore(
            new CustomAuthenticationFilter(authenticationManagerBean()),
            UsernamePasswordAuthenticationFilter.class);
  }

  @Bean
  public PasswordEncoder getPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowCredentials(true);
    config.setAllowedOriginPatterns(Collections.singletonList("*"));
    config.setAllowedMethods(Collections.singletonList("*"));
    config.setAllowedHeaders(Collections.singletonList("*"));
    config.setExposedHeaders(Collections.singletonList("Total-Length"));
    source.registerCorsConfiguration("/**", config);
    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return bean;
  }
}
