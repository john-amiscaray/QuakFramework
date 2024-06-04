package io.john.amiscaray.backend.framework.core.di;

import io.john.amiscaray.backend.framework.core.Application;
import io.john.amiscaray.backend.framework.core.di.stub.MockStringProvider;
import io.john.amiscaray.backend.framework.core.di.stub.MockUserAccountDetailsProvider;
import io.john.amiscaray.backend.framework.core.di.stub.MockUserAccountProvider;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockUser;
import io.john.amiscaray.backend.framework.core.di.stub.MockUserProvider;
import io.john.amiscaray.backend.framework.core.di.stub.pojo.MockUserAccount;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ApplicationContextTest {

    private static final Application application = new DummyApplication(ApplicationContext.class, new String[] {});
    private static final MockStringProvider stringProvider = new MockStringProvider();
    private static final MockUserProvider userProvider = new MockUserProvider(stringProvider.username());
    private static final MockUserAccountDetailsProvider accountDetailsProvider = new MockUserAccountDetailsProvider();
    private static final MockUserAccountProvider userAccountProvider = new MockUserAccountProvider(
            userProvider.getUser(),
            accountDetailsProvider.createdOn(),
            accountDetailsProvider.balance()
    );
    private static ApplicationContext ctx;

    @BeforeAll
    public static void setUp() throws Exception {

        application.start();
        ctx = ApplicationContext.getInstance();

    }

    @Test
    public void testProvideSimpleString() {

        assertEquals(stringProvider.username(), ctx.getInstance(String.class));

    }

    @Test
    public void testProvideMockUser() {

        assertEquals(userProvider.getUser(), ctx.getInstance(MockUser.class));

    }

    @Test
    public void testProvideMockUserAccount() {

        assertEquals(userAccountProvider.userAccount(), ctx.getInstance(MockUserAccount.class));

    }

    private static class DummyApplication extends Application {
        public DummyApplication(Class<?> main, String[] args) {
            super(main, args);
        }
    }

}