package io.john.amiscaray.quak.security.auth.exception;

public class InvalidCredentialsException extends Exception{

    public InvalidCredentialsException() {
        super("The given credentials are invalid.");
    }

}
