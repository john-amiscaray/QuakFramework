package io.john.amiscaray.backend.framework.core.di;

import io.john.amiscaray.backend.framework.core.Application;
import io.john.amiscaray.backend.framework.core.di.dependency.Dependency;
import io.john.amiscaray.backend.framework.core.di.stub.*;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockUser;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockUserAccount;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockStudent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationContextTest {

    private static final Application application = new Application(ApplicationContext.class, new String[] {}) {
        @Override
        public void finish() {

        }

        @Override
        protected void startUp() {

        }
    };
    private static final MockStringProvider stringProvider = new MockStringProvider();
    private static final MockUserProvider userProvider = new MockUserProvider(stringProvider.username());
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

        assertEquals(stringProvider.username(), ctx.getInstance(new Dependency<>("username", String.class)));

    }

    @Test
    public void testProvideMockUser() {

        assertEquals(userProvider.getUser(), ctx.getInstance(MockUser.class));

    }

    @Test
    public void testProvideMockUserAccount() {

        assertEquals(userAccountProvider.userAccount(), ctx.getInstance(MockUserAccount.class));

    }

    @Test
    public void testProvideMockStudent() {

        assertEquals(new MockStudent(
                studentDetailsProvider.studentID(),
                studentDetailsProvider.name(),
                studentDetailsProvider.gpa()
        ), ctx.getInstance(MockStudent.class));

    }

}