module backend.framework.data.test {
    requires backend.framework.core;
    requires backend.framework.data;
    requires com.h2database;
    requires jakarta.persistence;
    requires java.logging;
    requires java.sql;
    requires static lombok;
    requires org.junit.jupiter.api;

    opens io.john.amiscaray.backend.framework.data.test.stub to org.hibernate.orm.core;
    opens io.john.amiscaray.backend.framework.data.test to org.junit.platform.commons;
}