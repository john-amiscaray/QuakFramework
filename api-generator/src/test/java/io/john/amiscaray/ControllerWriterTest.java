package io.john.amiscaray;

import io.john.amiscaray.model.GeneratedClass;
import io.john.amiscaray.stub.model.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerWriterTest {

    private static ControllerWriter controllerWriter;

    @BeforeAll
    public static void setUp() {
        controllerWriter = ControllerWriter.getInstance();
    }

    @Test
    public void testWriteControllerFromStudentRestModel() {
        var actualGenerated = controllerWriter.writeNewController("io.john.amiscaray.controllers", Student.class);

        assertEquals(new GeneratedClass(
                "StudentController.java",
                """
                package io.john.amiscaray.controllers;
                
                import io.john.amiscaray.stub.model.Student;
                import io.john.amiscaray.backend.framework.data.DatabaseProxy;
                import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
                import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
                
                @Controller
                public class StudentController {
                
                    private DatabaseProxy databaseProxy;
                    
                    public StudentController (DatabaseProxy databaseProxy) {
                        this.databaseProxy = databaseProxy;
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/")
                    public Response<List<Student>> getAll() {
                        return Response.of(databaseProxy.queryAll(Student.class));
                    }
                
                }
                """
        ), actualGenerated);
    }

}
