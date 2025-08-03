package com.mizanwise.keycloak_passwordless_spi.sms;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;

import static com.mizanwise.keycloak_passwordless_spi.OtpAuthenticatorFactory.PROP_SMS_KAVENEGAR_API_KEY;
import static com.mizanwise.keycloak_passwordless_spi.OtpAuthenticatorFactory.PROP_SMS_KAVENEGAR_SENDER;

/**
 * SMS sender backed by a direct HTTP call to Kavenegar’s REST API.
 */
public class ConsoleSmsSenderProvider implements SmsSenderProvider {
    private static final Logger LOG = Logger.getLogger(ConsoleSmsSenderProvider.class.getName());

    public ConsoleSmsSenderProvider() {

    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        LOG.info("Sending SMS to " + phoneNumber + ": " + message);
    }

    @Override
    public void sendSmsTemplate(String phoneNumber, String templateName, Object... args) {
        LOG.info("Sending SMS with template '" + templateName + "' to " + phoneNumber + " with args: {}"+ Arrays.toString(args));
    }
}
