package io.john.amiscaray.web;

public record ApplicationProperties(int serverPort, String serverDirectory, String serverDocBase,
                                    String serverContextPath) {

    public static ApplicationPropertiesBuilder builder() {
        return new ApplicationPropertiesBuilder();
    }

    public static class ApplicationPropertiesBuilder {
        private int serverPort;
        private String serverDirectory;
        private String serverDocBase;
        private String serverContextPath;

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

        public ApplicationProperties build() {
            return new ApplicationProperties(this.serverPort, this.serverDirectory, this.serverDocBase, this.serverContextPath);
        }

        public String toString() {
            return "ApplicationProperties.ApplicationPropertiesBuilder(serverPort=" + this.serverPort + ", serverDirectory=" + this.serverDirectory + ", serverDocBase=" + this.serverDocBase + ", serverContextPath=" + this.serverContextPath + ")";
        }
    }
}
