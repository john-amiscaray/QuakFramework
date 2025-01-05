package io.john.amiscaray.quak.core.properties;

import java.util.Optional;
import java.util.Properties;

/**
 * Acts as a wrapper for the application's {@link io.john.amiscaray.quak.core.properties.ApplicationProperty} instances. This reads the application.properties file and sets the values of each ApplicationProperty accordingly. This gets initialized on application startup.
 */
public class ApplicationProperties{

    private static ApplicationProperties applicationPropertiesInstance;
    private Properties fileProperties;
    private String classScanPackage;

    private ApplicationProperties() {
    }

    /**
     * Init this from the properties from the application.properties file and the application's class scan package.
     * @param fileProperties The properties retrieved from the application.properties file.
     * @param classScanPackage The package Quak scans to looks for classes (usually for dependency injection).
     */
    public void init(Properties fileProperties, String classScanPackage) {
        this.fileProperties = fileProperties;
        this.classScanPackage = classScanPackage;

        for (var applicationProperty : ApplicationProperty.class.getEnumConstants()) {
            var value = Optional.ofNullable(fileProperties.getProperty(applicationProperty.getName()))
                    .orElse(applicationProperty.getDefaultValue());
            if (value.matches("\\$\\{.+}")) {
                value = System.getenv(value.substring(2, value.length() - 1));
            }
            applicationProperty.setValue(value);
        }
    }

    /**
     * Retrieve the singleton instance of this.
     * @return The singleton instance.
     */
    public static ApplicationProperties getInstance() {
        if (applicationPropertiesInstance == null) {
            applicationPropertiesInstance = new ApplicationProperties();
        }
        return applicationPropertiesInstance;
    }

    /**
     * Get the property from the application.properties file represented by an instance of {@link io.john.amiscaray.quak.core.properties.ApplicationProperty ApplicationProperty}.
     * @param property The instance of {@link io.john.amiscaray.quak.core.properties.ApplicationProperty ApplicationProperty}.
     * @return The value of the property as a string.
     */
    public String get(ApplicationProperty property) {
        return property.getOrElseDefault(fileProperties);
    }

    /**
     * Get the property from the application.properties file represented by an instance of {@link io.john.amiscaray.quak.core.properties.ApplicationProperty ApplicationProperty}
     * or default to a given default value.
     * @param property The instance of {@link io.john.amiscaray.quak.core.properties.ApplicationProperty ApplicationProperty}.
     * @param defaultValue The default value if the property could not be found.
     * @return The value of the property as a string.
     */
    public String getOrElse(ApplicationProperty property, String defaultValue) {
        return property.getOrElse(fileProperties, defaultValue);
    }

}
