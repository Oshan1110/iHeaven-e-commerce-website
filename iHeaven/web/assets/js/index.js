function indexOnloadFunctions() {
    CheckSessionCart();
    loadProductData();
}

async function CheckSessionCart() {
    const response = await fetch("CheckSessionCart");
    if (!response.ok) {
        const json = await response.json();
        console.log(json.message);
        sweetAlert("Error", json.message, "error");
    }
}

async function loadProductData() {
    const response = await fetch("LoadHomeData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            loadNewArrivals(json);
            loadMessages(json);
        } else {
            console.log("error1");
//            sweetAlert("Error", json.message, "error");
        }
    } else {
        console.log("error2");
//        sweetAlert("Error", json.message, "error");
    }
}

function loadNewArrivals(json) {
    const new_arrival_product_container = document.getElementById("product-container");
    new_arrival_product_container.innerHTML = "";

    json.productList.forEach(item => {
        let product_card = `<div class="product-card bg-white rounded-xl shadow-md overflow-hidden transition duration-300">
    <div class="relative">
        <a href="single-product.html?id=${item.id}">
            <img src="product-images\\${item.id}\\image1.png"
                 alt="iPhone 15" class="w-full h-64 object-contain">

            <!-- Quality label -->
            <div class="absolute top-4 left-4 text-white text-xs font-bold px-2 py-1 rounded 
                ${item.quality.id === 1 ? 'bg-green-500' : item.quality.id === 2 ? 'bg-red-500' : ''}">
                ${item.quality.value}
            </div>

            <!-- Out of Stock label -->
            ${item.qty === 0 ? `
                <div class="absolute top-4 right-4 bg-red-600 text-white text-xs font-bold px-2 py-1 rounded">
                    Out of Stock
                </div>
            ` : ''}
        </a>
    </div>
    <div class="p-6">
        <div class="flex justify-between items-start">
            <div>
                <h3 class="text-xl font-bold text-gray-900">${item.title}</h3>
                <p class="text-gray-600 text-sm">${item.storage.value}</p>
                <p class="text-gray-600 text-sm">${item.color.name}</p>
            </div>
            <div class="text-right">
                <p class="text-blue-600 font-bold">LKR ${new Intl.NumberFormat(
                "en-US",
                {minimumFractionDigits: 2}
        ).format(item.price)}</p>
            </div>
        </div>
        <button
            class="mt-6 w-full bg-gradient-to-r from-blue-500 to-purple-600 text-white py-2 rounded-full transition duration-300" 
            onclick="addToCart(${item.id},1);"
            ${item.qty === 0 ? 'disabled class="opacity-50 cursor-not-allowed"' : ''}>
            Add to Cart
        </button>
    </div>
</div>
`;
        new_arrival_product_container.innerHTML += product_card;
    });
}

async function addToCart(productId, qty) {
    const response = await fetch("AddToCart?prId=" + productId + "&qty=" + qty);
    if (response.ok) {
        const json = await response.json(); // await response.text();
        if (json.status) {
            console.log(json.message);
            sweetAlert("Success", json.message, "success");
        } else {
            console.log("somthing went wrong");
            sweetAlert("somthing went wrong", json.message, "error");

        }
    } else {
        console.log(json.message);
        sweetAlert("Success", json.message, "success");
    }
}

function loadMessages(json) {
    const message_container = document.getElementById("message-container");
    message_container.innerHTML = "";
    json.userFeedbacksList.forEach(item => {
        let message_card = `<div class="bg-gray-50 p-6 rounded-xl w-[350px] flex-shrink-0 animate-scroll">
                            <!-- Card Content -->
                            <div class="flex items-center mb-4">
                                <div class="flex-shrink-0">
                                    <div class="w-10 h-10 rounded-full bg-gray-200 flex items-center justify-center mb-0">
                                        <i class="fas fa-user text-lg text-gray-500"></i>
                                    </div>
                                </div>
                                <div class="ml-3">
                                    <h4 class="text-sm font-medium text-gray-900">${item.first_name} ${item.last_name}</h4>
                                    <div class="flex text-yellow-400 text-xs">
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                    </div>
                                </div>
                            </div>
                            <p class="text-gray-600 italic text-sm leading-relaxed">
                                ${item.text}
                            </p>
                        </div>`;
        message_container.innerHTML += message_card;
    });
}

