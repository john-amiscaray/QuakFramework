package io.john.amiscaray.quak.data.test;

import io.john.amiscaray.quak.core.Application;
import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.data.DatabaseProxy;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DatabaseProxyStartupDependencyProviderTest {

    public static class MockApplication extends Application {
        public MockApplication(Class<?> main, String[] args) {
            super(main, args);
        }

        @Override
        protected void finish() throws Exception {

        }

        @Override
        protected void startUp() throws Exception {

        }
    }

    @BeforeAll
    public static void setUp() throws Exception {
        var application = new MockApplication(DatabaseProxyStartupDependencyProviderTest.class, new String[] {});
        application.start();
    }

    @Test
    public void testApplicationContextContainsDatabaseProxyOnStartup() {
        var context = ApplicationContext.getInstance();

        assertTrue(context.hasInstance(DatabaseProxy.class));
        assertNotNull(context.getInstance(DatabaseProxy.class));
    }

}
