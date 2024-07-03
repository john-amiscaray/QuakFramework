module backend.framework.core {
    exports io.john.amiscaray.backend.framework.core.properties;
    exports io.john.amiscaray.backend.framework.core;
    exports io.john.amiscaray.backend.framework.core.di.exception;
    exports io.john.amiscaray.backend.framework.core.di.provider;
    exports io.john.amiscaray.backend.framework.core.di;
    requires lombok;
    requires org.reflections;
    requires org.apache.commons.lang3;
}