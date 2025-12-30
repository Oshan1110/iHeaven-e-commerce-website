function getQueryParam(param) {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get(param);
}

async function loadSearchResults() {
    const query = getQueryParam("search");
    const resultContainer = document.getElementById("product-results");

    if (!query) {
        resultContainer.innerHTML = "<p>No search term provided.</p>";
        return;
    }

    try {
        const res = await fetch("searchProductSingle", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify({search: query})
        });

        const data = await res.json();
        resultContainer.innerHTML = "";

        if (data.status && data.products.length > 0) {
            data.products.forEach(product => {
                const Productdiv = document.createElement("div");
                console.log(product);
                Productdiv.innerHTML = `
                        <div class="product-card bg-white rounded-xl shadow-md overflow-hidden transition duration-300">
                            <div class="relative">
                                <a href="single-product.html?id=${product.id}">
                                    <img src="product-images/${product.id}/image1.png"
                                         alt="${product.title}" class="w-full h-64 object-contain">
                                </a>
                            </div>
                            <div class="p-6">
                                <div class="flex justify-between items-start">
                                    <div>
                                        <h3 class="text-xl font-bold text-gray-900">${product.title}</h3>
                                        <p class="text-gray-600 text-sm">Color: <span class="text-green-600 font-bold text-sm">${product.color}</span></p>
                                        <p class="text-gray-600 text-sm">Storage: <span class="text-gray-600 font-bold text-sm">${product.storage}</span></p>
                                    </div>
                                    <div class="text-right">
                                        <p class="text-blue-600 font-bold">LKR ${product.price}</p>
                                    </div>
                                </div>
                <a href="single-product.html?id=${product.id}">
                                <button class="mt-6 w-full bg-gradient-to-r from-blue-500 to-purple-600 text-white py-2 rounded-full transition duration-300">
                                    View Product
                                </button>
                </a>
                            </div>
                        </div>
                    `;
                resultContainer.appendChild(Productdiv);
            });
        } else {
            const searchText = document.getElementById("searchText").innerHTML = `<div class="text-center mb-12">
                <h1 class="text-4xl font-bold text-gray-800 mb-2">No Product Found</h1>
                <p class="text-gray-600 max-w-2xl mx-auto">Discover our carefully curated collection of high-quality items
                </p>
            </div>`;
            resultContainer.innerHTML = "";
        }
    } catch (err) {
        console.error("Search error:", err);
        resultContainer.innerHTML = "<p>Error searching products.</p>";
    }
}

// Run when page loads
window.addEventListener("DOMContentLoaded", loadSearchResults);


