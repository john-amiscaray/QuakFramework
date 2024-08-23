package io.john.amiscaray.jpms;

import io.john.amiscaray.backend.framework.generator.api.RestModel;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ModuleInfoWriter {

    private List<? extends Class<?>> projectClasses;
    private String rootPackage;

    public String writeModuleInfo() {
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

        return String.format("""
                module %1$s {
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
                }
                """, rootPackage, generateRulesForModelPackages(modelPackages), generateRulesForORMPackages(ormPackages));
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
