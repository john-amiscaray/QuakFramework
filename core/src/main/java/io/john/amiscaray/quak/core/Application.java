package io.john.amiscaray.quak.core;

import io.john.amiscaray.quak.core.exception.MissingApplicationPropertiesException;
import io.john.amiscaray.quak.core.properties.ApplicationProperties;
import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.core.di.exception.ContextInitializationException;
import lombok.Getter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Consumer;

/**
 * Represents the base functionality of a quak application. This contains lifecycle hooks and methods to start or end the application.
 */
public abstract class Application {

    @Getter
    protected boolean hasStarted;
    @Getter
    protected boolean contextLoaded;
    @Getter
    protected String classScanPackage;
    protected String[] args;
    protected Class<?> main;
    protected Map<LifecycleState, List<Consumer<Application>>> lifecycleListeners;

    public Application(Class<?> main, String[] args) {
        this.args = args;
        this.main = main;
        classScanPackage = main.getPackageName();
        initLifecycleListeners();
    }

    protected void initLifecycleListeners() {
        lifecycleListeners = new HashMap<>();
        for(LifecycleState lifecycleState : LifecycleState.values()) {
            lifecycleListeners.put(lifecycleState, new ArrayList<>());
        }
    }

    public final void start() throws Exception{
        preStart();
        initProperties();
        initContext();
        contextLoaded();
        contextLoaded = true;
        startUp();
        postStart();
        hasStarted = true;
    }

    public final void stop() throws Exception {
        if (!hasStarted) {
            throw new IllegalStateException("stop called before application started");
        }
        preStop();
        finish();
        postStop();
        hasStarted = false;
    }

    protected abstract void finish() throws Exception;

    protected abstract void startUp() throws Exception;

    public void startAsync() {
        Thread.startVirtualThread(() -> {
            try {
                start();
                on(LifecycleState.POST_STOP, _application -> {
                    synchronized (this) {
                        this.notifyAll();
                    }
                });
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void await() throws InterruptedException {
        while (contextLoaded) {
            synchronized (this) {
                wait();
            }
        }
    }

    private void initContext() {
        var applicationContext = ApplicationContext.getInstance();
        try {
            applicationContext.init(classScanPackage);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new ContextInitializationException(e);
        }
    }

    private void initProperties() {
        if (main.getResource("/application.properties") == null) {
            throw new MissingApplicationPropertiesException();
        }
        try (var propertiesFileInputStream = main.getResourceAsStream("/application.properties")) {
            var applicationProperties = ApplicationProperties.getInstance();
            var propertiesFromFile = new Properties();
            propertiesFromFile.load(propertiesFileInputStream);
            applicationProperties.init(propertiesFromFile, classScanPackage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void on(LifecycleState state, Consumer<Application> consumer) {
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
        contextLoaded = false;
        hasStarted = false;
    }

    public enum LifecycleState {
        PRE_START,
        CONTEXT_LOADED,
        POST_START,
        PRE_STOP,
        POST_STOP
    }

}
