package io.john.amiscaray.controller;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import io.john.amiscaray.backend.framework.generator.api.APIQuery;
import io.john.amiscaray.backend.framework.generator.api.EntityGenerator;
import io.john.amiscaray.backend.framework.generator.api.ModelGenerator;
import io.john.amiscaray.backend.framework.generator.api.APINativeQuery;
import io.john.amiscaray.model.GeneratedClass;
import jakarta.persistence.Id;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static io.john.amiscaray.util.ParserUtils.getAnnotationMemberValue;

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

    private MethodDeclaration getModelGeneratorFromDataClass(ClassOrInterfaceDeclaration dataClass) {
        var annotatedMethods = dataClass.getMethods()
                .stream()
                .filter(method -> method.isAnnotationPresent(ModelGenerator.class))
                .toList();

        if (annotatedMethods.isEmpty()) {
            throw new IllegalStateException("A linked data class should have a method annotated with @ModelGenerator");
        } else if (annotatedMethods.size() > 1) {
            throw new IllegalStateException("A linked data class should have exactly ONE method annotated with @ModelGenerator");
        }

        return annotatedMethods.getFirst();
    }

    private FieldDeclaration findIDFieldForEntityClass(ClassOrInterfaceDeclaration entityClass) {
        return entityClass.getFields()
                .stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Missing an ID attribute for data class: " + entityClass.getNameAsString()));
    }

    private MethodDeclaration getIdGetterFromDataClass(ClassOrInterfaceDeclaration entityClass) {
        var idField = findIDFieldForEntityClass(entityClass);

        return entityClass.getMethodsByName("get" + idField)
                .stream()
                .filter(method -> method.getParameters().isEmpty())
                .findFirst()
                .orElse(idField.createGetter());
    }

    private MethodDeclaration getIdSetterFromDataClass(ClassOrInterfaceDeclaration entityClass) {
        var idField = findIDFieldForEntityClass(entityClass);
        return entityClass.getMethodsByName("set" + idField)
                .stream()
                .filter(method -> method.getParameters().size() == 1
                        && method.getParameterByType(idField.getElementType().asString()).isPresent())
                .findFirst()
                .orElse(idField.createSetter());
    }

    private String getStringToEntityIDConversionMethodName(ClassOrInterfaceDeclaration entityClass) {
        var idField = findIDFieldForEntityClass(entityClass);
        String result = "String.valueOf";

        if (idField.getElementType().asString().equals("Long")) {
            result = "Long.parseLong";
        } else if (idField.getElementType().asString().equals("Integer")) {
            result = "Integer.parseInt";
        } else if (idField.getElementType().asString().equals("Double")) {
            result = "Double.parseDouble";
        } else if (idField.getElementType().asString().equals("Float")) {
            result = "Float.parseFloat";
        }

        return result;
    }

    private String getEntityIDTypeString(ClassOrInterfaceDeclaration dataClass) {
        var idField = findIDFieldForEntityClass(dataClass);
        String result = "String";

        if (idField.getElementType().asString().equals("Long")) {
            result = "Long";
        } else if (idField.getElementType().asString().equals("Integer")) {
            result = "Integer";
        } else if (idField.getElementType().asString().equals("Double")) {
            result = "Double";
        } else if (idField.getElementType().asString().equals("Float")) {
            result = "Float";
        }

        return result;
    }

    private MethodDeclaration getEntityGeneratorFromRestModel(ClassOrInterfaceDeclaration restModelClass) {
        var annotatedMethods = restModelClass.getMethods()
                .stream()
                .filter(method -> method.isAnnotationPresent(EntityGenerator.class))
                .toList();

        if (annotatedMethods.isEmpty()) {
            throw new IllegalStateException("A Rest Model should have a method annotated with @EntityGenerator");
        } else if (annotatedMethods.size() > 1) {
            throw new IllegalStateException("A Rest Model should have exactly ONE method annotated with @EntityGenerator");
        }

        return annotatedMethods.getFirst();
    }

    private List<MethodDeclaration> getAPIQueryMethods(ClassOrInterfaceDeclaration entityClass) {
        var annotatedMethods = entityClass.getMethods()
                .stream()
                .sorted(Comparator.comparing(MethodDeclaration::getNameAsString))
                .filter(method -> method.isAnnotationPresent(APIQuery.class))
                .toList();

        for (var method : annotatedMethods) {
            var errorMessage = "Method " + method.getName() + " annotated with @APIQuery should: ";
            var errors = new ArrayList<String>();

            if (!method.getTypeAsString().equals("DatabaseQuery")) {
                errors.add("be annotated with @APIQuery should return a DatabaseQuery");
            }

            if (!method.getParameters().isEmpty()) {
                errors.add("be annotated with @APIQuery should not have any parameters");
            }

            if (!method.isStatic()) {
                errors.add("be static");
            }

            if (!errors.isEmpty()) {
                errorMessage += String.join(", ", errors) + ".";
                throw new IllegalStateException(errorMessage);
            }
        }

        return annotatedMethods;
    }

    private String generateAPIQueryEndpoints(String rootPathName, ClassOrInterfaceDeclaration entityClass, String restModelName, String restModelMappingMethodName) {
        var queryMethods = getAPIQueryMethods(entityClass);
        var dataClassName = entityClass.getNameAsString();
        var result = new StringBuilder();
        for (var queryMethod : queryMethods) {
            var queryPath = getAnnotationMemberValue(queryMethod.getAnnotationByName("APIQuery").orElseThrow(), "path").orElseThrow().asStringLiteralExpr().asString();
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

    private List<MethodDeclaration> getNativeQueryMethods(ClassOrInterfaceDeclaration entityClass) {
        var annotatedMethods = entityClass.getMethods()
                .stream()
                .sorted(Comparator.comparing(MethodDeclaration::getNameAsString))
                .filter(method -> method.isAnnotationPresent(APINativeQuery.class))
                .toList();

        for (var method : annotatedMethods) {
            var errorMessage = "Method " + method.getName() + " annotated with @APINativeQuery should: ";
            var errors = new ArrayList<String>();

            if (!method.getTypeAsString().equals("NativeQuery")) {
                errors.add("be annotated with @APINativeQuery should return a io.john.amiscaray.backend.framework.data.query.NativeQuery");
            }

            if (method.getParameters().size() != 1
                    && method.getParameters().getFirst().isPresent()
                    && method.getParameters().getFirst().get().getTypeAsString().equals("Request")
            ) {
                errors.add("be annotated with @APINativeQuery should have a single io.john.amiscaray.backend.framework.web.handler.request.Request parameter");
            }

            if (!method.isStatic()) {
                errors.add("be static");
            }

            if (!errors.isEmpty()) {
                errorMessage += String.join(", ", errors) + ".";
                throw new IllegalStateException(errorMessage);
            }
        }

        return annotatedMethods;
    }

    private String generateNativeQueryEndpoints(String rootPathName, ClassOrInterfaceDeclaration entityClass, String restModelName, String restModelMappingMethodName) {
        var queryMethods = getNativeQueryMethods(entityClass);
        var dataClassName = entityClass.getNameAsString();
        var result = new StringBuilder();
        for (var queryMethod : queryMethods) {
            var queryPath = getAnnotationMemberValue(queryMethod.getAnnotationByName("NativeQuery").orElseThrow(), "path").orElseThrow().asStringLiteralExpr().asString();
            if (queryPath.startsWith("/")) {
                queryPath = queryPath.substring(1);
            }
            var methodName = queryMethod.getName();
            result.append(String.format("""
                    @Handle(method = RequestMethod.GET, path = "/%1$s/%2$s")
                    public Response<List<%5$s>> %3$s(Request<Void> request) {
                        var query = %4$s.%3$s(request);
                        return Response.of(databaseProxy.createSelectionQuery(query, %4$s.class)
                            .getResultList()
                            .stream()
                            .map(%4$s::%6$s)
                            .toList());
                    }
                    """, rootPathName, queryPath, methodName, dataClassName, restModelName, restModelMappingMethodName));
        }

        return result.toString().stripTrailing();
    }

    public GeneratedClass writeNewController(String targetPackage,
                                             ClassOrInterfaceDeclaration restModelClass,
                                             ClassOrInterfaceDeclaration entityClass)  {

        var restModelAnnotationOpt = restModelClass.getAnnotationByName("RestModel");
        if (restModelAnnotationOpt.isEmpty()) {
            throw new IllegalArgumentException("The class passed to write a new controller for must be annotated with @RestModel");
        }

        var restModelName = restModelClass.getName().asString();

        var rootPath = restModelName.toLowerCase();
        var modelGeneratorMethod = getModelGeneratorFromDataClass(entityClass);
        var entityGeneratorMethod = getEntityGeneratorFromRestModel(restModelClass);
        var entityIDGetterMethod = getIdGetterFromDataClass(entityClass);
        var entityIDSetterMethod = getIdSetterFromDataClass(entityClass);
        var stringToEntityIDConversionMethod = getStringToEntityIDConversionMethodName(entityClass);
        var entityIDTypeString = getEntityIDTypeString(entityClass);
        var queryMethodImpls = generateAPIQueryEndpoints(rootPath, entityClass, restModelName, modelGeneratorMethod.getNameAsString()).indent(4);
        var nativeQueryMethodImpls = generateNativeQueryEndpoints(rootPath, entityClass, restModelName, modelGeneratorMethod.getNameAsString()).indent(4);

        var sourceCode = String.format("""
                package %1$s;
                
                import io.john.amiscaray.backend.framework.core.di.provider.annotation.Instantiate;
                import io.john.amiscaray.backend.framework.web.handler.request.DynamicPathRequest;
                import io.john.amiscaray.backend.framework.web.handler.request.Request;
                import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
                import io.john.amiscaray.backend.framework.web.handler.response.Response;
                import %4$s;
                import %6$s;
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
                
                %14$s
                }
                """, targetPackage,
                restModelName,
                rootPath,
                restModelClass.getFullyQualifiedName().orElseThrow(),
                entityClass.getNameAsString(),
                entityClass.getFullyQualifiedName().orElseThrow(),
                modelGeneratorMethod.getNameAsString(),
                entityGeneratorMethod.getNameAsString(),
                entityIDGetterMethod.getName(),
                entityIDSetterMethod.getName(),
                stringToEntityIDConversionMethod,
                entityIDTypeString,
                queryMethodImpls,
                nativeQueryMethodImpls);

        return new GeneratedClass(restModelName + "Controller.java", sourceCode);
    }

}
