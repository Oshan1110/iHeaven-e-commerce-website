async function loadData() {
    const response = await fetch("LoadData");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
//            document.getElementById("all-item-count").innerHTML=json.allProductCount;
            loadOptions("brand", json.brandList, "name");
            loadOptions("condition", json.qualityList, "value");
            loadOptions("color", json.colorList, "name");
            loadOptions("storage", json.storageList, "value");

            updateProductView(json);
        } else {
            sweetAlert("Error", "Somthing Went Wrong", "error");
        }
    } else {
        sweetAlert("Error", "Somthing Went Wrong", "error");
    }
}

function loadOptions(prefix, dataList, property) {
    let options = document.getElementById(prefix + "-options");
    let li = document.getElementById(prefix + "-li");
    options.innerHTML = "";

    dataList.forEach(item => {
        let li_clone = li.cloneNode(true);

        li_clone.querySelector("#" + prefix + "-a").innerHTML = item[property];
        options.appendChild(li_clone);
    });

    const all_li = document.querySelectorAll("#" + prefix + "-options li");
    all_li.forEach(list => {
        list.addEventListener("click", function () {
            all_li.forEach(y => {
                y.classList.remove("chosen"); // <li class=".."><a>...</a></l>
            });
            this.classList.add("chosen");// <li class="choosen .."><a>...</a></l>
        });
    });
}

async function applyFilters(firstResult) {
    const brand_name = document.getElementById("brand-options")
            .querySelector(".chosen")?.querySelector('label[for="brand"]').innerHTML;

    const condition_name = document.getElementById("condition-options")
            .querySelector(".chosen")?.querySelector('label[for="condition"]').innerHTML;

    const color_name = document.getElementById("color-options")
            .querySelector(".chosen")?.querySelector('label[for="color"]').innerHTML;

    const storage_name = document.getElementById("storage-options")
            .querySelector(".chosen")?.querySelector('label[for="storage"]').innerHTML;

    const min_price_input = parseFloat(document.getElementById("minPrice").value);
    const max_price_input = parseFloat(document.getElementById("maxPrice").value);

    const min_price = isNaN(min_price_input) ? 0 : min_price_input;
    const max_price = isNaN(max_price_input) ? 10000 : max_price_input;


    const sort_value = document.getElementById("st-sort").value;

    const data = {
        firstResult: firstResult,
        brandName: brand_name,
        conditionName: condition_name,
        colorName: color_name,
        storageName: storage_name,
        priceStart: min_price,
        priceEnd: max_price,
        sortValue: sort_value
    };

    const dataJSON = JSON.stringify(data);

    const response = await fetch("SearchProducts",
            {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: dataJSON
            });

    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            console.log(json);
            updateProductView(json);
        } else {
            sweetAlert("Error", "Somthing went wrong. Please try again later", "error");
        }
    } else {
        sweetAlert("Error", "Somthing went wrong. Please try again later", "error");
    }
}

const st_product = document.getElementById("st-product"); // product card parent node
let st_pagination_button = document.getElementById("st-pagination-button");
let current_page = 0;

function updateProductView(json) {
    const product_container = document.getElementById("st-product-container");

    const label = document.getElementById("bg-color");
    product_container.innerHTML = "";
    json.productList.forEach(product => {
        let st_product_clone = st_product.cloneNode(true);

        const label = st_product_clone.querySelector("#bg-color");

        if (label) {
            if (product.quality.id === 1) {
                label.classList.add("bg-green-500");
                label.classList.remove("bg-red-500");
                label.textContent = "New";
            } else if (product.quality.id === 2) {
                label.classList.add("bg-red-500");
                label.classList.remove("bg-green-500");
                label.textContent = "Used";
            } else {
                label.style.display = "none";
            }
        }

        st_product_clone.querySelector("#st-product-a-1").href = "single-product.html?id=" + product.id;
        st_product_clone.querySelector("#st-product-img-1").src = "product-images//" + product.id + "//image1.png";
        st_product_clone.querySelector("#st-product-add-to-cart").addEventListener("click", (e) => {
            addToCart(product.id, 1);
            e.preventDefault();
        });

        st_product_clone.querySelector("#st-product-title-1").innerHTML = product.title;
        st_product_clone.querySelector("#st-product-price-1").innerHTML = new Intl.NumberFormat("en-US", {
            minimumFractionDigits: 2
        }).format(product.price);

        st_product_clone.querySelector("#st-product-color-1").innerHTML = product.color.name;
        st_product_clone.querySelector("#st-product-storage-1").innerHTML = product.storage.value;

        product_container.appendChild(st_product_clone);
    });


    let st_pagination_container = document.getElementById("st-pagination-container");
    st_pagination_container.innerHTML = "";
    let all_product_count = json.allProductCount;
    console.log("All Product Count:", json.allProductCount);
    document.getElementById("all-item-c").innerHTML = json.allProductCount;
    let product_per_page = 6;
    let pages = Math.ceil(all_product_count / product_per_page);

    //previous-button
    if (current_page !== 0) {
        let st_pagination_button_prev_clone = st_pagination_button.cloneNode(true);
        st_pagination_button_prev_clone.innerHTML = "Prev";
        st_pagination_button_prev_clone.addEventListener(
                "click", (e) => {
            current_page--;
            applyFilters(current_page * product_per_page);
            e.preventDefault();
        });
        st_pagination_container.appendChild(st_pagination_button_prev_clone);
    }


    // pagination-buttons
    for (let i = 0; i < pages; i++) {
        let st_pagination_button_clone = st_pagination_button.cloneNode(true);
        st_pagination_button_clone.innerHTML = i + 1;
        st_pagination_button_clone.addEventListener(
                "click", (e) => {
            current_page = i;
            applyFilters(i * product_per_page);
            e.preventDefault();
        });

        if (i === Number(current_page)) {
            st_pagination_button_clone.className = "inline-block bg-gradient-to-r from-blue-500 to-purple-600 text-white font-bold text-lg py-3 px-6 rounded-lg ml-2";
        } else {
            st_pagination_button_clone.className = "inline-block border border-gray-500 text-gray-700 hover:bg-gray-100 text-lg font-medium py-3 px-6 rounded-lg ml-2";
        }
        st_pagination_container.appendChild(st_pagination_button_clone);
    }

    // next-button
    if ((current_page + 1) * product_per_page < all_product_count) {
        let st_pagination_button_next_clone = st_pagination_button.cloneNode(true);
        st_pagination_button_next_clone.innerHTML = "Next";
        st_pagination_button_next_clone.addEventListener("click", (e) => {
            current_page++;
            applyFilters(current_page * product_per_page);
            e.preventDefault();
        });
        st_pagination_container.appendChild(st_pagination_button_next_clone);
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

function refresh() {
    location.reload();
}
//    console.log(brand_name);
//    console.log(condition_name);
//    console.log(color_name);
//    console.log(storage_name);
//    console.log(min_price);
//    console.log(max_price);
//    console.log(sort_value);


