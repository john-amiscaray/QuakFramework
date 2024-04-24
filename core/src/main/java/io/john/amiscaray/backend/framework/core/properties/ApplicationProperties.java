package io.john.amiscaray.backend.framework.core.properties;

public record ApplicationProperties(int serverPort, String serverDirectory, String serverDocBase,
                                    String serverContextPath, String sqlDialect, String dbConnectionDriver,
                                    String dbUsername, String dbPassword,
                                    String hbm2ddl, String dbConnectionURL) {
    public static ApplicationPropertiesBuilder builder() {
        return new ApplicationPropertiesBuilder();
    }

    public static class ApplicationPropertiesBuilder {
        private int serverPort;
        private String serverDirectory;
        private String serverDocBase;
        private String serverContextPath;
        private String sqlDialect;
        private String dbConnectionDriver;
        private String dbUsername;
        private String dbPassword;
        private String hbm2ddl;
        private String dbConnectionURL;

        ApplicationPropertiesBuilder() {
        }

        public ApplicationPropertiesBuilder serverPort(int serverPort) {
            this.serverPort = serverPort;
            return this;
        }

        public ApplicationPropertiesBuilder serverDirectory(String serverDirectory) {
            this.serverDirectory = serverDirectory;
            return this;
        }

        public ApplicationPropertiesBuilder serverDocBase(String serverDocBase) {
            this.serverDocBase = serverDocBase;
            return this;
        }

        public ApplicationPropertiesBuilder serverContextPath(String serverContextPath) {
            this.serverContextPath = serverContextPath;
            return this;
        }

        public ApplicationPropertiesBuilder sqlDialect(String sqlDialect) {
            this.sqlDialect = sqlDialect;
            return this;
        }

        public ApplicationPropertiesBuilder dbConnectionDriver(String dbConnectionDriver) {
            this.dbConnectionDriver = dbConnectionDriver;
            return this;
        }

        public ApplicationPropertiesBuilder dbUsername(String dbUsername) {
            this.dbUsername = dbUsername;
            return this;
        }

        public ApplicationPropertiesBuilder dbPassword(String dbPassword) {
            this.dbPassword = dbPassword;
            return this;
        }

        public ApplicationPropertiesBuilder hbm2ddl(String hbm2ddl) {
            this.hbm2ddl = hbm2ddl;
            return this;
        }

        public ApplicationPropertiesBuilder dbConnectionURL(String dbConnectionURL) {
            this.dbConnectionURL = dbConnectionURL;
            return this;
        }

        public ApplicationProperties build() {
            return new ApplicationProperties(this.serverPort, this.serverDirectory, this.serverDocBase, this.serverContextPath, this.sqlDialect, this.dbConnectionDriver, this.dbUsername, this.dbPassword, this.hbm2ddl, this.dbConnectionURL);
        }

        public String toString() {
            return "ApplicationProperties.ApplicationPropertiesBuilder(serverPort=" + this.serverPort + ", serverDirectory=" + this.serverDirectory + ", serverDocBase=" + this.serverDocBase + ", serverContextPath=" + this.serverContextPath + ", sqlDialect=" + this.sqlDialect + ", dbConnectionDriver=" + this.dbConnectionDriver + ", dbUsername=" + this.dbUsername + ", dbPassword=" + this.dbPassword + ", hbm2ddl=" + this.hbm2ddl + ", dbConnectionURL=" + this.dbConnectionURL + ")";
        }
    }
}
