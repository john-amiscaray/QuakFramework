package io.john.amiscaray.backend.framework.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.web.cfg.WebConfig;
import io.john.amiscaray.backend.framework.web.controller.PathController;
import io.john.amiscaray.backend.framework.web.handler.request.DynamicPathRequest;
import io.john.amiscaray.backend.framework.web.handler.request.Request;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpController extends HttpServlet {

    @Setter
    private Map<String, String> pathParameters = new HashMap<>();
    private final String urlPattern;
    private final Map<RequestMethod, PathController<?, ?>> pathControllers;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Logger LOG = LoggerFactory.getLogger(HttpController.class);

    public HttpController(String urlPattern, Map<RequestMethod, PathController<?, ?>> pathControllers) {
        this.urlPattern = urlPattern;
        this.pathControllers = pathControllers;
    }

    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Map<String, String> pathParameters) throws IOException {
        this.pathParameters = pathParameters;
        service(servletRequest, servletResponse);
    }

    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Servicing Request: {} {}", servletRequest.getMethod(), servletRequest.getRequestURI());
        }
        var method = RequestMethod.valueOf(servletRequest.getMethod());
        var controller = pathControllers.get(method);
        var queryParams = servletRequest.getParameterMap()
                .entrySet()
                .stream()
                .filter(paramEntry -> paramEntry.getValue().length > 0)
                .map(paramEntry -> Map.entry(paramEntry.getKey(), paramEntry.getValue()[0]))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        if (controller == null || controller.requestHandler() == null) {
            servletResponse.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "This path does not support HTTP method " + method.name());
            return;
        }

        var requestHeaders = extractHeaders(servletRequest);
        var bodyRaw = readBody(servletRequest);
        var requestAttributes = extractAttributes(servletRequest);
        Request<?> request;
        if (controller.requestBodyType().equals(String.class)) {
            request = new DynamicPathRequest<>(
                    requestHeaders,
                    queryParams,
                    method,
                    pathParameters,
                    bodyRaw,
                    requestAttributes);
        } else if (controller.requestBodyType().equals(Void.class)) {
            request = new DynamicPathRequest<>(
                    requestHeaders,
                    queryParams,
                    method,
                    pathParameters,
                    null,
                    requestAttributes);
        } else {
            request = new DynamicPathRequest<>(
                    requestHeaders,
                    queryParams,
                    method,
                    pathParameters,
                    MAPPER.readerFor(controller.requestBodyType()).readValue(bodyRaw),
                    requestAttributes);
        }

        try {
            var response = controller.requestHandler().handleRequest((Request) request);
            servletResponse.setStatus(response.status());
            writeResponseHeaders(response.headers(), servletResponse);

            if (response.status() >= 200 && response.status() < 300) {
                if (controller.responseBodyType().equals(String.class)) {
                    servletResponse.getWriter().print(response.body());
                } else if (!controller.responseBodyType().equals(Void.class)) {
                    MAPPER.writerFor(controller.responseBodyType()).writeValue(servletResponse.getWriter(), controller.responseBodyType().cast(response.body()));
                }
            }
        } catch (Exception ex) {
            var applicationContext = ApplicationContext.getInstance();
            if (applicationContext.hasInstance(WebConfig.APPLICATION_WEB_CFG_DEPENDENCY_ID)) {
                var statusCode = backTraceToFirstExceptionStatusCode(applicationContext.getInstance(WebConfig.APPLICATION_WEB_CFG_DEPENDENCY_ID), ex);
                if (statusCode != null) {
                    servletResponse.setStatus(statusCode);
                    servletResponse.getWriter().write(ex.getMessage());
                } else {
                    servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                servletResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    private Integer backTraceToFirstExceptionStatusCode(WebConfig cfg, Exception ex) {
        if (ex == null) {
            return null;
        }
        var exceptionToStatusCode = cfg.exceptionHttpStatusMapping();
        if (exceptionToStatusCode.containsKey(ex.getClass())) {
            return exceptionToStatusCode.get(ex.getClass());
        }

        if (ex.getCause() instanceof Exception cause) {
            return backTraceToFirstExceptionStatusCode(cfg, cause);
        } else {
            return null;
        }
    }

    private Map<String, String> extractHeaders(HttpServletRequest req) {
        var headerNames = req.getHeaderNames();
        var result = new HashMap<String, String>();

        if (headerNames == null) {
            return result;
        }

        while (headerNames.hasMoreElements()) {
            var headerName = headerNames.nextElement();
            result.put(headerName, req.getHeader(headerName));
        }

        return result;
    }

    private Map<String, Object> extractAttributes(HttpServletRequest req) {
        var attributeIter = req.getAttributeNames();
        var result = new HashMap<String, Object>();
        while (attributeIter.hasMoreElements()) {
            var attrName = attributeIter.nextElement();
            result.put(attrName, req.getAttribute(attrName));
        }
        return result;
    }

    private void writeResponseHeaders(Map<String, String> headers, HttpServletResponse response) {
        for (var header : headers.entrySet()) {
            response.setHeader(header.getKey(), header.getValue());
        }
    }

    private String readBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

}
