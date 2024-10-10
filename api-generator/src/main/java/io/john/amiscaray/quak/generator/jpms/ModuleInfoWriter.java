package io.john.amiscaray.quak.generator.jpms;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import io.john.amiscaray.quak.generator.model.VisitedSourcesState;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ModuleInfoWriter {

    private VisitedSourcesState finalVisitedSourcesState;
    private String rootPackage;
    private String moduleInfoTemplate;

    public ModuleInfoWriter(VisitedSourcesState finalVisitedSourcesState, String rootPackage) {
        this.finalVisitedSourcesState = finalVisitedSourcesState;
        this.rootPackage = rootPackage;
    }

    public String writeModuleInfo() {
        if (finalVisitedSourcesState.visitedRestModelClasses().isEmpty() && finalVisitedSourcesState.visitedEntityClasses().isEmpty()) {
            return null;
        }

        var modelPackages = extractPackagesFromClasses(finalVisitedSourcesState.visitedRestModelClasses());
        var ormPackages = extractPackagesFromClasses(finalVisitedSourcesState.visitedEntityClasses());
        var diComponentPackages = extractPackagesFromClasses(finalVisitedSourcesState.visitedDIComponents());

        if (moduleInfoTemplate == null) {
            return String.format("""
            module %1$s {
            %2$s
            }
            """, rootPackage, generateModuleInfoContent(modelPackages, ormPackages, diComponentPackages));
        } else {
            // remove the closing curly bracket and add some space to add the generated code
            moduleInfoTemplate = moduleInfoTemplate.stripTrailing();
            moduleInfoTemplate = moduleInfoTemplate.substring(0, moduleInfoTemplate.length() - 1) + "\n    // GENERATED SOURCES:\n";

            return moduleInfoTemplate + generateModuleInfoContent(modelPackages, ormPackages, diComponentPackages) + "}";
        }
    }

    private List<String> extractPackagesFromClasses(List<ClassOrInterfaceDeclaration> classes) {
        return classes.stream()
                .filter(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.getFullyQualifiedName().isPresent())
                .map(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.getFullyQualifiedName().get())
                .map(fullyQualifiedClassName -> fullyQualifiedClassName.substring(0, fullyQualifiedClassName.lastIndexOf(".")))
                .distinct()
                .sorted()
                .toList();
    }

    private String generateModuleInfoContent(List<String> modelPackages, List<String> ormPackages, List<String> diComponentPackages) {
        return String.format("""
                exports %1$s.controllers to backend.framework.core, backend.framework.web;
                
                // Rules for RestModels
                %2$s
                // Rules for Entities
                %3$s
                // Rules for DI Components
                %4$s
                
                requires backend.framework.core;
                requires backend.framework.data;
                requires backend.framework.generator;
                requires backend.framework.web;
                requires backend.framework.web.model;
                requires jakarta.persistence;
                requires static lombok;
                requires org.reflections;
                """, rootPackage, generateRulesForModelPackages(modelPackages),
                        generateRulesForORMPackages(ormPackages),
                        generateRulesForPackagesWithDIComponents(diComponentPackages))
                .indent(4);
    }

    private String generateRulesForModelPackages(List<String> modelPackages) {
        var result = new StringBuilder();
        for (var modelPackage : modelPackages) {
            result.append("opens ").append(modelPackage).append(" to ").append("com.fasterxml.jackson.databind;\n");
        }
        return result.toString().trim();
    }

    private String generateRulesForORMPackages(List<String> ormPackages) {
        var result = new StringBuilder();
        for (var ormPackage : ormPackages) {
            result.append("opens ").append(ormPackage).append(" to ").append("org.hibernate.orm.core;\n");
        }
        return result.toString().trim();
    }

    private String generateRulesForPackagesWithDIComponents(List<String> packagesWithDIComponents) {
        var result = new StringBuilder();
        for (var diPackage : packagesWithDIComponents) {
            result.append("opens ").append(diPackage).append(" to ").append("backend.framework.core;\n");
        }
        return result.toString().trim();
    }

}
