package io.john.amiscaray.quak.generator.test.controller;

import io.john.amiscaray.quak.generator.controller.ControllerWriter;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.john.amiscaray.quak.generator.test.assertions.TestSourceUtil.parsedClassOrInterfaceDeclarationOf;
import static io.john.amiscaray.quak.generator.test.stub.MockSource.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;

public class ControllerWriterTest {

    private static ControllerWriter controllerWriter;

    @BeforeAll
    public static void setUp() {
        controllerWriter = ControllerWriter.getInstance();
    }

    @Test
    public void testWriteControllerFromStudentRestModel() {
        var actualGenerated = controllerWriter.writeNewController(
                "io.john.amiscaray.controllers",
                parsedClassOrInterfaceDeclarationOf(studentRestModelSourceCode()),
                parsedClassOrInterfaceDeclarationOf(studentTableSourceCode()));

        MatcherAssert.assertThat(actualGenerated.name(), equalTo("StudentController.java"));
        MatcherAssert.assertThat(actualGenerated.sourceCode(), equalToCompressingWhiteSpace(
                """
                package io.john.amiscaray.controllers;
                
                import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
                import io.john.amiscaray.quak.http.request.DynamicPathRequest;
                import io.john.amiscaray.quak.http.request.Request;
                import io.john.amiscaray.quak.http.request.RequestMethod;
                import io.john.amiscaray.quak.http.response.Response;
                import io.john.amiscaray.stub.model.Student;
                import io.john.amiscaray.stub.data.StudentTableEntry;
                import io.john.amiscaray.quak.data.DatabaseProxy;
                import io.john.amiscaray.quak.web.controller.annotation.Controller;
                import io.john.amiscaray.quak.web.handler.annotation.Handle;
                
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
                    
                    @Handle(method = RequestMethod.GET, path = "/student/{id}Long")
                    public Response<Student> getStudent(DynamicPathRequest<Void> request) {
                        try {
                            var id = Long.parseLong(request.pathVariables().get("id"));
                            var fetched = databaseProxy.fetchById(id, StudentTableEntry.class);
                            
                            if (fetched == null) {
                                return new Response(404, null);
                            }
                        
                            return Response.of(StudentTableEntry.toStudentModel(fetched));
                        } catch (NumberFormatException e) {
                            return new Response(404, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.DELETE, path = "/student/{id}Long")
                    public Response<Void> deleteStudent(DynamicPathRequest<Void> request) {
                        try {
                            var id = Long.parseLong(request.pathVariables().get("id"));
                            
                            try {
                                databaseProxy.delete(id, StudentTableEntry.class);
                            } catch (IllegalArgumentException e) {
                                return new Response(404, null);
                            }
                            
                            return new Response(204, null);
                        } catch (NumberFormatException e) {
                            return new Response(404, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.PUT, path = "/student/{id}Long")
                    public Response<Void> putStudent(DynamicPathRequest<Student> request) {
                        try {
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
                        } catch (NumberFormatException e) {
                            return new Response(404, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.PATCH, path = "/student/{id}Long")
                    public Response<Void> patchStudent(DynamicPathRequest<Student> request) {
                        try {
                            var id = Long.parseLong(request.pathVariables().get("id"));
                            
                            var entity = Student.toStudentEntry(request.body());
                            entity.setId(id);
                            var foundEntity = databaseProxy.patch(entity, id, StudentTableEntry.class);
                            
                            if (foundEntity) {
                                return new Response(204, null);
                            } else {
                                return new Response(404, null);
                            }
                        } catch (NumberFormatException e) {
                            return new Response(404, null);
                        }
                    }
                
                
                }
                """
        ));
    }

