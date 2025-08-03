package com.mizanwise.keycloak_passwordless_spi.sms;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;

import static com.mizanwise.keycloak_passwordless_spi.OtpAuthenticatorFactory.PROP_SMS_KAVENEGAR_API_KEY;
import static com.mizanwise.keycloak_passwordless_spi.OtpAuthenticatorFactory.PROP_SMS_KAVENEGAR_SENDER;

/**
 * SMS sender backed by a direct HTTP call to Kavenegar’s REST API.
 */
public class KavenegarSmsSenderProvider implements SmsSenderProvider {
    private static final Logger LOG = Logger.getLogger(KavenegarSmsSenderProvider.class.getName());

    private final String apiKey;
    private final String sender;

    public KavenegarSmsSenderProvider(Map<String, String> cfg) {
        String apiKey = cfg.getOrDefault(PROP_SMS_KAVENEGAR_API_KEY, "Your code is {code}");
        String senderLine = cfg.getOrDefault(PROP_SMS_KAVENEGAR_SENDER, "");

        this.apiKey = apiKey;
        this.sender = senderLine;
    }

    @Override
    public void sendSms(String phoneNumber, String message) {
        try {
            String encodedPhone = URLEncoder.encode(phoneNumber, StandardCharsets.UTF_8);
            String encodedSender = URLEncoder.encode(sender, StandardCharsets.UTF_8);
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

            String url = String.format(
                    "https://api.kavenegar.com/v1/%s/sms/send.json" +
                            "?receptor=%s&sender=%s&message=%s",
                    URLEncoder.encode(apiKey, StandardCharsets.UTF_8),
                    encodedPhone,
                    encodedSender,
                    encodedMessage
            );

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            LOG.info("Kavenegar SMS response code: "
                    + response.statusCode() + ", body: " + response.body());
            if (response.statusCode() != 200) {
                throw new IOException("Non-200 from Kavenegar: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            LOG.severe("Failed to send SMS via Kavenegar: " + e.getMessage());
            throw new RuntimeException("Kavenegar sendSms failed", e);
        }
    }

    @Override
    public void sendSmsTemplate(String phoneNumber, String templateName, Object... args) {
        // If you need template support, you can call:
        // https://api.kavenegar.com/v1/{apiKey}/sms/send.json
        // with &template={templateName}&receptor=...&message={JSON args}
        LOG.warning("sendSmsTemplate is not implemented yet");
    }
}
