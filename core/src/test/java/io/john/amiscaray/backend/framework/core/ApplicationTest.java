package io.john.amiscaray.backend.framework.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.john.amiscaray.backend.framework.core.properties.ApplicationProperty.*;

public class ApplicationTest {

    private static final Application application = new Application(ApplicationTest.class, new String[0]) {
        @Override
        public void finish() throws Exception {

        }

        @Override
        protected void startUp() {

        }
    };

    @BeforeAll
    static void setUp() throws Exception {
        application.start();
    }

    @Test
    void testPropertiesAreLoadedFromFile() {
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

}
