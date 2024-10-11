package io.john.amiscaray.quak.core.di.provider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated method is used by a Provider to provide an object to the application context. These methods may have parameters corresponding to objects in the application context.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD})
public @interface Provide {

    String dependencyName() default "";

}
