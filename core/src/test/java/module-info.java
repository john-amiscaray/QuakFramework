import io.john.amiscaray.quak.core.di.provider.DependencyProvider;
import io.john.amiscaray.quak.core.test.di.stub.MockProgramProvider;
import io.john.amiscaray.quak.core.test.di.stub.EmployeeStartupDependencyProvider;

module backend.framework.core.test {

    requires org.junit.jupiter.api;
    requires org.hamcrest;
    requires quak.framework.core;
    requires static lombok;

    opens io.john.amiscaray.quak.core.test to org.junit.platform.commons;
    opens io.john.amiscaray.quak.core.test.di to org.junit.platform.commons;
    exports io.john.amiscaray.quak.core.test.di.stub;
    exports io.john.amiscaray.quak.core.test.di.stub.pojo;
    opens io.john.amiscaray.quak.core.test.di.stub to org.junit.platform.commons;

    provides DependencyProvider with EmployeeStartupDependencyProvider, MockProgramProvider;

}