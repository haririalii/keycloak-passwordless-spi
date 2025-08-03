# Keycloak Passwordless SPI (OTP) — Dynamic Channel & Provider Framework

A **fully dynamic, pluggable passwordless authentication SPI for Keycloak** that sends OTPs over arbitrary channels (SMS, Email, etc.) with runtime channel/provider resolution. The request can specify both channel and provider.

## Features

- Passwordless login via OTP (one-time code)
- Dynamic channel resolution (e.g., `SMS`, `EMAIL`) per request
- Pluggable OTP sender providers (twilio, kavenegar, SMTP, console/mock, etc.)
- Per-request override of channel and provider (`channel`, `provider` form fields)
- Default channel/provider from Keycloak authenticator configuration
- Development mode with fake OTP bypass
- Extensible: add new channels (push, voice) by implementing `OtpSenderProvider`
- Thread-safe, on-demand provider factory

## Prerequisites

- Java 8+ (compatible with your Keycloak version)
- Keycloak (compatible version; tested with Keycloak 20+ but adapt as needed)
- Build tool: Maven

## Quickstart

### 1. Build

If using **Maven**:

```bash
mvn clean package
