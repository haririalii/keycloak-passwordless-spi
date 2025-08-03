package com.mizanwise.keycloak_passwordless_spi.otp;

public class OtpSendingException extends Exception {
    public OtpSendingException(String msg) { super(msg); }
    public OtpSendingException(String msg, Throwable cause) { super(msg, cause); }
}
