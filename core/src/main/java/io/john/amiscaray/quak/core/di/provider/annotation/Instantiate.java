package io.john.amiscaray.quak.core.di.provider.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * For a {@link io.john.amiscaray.quak.core.di.provider.annotation.ManagedType ManagedType} or controller, use the constructor annotated with this to instantiate it.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.CONSTRUCTOR)
public @interface Instantiate {
}
