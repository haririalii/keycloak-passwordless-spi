package com.mizanwise.keycloak_passwordless_spi;

import com.mizanwise.keycloak_passwordless_spi.otp.OtpChannel;
import com.mizanwise.keycloak_passwordless_spi.otp.OtpRequest;
import com.mizanwise.keycloak_passwordless_spi.otp.OtpSenderProviderFactory;
import com.mizanwise.keycloak_passwordless_spi.otp.OtpSendingException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.authentication.Authenticator;
import org.keycloak.models.*;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.mizanwise.keycloak_passwordless_spi.OtpAuthenticatorFactory.*;

public class OtpAuthenticator implements Authenticator {

    private static final Logger LOG = Logger.getLogger(OtpAuthenticator.class.getName());
    private static final String FAKE_OTP = "929903"; // A fake OTP for testing purposes

    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_OTP = "otp";
    private static final String PARAM_CHANNEL = "channel";

    private static final SecureRandom RND = new SecureRandom();

    @Override
    public void authenticate(AuthenticationFlowContext ctx) {
        process(ctx);
    }

    @Override
    public void action(AuthenticationFlowContext ctx) {
        process(ctx);
    }

    private void process(AuthenticationFlowContext ctx) {
        MultivaluedMap<String, String> form = ctx.getHttpRequest().getDecodedFormParameters();
        String phone = val(form, PARAM_USERNAME);
        String otp = val(form, PARAM_OTP);
        String channel = val(form, PARAM_CHANNEL);

        Map<String, String> cfg =
                Optional.ofNullable(ctx.getAuthenticatorConfig())
                        .map(AuthenticatorConfigModel::getConfig)
                        .orElseGet(Map::of);

        boolean developmentMode = Boolean.parseBoolean(cfg.getOrDefault(PROP_DEVELOPMENT_MODE, "false"));
        int otpLen = parseInt(cfg.getOrDefault(PROP_LEN, "6"), 6);
        int otpExp = parseInt(cfg.getOrDefault(PROP_EXPIRATION_TIME, "5"), 5);
        boolean allowReg = Boolean.parseBoolean(cfg.getOrDefault(PROP_ALLOW_REG, "true"));
        String errUnknown = cfg.getOrDefault(PROP_ERR_UNK, "unknown_user");
        String defaultChannel = cfg.getOrDefault(PROP_DEFAULT_CHANNEL, OtpChannel.SMS.toString());

        // Send it to enter phone page if it's not provided
        if (phone == null) {
            Response page = ctx.form().createForm("phone.ftl");
            ctx.challenge(page);
            return;
        }

        // Send an OTP to the user if it's not provided
        if (otp == null) {
            String code = generateCode(otpLen);
            ctx.getAuthenticationSession().setAuthNote("otp", code);
            ctx.getAuthenticationSession().setAuthNote("otp_issuing_time", Instant.now().toEpochMilli() + "");

            try {
                if (channel == null || channel.isEmpty()) {
                    channel = defaultChannel;
                }
                OtpChannel otpChannel = OtpChannel.fromString(channel.trim().toUpperCase());
                if (otpChannel == null) {
                    LOG.errorf("Unknown OTP channel: %s", channel);
                    Response page = ctx.form().createForm("phone.ftl");
                    ctx.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, page);
                    return;
                }

                OtpSenderProviderFactory.getInstance(cfg).get(otpChannel)
                        .sendOtp(
                                OtpRequest.builder()
                                        .destination(phone)
                                        .code(code)
                                        .build()
                        );
            } catch (Exception e) {
                LOG.errorf("Failed to send OTP to %s: %s", phone, e.getMessage());
                Response page = ctx.form().createForm("phone.ftl");
                ctx.failureChallenge(AuthenticationFlowError.INTERNAL_ERROR, page);
                return;
            }

            Response page = ctx.form()
                    .setAttribute("phone", phone)
                    .createForm("otp.ftl");
            ctx.challenge(page);

            return;
        }

        // Verify the OTP real stored or fake OTP
        String expected = ctx.getAuthenticationSession().getAuthNote("otp");
        String issuingTime = ctx.getAuthenticationSession().getAuthNote("otp_issuing_time");
        Instant issuedAt = Instant.ofEpochMilli(Long.parseLong(issuingTime));
        boolean isExpired = Instant.now().plus(otpExp, ChronoUnit.MINUTES)
                .isBefore(issuedAt);
        boolean valid = otp.equals(expected) || (developmentMode && FAKE_OTP.equals(otp));
        if (!valid || isExpired) {
            Response page = ctx.form()
                    .setAttribute("phone", phone)
                    .setError("Invalid code")
                    .createForm("otp.ftl");
            ctx.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, page);
            return;
        }

        // Lookup for user or create a new user
        UserProvider users = ctx.getSession().users();
        RealmModel realm = ctx.getRealm();
        UserModel user = users.getUserByUsername(realm, phone);

        if (user == null) {
            if (!allowReg) {
                ctx.failureChallenge(AuthenticationFlowError.UNKNOWN_USER,
                        json(ctx, 401, "UNKNOWN_USER", errUnknown));
                return;
            }
            user = users.addUser(realm, phone);
            user.setEnabled(true);
            user.setUsername(phone);
            user.setAttribute("phone_number", List.of(phone));
        }

        ctx.setUser(user);
        ctx.success();
    }

    private static Response json(AuthenticationFlowContext ctx, int status, String action, String code) {
        return Response.status(status)
                .entity(Map.of("action", action, "error", code,
                        "execution", ctxExecution(ctx),
                        "tab_id", ctxTab(ctx)))
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private static String ctxExecution(AuthenticationFlowContext ctx) {
        return ctx.getExecution().getId();
    }

    private static String ctxTab(AuthenticationFlowContext ctx) {
        return ctx.getAuthenticationSession().getTabId();
    }

    private static String val(MultivaluedMap<String, String> m, String key) {
        return m != null ? m.getFirst(key) : null;
    }

    private static String generateCode(int digits) {
        int max = (int) Math.pow(10, digits);
        return String.format("%0" + digits + 'd', RND.nextInt(max));
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    @Override
    public boolean requiresUser() {
        return false;
    }

    @Override
    public boolean configuredFor(KeycloakSession s, RealmModel r, UserModel u) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession s, RealmModel r, UserModel u) {
    }

    @Override
    public void close() {
    }
}
