module backend.framework.data.test {
    requires quak.framework.core;
    requires quak.framework.data;
    requires com.h2database;
    requires jakarta.persistence;
    requires java.logging;
    requires java.sql;
    requires static lombok;
    requires org.junit.jupiter.api;
    requires org.hibernate.orm.core;

    opens io.john.amiscaray.quak.data.test.stub to org.hibernate.orm.core;
    opens io.john.amiscaray.quak.data.test to org.junit.platform.commons;
}