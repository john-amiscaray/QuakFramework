package io.john.amiscaray.quak.web.controller.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a class used to handle HTTP requests. Every method in the class used to handle requests must be annotated with {@link io.john.amiscaray.quak.web.handler.annotation.Handle}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Controller {
    /**
     * @return The parent path for each sub-path associated with the handler methods. Defaults to an empty string meaning they are relative to the root of the API.
     */
    String contextPath() default "";
}
