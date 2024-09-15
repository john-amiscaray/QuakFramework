module backend.framework.security.test {

    requires org.junit.jupiter.api;
    requires org.mockito;
    requires backend.framework.security;
    requires jakarta.servlet;

    exports io.john.amiscaray.backend.framework.security.test.auth.filter to org.junit.platform.commons;
}