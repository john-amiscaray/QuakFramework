package io.john.amiscaray.backend.framework.web.test.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.john.amiscaray.backend.framework.core.di.ApplicationContext;
import io.john.amiscaray.backend.framework.web.application.WebApplication;
import io.john.amiscaray.backend.framework.web.application.WebStarter;
import io.john.amiscaray.backend.framework.web.test.application.stub.ApplicationDetails;
import io.john.amiscaray.backend.framework.web.test.application.stub.MockApplicationDetailsProvider;
import io.john.amiscaray.backend.framework.web.test.util.TestConnectionUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static io.john.amiscaray.backend.framework.web.test.stub.MockAccount.*;
import static io.john.amiscaray.backend.framework.web.test.stub.MockUserInfo.dummyUser;
import static io.john.amiscaray.backend.framework.web.test.stub.MockUserInfo.dummyUsers;
import static io.john.amiscaray.backend.framework.web.test.util.TestConnectionUtil.ROOT_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebStarterTest {

    private static WebApplication application;
    private final TestConnectionUtil connectionUtil = TestConnectionUtil.getInstance();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final MockApplicationDetailsProvider applicationDetailsProvider = new MockApplicationDetailsProvider();
    private static ApplicationContext ctx;
    private static ApplicationDetails applicationDetails;

    @BeforeAll
    static void initApplication() throws ExecutionException, InterruptedException, TimeoutException {

        application = WebStarter.beginWebApplication(WebStarterTest.class, new String[] {})
                .get(10, TimeUnit.SECONDS);
        ctx = ApplicationContext.getInstance();
        applicationDetails = ctx.getInstance(ApplicationDetails.class);

    }

    @AfterAll
    public static void stop() throws Exception {

        application.stop();

    }

    @Test
    public void testGetRequestAtRootUsingControllerClass() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL))
                .GET()
                .build();

        connectionUtil.attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var body = httpResponse.body();
                    var status = httpResponse.statusCode();
                    assertEquals(HttpServletResponse.SC_OK, status);
                    assertEquals("Hello World", body);
                });

    }

    @Test
    public void testPostRequestToSaveMockUser() throws JsonProcessingException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(dummyUser())))
                .build();

        connectionUtil.attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var body = httpResponse.body();
                    var status = httpResponse.statusCode();
                    assertEquals(HttpServletResponse.SC_CREATED, status);
                    assertEquals("", body);
                });
    }

    @Test
    public void testGetRequestForUsersWithAge() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "user/age/21"))
                .GET()
                .build();

        connectionUtil.attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var body = httpResponse.body();
                    var status = httpResponse.statusCode();
                    assertEquals(HttpServletResponse.SC_OK, status);
                    try {
                        assertEquals(
                                MAPPER.writeValueAsString(dummyUsers()), body);
                    } catch (JsonProcessingException e) {
                        Assertions.fail(e);
                    }
                });

    }

    @Test
    public void testGetRequestForAccountByID() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "accounts/account/12"))
                .GET()
                .build();

        connectionUtil.attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var body = httpResponse.body();
                    var status = httpResponse.statusCode();
                    assertEquals(HttpServletResponse.SC_OK, status);
                    try {
                        assertEquals(MAPPER.writeValueAsString(dummyAccount(12)), body);
                    } catch (JsonProcessingException e) {
                        Assertions.fail(e);
                    }
                });
    }

    @Test
    public void testGetRequestToAccountWithQueryParamForAccountName() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "accounts/account?name=savings"))
                .GET()
                .build();

        connectionUtil.attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var body = httpResponse.body();
                    var status = httpResponse.statusCode();
                    assertEquals(HttpServletResponse.SC_OK, status);
                    try {
                        assertEquals(MAPPER.writeValueAsString(dummyAccountsWithName("savings")), body);
                    } catch (JsonProcessingException e) {
                        Assertions.fail(e);
                    }
                });
    }

    @Test
    public void testGetRequestToAccountWithQueryParamForAccountNameAndBalance() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "accounts/account?name=savings&balance=10000"))
                .GET()
                .build();

        connectionUtil.attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var body = httpResponse.body();
                    var status = httpResponse.statusCode();
                    assertEquals(HttpServletResponse.SC_OK, status);
                    try {
                        assertEquals(MAPPER.writeValueAsString(dummyAccountsWithNameAndUserBalance("savings", 10000)), body);
                    } catch (JsonProcessingException e) {
                        Assertions.fail(e);
                    }
                });
    }

    @Test
    public void testPostRequestToRootGivesHTTP405() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        connectionUtil.attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var status = httpResponse.statusCode();
                    assertEquals(HttpServletResponse.SC_METHOD_NOT_ALLOWED, status);
                });
    }

    @Test
    public void testGetRequestToApplicationNamePathRetrievesApplicationName() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "application/name"))
                .GET()
                .build();

        connectionUtil.attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var status = httpResponse.statusCode();
                    var body = httpResponse.body();
                    assertEquals(HttpServletResponse.SC_OK, status);
                    assertEquals(applicationDetailsProvider.applicationName(), body);
                });
    }

    @Test
    public void testGetRequestToApplicationVersionPathRetrievesApplicationVersion() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "application/version"))
                .GET()
                .build();

        connectionUtil.attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var status = httpResponse.statusCode();
                    var body = httpResponse.body();
                    assertEquals(HttpServletResponse.SC_OK, status);
                    assertEquals(applicationDetailsProvider.version(), Float.parseFloat((String) body));
                });
    }

    @Test
    public void testGetRequestToApplicationDetailsPathRetrievesApplicationDetails() {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "application/details"))
                .GET()
                .build();

        connectionUtil.attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var status = httpResponse.statusCode();
                    String body = (String) httpResponse.body();
                    assertEquals(HttpServletResponse.SC_OK, status);
                    try {
                        assertEquals(applicationDetails, MAPPER.readerFor(ApplicationDetails.class).readValue(body));
                    } catch (JsonProcessingException e) {
                        Assertions.fail(e);
                    }
                });
    }

}
