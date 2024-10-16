package io.john.amiscaray.quak.web.filter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a {@link jakarta.servlet.Filter} to be used by the application.
 */
@Target(value={ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationFilter {

    /**
     * @return The name of the filter.
     */
    String name() default "";

    /**
     * @return URL patterns this filter will handle.
     */
    String[] urlPatterns() default {"/*" };

    /**
     * @return A priority determining when this filter will be applied relative to other filters.
     */
    int priority() default Integer.MAX_VALUE;

}
