function searchSingle() {
    const query = document.getElementById("serchP").value.trim();
    if (query !== "") {
        // Redirect to search-single.html with query string
        window.location.href = `search-single.html?search=${encodeURIComponent(query)}`;
    }
}

//async function searchSingle() {
//    const query = document.getElementById("serchP").value.trim();
//    const resultContainer = document.getElementById("product-results");
//
//    const response = await fetch("searchProductSingle", {
//        method: "POST",
//        headers: {
//            "Content-Type": "application/json"
//        },
//        body: JSON.stringify({search: query})
//    })
//            .then(res => res.json())
//            .then(data => {
//                resultContainer.innerHTML = "";
//
//                if (data.status && data.products.length > 0) {
//                    data.products.forEach(product => {
//                        const Productdiv = document.createElement("div");
//                        Productdiv.innerHTML = `
//                        <div class="product-card bg-white rounded-xl shadow-md overflow-hidden transition duration-300">
//                        <div class="relative">
//                            <a href="single-product.html?id=${product.id}">
//                            <img src="product-images\\${product.id}\\image1.png"
//                                 alt="iPhone 15" class="w-full h-64 object-contain"></a>
//                        </div>
//                        <div class="p-6">
//                            <div class="flex justify-between items-start">
//                                <div>
//                                    <h3 class="text-xl font-bold text-gray-900">${product.title}</h3>
//                                    <p class="text-gray-600 text-sm">Latest model with Dynamic Island</p>
//                                </div>
//                                <div class="text-right">
//                                    <p class="text-blue-600 font-bold">${product.title}</p>
//                                </div>
//                            </div>
//                            <button
//                                class="mt-6 w-full bg-blue-500 hover:bg-blue-600 text-white py-2 rounded-lg transition duration-300">
//                                Add to Cart
//                            </button>
//                        </div>
//                    </div>
//                    `;
//                        resultContainer.appendChild(Productdiv);
//                    });
//                } else {
//                    resultContainer.innerHTML = "<p>No products found.</p>";
//                }
//            })
//            .catch(err => {
//                console.error("Search error:", err);
//                resultContainer.innerHTML = "<p>Error searching products.</p>";
//            });
//}


