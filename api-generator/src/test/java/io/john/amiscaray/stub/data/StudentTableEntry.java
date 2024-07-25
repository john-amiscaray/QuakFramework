package io.john.amiscaray.stub.data;

import io.john.amiscaray.backend.framework.generator.api.ModelGenerator;
import io.john.amiscaray.stub.model.Student;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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
