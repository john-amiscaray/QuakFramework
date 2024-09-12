module backend.framework.web.test {
    requires static lombok;
    requires backend.framework.web;
    requires com.fasterxml.jackson.databind;
    requires org.junit.jupiter.api;
    requires org.apache.tomcat.embed.core;
    requires java.net.http;
    requires backend.framework.core;

    opens io.john.amiscaray.backend.framework.web.test.application to org.junit.platform.commons;
    opens io.john.amiscaray.backend.framework.web.test.application.stub;
    opens io.john.amiscaray.backend.framework.web.test.application.stub.controller;
    opens io.john.amiscaray.backend.framework.web.test.application.stub.filter;
    opens io.john.amiscaray.backend.framework.web.test.stub;
    opens io.john.amiscaray.backend.framework.web.test.util;
}