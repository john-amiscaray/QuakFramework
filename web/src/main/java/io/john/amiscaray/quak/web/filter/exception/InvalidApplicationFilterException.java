package io.john.amiscaray.quak.web.filter.exception;

/**
 * Thrown when a class annotated with {@link io.john.amiscaray.quak.web.filter.annotation.ApplicationFilter} is invalid.
 */
public class InvalidApplicationFilterException extends RuntimeException {

    public InvalidApplicationFilterException(Class<?> annotatedClass, String reason) {
        super("Invalid application filter: " + annotatedClass.getName() + "\n" + reason);
    }
}
