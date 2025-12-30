var modelList;
async function loadProductData() {
    const response = await fetch("LoadProductData");

    if (response.ok) {
        const json = await response.json();
        if (json.status) {

            loadSelect("brand", json.brandList, "name");
            modelList = json.modelList;
//            loadSelect("model", json.modelList, "name");
            loadSelect("storage", json.storageList, "value");
            loadSelect("color", json.colorList, "name");
            loadSelect("condition", json.qualityList, "value");

        } else {
//            document.getElementById("message").innerHTML = "unable to load product data! Please try again later";
            sweetAlert("Error", "unable to load product data! Please try again later", "error");
        }
    } else {
//        document.getElementById("messge").innerHTML = "Unable to load product data! Please try again later"
        sweetAlert("Error", "unable to load product data! Please try again later", "error");
    }
}

function loadSelect(selectId, list, property) {
    const select = document.getElementById(selectId);
    list.forEach(item => {
        const option = document.createElement("option")
        option.value = item.id;
        option.innerHTML = item[property];
        select.appendChild(option);
    });
}

function loadModels() {
    const brand = document.getElementById("brand").value;
    const modelSelect = document.getElementById("model");
    modelSelect.length = 1;

    modelList.forEach(item => {
        if (item.brand.id == brand) {
            const option = document.createElement("option");
            option.value = item.id;
            option.innerHTML = item.name;
            modelSelect.appendChild(option);
        }
    });
}

async function saveProduct() {
    const brandId = document.getElementById("brand").value;
    const modelId = document.getElementById("model").value;
    const title = document.getElementById("title").value;
    const desc = document.getElementById("desc").value;
    const storageId = document.getElementById("storage").value;
    const colorId = document.getElementById("color").value;
    const conditionId = document.getElementById("condition").value;
    const price = document.getElementById("price").value;
    const qty = document.getElementById("qty").value;

    const img1 = document.getElementById("img1").files[0];
    const img2 = document.getElementById("img2").files[0];
    const img3 = document.getElementById("img3").files[0];

    const form = new FormData();
    form.append("brandId", brandId);
    form.append("modelId", modelId);
    form.append("title", title);
    form.append("desc", desc);
    form.append("storageId", storageId);
    form.append("colorId", colorId);
    form.append("conditionId", conditionId);
    form.append("price", price);
    form.append("qty", qty);
    form.append("img1", img1);
    form.append("img2", img2);
    form.append("img3", img3);

    const response = await fetch(
            "SaveProducts",
            {
                method: "POST",
                body: form
            }
    );



    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            document.getElementById("brand").value = 0;
            document.getElementById("model").value = 0;
            document.getElementById("title").value = "";
            document.getElementById("desc").value = "";
            document.getElementById("storage").value = 0;
            document.getElementById("color").value = 0;
            document.getElementById("condition").value = 0;
            document.getElementById("price").value = "0.00";
            document.getElementById("qty").value = 1;
            document.getElementById("img1").value = "";
            document.getElementById("img2").value = "";
            document.getElementById("img3").value = "";
        } else {
            if (json.message === "Please login") {
                window.location = "sign-in.html";
            } else {
                sweetAlert("Error", json.message, "error");
            }
        }
    } else {
        sweetAlert("Error", "Error in product adding", "error");
    }
}


