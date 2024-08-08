package io.john.amiscaray;

import io.john.amiscaray.model.GeneratedClass;
import io.john.amiscaray.stub.model.Employee;
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
                    public Response<List<Student>> getAllStudent(Request<Void> request) {
                        return Response.of(
                            databaseProxy.queryAll(StudentTableEntry.class)
                                .stream()
                                .map(StudentTableEntry::toStudentModel)
                                .toList()
                        );
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/student/{id}")
                    public Response<Student> getStudent(DynamicPathRequest<Void> request) {
                        var id = Long.parseLong(request.pathVariables().get("id"));
                        var fetched = databaseProxy.fetchById(id, StudentTableEntry.class);
                        
                        if (fetched == null) {
                            return new Response(404, null);
                        }
                    
                        return Response.of(StudentTableEntry.toStudentModel(fetched));
                    }
                    
                    @Handle(method = RequestMethod.DELETE, path = "/student/{id}")
                    public Response<Void> deleteStudent(DynamicPathRequest<Void> request) {
                        var id = Long.parseLong(request.pathVariables().get("id"));
                        
                        try {
                            databaseProxy.delete(id, StudentTableEntry.class);
                        } catch (IllegalArgumentException e) {
                            return new Response(404, null);
                        }
                        
                        return new Response(204, null);
                    }
                    
                    @Handle(method = RequestMethod.PUT, path = "/student/{id}")
                    public Response<Void> putStudent(DynamicPathRequest<Student> request) {
                        var id = Long.parseLong(request.pathVariables().get("id"));
                        
                        var entity = Student.toStudentEntry(request.body());
                        entity.setId(id);
                        var isUpdate = databaseProxy.put(entity, id, StudentTableEntry.class);
                        
                        if (isUpdate) {
                            return new Response(204, null);
                        } else {
                            var headers = new HashMap<String, String>();
                            headers.put("Location", "/student/" + entity.getId());
                            
                            return new Response(headers, 201, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.PATCH, path = "/student/{id}")
                    public Response<Void> patchStudent(DynamicPathRequest<Student> request) {
                        var id = Long.parseLong(request.pathVariables().get("id"));
                        
                        var entity = Student.toStudentEntry(request.body());
                        entity.setId(id);
                        var foundEntity = databaseProxy.patch(entity, id, StudentTableEntry.class);
                        
                        if (foundEntity) {
                            return new Response(204, null);
                        } else {
                            return new Response(404, null);
                        }
                    }
                
                
                }
                """
        ), actualGenerated);
    }

    @Test
    public void testWriteControllerFromEmployeeRestModel() throws IntrospectionException {
        var actualGenerated = controllerWriter.writeNewController("io.john.amiscaray.controllers", Employee.class);

        assertEquals(new GeneratedClass(
                "EmployeeController.java",
                """
                package io.john.amiscaray.controllers;
                
                import io.john.amiscaray.backend.framework.core.di.provider.Instantiate;
                import io.john.amiscaray.backend.framework.web.handler.request.DynamicPathRequest;
                import io.john.amiscaray.backend.framework.web.handler.request.Request;
                import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
                import io.john.amiscaray.backend.framework.web.handler.response.Response;
                import io.john.amiscaray.stub.model.Employee;
                import io.john.amiscaray.stub.data.EmployeeTableEntry;
                import io.john.amiscaray.backend.framework.data.DatabaseProxy;
                import io.john.amiscaray.backend.framework.web.controller.annotation.Controller;
                import io.john.amiscaray.backend.framework.web.handler.annotation.Handle;
                
                import java.util.HashMap;
                import java.util.List;
                
                @Controller
                public class EmployeeController {
                
                    private DatabaseProxy databaseProxy;
                    
                    @Instantiate
                    public EmployeeController(DatabaseProxy databaseProxy) {
                        this.databaseProxy = databaseProxy;
                    }
                    
                    @Handle(method = RequestMethod.POST, path = "/employee")
                    public Response<Void> saveEmployee(Request<Employee> request) {
                        var employee = request.body();
                        var entity = Employee.toEmployeeEntity(employee);
                        databaseProxy.persist(entity);
                        
                        var headers = new HashMap<String, String>();
                        headers.put("Location", "/employee/" + entity.getId());
                        
                        return new Response(headers, 201, null);
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/employee")
                    public Response<List<Employee>> getAllEmployee(Request<Void> request) {
                        return Response.of(
                            databaseProxy.queryAll(EmployeeTableEntry.class)
                                .stream()
                                .map(EmployeeTableEntry::toEmployeeDTO)
                                .toList()
                        );
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/employee/{id}")
                    public Response<Employee> getEmployee(DynamicPathRequest<Void> request) {
                        var id = Double.parseDouble(request.pathVariables().get("id"));
                        var fetched = databaseProxy.fetchById(id, EmployeeTableEntry.class);
                        
                        if (fetched == null) {
                            return new Response(404, null);
                        }
                    
                        return Response.of(EmployeeTableEntry.toEmployeeDTO(fetched));
                    }
                    
                    @Handle(method = RequestMethod.DELETE, path = "/employee/{id}")
                    public Response<Void> deleteEmployee(DynamicPathRequest<Void> request) {
                        var id = Double.parseDouble(request.pathVariables().get("id"));
                        
                        try {
                            databaseProxy.delete(id, EmployeeTableEntry.class);
                        } catch (IllegalArgumentException e) {
                            return new Response(404, null);
                        }
                        
                        return new Response(204, null);
                    }
                    
                    @Handle(method = RequestMethod.PUT, path = "/employee/{id}")
                    public Response<Void> putEmployee(DynamicPathRequest<Employee> request) {
                        var id = Double.parseDouble(request.pathVariables().get("id"));
                        
                        var entity = Employee.toEmployeeEntity(request.body());
                        entity.setId(id);
                        var isUpdate = databaseProxy.put(entity, id, EmployeeTableEntry.class);
                        
                        if (isUpdate) {
                            return new Response(204, null);
                        } else {
                            var headers = new HashMap<String, String>();
                            headers.put("Location", "/employee/" + entity.getId());
                            
                            return new Response(headers, 201, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.PATCH, path = "/employee/{id}")
                    public Response<Void> patchEmployee(DynamicPathRequest<Employee> request) {
                        var id = Double.parseDouble(request.pathVariables().get("id"));
                        
                        var entity = Employee.toEmployeeEntity(request.body());
                        entity.setId(id);
                        var foundEntity = databaseProxy.patch(entity, id, EmployeeTableEntry.class);
                        
                        if (foundEntity) {
                            return new Response(204, null);
                        } else {
                            return new Response(404, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/employee/sales")
                    public Response<List<Employee>> queryEmployeesInSalesDepartment(Request<Void> request) {
                        var query = EmployeeTableEntry.queryEmployeesInSalesDepartment();
                        return Response.of(databaseProxy.queryAll(EmployeeTableEntry.class, query)
                            .stream()
                            .map(EmployeeTableEntry::toEmployeeDTO)
                            .toList());
                    }
                    @Handle(method = RequestMethod.GET, path = "/employee/salary/high")
                    public Response<List<Employee>> queryEmployeesWithHighSalaries(Request<Void> request) {
                        var query = EmployeeTableEntry.queryEmployeesWithHighSalaries();
                        return Response.of(databaseProxy.queryAll(EmployeeTableEntry.class, query)
                            .stream()
                            .map(EmployeeTableEntry::toEmployeeDTO)
                            .toList());
                    }
                
                }
                """
        ), actualGenerated);
    }

}
