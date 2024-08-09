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
import io.john.amiscaray.backend.framework.web.test.util.TestConnectionUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.john.amiscaray.backend.framework.web.test.stub.MockUserInfo.*;
import static io.john.amiscaray.backend.framework.web.test.util.TestConnectionUtil.ROOT_URL;
import static org.junit.jupiter.api.Assertions.*;

public class WebApplicationTest {
    
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final MockUserInfo MOCK_USER = new MockUserInfo("John", 21, "831 Some Street");
    private static final TestConnectionUtil connectionUtil = TestConnectionUtil.getInstance();
    private static WebApplication application;

    @BeforeAll
    static void startWebApplication() {
        application = WebApplication.getInstance();
        var configuration = WebApplication.Configuration.builder()
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
                        new RequestMapping(RequestMethod.GET, "/users"),
                        new SimplePathController<>(
                                Void.class,
                                List.class,
                                request -> {
                                    var name = request.queryParams().get("name");
                                    var age = request.queryParams().get("age");
                                    if (name != null && age != null) {
                                        return Response.of(dummyUsersWithAgeAndName(Integer.parseInt(age), name));
                                    } else if (name != null) {
                                        return Response.of(dummyUsersWithName(name));
                                    } else if (age != null) {
                                        return Response.of(dummyUsersWithAge(Integer.parseInt(age)));
                                    } else {
                                        return Response.of(List.of());
                                    }
                                }
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
                        new RequestMapping(RequestMethod.GET, "/user/{id}Long"),
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
                        new RequestMapping(RequestMethod.DELETE, "/user/{id}Long"),
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
                        new RequestMapping(RequestMethod.GET, "/user/addresses"),
                        new DynamicPathController<>(
                                Void.class,
                                List.class,
                                request -> Response.of(dummyUsers().stream().map(MockUserInfo::getAddress).toList())
                        )
                )
                .pathMapping(
                        new RequestMapping(RequestMethod.GET, "/user/{id}Long/info"),
                        new DynamicPathController<>(
                                String.class,
                                String.class,
                                request -> new Response<>(
                                        new HashMap<>(),
                                        HttpServletResponse.SC_OK,
                                        "User " + request.pathVariables().get("id"))
                        )
                )
                .pathMapping(
                        new RequestMapping(RequestMethod.GET, "/user/{id}Long/address"),
                        new DynamicPathController<>(
                                String.class,
                                String.class,
                                request -> new Response<>(
                                        new HashMap<>(),
                                        HttpServletResponse.SC_OK,
                                        "User " + request.pathVariables().get("id") + " : " + dummyUser().getAddress())

                        )
                )
                .pathMapping(
                        new RequestMapping(RequestMethod.GET, "/user/{id}Long/{address}String"),
                        new DynamicPathController<>(
                                String.class,
                                String.class,
                                request -> new Response<>(
                                        new HashMap<>(),
                                        HttpServletResponse.SC_OK,
                                        request.pathVariables().get("id") + " : " + request.pathVariables().get("address"))

                        )
                )
                .build();
        application.init(configuration);
        application.startAsync();
    }

    @AfterAll
    public static void stop() throws LifecycleException {

        application.finish();

    }

    @Test
    public void testGetRequestToRootYieldsHelloWorld() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL))
                .build();

        connectionUtil.attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var body = httpResponse.body();
                    assertEquals("Hello World", body);
                });
    }

    @Test
    @Disabled("Currently broken when running on github actions. No solution found")
    public void testPostRequestToRootYields201Created() throws JsonProcessingException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(new MockUserInfo("John", 21, "1234 Some Street"))))
                .build();
        connectionUtil.attemptConnectionAndAssert(
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
                .uri(URI.create(ROOT_URL + "user/1"))
                .DELETE()
                .build();
        connectionUtil.attemptConnectionAndAssert(
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
                .uri(URI.create(ROOT_URL + "user/1/info"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var body = httpResponse.body();
                    var status = httpResponse.statusCode();
                    assertEquals(200, status);
                    assertEquals("User 1", body);
                }
        );
    }

    @Test
    public void testGetRequestForUserWithIDYieldsMockUser() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "user/1"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    MockUserInfo body;
                    try {
                        body = MAPPER.readerFor(MockUserInfo.class).readValue((String) httpResponse.body());
                        var status = httpResponse.statusCode();
                        assertEquals(status, 200);
                        assertEquals(MOCK_USER, body);
                    } catch (JsonProcessingException e) {
                        throw new AssertionError("Could not parse body as mock user: ", e);
                    }
                }
        );
    }

    @Test
    public void testGetRequestForUserWithQueryParamForName() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "users?name=John"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    MockUserInfo[] body;
                    try {
                        body = MAPPER.readerFor(MockUserInfo[].class).readValue((String) httpResponse.body());
                        var status = httpResponse.statusCode();
                        assertEquals(status, 200);
                        assertEquals(dummyUsersWithName("John"), Arrays.stream(body).toList());
                    } catch (JsonProcessingException e) {
                        throw new AssertionError("Could not parse body: ", e);
                    }
                }
        );
    }

    @Test
    public void testGetRequestForUserWithQueryParamsForNameAndAge() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "users?name=John&age=21"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    MockUserInfo[] body;
                    try {
                        body = MAPPER.readerFor(MockUserInfo[].class).readValue((String) httpResponse.body());
                        var status = httpResponse.statusCode();
                        assertEquals(status, 200);
                        assertEquals(dummyUsersWithAgeAndName(21, "John"), Arrays.stream(body).toList());
                    } catch (JsonProcessingException e) {
                        throw new AssertionError("Could not parse body: ", e);
                    }
                }
        );
    }

    @Test
    public void testGetRequestForUserAddressesReturnsListOfAddresses() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "user/addresses"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    String[] body;
                    try {
                        body = MAPPER.readerFor(String[].class).readValue((String) httpResponse.body());
                        var status = httpResponse.statusCode();
                        assertEquals(status, 200);
                        assertEquals(dummyUsers().stream().map(MockUserInfo::getAddress).toList(), Arrays.stream(body).toList());
                    } catch (JsonProcessingException e) {
                        throw new AssertionError("Could not parse body: ", e);
                    }
                }
        );
    }

    @Test
    public void testGetRequestForUserIDAddressReturnsProperInfoString() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "user/1/address"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> assertEquals(httpResponse.body(), "User 1 : " + dummyUser().getAddress())
        );
    }

    @Test
    public void testGetRequestForPathWithMultipleVariables() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "user/21/jumpstreet"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> assertEquals("21 : jumpstreet", httpResponse.body())
        );
    }

}