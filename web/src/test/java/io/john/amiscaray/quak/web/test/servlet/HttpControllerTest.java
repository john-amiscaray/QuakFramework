package io.john.amiscaray.quak.web.test.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.http.request.RequestMethod;
import io.john.amiscaray.quak.http.response.Response;
import io.john.amiscaray.quak.web.cfg.WebConfig;
import io.john.amiscaray.quak.web.controller.SimplePathController;
import io.john.amiscaray.quak.web.handler.RequestHandler;
import io.john.amiscaray.quak.web.servlet.HttpController;
import io.john.amiscaray.quak.web.test.servlet.stub.Employee;
import io.john.amiscaray.quak.web.test.servlet.stub.Student;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;

import static org.mockito.Mockito.*;

public class HttpControllerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldYieldHTTP400AfterInvalidRequestBodyWhenConfiguredToYield400OnJsonMappingException() throws IOException {
        var httpController = new HttpController("", Map.of(
                RequestMethod.GET,
                new SimplePathController<>(
                        Student.class,
                        Void.class,
                        request -> new Response<>(200, null)
                )
        ));
        var mockApplicationContext = mock(ApplicationContext.class);
        var webConfig = WebConfig.builder()
                        .mapExceptionToStatusCode(JsonMappingException.class, 400)
                        .build();
        var mockRequest = mockRequest("/", "GET");
        when(mockRequest.getReader())
                .thenReturn(requestBodyReaderFor(new Employee("John", "Tech", 65000)));
        var mockResponse = mockResponse();
        doReturn(webConfig)
                .when(mockApplicationContext)
                .getInstance(WebConfig.APPLICATION_WEB_CFG_DEPENDENCY_ID);
        try (var mockStaticApplicationContext = mockStatic(ApplicationContext.class)) {
            mockStaticApplicationContext.when(ApplicationContext::getInstance)
                    .thenReturn(mockApplicationContext);

            httpController.service(mockRequest, mockResponse);

            verify(mockResponse, times(1))
                    .setStatus(400);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldYieldHTTP201ForIllegalArgumentExceptionWhenConfiguredToYield200ForAnyException() throws IOException {
        var httpController = new HttpController("", Map.of(
                RequestMethod.GET,
                new SimplePathController<>(
                        Student.class,
                        Void.class,
                        request -> new Response<>(200, null)
                )
        ));
        var mockApplicationContext = mock(ApplicationContext.class);
        var webConfig = WebConfig.builder()
                .mapExceptionToStatusCode(Exception.class, 201)
                .build();
        var mockRequest = mockRequest("/", "GET");
        var mockResponse = mockResponse();
        when(mockRequest.getReader()).thenThrow(new IllegalArgumentException("Dummy"));
        doReturn(webConfig)
                .when(mockApplicationContext)
                .getInstance(WebConfig.APPLICATION_WEB_CFG_DEPENDENCY_ID);
        try (var mockStaticApplicationContext = mockStatic(ApplicationContext.class)) {
            mockStaticApplicationContext.when(ApplicationContext::getInstance)
                    .thenReturn(mockApplicationContext);

            httpController.service(mockRequest, mockResponse);

            verify(mockResponse, times(1))
                    .setStatus(201);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldPrioritizeDirectMappingsForExceptionsOverSuperExceptions() throws IOException {
        var httpController = new HttpController("", Map.of(
                RequestMethod.GET,
                new SimplePathController<>(
                        Student.class,
                        Void.class,
                        request -> new Response<>(200, null)
                )
        ));
        var mockApplicationContext = mock(ApplicationContext.class);
        var webConfig = WebConfig.builder()
                .mapExceptionToStatusCode(Exception.class, 200)
                .mapExceptionToStatusCode(IllegalArgumentException.class, 400)
                .build();
        var mockRequest = mockRequest("/", "GET");
        var mockResponse = mockResponse();
        when(mockRequest.getReader()).thenThrow(new IllegalArgumentException("Dummy"));
        doReturn(webConfig)
                .when(mockApplicationContext)
                .getInstance(WebConfig.APPLICATION_WEB_CFG_DEPENDENCY_ID);
        try (var mockStaticApplicationContext = mockStatic(ApplicationContext.class)) {
            mockStaticApplicationContext.when(ApplicationContext::getInstance)
                    .thenReturn(mockApplicationContext);

            httpController.service(mockRequest, mockResponse);

            verify(mockResponse, times(1))
                    .setStatus(400);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void shouldCallRequestHandlerWhenEverythingIsOk() throws IOException, ServletException {
        var mockRequestHandler = mock(RequestHandler.class);
        var httpController = new HttpController("", Map.of(
                RequestMethod.GET,
                new SimplePathController<>(
                        Void.class,
                        Void.class,
                        mockRequestHandler
                )
        ));
        when(mockRequestHandler.handleRequest(any()))
                .thenReturn(new Response(200, null));
        var mockRequest = mockRequest("/", "GET");
        when(mockRequest.getReader())
                .thenReturn(requestBodyReader());
        var mockResponse = mockResponse();

        httpController.service(mockRequest, mockResponse);

        verify(mockRequestHandler, times(1))
                .handleRequest(any());
    }

    private static BufferedReader requestBodyReaderFor(Object obj) throws JsonProcessingException {
        return new BufferedReader(new StringReader(MAPPER.writeValueAsString(obj)));
    }

    private static BufferedReader requestBodyReaderForString(String string) {
        return new BufferedReader(new StringReader(string));
    }

    private static BufferedReader requestBodyReader() {
        return requestBodyReaderForString("");
    }

    private static HttpServletRequest mockRequest(String path, String method) throws IOException {
        var request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn(path);
        when(request.getMethod()).thenReturn(method);
        when(request.getHeaderNames()).thenReturn(Collections.emptyEnumeration());
        when(request.getAttributeNames()).thenReturn(Collections.emptyEnumeration());
        return request;
    }

    private static HttpServletResponse mockResponse() throws IOException {
        var response = mock(HttpServletResponse.class);
        when(response.getWriter()).thenReturn(new PrintWriter(System.out, true));
        return response;
    }

}
