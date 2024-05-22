package io.john.amiscaray.backend.framework.web.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.john.amiscaray.backend.framework.web.controller.PathController;
import io.john.amiscaray.backend.framework.web.handler.request.DynamicPathRequest;
import io.john.amiscaray.backend.framework.web.handler.request.Request;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpController extends HttpServlet {

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

        if (controller == null || controller.requestHandler() == null) {
            servletResponse.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "This path does not support HTTP method " + method.name());
            return;
        }

        var requestHeaders = extractHeaders(servletRequest);
        var bodyRaw = readBody(servletRequest);
        Request<?> request;
        if (controller.requestBodyType().equals(String.class)) {
            request = new DynamicPathRequest<>(requestHeaders, method,
                    pathParameters,
                    bodyRaw);
        } else if (controller.requestBodyType().equals(Void.class)) {
            request = new DynamicPathRequest<>(requestHeaders,
                    method,
                    pathParameters,
                    null);
        } else {
            request = new DynamicPathRequest<>(requestHeaders,
                    method,
                    pathParameters,
                    MAPPER.readerFor(controller.requestBodyType()).readValue(bodyRaw));
        }

        var response = controller.requestHandler().handleRequest((Request) request);
        servletResponse.setStatus(response.status());
        writeResponseHeaders(response.headers(), servletResponse);

        if (controller.responseBodyType().equals(String.class)) {
            servletResponse.getWriter().print(response.body());
        } else if (!controller.responseBodyType().equals(Void.class)) {
            MAPPER.writerFor(controller.responseBodyType()).writeValue(servletResponse.getWriter(), controller.responseBodyType().cast(response.body()));
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
