package io.john.amiscaray.backend.framework.core;

import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.di.exception.ContextInitializationException;
import io.john.amiscaray.backend.framework.core.properties.ApplicationProperties;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

    public void start() throws Exception{
        initProperties();
        initContext();
    }

    public void startAsync() {
        Thread.startVirtualThread(() -> {
            try {
                this.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void initContext() {
        var applicationContext = ApplicationContext.getInstance();
        try {
            applicationContext.init(classScanPackage);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new ContextInitializationException(e);
        }
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
