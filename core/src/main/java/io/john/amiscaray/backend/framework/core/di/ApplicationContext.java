package io.john.amiscaray.backend.framework.core.di;

import io.john.amiscaray.backend.framework.core.di.dependency.DependencyID;
import io.john.amiscaray.backend.framework.core.di.exception.DependencyInstantiationException;
import io.john.amiscaray.backend.framework.core.di.exception.DependencyResolutionException;
import io.john.amiscaray.backend.framework.core.di.exception.ProviderMissingConstructorException;
import io.john.amiscaray.backend.framework.core.di.provider.*;
import io.john.amiscaray.backend.framework.core.di.provider.annotation.*;
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
                .map(this::getProviderConstructor)
                .map(ConstructorDependencyProvider::new)
                .toList());
        dependencyProviders.addAll(providers.stream()
                .flatMap(provider -> Stream.of(provider.getMethods()))
                .filter(method -> method.isAnnotationPresent(Provide.class))
                .map(MethodDependencyProvider::new)
                .toList()
        );
        dependencyProviders.addAll(managedInstances.stream().map(this::getManagedInstanceConstructor).map(ConstructorDependencyProvider::new).toList());
        dependencyProviders.addAll(ServiceLoader.load(DependencyProvider.class).stream().map(ServiceLoader.Provider::get).map(provider -> (DependencyProvider<?>) provider).toList());

        buildDependencyGraph(dependencyProviders);
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

    public boolean hasInstance(DependencyID<?> dependencyID) {
        return instances.containsKey(dependencyID);
    }

    public <T> T getInstance(DependencyID<T> dependencyID) {
        return (T) instances.get(dependencyID);
    }

    public <T> List<T> getAggregateDependencies(String aggregateListName, Class<T> dependencyType) {
        return aggregateDependencies.get(new DependencyID<>(aggregateListName, dependencyType));
    }
}
