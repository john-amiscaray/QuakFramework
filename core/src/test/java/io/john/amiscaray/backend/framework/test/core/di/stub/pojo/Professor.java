package io.john.amiscaray.backend.framework.test.core.di.stub.pojo;

import io.john.amiscaray.backend.framework.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.ProvidedWith;

import java.util.List;

import static io.john.amiscaray.backend.framework.test.core.di.stub.MockProfessorDetailsProvider.*;
import static io.john.amiscaray.backend.framework.test.core.di.stub.pojo.Professor.DEPENDENCY_NAME;

@ManagedType(dependencyName = DEPENDENCY_NAME)
public record Professor (
        @ProvidedWith(dependencyName = BOBBERT) String name,
        @ProvidedWith(dependencyName = BOBBERT_DEPARTMENT) String department,
        @ProvidedWith(dependencyName = BOBBERT_COURSES) List<String> courses) {

    public static final String DEPENDENCY_NAME = "Bobbert";

    @Instantiate
    public Professor {

    }

}
