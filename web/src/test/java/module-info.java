module backend.framework.web.test {
    requires static lombok;
    requires backend.framework.web;
    requires com.fasterxml.jackson.databind;
    requires org.junit.jupiter.api;
    requires org.apache.tomcat.embed.core;
    requires java.net.http;

    opens io.john.amiscaray.backend.framework.web.test.application to org.junit.platform.commons;
    opens io.john.amiscaray.backend.framework.web.test.stub;
}