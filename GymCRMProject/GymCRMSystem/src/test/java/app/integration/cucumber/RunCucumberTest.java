package app.integration.cucumber;

import org.junit.platform.suite.api.*;
import org.springframework.context.annotation.Import;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "app.integration.cucumber")
@Import({TestContext.class, JwtTokenFactory.class, TestUtils.class, DataTableConfigurer.class})
public class RunCucumberTest {
}
