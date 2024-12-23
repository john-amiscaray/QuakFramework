package io.john.amiscaray.quak.generator.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation marks a static method in an entity class that returns a {@link io.john.amiscaray.quak.data.query.NativeQuery}. This is used to generate an API endpoint returning the results of that query. Such methods need to accept a {@link io.john.amiscaray.quak.http.request.Request} object as a parameter.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.METHOD)
public @interface APINativeQuery {

    /**
     * @return The path to use for the API generation.
     */
    String path();

}
