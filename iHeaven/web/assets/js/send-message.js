async function sendMessage() {
    const firstName = document.getElementById("fname").value;
    const lastName = document.getElementById("lname").value;
    const email = document.getElementById("email").value;
    const fMessage = document.getElementById("message").value;

    const msgDataObject = {
        firstName: firstName,
        lastName: lastName,
        email: email,
        fMessage: fMessage
    };
    
     const msgDataJSON = JSON.stringify(msgDataObject);
     
     const response = await fetch("SaveMessage", {
         method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: msgDataJSON
     });
     
     if (response.ok) {
        const massage = document.getElementById("message");
        const json = await response.json();
        if (json.status) {
            console.log(json);
            sweetAlert("Success", json.message, "success");
            document.getElementById("fname").value = "";
            document.getElementById("lname").value = "";
            document.getElementById("email").value = "";
            document.getElementById("message").value = "";
        } else {
//            let timeLeft = 5;
//            massage.innerHTML = json.message;
//
//            const countdown = setInterval(() => {
//                timeLeft--;
//                if (timeLeft <= 0) {
//                    massage.innerHTML = "";
//                    clearInterval(countdown);
//                }
//            }, 1000);
        }

    } else {
//        document.getElementById("message").innerHTML = "Profile deatils update failed!";
        sweetAlert("Profile deatils update failed!", json.message, "info");

    }
}

