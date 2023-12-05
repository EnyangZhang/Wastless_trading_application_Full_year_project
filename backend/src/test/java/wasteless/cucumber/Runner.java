package wasteless.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import wasteless.Main;

@RunWith(Cucumber.class)
@SpringBootTest
@ContextConfiguration(classes = Main.class)
@CucumberOptions(
    features = {"src/test/resources/cucumber"},
    plugin = {"pretty"},
    tags = "not @Skip")
public class Runner {}
