async function loadCheckoutData() {
    const response = await fetch("LoadCheckoutData");
    if (response.ok) { //200
        const json = await response.json();
        if (json.status) {
            console.log(json);
            const userAddress = json.userAddress;
            const cityList = json.cityList;
            const cartItems = json.cartList;
            const deliveryTypes = json.deliveryTypes;

            // load citites
            let city_select = document.getElementById("city-select");

            cityList.forEach(city => {
                let option = document.createElement("option");
                option.value = city.id;
                option.innerHTML = city.name;
                city_select.appendChild(option);
            });

            // load current address
            const current_address_checkbox = document.getElementById("same-as-shipping");
            current_address_checkbox.addEventListener("change", function () {

                let first_name = document.getElementById("first-name");
                let last_name = document.getElementById("last-name");
                let line_one = document.getElementById("line-one");
                let line_two = document.getElementById("line-two");
                let postal_code = document.getElementById("postal-code");
                let mobile = document.getElementById("mobile");
                let apart_details = document.getElementById("apart-details");
                if (current_address_checkbox.checked) {
                    first_name.value = userAddress.user.fname;
                    last_name.value = userAddress.user.lname;
                    city_select.value = userAddress.city.id;
                    city_select.disabled = true;
                    city_select.dispatchEvent(new Event("change"));
                    line_one.value = userAddress.line_1;
                    line_two.value = userAddress.line_2;
                    postal_code.value = userAddress.postalCode;
                    mobile.value = userAddress.mobile;
                    apart_details.value = userAddress.line_1 + "," + userAddress.line_2;
                } else {
                    first_name.value = "";
                    last_name.value = "";
                    city_select.value = 0;
                    city_select.disabled = false;
                    city_select.dispatchEvent(new Event("change"));
                    line_one.value = "";
                    line_two.value = "";
                    postal_code.value = "";
                    mobile.value = "";
                    apart_details.value = "";
                }
            });
            const cart_item_container = document.getElementById("cart-item-container");
            cart_item_container.innerHTML = "";
            let total = 0;
            let totalQty = 0;
            cartItems.forEach(cart => {
                let productSubTotal = cart.product.price * cart.qty;
                total += productSubTotal;
                totalQty += cart.qty;
                let tableData = `<div class="flex items-start space-x-4 mb-4 pb-4 border-b border-gray-200">
                                    <div class="bg-gray-100 p-2 rounded-lg">
                                        <img src="product-images\\${cart.product.id}\\image1.png" alt="MacBook Pro" class="w-16 h-16 object-contain">
                                    </div>
                                    <div>
                                        <h3 class="font-medium">${cart.product.title}</h3>
                                        <p class="text-sm text-gray-600">${cart.product.color.name}</p>
                                        <p class="text-sm text-gray-600">${cart.product.storage.value}</p>
                                        <p class="text-sm font-medium mt-1"><span>LKR</span> ${new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(cart.product.price)} <span>x</span><span>${cart.qty}</span></p>
                                    </div>
                                </div>`;
                cart_item_container.innerHTML += tableData;

            });
            document.getElementById("subtotal-cart").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2})
                    .format(total);
            document.getElementById("total-cart").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2})
                    .format(total);
        } else {
            if (json.message === "empty-cart") {
                sweetAlert("Error", "Empty cart. Please add some product", "info");
                window.location = "index.html";
            } else {
                sweetAlert("Error", json.message, "error");
            }
        }
    } else {
        if (response.status === 401) {
            window.location = "sign-in.html";
        }
    }
}

async function checkout() {
    let checkbox = document.getElementById("same-as-shipping").checked;
    let first_name = document.getElementById("first-name");
    let last_name = document.getElementById("last-name");
    let line_one = document.getElementById("line-one");
    let line_two = document.getElementById("line-two");
    let postal_code = document.getElementById("postal-code");
    let mobile = document.getElementById("mobile");
    let city_select = document.getElementById("city-select");

    let data = {
        isCurrentAddress: checkbox,
        firstName: first_name.value,
        lastName: last_name.value,
        citySelect: city_select.value,
        lineOne: line_one.value,
        lineTwo: line_two.value,
        postalCode: postal_code.value,
        mobile: mobile.value
    };
    let dataJSON = JSON.stringify(data);

    const response = await fetch("CheckOut", {
        method: "POST",
        header: {
            "Content-Type": "application/json"
        },
        body: dataJSON
    });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            // PayHere Process
            payhere.startPayment(json.payhereJson);
//            location.reload();
        } else {
            sweetAlert("Error", json.message, "error");
        }
    } else {
        sweetAlert("Error", "Somthing went wrong!", "error");
    }
}


