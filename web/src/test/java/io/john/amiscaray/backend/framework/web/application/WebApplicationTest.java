package io.john.amiscaray.backend.framework.web.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.john.amiscaray.backend.framework.web.handler.PathController;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMapping;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import io.john.amiscaray.backend.framework.web.handler.response.Response;
import io.john.amiscaray.backend.framework.web.stub.MockUserInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class WebApplicationTest {

    private static final int REQUEST_TIMEOUT_SECONDS = 10;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String ROOT_URL = "http://localhost:9000";

    @BeforeAll
    static void startWebApplication() throws Exception {
        WebApplication webApplication = WebApplication.builder()
                .main(WebApplicationTest.class)
                .args(new String[]{})
                .pathMapping(
                        new RequestMapping(RequestMethod.GET, "/"),
                        new PathController<>(
                                Void.class,
                                String.class,
                                request -> new Response<>(new HashMap<>(), HttpServletResponse.SC_OK, "Hello World")
                        )
                )
                .pathMapping(
                        new RequestMapping(RequestMethod.POST, "/"),
                        new PathController<>(
                                MockUserInfo.class,
                                String.class,
                                _request -> new Response<>(new HashMap<>(), HttpServletResponse.SC_CREATED, "/user/1")
                        )
                )
                .build();

        webApplication.start();
    }

    @Test
    public void testGetRequestToRootYieldsHelloWorld() {
        var httpClient = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "/test/"))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(body -> assertEquals("Hello World", body))
                .orTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .join();
    }

    @Test
    public void testPostRequestToRootYields201Created() throws JsonProcessingException {
        var httpClient = HttpClient.newHttpClient();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "/test/"))
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(new MockUserInfo("John", 21, "1234 Some Street"))))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    assertEquals(201, response.statusCode());
                    assertEquals("/user/1", response.body());
                })
                .orTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .join();
    }

}