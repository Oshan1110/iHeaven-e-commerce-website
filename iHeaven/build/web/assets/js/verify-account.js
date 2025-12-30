async function verifyAccount() {
    const verificationCode = document.getElementById("verificationCode").value;
    const verification = {
        verificationCode: verificationCode
    };

    const verificationJson = JSON.stringify(verification);
    const response = await fetch(
            "VerifyAccount",
            {
                method: "POST",
                body: verificationJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            window.location = 'index.html';
        } else {
//            document.getElementById("message").innerHTML = json.message;
            sweetAlert("Error", json.message, "error");
        }
    } else {
//        document.getElementById("message").innerHTML = "Registraion failed. Please try again!"
        sweetAlert("Error", "Registraion failed. Please try again!", "error");
    }
}
async function ResenCode() {
    const response = await fetch("ResendCode");
    const Json = await response.json();

    if (Json.status) {
        startCountdown();
    }

}
async function startCountdown() {
    const resendBtn = document.getElementById('resendBtn');
    const countdownElement = document.getElementById('countdown');
    let seconds = 60;
    resendBtn.disabled = true;
    const countdownInterval = setInterval(() => {
        seconds--;
        countdownElement.textContent = seconds;
        if (seconds <= 0) {
            clearInterval(countdownInterval);
            resendBtn.disabled = false;
            countdownElement.textContent = '';
        }
    }, 1000);

}


