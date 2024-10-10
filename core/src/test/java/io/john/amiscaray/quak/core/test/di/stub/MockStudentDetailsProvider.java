package io.john.amiscaray.quak.core.test.di.stub;

import io.john.amiscaray.quak.core.di.provider.annotation.Provide;
import io.john.amiscaray.quak.core.di.provider.annotation.Provider;

@Provider
public class MockStudentDetailsProvider {

    @Provide(dependencyName = "studentID")
    public Long studentID() {
        return 123L;
    }

    @Provide(dependencyName = "studentName")
    public String name() {
        return "John";
    }

    @Provide(dependencyName = "gpa")
    public double gpa() {
        return 4.0;
    }

}
