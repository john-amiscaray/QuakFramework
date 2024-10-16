package io.john.amiscaray.quak.generator.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a model class representing data used for request or response bodies.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface RestModel {
    /**
     * @return The corresponding class for the database representation of this model.
     */
    Class<?> dataClass();
}
