package io.john.amiscaray.backend.framework.web.test.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.john.amiscaray.backend.framework.web.application.WebApplication;
import io.john.amiscaray.backend.framework.web.controller.DynamicPathController;
import io.john.amiscaray.backend.framework.web.controller.SimplePathController;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMapping;
import io.john.amiscaray.backend.framework.web.handler.request.RequestMethod;
import io.john.amiscaray.backend.framework.web.handler.response.Response;
import io.john.amiscaray.backend.framework.web.test.stub.MockUserInfo;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class WebApplicationTest {

    private static final int REQUEST_TIMEOUT_SECONDS = 10;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String ROOT_URL = "http://localhost:9000/test";
    private static final int MAX_CONNECTION_RETRIES = 20;
    private static final MockUserInfo MOCK_USER = new MockUserInfo("John", 21, "831 Some Street");

    @BeforeAll
    static void startWebApplication() {
        WebApplication webApplication = WebApplication.builder()
                .main(WebApplicationTest.class)
                .args(new String[]{})
                .pathMapping(
                        new RequestMapping(RequestMethod.GET, "/"),
                        new SimplePathController<>(
                                Void.class,
                                String.class,
                                _request -> new Response<>(new HashMap<>(), HttpServletResponse.SC_OK, "Hello World")
                        )
                )
                .pathMapping(
                        new RequestMapping(RequestMethod.POST, "/user"),
                        new DynamicPathController<>(
                                MockUserInfo.class,
                                String.class,
                                _request -> new Response<>(new HashMap<>(), HttpServletResponse.SC_CREATED, "/user/1")
                        )
                )
                .pathMapping(
                        new RequestMapping(RequestMethod.GET, "/user/{id}"),
                        new DynamicPathController<>(
                                Void.class,
                                MockUserInfo.class,
                                request -> new Response<>(
                                        new HashMap<>(),
                                        HttpServletResponse.SC_OK,
                                        MOCK_USER
                                )
                        )
                )
                .pathMapping(
                        new RequestMapping(RequestMethod.DELETE, "/user/{id}"),
                        new DynamicPathController<>(
                                Void.class,
                                String.class,
                                request -> new Response<>(Map.of("thing",
                                        request.pathVariables().get("id")),
                                        HttpServletResponse.SC_NO_CONTENT,
                                        null)
                        )
                )
                .pathMapping(
                        new RequestMapping(RequestMethod.GET, "/user/{id}/info"),
                        new DynamicPathController<>(
                                String.class,
                                String.class,
                                request -> new Response<>(
                                        new HashMap<>(),
                                        HttpServletResponse.SC_OK,
                                        "User " + request.pathVariables().get("id"))
                        )
                )
                .build();
        webApplication.startAsync();
    }

    public void attemptConnectionAndAssert(HttpRequest request,
                                           HttpResponse.BodyHandler<?> bodyHandler,
                                           Consumer<HttpResponse<?>> assertionBlock,
                                           int retries) {
        var httpClient = HttpClient.newHttpClient();

        httpClient.sendAsync(request, bodyHandler)
                .thenAccept(assertionBlock)
                .orTimeout(REQUEST_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .exceptionally(exception -> {
                    if (exception instanceof ConnectException || exception.getCause() instanceof ConnectException) {
                        if (retries < MAX_CONNECTION_RETRIES) {
                            try {
                                Thread.sleep(50);
                                attemptConnectionAndAssert(request, bodyHandler, assertionBlock, retries + 1);
                            } catch (InterruptedException e) {
                                Assertions.fail("Unable to pause and re-attempt connection", e);
                            }
                        } else {
                            Assertions.fail("Failed to connect after " + MAX_CONNECTION_RETRIES + " attempts");
                        }
                    } else {
                        Assertions.fail(exception);
                    }
                    return null;
                })
                .join();
    }

    private void attemptConnectionAndAssert(HttpRequest request,
                                            HttpResponse.BodyHandler<?> bodyHandler,
                                            Consumer<HttpResponse<?>> assertionBlock){
        attemptConnectionAndAssert(request, bodyHandler, assertionBlock, 0);
    }

    @Test
    public void testGetRequestToRootYieldsHelloWorld() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "/"))
                .build();

        attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var body = httpResponse.body();
                    assertEquals("Hello World", body);
                });
    }

    @Test
    public void testPostRequestToRootYields201Created() throws JsonProcessingException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "/user"))
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(new MockUserInfo("John", 21, "1234 Some Street"))))
                .build();
        attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    assertEquals(HttpServletResponse.SC_CREATED, httpResponse.statusCode());
                    assertEquals("/user/1", httpResponse.body());
                }
        );
    }

    @Test
    public void testDeleteRequestYields204WithCustomHeaderWithDeletedID() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "/user/1"))
                .DELETE()
                .build();
        attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var thingHeader = httpResponse.headers().firstValue("thing");
                    assertTrue(thingHeader.isPresent());
                    assertEquals(Integer.parseInt(thingHeader.get()), 1);
                }
        );
    }

    @Test
    public void testGetRequestForUserInfoYields200AndHasUserIDStringInBody() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "/user/1/info"))
                .GET()
                .build();
        attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var body = httpResponse.body();
                    var status = httpResponse.statusCode();
                    assertEquals("User 1", body);
                    assertEquals(status, 200);
                }
        );
    }

    @Test
    public void testGetRequestForUserWithIDYieldsMockUser() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "/user/1"))
                .GET()
                .build();
        attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    MockUserInfo body;
                    try {
                        body = MAPPER.readerFor(MockUserInfo.class).readValue((String) httpResponse.body());
                        var status = httpResponse.statusCode();
                        assertEquals(MOCK_USER, body);
                        assertEquals(status, 200);
                    } catch (JsonProcessingException e) {
                        throw new AssertionError("Could not parse body as mock user: ", e);
                    }
                }
        );
    }

}