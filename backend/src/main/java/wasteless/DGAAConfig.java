package wasteless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import wasteless.model.Address;
import wasteless.model.User;
import wasteless.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * This class runs when the main application is started, it also contains the logic for DGAA checks
 */
@Component
@EnableScheduling
public class DGAAConfig implements ApplicationRunner {

  private static final Logger logger = LogManager.getLogger(DGAAConfig.class.getName());

  /** This is where the default DGAA username and password will be stored */
  @Value("${dgaa.user}")
  private String username;

  @Value("${dgaa.pass}")
  private String password;

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  @Autowired
  public DGAAConfig(UserRepository repository, PasswordEncoder passwordEncoder) {
    this.userRepository = repository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public void run(ApplicationArguments args) {
    logger.info("Startup application with {}", args);
    logger.info("Running startup DGAA check... ");
    this.checkDGAA();
  }

  /**
   * This method periodically calls a method to check if a DGAA is present The variable
   * dgaa.check.period.string is in application.properties
   */
  @Scheduled(
      fixedDelayString = "${dgaa.check.period.string}",
      initialDelayString = "${dgaa.check.period.string}")
  public void periodicCheck() {
    logger.info("Checking for DGAA... ");
    this.checkDGAA();
  }

  /**
   * This method returns true if a user with a DGAA role exists
   *
   * @return True if a user with DGAA role exists, otherwise false
   */
  public Boolean isDGAAPresent() {
    return this.findDGAA() != null;
  }

  /** This method checks if a user with a DGAA role exists */
  public void checkDGAA() {
    if (Boolean.FALSE.equals(this.isDGAAPresent())) {
      logger.warn("No DGAA was found during check! ");
      this.createDGAA();
    } else {
      logger.info("DGAA check pass. DGAA is present in the system");
    }
  }

  /**
   * This method creates a DGAA, it is called when is DGGA present returns false from inside the
   * periodic check
   */
  public void createDGAA() {
    logger.info("Creating DGAA now with username {}", this.username);
    String firstName = "Admin";
    String lastname = "Default Global";
    LocalDate date = LocalDate.of(1990, 3, 1);
    Address address =
        new Address("29", "Ilam Road", "Ilam", "Christchurch", "Canterbury", "New Zealand", "8053");
    User dgaa =
        new User(
            firstName,
            lastname,
            "",
            "",
            "",
            this.username,
            date,
            "",
            address,
            passwordEncoder.encode(this.password));
    dgaa.setRole("default_global_admin");

    List<User> duplicateUsers = userRepository.findAllByRole("default_global_admin");
    if (!duplicateUsers.isEmpty()) {
      logger.info("DGAA already exists");
    } else {
      try {
        dgaa.setCreated(LocalDate.now());
        userRepository.save(dgaa);
        logger.info("DGAA created");
      } catch (Exception e) {
        logger.error(String.format("Error creating DGAA: %s", e.getMessage()));
      }
    }
  }

  /**
   * This method calls the method in user repository to return the first user with the DGAA role if
   * one exists
   *
   * @return the user with the DGAA role
   */
  public User findDGAA() {
    return this.userRepository.findByRole("default_global_admin");
  }
}
