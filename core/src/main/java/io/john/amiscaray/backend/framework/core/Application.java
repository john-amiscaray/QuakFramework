package io.john.amiscaray.backend.framework.core;

import io.john.amiscaray.backend.framework.core.properties.ApplicationProperties;
import java.io.IOException;
import java.util.*;

import static io.john.amiscaray.backend.framework.core.properties.ApplicationProperty.*;

public class Application {

    protected ApplicationProperties properties;
    protected String classScanPackage;
    protected String[] args;
    protected Class<?> main;

    public Application(Class<?> main, String[] args) {
        this.args = args;
        this.main = main;
        classScanPackage = main.getPackageName();
    }

    public void start() throws Exception {
        properties = getApplicationProperties();
    }

    public void startAsync() {
        Thread.startVirtualThread(() -> {
            try {
                start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public ApplicationProperties getApplicationProperties() {
        if (properties == null) {
            try (var propertiesFileInputStream = main.getResourceAsStream("/application.properties")) {
                var propertiesFromFile = new Properties();
                propertiesFromFile.load(propertiesFileInputStream);
                properties = new ApplicationProperties(propertiesFromFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }

}
