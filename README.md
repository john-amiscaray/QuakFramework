# Quak Framework

## Table Of Contents

- [Overview](#overview)
- [Quak Modules Overview](#quak-modules-overview)
- [Dependency Injection](#dependency-injection)

## Overview

An intuitive Java backend framework to quickly develop REST APIs. Features:
- Dependency injection
- Application lifecycle hooks
- Annotation-based HTTP request handling
- Code generation
- HTTP Authentication and Authorization
- Database operations

## Quak Modules Overview

Quak Framework is split into different modules depending on the needs of your application. The following is a table of each of the modules and their usage:

| Module                         | Usage                                                                                                                                                                                                                                                             |
|--------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| quak.framework.core            | Contains the core Quak functionality. Includes dependency injection, application lifecycle hooks, and application configuration.                                                                                                                                  |
| quak.framework.web             | Contains web functionality. Includes annotation-based REST controllers, function based REST controllers, request filtering functionality, and exception status code mapping.                                                                                      |
| quak.framework.web-model       | Contains model classes for HTTP requests, responses, and status codes.                                                                                                                                                                                            |
| quak.framework.data            | Contains database access functionality.                                                                                                                                                                                                                           |
| quak.framework.security        | Contains security functionality. Includes HTTP authorization, authentication, user principal management, and CORS configuration.                                                                                                                                  |
| quak.framework.generator       | A maven plugin for web controller and module-info generation. Before compilation, scans the project for classes with annotations from the quak.framework.generator-model module. These annotations give information to allow this plugin to generate controllers. |
| quak.framework.generator-model | Annotations for project classes and methods to give information for the quak.framework.generator maven plugin.                                                                                                                                                    |

## Dependency Injection

Quak framework has a robust API for dependency injection. Quak implements dependency injection using a combination of constructor-based dependency injection, annotation-based dependency providers, JPMS service loading, and a application singleton holding the dependencies.

### Dependencies

Dependencies in Quak are internally represented using the `io.john.amiscaray.quak.core.di.dependency.ProvidedDependency` record. This contains the provided instance and a `io.john.amiscaray.quak.core.di.dependency.DependencyID`. The dependency ID contains a type for the instance and an optional String identifier in case you need multiple dependencies of the same type.

### ApplicationContext

`io.john.amiscaray.quak.core.di.ApplicationContext` is an application singleton used to access dependencies. It contains methods for testing for dependencies and retrieving them by either their type or their dependency ID. Additionally, it has functionality for aggregating dependencies into a list.

Quak resolves the application context at the start of your application's lifecycle. This means that it goes through all the dependencies and attempts to instantiate all the dependencies required to start up the application. Quak provides an application lifecycle hook for when the application context is loaded called `CONTEXT_LOADED`.

### The DependencyProvider Interface

Dependencies are provided to Quak using the `io.john.amiscaray.quak.core.di.provider.DependencyProvider` interface. Whether you provide dependencies to the framework using an annotation-based approach or using JPMS service loading, dependency providers will be represented using this interface. This contains methods for retrieving the dependency ID of the dependency we are providing, retrieving the dependency IDs of the dependencies needed to instantiate this dependency, testing whether this dependency is required to start the application, retrieving an identifier of a list to aggregate this dependency to, and retrieving the provided dependency given the application context.

### Providing Dependencies Using Service Loading

Dependencies can be provided to application context using JPMS' service loading functionality. This can be done by implementing the above-mentioned DependencyProvider interface and adding a _provides_ statements in your application's module info.

> Note that your application needs a module-info.java file for Quak to provide some required dependencies for you (ex. quak.framework.data's DatabaseProxy). This can be generated for you using the quak.framework.generator maven plugin.

### Annotation-based Dependency Providers

Dependency providers can be implemented using an annotation-based approach. A dependency provider can be represented by any class annotated with `io.john.amiscaray.quak.core.di.provider.annotation.Provider` these classes can contain methods annotated with `io.john.amiscaray.quak.core.di.provider.annotation.Provide` saying that the return value will be added to the application context. At the beginning of your application's lifecycle, these providers will be used to implement the DependencyProvider interface, get instantiated and added to the application context, then provide additional dependency using these `@Provide` methods.

As alluded to earlier, a dependency provider is also added as a dependency to the application context. Thus, we can use constructor-based dependency injection to instantiate the provider using a constructor annotated with `io.john.amiscaray.quak.core.di.provider.annotation.Instantiate`. Any parameters must first be instantiated into the application context. Similarly, each `@Provide` method can accept arguments for instances in the application context.

### ManagedTypes

Application components that you can instantiate via constructor-based dependency injection are annotated with `io.john.amiscaray.quak.core.di.provider.annotation.ManagedType`. Like the annotation-based providers mentioned above, these can be instantiated via constructor using the `io.john.amiscaray.quak.core.di.provider.annotation.Instantiate` annotation. On application startup, these will be added to the application context.