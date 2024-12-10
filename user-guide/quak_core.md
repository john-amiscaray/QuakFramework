# Quak Core

## Application Lifecycle Hooks

Quak represents an application using the `io.john.amiscaray.quak.core.Application` class. This class includes functionality for application lifecycle hooks. Different phases of the application lifecycle are represented using the `LifecycleState` enum defined in the `Application` class. Callbacks can be added for each of these lifecycle states using the `Application#on` method. The following are the application lifecycle states and their meanings:

- `PRE_START` : Before the application starts.
- `CONTEXT_LOADED` : After the application context loads (see the section below on dependency injection).
- `POST_START` : After the application starts.
- `PRE_STOP` : Before the application stops.
- `POST_STOP` : After the application stops.

## Dependency Injection

Quak framework has a robust API for dependency injection. Quak implements dependency injection using a combination of constructor-based dependency injection, annotation-based dependency providers, JPMS service loading, and a application singleton holding the dependencies.

### Dependencies

Dependencies in Quak are internally represented using the `io.john.amiscaray.quak.core.di.dependency.ProvidedDependency` record. This contains the provided instance and a `io.john.amiscaray.quak.core.di.dependency.DependencyID`. The dependency ID contains a type for the instance and an optional String identifier in case you need multiple dependencies of the same type.

### ApplicationContext

`io.john.amiscaray.quak.core.di.ApplicationContext` is an application singleton used to access dependencies. It contains methods for testing for dependencies and retrieving them by either their type or their dependency ID. Additionally, it has functionality for aggregating dependencies into a list.

Quak resolves the application context at the start of your application's lifecycle. This means that it goes through all the dependencies and attempts to instantiate all of them. Quak provides an application lifecycle hook for when the application context is loaded called `CONTEXT_LOADED`. If Quak fails to instantiate any required dependencies, the application will fail.

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
