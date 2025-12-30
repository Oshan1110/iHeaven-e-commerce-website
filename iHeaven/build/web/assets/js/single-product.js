async function loadData() {
    const searchParams = new URLSearchParams(window.location.search);
    if (searchParams.has("id")) {
        const productId = searchParams.get("id");
        console.log(productId);
        const response = await fetch("LoadSingleProduct?id=" + productId);
        if (response.ok) {
            const json = await response.json();
            if (json.status) {
                console.log(json);
                //single-product-images
                document.getElementById("image1").src = "product-images\\" + json.product.id + "\\image1.png";
                document.getElementById("thumb-image1").src = "product-images\\" + json.product.id + "\\image1.png";
                document.getElementById("thumb-image2").src = "product-images\\" + json.product.id + "\\image2.png";
                document.getElementById("thumb-image3").src = "product-images\\" + json.product.id + "\\image3.png";
                //single-product-images-end

                document.getElementById("product-title").innerHTML = json.product.title;
                document.getElementById("published-on").innerHTML = json.product.added_date;
                document.getElementById("product-price").innerHTML = new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(json.product.price);
                document.getElementById("brand-name").innerHTML = json.product.model.brand.name;
                document.getElementById("model-name").innerHTML = json.product.model.name;
                document.getElementById("product-quality").innerHTML = json.product.quality.value;
                document.getElementById("product-stock").innerHTML = json.product.qty;

                // product-color
                document.getElementById("color-background").innerHTML = json.product.color.name;

                //product-storage
                document.getElementById("product-storage").innerHTML = json.product.storage.value;
                //product-description
                document.getElementById("description").innerHTML = json.product.description;

                //add-to-cart-main-button
                const addToCartMain = document.getElementById("add-to-cart-main");
                addToCartMain.addEventListener(
                        "click", (e) => {
                    addToCart(json.product.id, document.getElementById("quantity").value);
                    e.preventDefault();
                });
                //add-to-cart-main-button-end
            } else {
                window.location = "index.html";
            }
        } else {
            window.location = "index.html";
        }
    }
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

