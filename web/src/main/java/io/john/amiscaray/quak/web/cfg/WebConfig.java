package io.john.amiscaray.quak.web.cfg;

import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import lombok.Builder;
import lombok.Singular;

import java.util.Map;

/**
 * Web related configuration.
 * @param exceptionHttpStatusMapping Configures which HTTP status codes to use when handling different exceptions.
 */
public record WebConfig(
        @Singular("mapExceptionToStatusCode")
        Map<Class<? extends Exception>, Integer> exceptionHttpStatusMapping){

    /**
     * The dependency name the framework expects for the web config.
     */
    public static final String APPLICATION_WEB_CFG_DEPENDENCY_NAME = "WebConfig";
    /**
     * The dependency ID for the framework to retrieve the web config.
     */
    public static final DependencyID<WebConfig> APPLICATION_WEB_CFG_DEPENDENCY_ID = new DependencyID<>(
            APPLICATION_WEB_CFG_DEPENDENCY_NAME,
            WebConfig.class
    );

    @Builder
    public WebConfig {

    }

}
