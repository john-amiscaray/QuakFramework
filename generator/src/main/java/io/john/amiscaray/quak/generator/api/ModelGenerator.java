package io.john.amiscaray.quak.generator.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a static method that generates an instance of a {@link io.john.amiscaray.quak.generator.api.RestModel} from the current entity.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface ModelGenerator {
}
