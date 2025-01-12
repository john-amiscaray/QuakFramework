package io.john.amiscaray.quak.cli.generator;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.terminal.Terminal;
import io.john.amiscaray.quak.cli.cfg.ProjectConfig;
import io.john.amiscaray.quak.cli.templates.Template;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
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
        var mavenSettingsWritten = true;
        if (!mavenSettings.exists()) {
            writeToFile(new File(FileUtils.getUserDirectory() + "/.m2", "settings.xml"), generateMavenSettings());
        } else {
            mavenSettingsWritten = addGithubServerToExistingMavenSettings(mavenSettings);
        }

        writeToFile(new File(resourcesDir, "application.properties"), "");

        if (projectConfig.template().equals(Template.AUTH)) {
            generateAuthFramework(new File(rootFolder), sourcesDir, resourcesDir, projectConfig);
        }
        if (mavenSettingsWritten) {
            terminal.setForegroundColor(TextColor.ANSI.YELLOW);
            terminal.putCharacter('\n');
            terminal.putString("WARNING: setting.xml written in ~/.m2/settings.xml. Ensure you put your github username and personal access token in the added 'github' server. This allows you to authenticate with Github packages.");
            terminal.flush();
            Thread.sleep(5000);
        }
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

    /**
     * @param mavenSettings
     * @return a boolean whether the file was written.
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws TransformerException
     * @throws InterruptedException
     */
    private boolean addGithubServerToExistingMavenSettings(File mavenSettings) throws IOException, SAXException, ParserConfigurationException, TransformerException, InterruptedException {
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
            return false;
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

        writeTransformedXML(document, mavenSettings);
        return true;
    }

    private void generateAuthFramework(File rootDir, File sourcesDir, File resourcesDir, ProjectConfig projectConfig) throws IOException, InterruptedException, ParserConfigurationException, TransformerException, SAXException {
        var ormFolder = new File(sourcesDir, "/orm");
        var securityFolder = new File(sourcesDir, "/security");
        var cryptFolder = new File(securityFolder, "/encryption");
        var securityDIFolder = new File(securityFolder, "/di");
        var modelsFolder = new File(sourcesDir, "/models");
        var controllersFolder = new File(sourcesDir, "/controllers");
        var controllersConfigFolder = new File(sourcesDir, "/controllers/config");

        ormFolder.mkdirs();
        securityFolder.mkdirs();
        modelsFolder.mkdirs();
        cryptFolder.mkdirs();
        securityDIFolder.mkdirs();
        controllersFolder.mkdirs();
        controllersConfigFolder.mkdirs();

        writeToFile(new File(resourcesDir, "application.properties"), """
                hibernate.connection.url=jdbc:mysql://localhost:3306/test
                hibernate.connection.driver_class=com.mysql.cj.jdbc.Driver
                hibernate.dialect=org.hibernate.dialect.MySQLDialect
                hibernate.connection.username=root
                hibernate.connection.password=password
                hibernate.hbm2ddl.auto=create-drop
                jwt.secret.key=${JWT_SECRET}
                """);
        writeToFile(new File(resourcesDir, "module-info.template"), String.format("""
                module my.module {
                                
                    requires org.slf4j;
                    requires quak.framework.security;
                    requires bcrypt;
                    requires com.auth0.jwt;
                                
                    requires com.fasterxml.jackson.databind;
                                
                    exports %1$s.models to com.fasterxml.jackson.databind;
                }
                """, projectConfig.groupID()));
        writeToFile(new File(ormFolder, "User.java"), String.format("""
                package %1$s.orm;
                                
                import jakarta.persistence.*;
                import lombok.Getter;
                import lombok.Setter;
                                
                @Setter
                @Getter
                @Entity
                public class User {
                                
                    @Id
                    @GeneratedValue(strategy = GenerationType.AUTO)
                    private long id;
                    @Column(unique = true, nullable = false)
                    private String username;
                    private String password;
                                
                    public User() {
                    }
                                
                    public User(String username, String password) {
                        this.username = username;
                        this.password = password;
                    }
                                
                }
                """, projectConfig.groupID()));
        writeToFile(new File(securityFolder, "Authenticator.java"), String.format("""
                package %1$s.security;
                                
                import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
                import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
                import io.john.amiscaray.quak.data.DatabaseProxy;
                import io.john.amiscaray.quak.security.auth.credentials.Credentials;
                import io.john.amiscaray.quak.security.auth.jwt.JwtUtil;
                import io.john.amiscaray.quak.security.auth.principal.Principal;
                import io.john.amiscaray.quak.security.di.SecurityDependencyIDs;
                import %1$s.orm.User;
                import %1$s.security.encryption.BcryptService;
                import lombok.extern.java.Log;
                                
                import java.util.Optional;
                                
                import static io.john.amiscaray.quak.data.query.QueryCriteria.*;
                                
                @ManagedType(dependencyName = SecurityDependencyIDs.AUTHENTICATOR_DEPENDENCY_NAME, dependencyType = io.john.amiscaray.quak.security.auth.Authenticator.class)
                @Log
                public class Authenticator implements io.john.amiscaray.quak.security.auth.Authenticator {
                                
                    private final DatabaseProxy databaseProxy;
                    private final JwtUtil jwtUtil;
                    private final BcryptService bcryptService;
                                
                    @Instantiate
                    public Authenticator(DatabaseProxy databaseProxy, JwtUtil jwtUtil, BcryptService bcryptService) {
                        this.databaseProxy = databaseProxy;
                        this.jwtUtil = jwtUtil;
                        this.bcryptService = bcryptService;
                    }
                                
                    @Override
                    public Optional<Principal> lookupPrincipal(String securityID) {
                        var matchingUsers = databaseProxy.queryAllWhere(User.class, valueOfField("id", is(securityID)));
                        if (!matchingUsers.isEmpty()) {
                            assert matchingUsers.size() == 1;
                            var user = matchingUsers.getFirst();
                            return Optional.of(() -> user.getId() + "");
                        }
                        return Optional.empty();
                    }
                                
                    @Override
                    public Optional<Principal> lookupPrincipal(Credentials credentials) {
                        var matchingUsers = databaseProxy.queryAllWhere(User.class, valueOfField("username", is(credentials.getUsername())));
                        if (!matchingUsers.isEmpty()) {
                            assert matchingUsers.size() == 1;
                            var user = matchingUsers.getFirst();
                            if (bcryptService.verify(credentials.getPassword(), user.getPassword())) {
                                return Optional.of(() -> user.getId() + "");
                            }
                        }
                        return Optional.empty();
                    }
                                
                }
                """, projectConfig.groupID()));
        writeToFile(new File(cryptFolder, "BcryptService.java"), String.format("""
                package %1$s.security.encryption;
                                
                import at.favre.lib.crypto.bcrypt.BCrypt;
                import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
                                
                @ManagedType
                public class BcryptService {
                                
                    private final BCrypt.Hasher hasher;
                    private final BCrypt.Verifyer verifyer;
                                
                    public BcryptService() {
                        hasher = BCrypt.withDefaults();
                        verifyer = BCrypt.verifyer();
                    }
                                
                    public String hash(String password) {
                        return hasher.hashToString(10, password.toCharArray());
                    }
                                
                    public boolean verify(String password, String hashedPassword) {
                        return verifyer.verify(password.toCharArray(), hashedPassword.toCharArray()).verified;
                    }
                                
                }
                """, projectConfig.groupID()));
        writeToFile(new File(securityDIFolder, "JWTSecretProvider.java"), String.format("""
                package %1$s.security.di;
                                
                import io.john.amiscaray.quak.core.di.provider.annotation.Provide;
                import io.john.amiscaray.quak.core.di.provider.annotation.Provider;
                import io.john.amiscaray.quak.core.properties.ApplicationProperties;
                import io.john.amiscaray.quak.core.properties.ApplicationProperty;
                                
                @Provider
                public class JWTSecretProvider {
                                
                    @Provide(dependencyName = "jwt")
                    public String provideJWTSecret() {
                        var properties = ApplicationProperties.getInstance();
                        return properties.get(ApplicationProperty.JWT_SECRET_KEY);
                    }
                                
                }
                """, projectConfig.groupID()));
        writeToFile(new File(securityDIFolder, "SecurityConfigProvider.java"), String.format("""
                package %1$s.security.di;
                                
                import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
                import io.john.amiscaray.quak.core.di.provider.annotation.Provide;
                import io.john.amiscaray.quak.core.di.provider.annotation.ProvidedWith;
                import io.john.amiscaray.quak.core.di.provider.annotation.Provider;
                import io.john.amiscaray.quak.security.auth.principal.role.Role;
                import io.john.amiscaray.quak.security.config.CORSConfig;
                import io.john.amiscaray.quak.security.config.EndpointMapping;
                import io.john.amiscaray.quak.security.config.SecurityConfig;
                import io.john.amiscaray.quak.security.di.AuthenticationStrategy;
                import io.john.amiscaray.quak.security.di.SecurityDependencyIDs;
                                
                import java.time.Duration;
                import java.util.List;
                                
                @Provider
                public class SecurityConfigProvider {
                                
                    private final String jwtSecret;
                                
                    @Instantiate
                    public SecurityConfigProvider(@ProvidedWith(dependencyName = "jwt") String jwtSecret) {
                        this.jwtSecret = jwtSecret;
                    }
                                
                    @Provide(dependencyName = SecurityDependencyIDs.SECURITY_CONFIG_DEPENDENCY_NAME)
                    public SecurityConfig securityConfig() {
                        return SecurityConfig.builder()
                                .securePathWithRole(new EndpointMapping(
                                        "/hello",
                                        List.of(EndpointMapping.RequestMethodMatcher.ALL)
                                ), List.of(Role.any()))
                                .securePathWithCorsConfig("/*", CORSConfig.builder()
                                        .allowOrigin("*")
                                        .allowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH"))
                                        .build())
                                .authenticationStrategy(AuthenticationStrategy.JWT)
                                .jwtSecretKey(jwtSecret)
                                .jwtSecretExpiryTime(Duration.ofHours(10).toMillis())
                                .build();
                    }
                                
                }
                """, projectConfig.groupID()));
        writeToFile(new File(modelsFolder, "AuthRequestBody.java"), String.format("""
                package %1$s.models;
                                
                public record AuthRequestBody(String username, String password) {
                
                }
                """, projectConfig.groupID()));
        writeToFile(new File(controllersFolder, "AuthController.java"), String.format("""
                package %1$s.controllers;
                                
                import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
                import io.john.amiscaray.quak.data.DatabaseProxy;
                import io.john.amiscaray.quak.http.request.Request;
                import io.john.amiscaray.quak.http.request.RequestMethod;
                import io.john.amiscaray.quak.http.response.Response;
                import io.john.amiscaray.quak.security.auth.Authenticator;
                import io.john.amiscaray.quak.security.auth.credentials.Credentials;
                import io.john.amiscaray.quak.security.auth.exception.InvalidCredentialsException;
                import io.john.amiscaray.quak.security.auth.jwt.JwtUtil;
                import io.john.amiscaray.quak.web.controller.annotation.Controller;
                import io.john.amiscaray.quak.web.handler.annotation.Handle;
                import %1$s.models.AuthRequestBody;
                import %1$s.orm.User;
                import %1$s.security.encryption.BcryptService;
                                
                @Controller
                public class AuthController {
                                
                    private final JwtUtil jwtUtil;
                    private final Authenticator authenticator;
                    private final DatabaseProxy databaseProxy;
                    private final BcryptService bcryptService;
                                
                    @Instantiate
                    public AuthController(JwtUtil jwtUtil, Authenticator authenticator, DatabaseProxy databaseProxy, BcryptService bcryptService) {
                        this.jwtUtil = jwtUtil;
                        this.authenticator = authenticator;
                        this.databaseProxy = databaseProxy;
                        this.bcryptService = bcryptService;
                    }
                                
                    @Handle(path="/signup", method = RequestMethod.POST)
                    public Response<Void> signUp(Request<AuthRequestBody> request) {
                        var requestBody = request.body();
                        databaseProxy.persist(new User(
                                requestBody.username(),
                                bcryptService.hash(requestBody.password())
                        ));
                        return new Response<>(201, null);
                    }
                                
                    @Handle(path="/login", method = RequestMethod.POST)
                    public Response<String> login(Request<AuthRequestBody> request) {
                        var requestBody = request.body();
                                
                        try {
                            var authentication = authenticator.authenticate(new Credentials() {
                                @Override
                                public String getUsername() {
                                    return requestBody.username();
                                }
                                
                                @Override
                                public String getPassword() {
                                    return requestBody.password();
                                }
                            });
                            var jwt = jwtUtil.generateToken(authentication.getIssuedTo());
                            return Response.of(jwt);
                        } catch (InvalidCredentialsException e) {
                            return new Response<>(401, "Invalid credentials");
                        }
                    }
                                
                } 
                """, projectConfig.groupID()));
        writeToFile(new File(controllersFolder, "HelloController.java"), String.format("""
                package %1$s.controllers;
                                
                import io.john.amiscaray.quak.http.request.Request;
                import io.john.amiscaray.quak.http.request.RequestMethod;
                import io.john.amiscaray.quak.http.response.Response;
                import io.john.amiscaray.quak.web.controller.annotation.Controller;
                import io.john.amiscaray.quak.web.handler.annotation.Handle;
                                
                @Controller
                public class HelloController {
                                
                    @Handle(path = "/hello", method = RequestMethod.GET)
                    public Response<String> hello(Request<Void> request) {
                        return Response.of("Hello, world");
                    }
                                
                }
                """, projectConfig.groupID()));
        writeToFile(new File(controllersConfigFolder, "AppWebConfigProvider.java"), String.format("""
                package %1$s.controllers.config;
                                
                import com.fasterxml.jackson.databind.JsonMappingException;
                import io.john.amiscaray.quak.core.di.provider.annotation.Provide;
                import io.john.amiscaray.quak.core.di.provider.annotation.Provider;
                import io.john.amiscaray.quak.web.cfg.WebConfig;
                                
                import java.util.Map;
                                
                @Provider
                public class AppWebConfigProvider {
                                
                    @Provide(dependencyName = WebConfig.APPLICATION_WEB_CFG_DEPENDENCY_NAME)
                    public WebConfig provideWebConfig() {
                        return new WebConfig(Map.of(JsonMappingException.class, 400));
                    }
                                
                }
                """, projectConfig.groupID()));

        addBCryptDependencyToPom(rootDir);
    }

    private void addBCryptDependencyToPom(File rootDir) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        var pom = new File(rootDir, "pom.xml");
        var documentBuilderFactory = DocumentBuilderFactory.newInstance();
        var documentBuilder = documentBuilderFactory.newDocumentBuilder();
        var document = documentBuilder.parse(pom);

        var dependencies = document.getDocumentElement().getElementsByTagName("dependencies").item(0);

        var newDependency = document.createElement("dependency");

        var newDependencyGroupId = document.createElement("groupId");
        newDependencyGroupId.setTextContent("at.favre.lib");
        var newDependencyArtifactId = document.createElement("artifactId");
        newDependencyArtifactId.setTextContent("bcrypt");
        var newDependencyVersion = document.createElement("version");
        newDependencyVersion.setTextContent("0.10.2"); // TODO make this version dynamic

        newDependency.appendChild(newDependencyGroupId);
        newDependency.appendChild(newDependencyArtifactId);
        newDependency.appendChild(newDependencyVersion);

        dependencies.appendChild(newDependency);

        writeTransformedXML(document, pom);
    }

    private void writeTransformedXML(Document document, File altered) throws TransformerException {
        var transformerFactory = TransformerFactory.newInstance();
        var transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        var source = new DOMSource(document);
        var result = new StreamResult(altered);
        transformer.transform(source, result);
    }

}