    @Test
    public void testWriteControllerFromEmployeeRestModel() {
        var actualGenerated = controllerWriter.writeNewController(
                "io.john.amiscaray.controllers",
                parsedClassOrInterfaceDeclarationOf(employeeRestModelSourceCode()),
                parsedClassOrInterfaceDeclarationOf(employeeTableSourceCode())
                );

        MatcherAssert.assertThat(actualGenerated.name(), equalTo("EmployeeController.java"));
        MatcherAssert.assertThat(actualGenerated.sourceCode(), equalToCompressingWhiteSpace(
                """
                package io.john.amiscaray.controllers;
                
                import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
                import io.john.amiscaray.quak.http.request.DynamicPathRequest;
                import io.john.amiscaray.quak.http.request.Request;
                import io.john.amiscaray.quak.http.request.RequestMethod;
                import io.john.amiscaray.quak.http.response.Response;
                import io.john.amiscaray.stub.model.Employee;
                import io.john.amiscaray.stub.data.EmployeeTableEntry;
                import io.john.amiscaray.quak.data.DatabaseProxy;
                import io.john.amiscaray.quak.web.controller.annotation.Controller;
                import io.john.amiscaray.quak.web.handler.annotation.Handle;
                
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
                    
                    @Handle(method = RequestMethod.GET, path = "/employee/{id}Double")
                    public Response<Employee> getEmployee(DynamicPathRequest<Void> request) {
                        try {
                            var id = Double.parseDouble(request.pathVariables().get("id"));
                            var fetched = databaseProxy.fetchById(id, EmployeeTableEntry.class);
                            
                            if (fetched == null) {
                                return new Response(404, null);
                            }
                        
                            return Response.of(EmployeeTableEntry.toEmployeeDTO(fetched));
                        } catch (NumberFormatException e) {
                            return new Response(404, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.DELETE, path = "/employee/{id}Double")
                    public Response<Void> deleteEmployee(DynamicPathRequest<Void> request) {
                        try {
                            var id = Double.parseDouble(request.pathVariables().get("id"));
                            
                            try {
                                databaseProxy.delete(id, EmployeeTableEntry.class);
                            } catch (IllegalArgumentException e) {
                                return new Response(404, null);
                            }
                            
                            return new Response(204, null);
                        } catch (NumberFormatException e) {
                            return new Response(404, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.PUT, path = "/employee/{id}Double")
                    public Response<Void> putEmployee(DynamicPathRequest<Employee> request) {
                        try {
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
                        } catch (NumberFormatException e) {
                            return new Response(404, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.PATCH, path = "/employee/{id}Double")
                    public Response<Void> patchEmployee(DynamicPathRequest<Employee> request) {
                        try {
                            var id = Double.parseDouble(request.pathVariables().get("id"));
                            
                            var entity = Employee.toEmployeeEntity(request.body());
                            entity.setId(id);
                            var foundEntity = databaseProxy.patch(entity, id, EmployeeTableEntry.class);
                            
                            if (foundEntity) {
                                return new Response(204, null);
                            } else {
                                return new Response(404, null);
                            }
                        } catch (NumberFormatException e) {
                            return new Response(404, null);
                        }
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/employee/sales")
                    public Response<List<Employee>> queryEmployeesInSalesDepartment(DynamicPathRequest<Void> request) {
                        var query = EmployeeTableEntry.queryEmployeesInSalesDepartment(request);
                        return Response.of(databaseProxy.queryAll(EmployeeTableEntry.class, query)
                            .stream()
                            .map(EmployeeTableEntry::toEmployeeDTO)
                            .toList());
                    }
                    
                    @Handle(method = RequestMethod.GET, path = "/employee/salary/high")
                    public Response<List<Employee>> queryEmployeesWithHighSalaries(DynamicPathRequest<Void> request) {
                        var query = EmployeeTableEntry.queryEmployeesWithHighSalaries(request);
                        return Response.of(databaseProxy.queryAll(EmployeeTableEntry.class, query)
                            .stream()
                            .map(EmployeeTableEntry::toEmployeeDTO)
                            .toList());
                    }
                
                    @Handle(method = RequestMethod.GET, path = "/employee/salary/low")
                    public Response<List<Employee>> queryEmployeesWithSalariesLessThan(DynamicPathRequest<Void> request) {
                        var query = EmployeeTableEntry.queryEmployeesWithSalariesLessThan(request);
                        return Response.of(databaseProxy.createSelectionQuery(query, EmployeeTableEntry.class)
                            .getResultList()
                            .stream()
                            .map(EmployeeTableEntry::toEmployeeDTO)
                            .toList());
                    }
                }
                """
        ));
    }

}
