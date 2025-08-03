package com.mizanwise.keycloak_passwordless_spi.otp;

public interface OtpSenderProvider {
    void sendOtp(OtpRequest request) throws OtpSendingException;
}
