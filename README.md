# Quak Framework

<picture>
  <source srcset="assets/logo/quak_logo_dark.png" media="(prefers-color-scheme: dark)">
  <source srcset="assets/logo/quak_logo_light.png" media="(prefers-color-scheme: light)">
  <img src="assets/logo/quak_logo_light.png" alt="Quak logo">
</picture>

> While this project is still in its 1.0-SNAPSHOT version, please feel free to give feedback on the current state of it [here](https://forms.gle/8hzAeV2Ae1p9ksYcA).

## Overview

An intuitive Java backend framework to quickly develop REST APIs. Features:

- Dependency injection
- Application lifecycle hooks
- Annotation-based HTTP request handling
- Code generation
- HTTP Authentication and Authorization
- Database operations

Below are some simple code snippets to get you a quick look into some of the main features of Quak:

### Simple Hello World Controller Application

```java
package io.john.amiscaray.test.controllers;

import io.john.amiscaray.quak.http.request.Request;
import io.john.amiscaray.quak.http.request.RequestMethod;
import io.john.amiscaray.quak.http.response.Response;
import io.john.amiscaray.quak.web.controller.annotation.Controller;
import io.john.amiscaray.quak.web.handler.annotation.Handle;

@Controller
public class HelloWorldController {

    @Handle(path="/greeting", method = RequestMethod.GET)
    public Response<String> greet(Request<Void> request) {
        return Response.of("Hello World!");
    }

}
```

```java
package io.john.amiscaray.test;

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
```

### Data Querying

```java
package io.john.amiscaray.quak.data.test.stub;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String department;
    private Long salary;

    public Employee(Long id, String name, String department) {
        this(name, department, 40000L);
        this.id = id;
    }

    public Employee(String name, String department, Long salary) {
        this.name = name;
        this.department = department;
        this.salary = salary;
    }

    public Employee(String name, String department) {
        this(name, department, 40000L);
    }
}
```

```java
package io.john.amiscaray.test.data;

import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.data.DatabaseProxy;
import io.john.amiscaray.quak.data.query.DatabaseQuery;
import io.john.amiscaray.test.orm.Employee;

import java.util.List;

import static io.john.amiscaray.quak.data.query.QueryCriteria.*;

@ManagedType
public class EmployeeRepository {

    private DatabaseProxy databaseProxy;

    @Instantiate
    public EmployeeRepository(DatabaseProxy databaseProxy) {
        this.databaseProxy = databaseProxy;
    }

    public List<Employee> queryEmployeesWithIDsLessThanOrEqualTo2AndGreaterThanOrEqualTo4() {
        return databaseProxy.queryAllWhere(Employee.class, valueOfField("id", isGreaterThanOrEqualTo(2)).and(valueOfField("id", isLessThanOrEqualTo(4))));
    }
}
```

### Configure CORS And JWT Auth

```java
package io.john.amiscaray.test.security.di;

import io.john.amiscaray.quak.core.di.provider.annotation.Provide;
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
public class SecurityConfigProvider2 {

    @Provide(dependencyName = SecurityDependencyIDs.SECURITY_CONFIG_DEPENDENCY_NAME)
    public SecurityConfig securityConfig() {
        return SecurityConfig.builder()
                .securePathWithCorsConfig(
                        "/*",
                        CORSConfig.builder()
                                .allowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE"))
                                .allowOrigin("http://127.0.0.1:5500")
                                .allowAllHeaders(true)
                                .build()
                )
                .authenticationStrategy(AuthenticationStrategy.JWT)
                .securePathWithRole(new EndpointMapping("/*", List.of(EndpointMapping.RequestMethodMatcher.ALL)), List.of(Role.any()))
                .jwtSecretExpiryTime(Duration.ofHours(10).toMillis())
                .jwtSecretKey(System.getenv("JWT_SECRET"))
                .build();
    }

}
```

A full sample application can be found in [this repository](https://github.com/john-amiscaray/QuakExample).

## Quak Modules Overview

Quak Framework is split into different modules depending on the needs of your application. The following is a table of each of the modules and their usage:

| Module                                                       | Usage                                                                                                                                                                                                                                                                                                                                                                                  |
|--------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [quak.framework.core](https://github.com/john-amiscaray/QuakFramework/wiki/Quak-Core)               | Contains the core Quak functionality. Includes dependency injection, application lifecycle hooks, and application configuration.                                                                                                                                                                                                                                                       |
| [quak.framework.web](https://github.com/john-amiscaray/QuakFramework/wiki/Quak-Web)                 | Contains web functionality. Includes annotation-based REST controllers, function-based REST controllers, request filtering functionality, and exception status code mapping.                                                                                                                                                                                                           |
| quak.framework.web-model                                     | Contains model classes for HTTP requests, responses, and status codes.                                                                                                                                                                                                                                                                                                                 |
| [quak.framework.data](https://github.com/john-amiscaray/QuakFramework/wiki/Quak-Data)               | Contains database access functionality.                                                                                                                                                                                                                                                                                                                                                |
| [quak.framework.security](https://github.com/john-amiscaray/QuakFramework/wiki/Quak-Security)       | Contains security functionality. Includes HTTP authorization, authentication, user principal management, and CORS configuration.                                                                                                                                                                                                                                                       |
| [quak.framework.generator](https://github.com/john-amiscaray/QuakFramework/wiki/Quak-Generator-API) | A maven plugin for web controller and module-info generation. Before compilation, scans the project for classes with annotations from the `quak.framework.generator-model` module. These annotations give information to allow this plugin to generate controllers. For module-info generation, automatically adds required declarations for dependency injection and web controllers. |
| quak.framework.generator-model                               | Annotations for project classes and methods to give information for the quak.framework.generator maven plugin.                                                                                                                                                                                                                                                                         |

View the [javadocs](https://john-amiscaray.github.io/QuakFramework/) for more info.

## Installation

> NOTE: Currently, Quak has only been released as a 1.0-SNAPSHOT version hosted via GitHub packages. When Quak has a full non-snapshot release, it will be hosted on maven central. Because Quak is only available on GitHub packages, you'll need extra setup beyond adding dependencies to your project's pom.xml.

You can use the [Quak CLI](https://github.com/john-amiscaray/QuakFramework/wiki/The-Quak-CLI) to set up most of the project for you (except for step three below where you need to insert your GitHub username and personal access token manually). Otherwise, follow the steps below:

1. Add the appropriate Quak modules as maven dependencies in your project's `pom.xml`. See the [project GitHub packages](https://github.com/john-amiscaray/QuakFramework/packages) for more details.
2. In your `pom.xml`, add the following repositories:
```xml
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
```
3. In your GitHub developer settings, generate a classic [personal access token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens) with at least the `read:packages` permission enabled.
4. In your maven settings file (in your `~/.m2/settings.xml` file) add the following to authenticate with GitHub packages:
```xml
<servers>
    <server>
        <id>github</id>
        <username>Your GitHub Username</username>
        <password>Your GitHub Personal Access Token</password>
    </server>
</servers>
```
Be sure to update your Maven settings in IntelliJ as follows:

![IntelliJ Maven Settings](assets/user-guide/mvn-settings.png)

## Example Code

For an example of how you might use Quak, check out [this repository](https://github.com/john-amiscaray/QuakExample).
