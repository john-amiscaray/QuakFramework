import io.john.amiscaray.backend.framework.core.di.provider.DependencyProvider;
import io.john.amiscaray.backend.framework.web.test.application.stub.di.SimpleAuthenticatorProvider;
import io.john.amiscaray.backend.framework.web.test.stub.WebConfigProvider;

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
    requires org.mockito;
    requires web.model;

    provides DependencyProvider with SimpleAuthenticatorProvider, WebConfigProvider;

    opens io.john.amiscaray.backend.framework.web.test.application to org.junit.platform.commons;
    opens io.john.amiscaray.backend.framework.web.test.application.stub;
    opens io.john.amiscaray.backend.framework.web.test.application.stub.controller;
    opens io.john.amiscaray.backend.framework.web.test.application.stub.filter;
    opens io.john.amiscaray.backend.framework.web.test.application.stub.di;
    opens io.john.amiscaray.backend.framework.web.test.controller.exception;
    opens io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.badreturn;
    opens io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.nohandlers;
    opens io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.noparams;
    opens io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.stringparam;
    opens io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.voidreturn;
    opens io.john.amiscaray.backend.framework.web.test.stub;
    opens io.john.amiscaray.backend.framework.web.test.util;
}