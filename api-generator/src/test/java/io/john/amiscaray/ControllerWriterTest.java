package io.john.amiscaray;

import io.john.amiscaray.model.GeneratedClass;
import io.john.amiscaray.stub.model.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.beans.IntrospectionException;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerWriterTest {

    private static ControllerWriter controllerWriter;

    @BeforeAll
    public static void setUp() {
        controllerWriter = ControllerWriter.getInstance();
    }

    @Test
    public void testWriteControllerFromStudentRestModel() throws IntrospectionException {
        var actualGenerated = controllerWriter.writeNewController("io.john.amiscaray.controllers", Student.class);

        assertEquals(new GeneratedClass(
                "StudentController.java",
                """
                package io.john.amiscaray.controllers;
                
                import io.john.amiscaray.backend.framework.core.di.provider.Instantiate;
                import io.john.amiscaray.backend.framework.web.handler.request.DynamicPathRequest;
                import io.john.amiscaray.backend.framework.web.handler.request.Request;
                import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
                import io.john.amiscaray.backend.framework.web.handler.response.Response;
                import io.john.amiscaray.stub.model.Student;
                import io.john.amiscaray.stub.data.StudentTableEntry;
                import io.john.amiscaray.backend.framework.data.DatabaseProxy;
                import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
                import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
                
                import java.util.HashMap;
                import java.util.List;
                
                @Controller
                public class StudentController {
                
                    private DatabaseProxy databaseProxy;
                    
                    @Instantiate
                    public StudentController(DatabaseProxy databaseProxy) {
                        this.databaseProxy = databaseProxy;
                    }
                    
                    @Handle(method = RequestMethod.POST, path = "/student")
                    public Response<Void> saveStudent(Request<Student> request) {
                        var student = request.body();
                        var entity = Student.toStudentEntry(student);
                        databaseProxy.persist(entity);
                        
                        var headers = new HashMap<String, String>();
                        headers.put("Location", "/student/" + entity.getId());
                        
                        return new Response(headers, 201, null);
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/student")
                    public Response<List<Student>> getAll(Request<Void> request) {
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
                    
                    @Handle(method = RequestMethod.DELETE, path = "/student/{id}")
                    public Response<Void> delete(DynamicPathRequest<Void> request) {
                        var id = request.pathVariables().get("id");
                        
                        try {
                            databaseProxy.delete(id, StudentTableEntry.class);
                        } catch (IllegalArgumentException e) {
                            return new Response(404, null);
                        }
                        
                        return new Response(204, null);
                    }
                
                }
                """
        ), actualGenerated);
    }

}
