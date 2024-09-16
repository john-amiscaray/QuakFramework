import io.john.amiscaray.backend.framework.core.di.provider.DependencyProvider;
import io.john.amiscaray.backend.framework.security.di.AuthenticationFilterProvider;

module backend.framework.security {
    exports io.john.amiscaray.backend.framework.security.auth.filter;
    exports io.john.amiscaray.backend.framework.security.auth;
    exports io.john.amiscaray.backend.framework.security.auth.credentials;
    exports io.john.amiscaray.backend.framework.security.auth.exception;
    exports io.john.amiscaray.backend.framework.security.auth.principal;
    requires static lombok;
    requires jakarta.servlet;
    requires backend.framework.core;

    provides DependencyProvider with AuthenticationFilterProvider;
}