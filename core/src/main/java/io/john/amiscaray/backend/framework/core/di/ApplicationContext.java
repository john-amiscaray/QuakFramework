package io.john.amiscaray.backend.framework.core.di;

import io.john.amiscaray.backend.framework.core.di.dependency.Dependency;
import io.john.amiscaray.backend.framework.core.di.exception.ContextInitializationException;
import io.john.amiscaray.backend.framework.core.di.exception.DependencyResolutionException;
import io.john.amiscaray.backend.framework.core.di.exception.DependencyInstantiationException;
import io.john.amiscaray.backend.framework.core.di.exception.ProviderMissingConstructorException;
import io.john.amiscaray.backend.framework.core.di.provider.*;
import org.apache.commons.lang3.ClassUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationContext {

    private static ApplicationContext applicationContextInstance;
    private Map<Dependency<?>, Object> instances;
    private String classScanPackage;

    private ApplicationContext() {
    }

    public void init(String classScanPackage) throws InvocationTargetException, DependencyInstantiationException, IllegalAccessException {
        this.classScanPackage = classScanPackage;
        instances = new HashMap<>();
        var reflections = new Reflections(classScanPackage, Scanners.TypesAnnotated);
        var providers = reflections.getTypesAnnotatedWith(Provider.class)
                .stream()
                .toList();
        var managedInstances = reflections.getTypesAnnotatedWith(ManagedType.class)
                .stream()
                .toList();
        var executablesToCall = Stream.concat(Stream.concat(
                providers.stream().flatMap(provider -> Stream.of(provider.getMethods())).filter(method -> method.isAnnotationPresent(Provide.class)),
                providers.stream().map(this::getProviderConstructor)
        ), managedInstances.stream().map(this::getManagedInstanceConstructor));
        buildDependencyGraph(executablesToCall.collect(Collectors.toList()));
    }

    private Constructor<?> getManagedInstanceConstructor(Class<?> managedInstanceClass) {
        var constructors = managedInstanceClass.getDeclaredConstructors();
        if (Arrays.stream(constructors).anyMatch(constructor -> constructor.isAnnotationPresent(Instantiate.class))) {
            return Arrays.stream(constructors).filter(constructor -> constructor.isAnnotationPresent(Instantiate.class))
                    .findFirst().orElseThrow();
        }

        return Arrays.stream(constructors)
                .filter(constructor -> constructor.getParameters().length == 0)
                .findFirst().orElseThrow(() -> new ProviderMissingConstructorException(managedInstanceClass));
    }

    private Constructor<?> getProviderConstructor(Class<?> providerClass) {
        var constructors = providerClass.getConstructors();
        if (Arrays.stream(constructors).anyMatch(constructor -> constructor.isAnnotationPresent(Provide.class))) {
            return Arrays.stream(constructors).filter(constructor -> constructor.isAnnotationPresent(Provide.class))
                    .findFirst().orElseThrow();
        }

        return Arrays.stream(constructors)
                .filter(constructor -> constructor.getParameters().length == 0)
                .findFirst().orElseThrow(() -> new ProviderMissingConstructorException(providerClass));
    }

    private void buildDependencyGraph(List<Executable> executables) {
        var canSatisfyDependencies = false;
        Predicate<Executable> executableHasItsDependenciesSatisfied = executable -> {
           var allParametersSatisfied = Arrays.stream(executable.getParameters())
                .allMatch(parameter -> {
                    var parameterDependencyName = getParameterDependencyName(parameter);
                    return instances.keySet()
                            .stream()
                            .anyMatch(key -> {
                                var couldDependencyBeUsed = key.type().equals(ClassUtils.primitiveToWrapper(parameter.getType()));
                                if (!parameterDependencyName.isEmpty()) {
                                    couldDependencyBeUsed &= key.name().equals(parameterDependencyName);
                                }
                                return couldDependencyBeUsed;
                            });
                });
           var ifMethodThenDeclaringClassIsSatisfied = executable instanceof Constructor<?> || hasInstance(executable.getDeclaringClass());
           return allParametersSatisfied && ifMethodThenDeclaringClassIsSatisfied;
        };
        do {
            executables.stream().filter(executableHasItsDependenciesSatisfied)
                    .findFirst()
                    .ifPresent(executable -> {
                        var returnType = ClassUtils.primitiveToWrapper((Class<?>) executable.getAnnotatedReturnType().getType());
                        try {
                            var returnedInstance = switch (executable) {
                                case Method method -> {
                                    if (method.getParameters().length == 0) {
                                        yield method.invoke(getInstance(method.getDeclaringClass()));
                                    }
                                    yield method.invoke(getInstance(method.getDeclaringClass()), fetchInstancesOfParameters(method.getParameters()));
                                }
                                case Constructor<?> constructor -> {
                                    if (constructor.getParameters().length == 0) {
                                        yield constructor.newInstance();
                                    }
                                    yield constructor.newInstance(fetchInstancesOfParameters(constructor.getParameters()));
                                }
                            };
                            var dependencyName = executable.isAnnotationPresent(Provide.class) ?
                                    executable.getAnnotation(Provide.class).dependencyName() :
                                    "";
                            if (dependencyName.isEmpty()) {
                                dependencyName = executable.getName();
                            }
                            instances.put(new Dependency<>(dependencyName, returnType), returnedInstance);
                        } catch (InvocationTargetException | IllegalAccessException |
                                 InstantiationException e) {
                            throw new ContextInitializationException(e);
                        }
                        executables.remove(executable);
                    });
            canSatisfyDependencies = executables.stream()
                    .anyMatch(executableHasItsDependenciesSatisfied);
        } while (canSatisfyDependencies);

        if (!executables.isEmpty()) {
            var missingDependencies = new HashSet<Class<?>>();
            for (var executable : executables) {
                missingDependencies.addAll(Arrays.stream(executable.getParameters())
                        .map(Parameter::getType)
                        .filter(type -> !hasInstance(type)).toList());
                if (executable instanceof Method method && !hasInstance(method.getDeclaringClass())) {
                    missingDependencies.add(method.getDeclaringClass());
                }
            }
            throw new DependencyResolutionException(missingDependencies);
        }
    }

    private String getParameterDependencyName(Parameter parameter) {
        if (parameter.isAnnotationPresent(ProvidedWith.class)) {
            return parameter.getAnnotation(ProvidedWith.class).dependencyName();
        }
        return "";
    }

    public Object[] fetchInstancesOfParameters(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(parameter -> {
                    var type = ClassUtils.primitiveToWrapper(parameter.getType());
                    if (parameter.isAnnotationPresent(ProvidedWith.class)) {
                        var dependencyName = parameter.getAnnotation(ProvidedWith.class).dependencyName();
                        if (!dependencyName.isEmpty()) {
                            return getInstance(new Dependency<>(dependencyName, type));
                        }
                    }
                    return getInstance(type);
                })
                .filter(Objects::nonNull)
                .toArray(Object[]::new);
    }

    public static ApplicationContext getInstance() {
        if (applicationContextInstance == null) {
            applicationContextInstance = new ApplicationContext();
        }
        return applicationContextInstance;
    }

    public boolean hasInstance(Class<?> clazz) {
        return instances.entrySet()
                .stream()
                .filter(entry -> entry.getKey().type().equals(clazz))
                .map(Map.Entry::getValue)
                .findFirst()
                .isPresent();
    }

    public <T> T getInstance(Class<T> clazz) {
        if (!hasInstance(clazz)) {
            try {
                var clazzConstructorForInstantiation = getManagedInstanceConstructor(clazz);
                if (clazzConstructorForInstantiation.getParameters().length == 0) {
                    return (T) clazzConstructorForInstantiation.newInstance();
                } else {
                    return (T) clazzConstructorForInstantiation.newInstance(fetchInstancesOfParameters(clazzConstructorForInstantiation.getParameters()));
                }
            } catch (InvocationTargetException | DependencyInstantiationException | IllegalAccessException | java.lang.InstantiationException e) {
                throw new DependencyInstantiationException(clazz, e);
            }
        }
        return (T) instances.entrySet()
                .stream()
                .filter(entry -> entry.getKey().type().equals(clazz))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseThrow();
    }

    public boolean hasInstance(Dependency<?> dependency) {
        return instances.containsKey(dependency);
    }

    public <T> T getInstance(Dependency<T> dependency) {
        return (T) instances.get(dependency);
    }
}
