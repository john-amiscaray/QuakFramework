package io.john.amiscaray.backend.framework.core;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ApplicationTest {

    private final Application application = new Application(ApplicationTest.class, new String[0]);

    @Test
    void testPropertiesAreLoadedFromFile() {
        var properties = application.getApplicationProperties();

        Assertions.assertEquals(properties.serverPort(), 9000);
        Assertions.assertEquals(properties.serverDirectory(), "myserver");
        Assertions.assertEquals(properties.serverDocBase(), ".");
        Assertions.assertEquals(properties.serverContextPath(), "/test");
        Assertions.assertEquals(properties.dbConnectionURL(), "jdbc:h2:mem:test");
        Assertions.assertEquals(properties.dbConnectionDriver(), "org.h2.Driver");
        Assertions.assertEquals(properties.hbm2ddl(), "update");
        Assertions.assertEquals(properties.dbUsername(), "sa");
        Assertions.assertEquals(properties.dbPassword(), "");
        Assertions.assertEquals(properties.sqlDialect(), "org.hibernate.dialect.H2Dialect");
    }

}
