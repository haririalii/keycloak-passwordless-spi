<#ftl encoding="UTF-8">
<#assign dir = (locale?? && locale?starts_with("fa"))?then("rtl","ltr")>

<!DOCTYPE html>
<html lang="${locale!}" dir="${dir}">
<head>
    <meta charset="UTF-8">
    <title>${msg("profile.title")}</title>

    <link rel="stylesheet" href="${url.resourcesPath}/css/otp.css">
</head>
<body>

<div class="profile-form">
    <div class="form-header">
        <h2>${msg("profile.heading")}</h2>
    </div>

    <#-- show Keycloak error, if any -->
    <#if message??>
        <p class="error">${kcSanitize(message.summary!)}</p>
    </#if>

    <form action="${url.loginAction}" method="post">
        <input type="hidden" name="execution" value="${execution}">
        <input type="hidden" name="username" value="${username!}">

        <div class="profile-wrap">

            <div class="input-row">
                <label for="first_name">${msg("profile.firstname.label")}</label><br/>
                <input type="text"
                       id="first_name"
                       name="first_name"
                       class="input-phone"
                       placeholder="${msg("profile.firstname.placeholder")}"
                       required>
            </div>

            <div class="input-row">
                <label for="last_name">${msg("profile.lastname.label")}</label><br/>
                <input type="text"
                       id="last_name"
                       name="last_name"
                       class="input-phone"
                       placeholder="${msg("profile.lastname.placeholder")}"
                       required>
            </div>

            <div class="input-row">
                <label for="email">${msg("profile.email.label")}</label><br/>
                <input type="email"
                       id="email"
                       name="email"
                       class="input-phone"
                       placeholder="${msg("profile.email.placeholder")}"
                       required>
            </div>

        </div>

        <div class="btn-wrap">
            <button type="submit">${msg("profile.submit")}</button>
        </div>
    </form>
</div>

</body>
</html>