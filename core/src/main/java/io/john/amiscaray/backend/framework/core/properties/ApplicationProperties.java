package io.john.amiscaray.backend.framework.core.properties;


import lombok.AllArgsConstructor;

import java.util.Properties;

@AllArgsConstructor
public class ApplicationProperties{

    private final Properties fileProperties;

    public String get(ApplicationProperty property) {
        return property.getOrElseDefault(fileProperties);
    }

    public String getOrElse(ApplicationProperty property, String defaultValue) {
        return property.getOrElse(fileProperties, defaultValue);
    }

}
