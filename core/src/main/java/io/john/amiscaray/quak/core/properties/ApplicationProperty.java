package io.john.amiscaray.quak.core.properties;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;
import java.util.Properties;

/**
 * Represents application properties specified in the application.properties file. This file goes in the application's resource folder.
 */
@Getter
public enum ApplicationProperty {
    /**
     * The upmost package Quak uses to scan for classes.
     */
    CONTEXT_PACKAGE("application.context.package", ""),
    /**
     * The TCP port to run the application on. Defaults to 8080.
     */
    PORT("server.port", "8080"),
    /**
     * The servlet context path. If set, all paths will be prefixed with this.
     */
    CONTEXT_PATH("server.context.path", ""),
    /**
     * The Tomcat server document base.
     */
    DOCUMENT_BASE("server.document.base", "."),
    /**
     * The Tomcat server directory.
     */
    SERVER_DIRECTORY("server.directory", "server"),
    /**
     * The SQL dialect to use. Defaults to org.hibernate.dialect.MySQLDialect.
     */
    SQL_DIALECT("hibernate.dialect", "org.hibernate.dialect.MySQLDialect"),
    /**
     * The SQL driver class to use. Defaults to com.mysql.cj.jdbc.Driver.
     */
    DB_DRIVER_CLASS("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver"),
    /**
     * The database connection URL. Defaults to jdbc:mysql://localhost:3306/test.
     */
    DB_CONNECTION_URL("hibernate.connection.url", "jdbc:mysql://localhost:3306/test"),
    /**
     * The hibernate database username. Defaults to <i>"root"</i>.
     */
    DB_CONNECTION_USERNAME("hibernate.connection.username", "root"),
    /**
     * The hibernate database password. Defaults to an empty string.
     */
    DB_CONNECTION_PASSWORD("hibernate.connection.password", ""),
    /**
     * The hibernate <i>"hibernate.hbm2ddl.auto"</i> property. Defaults to "none".
     */
    HBM2DDL("hibernate.hbm2ddl.auto", "none"),
    /**
     * A JWT secret key to use. Defaults to an empty string.
     */
    JWT_SECRET_KEY("jwt.secret.key", ""),
    /**
     * The JWT expiry time in milliseconds. Defaults to 10 hours (36000000 milliseconds).
     */
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
