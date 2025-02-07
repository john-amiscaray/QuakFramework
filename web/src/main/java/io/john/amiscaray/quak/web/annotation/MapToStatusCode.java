package io.john.amiscaray.quak.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps an exception to an HTTP status code. When this exception gets thrown during the execution of a controller, return a response with this status code and the exception message.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MapToStatusCode {
    /**
     * @return The status code to map this exception to.
     */
    int value();
}
