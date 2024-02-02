package io.john.amiscaray.web;

public enum ApplicationProperty {
    PORT("server.port"),
    CONTEXT_PATH("server.context.path"),
    DOCUMENT_BASE("server.document.base"),
    SERVER_DIRECTORY("server.directory");

    private final String name;

    ApplicationProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
