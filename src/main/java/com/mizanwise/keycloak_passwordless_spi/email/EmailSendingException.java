package com.mizanwise.keycloak_passwordless_spi.email;

public class EmailSendingException extends Exception {
    public EmailSendingException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public EmailSendingException(String msg) {
        super(msg);
    }
}
