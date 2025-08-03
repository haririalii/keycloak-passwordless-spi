package com.mizanwise.keycloak_passwordless_spi.sms;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mizanwise.keycloak_passwordless_spi.OtpAuthenticatorFactory.PROP_DEFAULT_SMS_PROVIDER;

/**
 * Simple factory for KavenegarSmsSenderProvider.
 */
public class SmsSenderProviderFactory {

    private final ConcurrentHashMap<SmsProviderType, SmsSenderProvider> providers = new ConcurrentHashMap<>();

    private SmsSenderProviderFactory(Map<String, String> cfg) {
        providers.put(SmsProviderType.CONSOLE, new ConsoleSmsSenderProvider());
        providers.put(SmsProviderType.KAVENEGAR, new KavenegarSmsSenderProvider(cfg));
    }

    private SmsSenderProvider get(String provider) {
        if (provider == null || provider.isEmpty()) {
            return null;
        }
        SmsProviderType smsProviderType = SmsProviderType.valueOf(provider.trim().toLowerCase());
        return get(smsProviderType);
    }

    private SmsSenderProvider get(SmsProviderType type) {
        SmsSenderProvider provider = providers.get(type);
        if (provider == null) {
            throw new IllegalArgumentException("Unknown SMS provider type: " + type);
        }
        return provider;
    }

    public static SmsSenderProvider getInstance(String provider, Map<String, String> cfg) {
        if (provider == null || provider.isEmpty()) {
            provider = cfg.getOrDefault(PROP_DEFAULT_SMS_PROVIDER, "console");
        }
        return new SmsSenderProviderFactory(cfg).get(provider);
    }
}
