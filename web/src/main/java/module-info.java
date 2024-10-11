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

    exports io.john.amiscaray.quak.web.application;
    exports io.john.amiscaray.quak.web.filter.annotation;
    exports io.john.amiscaray.quak.web.filter.exception;
    exports io.john.amiscaray.quak.web.handler;
    exports io.john.amiscaray.quak.web.controller;
    exports io.john.amiscaray.quak.web.controller.annotation;
    exports io.john.amiscaray.quak.web.handler.annotation;
    exports io.john.amiscaray.quak.web.cfg;
    exports io.john.amiscaray.quak.web.annotation;
    exports io.john.amiscaray.quak.web.controller.exception;
}