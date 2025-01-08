package io.john.amiscaray.quak.cli.generator;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.Terminal;
import io.john.amiscaray.quak.cli.cfg.ProjectConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ProjectGenerator {

    private static ProjectGenerator instance;
    private Terminal terminal;

    private ProjectGenerator() { }

    public static ProjectGenerator getInstance() {
        if (instance == null) {
            instance = new ProjectGenerator();
        }
        return instance;
    }

    public void init(Terminal terminal) {
        this.terminal = terminal;
    }

    public void generateProject(ProjectConfig projectConfig) throws IOException, InterruptedException {
        var rootFolder = projectConfig.artifactID();
        var packagePath = createPackagePath(projectConfig);
        var sourcesDir = new File(rootFolder + "/src/main/java/" + packagePath);
        var resourcesDir = new File(rootFolder + "/src/main/resources");
        var testDir = new File(rootFolder + "/src/main/test/java/" + packagePath);
        resourcesDir.mkdirs();
        sourcesDir.mkdirs();
        testDir.mkdirs();
        var projectPom = new File(rootFolder, "pom.xml");
        try (var fileWriter = new FileWriter(projectPom)) {
            fileWriter.write(generateProjectPomSrc(projectConfig));
        } catch (IOException ex) {
            terminal.setForegroundColor(TextColor.ANSI.RED_BRIGHT);
            terminal.putCharacter('\n');
            terminal.putString("Error writing pom.xml: " + ex.getMessage());
            terminal.flush();
            Thread.sleep(5000);
        }
    }

    private String createPackagePath(ProjectConfig projectConfig) {
        return projectConfig.groupID().replace(".", "/") + "/";
    }

    private String generateProjectPomSrc(ProjectConfig projectConfig) {
        return String.format("""
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0"
                         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
                    <modelVersion>4.0.0</modelVersion>
                                
                    <groupId>%1$s</groupId>
                    <artifactId>%2$s</artifactId>
                    <version>1.0-SNAPSHOT</version>
                                
                    <properties>
                        <maven.compiler.source>21</maven.compiler.source>
                        <maven.compiler.target>21</maven.compiler.target>
                        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    </properties>
                                
                    <dependencies>
                        <dependency>
                            <groupId>io.john.amiscaray</groupId>
                            <artifactId>quak.framework.core</artifactId>
                            <version>1.0-SNAPSHOT</version>
                        </dependency>
                        <dependency>
                            <groupId>io.john.amiscaray</groupId>
                            <artifactId>quak.framework.data</artifactId>
                            <version>1.0-SNAPSHOT</version>
                        </dependency>
                        <dependency>
                            <groupId>io.john.amiscaray</groupId>
                            <artifactId>quak.framework.web</artifactId>
                            <version>1.0-SNAPSHOT</version>
                        </dependency>
                        <dependency>
                            <groupId>io.john.amiscaray</groupId>
                            <artifactId>quak.framework.generator-model</artifactId>
                            <version>1.0-SNAPSHOT</version>
                        </dependency>
                        <dependency>
                            <groupId>jakarta.persistence</groupId>
                            <artifactId>jakarta.persistence-api</artifactId>
                            <version>3.1.0</version>
                        </dependency>
                        <dependency>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>1.18.34</version>
                        </dependency>
                    </dependencies>
                                
                    <repositories>
                        <repository>
                            <id>github</id>
                            <url>https://maven.pkg.github.com/john-amiscaray/QuakFramework</url>
                        </repository>
                    </repositories>
                                
                    <pluginRepositories>
                        <pluginRepository>
                            <id>github</id>
                            <url>https://maven.pkg.github.com/john-amiscaray/QuakFramework</url>
                        </pluginRepository>
                    </pluginRepositories>
                                
                    <build>
                        <plugins>
                            <plugin>
                                <groupId>org.apache.maven.plugins</groupId>
                                <artifactId>maven-compiler-plugin</artifactId>
                                <version>3.13.0</version>
                                <configuration>
                                    <source>${maven.compiler.source}</source>
                                    <target>${maven.compiler.target}</target>
                                    <annotationProcessorPaths>
                                        <path>
                                            <groupId>org.projectlombok</groupId>
                                            <artifactId>lombok</artifactId>
                                            <version>1.18.34</version>
                                        </path>
                                    </annotationProcessorPaths>
                                    <compilerArgs>
                                        <arg>-sourcepath</arg>
                                        <arg>${project.basedir}/src/main/java${path.separator}${project.basedir}/target/generated-sources/annotations${path.separator}/</arg>
                                    </compilerArgs>
                                </configuration>
                            </plugin>
                            <plugin>
                                <groupId>io.john.amiscaray</groupId>
                                <artifactId>quak.framework.generator</artifactId>
                                <version>1.0-SNAPSHOT</version>
                                <executions>
                                    <execution>
                                        <goals>
                                            <goal>generate-controllers</goal>
                                        </goals>
                                        <phase>process-sources</phase>
                                        <configuration>
                                            <rootPackage>%1$s</rootPackage>
                                            <targetPackage>%1$s.%2$s</targetPackage>
                                        </configuration>
                                    </execution>
                                </executions>
                            </plugin>
                        </plugins>
                    </build>
                </project>
                """, projectConfig.groupID(), projectConfig.artifactID());
    }

}
