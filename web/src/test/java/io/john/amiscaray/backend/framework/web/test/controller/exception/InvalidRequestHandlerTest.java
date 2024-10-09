package io.john.amiscaray.backend.framework.web.test.controller.exception;

import io.john.amiscaray.backend.framework.web.application.WebStarter;
import io.john.amiscaray.backend.framework.web.controller.exception.InvalidRequestHandlerException;
import io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.badreturn.TestControllerWithHandlerWithInvalidReturnType;
import io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.nohandlers.TestControllerWithNoHandlers;
import io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.noparams.TestControllerWithHandlerWithNoParams;
import io.john.amiscaray.backend.framework.web.test.controller.exception.stub.controller.voidreturn.TestControllerWithHandlerWithVoidReturn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

public class InvalidRequestHandlerTest {

    @Test
    public void testControllerWithHandlerWithoutValidReturnTypeYieldsInvalidRequestHandlerException() throws ExecutionException, InterruptedException, TimeoutException {
        WebStarter.beginWebApplication(TestControllerWithHandlerWithInvalidReturnType.class, new String[] {})
                .thenRun(Assertions::fail)
                .exceptionally(ex -> {
                    assertThat(ex.getCause(), instanceOf(InvalidRequestHandlerException.class));
                    return null;
                })
                .get(10, TimeUnit.SECONDS);
    }

    @Test
    public void testControllerWithHandlerWithoutParamsYieldsInvalidRequestHandlerException() throws ExecutionException, InterruptedException, TimeoutException {
        WebStarter.beginWebApplication(TestControllerWithHandlerWithNoParams.class, new String[] {})
                .thenRun(Assertions::fail)
                .exceptionally(ex -> {
                    assertThat(ex.getCause(), instanceOf(InvalidRequestHandlerException.class));
                    return null;
                })
                .get(10, TimeUnit.SECONDS);
    }

    @Test
    public void testControllerWithHandlerWithVoidReturnYieldsInvalidRequestHandlerException() throws ExecutionException, InterruptedException, TimeoutException {
        WebStarter.beginWebApplication(TestControllerWithHandlerWithVoidReturn.class, new String[] {})
                .thenRun(Assertions::fail)
                .exceptionally(ex -> {
                    assertThat(ex.getCause(), instanceOf(InvalidRequestHandlerException.class));
                    return null;
                })
                .get(10, TimeUnit.SECONDS);
    }

    @Test
    public void testControllerWithHandlerWithStringParamYieldsInvalidRequestHandlerException() throws ExecutionException, InterruptedException, TimeoutException {
        WebStarter.beginWebApplication(TestControllerWithHandlerWithVoidReturn.class, new String[] {})
                .thenRun(Assertions::fail)
                .exceptionally(ex -> {
                    assertThat(ex.getCause(), instanceOf(InvalidRequestHandlerException.class));
                    return null;
                })
                .get(10, TimeUnit.SECONDS);
    }

    @Test
    public void testApplicationWithControllerWithNoHandlersRunsSuccessfully() throws ExecutionException, InterruptedException, TimeoutException {
        WebStarter.beginWebApplication(TestControllerWithNoHandlers.class, new String[] {})
                .get(10, TimeUnit.SECONDS);
    }

}
