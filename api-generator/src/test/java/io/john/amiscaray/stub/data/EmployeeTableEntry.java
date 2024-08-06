package io.john.amiscaray.stub.data;

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

    @ModelGenerator
    public static Employee toEmployeeDTO(EmployeeTableEntry entry) {
        return new Employee(entry.getName(), entry.getDepartment(), entry.getAddress());
    }

}
