package com.mizanwise.keycloak_passwordless_spi.email;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EmailMessage {
    private final String from;
    private final String to;
    private final String subject;
    private final String body;
    private final List<String> cc;
    private final List<String> bcc;
    private final String replyTo;

    private EmailMessage(Builder b) {
        this.from = b.from;
        this.to = b.to;
        this.subject = b.subject;
        this.body = b.body;
        this.cc = b.cc != null ? b.cc : Collections.emptyList();
        this.bcc = b.bcc != null ? b.bcc : Collections.emptyList();
        this.replyTo = b.replyTo;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public List<String> getCc() {
        return cc;
    }

    public List<String> getBcc() {
        return bcc;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String from;
        private String to;
        private String subject;
        private String body;
        private List<String> cc;
        private List<String> bcc;
        private String replyTo;

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public Builder cc(List<String> cc) {
            this.cc = cc;
            return this;
        }

        public Builder bcc(List<String> bcc) {
            this.bcc = bcc;
            return this;
        }

        public Builder replyTo(String replyTo) {
            this.replyTo = replyTo;
            return this;
        }

        public EmailMessage build() {
            return new EmailMessage(this);
        }
    }
}
