package io.john.amiscaray.backend.framework.web.application;

import io.john.amiscaray.backend.framework.core.Application;
import io.john.amiscaray.backend.framework.web.handler.PathController;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMapping;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public class WebApplication extends Application {
    protected Tomcat server;
    protected Context context;
    private final Map<RequestMapping, PathController<?, ?>> pathControllers;

    private WebApplication(Class<?> main, Map<RequestMapping, PathController<?, ?>> pathControllers, String[] args) {
        super(main, args);
        this.pathControllers = pathControllers;
    }

    public static WebApplicationBuilder builder() {
        return new WebApplicationBuilder();
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

        server.start();
        server.getService().addConnector(connector1);
        server.getServer().await();
    }

    public static class WebApplicationBuilder {
        private Class<?> main;
        private ArrayList<RequestMapping> pathControllers$key;
        private ArrayList<PathController<?, ?>> pathControllers$value;
        private String[] args;

        WebApplicationBuilder() {
        }

        public WebApplicationBuilder main(Class<?> main) {
            this.main = main;
            return this;
        }

        public WebApplicationBuilder pathMapping(RequestMapping pathMappingKey, PathController<?, ?> pathMappingValue) {
            if (this.pathControllers$key == null) {
                this.pathControllers$key = new ArrayList<RequestMapping>();
                this.pathControllers$value = new ArrayList<PathController<?, ?>>();
            }
            this.pathControllers$key.add(pathMappingKey);
            this.pathControllers$value.add(pathMappingValue);
            return this;
        }

        public WebApplicationBuilder pathControllers(Map<? extends RequestMapping, ? extends PathController<?, ?>> pathControllers) {
            if (pathControllers == null) {
                throw new NullPointerException("pathControllers cannot be null");
            }
            if (this.pathControllers$key == null) {
                this.pathControllers$key = new ArrayList<RequestMapping>();
                this.pathControllers$value = new ArrayList<PathController<?, ?>>();
            }
            for (final Map.Entry<? extends RequestMapping, ? extends PathController<?, ?>> $lombokEntry : pathControllers.entrySet()) {
                this.pathControllers$key.add($lombokEntry.getKey());
                this.pathControllers$value.add($lombokEntry.getValue());
            }
            return this;
        }

        public WebApplicationBuilder clearPathControllers() {
            if (this.pathControllers$key != null) {
                this.pathControllers$key.clear();
                this.pathControllers$value.clear();
            }
            return this;
        }

        public WebApplicationBuilder args(String[] args) {
            this.args = args;
            return this;
        }

        public WebApplication build() {
            Map<RequestMapping, PathController<?, ?>> pathControllers;
            switch (this.pathControllers$key == null ? 0 : this.pathControllers$key.size()) {
                case 0:
                    pathControllers = java.util.Collections.emptyMap();
                    break;
                case 1:
                    pathControllers = java.util.Collections.singletonMap(this.pathControllers$key.get(0), this.pathControllers$value.get(0));
                    break;
                default:
                    pathControllers = new java.util.LinkedHashMap<RequestMapping, PathController<?, ?>>(this.pathControllers$key.size() < 1073741824 ? 1 + this.pathControllers$key.size() + (this.pathControllers$key.size() - 3) / 3 : Integer.MAX_VALUE);
                    for (int $i = 0; $i < this.pathControllers$key.size(); $i++)
                        pathControllers.put(this.pathControllers$key.get($i), (PathController<?, ?>) this.pathControllers$value.get($i));
                    pathControllers = java.util.Collections.unmodifiableMap(pathControllers);
            }

            return new WebApplication(this.main, pathControllers, this.args);
        }

        public String toString() {
            return "WebApplication.WebApplicationBuilder(main=" + this.main + ", pathControllers$key=" + this.pathControllers$key + ", pathControllers$value=" + this.pathControllers$value + ", args=" + java.util.Arrays.deepToString(this.args) + ")";
        }
    }
}
