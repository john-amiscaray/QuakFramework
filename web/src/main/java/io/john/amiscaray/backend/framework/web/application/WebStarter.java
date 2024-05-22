package io.john.amiscaray.backend.framework.web.application;

import io.john.amiscaray.backend.framework.web.controller.DynamicPathController;
import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.web.handler.request.Request;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMapping;
import io.john.amiscaray.backend.framework.web.handler.response.Response;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;

public class WebStarter {

    public static WebApplication beginWebApplication(Class<?> main, String[] args) {
        var reflections = new Reflections(main.getPackageName(), Scanners.TypesAnnotated);
        var applicationBuilder = WebApplication.builder()
                .args(args)
                .main(main);

        var controllers = reflections.getTypesAnnotatedWith(Controller.class);

        for(var controller : controllers) {
            try {
                // TODO find a way to allow for non-empty constructors (i.e., dependency injection)
                var instance = controller.getConstructor().newInstance();
                var handlerMethods = Arrays.stream(controller.getMethods())
                        .filter(method -> method.isAnnotationPresent(Handle.class))
                        .filter(method -> method.getReturnType().equals(Response.class))
                        .filter(method -> method.getParameterCount() == 1)
                        .filter(method -> method.getParameters()[0].getType().isAssignableFrom(Request.class))
                        .toList();

                for (var handlerMethod : handlerMethods) {
                    var handlerInfo = handlerMethod.getAnnotation(Handle.class);
                    var requestArgument = handlerMethod.getParameters()[0];
                    var responseReturnType = (ParameterizedType) handlerMethod.getGenericReturnType();

                    var requestBodyType = Class.forName(((ParameterizedType) requestArgument.getParameterizedType())
                            .getActualTypeArguments()[0].getTypeName());

                    var responseBodyType = Class.forName(responseReturnType.getActualTypeArguments()[0].getTypeName());

                    applicationBuilder.pathMapping(
                            new RequestMapping(handlerInfo.method(), handlerInfo.path()),
                            new DynamicPathController<>(
                                    requestBodyType,
                                    responseBodyType,
                                    request -> {
                                        handlerMethod.setAccessible(true);
                                        try {
                                            return (Response) handlerMethod.invoke(instance, request);
                                        } catch (IllegalAccessException | InvocationTargetException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            )
                    );
                }
            } catch (InstantiationException e) {
                throw new RuntimeException("Controller could not be instantiated: ", e);
            } catch (IllegalAccessException | InvocationTargetException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException("Controller did not have an empty constructor", e);
            }
        }

        var application = applicationBuilder.build();
        application.startAsync();
        return application;
    }

}
