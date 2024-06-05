package io.john.amiscaray.backend.framework.core;

import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.di.exception.ContextInitializationException;
import io.john.amiscaray.backend.framework.core.properties.ApplicationProperties;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

public abstract class Application {

    protected String classScanPackage;
    protected String[] args;
    protected Class<?> main;
    protected Map<LifecycleState, List<Consumer<Application>>> lifecycleListeners = new HashMap<>();

    public Application(Class<?> main, String[] args) {
        this.args = args;
        this.main = main;
        classScanPackage = main.getPackageName();
        for(LifecycleState lifecycleState : LifecycleState.values()) {
            lifecycleListeners.put(lifecycleState, new ArrayList<>());
        }
    }

    public final void start() throws Exception{
        preStart();
        initProperties();
        initContext();
        contextLoaded();
        startUp();
        postStart();
    }

    public final void stop() throws Exception {
        preStop();
        finish();
        postStop();
    }

    public abstract void finish() throws Exception;

    protected abstract void startUp() throws Exception;

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

    private void on(LifecycleState state, Consumer<Application> consumer) {
        lifecycleListeners.get(state).add(consumer);
    }

    private void preStart() {
        lifecycleListeners.get(LifecycleState.PRE_START)
                .forEach(consumer -> consumer.accept(this));
    }

    private void contextLoaded() {
        lifecycleListeners.get(LifecycleState.CONTEXT_LOADED)
                .forEach(consumer -> consumer.accept(this));
    }

    private void postStart() {
        lifecycleListeners.get(LifecycleState.POST_START)
                .forEach(consumer -> consumer.accept(this));
    }

    private void preStop() {
        lifecycleListeners.get(LifecycleState.PRE_STOP)
                .forEach(consumer -> consumer.accept(this));
    }

    private void postStop() {
        lifecycleListeners.get(LifecycleState.POST_STOP)
                .forEach(consumer -> consumer.accept(this));
    }

    public enum LifecycleState {
        PRE_START,
        CONTEXT_LOADED,
        POST_START,
        PRE_STOP,
        POST_STOP
    }

}
