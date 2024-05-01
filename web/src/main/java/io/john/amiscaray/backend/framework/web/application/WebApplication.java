package io.john.amiscaray.backend.framework.web.application;

import io.john.amiscaray.backend.framework.core.Application;
import io.john.amiscaray.backend.framework.web.controller.PathController;
import io.john.amiscaray.backend.framework.web.controller.SimplePathController;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMapping;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import io.john.amiscaray.backend.framework.web.servlet.HttpController;
import lombok.Builder;
import lombok.Singular;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.javatuples.Pair;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WebApplication extends Application {
    protected Tomcat server;
    protected Context context;
    private final Map<RequestMapping, PathController<?, ?>> pathControllers;

    @Builder
    private WebApplication(Class<?> main, @Singular("pathMapping") Map<RequestMapping, PathController<?, ?>> pathControllers, String[] args) {
        super(main, args);
        this.pathControllers = pathControllers;
    }

    @Override
    public void start() throws Exception {
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
        Thread.startVirtualThread(() -> server.getServer().await());
    }

    private void registerServlets() {
        var urlToControllersMapping = new HashMap<String, List<Pair<RequestMethod, PathController<?, ?>>>>();
        for (var entry : pathControllers.entrySet()) {
            Pair<RequestMethod, PathController<?, ?>> methodToControllerMapping = Pair.with(entry.getKey().method(), entry.getValue());
            if (urlToControllersMapping.containsKey(entry.getKey().url())) {
                urlToControllersMapping.get(entry.getKey().url()).add(methodToControllerMapping);
            } else {
                urlToControllersMapping.put(entry.getKey().url(), new ArrayList<>(List.of(methodToControllerMapping)));
            }
        }
        for (var entry : urlToControllersMapping.entrySet()) {
            var url = entry.getKey();
            var controller = new HttpController(entry.getValue()
                    .stream()
                    .collect(Collectors.toMap(
                            Pair::getValue0,
                            Pair::getValue1
                    )));
            server.addServlet(properties.serverContextPath(), controller.toString(), controller);
            context.addServletMappingDecoded(url, controller.toString());
        }
    }

}
