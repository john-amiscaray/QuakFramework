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

    @EntityGenerator
    public static EmployeeTableEntry toEmployeeEntity(Employee employee) {
        return new EmployeeTableEntry(null, employee.name, employee.department, employee.address);
    }

}
