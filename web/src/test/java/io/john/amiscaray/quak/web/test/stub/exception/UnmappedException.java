package io.john.amiscaray.quak.web.test.stub.exception;

public class UnmappedException extends RuntimeException{

    public static String MESSAGE = "You should not see this";

    public UnmappedException() {
        super(MESSAGE);
    }
}
