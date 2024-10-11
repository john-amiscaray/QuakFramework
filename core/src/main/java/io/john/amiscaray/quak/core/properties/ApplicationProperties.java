package io.john.amiscaray.quak.core.properties;

import java.util.Optional;
import java.util.Properties;

public class ApplicationProperties{

    private static ApplicationProperties applicationPropertiesInstance;
    private Properties fileProperties;
    private String classScanPackage;

    private ApplicationProperties() {
    }

    public void init(Properties fileProperties, String classScanPackage) {
        this.fileProperties = fileProperties;
        this.classScanPackage = classScanPackage;

        for (var applicationProperty : ApplicationProperty.class.getEnumConstants()) {
            var value = Optional.ofNullable(fileProperties.getProperty(applicationProperty.getName()))
                    .orElse(applicationProperty.getDefaultValue());
            applicationProperty.setValue(value);
        }
    }

    public static ApplicationProperties getInstance() {
        if (applicationPropertiesInstance == null) {
            applicationPropertiesInstance = new ApplicationProperties();
        }
        return applicationPropertiesInstance;
    }

    public String get(ApplicationProperty property) {
        return property.getOrElseDefault(fileProperties);
    }

    public String getOrElse(ApplicationProperty property, String defaultValue) {
        return property.getOrElse(fileProperties, defaultValue);
    }

}
