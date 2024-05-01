package io.john.amiscaray.backend.framework.core.properties;

import lombok.Builder;

@Builder
public record ApplicationProperties(int serverPort, String serverDirectory, String serverDocBase,
                                    String serverContextPath, String sqlDialect, String dbConnectionDriver,
                                    String dbUsername, String dbPassword,
                                    String hbm2ddl, String dbConnectionURL) {

}
