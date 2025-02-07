package io.john.amiscaray.quak.core.di;

import io.john.amiscaray.quak.core.di.dependency.DependencyID;
import io.john.amiscaray.quak.core.di.exception.DependencyInstantiationException;
import io.john.amiscaray.quak.core.di.exception.DependencyResolutionException;
import io.john.amiscaray.quak.core.di.exception.AccessibleConstructorException;
import io.john.amiscaray.quak.core.di.provider.ConstructorDependencyProvider;
import io.john.amiscaray.quak.core.di.provider.DependencyProvider;
import io.john.amiscaray.quak.core.di.provider.MethodDependencyProvider;
import io.john.amiscaray.quak.core.di.provider.annotation.*;
import lombok.Getter;
import org.apache.commons.lang3.ClassUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Stores all the project's dependencies as a map with {@link io.john.amiscaray.quak.core.di.dependency.DependencyID} as the key and the instance as the value. Also stores lists of grouped dependencies as "aggregate lists" (see the {@link io.john.amiscaray.quak.core.di.provider.annotation.AggregateTo AggregateTo} annotation for more details).
 */
public class ApplicationContext {

    private static ApplicationContext applicationContextInstance;
    private static final Logger LOG = LoggerFactory.getLogger(ApplicationContext.class);
    private Map<DependencyID<?>, Object> instances;
    private Map<DependencyID<?>, List> aggregateDependencies;
    @Getter
    private String classScanPackage;

    private ApplicationContext() {
    }

    public void init(String classScanPackage) throws InvocationTargetException, DependencyInstantiationException, IllegalAccessException {
        this.classScanPackage = classScanPackage;
        instances = new HashMap<>();
        aggregateDependencies = new HashMap<>();
        var reflections = new Reflections(classScanPackage, Scanners.TypesAnnotated);
        var providers = reflections.getTypesAnnotatedWith(Provider.class)
                .stream()
                .toList();
        var managedInstances = reflections.getTypesAnnotatedWith(ManagedType.class)
                .stream()
                .toList();
        var dependencyProviders = new ArrayList<DependencyProvider<?>>(providers.stream()
                .map(this::getConstructorMarkedForInstantiation)
                .map(ConstructorDependencyProvider::new)
                .toList());
        dependencyProviders.addAll(providers.stream()
                .flatMap(provider -> Stream.of(provider.getMethods()))
                .filter(method -> method.isAnnotationPresent(Provide.class))
                .map(MethodDependencyProvider::new)
                .toList()
        );
        dependencyProviders.addAll(managedInstances.stream().map(this::getConstructorMarkedForInstantiation).map(ConstructorDependencyProvider::new).toList());
        dependencyProviders.addAll(ServiceLoader.load(DependencyProvider.class).stream().map(ServiceLoader.Provider::get).map(provider -> (DependencyProvider<?>) provider).toList());

        buildDependencyGraph(dependencyProviders);
    }

    private Constructor<?> getConstructorMarkedForInstantiation(Class<?> classToInstantiate) {
        var constructors = classToInstantiate.getDeclaredConstructors();
        if (Arrays.stream(constructors).anyMatch(constructor -> constructor.isAnnotationPresent(Instantiate.class))) {
            return Arrays.stream(constructors).filter(constructor -> constructor.isAnnotationPresent(Instantiate.class))
                    .findFirst().orElseThrow();
        }

        return Arrays.stream(constructors)
                .filter(constructor -> constructor.getParameters().length == 0)
                .findFirst().orElseThrow(() -> new AccessibleConstructorException(classToInstantiate));
    }

