package io.john.amiscaray.quak.core.test.properties;

import io.john.amiscaray.quak.core.properties.ApplicationProperties;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ApplicationPropertiesTest {

    @Test
    public void shouldAddPropertyNotSpecifiedInApplicationPropertyEnumAsCustomProperty() {
        var applicationProperties = ApplicationProperties.getInstance();
        var mockProperties = new Properties();
        mockProperties.setProperty("foo", "bar");
        applicationProperties.init(mockProperties, "org.test");

        assertThat(applicationProperties.getCustomProperty("foo"), equalTo("bar"));
    }

    @Test
    public void shouldInterpolateValueIntoCustomApplicationProperty() {
        var applicationProperties = ApplicationProperties.getInstance();
        var mockProperties = new Properties();
        mockProperties.setProperty("foo", "${PATH}");
        applicationProperties.init(mockProperties, "org.test");

        assertThat(applicationProperties.getCustomProperty("foo"), equalTo(System.getenv("PATH")));
    }

}
