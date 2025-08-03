package com.mizanwise.keycloak_passwordless_spi;

import org.keycloak.Config;
import org.keycloak.authentication.AuthenticatorFactory;
import org.keycloak.models.AuthenticationExecutionModel.Requirement;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

public class OtpAuthenticatorFactory implements AuthenticatorFactory {

    public static final String PROVIDER_ID = "passwordless-otp-authenticator";

    /* property keys */
    public static final String PROP_DEVELOPMENT_MODE = "mode.development";
    public static final String PROP_LEN = "otp.length";
    public static final String PROP_EXPIRATION_TIME = "otp.expiration_time";
    public static final String PROP_SMS_TPL = "sms.template";
    public static final String PROP_ALLOW_REG = "allow.user.registration";
    public static final String PROP_ERR_UNK = "error.unknown_user";
    public static final String PROP_DEFAULT_CHANNEL = "otp.default_channel";           // "SMS" or "EMAIL"
    public static final String PROP_DEFAULT_SMS_PROVIDER = "otp.default_provider.sms"; // e.g., "twilio" or "mock"
    public static final String PROP_DEFAULT_EMAIL_PROVIDER = "otp.default_provider.email"; // e.g., "smtp" or "console"
    public static final String PROP_SMS_KAVENEGAR_API_KEY = "sms.kavenegar.api_key";
    public static final String PROP_SMS_KAVENEGAR_SENDER = "sms.kavenegar.sender";

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String getDisplayType() {
        return "Dynamic OTP Authenticator";
    }

    @Override
    public String getReferenceCategory() {
        return "passwordless";
    }

    @Override
    public String getHelpText() {
        return "Passwordless login via dynamic OTP channel/provider (SMS/Email/etc.)";
    }

    @Override
    public Requirement[] getRequirementChoices() {
        return new Requirement[]{Requirement.REQUIRED, Requirement.ALTERNATIVE};
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        List<ProviderConfigProperty> props = new ArrayList<>();

        ProviderConfigProperty developmentMode = new ProviderConfigProperty();
        developmentMode.setName(PROP_DEVELOPMENT_MODE);
        developmentMode.setLabel("Development mode");
        developmentMode.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        developmentMode.setDefaultValue("false");
        developmentMode.setHelpText("Mode that indicate SPI is used for development/testing purposes");
        props.add(developmentMode);

        ProviderConfigProperty len = new ProviderConfigProperty();
        len.setName(PROP_LEN);
        len.setLabel("OTP length");
        len.setType(ProviderConfigProperty.STRING_TYPE);
        len.setDefaultValue("6");
        len.setHelpText("Number of digits in generated OTP");
        props.add(len);

        ProviderConfigProperty exp = new ProviderConfigProperty();
        exp.setName(PROP_EXPIRATION_TIME);
        exp.setLabel("Expiration (minutes)");
        exp.setType(ProviderConfigProperty.NUMBER_TYPE);
        exp.setDefaultValue("5");
        exp.setHelpText("Validity duration of the OTP in minutes");
        props.add(exp);

        ProviderConfigProperty tpl = new ProviderConfigProperty();
        tpl.setName(PROP_SMS_TPL);
        tpl.setLabel("Template");
        tpl.setType(ProviderConfigProperty.STRING_TYPE);
        tpl.setDefaultValue("Your code is {code}");
        tpl.setHelpText("Template used when sending code, {code} is replaced");
        props.add(tpl);

        ProviderConfigProperty reg = new ProviderConfigProperty();
        reg.setName(PROP_ALLOW_REG);
        reg.setLabel("Auto-register unknown users");
        reg.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        reg.setDefaultValue("true");
        reg.setHelpText("If true, unknown user identifiers will be auto-registered");
        props.add(reg);

        ProviderConfigProperty err = new ProviderConfigProperty();
        err.setName(PROP_ERR_UNK);
        err.setLabel("Unknown user error code");
        err.setType(ProviderConfigProperty.STRING_TYPE);
        err.setDefaultValue("unknown_user");
        err.setHelpText("Error code when user is unknown and registration disallowed");
        props.add(err);

        ProviderConfigProperty defaultChannel = new ProviderConfigProperty();
        defaultChannel.setName(PROP_DEFAULT_CHANNEL);
        defaultChannel.setLabel("Default channel");
        defaultChannel.setType(ProviderConfigProperty.STRING_TYPE);
        defaultChannel.setDefaultValue("SMS");
        defaultChannel.setHelpText("Fallback channel if request doesn’t specify one");
        props.add(defaultChannel);

        ProviderConfigProperty defaultSms = new ProviderConfigProperty();
        defaultSms.setName(PROP_DEFAULT_SMS_PROVIDER);
        defaultSms.setLabel("Default SMS provider");
        defaultSms.setType(ProviderConfigProperty.STRING_TYPE);
        defaultSms.setDefaultValue("console");
        defaultSms.setHelpText("Provider name to use for SMS if not overridden per-request");
        props.add(defaultSms);

        ProviderConfigProperty defaultEmail = new ProviderConfigProperty();
        defaultEmail.setName(PROP_DEFAULT_EMAIL_PROVIDER);
        defaultEmail.setLabel("Default Email provider");
        defaultEmail.setType(ProviderConfigProperty.STRING_TYPE);
        defaultEmail.setDefaultValue("console");
        defaultEmail.setHelpText("Provider name to use for Email if not overridden per-request");
        props.add(defaultEmail);

        return props;
    }

    @Override
    public void init(Config.Scope scope) { }

    @Override
    public void postInit(org.keycloak.models.KeycloakSessionFactory factory) { }

    @Override
    public void close() { }

    @Override
    public org.keycloak.authentication.Authenticator create(org.keycloak.models.KeycloakSession session) {
        return new OtpAuthenticator();
    }
}
