package io.john.amiscaray.quak.core.di.provider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated class provides objects to the application context using methods annotated with {@link io.john.amiscaray.quak.core.di.provider.annotation.Provide}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Provider {

    String dependencyName() default "";

}
