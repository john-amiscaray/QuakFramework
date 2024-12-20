package io.john.amiscaray.quak.core.exception;

public class MissingApplicationPropertiesException extends RuntimeException {

    public MissingApplicationPropertiesException() {
        super("An application.properties file is required. Please add one to your project's src/main/resources folder.");
    }

}
