import io.john.amiscaray.backend.framework.core.di.provider.DependencyProvider;
import io.john.amiscaray.backend.framework.data.di.DatabaseProxyStartupDependencyProvider;

module backend.framework.data {
    requires java.logging;
    requires jakarta.persistence;
    requires java.naming;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires org.reflections;
    requires backend.framework.core;
    requires lombok;

    exports io.john.amiscaray.backend.framework.data;
    exports io.john.amiscaray.backend.framework.data.query.numeric;
    exports io.john.amiscaray.backend.framework.data.query.string;
    exports io.john.amiscaray.backend.framework.data.query;
    exports io.john.amiscaray.backend.framework.data.update;

    provides DependencyProvider with DatabaseProxyStartupDependencyProvider;
}