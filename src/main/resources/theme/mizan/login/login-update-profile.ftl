<#ftl encoding="UTF-8">
<#assign dir="${(locale.rtl)?then('rtl','ltr')}">
<html lang="${locale!}" dir="${dir}">
<head>
    <meta charset="UTF-8">
    <title>${msg("updateProfileTitle")!}</title>
    <link rel="stylesheet" href="${url.resourcesPath}/css/otp.css">
</head>
<body>
<div class="profile-form">
    <div class="form-header">
        <h2>${msg("updateProfileTitle")!}</h2>
        <p>${msg("updateProfileInstruction")!}</p>
    </div>

    <#-- Show validation error summary (if any) -->
    <#if errorMessage??>
        <p class="error">${errorMessage}</p>
    </#if>

    <form id="kc-update-profile-form" action="${url.loginAction}" method="post">
        <input type="hidden" name="execution" value="${execution}"/>
        <input type="hidden" name="username" value="${username!}"/>

        <div class="input-row">
            <label for="firstName">${msg("firstName")!} *</label><br/>
            <input type="text"
                   id="firstName"
                   name="firstName"
                   class="input-phone"
                   placeholder="${msg("profile.firstname.placeholder")!}"
                   value="${user.firstName!}"
                   required/>

            <#-- Inline error for firstName, if Keycloak reports it -->
            <#if formErrors?has_content && formErrors["firstName"]??>
                <p class="error">${formErrors["firstName"]?first}</p>
            </#if>
        </div>

        <div class="input-row">
            <label for="lastName">${msg("lastName")!} *</label><br/>
            <input type="text"
                   id="lastName"
                   name="lastName"
                   class="input-phone"
                   placeholder="${msg("profile.lastname.placeholder")!}"
                   value="${user.lastName!}"
                   required/>

            <#if formErrors?has_content && formErrors["lastName"]??>
                <p class="error">${formErrors["lastName"]?first}</p>
            </#if>
        </div>

        <div class="input-row">
            <label for="email">${msg("email")!} *</label><br/>
            <input type="email"
                   id="email"
                   name="email"
                   class="input-phone"
                   placeholder="${msg("profile.email.placeholder")!}"
                   value="${user.email!}"
                   required/>

            <#if formErrors?has_content && formErrors["email"]??>
                <p class="error">${formErrors["email"]?first}</p>
            </#if>
        </div>

        <div class="btn-wrap">
            <button type="submit">${msg("doSubmit")!}</button>
        </div>
    </form>
</div>
</body>
</html>
