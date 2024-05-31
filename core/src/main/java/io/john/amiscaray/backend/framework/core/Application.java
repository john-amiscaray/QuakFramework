package io.john.amiscaray.backend.framework.core;

import io.john.amiscaray.backend.framework.core.properties.ApplicationProperties;

import java.io.IOException;
import java.util.*;

public abstract class Application {

    protected String classScanPackage;
    protected String[] args;
    protected Class<?> main;

    public Application(Class<?> main, String[] args) {
        this.args = args;
        this.main = main;
        classScanPackage = main.getPackageName();
    }

    public void start() {
        initProperties();
    }

    public void startAsync() {
        Thread.startVirtualThread(this::start);
    }

    private void initProperties() {
        try (var propertiesFileInputStream = main.getResourceAsStream("/application.properties")) {
            var applicationProperties = ApplicationProperties.getInstance();
            var propertiesFromFile = new Properties();
            propertiesFromFile.load(propertiesFileInputStream);
            applicationProperties.init(propertiesFromFile, classScanPackage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