    private void buildDependencyGraph(List<DependencyProvider<?>> dependencyProviders) {
        var canSatisfyDependencies = false;
        Predicate<DependencyProvider<?>> providerHasItsDependenciesSatisfied = provider -> {
            List<DependencyID<?>> dependencies = provider.getDependencies();
            return dependencies.stream()
                    .allMatch(dependency -> dependency.name().isEmpty() ? hasInstance(dependency.type()) : hasInstance(dependency));
        };
        do {
            dependencyProviders.stream().filter(providerHasItsDependenciesSatisfied)
                    .findFirst()
                    .ifPresent(provider -> {
                        var providedDependency = provider.provideDependency(this);
                        var id = providedDependency.id();
                        if (provider.aggregateList() != null && !provider.aggregateList().isEmpty()) {
                            var aggregateID = new DependencyID<>(
                                    provider.aggregateList(),
                                    providedDependency.id().type()
                            );
                            if (aggregateDependencies.containsKey(aggregateID)) {
                                aggregateDependencies.get(aggregateID)
                                        .add(providedDependency.instance());
                            } else {
                                aggregateDependencies.put(aggregateID, new ArrayList<>(List.of(providedDependency.instance())));
                            }
                        }
                        instances.put(id, providedDependency.instance());
                        dependencyProviders.remove(provider);
                    });
            canSatisfyDependencies = dependencyProviders.stream()
                    .anyMatch(providerHasItsDependenciesSatisfied);
        } while (canSatisfyDependencies);

        if (!dependencyProviders.isEmpty() && !dependencyProviders.stream().allMatch(DependencyProvider::isDependencyOptional)) {
            var missingDependencies = new HashSet<Class<?>>();
            for (var provider : dependencyProviders) {
                if (provider.isDependencyOptional()) {
                    continue;
                }
                missingDependencies.addAll(provider.getDependencies().stream()
                        .map(DependencyID::type)
                        .filter(type -> !hasInstance(type)).toList());
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

    /**
     * Fetches instances of the given parameters. Used to call methods reflectively.
     * @param parameters The parameters.
     * @return The instances of the parameters to use.
     */
    public Object[] fetchInstancesOfParameters(Parameter[] parameters) {
        return Arrays.stream(parameters)
                .map(parameter -> {
                    var type = ClassUtils.primitiveToWrapper(parameter.getType());
                    if (parameter.isAnnotationPresent(ProvidedWith.class)) {
                        var dependencyName = parameter.getAnnotation(ProvidedWith.class).dependencyName();
                        if (!dependencyName.isEmpty()) {
                            return getInstance(new DependencyID<>(dependencyName, type));
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

    /**
     * Check if the application context contains an instance of the given class.
     * @param clazz The class
     * @return A boolean representing if the application context contains an instance of the given class.
     */
    public boolean hasInstance(Class<?> clazz) {
        return instances.entrySet()
                .stream()
                .filter(entry -> entry.getKey().type().equals(clazz))
                .map(Map.Entry::getValue)
                .findFirst()
                .isPresent();
    }

    /**
     * Gets an instance from the application context.
     * @param clazz The class
     * @return An instance of the class
     * @param <T> A generic parameter matching the type of the instance
     */
    public <T> T getInstance(Class<T> clazz) {
        if (!hasInstance(clazz)) {
            try {
                var clazzConstructorForInstantiation = getConstructorMarkedForInstantiation(clazz);
                var instance = clazzConstructorForInstantiation.getParameters().length == 0 ?
                        (T) clazzConstructorForInstantiation.newInstance() :
                        (T) clazzConstructorForInstantiation.newInstance(fetchInstancesOfParameters(clazzConstructorForInstantiation.getParameters()));
                instances.put(new DependencyID<>(clazz), instance);
                return instance;
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

    /**
     * Checks if the application context contains an instance associated with the given dependency ID
     * @param dependencyID The dependency ID
     * @return A boolean representing if the application context contains an instance associated with the dependency ID
     */
    public boolean hasInstance(DependencyID<?> dependencyID) {
        return instances.containsKey(dependencyID);
    }

    /**
     * Gets an instance from the application context.
     * @param dependencyID The dependency ID of the instance to retrieve
     * @return An instance of the class
     * @param <T> A generic parameter matching the type of the instance
     */
    public <T> T getInstance(DependencyID<T> dependencyID) {
        return (T) instances.get(dependencyID);
    }

    /**
     * Fetches a list of dependencies aggregated into a named list.
     * @param aggregateListName The name of the list of dependencies.
     * @param dependencyType The type of the dependencies.
     * @return The list of the dependencies.
     * @param <T> The type of the dependencies.
     */
    public <T> List<T> getAggregateDependencies(String aggregateListName, Class<T> dependencyType) {
        return aggregateDependencies.get(new DependencyID<>(aggregateListName, dependencyType));
    }
}
