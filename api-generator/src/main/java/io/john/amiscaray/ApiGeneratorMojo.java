package io.john.amiscaray;

import io.john.amiscaray.backend.framework.generator.api.RestModel;
import io.john.amiscaray.controller.ControllerWriter;
import io.john.amiscaray.jpms.ModuleInfoWriter;
import io.john.amiscaray.util.ReflectionUtils;
import lombok.SneakyThrows;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Mojo(name = "generate-class", requiresDependencyResolution = ResolutionScope.RUNTIME, defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ApiGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private File projectClassOutputDirectory;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources")
    private File generatedClassesDirectory;

    @Parameter(required = true)
    private String classScanPackage;

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    private final ControllerWriter controllerWriter = ControllerWriter.getInstance();

    @SneakyThrows
    @Override
    public void execute() {

        getLog().info("Output Directory: " + projectClassOutputDirectory.getAbsolutePath());
        getLog().info("Class scan package: " + classScanPackage);

        var projectClasses = ReflectionUtils.loadClassesFromPackage(projectClassOutputDirectory, classScanPackage);
        getLog().info("HELLOOOOO:");
        getLog().info(String.join(" , ", projectClasses.stream().map(Class::getSimpleName).toList()));
        var types = projectClasses
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(RestModel.class))
                .toList();

        var targetPackage = classScanPackage + ".controllers";
        var outputLocation = targetPackage.replace(".", "/");

        File controllerOutputFileLocation = new File(generatedClassesDirectory, outputLocation);
        if (!controllerOutputFileLocation.exists()) {
            controllerOutputFileLocation.mkdirs();
        }

        getLog().info(types.toString());

        for(var type : types) {
            var generatedClass = controllerWriter.writeNewController(targetPackage, type);
            File outputFile = new File(controllerOutputFileLocation, generatedClass.name());
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(generatedClass.sourceCode());
            } catch (IOException e) {
                throw new MojoExecutionException("Error writing file " + outputFile, e);
            }
        }

//        var generatedModuleInfo = new ModuleInfoWriter(projectClasses, classScanPackage).writeModuleInfo(getLog());
//
//        if (generatedModuleInfo != null) {
//            File moduleInfoFileLocation = new File(generatedClassesDirectory, "module-info.java");
//
//            try (FileWriter writer = new FileWriter(moduleInfoFileLocation)) {
//                writer.write(generatedModuleInfo);
//            } catch (IOException e) {
//                throw new MojoExecutionException("Error writing file " + moduleInfoFileLocation, e);
//            }
//        }

        project.addCompileSourceRoot(generatedClassesDirectory.getPath());

    }

}
