package io.john.amiscaray.quak.core.test.di.stub.pojo;

import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.core.di.provider.annotation.ProvidedWith;
import io.john.amiscaray.quak.core.test.di.stub.MockProfessorDetailsProvider;

import java.util.List;

import static io.john.amiscaray.quak.core.test.di.stub.pojo.Professor.DEPENDENCY_NAME;

@ManagedType(dependencyName = DEPENDENCY_NAME)
public record Professor (
        @ProvidedWith(dependencyName = MockProfessorDetailsProvider.BOBBERT) String name,
        @ProvidedWith(dependencyName = MockProfessorDetailsProvider.BOBBERT_DEPARTMENT) String department,
        @ProvidedWith(dependencyName = MockProfessorDetailsProvider.BOBBERT_COURSES) List<String> courses) {

    public static final String DEPENDENCY_NAME = "Bobbert";

    @Instantiate
    public Professor {

    }

}
