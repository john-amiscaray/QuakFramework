package io.john.amiscaray.backend.framework.test.core.di.stub.pojo;

import io.john.amiscaray.backend.framework.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.ProvidedWith;

@ManagedType
public record MockStudent(
        @ProvidedWith(dependencyName = "studentID") Long studentID,
        @ProvidedWith(dependencyName = "studentName") String name,
        @ProvidedWith(dependencyName = "gpa") Double gpa) {

    @Instantiate
    public MockStudent {

    }

}
