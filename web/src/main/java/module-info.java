module backend.framework.web {
    requires backend.framework.core;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires javatuples;
    requires lombok;
    requires org.apache.tomcat.embed.core;

    exports io.john.amiscaray.backend.framework.web.application;
    exports io.john.amiscaray.backend.framework.web.handler;
    exports io.john.amiscaray.backend.framework.web.handler.request;
    exports io.john.amiscaray.backend.framework.web.handler.response;
}