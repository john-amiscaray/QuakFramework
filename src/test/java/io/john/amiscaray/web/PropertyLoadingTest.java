package io.john.amiscaray.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PropertyLoadingTest {

    private final Application application = new Application();

    @Test
    void testPropertiesAreLoadedFromFile() {

        assertEquals(application.getApplicationProperties().serverPort(), 9000);
        assertEquals(application.getApplicationProperties().serverDirectory(), "myserver");
        assertEquals(application.getApplicationProperties().serverDocBase(), "based");
        assertEquals(application.getApplicationProperties().serverContextPath(), "/test");

    }

}
