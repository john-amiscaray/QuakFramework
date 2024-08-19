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
            return;
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
                        } else if (currentPatternPart.matches("\\{[a-z]+}(String|Long|Integer|Double|Float)?")) {
                            var pathVariableParts = currentPatternPart.split("}", 2);
                            var identifier = pathVariableParts[0].substring(1);
                            var type = pathVariableParts[1];
                            if ("Long".equals(type) && !isPathVariableLong(currentPathPart)) {
                                pathsMatch = false;
                                break;
                            } else if ("Integer".equals(type) && !isPathVariableInt(currentPathPart)) {
                                pathsMatch = false;
                                break;
                            } else if ("Double".equals(type) && !isPathVariableDouble(currentPathPart)) {
                                pathsMatch = false;
                                break;
                            } else if ("Float".equals(type) && !isPathVariableFloat(currentPathPart)) {
                                pathsMatch = false;
                                break;
                            }
                            // if the type is string or not specified, then it's a string so no check is needed
                            pathVariables.put(
                                    identifier,
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
        res.setStatus(404);
    }

    private boolean isPathVariableLong(String pathPart) {
        try {
            Long.parseLong(pathPart);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isPathVariableInt(String pathPart) {
        try {
            Integer.parseInt(pathPart);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isPathVariableDouble(String pathPart) {
        try {
            Double.parseDouble(pathPart);
            return !isPathVariableInt(pathPart); // Exclusively a double, not an int
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isPathVariableFloat(String pathPart) {
        try {
            Float.parseFloat(pathPart);
            return !isPathVariableInt(pathPart); // Exclusively a float, not an int
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
