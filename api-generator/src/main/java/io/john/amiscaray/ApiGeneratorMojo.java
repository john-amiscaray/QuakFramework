package io.john.amiscaray;

import io.john.amiscaray.backend.framework.generator.api.RestModel;
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

@Mojo(name = "generate-class", requiresDependencyResolution = ResolutionScope.RUNTIME, defaultPhase = LifecyclePhase.PROCESS_CLASSES)
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
    public void execute() throws MojoExecutionException {

        getLog().info("Output Directory: " + projectClassOutputDirectory.getAbsolutePath());
        getLog().info("Class scan package: " + classScanPackage);

        var types = ReflectionUtils.loadClassesFromPackage(projectClassOutputDirectory, classScanPackage)
                .stream()
                .filter(clazz -> clazz.isAnnotationPresent(RestModel.class))
                .toList();

        var targetPackage = classScanPackage + ".controllers";
        var outputLocation = targetPackage.replace(".", "/");

        File file = new File(generatedClassesDirectory, outputLocation);
        if (!file.exists()) {
            file.mkdirs();
        }

        getLog().info(types.toString());

        for(var type : types) {
            var generatedClass = controllerWriter.writeNewController(targetPackage, type);
            File outputFile = new File(file, generatedClass.getName());
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(generatedClass.getSourceCode());
            } catch (IOException e) {
                throw new MojoExecutionException("Error writing file " + outputFile, e);
            }
        }

        project.addCompileSourceRoot(generatedClassesDirectory.getAbsolutePath());

    }

}
