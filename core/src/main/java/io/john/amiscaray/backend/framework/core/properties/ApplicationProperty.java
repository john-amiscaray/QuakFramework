package io.john.amiscaray.backend.framework.core.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.Properties;

@Getter
public enum ApplicationProperty {
    CONTEXT_PACKAGE("application.context.package", ""),
    PORT("server.port", "8080"),
    CONTEXT_PATH("server.context.path", ""),
    DOCUMENT_BASE("server.document.base", "."),
    SERVER_DIRECTORY("server.directory", "server"),
    SQL_DIALECT("hibernate.dialect", "org.hibernate.dialect.MySQLDialect"),
    DB_DRIVER_CLASS("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver"),
    DB_CONNECTION_URL("hibernate.connection.url", "jdbc:mysql://localhost:3306/test"),
    DB_CONNECTION_USERNAME("hibernate.connection.username", "root"),
    DB_CONNECTION_PASSWORD("hibernate.connection.password", ""),
    HBM2DDL("hibernate.hbm2ddl.auto", "none"),
    JWT_SECRET_KEY("jwt.secret.key", ""),
    JWT_EXPIRY_TIME("jwt.expiry.time", "36000000");

    private String name;
    private String defaultValue;
    @Setter
    private String value;

    ApplicationProperty(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    String getOrElse(Properties properties, String defaultValue) {
        return Optional.ofNullable(properties.getProperty(name)).orElse(defaultValue);
    }

    String getOrElseDefault(Properties properties) {
        return Optional.of(properties.getProperty(name)).orElse(defaultValue);
    }
}
