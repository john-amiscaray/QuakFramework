package io.john.amiscaray.quak.web.application;

import io.john.amiscaray.quak.core.Application;
import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.http.response.Response;
import io.john.amiscaray.quak.web.controller.DynamicPathController;
import io.john.amiscaray.quak.web.controller.PathController;
import io.john.amiscaray.quak.web.controller.annotation.Controller;
import io.john.amiscaray.quak.web.controller.exception.InvalidRequestHandlerException;
import io.john.amiscaray.quak.web.handler.annotation.Handle;
import io.john.amiscaray.quak.http.request.Request;
import io.john.amiscaray.quak.http.request.RequestMapping;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Used to start a web application by scanning through the project classes an instantiating the provided controllers and filters.
 */
public class WebStarter {

    private static final Logger LOG = LoggerFactory.getLogger(WebStarter.class);

    private static String getRawTypeName(Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            return parameterizedType.getRawType().getTypeName();
        }
        return type.getTypeName();
    }

    /**
     * Begins a web application. Scans for classes annotated with {@link io.john.amiscaray.quak.web.controller.annotation.Controller} and creates instances of {@link io.john.amiscaray.quak.web.controller.PathController} based on them, so we can handle requests.
     * @param main The main entry class of the program.
     * @param args The program args.
     * @return A completable future returning the web application. Completes after the context loads and the controllers have been parsed.
     */
    public static CompletableFuture<WebApplication> beginWebApplication(Class<?> main, String[] args) {
        var application = WebApplication.getInstance();
        application.init(
                WebApplication.Configuration.builder()
                        .main(main)
                        .args(args).build()
        );
        var result = new CompletableFuture<WebApplication>();

        application.on(Application.LifecycleState.CONTEXT_LOADED, app -> {
            var reflections = new Reflections(app.getClassScanPackage(), Scanners.TypesAnnotated);
            var controllers = reflections.getTypesAnnotatedWith(Controller.class);
            var ctx = ApplicationContext.getInstance();
            Map<RequestMapping, PathController<?, ?>> pathControllers = new HashMap<>();

            for(var controller : controllers) {
                try {
                    var instance = ctx.getInstance(controller);
                    var handlerMethods = Arrays.stream(controller.getMethods())
                            .filter(method -> method.isAnnotationPresent(Handle.class))
                            .toList();
                    var contextPath = controller.getAnnotation(Controller.class).contextPath();

                    for (var handlerMethod : handlerMethods) {
                        if (!(handlerMethod.getReturnType().equals(Response.class) &&
                                handlerMethod.getParameterCount() == 1 &&
                                Request.class.isAssignableFrom(handlerMethod.getParameters()[0].getType()))) {
                            result.completeExceptionally(new InvalidRequestHandlerException(controller, handlerMethod));
                            return;
                        }
                        var handlerInfo = handlerMethod.getAnnotation(Handle.class);
                        var requestArgument = handlerMethod.getParameters()[0];
                        var responseReturnType = (ParameterizedType) handlerMethod.getGenericReturnType();

                        var requestBodyTypeName = getRawTypeName(((ParameterizedType) requestArgument.getParameterizedType())
                                .getActualTypeArguments()[0]);
                        var requestBodyType = Class.forName(requestBodyTypeName);

                        var responseBodyTypeName = getRawTypeName(responseReturnType.getActualTypeArguments()[0]);
                        var responseBodyType = Class.forName(responseBodyTypeName);

                        pathControllers.put(
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
                } catch (ClassNotFoundException e) {
                    result.completeExceptionally(e);
                }
            }

            application.addPathMappings(pathControllers);

            result.complete((WebApplication) app);
        });

        application.startAsync();

        return result;
    }

}
