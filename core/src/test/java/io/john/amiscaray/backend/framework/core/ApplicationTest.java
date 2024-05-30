package io.john.amiscaray.backend.framework.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.john.amiscaray.backend.framework.core.properties.ApplicationProperty.*;

public class ApplicationTest {

    private final Application application = new Application(ApplicationTest.class, new String[0]);

    @Test
    void testPropertiesAreLoadedFromFile() {
        var properties = application.getApplicationProperties();

        Assertions.assertEquals(properties.get(CONTEXT_PACKAGE), "org.something.stupid");
        Assertions.assertEquals(Integer.parseInt(properties.get(PORT)), 9000);
        Assertions.assertEquals(properties.get(SERVER_DIRECTORY), "myserver");
        Assertions.assertEquals(properties.get(DOCUMENT_BASE), ".");
        Assertions.assertEquals(properties.get(CONTEXT_PATH), "/test");
        Assertions.assertEquals(properties.get(DB_CONNECTION_URL), "jdbc:h2:mem:test");
        Assertions.assertEquals(properties.get(DB_DRIVER_CLASS), "org.h2.Driver");
        Assertions.assertEquals(properties.get(HBM2DDL), "update");
        Assertions.assertEquals(properties.get(DB_CONNECTION_USERNAME), "sa");
        Assertions.assertEquals(properties.get(DB_CONNECTION_PASSWORD), "");
        Assertions.assertEquals(properties.get(SQL_DIALECT), "org.hibernate.dialect.H2Dialect");
    }

}
