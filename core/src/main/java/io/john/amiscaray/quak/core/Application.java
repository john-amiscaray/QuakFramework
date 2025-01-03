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
 * Represents the base functionality of a quak application. This contains lifecycle hooks you can add callbacks to and methods to start or end the application.
 * Lifecycle hooks include: PRE_START, CONTEXT_LOADED, POST_START, PRE_STOP, POST_STOP.
 */
public abstract class Application {

    /**
     * Whether the application has started.
     */
    @Getter
    protected boolean hasStarted;
    /**
     * Whether the application context loaded.
     */
    @Getter
    protected boolean contextLoaded;
    /**
     * The package from which Quak scans for dependencies. This should be an upmost package containing all classes.
     */
    @Getter
    protected String classScanPackage;
    /**
     * The program arguments passed to the application.
     */
    protected String[] args;
    /**
     * The class that contains the application's main method.
     */
    protected Class<?> main;
    /**
     * A map from lifecycle state to list of lifecycle event consumer. The consumers accept this application instance.
     */
    protected Map<LifecycleState, List<Consumer<Application>>> lifecycleListeners;

    /**
     * Initialize an application.
     * @param main The class containing the application's main method.
     * @param args The program arguments passed to the application.
     */
    public Application(Class<?> main, String[] args) {
        this.args = args;
        this.main = main;
        classScanPackage = main.getPackageName();
        initLifecycleListeners();
    }

    /**
     * Initializes a map of lifecycle states to lifecycle event consumers.
     */
    protected void initLifecycleListeners() {
        lifecycleListeners = new HashMap<>();
        for(LifecycleState lifecycleState : LifecycleState.values()) {
            lifecycleListeners.put(lifecycleState, new ArrayList<>());
        }
    }

    /**
     * Starts the application. In this initialization sequence, in this order, the application: calls any <i>"pre-start"</i> listeners,
     * initializes the application properties, loads the application context, invokes the <i>"context loaded"</i> listeners, calls the
     * {@link Application#startUp() startup method}, and calls the <i>"post start"</i> listeners.
     * @throws Exception any exceptions from this method will be thrown.
     */
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

    /**
     * Stops the application. In this order, this method: calls any <i>"pre stop"</i> listeners, invokes the {@link Application#finish() finish method},
     * and calls any <i>"post stop"</i> listeners.
     * @throws Exception any exceptions from this method will be thrown.
     */
    public final void stop() throws Exception {
        if (!hasStarted) {
            throw new IllegalStateException("stop called before application started");
        }
        preStop();
        finish();
        postStop();
        hasStarted = false;
    }

    /**
     * Unimplemented tear down logic for your application.
     * @throws Exception any exception in this method is thrown.
     */
    protected abstract void finish() throws Exception;

    /**
     * Unimplemented startup logic for your application.
     * @throws Exception any exception in this method is thrown.
     */
    protected abstract void startUp() throws Exception;

    /**
     * Starts the application asynchronously. Notifies anything blocking on this application to stop when it does stop.
     */
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

    /**
     * Block while the application is running.
     * @throws InterruptedException if there is a thread interruption.
     */
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

    /**
     * Register an application lifecycle state listener.
     * @param state The lifecycle state.
     * @param consumer A consumer that is called when this lifecycle state occurs. Accepts this.
     */
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

    /**
     * Represents states during an application's lifecycle
     */
    public enum LifecycleState {
        /**
         * Before the application starts.
         */
        PRE_START,
        /**
         * After the application context loads (i.e., all the dependencies are resolved).
         */
        CONTEXT_LOADED,
        /**
         * After the application starts.
         */
        POST_START,
        /**
         * Before the application stops.
         */
        PRE_STOP,
        /**
         * After the application stops.
         */
        POST_STOP
    }

}
