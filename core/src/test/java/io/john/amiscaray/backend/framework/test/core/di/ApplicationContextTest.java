package io.john.amiscaray.backend.framework.test.core.di;

import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.test.core.di.stub.*;
import io.john.amiscaray.backend.framework.test.core.di.stub.pojo.*;
import io.john.amiscaray.backend.framework.core.Application;
import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.john.amiscaray.backend.framework.test.core.di.stub.pojo.MockEmployee.mockEmployee;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class ApplicationContextTest {

    private static final Application application = new Application(ApplicationContextTest.class, new String[] {}) {
        @Override
        public void finish() {

        }

        @Override
        protected void startUp() {

        }
    };
    private static final MockStringProvider stringProvider = new MockStringProvider();
    private static final MockUserProvider userProvider = new MockUserProvider(stringProvider.username());
    private static final MockProfessorDetailsProvider professorDetailsProvider = new MockProfessorDetailsProvider();
    private static final MockUserAccountDetailsProvider accountDetailsProvider = new MockUserAccountDetailsProvider();
    private static final MockUserAccountProvider userAccountProvider = new MockUserAccountProvider(
            userProvider.getUser(),
            accountDetailsProvider.createdOn(),
            accountDetailsProvider.balance(),
            stringProvider.accountName()
    );
    private static final MockStudentDetailsProvider studentDetailsProvider = new MockStudentDetailsProvider();
    private static ApplicationContext ctx;

    @BeforeAll
    public static void setUp() throws Exception {

        application.start();
        ctx = ApplicationContext.getInstance();

    }

    @Test
    public void testProvideSimpleString() {

        assertEquals(stringProvider.username(), ctx.getInstance(new DependencyID<>("username", String.class)));

    }

    @Test
    public void testProvideGreeting() {

        assertEquals(stringProvider.greeting(
                stringProvider.username(),
                stringProvider.accountName(),
                userAccountProvider.userAccount().balance()
        ), ctx.getInstance(new DependencyID<>("greeting", String.class)));

    }

    @Test
    public void testProvideAccountString() {

        Assertions.assertEquals(userAccountProvider.userAccount().toString(), ctx.getInstance(new DependencyID<>("accountString", String.class)));

    }

    @Test
    public void testProvideMockUser() {

        Assertions.assertEquals(userProvider.getUser(), ctx.getInstance(MockUser.class));

    }

    @Test
    public void testProvideMockUserAccount() {

        Assertions.assertEquals(userAccountProvider.userAccount(), ctx.getInstance(MockUserAccount.class));

    }

    @Test
    public void testProvideMockStudent() {

        Assertions.assertEquals(new MockStudent(
                studentDetailsProvider.studentID(),
                studentDetailsProvider.name(),
                studentDetailsProvider.gpa()
        ), ctx.getInstance(MockStudent.class));

    }

    @Test
    public void testProvideMockEmployeeFromStartupDependencyProvider() {

        Assertions.assertEquals(
                mockEmployee(),
                ctx.getInstance(MockEmployee.class)
        );

    }

    @Test
    public void testAggregateListOfEmployeeFromEmployeeStartupDependencyProvider() {

        assertThat(
                ctx.getAggregateDependencies("EmployeeProviders", MockEmployee.class),
                contains(mockEmployee())
        );

    }

    @Test
    public void testProvideAggregateDependencyStrings() {

        assertThat(
                ctx.getAggregateDependencies("Strings", String.class),
                containsInAnyOrder(stringProvider.username(), stringProvider.accountName())
                );

    }

    @Test
    public void testProvideAggregateDependencyUserAccounts() {

        assertThat(
                ctx.getAggregateDependencies("userAccounts", MockUserAccount.class),
                contains(userAccountProvider.userAccount())
        );

    }

    @Test
    public void testProvideAggregateDependencyForUserAccountProvidersNamedUserAccounts() {

        assertThat(
                ctx.getAggregateDependencies("userAccounts", MockUserAccountProvider.class),
                contains(userAccountProvider)
        );

    }

    @Test
    public void testGetNamedManagedTypeForProfessor() {
        assertThat(
                ctx.getInstance(new DependencyID<>(Professor.DEPENDENCY_NAME, Professor.class)),
                equalTo(new Professor(professorDetailsProvider.name(), professorDetailsProvider.department(), professorDetailsProvider.courses()))
        );
    }

    @Test
    public void testGetNamedProviderForProfessorDetailsProvider() {
        assertThat(
                ctx.hasInstance(new DependencyID<>(MockProfessorDetailsProvider.DEPENDENCY_NAME, MockProfessorDetailsProvider.class)),
                is(true)
        );
    }

}