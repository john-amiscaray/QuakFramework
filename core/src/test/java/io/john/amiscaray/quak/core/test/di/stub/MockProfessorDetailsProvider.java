package io.john.amiscaray.quak.core.test.di.stub;

import io.john.amiscaray.quak.core.di.provider.annotation.Provide;
import io.john.amiscaray.quak.core.di.provider.annotation.Provider;

import java.util.List;

import static io.john.amiscaray.quak.core.test.di.stub.MockProfessorDetailsProvider.DEPENDENCY_NAME;

@Provider(dependencyName = DEPENDENCY_NAME)
public class MockProfessorDetailsProvider {

    public static final String DEPENDENCY_NAME = "BobbertDetailsProvider";
    public static final String BOBBERT = "Bobbert";
    public static final String BOBBERT_DEPARTMENT = "Bobbert Department";
    public static final String BOBBERT_COURSES = "Bobbert Courses";

    @Provide(dependencyName = BOBBERT)
    public String name() {
        return "Bobbert Hunt";
    }

    @Provide(dependencyName = BOBBERT_DEPARTMENT)
    public String department() {
        return "Computer Science";
    }

    @Provide(dependencyName = BOBBERT_COURSES)
    public List<String> courses() {
        return List.of("CPS613", "CPS714");
    }

}
