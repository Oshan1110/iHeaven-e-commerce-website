async function verifyPassword() {
    const newPassword = document.getElementById("newPassword").value.trim();
    const vPassword = document.getElementById("vPassword").value.trim();

    const user = {
        newPassword: newPassword,
        vPassword: vPassword
    };

    try {
        const response = await fetch("ChangePassword", {
            method: "POST",
            body:JSON.stringify(user),
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            throw new Error("Server error");
        }

        const json = await response.json();

        if (json.status) {
            window.location = "sign-in.html";
        } else {
            Swal.fire("Error", json.message || "Update failed", "info");
        }

    } catch (error) {
        console.error("Fetch error:", error);
        Swal.fire("Error", "Something went wrong!", "error");
    }

}




