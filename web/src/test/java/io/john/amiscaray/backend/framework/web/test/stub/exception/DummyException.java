package io.john.amiscaray.backend.framework.web.test.stub.exception;

public class DummyException extends RuntimeException{

    public static final String MESSAGE = "You are a dummy";

    public DummyException() {
        super(MESSAGE);
    }
}
