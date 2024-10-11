package io.john.amiscaray.quak.web.controller.exception;

import java.lang.reflect.Method;

public class InvalidRequestHandlerException extends RuntimeException {

    public InvalidRequestHandlerException(Class<?> controllerClass, Method handlerMethod) {
        super("Request handler method " + controllerClass.getSimpleName() + "#" + handlerMethod.getName() + " is incorrectly defined. It must have a io.john.amiscaray.backend.framework.web.handler.request.Request parameter and return a io.john.amiscaray.backend.framework.web.handler.response.Response object.");
    }

}
