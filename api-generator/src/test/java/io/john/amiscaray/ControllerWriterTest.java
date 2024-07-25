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
                import io.john.amiscaray.stub.data.StudentTableEntry;
                import io.john.amiscaray.backend.framework.data.DatabaseProxy;
                import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
                import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
                
                @Controller
                public class StudentController {
                
                    private DatabaseProxy databaseProxy;
                    
                    public StudentController(DatabaseProxy databaseProxy) {
                        this.databaseProxy = databaseProxy;
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/")
                    public Response<List<Student>> getAll() {
                        return Response.of(
                            databaseProxy.queryAll(StudentTableEntry.class)
                                .stream()
                                .map(StudentTableEntry::toStudentModel)
                                .toList()
                        );
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/student/{id}")
                    public Response<Student> getStudent(DynamicPathRequest<Void> request) {
                        var id = request.pathVariables().get("id");
                        var fetched = databaseProxy.fetchById(id, StudentTableEntry.class);
                        
                        if (fetched == null) {
                            return new Response(404, null);
                        }
                    
                        return Response.of(StudentTableEntry.toStudentModel(fetched));
                    }
                
                }
                """
        ), actualGenerated);
    }

}
