package io.john.amiscaray.quak.web.cfg;

import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import lombok.Builder;
import lombok.Singular;

import java.util.Map;

public record WebConfig(
        @Singular("mapExceptionToStatusCode")
        Map<Class<? extends Exception>, Integer> exceptionHttpStatusMapping){

    public static final DependencyID<WebConfig> APPLICATION_WEB_CFG_DEPENDENCY_ID = new DependencyID<>(
            "WebConfig",
            WebConfig.class
    );

    @Builder
    public WebConfig {

    }

}
