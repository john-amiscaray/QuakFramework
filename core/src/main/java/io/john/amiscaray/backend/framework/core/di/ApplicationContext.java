package io.john.amiscaray.backend.framework.core.di;

import java.util.Map;

public class ApplicationContext {

    private static ApplicationContext applicationContextInstance;
    private Map<Class<?>, Object> instances;
    private final String classScanPackage;

    private ApplicationContext(String classScanPackage) {
        this.classScanPackage = classScanPackage;
    }

    public static ApplicationContext getInstance(String classScanPackage) {
        if (applicationContextInstance == null) {
            applicationContextInstance = new ApplicationContext(classScanPackage);
        }
        return applicationContextInstance;
    }

    public <T> T getInstance(Class<T> clazz) {
        return (T) instances.get(clazz);
    }

}
