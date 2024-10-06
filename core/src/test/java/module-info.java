import io.john.amiscaray.backend.framework.core.di.provider.DependencyProvider;
import io.john.amiscaray.backend.framework.test.core.di.stub.MockProgramProvider;
import io.john.amiscaray.backend.framework.test.core.di.stub.EmployeeStartupDependencyProvider;

module backend.framework.core.test {

    requires org.junit.jupiter.api;
    requires org.hamcrest;
    requires backend.framework.core;
    requires static lombok;

    opens io.john.amiscaray.backend.framework.test.core to org.junit.platform.commons;
    opens io.john.amiscaray.backend.framework.test.core.di to org.junit.platform.commons;
    exports io.john.amiscaray.backend.framework.test.core.di.stub;
    exports io.john.amiscaray.backend.framework.test.core.di.stub.pojo;
    opens io.john.amiscaray.backend.framework.test.core.di.stub to org.junit.platform.commons;

    provides DependencyProvider with EmployeeStartupDependencyProvider, MockProgramProvider;

}