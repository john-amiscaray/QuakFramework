package io.john.amiscaray.quak.core.di.provider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates parameters for either methods annotated with {@link io.john.amiscaray.quak.core.di.provider.annotation.Provide} or constructors annotated with {@link io.john.amiscaray.quak.core.di.provider.annotation.Instantiate}. This specifies the name of the dependency in the application context to use.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface ProvidedWith {

    String dependencyName();

}
