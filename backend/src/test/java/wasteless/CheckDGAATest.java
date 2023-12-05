package wasteless;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import wasteless.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CheckDGAATest {

  private static final Logger log = LoggerFactory.getLogger(Main.class);

  @Autowired private UserRepository userRepository;

  @Autowired private wasteless.DGAAConfig DGAAConfig;

  @Test
  void checkDGAACreatedOnStart() {
    assertTrue(DGAAConfig.isDGAAPresent());
  }

  @Test
  void checkDGAACreatedAfterDeletion() {
    userRepository.deleteById(userRepository.findByRole("default_global_admin").getUserId());
    log.info("Deleted DGAA...");
    DGAAConfig.checkDGAA();
    assertTrue(DGAAConfig.isDGAAPresent());
  }

  @Test
  void checkNoDuplicateDGAA() {
    assertTrue(DGAAConfig.isDGAAPresent());
    log.info("Attemting to create duplicate DGAA...");
    DGAAConfig.createDGAA();
    assertEquals(1, userRepository.findAllByRole("default_global_admin").size());
  }
}
