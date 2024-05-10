package io.john.amiscaray.backend.framework.web.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class HttpControllerGroup extends HttpServlet {

    private Map<String, HttpController> controllers;

    public void addController(String urlPattern, HttpController controller) {
        controllers.put(urlPattern, controller);
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse res) throws IOException {
        var path = req.getRequestURI().replaceFirst("^" + req.getContextPath(), "");
        if (controllers.containsKey(path)) {
            var controller = controllers.get(path);
            controller.service(req, res);
        } else {
            for (var mapping : controllers.entrySet()) {
                var controller = mapping.getValue();
                var mappingPatternParts = mapping.getKey().split("/");
                var pathParts = path.split("/");
                var pathVariables = new HashMap<String, String>();
                if (mappingPatternParts.length == pathParts.length) {
                    var pathsMatch = true;
                    for (int i = 0; i < mappingPatternParts.length; i++) {
                        var currentPatternPart = mappingPatternParts[i];
                        var currentPathPart = pathParts[i];
                        if (currentPatternPart.equals(currentPathPart)) {
                            continue;
                        } else if (currentPatternPart.matches("\\{.*}")) {
                            pathVariables.put(
                                    currentPatternPart.substring(1, currentPatternPart.length() - 1),
                                    currentPathPart);
                            continue;
                        }
                        pathsMatch = false;
                    }
                    if (pathsMatch) {
                        controller.service(req, res, pathVariables);
                        return;
                    }
                }
            }
        }
    }
}
