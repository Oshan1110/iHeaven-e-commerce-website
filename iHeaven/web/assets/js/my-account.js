async function loadData() {
    const response = await fetch("MyAccount");
    if (response.ok) {
        const json = await response.json();
        document.getElementById("username").innerHTML = `${json.firstName} ${json.lastName}`;
        document.getElementById("verify").innerHTML = json.verify;
        document.getElementById("firstName").value = json.firstName;
        document.getElementById("lastName").value = json.lastName;
        document.getElementById("email").value = json.email;
        document.getElementById("password").value = json.password;
    }
}

async function getCityData() {
    const response = await fetch("CityData");
    if (response.ok) {
        const json = await response.json();
        const citySelect = document.getElementById("citySelect");
        json.forEach(city => {
            let option = document.createElement("option");
            option.innerHTML = city.name;
            option.value = city.id;
            citySelect.appendChild(option);
        });

    }
}

async function saveChanges() {

    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const lineOne = document.getElementById("lineOne").value;
    const lineTwo = document.getElementById("lineTwo").value;
    const postalCode = document.getElementById("postalCode").value;
    const cityId = document.getElementById("citySelect").value;
    const currentPassword = document.getElementById("password").value;
    const newPassword = document.getElementById("newPassword").value;
    const confirmPassword = document.getElementById("confirmPassword").value;
    const mobile = document.getElementById("mobile").value;

    const userDataObject = {
        firstName: firstName,
        lastName: lastName,
        lineOne: lineOne,
        lineTwo: lineTwo,
        postalCode: postalCode,
        cityId: cityId,
        currentPassword: currentPassword,
        newPassword: newPassword,
        confirmPassword: confirmPassword,
        mobile: mobile
    };

    const userDataJSON = JSON.stringify(userDataObject);

    const response = await fetch("SaveChanges", {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: userDataJSON
    });
    if (response.ok) {
        const massage = document.getElementById("message");
        const json = await response.json();
        if (json.status) {
            sweetAlert("Success", "Details saved", "success");
            loadData();
            document.getElementById("firstName").value = "";
            document.getElementById("lastName").value = "";
            document.getElementById("lineOne").value = "";
            document.getElementById("lineTwo").value = "";
            const postalCode = document.getElementById("postalCode").value = "";
            document.getElementById("citySelect").value = 0;
            document.getElementById("password").value = "";
            document.getElementById("newPassword").value = "";
            document.getElementById("confirmPassword").value = "";
            document.getElementById("mobile").value = "";
        } else {
            let timeLeft = 5;
            massage.innerHTML = json.message;

            const countdown = setInterval(() => {
                timeLeft--;
                if (timeLeft <= 0) {
                    massage.innerHTML = "";
                    clearInterval(countdown);
                }
            }, 1000);
        }

    } else {
//        document.getElementById("message").innerHTML = "Profile deatils update failed!";
//        sweetAlert("Profile deatils update failed!", json.message, "info");

    }
}

async function productListing() {
    const response = await fetch("productListing");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const productListingLoad = document.getElementById("p-container");
            productListingLoad.innerHTML = "";

            json.productList.forEach(product => {
//                console.log(product);
                let tableData = `<tr class="order-item">
                                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${product.title}</td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${product.added_date}</td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">LKR ${product.price}</td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${product.storage.value}</td>
                                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${product.color.name}</td>
                                    </tr>`;
                productListingLoad.innerHTML += tableData;
            });
        }
    }
}

async function orderListing() {
    const response = await fetch("orderListing");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const orderListingLoad = document.getElementById("order-container");
            orderListingLoad.innerHTML = "";
            json.orderItems.forEach(order => {
                console.log(order);
                let tableData = `<tr class="order-item">
                        <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${order.order.id}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${order.product.title}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${order.order.added_date}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">LKR ${order.product.price}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${order.qty}</td>
                    </tr>`;

                orderListingLoad.innerHTML += tableData;
            });

        }

    }
}


