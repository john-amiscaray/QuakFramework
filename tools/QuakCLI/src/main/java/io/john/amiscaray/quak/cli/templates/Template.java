package io.john.amiscaray.quak.cli.templates;

public enum Template {
    BASIC("Basic template"),
    AUTH("Pre-configured JWT-based auth");

    public final String textName;

    Template(String textName) {
        this.textName = textName;
    }

    @Override
    public String toString() {
        return textName;
    }
}
