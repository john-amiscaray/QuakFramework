package io.john.amiscaray.quak.generator.jpms;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import io.john.amiscaray.quak.generator.model.VisitedSourcesState;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ModuleInfoWriter {

    private VisitedSourcesState finalVisitedSourcesState;
    private String rootPackage;
    private String targetControllerPackage;
    private String moduleInfoTemplate;

    public String writeModuleInfo() {
        if (finalVisitedSourcesState.visitedRestModelClasses().isEmpty() && finalVisitedSourcesState.visitedEntityClasses().isEmpty()) {
            return null;
        }

        var packageNameToPackageInfo = new HashMap<String, ParsedPackageInfo>();

        var restModelPackages = extractPackagesFromClasses(finalVisitedSourcesState.visitedRestModelClasses());
        for (var restModelPackage : restModelPackages) {
            if (packageNameToPackageInfo.containsKey(restModelPackage)) {
                packageNameToPackageInfo.get(restModelPackage)
                        .setContainsRestModelClasses(true);
            } else {
                packageNameToPackageInfo.put(
                        restModelPackage,
                        new ParsedPackageInfo(restModelPackage, false, true, false));
            }
        }
        var ormPackages = extractPackagesFromClasses(finalVisitedSourcesState.visitedEntityClasses());
        for (var ormPackage : ormPackages) {
            if (packageNameToPackageInfo.containsKey(ormPackage)) {
                packageNameToPackageInfo.get(ormPackage)
                        .setContainsORMClasses(true);
            } else {
                packageNameToPackageInfo.put(
                        ormPackage,
                        new ParsedPackageInfo(ormPackage, true, false, false)
                );
            }
        }
        var diComponentPackages = extractPackagesFromClasses(finalVisitedSourcesState.visitedDIComponents());
        for (var diComponentPackage : diComponentPackages) {
            if (packageNameToPackageInfo.containsKey(diComponentPackage)) {
                packageNameToPackageInfo.get(diComponentPackage)
                        .setContainsDIClasses(true);
            } else {
                packageNameToPackageInfo.put(
                        diComponentPackage,
                        new ParsedPackageInfo(diComponentPackage, false, false, true)
                );
            }
        }

        if (moduleInfoTemplate == null) {
            return String.format("""
            module %1$s {
            %2$s
            }
            """, rootPackage, generateModuleInfoContent(packageNameToPackageInfo));
        } else {
            // remove the closing curly bracket and add some space to add the generated code
            moduleInfoTemplate = moduleInfoTemplate.stripTrailing();
            moduleInfoTemplate = moduleInfoTemplate.substring(0, moduleInfoTemplate.length() - 1) + "\n    // GENERATED SOURCES:\n";

            return moduleInfoTemplate + generateModuleInfoContent(packageNameToPackageInfo) + "}";
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

    private String generateModuleInfoContent(Map<String, ParsedPackageInfo> packageNameToPackageInfo) {
        return String.format("""
                exports %1$s to quak.framework.core, quak.framework.web;
                
                %2$s
                
                requires quak.framework.core;
                requires quak.framework.data;
                requires quak.framework.generator.model;
                requires quak.framework.web;
                requires quak.framework.web.model;
                requires jakarta.persistence;
                requires static lombok;
                requires org.reflections;
                """, targetControllerPackage,
                generateRulesForPackages(packageNameToPackageInfo)).indent(4);
    }

    private String generateRulesForPackages(Map<String, ParsedPackageInfo> packageNameToPackageInfo) {
        var resultingRules = new StringBuilder();
        for (var packageInfo : packageNameToPackageInfo.values()) {
            resultingRules.append(generateRulesForPackage(packageInfo));
            resultingRules.append("\n");
        }
        return resultingRules.toString().stripTrailing();
    }

    private String generateRulesForPackage(ParsedPackageInfo packageInfo) {
        var resultingRule = new StringBuilder("opens ").append(packageInfo.getPackageName()).append(" to ");
        var openedTo = new ArrayList<String>();
        if (packageInfo.isContainsDIClasses()) {
            openedTo.add("quak.framework.core");
        }
        if (packageInfo.isContainsRestModelClasses()) {
            openedTo.add("com.fasterxml.jackson.databind");
        }
        if (packageInfo.isContainsORMClasses()) {
            openedTo.add("org.hibernate.orm.core");
        }
        return resultingRule.append(String.join(", ", openedTo)).append(";").toString();
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
            result.append("opens ").append(diPackage).append(" to ").append("quak.framework.core;\n");
        }
        return result.toString().trim();
    }

}
