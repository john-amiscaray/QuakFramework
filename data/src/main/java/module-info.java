import io.john.amiscaray.quak.core.di.provider.DependencyProvider;
import io.john.amiscaray.quak.data.di.DatabaseProxyStartupDependencyProvider;

module quak.framework.data {
    requires java.logging;
    requires jakarta.persistence;
    requires java.naming;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires org.reflections;
    requires quak.framework.core;
    requires lombok;

    exports io.john.amiscaray.quak.data;
    exports io.john.amiscaray.quak.data.query.numeric;
    exports io.john.amiscaray.quak.data.query.string;
    exports io.john.amiscaray.quak.data.query;
    exports io.john.amiscaray.quak.data.update;

    provides DependencyProvider with DatabaseProxyStartupDependencyProvider;
}