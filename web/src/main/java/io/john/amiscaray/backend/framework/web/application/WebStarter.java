package io.john.amiscaray.backend.framework.web.application;

import io.john.amiscaray.backend.framework.core.Application;
import io.john.amiscaray.backend.framework.web.controller.DynamicPathController;
import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
import io.john.amiscaray.backend.framework.web.handler.request.Request;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMapping;
import io.john.amiscaray.backend.framework.web.handler.response.Response;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class WebStarter {

    private static final Logger LOG = LoggerFactory.getLogger(WebStarter.class);

    private static String getRawTypeName(Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            return parameterizedType.getRawType().getTypeName();
        }
        return type.getTypeName();
    }

    public static CompletableFuture<WebApplication> beginWebApplication(Class<?> main, String[] args) {
        var application = WebApplication.getInstance();
        var result = new CompletableFuture<WebApplication>();

        application.on(Application.LifecycleState.CONTEXT_LOADED, app -> {
            var reflections = new Reflections(main.getPackageName(), Scanners.TypesAnnotated);
            var configurationBuilder = WebApplication.Configuration.builder()
                    .main(main)
                    .args(args);
            var controllers = reflections.getTypesAnnotatedWith(Controller.class);

            for(var controller : controllers) {
                try {
                    // TODO find a way to allow for non-empty constructors (i.e., dependency injection)
                    var instance = controller.getConstructor().newInstance();
                    var handlerMethods = Arrays.stream(controller.getMethods())
                            .filter(method -> method.isAnnotationPresent(Handle.class))
                            .filter(method -> method.getReturnType().equals(Response.class))
                            .filter(method -> method.getParameterCount() == 1)
                            .filter(method -> Request.class.isAssignableFrom(method.getParameters()[0].getType()))
                            .toList();
                    var contextPath = controller.getAnnotation(Controller.class).contextPath();

                    for (var handlerMethod : handlerMethods) {
                        var handlerInfo = handlerMethod.getAnnotation(Handle.class);
                        var requestArgument = handlerMethod.getParameters()[0];
                        var responseReturnType = (ParameterizedType) handlerMethod.getGenericReturnType();

                        var requestBodyTypeName = getRawTypeName(((ParameterizedType) requestArgument.getParameterizedType())
                                .getActualTypeArguments()[0]);
                        var requestBodyType = Class.forName(requestBodyTypeName);

                        var responseBodyTypeName = getRawTypeName(responseReturnType.getActualTypeArguments()[0]);
                        var responseBodyType = Class.forName(responseBodyTypeName);

                        configurationBuilder.pathMapping(
                                new RequestMapping(handlerInfo.method(), contextPath + handlerInfo.path()),
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

            application.init(configurationBuilder.build());

            result.complete((WebApplication) app);
        });

        application.startAsync();

        return result;
    }

}
