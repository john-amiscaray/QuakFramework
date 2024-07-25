package io.john.amiscaray;

import io.john.amiscaray.backend.framework.generator.api.ModelGenerator;
import io.john.amiscaray.backend.framework.generator.api.RestModel;
import io.john.amiscaray.model.GeneratedClass;

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

    public GeneratedClass writeNewController(String targetPackage, Class<?> restModel) {

        if (!restModel.isAnnotationPresent(RestModel.class)) {
            throw new IllegalArgumentException("The class passed to write a new controller for must be annotated with @RestModel");
        }

        var restModelName = restModel.getSimpleName();
        var restModelPackage = restModel.getPackageName();

        var entityClass = restModel.getAnnotation(RestModel.class).dataClass();
        var modelGeneratorMethod = getModelGeneratorFromDataClass(entityClass);

        var sourceCode = String.format("""
                package %1$s;
                
                import %4$s.%2$s;
                import %6$s.%5$s;
                import io.john.amiscaray.backend.framework.data.DatabaseProxy;
                import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
                import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
                
                @Controller
                public class %2$sController {
                
                    private DatabaseProxy databaseProxy;
                    
                    public %2$sController(DatabaseProxy databaseProxy) {
                        this.databaseProxy = databaseProxy;
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/")
                    public Response<List<%2$s>> getAll() {
                        return Response.of(
                            databaseProxy.queryAll(%5$s.class)
                                .stream()
                                .map(%5$s::%7$s)
                                .toList()
                        );
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/%3$s/{id}")
                    public Response<Student> get%2$s(DynamicPathRequest<Void> request) {
                        var id = request.pathVariables().get("id");
                        var fetched = databaseProxy.fetchById(id, StudentTableEntry.class);
                        
                        if (fetched == null) {
                            return new Response(404, null);
                        }
                    
                        return Response.of(StudentTableEntry.toStudentModel(fetched));
                    }
                
                }
                """, targetPackage,
                restModelName,
                restModelName.toLowerCase(),
                restModelPackage,
                entityClass.getSimpleName(),
                entityClass.getPackageName(),
                modelGeneratorMethod.getName());

        return new GeneratedClass(restModelName + "Controller.java", sourceCode);
    }

}
