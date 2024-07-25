package io.john.amiscaray;

import io.john.amiscaray.model.GeneratedClass;

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

    public GeneratedClass writeNewController(String targetPackage, Class<?> restModel) {
        var restModelName = restModel.getSimpleName();
        var restModelPackage = restModel.getPackageName();
        var sourceCode = String.format("""
                package %1$s;
                
                import %3$s.%2$s;
                import io.john.amiscaray.backend.framework.data.DatabaseProxy;
                import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
                import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
                
                @Controller
                public class %2$sController {
                
                    private DatabaseProxy databaseProxy;
                    
                    public %2$sController (DatabaseProxy databaseProxy) {
                        this.databaseProxy = databaseProxy;
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/")
                    public Response<List<%2$s>> getAll() {
                        return Response.of(databaseProxy.queryAll(%2$s.class));
                    }
                
                }
                """, targetPackage, restModelName, restModelPackage);

        return new GeneratedClass(restModelName + "Controller.java", sourceCode);
    }

}
