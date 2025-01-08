package io.john.amiscaray.quak.cli.cfg;

import io.john.amiscaray.quak.cli.templates.Template;

public record ProjectConfig(String artifactID, String groupID, Template template) {
}
