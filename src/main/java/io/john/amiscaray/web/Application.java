package io.john.amiscaray.web;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static io.john.amiscaray.web.ApplicationProperty.*;

public abstract class Application {

    protected Context context;

    public void start() throws LifecycleException {
        var properties = getApplicationProperties();
        var tomcat = new Tomcat();
        tomcat.setBaseDir(properties.serverDirectory());

        var connector1 = tomcat.getConnector();
        connector1.setPort(properties.serverPort());

        var docBase = new File(properties.serverDocBase()).getAbsolutePath();

        context = tomcat.addContext(properties.serverContextPath(), docBase);

        tomcat.start();
        tomcat.getService().addConnector(connector1);
        tomcat.getServer().await();
    }

    private Context getContext() {
        return context;
    }

    public ApplicationProperties getApplicationProperties() {
        try (var propertiesFileInputStream = Application.class.getResourceAsStream("/application.properties")) {
            var properties = new Properties();
            properties.load(propertiesFileInputStream);
            return parsePropertiesFromFile(properties);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ApplicationProperties parsePropertiesFromFile(Properties properties) {
        var port = Optional.of(properties.getProperty(PORT.getName())).orElse("8080");
        var serverDirectory = Optional.of(properties.getProperty(SERVER_DIRECTORY.getName())).orElse("server");
        var serverContextPath = Optional.of(properties.getProperty(CONTEXT_PATH.getName())).orElse("");
        var serverDocBase = Optional.of(properties.getProperty(DOCUMENT_BASE.getName())).orElse(".");
        return ApplicationProperties.builder()
                .serverPort(Integer.parseInt(port))
                .serverDirectory(serverDirectory)
                .serverContextPath(serverContextPath)
                .serverDocBase(serverDocBase)
                .build();
    }

}
