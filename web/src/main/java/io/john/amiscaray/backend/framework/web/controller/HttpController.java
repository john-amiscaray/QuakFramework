package io.john.amiscaray.backend.framework.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.john.amiscaray.backend.framework.web.handler.PathController;
import io.john.amiscaray.backend.framework.web.handler.request.Request;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class HttpController extends HttpServlet {

    private final Map<RequestMethod, PathController<?, ?>> pathControllers;
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException {
        var method = RequestMethod.valueOf(servletRequest.getMethod());
        var controller = pathControllers.get(method);

        if (controller == null || controller.requestHandler() == null) {
            servletResponse.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "This path does not support HTTP method " + method.name());
            return;
        }

        var requestHeaders = extractHeaders(servletRequest);
        var bodyRaw = readBody(servletRequest);
        Request<?> request;
        if (controller.requestBodyType().equals(String.class)) {
            request = new Request<>(requestHeaders, method, bodyRaw);
        } else if (controller.requestBodyType().equals(Void.class)) {
            request = new Request<>(requestHeaders, method, null);
        } else {
            request = new Request<>(requestHeaders,
                    method,
                    MAPPER.readerFor(controller.requestBodyType()).readValue(bodyRaw));
        }

        var responseHeaders = new HashMap<String, String>();
        var response = controller.requestHandler().handleRequest((Request) request);
        servletResponse.setStatus(response.status());
        writeResponseHeaders(responseHeaders, servletResponse);

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

        while(headerNames.hasMoreElements()) {
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