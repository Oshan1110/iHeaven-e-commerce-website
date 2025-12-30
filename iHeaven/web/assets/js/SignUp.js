async function  signUp() {
    const fname = document.getElementById("fname").value;
    const lname = document.getElementById("lname").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const password2 = document.getElementById("password2").value;

    const user = {
        fristName: fname,
        lastName : lname,
        email: email,
        password: password,
        password2: password2
    };

    const userJson = JSON.stringify(user);

    const response = await fetch(
            "SignUp",
            {
                method: "POST",
                body:userJson,
                header: {
                    "Content-Type": "application/json"
                }
            }
    );
    
    if(response.ok){//success
        const Json =  await response.json();
       if(Json.status){//if true
           window.location = "verifinotitfy.html"
       }else{ // when status false
          
//           document.getElementById("message").innerHTML = Json.message;
           sweetAlert("Error", Json.message, "info");
       }
    
    
    }else{
//       document.getElementById("message").innerHTML = "Registration Faild Plrase Try Again!";  
       sweetAlert("Registration Faild Plrase Try Again!", Json.message, "error");
    }

}


