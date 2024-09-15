package io.john.amiscaray.auth.exception;

public class InvalidCredentialsException extends Exception{

    public InvalidCredentialsException() {
        super("The given credentials are invalid.");
    }

}
