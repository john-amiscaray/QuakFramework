package io.john.amiscaray.backend.framework.web.application;

import io.john.amiscaray.backend.framework.core.Application;
import io.john.amiscaray.backend.framework.web.controller.PathController;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMapping;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import io.john.amiscaray.backend.framework.web.servlet.HttpController;
import io.john.amiscaray.backend.framework.web.servlet.HttpControllerGroup;
import lombok.Builder;
import lombok.Singular;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class WebApplication extends Application {
    protected Tomcat server;
    protected Context context;
    private final Map<RequestMapping, PathController<?, ?>> pathControllers;
    private static final Logger LOG = LoggerFactory.getLogger(WebApplication.class);

    @Builder
    private WebApplication(Class<?> main, @Singular("pathMapping") Map<RequestMapping, PathController<?, ?>> pathControllers, String[] args) {
        super(main, args);
        this.pathControllers = pathControllers;
    }

    @Override
    public void start() throws Exception {
        if (LOG.isInfoEnabled()) {
            LOG.info("START APPLICATION");
        }
        super.start();
        server = new Tomcat();
        server.setBaseDir(properties.serverDirectory());

        var connector1 = server.getConnector();
        connector1.setPort(properties.serverPort());

        var docBase = new File(properties.serverDocBase()).getAbsolutePath();

        context = server.addContext(properties.serverContextPath(), docBase);

        registerServlets();

        server.start();
        server.getService().addConnector(connector1);
        server.getServer().await();
    }

    public void stop() throws LifecycleException {
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
        var controllersToAdd = new ArrayList<>(urlToHttpControllerMapping.entrySet());
        while (!controllersToAdd.isEmpty()) {
            var nextControllerMapping = controllersToAdd.get(0);
            var commonPrefixes = new HashSet<String>();
            for (var controllerMapping : controllersToAdd) {
                if (controllerMapping.equals(nextControllerMapping)) {
                    continue;
                }
                var urlPattern = controllerMapping.getKey();
                var commonPrefix = StringUtils.getCommonPrefix(nextControllerMapping.getKey(), urlPattern);
                if (commonPrefixIsValid(commonPrefix)) {
                    var smallerPrefixesExist = !commonPrefixes.isEmpty() &&
                            commonPrefixes.stream().noneMatch(commonPrefix::startsWith);
                    if (!smallerPrefixesExist) {
                        commonPrefixes.add(commonPrefix);
                    }
                }
            }
            if (commonPrefixes.isEmpty()) {
                var controller = new HttpControllerGroup(Map.ofEntries(nextControllerMapping));
                var url = cleanURLPath(nextControllerMapping.getKey());
                server.addServlet(properties.serverContextPath(), controller.toString(), controller);
                context.addServletMappingDecoded(url, controller.toString());
            } else {
                for(var prefix : commonPrefixes) {
                    var controllerMappingsWithPrefix = controllersToAdd.stream()
                            .filter(mapping -> mapping.getKey().startsWith(prefix))
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    var httpController = new HttpControllerGroup(controllerMappingsWithPrefix);
                    server.addServlet(properties.serverContextPath(), httpController.toString(), httpController);
                    context.addServletMappingDecoded(cleanURLPath(prefix) + "/*", httpController.toString());
                    controllersToAdd.removeAll(controllerMappingsWithPrefix.entrySet());
                }
            }
            controllersToAdd.remove(nextControllerMapping);
        }
    }

    private boolean commonPrefixIsValid(String prefix) {
        return !prefix.isEmpty() && !prefix.equals("/");
    }

    private String cleanURLPath(String urlPath) {
        var cleaned = urlPath.replaceAll("\\{.+}", "*");
        if (cleaned.equals("/")) {
            return "";
        }
        return cleaned;
    }

}
