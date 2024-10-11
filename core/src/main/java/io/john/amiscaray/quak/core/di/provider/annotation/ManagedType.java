package io.john.amiscaray.quak.core.di.provider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated class should be instantiated on application startup and added to the application context. The dependencyName argument is the name of this dependency when added to the application context.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ManagedType {

    String dependencyName() default "";

}
