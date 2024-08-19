import io.john.amiscaray.backend.framework.core.di.provider.StartupDependencyProvider;
import io.john.amiscaray.backend.framework.test.core.di.stub.EmployeeStartupDependencyProvider;

module backend.framework.core.test {

    requires org.junit.jupiter.api;
    requires backend.framework.core;

    opens io.john.amiscaray.backend.framework.test.core to org.junit.platform.commons;
    opens io.john.amiscaray.backend.framework.test.core.di to org.junit.platform.commons;
    exports io.john.amiscaray.backend.framework.test.core.di.stub;
    exports io.john.amiscaray.backend.framework.test.core.di.stub.pojo;

    provides StartupDependencyProvider with EmployeeStartupDependencyProvider;

}