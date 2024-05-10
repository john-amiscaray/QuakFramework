module backend.framework.web {
    requires backend.framework.core;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires javatuples;
    requires lombok;
    requires org.apache.tomcat.embed.core;
    requires org.eclipse.jdt.core.compiler.batch;
    requires org.apache.commons.lang3;

    exports io.john.amiscaray.backend.framework.web.application;
    exports io.john.amiscaray.backend.framework.web.handler;
    exports io.john.amiscaray.backend.framework.web.handler.request;
    exports io.john.amiscaray.backend.framework.web.handler.response;
    exports io.john.amiscaray.backend.framework.web.controller;
}