async function ResenCode() {
    const response = await fetch("ResendCode");
    const resendBtn = document.getElementById("resendBtn");
    const Json = await response.json();

    if (Json.status) {
        let timeLeft = 60;
        const countdown = setInterval(() => {
        timeLeft--;
        resendBtn.textContent = `Resend Code In (${timeLeft}s)`;
        resendBtn.disabled = true;
        if (timeLeft <= 0) {
            clearInterval(countdown);
            resendBtn.disabled = false;
            resendBtn.textContent = `Resend Code`;
        }
    }, 1000);
        
    }

}


