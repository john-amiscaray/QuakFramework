package io.john.amiscaray.backend.framework.core.di;

import io.john.amiscaray.backend.framework.core.di.exception.ContextInitializationException;
import io.john.amiscaray.backend.framework.core.di.exception.DependencyResolutionException;
import io.john.amiscaray.backend.framework.core.di.exception.ProviderMissingConstructorException;
import io.john.amiscaray.backend.framework.core.di.provider.Provide;
import io.john.amiscaray.backend.framework.core.di.provider.Provider;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApplicationContext {

    private static ApplicationContext applicationContextInstance;
    private Map<Class<?>, Object> instances;
    private String classScanPackage;

    private ApplicationContext() {
    }

    public void init(String classScanPackage) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        this.classScanPackage = classScanPackage;
        instances = new HashMap<>();
        var reflections = new Reflections(classScanPackage, Scanners.TypesAnnotated);
        var providers = reflections.getTypesAnnotatedWith(Provider.class)
                .stream()
                .toList();
        var executablesToCall = Stream.concat(
                providers.stream().flatMap(provider -> Stream.of(provider.getMethods())).filter(method -> method.isAnnotationPresent(Provide.class)),
                providers.stream().map(this::getProviderConstructor)
        );
        buildDependencyGraph(executablesToCall.collect(Collectors.toList()));
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
                .allMatch(parameter -> instances.containsKey(parameter.getType()));
           var ifMethodThenDeclaringClassIsSatisfied = executable instanceof Constructor<?> || instances.containsKey(executable.getDeclaringClass());
           return allParametersSatisfied && ifMethodThenDeclaringClassIsSatisfied;
        };
        do {
            executables.stream().filter(executableHasItsDependenciesSatisfied)
                    .findFirst()
                    .ifPresent(executable -> {
                        var typeName = executable.getAnnotatedReturnType().getType().getTypeName();
                        try {
                            var returnedInstance = switch (executable) {
                                case Method method -> {
                                    if (method.getParameters().length == 0) {
                                        yield method.invoke(instances.get(method.getDeclaringClass()));
                                    }
                                    yield method.invoke(instances.get(method.getDeclaringClass()), fetchInstancesOfParameters(method.getParameters()));
                                }
                                case Constructor<?> constructor -> {
                                    if (constructor.getParameters().length == 0) {
                                        yield constructor.newInstance();
                                    }
                                    yield constructor.newInstance(fetchInstancesOfParameters(constructor.getParameters()));
                                }
                            };

                            instances.put(Class.forName(typeName), returnedInstance);
                        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException |
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
                        .filter(type -> !instances.containsKey(type)).toList());
                if (executable instanceof Method method && !instances.containsKey(method.getDeclaringClass())) {
                    missingDependencies.add(method.getDeclaringClass());
                }
            }
            throw new DependencyResolutionException(missingDependencies);
        }
    }

    private Object[] fetchInstancesOfParameters(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(Parameter::getType)
                .map(clazz -> instances.get(clazz))
                .filter(Objects::nonNull)
                .toArray(Object[]::new);
    }

    public static ApplicationContext getInstance() {
        if (applicationContextInstance == null) {
            applicationContextInstance = new ApplicationContext();
        }
        return applicationContextInstance;
    }

    public <T> T getInstance(Class<T> clazz) {
        return (T) instances.get(clazz);
    }

}
