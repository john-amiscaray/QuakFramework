module quak.framework.web {
    requires quak.framework.core;
    requires quak.framework.security;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires javatuples;
    requires lombok;
    requires org.apache.tomcat.embed.core;
    requires org.eclipse.jdt.core.compiler.batch;
    requires org.apache.commons.lang3;
    requires org.reflections;
    requires org.slf4j;
    requires quak.framework.web.model;

    exports io.john.amiscaray.backend.framework.web.application;
    exports io.john.amiscaray.backend.framework.web.filter.annotation;
    exports io.john.amiscaray.backend.framework.web.filter.exception;
    exports io.john.amiscaray.backend.framework.web.handler;
    exports io.john.amiscaray.backend.framework.web.controller;
    exports io.john.amiscaray.backend.framework.web.controller.annotation;
    exports io.john.amiscaray.backend.framework.web.handler.annotation;
    exports io.john.amiscaray.backend.framework.web.cfg;
    exports io.john.amiscaray.backend.framework.web.annotation;
    exports io.john.amiscaray.backend.framework.web.controller.exception;
}