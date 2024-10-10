package io.john.amiscaray;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.john.amiscaray.controller.ControllerWriter;
import io.john.amiscaray.jpms.ModuleInfoWriter;
import io.john.amiscaray.model.GeneratedClass;
import io.john.amiscaray.model.VisitedSourcesState;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import static io.john.amiscaray.util.ParserUtils.getAnnotationMemberValue;

@Mojo(name = "generate-class", requiresDependencyResolution = ResolutionScope.RUNTIME, defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ApiGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.sourceDirectory}", required = true)
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources", required = true)
    private File generatedClassesDirectory;

    @Parameter(required = true)
    private String targetPackage;

    @Parameter(required = true)
    private String rootPackage;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    private final ControllerWriter controllerWriter = ControllerWriter.getInstance();

    private final VisitedSourcesState visitedSourcesState = new VisitedSourcesState();

    private String moduleInfoTemplateSource = null;

    private final JavaParser JAVA_PARSER = new JavaParser();

    @Override
    public void execute() {
        if (!sourceDirectory.exists()) {
            getLog().warn("Source directory does not exist: " + sourceDirectory.getAbsolutePath());
            return;
        }

        try {
            inspectSourceFiles(sourceDirectory);
            inspectResourceFiles();
        } catch (IOException e) {
            getLog().error("Unable to generate sources:\n", e);
        }

        generateControllers();
        generateModuleInfo();

        project.addCompileSourceRoot(generatedClassesDirectory.getPath());
    }

    private void generateModuleInfo() {
        var moduleInfoWriter = new ModuleInfoWriter(visitedSourcesState, rootPackage, moduleInfoTemplateSource);
        try {
            writeGeneratedModuleInfo(moduleInfoWriter.writeModuleInfo());
        } catch (IOException e) {
            getLog().error("Could not write module-info.java: ", e);
        }
    }

    private void writeGeneratedModuleInfo(String moduleInfoSource) throws IOException {
        var newGeneratedJavaSource = new File(generatedClassesDirectory, "module-info.java");
        try (var fileWriter = new FileWriter(newGeneratedJavaSource)) {
            fileWriter.write(moduleInfoSource);
        }
    }

    private void generateControllers() {
        var restModelClassToEntityClass = new HashMap<ClassOrInterfaceDeclaration, ClassOrInterfaceDeclaration>();

        for (var restModelToEntityEntry : visitedSourcesState.restModelClassToEntity().entrySet()) {
            var entityClassName = restModelToEntityEntry.getValue().getType().asString();
            // TODO make sure exactly one matches
            if (visitedSourcesState.visitedEntityClasses()
                    .stream()
                    .noneMatch(entityClass -> entityClassName.equals(entityClass.getNameAsString()))) {
                throw new IllegalArgumentException("Missing entity class for " + restModelToEntityEntry.getKey().getNameAsString());
            }
            var entityClassDeclaration = visitedSourcesState.visitedEntityClasses()
                    .stream()
                    .filter(entityClass -> entityClassName.equals(entityClass.getNameAsString()))
                    .findFirst()
                    .get();
            restModelClassToEntityClass.put(restModelToEntityEntry.getKey(), entityClassDeclaration);
        }

        for (var restModelToEntityClassEntry : restModelClassToEntityClass.entrySet()) {
            var generatedSource = controllerWriter.writeNewController(targetPackage, restModelToEntityClassEntry.getKey(), restModelToEntityClassEntry.getValue());
            try {
                writeGeneratedController(generatedSource);
            } catch (IOException e) {
                getLog().error("Could not generate source: ", e);
            }
        }
    }

    private void writeGeneratedController(GeneratedClass generatedClass) throws IOException {
        var packageSubFolders = targetPackage.replace(".", "/");
        var outDirectory = new File(generatedClassesDirectory, "/" + packageSubFolders);
        if (!outDirectory.exists()) {
            outDirectory.mkdirs();
        }
        var newGeneratedJavaSource = new File(outDirectory, generatedClass.name());
        try (var fileWriter = new FileWriter(newGeneratedJavaSource)) {
            fileWriter.write(generatedClass.sourceCode());
        }
    }

    private void inspectSourceFiles(File directory) throws IOException {
        var files = directory.listFiles();

        if (files != null) {
            for (var file : files) {
                if (file.isDirectory()) {
                    inspectSourceFiles(file);
                } else if (file.getName().endsWith(".java")) {
                    parseAndInspectJavaSource(file);
                }
            }
        }

    }

    private void inspectResourceFiles() throws IOException {
        for (var resource : project.getResources()) {
            var resourcePath = Paths.get(resource.getDirectory());
            if (Files.exists(resourcePath) && Files.isDirectory(resourcePath)) {
                try (var resourcesStream = Files.walk(resourcePath)) {
                    resourcesStream.forEach(filePath -> {
                        // TODO Is it possible for there to be multiple resource directories each with module-info templates?
                        if ("module-info.template".equals(filePath.getFileName().toString())) {
                            try {
                                moduleInfoTemplateSource = new String(Files.readAllBytes(filePath));
                            } catch (IOException e) {
                                getLog().error("Could not read module info template: ", e);
                            }
                        }
                    });
                }
            }
        }
    }

    private void parseAndInspectJavaSource(File javaFile) throws IOException {
        // Read the file content
        var fileContent = new String(Files.readAllBytes(Paths.get(javaFile.getAbsolutePath())));

        // Parse the Java file
        var parsedCompilationUnit = JAVA_PARSER.parse(fileContent);

        parsedCompilationUnit.ifSuccessful(compilationUnit -> compilationUnit.accept(new VoidVisitorAdapter<>() {
            @Override
            public void visit(ClassOrInterfaceDeclaration parsedClassOrInterface, VisitedSourcesState state) {
                if (parsedClassOrInterface.getAnnotationByName("RestModel").isPresent()) {
                    var entityClass = getAnnotationMemberValue(parsedClassOrInterface.getAnnotationByName("RestModel").get(), "dataClass").get().asClassExpr();
                    state.visitedRestModelClasses().add(parsedClassOrInterface);
                    state.restModelClassToEntity().put(parsedClassOrInterface, entityClass);
                } else if (parsedClassOrInterface.getAnnotationByName("Entity").isPresent()) {
                    state.visitedEntityClasses().add(parsedClassOrInterface);
                } else if (isParsedClassOrInterfaceDIComponent(parsedClassOrInterface)) {
                    state.visitedDIComponents().add(parsedClassOrInterface);
                }
            }
        }, visitedSourcesState));
    }

    private boolean isParsedClassOrInterfaceDIComponent(ClassOrInterfaceDeclaration parsedClassOrInterface) {
        var implementsDependencyProvider = parsedClassOrInterface.getImplementedTypes()
                .stream()
                .anyMatch(interfaceImplemented ->
                        interfaceImplemented.getNameAsString().equals("DependencyProvider")
                );
        var isManagedType = parsedClassOrInterface.getAnnotationByName("ManagedType").isPresent();
        var isProvider = parsedClassOrInterface.getAnnotationByName("Provider").isPresent();

        return implementsDependencyProvider || isManagedType || isProvider;
    }

}
