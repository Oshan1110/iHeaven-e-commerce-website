async function signIn() {
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    const signIn = {
        email: email,
        password: password
    };

    const signJson = JSON.stringify(signIn);

    const response = await fetch(
            "SignIn",
            {
                method: "POST",
                body: signJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            if (json.message == "1") {
                window.location = "verifinotitfy.html"
            } else {
                window.location = "index.html"
            }
        } else {
//            document.getElementById("message").innerHTML = json.message;
            sweetAlert("Error", json.message, "info");
        }
    } else {
//        document.getElementById("message").innerHTML = "Sign In failed. Please Try again!";
        sweetAlert("Sign In failed. Please Try again!", json.message, "error");
    }
}


