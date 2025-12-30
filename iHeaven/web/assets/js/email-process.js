function field() {
    const code = document.getElementById("code").disabled = true;
    const verifyBtn = document.getElementById("verifyBtn").disabled = true;
    const email = document.getElementById("email").disabled = false;
    const email_btn = document.getElementById("email-btn").disabled = false;
}

async function sendEmail() {
    const email = document.getElementById("email").value;
    const emailSent = {
        email: email
    };
    const emailJson = JSON.stringify(emailSent);
    const response = await fetch(
            "EmailProcess",
            {
                method: "POST",
                body: emailJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            sweetAlert("Success", "Email sent successfully", "success");
            const code = document.getElementById("code").disabled = false;
            const verifyBtn = document.getElementById("verifyBtn").disabled = false;
            const email = document.getElementById("email").disabled = true;
            const email_btn = document.getElementById("email-btn").disabled = true;
        } else {
//            document.getElementById("message").innerHTML = json.message;
            sweetAlert("Error", json.message, "error");
        }
    } else {
//        document.getElementById("message").innerHTML = "Registraion failed. Please try again!"
        sweetAlert("Error", "Registraion failed. Please try again!", "error");
    }
}

