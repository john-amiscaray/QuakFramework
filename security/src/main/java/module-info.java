import io.john.amiscaray.backend.framework.core.di.provider.DependencyProvider;
import io.john.amiscaray.backend.framework.security.di.AuthenticationFilterProvider;

module backend.framework.security {
    exports io.john.amiscaray.backend.framework.security.auth.filter;
    exports io.john.amiscaray.backend.framework.security.auth;
    exports io.john.amiscaray.backend.framework.security.auth.credentials;
    exports io.john.amiscaray.backend.framework.security.auth.exception;
    exports io.john.amiscaray.backend.framework.security.auth.principal;
    exports io.john.amiscaray.backend.framework.security.auth.principal.role;
    exports io.john.amiscaray.backend.framework.security.config;
    exports io.john.amiscaray.backend.framework.security.di;
    requires static lombok;
    requires backend.framework.core;
    requires org.slf4j;
    requires org.apache.tomcat.embed.core;

    provides DependencyProvider with AuthenticationFilterProvider;
}