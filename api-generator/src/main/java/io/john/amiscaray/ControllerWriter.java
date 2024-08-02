package io.john.amiscaray;

import io.john.amiscaray.backend.framework.generator.api.EntityGenerator;
import io.john.amiscaray.backend.framework.generator.api.ModelGenerator;
import io.john.amiscaray.backend.framework.generator.api.RestModel;
import io.john.amiscaray.model.GeneratedClass;
import jakarta.persistence.Id;
import org.apache.commons.lang3.StringUtils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class ControllerWriter {

    private static ControllerWriter controllerWriterInstance;

    private ControllerWriter() {
    }

    public static ControllerWriter getInstance() {
        if (controllerWriterInstance == null) {
            controllerWriterInstance = new ControllerWriter();
        }
        return controllerWriterInstance;
    }

    private Method getModelGeneratorFromDataClass(Class<?> dataClass) {
        var annotatedMethods = Arrays.stream(dataClass.getMethods())
                .filter(method -> method.isAnnotationPresent(ModelGenerator.class))
                .toList();

        if (annotatedMethods.isEmpty()) {
            throw new IllegalStateException("A linked data class should have a method annotated with @ModelGenerator");
        } else if (annotatedMethods.size() > 1) {
            throw new IllegalStateException("A linked data class should have exactly ONE method annotated with @ModelGenerator");
        }

        return annotatedMethods.getFirst();
    }

    private Method getIdGetterFromDataClass(Class<?> dataClass) throws IntrospectionException {
        var idField = Arrays.stream(dataClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow();
        return Arrays.stream(dataClass.getMethods())
                .filter(method -> method.getName().equals("get" + StringUtils.capitalize(idField.getName())))
                .filter(method -> method.getReturnType().equals(idField.getType()))
                .filter(method -> method.getParameterCount() == 0)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Data class " + dataClass.getSimpleName() + " is missing a getter for its ID field. This is required for API generation."));
    }

    private Method getEntityGeneratorFromRestModel(Class<?> dtoClass) {
        var annotatedMethods = Arrays.stream(dtoClass.getMethods())
                .filter(method -> method.isAnnotationPresent(EntityGenerator.class))
                .toList();

        if (annotatedMethods.isEmpty()) {
            throw new IllegalStateException("A Rest Model should have a method annotated with @EntityGenerator");
        } else if (annotatedMethods.size() > 1) {
            throw new IllegalStateException("A Rest Model should have exactly ONE method annotated with @EntityGenerator");
        }

        return annotatedMethods.getFirst();
    }

    public GeneratedClass writeNewController(String targetPackage, Class<?> restModel) throws IntrospectionException {

        if (!restModel.isAnnotationPresent(RestModel.class)) {
            throw new IllegalArgumentException("The class passed to write a new controller for must be annotated with @RestModel");
        }

        var restModelName = restModel.getSimpleName();
        var restModelPackage = restModel.getPackageName();

        var entityClass = restModel.getAnnotation(RestModel.class).dataClass();
        var modelGeneratorMethod = getModelGeneratorFromDataClass(entityClass);
        var entityGeneratorMethod = getEntityGeneratorFromRestModel(restModel);
        var entityIdGetterMethod = getIdGetterFromDataClass(entityClass);

        var sourceCode = String.format("""
                package %1$s;
                
                import io.john.amiscaray.backend.framework.core.di.provider.Instantiate;
                import io.john.amiscaray.backend.framework.web.handler.request.DynamicPathRequest;
                import io.john.amiscaray.backend.framework.web.handler.request.Request;
                import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
                import io.john.amiscaray.backend.framework.web.handler.response.Response;
                import %4$s.%2$s;
                import %6$s.%5$s;
                import io.john.amiscaray.backend.framework.data.DatabaseProxy;
                import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
                import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
                
                import java.util.HashMap;
                import java.util.List;
                
                @Controller
                public class %2$sController {
                
                    private DatabaseProxy databaseProxy;
                    
                    @Instantiate
                    public %2$sController(DatabaseProxy databaseProxy) {
                        this.databaseProxy = databaseProxy;
                    }
                    
                    @Handle(method = RequestMethod.POST, path = "/%3$s")
                    public Response<Void> save%2$s(Request<%2$s> request) {
                        var %3$s = request.body();
                        var entity = %2$s.%8$s(%3$s);
                        databaseProxy.persist(entity);
                        
                        var headers = new HashMap<String, String>();
                        headers.put("Location", "/%3$s/" + entity.%9$s());
                        
                        return new Response(headers, 201, null);
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/%3$s")
                    public Response<List<%2$s>> getAll(Request<Void> request) {
                        return Response.of(
                            databaseProxy.queryAll(%5$s.class)
                                .stream()
                                .map(%5$s::%7$s)
                                .toList()
                        );
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/%3$s/{id}")
                    public Response<%2$s> get%2$s(DynamicPathRequest<Void> request) {
                        var id = request.pathVariables().get("id");
                        var fetched = databaseProxy.fetchById(id, StudentTableEntry.class);
                        
                        if (fetched == null) {
                            return new Response(404, null);
                        }
                    
                        return Response.of(%5$s.%7$s(fetched));
                    }
                    
                    @Handle(method = RequestMethod.DELETE, path = "/%3$s/{id}")
                    public Response<Void> delete(DynamicPathRequest<Void> request) {
                        var id = request.pathVariables().get("id");
                        
                        try {
                            databaseProxy.delete(id, %5$s.class);
                        } catch (IllegalArgumentException e) {
                            return new Response(404, null);
                        }
                        
                        return new Response(204, null);
                    }
                
                }
                """, targetPackage,
                restModelName,
                restModelName.toLowerCase(),
                restModelPackage,
                entityClass.getSimpleName(),
                entityClass.getPackageName(),
                modelGeneratorMethod.getName(),
                entityGeneratorMethod.getName(),
                entityIdGetterMethod.getName());

        return new GeneratedClass(restModelName + "Controller.java", sourceCode);
    }

}
