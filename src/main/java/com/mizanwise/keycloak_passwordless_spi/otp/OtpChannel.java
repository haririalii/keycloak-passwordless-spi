package com.mizanwise.keycloak_passwordless_spi.otp;

public enum OtpChannel {
    SMS,
    EMAIL;

    public static OtpChannel fromString(String s) {
        if (s == null) {
            return null;
        }
        try {
            return OtpChannel.valueOf(s.trim().toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }
}
