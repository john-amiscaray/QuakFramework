package io.john.amiscaray;

import io.john.amiscaray.backend.framework.data.query.DatabaseQuery;
import io.john.amiscaray.backend.framework.generator.api.APIQuery;
import io.john.amiscaray.backend.framework.generator.api.EntityGenerator;
import io.john.amiscaray.backend.framework.generator.api.ModelGenerator;
import io.john.amiscaray.backend.framework.generator.api.RestModel;
import io.john.amiscaray.model.GeneratedClass;
import jakarta.persistence.Id;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

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

    private Field findIDFieldForDataClass(Class<?> dataClass) {
        return Arrays.stream(dataClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing an ID attribute for data class: " + dataClass.getName()));
    }

    private Method getIdGetterFromDataClass(Class<?> dataClass) throws IntrospectionException {
        var idField = findIDFieldForDataClass(dataClass);

        return new PropertyDescriptor(idField.getName(), dataClass).getReadMethod();
    }

    private Method getIdSetterFromDataClass(Class<?> dataClass) throws IntrospectionException {
        var idField = findIDFieldForDataClass(dataClass);

        return new PropertyDescriptor(idField.getName(), dataClass).getWriteMethod();
    }

    private String getStringToEntityIDConversionMethodName(Class<?> dataClass) {
        var idField = findIDFieldForDataClass(dataClass);
        String result = "String.valueOf";

        if (idField.getType().equals(Long.class)) {
            result = "Long.parseLong";
        } else if (idField.getType().equals(Integer.class)) {
            result = "Integer.parseInt";
        } else if (idField.getType().equals(Double.class)) {
            result = "Double.parseDouble";
        } else if (idField.getType().equals(Float.class)) {
            result = "Float.parseFloat";
        }

        return result;
    }

    private String getEntityIDTypeString(Class<?> dataClass) {
        var idField = findIDFieldForDataClass(dataClass);
        String result = "String";

        if (idField.getType().equals(Long.class)) {
            result = "Long";
        } else if (idField.getType().equals(Integer.class)) {
            result = "Integer";
        } else if (idField.getType().equals(Double.class)) {
            result = "Double";
        } else if (idField.getType().equals(Float.class)) {
            result = "Float";
        }

        return result;
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

    private List<Method> getAPIQueryMethods(Class<?> dataClass) {
        var annotatedMethods = Arrays.stream(dataClass.getMethods())
                .sorted(Comparator.comparing(Method::getName))
                .filter(method -> method.isAnnotationPresent(APIQuery.class))
                .toList();

        for (var method : annotatedMethods) {
            var errorMessage = "Method " + method.getName() + " annotated with @APIQuery should: ";
            var errors = new ArrayList<String>();

            if (!method.getReturnType().equals(DatabaseQuery.class)) {
                errors.add("be annotated with @APIQuery should return a DatabaseQuery");
            }

            if (method.getParameterCount() != 0) {
                errors.add("be annotated with @APIQuery should not have any parameters");
            }

            if (!Modifier.isStatic(method.getModifiers())) {
                errors.add("be static");
            }

            if (!errors.isEmpty()) {
                errorMessage += String.join(", ", errors) + ".";
                throw new IllegalStateException(errorMessage);
            }
        }

        return annotatedMethods;
    }

    private String generateAPIQueryEndpoints(String rootPathName, Class<?> dataClass, String restModelName, String restModelMappingMethodName) {
        var queryMethods = getAPIQueryMethods(dataClass);
        var dataClassName = dataClass.getSimpleName();
        var result = new StringBuilder();
        for (var queryMethod : queryMethods) {
            var queryPath = queryMethod.getAnnotation(APIQuery.class).path();
            if (queryPath.startsWith("/")) {
                queryPath = queryPath.substring(1);
            }
            var methodName = queryMethod.getName();
            result.append(String.format("""
                    @Handle(method = RequestMethod.GET, path = "/%1$s/%2$s")
                    public Response<List<%5$s>> %3$s(Request<Void> request) {
                        var query = %4$s.%3$s();
                        return Response.of(databaseProxy.queryAll(%4$s.class, query)
                            .stream()
                            .map(%4$s::%6$s)
                            .toList());
                    }
                    """, rootPathName, queryPath, methodName, dataClassName, restModelName, restModelMappingMethodName));
        }

        return result.toString().stripTrailing();
    }

    public GeneratedClass writeNewController(String targetPackage, Class<?> restModel) throws IntrospectionException {

        if (!restModel.isAnnotationPresent(RestModel.class)) {
            throw new IllegalArgumentException("The class passed to write a new controller for must be annotated with @RestModel");
        }

        var restModelName = restModel.getSimpleName();
        var restModelPackage = restModel.getPackageName();

        var entityClass = restModel.getAnnotation(RestModel.class).dataClass();
        var rootPath = restModelName.toLowerCase();
        var modelGeneratorMethod = getModelGeneratorFromDataClass(entityClass);
        var entityGeneratorMethod = getEntityGeneratorFromRestModel(restModel);
        var entityIDGetterMethod = getIdGetterFromDataClass(entityClass);
        var entityIDSetterMethod = getIdSetterFromDataClass(entityClass);
        var stringToEntityIDConversionMethod = getStringToEntityIDConversionMethodName(entityClass);
        var entityIDTypeString = getEntityIDTypeString(entityClass);
        var queryMethodImpls = generateAPIQueryEndpoints(rootPath, entityClass, restModelName, modelGeneratorMethod.getName()).indent(4);

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
                    public Response<List<%2$s>> getAll%2$s(Request<Void> request) {
                        return Response.of(
                            databaseProxy.queryAll(%5$s.class)
                                .stream()
                                .map(%5$s::%7$s)
                                .toList()
                        );
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/%3$s/{id}%12$s")
                    public Response<%2$s> get%2$s(DynamicPathRequest<Void> request) {
                        try {
                            var id = %11$s(request.pathVariables().get("id"));
                            var fetched = databaseProxy.fetchById(id, %5$s.class);
                            
                            if (fetched == null) {
                                return new Response(404, null);
                            }
                        
                            return Response.of(%5$s.%7$s(fetched));
                        } catch (NumberFormatException e) {
                            return new Response(404, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.DELETE, path = "/%3$s/{id}%12$s")
                    public Response<Void> delete%2$s(DynamicPathRequest<Void> request) {
                        try {
                            var id = %11$s(request.pathVariables().get("id"));
                            
                            try {
                                databaseProxy.delete(id, %5$s.class);
                            } catch (IllegalArgumentException e) {
                                return new Response(404, null);
                            }
                            
                            return new Response(204, null);
                        } catch (NumberFormatException e) {
                            return new Response(404, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.PUT, path = "/%3$s/{id}%12$s")
                    public Response<Void> put%2$s(DynamicPathRequest<%2$s> request) {
                        try {
                            var id = %11$s(request.pathVariables().get("id"));
                            
                            var entity = %2$s.%8$s(request.body());
                            entity.%10$s(id);
                            var isUpdate = databaseProxy.put(entity, id, %5$s.class);
                            
                            if (isUpdate) {
                                return new Response(204, null);
                            } else {
                                var headers = new HashMap<String, String>();
                                headers.put("Location", "/%3$s/" + entity.%9$s());
                                
                                return new Response(headers, 201, null);
                            }
                        } catch (NumberFormatException e) {
                            return new Response(404, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.PATCH, path = "/%3$s/{id}%12$s")
                    public Response<Void> patch%2$s(DynamicPathRequest<%2$s> request) {
                        try {
                            var id = %11$s(request.pathVariables().get("id"));
                            
                            var entity = %2$s.%8$s(request.body());
                            entity.%10$s(id);
                            var foundEntity = databaseProxy.patch(entity, id, %5$s.class);
                            
                            if (foundEntity) {
                                return new Response(204, null);
                            } else {
                                return new Response(404, null);
                            }
                        } catch (NumberFormatException e) {
                            return new Response(404, null);
                        }
                    }
                
                %13$s
                }
                """, targetPackage,
                restModelName,
                rootPath,
                restModelPackage,
                entityClass.getSimpleName(),
                entityClass.getPackageName(),
                modelGeneratorMethod.getName(),
                entityGeneratorMethod.getName(),
                entityIDGetterMethod.getName(),
                entityIDSetterMethod.getName(),
                stringToEntityIDConversionMethod,
                entityIDTypeString,
                queryMethodImpls);

        return new GeneratedClass(restModelName + "Controller.java", sourceCode);
    }

}
