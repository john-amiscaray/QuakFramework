module backend.framework.security.test {

    requires org.apache.tomcat.embed.core;
    requires org.junit.jupiter.api;
    requires org.mockito;
    requires backend.framework.security;
    requires static lombok;
    requires backend.framework.core;

    exports io.john.amiscaray.backend.framework.security.test.auth.filter to org.junit.platform.commons;
    exports io.john.amiscaray.backend.framework.security.test.cors.filter to org.junit.platform.commons;
}