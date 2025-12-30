async function otpVerify() {
    const otpCode = document.getElementById("code").value;
    const otp = {
        otpCode: otpCode
    };
    const otpJson = JSON.stringify(otp);
    const response = await fetch(
            "otpProcess",
            {
                method: "POST",
                body: otpJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            window.location = 'forgot-password.html'
        } else {
//            document.getElementById("message").innerHTML = json.message;
            sweetAlert("Error", json.message, "error");
        }
    } else {
//        document.getElementById("message").innerHTML = "Registraion failed. Please try again!"
        sweetAlert("Error", "Registraion failed. Please try again!", "error");
    }
}

