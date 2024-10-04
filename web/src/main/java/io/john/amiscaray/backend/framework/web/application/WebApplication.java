package io.john.amiscaray.backend.framework.web.application;

import io.john.amiscaray.backend.framework.core.Application;
import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.core.properties.ApplicationProperty;
import io.john.amiscaray.backend.framework.security.di.SecurityDependencyIDs;
import io.john.amiscaray.backend.framework.web.controller.PathController;
import io.john.amiscaray.backend.framework.web.filter.annotation.ApplicationFilter;
import io.john.amiscaray.backend.framework.web.filter.exception.InvalidApplicationFilterException;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMapping;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import io.john.amiscaray.backend.framework.web.servlet.HttpController;
import io.john.amiscaray.backend.framework.web.servlet.HttpControllerGroup;
import jakarta.servlet.Filter;
import lombok.Builder;
import lombok.Singular;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
import org.javatuples.Pair;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class WebApplication extends Application {
    protected Tomcat server;
    protected Context servletContext;
    private final ApplicationContext applicationContext = ApplicationContext.getInstance();
    private final Map<RequestMapping, PathController<?, ?>> pathControllers = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(WebApplication.class);
    private static WebApplication instance = null;

    public record Configuration(Class<?> main, String[] args, Map<RequestMapping, PathController<?, ?>> pathControllers) {
        @Builder
        public Configuration(Class<?> main, String[] args, @Singular("pathMapping") Map<RequestMapping, PathController<?, ?>> pathControllers) {
            this.main = main;
            this.args = args;
            this.pathControllers = pathControllers;
        }
    }

    public static WebApplication getInstance() {
        if (instance == null) {
            instance = new WebApplication();
        }
        return instance;
    }

    private WebApplication() {
        super(WebApplication.class, new String[]{});
    }

    public void init(Configuration config) {
        main = config.main;
        classScanPackage = main.getPackageName();
        args = config.args;
        pathControllers.putAll(config.pathControllers);
    }

    @Override
    public void startUp() throws Exception {
        if (LOG.isInfoEnabled()) {
            LOG.info("START APPLICATION");
        }
        server = new Tomcat();
        server.setBaseDir(ApplicationProperty.SERVER_DIRECTORY.getValue());

        var connector1 = server.getConnector();
        connector1.setPort(Integer.parseInt(ApplicationProperty.PORT.getValue()));

        var docBase = new File(ApplicationProperty.DOCUMENT_BASE.getValue()).getAbsolutePath();

        servletContext = server.addContext(ApplicationProperty.CONTEXT_PATH.getValue(), docBase);

        registerServlets();
        registerFilters();
        addAuthenticationFilter();

        server.start();
        server.getService().addConnector(connector1);
        hasStarted = true;
        server.getServer().await();
    }

    public void finish() throws LifecycleException {
        server.stop();
        server.destroy();
    }

    private void registerServlets() {
        if (LOG.isInfoEnabled()) {
            LOG.info("REGISTERING SERVLETS");
        }
        var urlToPathControllersMapping = new HashMap<String, List<Pair<RequestMethod, PathController<?, ?>>>>();
        for (var entry : pathControllers.entrySet()) {
            Pair<RequestMethod, PathController<?, ?>> methodToControllerMapping = Pair.with(entry.getKey().method(), entry.getValue());
            if (urlToPathControllersMapping.containsKey(entry.getKey().url())) {
                urlToPathControllersMapping.get(entry.getKey().url()).add(methodToControllerMapping);
            } else {
                urlToPathControllersMapping.put(entry.getKey().url(), new ArrayList<>(List.of(methodToControllerMapping)));
            }
        }
        var urlToHttpControllerMapping = new HashMap<String, HttpController>();
        for (var entry : urlToPathControllersMapping.entrySet()) {
            var url = entry.getKey();
            var controller = new HttpController(
                    url,
                    entry.getValue()
                    .stream()
                    .collect(Collectors.toMap(
                            Pair::getValue0,
                            Pair::getValue1
                    )));
            urlToHttpControllerMapping.put(url, controller);
        }
        // TODO rewrite this algorithm. First sort the entries by the shortest url patterns based on number of URL parts there are. Then, look for paths starting with the current path, group them in controller groups and pop them.
        var controllersToAdd = new ArrayList<>(urlToHttpControllerMapping.entrySet());
        controllersToAdd.sort(Comparator.comparingInt(entry -> getNumPaths(entry.getKey())));
        while (!controllersToAdd.isEmpty()) {
            var currentControllerMapping = controllersToAdd.get(0);
            var controllersToGroup = new HashMap<String, HttpController>();
            if (!currentControllerMapping.getKey().equals("/")) { // root URL should be handled on its own
                for (var controllerMapping : controllersToAdd) {
                    if (controllerMapping.equals(currentControllerMapping)) {
                        continue;
                    }
                    if (controllerMapping.getKey().startsWith(currentControllerMapping.getKey())) {
                        controllersToGroup.put(controllerMapping.getKey(), controllerMapping.getValue());
                    }
                }
            }
            if (controllersToGroup.isEmpty()) {
                var controller = new HttpControllerGroup(Map.ofEntries(currentControllerMapping));
                var url = cleanURLPath(currentControllerMapping.getKey());
                server.addServlet(ApplicationProperty.CONTEXT_PATH.getValue(), controller.toString(), controller);
                servletContext.addServletMappingDecoded(url, controller.toString());
                controllersToAdd.remove(currentControllerMapping);
            } else {
                controllersToGroup.put(currentControllerMapping.getKey(), currentControllerMapping.getValue());
                var httpController = new HttpControllerGroup(controllersToGroup);
                server.addServlet(ApplicationProperty.CONTEXT_PATH.getValue(), httpController.toString(), httpController);
                servletContext.addServletMappingDecoded(currentControllerMapping.getKey() + "/*", httpController.toString());
                controllersToAdd.removeAll(controllersToGroup.entrySet());
            }
        }
    }

    private void addCORSFilter() {

    }

    private void addAuthenticationFilter() {
        var securityFilterDependencyID = SecurityDependencyIDs.SECURITY_FILTER_DEPENDENCY;
        var securityFilter = applicationContext.getInstance(securityFilterDependencyID);
        var securityConfig = applicationContext.getInstance(SecurityDependencyIDs.SECURITY_CONFIG_DEPENDENCY);
        if (securityFilter == null || securityConfig == null) {
            return;
        }

        var filterDef = new FilterDef();
        filterDef.setFilterName(securityFilterDependencyID.name());
        filterDef.setFilter(securityFilter);

        servletContext.addFilterDef(filterDef);

        var filterMap = new FilterMap();
        filterMap.setFilterName(securityFilterDependencyID.name());
        for (var securityPathMapping : securityConfig.securedEndpointRoles().entrySet()) {
            filterMap.addURLPattern(securityPathMapping.getKey().url());
        }

        servletContext.addFilterMap(filterMap);
    }

    private void registerFilters() {
        if (LOG.isInfoEnabled()) {
            LOG.info("REGISTERING FILTERS");
        }

        var reflections = new Reflections(classScanPackage, Scanners.TypesAnnotated);
        var applicationFilters = reflections.getTypesAnnotatedWith(ApplicationFilter.class)
                .stream()
                .sorted(Comparator.comparingInt(type -> type.getAnnotation(ApplicationFilter.class).priority()))
                .toList();

        for(var filterClass : applicationFilters) {
            if (!Filter.class.isAssignableFrom(filterClass)) {
                throw new InvalidApplicationFilterException(filterClass, "Filter must implement jakarta.servlet.Filter.");
            }
            var filterInstance = (Filter) applicationContext.getInstance(filterClass);
            var filterMetaData = filterClass.getAnnotation(ApplicationFilter.class);
            var filterName = filterMetaData.name().isEmpty() ? filterClass.getName() : filterMetaData.name();

            var filterDef = new FilterDef();
            filterDef.setFilterName(filterName);
            filterDef.setFilter(filterInstance);

            servletContext.addFilterDef(filterDef);

            var filterMap = new FilterMap();
            filterMap.setFilterName(filterName);
            for (var urlPattern : filterMetaData.urlPatterns()) {
                filterMap.addURLPattern(urlPattern);
            }

            servletContext.addFilterMap(filterMap);
        }
    }

    protected void addPathMappings(Map<RequestMapping, PathController<?, ?>> pathMappings) {
        pathControllers.putAll(pathMappings);
    }

    private int getNumPaths(String url) {
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url.split("/").length;
    }

    private String cleanURLPath(String urlPath) {
        var cleaned = urlPath.replaceAll("\\{.+}", "*");
        if (cleaned.equals("/")) {
            return "";
        }
        return cleaned;
    }

}
