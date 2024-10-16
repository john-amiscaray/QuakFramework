package io.john.amiscaray.quak.web.handler.annotation;

import io.john.amiscaray.quak.http.request.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a method in a class annotated with {@link io.john.amiscaray.quak.web.controller.annotation.Controller}. A method with this annotation handles an HTTP request for a single endpoint.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handle {

    /**
     * @return The URL path.
     */
    String path();

    /**
     * @return The HTTP method for this endpoint.
     */
    RequestMethod method() default RequestMethod.GET;

}
