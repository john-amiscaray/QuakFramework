import io.john.amiscaray.backend.framework.core.di.provider.DependencyProvider;
import io.john.amiscaray.backend.framework.web.test.application.stub.di.SimpleAuthenticatorProvider;

module backend.framework.web.test {
    requires static lombok;
    requires backend.framework.web;
    requires com.fasterxml.jackson.databind;
    requires org.junit.jupiter.api;
    requires org.apache.tomcat.embed.core;
    requires org.hamcrest;
    requires java.net.http;
    requires backend.framework.core;
    requires backend.framework.security;
    requires com.auth0.jwt;

    provides DependencyProvider with SimpleAuthenticatorProvider;

    opens io.john.amiscaray.backend.framework.web.test.application to org.junit.platform.commons;
    opens io.john.amiscaray.backend.framework.web.test.application.stub;
    opens io.john.amiscaray.backend.framework.web.test.application.stub.controller;
    opens io.john.amiscaray.backend.framework.web.test.application.stub.filter;
    opens io.john.amiscaray.backend.framework.web.test.application.stub.di;
    opens io.john.amiscaray.backend.framework.web.test.stub;
    opens io.john.amiscaray.backend.framework.web.test.util;
}