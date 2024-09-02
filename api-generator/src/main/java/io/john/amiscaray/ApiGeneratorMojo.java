package io.john.amiscaray;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import io.john.amiscaray.controller.ControllerWriter;
import io.john.amiscaray.model.SourceClassVisitorState;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.PreparedStatement;
import java.util.HashMap;

@Mojo(name = "generate-class", requiresDependencyResolution = ResolutionScope.RUNTIME, defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ApiGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.sourceDirectory}", required = true)
    private File sourceDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources", required = true)
    private File generatedClassesDirectory;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    private final ControllerWriter controllerWriter = ControllerWriter.getInstance();

    private final SourceClassVisitorState sourceClassVisitorState = new SourceClassVisitorState();

    @Override
    public void execute() {
        if (!sourceDirectory.exists()) {
            getLog().warn("Source directory does not exist: " + sourceDirectory.getAbsolutePath());
            return;
        }

        project.addCompileSourceRoot(generatedClassesDirectory.getPath());
    }

    private void inspectSourceFiles(File directory) throws IOException {
        var files = directory.listFiles();

        for (File file : files) {
            if (file.isDirectory()) {
                inspectSourceFiles(directory);
            } else if (file.getName().endsWith(".java")) {
                parseAndInspectJavaSource(file);
            }
        }

        var restModelClassToEntityClass = new HashMap<ClassOrInterfaceDeclaration, ClassOrInterfaceDeclaration>();

        for (var restModelToEntityEntry : sourceClassVisitorState.restModelClassToEntity().entrySet()) {
            var entityClassName = restModelToEntityEntry.getValue().getType().asString();
            // TODO make sure exactly one matches
            if (sourceClassVisitorState.visitedEntityClasses()
                    .stream()
                    .noneMatch(entityClass -> entityClassName.equals(entityClass.getNameAsString()))) {
                throw new IllegalArgumentException("Missing entity class for " + restModelToEntityEntry.getKey().getNameAsString());
            }
            var entityClassDeclaration = sourceClassVisitorState.visitedEntityClasses()
                    .stream()
                    .filter(entityClass -> entityClassName.equals(entityClass.getNameAsString()))
                    .findFirst()
                    .get();
            restModelClassToEntityClass.put(restModelToEntityEntry.getKey(), entityClassDeclaration);
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
            public void visit(ClassOrInterfaceDeclaration parsedClassOrInterface, SourceClassVisitorState state) {
                if (parsedClassOrInterface.getAnnotationByName("RestModel").isPresent()) {
                    var restModelMetaData = (SingleMemberAnnotationExpr) parsedClassOrInterface.getAnnotationByName("RestModel").get();
                    var entityClass = (ClassExpr) restModelMetaData.getMemberValue();
                    state.restModelClassToEntity().put(parsedClassOrInterface, entityClass);
                } else if (parsedClassOrInterface.getAnnotationByName("Entity").isPresent()) {
                    state.visitedEntityClasses().add(parsedClassOrInterface);
                }
            }
        }, sourceClassVisitorState));
    }

}
