package com.mizanwise.keycloak_passwordless_spi.otp;

import java.time.Instant;

public class OtpRequest {
    private final String destination; // phone or email
    private final String code;

    private OtpRequest(Builder b) {
        this.destination = b.destination;
        this.code = b.code;
    }

    public String getDestination() {
        return destination;
    }

    public String getCode() {
        return code;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String destination;
        private String code;

        public Builder destination(String destination) {
            this.destination = destination;
            return this;
        }

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public OtpRequest build() {
            if (destination == null) throw new IllegalStateException("destination required");
            if (code == null) throw new IllegalStateException("code required");
            return new OtpRequest(this);
        }
    }
}
