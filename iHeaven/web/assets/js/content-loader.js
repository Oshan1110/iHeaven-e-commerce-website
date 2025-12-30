async function viewCart(){
    const response = await fetch("LoadCartItems");
    if (response.ok) {
        const json = await response.json();
        if (json.status) {
            const side_panel_cart_item_list = document.getElementById("cart-items");
            side_panel_cart_item_list.innerHTML = "";

            let total = 0;
            let totalQty = 0;
            json.cartItems.forEach(cart => {
                let productSubTotal = cart.product.price * cart.qty;
                total += productSubTotal;
                totalQty += cart.qty;
                let cartItem = `<div class="flex items-center justify-between border rounded-lg p-3 shadow-sm">
                        <img src="product-images\\${cart.product.id}\\image1.png" alt="Product" class="w-16 h-16 object-cover rounded" />
                        <div class="flex-1 mx-3">
                            <h4 class="font-medium text-gray-800 text-sm">${cart.product.title}</h4>
                        </div>
                        <div class="text-right">
                            <p class="text-sm font-semibold text-gray-800">LKR ${new Intl.NumberFormat(
                        "en-US",
                        {minimumFractionDigits: 2})
                        .format(cart.product.price)}</p>
                        </div>
                    </div>`;
                side_panel_cart_item_list.innerHTML += cartItem;
            });
            document.getElementById("cart-total").innerHTML = new Intl.NumberFormat("en-US",
                    {minimumFractionDigits: 2})
                    .format(total);
            document.getElementById("cart-subtotal").innerHTML = new Intl.NumberFormat("en-US",
                    {minimumFractionDigits: 2})
                    .format(total);
        } else {
            
        }
    } else {
        
    }
}

