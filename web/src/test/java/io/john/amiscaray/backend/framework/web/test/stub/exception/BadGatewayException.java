package io.john.amiscaray.backend.framework.web.test.stub.exception;

import io.john.amiscaray.backend.framework.web.annotation.MapToStatusCode;
import jakarta.servlet.http.HttpServletResponse;

@MapToStatusCode(HttpServletResponse.SC_BAD_GATEWAY)
public class BadGatewayException extends RuntimeException{

    public static final String MESSAGE = "BAD";

    public BadGatewayException() {
        super(MESSAGE);
    }

}
