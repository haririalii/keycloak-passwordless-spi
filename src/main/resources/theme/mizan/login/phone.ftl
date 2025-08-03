<#ftl encoding="UTF-8">
<#assign dir="${(locale.rtl)?then('rtl','ltr')}">

<!DOCTYPE html>
<html lang="${locale!}" dir="ltr">
<head>
    <meta charset="UTF-8">
    <title>${msg("phone.title")}</title>

    <link rel="stylesheet" href="${url.resourcesPath}/css/otp.css">
</head>
<body>

<div class="phone-form">
    <div class="form-header">
        <h2>${msg("phone.heading")}</h2>
    </div>

    <#-- Show any Keycloak error message inside the form box -->
    <#if message??>
        <p class="error">${kcSanitize(message.summary!)}</p>
    </#if>

    <form action="${url.loginAction}" method="post">
        <input type="hidden" name="execution" value="${execution}">

        <div class="credential-wrap">
            <select name="dial_code" class="select-dd">
                <option value="+98">+98</option>
            </select>

            <input type="text"
                   name="username"
                   class="input-phone"
                   placeholder="${msg("phone.placeholder")}"
                   required>
        </div>

        <div class="btn-wrap">
            <button type="submit">${msg("send.otp")}</button>
        </div>
    </form>
</div>

</body>
</html>
