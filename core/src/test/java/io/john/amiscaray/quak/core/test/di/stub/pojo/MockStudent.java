package io.john.amiscaray.quak.core.test.di.stub.pojo;

import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.core.di.provider.annotation.ProvidedWith;

@ManagedType
public record MockStudent(
        @ProvidedWith(dependencyName = "studentID") Long studentID,
        @ProvidedWith(dependencyName = "studentName") String name,
        @ProvidedWith(dependencyName = "gpa") Double gpa) {

    @Instantiate
    public MockStudent {
        var applicationContext = ApplicationContext.getInstance();
    }

}
