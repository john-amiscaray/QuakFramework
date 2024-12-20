# Quak Security

Quak offers a security module to allow you to secure your web application. The core of the security module is a group of interfaces you can use to define how you will manage user authentications and principal management. Aside from that, the module comes with configuration classes you can pass to the application context to configure you application security.

## User Authentication and Principal Management

To get started with `quak.framework.security`, you first need to implement the `io.john.amiscaray.quak.security.auth.Authenticator` interface. Using this interface, you define how your Quak application looks up user principles to authenticate them. This section will cover how to implement this interface and how to define a user principal.

### The Authenticator Interface

The `Authenticator` interface has a default `authenticate` method. This calls an unimplemented `lookupPrincipal` method, checks its return value is not null, then returns a `io.john.amiscaray.quak.security.auth.SimpleAuthentication` object. Thus, all you need to do is implement the `lookupPrincipal` method. This will require you to map a user in your database to a user principal. To do this, however, you need to define what a user principal is.

### The Principal Interface

User principals in `quak.framework.security` are represented using a `io.john.amiscaray.quak.security.auth.principal.Principal` interface which only has a `getSecurityID` method. When you develop your Quak application, you will have to map a user in your database to an implementation of this class.

### Registering Your Authenticator

Once you implement the `Authenticator` interface, you must add it to the application context. This `Authenticator` implementation should have a dependency name defined in the `io.john.amiscaray.quak.security.di.SecurityDependencyIDs.AUTHENTICATOR_DEPENDENCY` constant.

## Configuring an Authentication Strategy

Now that you have your `Authenticator` implemented and configured, you can configure an authentication strategy to configure how users will authenticate with your web API. This is done by adding a `io.john.amiscaray.quak.security.config.SecurityConfig` instance to the application context. This dependency should have a name defined in the `io.john.amiscaray.quak.security.di.SecurityDependencyIDs.SECURITY_CONFIG_DEPENDENCY_NAME` constant. This instance contains a `AuthenticationStrategy` field which is an enum containing the different types of authentication that Quak supports. Currently, this extends only to HTTP basic and JWT based authentication. For JWT authentication and authorization, Quak offers a `io.john.amiscaray.quak.security.auth.jwt.JwtUtil` class for creating and managing tokens.

## Configuring Endpoint Roles

To enforce authentication for URL paths, you can configure roles for accessing for your endpoints. You can do this using the `SecurityConfig`'s `securedEndpointRoles` map. This map has `io.john.amiscaray.quak.security.config.EndpointMapping` instances as keys and a list of `io.john.amiscaray.quak.security.auth.principal.role.Role` implementations as values. If you wish to secure an endpoint for any role or want to opt out of using roles, you can pass a list with a single role of `Role.any()`.

## Configuring CORS

To configure CORS for your URL paths, you can use the `SecurityConfig`'s `pathCorsConfigMap` map. This map has URL paths as keys and `io.john.amiscaray.quak.security.config.CORSConfig` instances as values.

## Example Setup

### Authenticator

```java
package io.john.amiscaray.test.security;

import io.john.amiscaray.quak.core.di.provider.annotation.ManagedType;
import io.john.amiscaray.quak.security.auth.Authenticator;
import io.john.amiscaray.quak.security.auth.credentials.Credentials;
import io.john.amiscaray.quak.security.auth.principal.Principal;
import io.john.amiscaray.quak.security.auth.principal.RoleAttachedPrincipal;
import io.john.amiscaray.quak.security.auth.principal.role.Role;
import io.john.amiscaray.quak.security.di.SecurityDependencyIDs;
import io.john.amiscaray.test.security.roles.Roles;

import java.time.Duration;
import java.util.Optional;

@ManagedType(dependencyName = SecurityDependencyIDs.AUTHENTICATOR_DEPENDENCY_NAME)
public class SimpleAuthenticator implements Authenticator {

    private static final RoleAttachedPrincipal JOHN = new RoleAttachedPrincipal() {
        @Override
        public Role[] getRoles() {
            return new Role[] { Roles.user() };
        }

        @Override
        public String getSecurityID() {
            return "Johnny Boy";
        }
    };

    private static final RoleAttachedPrincipal ELLI = new RoleAttachedPrincipal() {
        @Override
        public Role[] getRoles() {
            return new Role[] { Roles.admin() };
        }

        @Override
        public String getSecurityID() {
            return "Elli";
        }
    };

    @Override
    public Optional<Principal> lookupPrincipal(String s) {
        if (s.equals(JOHN.getSecurityID())) {
            return Optional.of(JOHN);
        } else if (s.equals(ELLI.getSecurityID())) {
            return Optional.of(ELLI);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Principal> lookupPrincipal(Credentials credentials) {
        if (credentials.getUsername().equals("John") && credentials.getPassword().equals("password")) {
            return Optional.of(JOHN);
        } else if (credentials.getUsername().equals("Elli") && credentials.getPassword().equals("password")) {
            return Optional.of(ELLI);
        }
        return Optional.empty();
    }

    @Override
    public Duration getAuthenticationValidDuration() {
        return Duration.ofDays(30);
    }

}
```

