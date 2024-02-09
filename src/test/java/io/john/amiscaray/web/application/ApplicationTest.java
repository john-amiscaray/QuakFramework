package io.john.amiscaray.web.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationTest {

    private final Application application = new Application(ApplicationTest.class, new String[0]);

    @Test
    void testPropertiesAreLoadedFromFile() {
        var properties = application.getApplicationProperties();

        assertEquals(properties.serverPort(), 9000);
        assertEquals(properties.serverDirectory(), "myserver");
        assertEquals(properties.serverDocBase(), ".");
        assertEquals(properties.serverContextPath(), "/test");
        assertEquals(properties.dbConnectionURL(), "jdbc:h2:mem:test");
        assertEquals(properties.dbConnectionDriver(), "org.h2.Driver");
        assertEquals(properties.hbm2ddl(), "update");
        assertEquals(properties.dbUsername(), "root");
        assertEquals(properties.dbPassword(), "password");
        assertEquals(properties.sqlDialect(), "org.hibernate.dialect.MySQLDialect");
    }

}
