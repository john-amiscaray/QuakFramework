package io.john.amiscaray.quak.web.test.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.john.amiscaray.quak.core.di.ApplicationContext;
import io.john.amiscaray.quak.http.response.Response;
import io.john.amiscaray.quak.web.application.WebApplication;
import io.john.amiscaray.quak.web.controller.DynamicPathController;
import io.john.amiscaray.quak.web.controller.SimplePathController;
import io.john.amiscaray.quak.web.test.application.stub.filter.MockFilter;
import io.john.amiscaray.quak.web.test.stub.MockUserInfo;
import io.john.amiscaray.quak.web.test.application.stub.filter.FilterOne;
import io.john.amiscaray.quak.web.test.application.stub.filter.FilterTwo;
import io.john.amiscaray.quak.web.test.application.stub.filter.UsersFilter;
import io.john.amiscaray.quak.web.test.util.TestConnectionUtil;
import io.john.amiscaray.quak.web.test.util.TestFilterCollector;
import io.john.amiscaray.quak.http.request.RequestMapping;
import io.john.amiscaray.quak.http.request.RequestMethod;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.catalina.LifecycleException;
import org.junit.jupiter.api.*;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
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
                                        return Response.of(MockUserInfo.dummyUsersWithAgeAndName(Integer.parseInt(age), name));
                                    } else if (name != null) {
                                        return Response.of(MockUserInfo.dummyUsersWithName(name));
                                    } else if (age != null) {
                                        return Response.of(MockUserInfo.dummyUsersWithAge(Integer.parseInt(age)));
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
                                request -> Response.of(MockUserInfo.dummyUsers().stream().map(MockUserInfo::getAddress).toList())
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
                                        "User " + request.pathVariables().get("id") + " : " + MockUserInfo.dummyUser().getAddress())

                        )
                )
                .pathMapping(
                        new RequestMapping(RequestMethod.GET, "/user/{id}Long/{address}String"),
                        new DynamicPathController<>(
                                Void.class,
                                String.class,
                                request -> new Response<>(
                                        new HashMap<>(),
                                        HttpServletResponse.SC_OK,
                                        request.pathVariables().get("id") + " : " + request.pathVariables().get("address"))

                        )
                )
                .pathMapping(
                        new RequestMapping(RequestMethod.GET, "/user/balance/{balance}Float"),
                        new DynamicPathController<>(
                                Void.class,
                                String.class,
                                request -> new Response<>(
                                        new HashMap<>(),
                                        HttpServletResponse.SC_OK,
                                        "Float: " + request.pathVariables().get("balance")
                                )
                        )
                )
                .pathMapping(
                        new RequestMapping(RequestMethod.GET, "/user/balance/{balance}Double/double"),
                        new DynamicPathController<>(
                                Void.class,
                                String.class,
                                request -> new Response<>(
                                        new HashMap<>(),
                                        HttpServletResponse.SC_OK,
                                        "Double: " + request.pathVariables().get("balance")
                                )
                        )
                )
                .pathMapping(
                        new RequestMapping(RequestMethod.GET, "/user/balance/{balance}Integer"),
                        new DynamicPathController<>(
                                Void.class,
                                String.class,
                                request -> new Response<>(
                                        new HashMap<>(),
                                        HttpServletResponse.SC_OK,
                                        "Int: " + request.pathVariables().get("balance")
                                )
                        )
                )
                .build();
        application.init(configuration);
        application.startAsync();
    }

    @AfterEach
    public void afterEach() {
        var ctx = ApplicationContext.getInstance();
        var filterCollector = ctx.getInstance(TestFilterCollector.class);

        filterCollector.clear();
    }

    @AfterAll
    public static void stop() throws LifecycleException {

        application.finish();

    }

    @Test
    public void testGetRequestToRootYieldsHelloWorld() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL))
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
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "user"))
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
    @Disabled("Currently broken when running on github actions. No solution found")
    public void testDeleteRequestYields204WithCustomHeaderWithDeletedID() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "user/1"))
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
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "user/1/info"))
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
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "user/1"))
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
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "users?name=John"))
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
                        Assertions.assertEquals(MockUserInfo.dummyUsersWithName("John"), Arrays.stream(body).toList());
                    } catch (JsonProcessingException e) {
                        throw new AssertionError("Could not parse body: ", e);
                    }
                }
        );
    }

    @Test
    public void testGetRequestForUserWithQueryParamsForNameAndAge() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "users?name=John&age=21"))
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
                        Assertions.assertEquals(MockUserInfo.dummyUsersWithAgeAndName(21, "John"), Arrays.stream(body).toList());
                    } catch (JsonProcessingException e) {
                        throw new AssertionError("Could not parse body: ", e);
                    }
                }
        );
    }

    @Test
    public void testGetRequestForUserAddressesReturnsListOfAddresses() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "user/addresses"))
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
                        Assertions.assertEquals(MockUserInfo.dummyUsers().stream().map(MockUserInfo::getAddress).toList(), Arrays.stream(body).toList());
                    } catch (JsonProcessingException e) {
                        throw new AssertionError("Could not parse body: ", e);
                    }
                }
        );
    }

    @Test
    public void testGetRequestForUserIDAddressReturnsProperInfoString() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "user/1/address"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> Assertions.assertEquals(httpResponse.body(), "User 1 : " + MockUserInfo.dummyUser().getAddress())
        );
    }

    @Test
    public void testGetRequestForPathWithMultipleVariables() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "user/21/jumpstreet"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> assertEquals("21 : jumpstreet", httpResponse.body())
        );
    }

    @Test
    public void testGetRequestForPathOfUserBalanceWithBalanceAsInteger() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "user/balance/3"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> assertEquals("Int: 3", httpResponse.body())
        );
    }

    @Test
    public void testGetRequestForPathOfUserBalanceWithBalanceAsFloat() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "user/balance/3.14"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> assertEquals("Float: 3.14", httpResponse.body())
        );
    }

    @Test
    public void testGetRequestForPathOfUserBalanceWithBalanceAsDouble() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "user/balance/3.14/double"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> assertEquals("Double: 3.14", httpResponse.body())
        );
    }

    @Test
    public void testMockFilterIsCalledOnRequest() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL))
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                _httpResponse -> {
                    var ctx = ApplicationContext.getInstance();
                    var filterCollector = ctx.getInstance(TestFilterCollector.class);
                    assertThat(filterCollector.getAppliedFilters(), hasItem(
                            instanceOf(MockFilter.class)
                    ));
                }
        );
    }

    @Test
    public void testFilterOneIsCalledBeforeFilterTwo() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL))
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                _httpResponse -> {
                    var ctx = ApplicationContext.getInstance();
                    var collectedFilters = ctx.getInstance(TestFilterCollector.class);
                    var filtersCalled = collectedFilters.getAppliedFilters();

                    assertInstanceOf(FilterOne.class, filtersCalled.getFirst());
                    assertInstanceOf(FilterTwo.class, filtersCalled.get(1));
                }
        );
    }

    @Test
    public void testFilterOneTwoAndMockFiltersAreCalledForRootURL() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL))
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                _httpResponse -> {
                    var ctx = ApplicationContext.getInstance();
                    var collectedFilters = ctx.getInstance(TestFilterCollector.class);
                    var filtersCalled = collectedFilters.getAppliedFilters();

                    assertThat(filtersCalled, containsInRelativeOrder(
                            instanceOf(FilterOne.class),
                            instanceOf(FilterTwo.class),
                            instanceOf(MockFilter.class)
                    ));
                }
        );

    }

    @Test
    public void testFilterOneTwoAndMockFilterAndUsersFilterAreCalledForUsersURL() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "users"))
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                _httpResponse -> {
                    var ctx = ApplicationContext.getInstance();
                    var collectedFilters = ctx.getInstance(TestFilterCollector.class);
                    var filtersCalled = collectedFilters.getAppliedFilters();

                    assertThat(filtersCalled, containsInRelativeOrder(
                            instanceOf(FilterOne.class),
                            instanceOf(FilterTwo.class),
                            instanceOf(MockFilter.class),
                            instanceOf(UsersFilter.class)
                    ));
                }
        );
    }

    @Test
    public void testUsersFilterIsAppliedToUserByIDEndpoint() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "user/1"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                _httpResponse -> {
                    var ctx = ApplicationContext.getInstance();
                    var collectedFilters = ctx.getInstance(TestFilterCollector.class);
                    var filtersCalled = collectedFilters.getAppliedFilters();

                    assertThat(filtersCalled, hasItem(
                            instanceOf(UsersFilter.class)
                    ));
                }
        );
    }

    @Test
    public void testUsersFilterIsAppliedToUserAddressesEndpoint() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(TestConnectionUtil.ROOT_URL + "user/addresses"))
                .GET()
                .build();
        connectionUtil.attemptConnectionAndAssert(
                request,
                HttpResponse.BodyHandlers.ofString(),
                _httpResponse -> {
                    var ctx = ApplicationContext.getInstance();
                    var collectedFilters = ctx.getInstance(TestFilterCollector.class);
                    var filtersCalled = collectedFilters.getAppliedFilters();

                    assertThat(filtersCalled, hasItem(
                            instanceOf(UsersFilter.class)
                    ));
                }
        );
    }

}