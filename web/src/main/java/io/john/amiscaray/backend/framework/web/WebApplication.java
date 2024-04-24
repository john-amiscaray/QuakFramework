package io.john.amiscaray.backend.framework.web;

import io.john.amiscaray.backend.framework.core.Application;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class WebApplication extends Application {
    protected Tomcat server;
    protected Context context;

    public WebApplication(Class<?> main, String[] args) {
        super(main, args);
    }

    @Override
    public void start() throws Exception {
        super.start();
        server = new Tomcat();
        server.setBaseDir(properties.serverDirectory());

        var connector1 = server.getConnector();
        connector1.setPort(properties.serverPort());

        var docBase = new File(properties.serverDocBase()).getAbsolutePath();

        context = server.addContext(properties.serverContextPath(), docBase);

        server.start();
        server.getService().addConnector(connector1);
        server.getServer().await();
    }
}
