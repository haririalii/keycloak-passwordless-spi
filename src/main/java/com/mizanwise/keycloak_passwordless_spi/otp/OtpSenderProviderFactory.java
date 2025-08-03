package com.mizanwise.keycloak_passwordless_spi.otp;

import com.mizanwise.keycloak_passwordless_spi.sms.SmsOtpSender;
import com.mizanwise.keycloak_passwordless_spi.sms.SmsSenderProvider;
import com.mizanwise.keycloak_passwordless_spi.sms.SmsSenderProviderFactory;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static com.mizanwise.keycloak_passwordless_spi.OtpAuthenticatorFactory.*;

public class OtpSenderProviderFactory {
    // channel -> provider, immutable after construction for thread safety
    private final Map<OtpChannel, OtpSenderProvider> providers;
    private final Map<String, String> config;

    private OtpSenderProviderFactory(Map<String, String> cfg) {
        this.config = Collections.unmodifiableMap(new HashMap<>(cfg));
        EnumMap<OtpChannel, OtpSenderProvider> map = new EnumMap<>(OtpChannel.class);

        // Dynamically register OTP sender providers based on configuration
        map.put(OtpChannel.SMS, new SmsOtpSender(cfg));

        // Future: register other channel providers here (EMAIL, PUSH, etc.)
        // e.g., map.put(OtpChannel.EMAIL, new EmailOtpSender(...));

        this.providers = Collections.unmodifiableMap(map);
    }

    /**
     * Build a new factory instance on demand using provided configuration.
     */
    public static OtpSenderProviderFactory getInstance(Map<String, String> cfg) {
        return new OtpSenderProviderFactory(cfg);
    }

    /**
     * Lookup provider by channel. Returns null if none registered.
     */
    public OtpSenderProvider get(OtpChannel channel) {
        return providers.get(channel);
    }

    /**
     * Expose the configuration used to build this factory.
     */
    public Map<String, String> getConfig() {
        return config;
    }

    /**
     * Snapshot of registered providers.
     */
    public Map<OtpChannel, OtpSenderProvider> snapshotProviders() {
        return providers;
    }
}
