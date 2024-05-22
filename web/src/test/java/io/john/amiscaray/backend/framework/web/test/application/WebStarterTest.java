package io.john.amiscaray.backend.framework.web.test.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.john.amiscaray.backend.framework.web.application.WebApplication;
import io.john.amiscaray.backend.framework.web.application.WebStarter;
import io.john.amiscaray.backend.framework.web.test.stub.MockUserInfo;
import io.john.amiscaray.backend.framework.web.test.util.TestConnectionUtil;
import org.apache.catalina.LifecycleException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static io.john.amiscaray.backend.framework.web.test.util.TestConnectionUtil.ROOT_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class WebStarterTest {

    private static WebApplication application;
    private final TestConnectionUtil connectionUtil = TestConnectionUtil.getInstance();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @BeforeAll
    static void initApplication() {

        application = WebStarter.beginWebApplication(WebStarterTest.class, new String[] {});

    }

    @AfterAll
    public static void stop() throws LifecycleException {

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
                    assertEquals(200, status);
                    assertEquals("Hello World", body);
                });

    }

    @Test
    public void testPostRequestToSaveMockUser() throws JsonProcessingException {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(ROOT_URL + "user"))
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(new MockUserInfo(
                            "John",
                            21,
                        "123 Some Street"
                ))))
                .build();

        connectionUtil.attemptConnectionAndAssert(request,
                HttpResponse.BodyHandlers.ofString(),
                httpResponse -> {
                    var body = httpResponse.body();
                    var status = httpResponse.statusCode();
                    assertEquals(201, status);
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
                    assertEquals(200, status);
                    try {
                        assertEquals(
                                MAPPER.writeValueAsString(
                                        List.of(
                                                new MockUserInfo("John", 21, "123 Some Street")
                                        )
                                ), body);
                    } catch (JsonProcessingException e) {
                        Assertions.fail(e);
                    }
                });

    }

}
