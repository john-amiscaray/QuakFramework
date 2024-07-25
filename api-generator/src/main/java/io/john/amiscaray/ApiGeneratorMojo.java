package io.john.amiscaray;

import io.john.amiscaray.backend.framework.generator.api.RestModel;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Mojo(name = "generate-class", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class ApiGeneratorMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.directory}/generated-sources", required = true)
    private File outputDirectory;

    @Parameter(required = true)
    private String classScanPackage;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    private final ControllerWriter controllerWriter = ControllerWriter.getInstance();

    @Override
    public void execute() throws MojoExecutionException {

        var reflections = new Reflections(classScanPackage, Scanners.TypesAnnotated);
        var types = reflections.getTypesAnnotatedWith(RestModel.class);
        var targetPackage = classScanPackage + ".controllers";
        var outputLocation = targetPackage.replace(".", "/");

        File file = new File(outputDirectory, outputLocation);
        if (!file.exists()) {
            file.mkdirs();
        }

        for(var type : types) {
            var generatedClass = controllerWriter.writeNewController(targetPackage, type);
            File outputFile = new File(outputLocation, generatedClass.name());
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(generatedClass.sourceCode());
            } catch (IOException e) {
                throw new MojoExecutionException("Error writing file " + outputFile, e);
            }
        }

        project.addCompileSourceRoot(outputDirectory.getAbsolutePath());

    }
}
