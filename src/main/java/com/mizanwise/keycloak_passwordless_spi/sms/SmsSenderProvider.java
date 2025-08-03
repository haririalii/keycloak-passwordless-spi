package com.mizanwise.keycloak_passwordless_spi.sms;

/**
 * SPI interface for sending SMS messages.
 */
public interface SmsSenderProvider {
    void sendSms(String phoneNumber, String message);
    void sendSmsTemplate(String phoneNumber, String templateName, Object... args);
}
