package io.john.amiscaray.web.application;

import io.john.amiscaray.web.application.properties.ApplicationProperties;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static io.john.amiscaray.web.application.properties.ApplicationProperty.*;

public class Application {
    protected Context context;
    protected ApplicationProperties properties;
    protected SessionFactory dbSessionFactory;

    public void start() throws LifecycleException {
        var properties = getApplicationProperties();
        var tomcat = new Tomcat();
        tomcat.setBaseDir(properties.serverDirectory());

        var connector1 = tomcat.getConnector();
        connector1.setPort(properties.serverPort());

        var docBase = new File(properties.serverDocBase()).getAbsolutePath();

        context = tomcat.addContext(properties.serverContextPath(), docBase);

        initDatabases();

        tomcat.start();
        tomcat.getService().addConnector(connector1);
        tomcat.getServer().await();
    }

    public void initDatabases() {

        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(Map.of(
                        SQL_DIALECT.getName(), properties.sqlDialect(),
                        DB_DRIVER_CLASS.getName(), properties.dbConnectionDriver(),
                        DB_CONNECTION_URL.getName(), properties.dbConnectionURL(),
                        DB_CONNECTION_USERNAME.getName(), properties.dbUsername(),
                        DB_CONNECTION_PASSWORD.getName(), properties.dbPassword(),
                        HBM2DDL.getName(), properties.hbm2ddl()
                ))
                .build();

        Metadata metadata = new MetadataSources(serviceRegistry)
                //.addAnnotatedClass(Employee.class)
                // other domain classes
                .buildMetadata();

        dbSessionFactory = metadata.buildSessionFactory();

    }

    private Context getContext() {
        return context;
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
