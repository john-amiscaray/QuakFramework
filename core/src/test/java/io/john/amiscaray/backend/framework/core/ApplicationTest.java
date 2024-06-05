package io.john.amiscaray.backend.framework.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.john.amiscaray.backend.framework.core.Application.*;
import static io.john.amiscaray.backend.framework.core.properties.ApplicationProperty.*;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {

    public static final String WRONG_APPLICATION_TYPE_MESSAGE = "Application unexpectedly is not an instance of DummyApplication. Please check the test setup.";
    private static DummyApplication application;

    @BeforeEach
    void setUp() {
        application = new DummyApplication(ApplicationTest.class, new String[0]);

        setUpListeners(application);
    }

    @Test
    public void testPropertiesAreLoadedFromFile() throws Exception {
        application.start();

        Assertions.assertEquals(CONTEXT_PACKAGE.getValue(), "org.something.stupid");
        Assertions.assertEquals(Integer.parseInt(PORT.getValue()), 9000);
        Assertions.assertEquals(SERVER_DIRECTORY.getValue(), "myserver");
        Assertions.assertEquals(DOCUMENT_BASE.getValue(), ".");
        Assertions.assertEquals(CONTEXT_PATH.getValue(), "/test");
        Assertions.assertEquals(DB_CONNECTION_URL.getValue(), "jdbc:h2:mem:test");
        Assertions.assertEquals(DB_DRIVER_CLASS.getValue(), "org.h2.Driver");
        Assertions.assertEquals(HBM2DDL.getValue(), "update");
        Assertions.assertEquals(DB_CONNECTION_USERNAME.getValue(), "sa");
        Assertions.assertEquals(DB_CONNECTION_PASSWORD.getValue(), "");
        Assertions.assertEquals(SQL_DIALECT.getValue(), "org.hibernate.dialect.H2Dialect");
    }

    @Test
    public void testApplicationCannotBeStoppedBeforeStarted() {

        assertThrows(IllegalStateException.class, () -> application.stop());

    }

    @Test
    public void testPreStartListenerIsCalled() throws Exception {
        application.start();

        assertTrue(application.hasPreStartBeenCalled);
    }

    @Test
    public void testContextLoadedListenerIsCalled() throws Exception {
        application.start();

        assertTrue(application.hasContextLoadedBeenCalled);
    }

    @Test
    public void testPostStartListenerIsCalled() throws Exception {
        application.start();

        assertTrue(application.hasPostStartBeenCalled);
    }

    @Test
    public void testPreStopListenerIsCalled() throws Exception {
        application.start();
        application.stop();

        assertTrue(application.hasPreStopBeenCalled);
    }

    @Test
    public void testPostStopListenerIsCalled() throws Exception {
        application.start();
        application.stop();

        assertTrue(application.hasPostStopBeenCalled);
    }

    @Test
    public void testLifecycleMethodsAreCalledInTheCorrectOrder() throws Exception {
        application.on(LifecycleState.PRE_START, app -> {
            if (app instanceof DummyApplication dummyApp) {
                assertApplicationLifeCycleStatesHaveBeenCalled(
                        dummyApp,
                        true,
                        false,
                        false,
                        false,
                        false
                );
            } else {
                Assertions.fail(WRONG_APPLICATION_TYPE_MESSAGE);
            }
        });
        application.on(LifecycleState.CONTEXT_LOADED, app -> {
            if (app instanceof DummyApplication dummyApp) {
                assertApplicationLifeCycleStatesHaveBeenCalled(
                        dummyApp,
                        true,
                        true,
                        false,
                        false,
                        false
                );
            } else {
                Assertions.fail(WRONG_APPLICATION_TYPE_MESSAGE);
            }
        });
        application.on(LifecycleState.POST_START, app -> {
            if (app instanceof DummyApplication dummyApp) {
                assertApplicationLifeCycleStatesHaveBeenCalled(
                        dummyApp,
                        true,
                        true,
                        true,
                        false,
                        false
                );
            } else {
                Assertions.fail(WRONG_APPLICATION_TYPE_MESSAGE);
            }
        });
        application.on(LifecycleState.PRE_STOP, app -> {
            if (app instanceof DummyApplication dummyApp) {
                assertApplicationLifeCycleStatesHaveBeenCalled(
                        dummyApp,
                        true,
                        true,
                        true,
                        true,
                        false
                );
            } else {
                Assertions.fail(WRONG_APPLICATION_TYPE_MESSAGE);
            }
        });
        application.on(LifecycleState.POST_STOP, app -> {
            if (app instanceof DummyApplication dummyApp) {
                assertApplicationLifeCycleStatesHaveBeenCalled(
                        dummyApp,
                        true,
                        true,
                        true,
                        true,
                        true
                );
            } else {
                Assertions.fail(WRONG_APPLICATION_TYPE_MESSAGE);
            }
        });

        application.start();
        application.stop();
    }

    private static void assertApplicationLifeCycleStatesHaveBeenCalled(
            DummyApplication application,
            boolean preStartCalled,
            boolean contextLoadedCalled,
            boolean postStartCalled,
            boolean preStopCalled,
            boolean postStopCalled
    ){
        assertEquals(preStartCalled, application.hasPreStartBeenCalled);
        assertEquals(contextLoadedCalled, application.hasContextLoadedBeenCalled);
        assertEquals(postStartCalled, application.hasPostStartBeenCalled);
        assertEquals(preStopCalled, application.hasPreStopBeenCalled);
        assertEquals(postStopCalled, application.hasPostStopBeenCalled);
    }

    private void setUpListeners(Application application) {

        application.on(LifecycleState.PRE_START, app -> {
            if (app instanceof DummyApplication dummyApp) {
                dummyApp.hasPreStartBeenCalled = true;
            } else {
                Assertions.fail(WRONG_APPLICATION_TYPE_MESSAGE);
            }
        });

        application.on(LifecycleState.CONTEXT_LOADED, app -> {
            if (app instanceof DummyApplication dummyApp) {
                dummyApp.hasContextLoadedBeenCalled = true;
            } else {
                Assertions.fail(WRONG_APPLICATION_TYPE_MESSAGE);
            }
        });

        application.on(LifecycleState.POST_START, app -> {
            if (app instanceof DummyApplication dummyApp) {
                dummyApp.hasPostStartBeenCalled = true;
            } else {
                Assertions.fail(WRONG_APPLICATION_TYPE_MESSAGE);
            }
        });

        application.on(LifecycleState.PRE_STOP, app -> {
            if (app instanceof DummyApplication dummyApp) {
                dummyApp.hasPreStopBeenCalled = true;
            } else {
                Assertions.fail(WRONG_APPLICATION_TYPE_MESSAGE);
            }
        });

        application.on(LifecycleState.POST_STOP, app -> {
            if (app instanceof DummyApplication dummyApp) {
                dummyApp.hasPostStopBeenCalled = true;
            } else {
                Assertions.fail(WRONG_APPLICATION_TYPE_MESSAGE);
            }
        });
    }

    private static final class DummyApplication extends Application {

        private boolean hasPreStartBeenCalled;
        private boolean hasContextLoadedBeenCalled;
        private boolean hasPostStartBeenCalled;
        private boolean hasPreStopBeenCalled;
        private boolean hasPostStopBeenCalled;

        public DummyApplication(Class<?> main, String[] args) {
            super(main, args);
        }

        @Override
        public void finish() {

        }

        @Override
        protected void startUp() {

        }
    }

}
