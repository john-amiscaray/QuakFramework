package io.john.amiscaray.web.application.properties;

import java.util.Optional;
import java.util.Properties;

public enum ApplicationProperty {
    PORT("server.port", "8080"),
    CONTEXT_PATH("server.context.path", ""),
    DOCUMENT_BASE("server.document.base", "."),
    SERVER_DIRECTORY("server.directory", "server"),
    SQL_DIALECT("hibernate.dialect", "org.hibernate.dialect.MySQLDialect"),
    DB_DRIVER_CLASS("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver"),
    DB_CONNECTION_URL("hibernate.connection.url", null),
    DB_CONNECTION_USERNAME("hibernate.connection.username", "root"),
    DB_CONNECTION_PASSWORD("hibernate.connection.password", ""),
    HBM2DDL("hibernate.hbm2ddl.auto", "none");

    private final String name;
    private final String defaultValue;

    ApplicationProperty(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public String getOrElseDefault(Properties properties) {
        return Optional.of(properties.getProperty(name)).orElse(defaultValue);
    }
}
