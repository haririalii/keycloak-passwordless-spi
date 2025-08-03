package com.mizanwise.keycloak_passwordless_spi.email;

public interface EmailSenderProvider {
    void sendEmail(EmailMessage message) throws EmailSendingException;
}
