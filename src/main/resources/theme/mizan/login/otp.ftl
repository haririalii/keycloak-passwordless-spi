<#ftl encoding="UTF-8">
<#assign dir="${(locale.rtl)?then('rtl','ltr')}">

<!DOCTYPE html>
<html lang="${locale!}" dir="ltr">
<head>
    <meta charset="UTF-8">
    <title>${msg("otp.title")}</title>

    <link rel="stylesheet" href="${url.resourcesPath}/css/otp.css">
</head>
<body>

<div class="otp-form">
    <div class="form-header">
        <h4>${msg("otp.heading")}</h4>
        <p>${msg("otp.heading")}</p> <#-- use same key (or add a new one) -->
    </div>

    <#-- show any realm/user-level error -->
    <#if message??>
        <p class="error">${kcSanitize(message.summary!)}</p>
    </#if>

    <form action="${url.loginAction}" method="post">
        <input type="hidden" name="execution" value="${execution}">
        <input type="hidden" name="username" value="${username!}">
        <input type="hidden" id="otp-field" name="otp">

        <div class="otp-wrap">
            <#list 1..6 as i>
                <input type="text"
                       class="code-input"
                       autocomplete="one-time-code"
                       required maxlength="1">
            </#list>
        </div>

        <div class="btn-wrap">
            <button type="submit">${msg("verify")}</button>
        </div>
    </form>

    <p><a href="#" class="resend-link">${msg("resend")}</a></p>
</div>

<script>
    const inputs = Array.from(document.querySelectorAll('.code-input'));
    const hidden = document.getElementById('otp-field');

    function updateHidden() {
        hidden.value = inputs.map(i => i.value || '').join('');
    }

    inputs.forEach((input, idx) => {
        input.addEventListener('input', e => {
            const v = e.target.value.replace(/\D/g, '');
            if (!v) {
                e.target.value = '';
                return;
            }
            e.target.value = v[0];
            if (idx < inputs.length - 1) inputs[idx + 1].focus();
            updateHidden();
        });

        input.addEventListener('keydown', e => {
            if (e.key === 'Backspace' && !e.target.value && idx > 0)
                inputs[idx - 1].focus();
        });

        input.addEventListener('paste', e => {
            e.preventDefault();
            const paste = (e.clipboardData.getData('text') || '')
                .replace(/\D/g, '').slice(0, inputs.length);
            paste.split('').forEach((ch, i) => inputs[i].value = ch);
            inputs[Math.min(paste.length, inputs.length) - 1].focus();
            updateHidden();
        });
    });

    document.querySelector('form')
        .addEventListener('submit', updateHidden);
</script>

</body>
</html>
