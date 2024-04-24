package io.john.amiscaray.backend.framework.core;

import io.john.amiscaray.backend.framework.core.properties.ApplicationProperties;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static io.john.amiscaray.backend.framework.core.properties.ApplicationProperty.*;

public class Application {
    protected Context context;
    protected ApplicationProperties properties;
    protected Tomcat server;
    protected String classScanPackage;
    protected String[] args;

    public Application(Class<?> main, String[] args) {
        this.args = args;
        classScanPackage = main.getPackageName();
    }

    public void start() throws LifecycleException {
        properties = getApplicationProperties();
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

    public ApplicationProperties getApplicationProperties() {
        if (properties == null) {
            try (var propertiesFileInputStream = Application.class.getResourceAsStream("/application.properties")) {
                var propertiesFromFile = new Properties();
                propertiesFromFile.load(propertiesFileInputStream);
                properties = parsePropertiesFromFile(propertiesFromFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return properties;
    }

    private ApplicationProperties parsePropertiesFromFile(Properties properties) {
        return ApplicationProperties.builder()
                .serverPort(Integer.parseInt(PORT.getOrElseDefault(properties)))
                .serverDirectory(SERVER_DIRECTORY.getOrElseDefault(properties))
                .serverContextPath(CONTEXT_PATH.getOrElseDefault(properties))
                .serverDocBase(DOCUMENT_BASE.getOrElseDefault(properties))
                .dbUsername(DB_CONNECTION_USERNAME.getOrElseDefault(properties))
                .dbPassword(DB_CONNECTION_PASSWORD.getOrElseDefault(properties))
                .dbConnectionURL(DB_CONNECTION_URL.getOrElseDefault(properties))
                .sqlDialect(SQL_DIALECT.getOrElseDefault(properties))
                .dbConnectionDriver(DB_DRIVER_CLASS.getOrElseDefault(properties))
                .hbm2ddl(HBM2DDL.getOrElseDefault(properties))
                .build();
    }

}
