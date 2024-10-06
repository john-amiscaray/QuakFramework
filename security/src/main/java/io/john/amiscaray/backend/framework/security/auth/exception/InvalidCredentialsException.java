package io.john.amiscaray.backend.framework.security.auth.exception;

public class InvalidCredentialsException extends Exception{

    public InvalidCredentialsException() {
        super("The given credentials are invalid.");
    }

}
