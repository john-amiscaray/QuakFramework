package io.john.amiscaray.jpms;

import io.john.amiscaray.backend.framework.generator.api.RestModel;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import org.apache.maven.plugin.logging.Log;

import java.util.List;

@AllArgsConstructor
public class ModuleInfoWriter {

    private List<? extends Class<?>> projectClasses;
    private String rootPackage;
    private String moduleInfoTemplate;

    public ModuleInfoWriter(List<? extends Class<?>> projectClasses, String rootPackage) {
        this.projectClasses = projectClasses;
        this.rootPackage = rootPackage;
    }

    public String writeModuleInfo(Log logger) {
        if (projectClasses.isEmpty()) {
            return null;
        }

        var modelPackages = projectClasses.stream()
                .filter(clazz -> clazz.isAnnotationPresent(RestModel.class))
                .map(clazz -> clazz.getPackage().getName())
                .distinct()
                .toList();
        var ormPackages = projectClasses.stream()
                .filter(clazz -> clazz.isAnnotationPresent(Entity.class))
                .map(clazz -> clazz.getPackage().getName())
                .distinct()
                .toList();

        if (moduleInfoTemplate == null) {
            return String.format("""
            module %1$s {
            %2$s
            }
            """, rootPackage, generateModuleInfoContent(modelPackages, ormPackages));
        } else {
            // remove the closing curly bracket and add some space to add the generated code
            moduleInfoTemplate = moduleInfoTemplate.stripTrailing();
            moduleInfoTemplate = moduleInfoTemplate.substring(0, moduleInfoTemplate.length() - 1) + "\n    // GENERATED SOURCES:\n";

            return moduleInfoTemplate + generateModuleInfoContent(modelPackages, ormPackages) + "}";
        }
    }

    private String generateModuleInfoContent(List<String> modelPackages, List<String> ormPackages) {
        return String.format("""
                exports %1$s.controllers to backend.framework.core, backend.framework.web;
                
                %2$s
                %3$s
                
                requires backend.framework.core;
                requires backend.framework.data;
                requires backend.framework.generator;
                requires backend.framework.web;
                requires jakarta.persistence;
                requires static lombok;
                requires org.reflections;
                """, rootPackage, generateRulesForModelPackages(modelPackages), generateRulesForORMPackages(ormPackages))
                .indent(4);
    }

    public String generateRulesForModelPackages(List<String> modelPackages) {
        var result = new StringBuilder();
        for (var modelPackage : modelPackages) {
            result.append("opens ").append(modelPackage).append(" to ").append("com.fasterxml.jackson.databind;\n");
        }
        return result.toString().trim();
    }

    public String generateRulesForORMPackages(List<String> ormPackages) {
        var result = new StringBuilder();
        for (var ormPackage : ormPackages) {
            result.append("opens ").append(ormPackage).append(" to ").append("org.hibernate.orm.core;\n");
        }
        return result.toString().trim();
    }

}
