package io.john.amiscaray.quak.web.handler.annotation;

import io.john.amiscaray.quak.http.request.RequestMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Handle {

    String path();
    RequestMethod method() default RequestMethod.GET;

}
