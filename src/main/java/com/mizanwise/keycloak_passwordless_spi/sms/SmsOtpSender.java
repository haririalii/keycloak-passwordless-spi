package com.mizanwise.keycloak_passwordless_spi.sms;

import com.mizanwise.keycloak_passwordless_spi.otp.*;

import java.util.Map;

import static com.mizanwise.keycloak_passwordless_spi.OtpAuthenticatorFactory.PROP_DEFAULT_SMS_PROVIDER;
import static com.mizanwise.keycloak_passwordless_spi.OtpAuthenticatorFactory.PROP_SMS_TPL;

public class SmsOtpSender implements OtpSenderProvider {
    private static final String ARG_CODE = "{code}";

    private final SmsSenderProvider delegate;
    private final String template;

    public SmsOtpSender(Map<String, String> cfg) {
        String smsTemplate = cfg.getOrDefault(PROP_SMS_TPL, "Your code is {code}");
        String smsProviderName = cfg.getOrDefault(PROP_DEFAULT_SMS_PROVIDER, "console");

        this.delegate = SmsSenderProviderFactory.getInstance(smsProviderName, cfg);
        this.template = smsTemplate;
    }

    @Override
    public void sendOtp(OtpRequest request) throws OtpSendingException {
        try {
            String message = template.replace(ARG_CODE, request.getCode());
            delegate.sendSms(request.getDestination(), message);
        } catch (Exception e) {
            throw new OtpSendingException("Failed to send SMS OTP", e);
        }
    }
}
