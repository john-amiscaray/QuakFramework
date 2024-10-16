package io.john.amiscaray.quak.core.di.provider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotated class provides objects to the application context using methods annotated with {@link io.john.amiscaray.quak.core.di.provider.annotation.Provide}. Note, providers are added to the application context. Thus, they need either an empty constructor or a constructor annotated with {@link io.john.amiscaray.quak.core.di.provider.annotation.Instantiate}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Provider {

    /**
     * @return The name of the provider when added to the application context.
     */
    String dependencyName() default "";

}
