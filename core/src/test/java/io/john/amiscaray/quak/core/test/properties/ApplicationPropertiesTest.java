package io.john.amiscaray.quak.core.test.properties;

import io.john.amiscaray.quak.core.properties.ApplicationProperties;
import io.john.amiscaray.quak.core.properties.ApplicationProperty;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ApplicationPropertiesTest {

    @Test
    public void shouldReturnValueFromConfigurationForApplicationProperty() {
        var applicationProperties = ApplicationProperties.getInstance();
        var mockProperties = new Properties();
        mockProperties.setProperty(ApplicationProperty.PORT.getName(), "4200");
        applicationProperties.init(mockProperties, "org.test");

        assertThat(applicationProperties.get(ApplicationProperty.PORT), equalTo("4200"));
    }

    @Test
    public void shouldReturnDefaultValueOfApplicationPropertyIfNotSpecifiedInFileProperties() {
        var applicationProperties = ApplicationProperties.getInstance();

        assertThat(applicationProperties.get(ApplicationProperty.PORT), equalTo(ApplicationProperty.PORT.getDefaultValue()));
    }

    @Test
    public void shouldReturnValueOfInterpolatedApplicationProperty() {
        var applicationProperties = ApplicationProperties.getInstance();
        var mockProperties = new Properties();
        mockProperties.setProperty(ApplicationProperty.PORT.getName(), "${PATH}");
        applicationProperties.init(mockProperties, "org.test");

        assertThat(applicationProperties.get(ApplicationProperty.PORT), equalTo(System.getenv("PATH")));
    }

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

    @Test
    public void shouldReturnNullWhenGettingCustomPropertyThatDoesNotExist() {
        var applicationProperties = ApplicationProperties.getInstance();

        assertThat(applicationProperties.getCustomProperty("foo"), equalTo(null));
    }

}
