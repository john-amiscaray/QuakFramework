package io.john.amiscaray.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyLoadingTest {

    private final Application application = new Application();

    @Test
    void testPropertiesAreLoadedFromFile() {
        
        var properties = application.getApplicationProperties();
        
        assertEquals(properties.serverPort(), 9000);
        assertEquals(properties.serverDirectory(), "myserver");
        assertEquals(properties.serverDocBase(), "based");
        assertEquals(properties.serverContextPath(), "/test");
        assertEquals(properties.dbConnectionURL(), "jdbc:mysql://localhost:3306/test");
        assertEquals(properties.dbConnectionDriver(), "com.mysql.cj.jdbc.Driver");
        assertEquals(properties.hbm2ddl(), "update");
        assertEquals(properties.dbUsername(), "root");
        assertEquals(properties.dbPassword(), "password");
        assertEquals(properties.sqlDialect(), "org.hibernate.dialect.MySQLDialect");

    }

}
