package io.john.amiscaray.backend.framework.web.test.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TestConnectionUtil {

    private static final int REQUEST_TIMEOUT_SECONDS = 2;
    private static final int MAX_CONNECTION_RETRIES = 40;
    private static TestConnectionUtil singleton;

    public static final String ROOT_URL = "http://localhost:9000/test/";

    private TestConnectionUtil() { }

    public static TestConnectionUtil getInstance() {
        if (singleton == null) {
            singleton = new TestConnectionUtil();
        }
        return singleton;
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

    public void attemptConnectionAndAssert(HttpRequest request,
                                           HttpResponse.BodyHandler<?> bodyHandler,
                                           Consumer<HttpResponse<?>> assertionBlock){
        attemptConnectionAndAssert(request, bodyHandler, assertionBlock, 0);
    }

}
