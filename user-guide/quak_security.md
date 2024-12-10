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
