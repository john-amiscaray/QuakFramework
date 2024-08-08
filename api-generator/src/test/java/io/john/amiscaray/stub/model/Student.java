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
