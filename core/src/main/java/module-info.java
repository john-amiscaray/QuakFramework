import io.john.amiscaray.backend.framework.core.di.provider.DependencyProvider;

module quak.framework.core {
    exports io.john.amiscaray.backend.framework.core.properties;
    exports io.john.amiscaray.backend.framework.core;
    exports io.john.amiscaray.backend.framework.core.di.dependency;
    exports io.john.amiscaray.backend.framework.core.di.exception;
    exports io.john.amiscaray.backend.framework.core.di.provider;
    exports io.john.amiscaray.backend.framework.core.di;
    exports io.john.amiscaray.backend.framework.core.di.provider.annotation;
    requires lombok;
    requires org.reflections;
    requires org.apache.commons.lang3;
    requires org.slf4j;

    uses DependencyProvider;
}