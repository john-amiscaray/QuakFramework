package io.john.amiscaray.stub;

public class MockSource {

    public static String studentRestModelSourceCode() {
        return """
                package io.john.amiscaray.stub.model;
                                
                import io.john.amiscaray.backend.framework.generator.api.EntityGenerator;
                import io.john.amiscaray.backend.framework.generator.api.RestModel;
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
                                
                import io.john.amiscaray.backend.framework.generator.api.ModelGenerator;
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
                                
                import io.john.amiscaray.backend.framework.generator.api.EntityGenerator;
                import io.john.amiscaray.backend.framework.generator.api.RestModel;
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
                                
                import io.john.amiscaray.backend.framework.data.query.DatabaseQuery;
                import io.john.amiscaray.backend.framework.data.query.ValueIs;
                import io.john.amiscaray.backend.framework.data.query.numeric.ValueGreaterThan;
                import io.john.amiscaray.backend.framework.generator.api.APIQuery;
                import io.john.amiscaray.backend.framework.generator.api.ModelGenerator;
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
                    public static DatabaseQuery queryEmployeesInSalesDepartment() {
                        return DatabaseQuery.builder()
                                .withCriteria(new ValueIs("department", "sales"))
                                .build();
                                
                    }
                                
                    @APIQuery(path = "salary/high")
                    public static DatabaseQuery queryEmployeesWithHighSalaries() {
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

}
