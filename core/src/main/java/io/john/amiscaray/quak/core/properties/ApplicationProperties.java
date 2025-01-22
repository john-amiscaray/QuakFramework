package io.john.amiscaray.quak.core.properties;

import java.util.*;

/**
 * Acts as a wrapper for the application's {@link io.john.amiscaray.quak.core.properties.ApplicationProperty} instances. This reads the application.properties file and sets the values of each ApplicationProperty accordingly. This gets initialized on application startup.
 */
public class ApplicationProperties{

    private static ApplicationProperties applicationPropertiesInstance;
    private Properties fileProperties;
    private String classScanPackage;
    private Map<String, String> customProperties = new HashMap<String, String>();

    private ApplicationProperties() {
    }

    /**
     * Init this from the application.properties file and the application's class scan package.
     * Reads the properties from the project's application.properties file and puts their values in the corresponding
     * {@link io.john.amiscaray.quak.core.properties.ApplicationProperty ApplicationProperty} enum constant.
     * If there is no matching ApplicationProperty enum constant, the property is added as a custom property.
     * @param fileProperties The properties retrieved from the application.properties file.
     * @param classScanPackage The package Quak scans to looks for classes (usually for dependency injection).
     */
    public void init(Properties fileProperties, String classScanPackage) {
        this.fileProperties = fileProperties;
        this.classScanPackage = classScanPackage;

        // Reset property values
        for(var property : ApplicationProperty.class.getEnumConstants()) {
            property.setValue(property.getDefaultValue());
        }

        fileProperties.forEach((key, value) -> {
            var k = (String) key;
            var v = (String) value;
            if (v.matches("\\$\\{.+}")) {
                v = System.getenv(v.substring(2, v.length() - 1));
            }
            var applicationPropertyConstant = Arrays.stream(ApplicationProperty.class.getEnumConstants())
                    .filter(property -> property.getName().equals(key))
                    .findFirst();
            if (applicationPropertyConstant.isPresent()) {
                var applicationProperty = applicationPropertyConstant.get();
                var finalValue = Optional.ofNullable(v)
                        .orElse(applicationProperty.getDefaultValue());
                applicationProperty.setValue(finalValue);
            } else {
                customProperties.put(k, v);
            }
        });
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
     * Gets the value of a custom property added to the application.properties file. A <i>custom value</i> is any value not
     * specified in the {@link io.john.amiscaray.quak.core.properties.ApplicationProperty ApplicationProperty} enum class.
     * @param key The name of the property.
     * @return The value of the property.
     */
    public String getCustomProperty(String key) {
        return customProperties.get(key);
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
