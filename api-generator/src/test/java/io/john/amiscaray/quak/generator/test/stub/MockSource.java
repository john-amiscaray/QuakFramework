package io.john.amiscaray.quak.generator.test.stub;

public class MockSource {

    public static String studentRestModelSourceCode() {
        return """
                package io.john.amiscaray.stub.model;
                                
                import io.john.amiscaray.quak.generator.api.EntityGenerator;
                import io.john.amiscaray.quak.generator.api.RestModel;
                import io.john.amiscaray.stub.data.StudentTableEntry;
                import lombok.AllArgsConstructor;
                import lombok.Getter;
                import lombok.NoArgsConstructor;
                import lombok.Setter;
                                
                @RestModel(dataClass = StudentTableEntry.class)
                @Getter
                @Setter
                @AllArgsConstructor
                @NoArgsConstructor
                public class Student {
                                
                    private long id;
                    private String name;
                    private int age;
                    private float gpa;
                                
                    @EntityGenerator
                    public static StudentTableEntry toStudentEntry(Student student) {
                        return new StudentTableEntry(student.id, student.name, student.age, student.gpa);
                    }
                                
                }
                                
                """;
    }

    public static String studentTableSourceCode() {
        return """
                package io.john.amiscaray.stub.data;
                                
                import io.john.amiscaray.quak.generator.api.ModelGenerator;
                import io.john.amiscaray.stub.model.Student;
                import jakarta.persistence.Entity;
                import jakarta.persistence.Id;
                import jakarta.persistence.Table;
                import lombok.AllArgsConstructor;
                import lombok.Getter;
                import lombok.NoArgsConstructor;
                import lombok.Setter;
                                
                @Entity
                @Getter
                @Setter
                @AllArgsConstructor
                @NoArgsConstructor
                @Table(name = "Student")
                public class StudentTableEntry {
                                
                    @Id
                    private Long id;
                    private String name;
                    private int age;
                    private float gpa;
                                
                    @ModelGenerator
                    public static Student toStudentModel(StudentTableEntry entry) {
                        return new Student(entry.id, entry.name, entry.age, entry.gpa);
                    }
                                
                }         
                """;
    }

    public static String employeeRestModelSourceCode() {
        return """
                package io.john.amiscaray.stub.model;
                                
                import io.john.amiscaray.quak.generator.api.EntityGenerator;
                import io.john.amiscaray.quak.generator.api.RestModel;
                import io.john.amiscaray.stub.data.EmployeeTableEntry;
                import lombok.AllArgsConstructor;
                import lombok.Getter;
                import lombok.NoArgsConstructor;
                import lombok.Setter;
                                
                @RestModel(dataClass = EmployeeTableEntry.class)
                @Getter
                @Setter
                @AllArgsConstructor
                @NoArgsConstructor
                public class Employee {
                                
                    private String name;
                    private String department;
                    private String address;
                    private Long salary;
                                
                    @EntityGenerator
                    public static EmployeeTableEntry toEmployeeEntity(Employee employee) {
                        return new EmployeeTableEntry(null, employee.name, employee.department, employee.address, employee.salary);
                    }
                                
                }
                """;
    }

    public static String employeeTableSourceCode() {
        return """
                package io.john.amiscaray.stub.data;
                                
                import io.john.amiscaray.quak.data.query.DatabaseQuery;
                import io.john.amiscaray.quak.data.query.ValueIs;
                import io.john.amiscaray.quak.data.query.numeric.ValueGreaterThan;
                import io.john.amiscaray.quak.generator.api.APIQuery;
                import io.john.amiscaray.quak.generator.api.ModelGenerator;
                import io.john.amiscaray.stub.model.Employee;
                import jakarta.persistence.*;
                import lombok.AllArgsConstructor;
                import lombok.Getter;
                import lombok.NoArgsConstructor;
                import lombok.Setter;
                                
                @Entity
                @Getter
                @Setter
                @AllArgsConstructor
                @NoArgsConstructor
                @Table(name = "Employee")
                public class EmployeeTableEntry {
                                
                    @Id
                    @GeneratedValue(strategy = GenerationType.AUTO)
                    private Double id;
                    private String name;
                    private String department;
                    private String address;
                    private long salary;
                                
                    @ModelGenerator
                    public static Employee toEmployeeDTO(EmployeeTableEntry entry) {
                        return new Employee(entry.getName(), entry.getDepartment(), entry.getAddress(), entry.getSalary());
                    }
                                
                    @APIQuery(path = "sales")
                    public static DatabaseQuery queryEmployeesInSalesDepartment(DynamicPathRequest<Void> request) {
                        return DatabaseQuery.builder()
                                .withCriteria(new ValueIs("department", "sales"))
                                .build();
                                
                    }
                                
                    @APIQuery(path = "salary/high")
                    public static DatabaseQuery queryEmployeesWithHighSalaries(DynamicPathRequest<Void> request) {
                        return DatabaseQuery.builder()
                                .withCriteria(new ValueGreaterThan("salary", 100000))
                                .build();
                    }
                    
                    @APINativeQuery(path = "salary/low")
                    public static NativeQuery queryEmployeesWithSalariesLessThan(DynamicPathRequest<Void> request) {
                        return new NativeQuery("FROM Employee WHERE salary <= :salary", Map.of("salary", request.queryParams().get("income")));
                    }
                                
                }
                """;
    }

    public static String managedTypeSourceCode() {
        return """
               package io.john.amiscaray.domain;
               
               @ManagedType
               public class AppDetails {
                    private String name;
                    private String description;
                    
                    public AppDetails() {
                        this.name = "Hello World";
                        this.description = "This is a hello world app";
                    }
               }
                """;
    }

    public static String dependencyProviderSourceCode() {
        return """
                package io.john.amiscaray.quak.data.di;
                
                import io.john.amiscaray.quak.core.di.ApplicationContext;
                import io.john.amiscaray.quak.core.di.dependency.DependencyID;
                import io.john.amiscaray.quak.core.di.dependency.ProvidedDependency;
                import io.john.amiscaray.quak.core.di.provider.DependencyProvider;
                import io.john.amiscaray.quak.data.DatabaseProxy;
                                
                import java.util.List;
                                
                public class DatabaseProxyStartupDependencyProvider implements DependencyProvider<DatabaseProxy> {
                                
                    @Override
                    public DependencyID<DatabaseProxy> getDependencyID() {
                        return new DependencyID<>("databaseProxy", DatabaseProxy.class);
                    }
                                
                    @Override
                    public ProvidedDependency<DatabaseProxy> provideDependency(ApplicationContext context) {
                        var databaseProxy = new DatabaseProxy(context.getClassScanPackage());
                        databaseProxy.beginSession();
                                
                        return new ProvidedDependency<>(
                                new DependencyID<>("databaseProxy", DatabaseProxy.class),
                                databaseProxy
                                );
                    }
                                
                    @Override
                    public List<DependencyID<?>> getDependencies() {
                        return List.of();
                    }
                                
                }
                """;
    }

}
