package io.john.amiscaray.backend.framework.core.di.stub;

import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;

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
