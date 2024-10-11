package io.john.amiscaray.quak.web.filter.exception;

public class InvalidApplicationFilterException extends RuntimeException {

    public InvalidApplicationFilterException(Class<?> annotatedClass, String reason) {
        super("Invalid application filter: " + annotatedClass.getName() + "\n" + reason);
    }
}
