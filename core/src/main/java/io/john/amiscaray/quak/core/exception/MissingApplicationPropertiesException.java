package io.john.amiscaray.quak.core.exception;

/**
 * Thrown if the application is missing an application.properties file.
 */
public class MissingApplicationPropertiesException extends RuntimeException {

    public MissingApplicationPropertiesException() {
        super("An application.properties file is required. Please add one to your project's src/main/resources folder.");
    }

}
