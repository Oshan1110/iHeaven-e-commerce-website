async function loadCartItems() {
    const response = await fetch("LoadCartItems");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const cart_item_container = document.getElementById("cart-item-container");
            cart_item_container.innerHTML = "";

            let total = 0;
            let totalQty = 0;
            json.cartItems.forEach(cart => {
                let productSubTotal = cart.product.price * cart.qty;
                total += productSubTotal;
                totalQty += cart.qty;
                let tableData = `
                                <div class="py-4 flex flex-col sm:flex-row">
                                    <div class="sm:w-1/4 mb-4 sm:mb-0">
                                    <img src="product-images\\${cart.product.id}\\image1.png" 
                                         alt="iPhone 15 Pro" 
                                         class="w-full h-auto rounded-lg object-cover">
                                </div>
                                <div class="sm:w-3/4 sm:pl-6">
                                    <div class="flex justify-between items-start">
                                        <div>
                                            <h3 class="text-lg font-semibold text-gray-800">${cart.product.title}</h3>
                                            <p class="text-gray-600 text-sm">256GB - Titanium Blue</p>
                                        </div>
                                        <div class="text-right">
                                            <div class="text-sm text-gray-500">LKR</div>
                                            <div class="text-lg font-semibold text-gray-800">${new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(cart.product.price)}</div>
                                        </div>
                                    </div>
                                    <div class="mt-4 flex items-center justify-between">
                                        <div class="flex items-center border border-gray-300 rounded-md">

                                            <span class="px-3 py-1 font-semibold">Quantity</span>
                                            <span class="px-3 py-1">${cart.qty}</span>

                                        </div>
                                        <button class="text-red-500 hover:text-red-700 text-sm" onclick="deleteProduct(${cart.product.id});">
                                            <i class="fas fa-trash mr-1"></i> Remove
                                        </button>
                                    </div>
                                    </div>
                                </div>`;
                cart_item_container.innerHTML += tableData;
            });
            document.getElementById("order-total-amount").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2})
                    .format(total);
            document.getElementById("order-total-amount2").innerHTML = new Intl.NumberFormat(
                    "en-US",
                    {minimumFractionDigits: 2})
                    .format(total);
        } else {

        }
    } else {

    }
}

//async function deleteProduct(productId){
//    if(!confirm("Are You sure want to delete this product?")) return;
//    
//    const response = await fetch("DeleteProduct", {
//        method: "POST",
//        headers: {"Content-Type": "application/json"},
//        body: JSON.stringify({id: productId})
//    });
//    
//    const Json = await response.json();
//    if(Json.status){
//        sweetAlert("Success", "product has been deleted", "success");
//        location.reload();
//    }else{
//        sweetAlert("Error", Json.message, "error");
//    }
//}

async function deleteProduct(productId) {
    const result = await Swal.fire({
        title: 'Are you sure?',
        text: "Do you really want to delete this product?",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'Cancel'
    });

    if (!result.isConfirmed) return;

    const response = await fetch("DeleteProduct", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ id: productId })
    });

    const Json = await response.json();
    if (Json.status) {
        await Swal.fire('Deleted!', 'Product has been deleted.', 'success');
        location.reload();
    } else {
        Swal.fire('Error!', Json.message, 'error');
    }
}



