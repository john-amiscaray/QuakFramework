import io.john.amiscaray.quak.core.di.provider.DependencyProvider;
import io.john.amiscaray.quak.security.di.AuthenticationFilterProvider;
import io.john.amiscaray.quak.security.di.CORSFilterProvider;
import io.john.amiscaray.quak.security.di.JWTUtilProvider;

module quak.framework.security {
    exports io.john.amiscaray.quak.security.auth.filter;
    exports io.john.amiscaray.quak.security.auth;
    exports io.john.amiscaray.quak.security.auth.credentials;
    exports io.john.amiscaray.quak.security.auth.exception;
    exports io.john.amiscaray.quak.security.auth.jwt;
    exports io.john.amiscaray.quak.security.auth.principal;
    exports io.john.amiscaray.quak.security.auth.principal.role;
    exports io.john.amiscaray.quak.security.config;
    exports io.john.amiscaray.quak.security.di;
    exports io.john.amiscaray.quak.security.cors.filter;
    requires static lombok;
    requires quak.framework.core;
    requires org.slf4j;
    requires org.apache.tomcat.embed.core;
    requires com.auth0.jwt;

    provides DependencyProvider with AuthenticationFilterProvider, CORSFilterProvider, JWTUtilProvider;
}