### Roles

```java
package io.john.amiscaray.test.security.roles;

import io.john.amiscaray.quak.security.auth.principal.role.Role;

public class Roles {

    public static Role user() {
        return () -> "USER";
    }

    public static Role admin() {
        return () -> "ADMIN";
    }

}
```

### Security Config

```java
package io.john.amiscaray.test.security.di;


import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.core.di.provider.annotation.Provide;
import io.john.amiscaray.quak.core.di.provider.annotation.ProvidedWith;
import io.john.amiscaray.quak.core.di.provider.annotation.Provider;
import io.john.amiscaray.quak.security.config.CORSConfig;
import io.john.amiscaray.quak.security.config.EndpointMapping;
import io.john.amiscaray.quak.security.config.SecurityConfig;
import io.john.amiscaray.quak.security.di.AuthenticationStrategy;
import io.john.amiscaray.quak.security.di.SecurityDependencyIDs;
import io.john.amiscaray.test.security.roles.Roles;

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
                .securePathWithRole(new EndpointMapping("/studentdto/*", List.of(EndpointMapping.RequestMethodMatcher.ANY_MODIFYING)), List.of(Roles.admin()))
                .securePathWithCorsConfig("/*", CORSConfig.builder()
                        .allowOrigin("http://127.0.0.1:5500")
                        .allowMethod("GET")
                        .build())
                .authenticationStrategy(AuthenticationStrategy.JWT)
                .jwtSecretKey(jwtSecret)
                .jwtSecretExpiryTime(Duration.ofHours(10).toMillis())
                .build();
    }

}
```

### JWT Issuer Controller

```java
package io.john.amiscaray.test.controllers;

import io.john.amiscaray.quak.core.di.provider.annotation.Instantiate;
import io.john.amiscaray.quak.http.request.Request;
import io.john.amiscaray.quak.http.request.RequestMethod;
import io.john.amiscaray.quak.http.response.Response;
import io.john.amiscaray.quak.security.auth.Authenticator;
import io.john.amiscaray.quak.security.auth.credentials.Credentials;
import io.john.amiscaray.quak.security.auth.exception.InvalidCredentialsException;
import io.john.amiscaray.quak.security.auth.jwt.JwtUtil;
import io.john.amiscaray.quak.web.controller.annotation.Controller;
import io.john.amiscaray.quak.web.handler.annotation.Handle;
import io.john.amiscaray.test.models.LoginRequestBody;

@Controller
public class JWTIssuerController {

    private JwtUtil jwtUtil;
    private Authenticator authenticator;

    @Instantiate
    public JWTIssuerController(JwtUtil jwtUtil, Authenticator authenticator) {
        this.jwtUtil = jwtUtil;
        this.authenticator = authenticator;
    }

    @Handle(path="/login", method = RequestMethod.POST)
    public Response<String> login(Request<LoginRequestBody> request) {
        var requestBody = request.body();
        var credentials = new Credentials() {
            @Override
            public String getUsername() {
                return requestBody.username();
            }

            @Override
            public String getPassword() {
                return requestBody.password();
            }
        };

        try {
            var authentication = authenticator.authenticate(credentials);
            var jwt = jwtUtil.generateToken(authentication.getIssuedTo());
            return Response.of(jwt);
        } catch (InvalidCredentialsException e) {
            return new Response<>(401, "Invalid credentials");
        }
    }

}
```