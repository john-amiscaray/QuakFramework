package io.john.amiscaray;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.john.amiscaray.controller.ControllerWriter;
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

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    private final ControllerWriter controllerWriter = ControllerWriter.getInstance();

    private final VisitedSourcesState visitedSourcesState = new VisitedSourcesState();

    @Override
    public void execute() {
        if (!sourceDirectory.exists()) {
            getLog().warn("Source directory does not exist: " + sourceDirectory.getAbsolutePath());
            return;
        }

        try {
            inspectSourceFiles(sourceDirectory);
        } catch (IOException e) {
            getLog().error("Unable to generate sources:\n", e);
        }

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
                writeGeneratedSource(generatedSource);
            } catch (IOException e) {
                getLog().error("Could not generate source: ", e);
            }
        }

        project.addCompileSourceRoot(generatedClassesDirectory.getPath());
    }

    private void inspectSourceFiles(File directory) throws IOException {
        var files = directory.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                inspectSourceFiles(file);
            } else if (file.getName().endsWith(".java")) {
                parseAndInspectJavaSource(file);
            }
        }

    }

    private void parseAndInspectJavaSource(File javaFile) throws IOException {
        // Read the file content
        var fileContent = new String(Files.readAllBytes(Paths.get(javaFile.getAbsolutePath())));
        var javaParser = new JavaParser();

        // Parse the Java file
        var parsedCompilationUnit = javaParser.parse(fileContent);

        parsedCompilationUnit.ifSuccessful(compilationUnit -> compilationUnit.accept(new VoidVisitorAdapter<>() {
            @Override
            public void visit(ClassOrInterfaceDeclaration parsedClassOrInterface, VisitedSourcesState state) {
                if (parsedClassOrInterface.getAnnotationByName("RestModel").isPresent()) {
                    var entityClass = getAnnotationMemberValue(parsedClassOrInterface.getAnnotationByName("RestModel").get(), "dataClass").get().asClassExpr();
                    state.visitedRestModelClasses().add(parsedClassOrInterface);
                    state.restModelClassToEntity().put(parsedClassOrInterface, entityClass);
                } else if (parsedClassOrInterface.getAnnotationByName("Entity").isPresent()) {
                    state.visitedEntityClasses().add(parsedClassOrInterface);
                }
            }
        }, visitedSourcesState));
    }

    private void writeGeneratedSource(GeneratedClass generatedClass) throws IOException {
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

}
