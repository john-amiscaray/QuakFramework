package io.john.amiscaray.quak.generator.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a static method that generates an instance of an entity from the current {@link io.john.amiscaray.quak.generator.api.RestModel}
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface EntityGenerator {


}
