package io.john.amiscaray.backend.framework.web.filter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplicationFilter {

    String name() default "";
    String[] urlPatterns() default {"/*" };
    int priority() default Integer.MAX_VALUE;

}
