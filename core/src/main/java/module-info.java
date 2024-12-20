import io.john.amiscaray.quak.core.di.provider.DependencyProvider;

module quak.framework.core {
    exports io.john.amiscaray.quak.core.properties;
    exports io.john.amiscaray.quak.core;
    exports io.john.amiscaray.quak.core.di.dependency;
    exports io.john.amiscaray.quak.core.di.exception;
    exports io.john.amiscaray.quak.core.di.provider;
    exports io.john.amiscaray.quak.core.di;
    exports io.john.amiscaray.quak.core.di.provider.annotation;
    exports io.john.amiscaray.quak.core.exception;
    requires lombok;
    requires org.reflections;
    requires org.apache.commons.lang3;
    requires org.slf4j;

    uses DependencyProvider;
}