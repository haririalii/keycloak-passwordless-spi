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

## Build

Using **Maven**:

```bash
mvn clean package
```

Install the resulting JAR into Keycloak's `providers/` directory (or use the extension mechanism for your version), then restart Keycloak.

## Authenticator Setup

1. In Keycloak admin console, go to **Authentication → Flows**.  
2. Create or edit a flow and add **Passwordless OTP Authenticator** (provider id: `passwordless-otp-authenticator`).  
3. Configure its execution (e.g., REQUIRED or ALTERNATIVE).  
4. Click the cog to configure the authenticator. Available config properties:

| Property Key                 | Description                                                 | Default               |
| ---------------------------- | ----------------------------------------------------------- | --------------------- |
| `mode.development`           | Enable development/test mode allowing fake OTP              | `false`               |
| `otp.length`                 | Number of digits in generated OTP                           | `6`                   |
| `otp.expiration_time`        | OTP validity window in minutes                              | `5`                   |
| `sms.template`               | Template for OTP message (`{code}` placeholder)             | `Your code is {code}` |
| `allow.user.registration`    | Auto-create user if unknown                                 | `true`                |
| `error.unknown_user`         | Error code when user is unknown and registration disallowed | `unknown_user`        |
| `otp.default_channel`        | Fallback channel when none specified (`SMS` or `EMAIL`)     | `SMS`                 |
| `otp.default_provider.sms`   | Default SMS provider name (e.g., `console`, `kavenegar`)    | `console`             |
| `otp.default_provider.email` | Default Email provider name (e.g., `console`, `smtp`)       | `console`             |
| `sms.kavenegar.api_key`      | Kavenegar API key (if using Kavenegar)                      | *(none)*              |
| `sms.kavenegar.sender`       | Sender line for Kavenegar                                   | *(none)*              |

## Form Parameters

During login/authentication requests, the following form parameters are used:

- `username` — destination identifier (phone number or email)  
- `otp` — the one-time code (when verifying)  
- `channel` — optional override of the channel (`SMS`, `EMAIL`, case-insensitive)  

Example to force email: include `channel=EMAIL` in the request.

## Runtime Behavior

1. User supplies `username` (phone or email).  
2. If no `otp` is provided, an OTP is generated and stored in the authentication session along with its issuance timestamp.  
3. Channel is resolved: request `channel` override or fallback to `otp.default_channel`.  
4. Provider is resolved dynamically via `OtpSenderProviderFactory.getInstance(cfg)` for that channel. 
5. OTP is dispatched through the resolved `OtpSenderProvider`.  
6. User submits the `otp`; authenticator validates it with expiration logic and, if in development mode, allows the configured fake OTP (`929903`).  
7. User lookup or creation happens based on `allow.user.registration`.  
8. Authentication succeeds if OTP is valid and user is present/created.

## Key Classes & Interfaces

### `OtpAuthenticator`

Handles request processing: generation, sending, and verification of OTP. Supports dynamic channel resolution and fake OTP in development mode.

### `OtpAuthenticatorFactory`

Keycloak factory that exposes configuration properties and creates `OtpAuthenticator` instances. Provider ID: `passwordless-otp-authenticator`.

### `OtpSenderProviderFactory`

Builds provider instances on demand from the configuration map. Dynamic—different named providers (e.g., `kavenegar`, `console`) can be requested at runtime without a global hardcoded instance.

### `OtpRequest`

Immutable request object used when sending an OTP. Built via builder. Contains destination, code, expiration context is managed externally.

### `OtpChannel`

Enum representing available channels (e.g., `SMS`, `EMAIL`), with parsing helpers.


## Extending Providers

To add new providers:

1. Implement `OtpSenderProvider` for the new backend (e.g., new SMS service, email, push).  
2. Support it in the corresponding factory (e.g., SMS factory picks by name).  
3. Use `otp.default_provider.*` form authenticator config to select it.

## Testing

- Use the built-in console providers to avoid real external calls during development.  
- Enable `mode.development=true` to test bypass with the fake OTP.  
- Write unit tests by mocking `OtpSenderProvider` and verifying `OtpRequest` contents.

## Troubleshooting

- Ensure `otp_issuing_time` is stored and parsed correctly; incorrect time leads to expiration logic failures.  
- Check that the resolved provider for the requested channel exists; missing provider will lead to internal errors.  
- Validate form parameters are named correctly (`username`, `otp`, `channel`).
