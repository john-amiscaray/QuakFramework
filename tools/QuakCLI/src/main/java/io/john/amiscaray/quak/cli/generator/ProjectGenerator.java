package io.john.amiscaray.quak.cli.generator;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.Terminal;
import io.john.amiscaray.quak.cli.cfg.ProjectConfig;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
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

    public void generateProject(ProjectConfig projectConfig) throws IOException, InterruptedException, ParserConfigurationException, SAXException, TransformerException {
        var rootFolder = projectConfig.artifactID();
        var packagePath = createPackagePath(projectConfig);
        var sourcesDir = new File(rootFolder + "/src/main/java/" + packagePath);
        var resourcesDir = new File(rootFolder + "/src/main/resources");
        var testDir = new File(rootFolder + "/src/main/test/java/" + packagePath);

        resourcesDir.mkdirs();
        sourcesDir.mkdirs();
        testDir.mkdirs();

        writeToFile(new File(rootFolder, "pom.xml"), generateProjectPomSrc(projectConfig));
        writeToFile(new File(sourcesDir, "Main.java"), generateProjectMainClassSrc(projectConfig));

        var mavenSettings = new File(FileUtils.getUserDirectory() + "/.m2", "settings.xml");
        if (!mavenSettings.exists()) {
            writeToFile(new File(FileUtils.getUserDirectory() + "/.m2", "settings.xml"), generateMavenSettings());
        } else {
            addGithubServerToExistingMavenSettings(mavenSettings);
        }
        terminal.setForegroundColor(TextColor.ANSI.YELLOW);
        terminal.putCharacter('\n');
        terminal.putString("WARNING: setting.xml written in ~/.m2/settings.xml. Ensure you put your github username and personal access token in the added 'github' server. This allows you to authenticate with Github packages.");
        terminal.flush();
        Thread.sleep(5000);
    }

    private void writeToFile(File file, String src) throws IOException, InterruptedException {
        try (var fileWriter = new FileWriter(file)) {
            fileWriter.write(src);
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

    private String generateProjectMainClassSrc(ProjectConfig projectConfig) {
        return String.format("""
                package %1$s;
                
                import io.john.amiscaray.quak.web.application.WebStarter;
                                
                import java.util.concurrent.ExecutionException;
                import java.util.concurrent.TimeUnit;
                import java.util.concurrent.TimeoutException;
                                
                public class Main {
                                
                    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {
                        var application = WebStarter.beginWebApplication(Main.class, args)
                                .get(10, TimeUnit.SECONDS);
                                
                        application.await();
                    }
                                
                }
                """, projectConfig.groupID());
    }

    private String generateMavenSettings() {
        return """
                <!-- IMPORTANT: copy this to ~/.m2/settings.xml -->
                <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd">
                    <servers>
                        <server>
                            <id>github</id>
                            <username>Your GitHub Username</username> <!-- Use environment variables in production. Example: ${env.GITHUB_ACTOR} -->
                            <password>Your GitHub Personal Access Token</password> <!-- Use environment variables in production. Example: ${env.GH_TOKEN} -->
                        </server>
                    </servers>
                </settings>
                """;
    }

    private void addGithubServerToExistingMavenSettings(File mavenSettings) throws IOException, SAXException, ParserConfigurationException, TransformerException, InterruptedException {
        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        var documentBuilder = documentBuilderFactory.newDocumentBuilder();
        var document = documentBuilder.parse(mavenSettings);
        var serversTags = document.getDocumentElement().getElementsByTagName("servers");
        var isNewServersTag = serversTags.getLength() == 0;

        var serversTag = isNewServersTag ? document.createElement("servers") : serversTags.item(0);
        var containsGithubServer = false;
        for(var j = 0; j < serversTag.getChildNodes().getLength(); j++) {
            var child = serversTag.getChildNodes().item(j);
            if (child.getNodeName().equals("server")) {
                for(var k = 0; k < child.getChildNodes().getLength(); k++) {
                    var serverProp = child.getChildNodes().item(k);
                    if (serverProp.getNodeName().equals("id")) {
                        if (serverProp.getTextContent().equals("github")) {
                            containsGithubServer = true;
                        }
                    }
                }
            }
        }
        if (containsGithubServer) {
            return;
        }
        var newServer = document.createElement("server");
        var serverID = document.createElement("id");
        serverID.setTextContent("github");

        var username = document.createElement("username");
        username.setTextContent("Your GitHub Username");

        var password = document.createElement("password");
        password.setTextContent("Your GitHub Personal Access Token");

        newServer.appendChild(serverID);
        newServer.appendChild(username);
        newServer.appendChild(password);

        serversTag.appendChild(newServer);

        if (isNewServersTag) {
            document.getDocumentElement().appendChild(serversTag);
        }

        var transformerFactory = TransformerFactory.newInstance();
        var transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        var source = new DOMSource(document);
        var result = new StreamResult(mavenSettings);
        transformer.transform(source, result);
    }

    private void generateAuthFramework(ProjectConfig projectConfig) {

    }

}
