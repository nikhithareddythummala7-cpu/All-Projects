/**
 * Render cart items in the cart page
 * @param {object} cartData - Cart data from server
 */
function renderCartItems(cartData) {
    const tbody = document.getElementById('cartItemsTbody');
    if (!tbody) return;

    tbody.innerHTML = '';
    
    if (!cartData.items || cartData.items.length === 0) {
        const emptyMessage = document.createElement('tr');
        emptyMessage.innerHTML = `
            <td colspan="5" class="text-center py-5">
                <div class="text-muted mb-3">Your cart is empty</div>
                <a href="/medicines" class="btn btn-primary">Browse Medicines</a>
            </td>
        `;
        tbody.appendChild(emptyMessage);
        
        // Update totals to 0
        document.getElementById('itemsTotal').textContent = '₹0.00';
        document.getElementById('deliveryCost').textContent = '₹0.00';
        document.getElementById('grandTotal').textContent = '₹0.00';
        
        // Disable order button
        const orderBtn = document.getElementById('placeOrderBtn');
        if (orderBtn) orderBtn.disabled = true;
        return;
    }
    
    // Enable order button if we have items
    const orderBtn = document.getElementById('placeOrderBtn');
    if (orderBtn) orderBtn.disabled = false;

    cartData.items.forEach(item => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${item.medicineName}</td>
            <td class="text-center">
                <div class="btn-group" role="group">
                    <button type="button" class="btn btn-sm btn-outline-secondary" 
                            onclick="updateCartQuantity('${item.medicineId}', ${Math.max(0, item.quantity - 1)})">-</button>
                    <span class="btn btn-sm">${item.quantity}</span>
                    <button type="button" class="btn btn-sm btn-outline-secondary" 
                            onclick="updateCartQuantity('${item.medicineId}', ${item.quantity + 1})">+</button>
                </div>
            </td>
            <td class="text-end">₹${item.price.toFixed(2)}</td>
            <td class="text-end">₹${(item.price * item.quantity).toFixed(2)}</td>
            <td class="text-center">
                <button type="button" class="btn btn-sm btn-outline-danger"
                        onclick="removeFromCart('${item.medicineId}')">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(row);
    });

    // Update order summary
    const itemsTotal = document.getElementById('itemsTotal');
    const deliveryCost = document.getElementById('deliveryCost');
    const grandTotal = document.getElementById('grandTotal');

    if (itemsTotal) itemsTotal.textContent = `₹${cartData.totalAmount.toFixed(2)}`;
    if (deliveryCost) deliveryCost.textContent = `₹${cartData.deliveryCost.toFixed(2)}`;
    if (grandTotal) grandTotal.textContent = `₹${(cartData.totalAmount + cartData.deliveryCost).toFixed(2)}`;

}

/**
 * Update the quantity of an item in the cart
 * @param {string} medicineId - Medicine ID
 * @param {number} newQuantity - New quantity
 */
function updateCartQuantity(medicineId, newQuantity) {
    if (newQuantity < 0) return;
    
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    fetch('/api/cart/update', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: token
        },
        body: JSON.stringify({ medicineId: medicineId, quantity: newQuantity })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to update cart');
        }
        return response.json();
    })
    .then(data => {
        if (data && typeof data.totalItems === 'number') {
            updateCartCount(data.totalItems);
        }
        if (data && data.items) {
            renderCartItems(data);
        }
    })
    .catch(error => {
        console.error('Error updating cart:', error);
        showToast('Error', error.message || 'Failed to update quantity', 'error');
    });
}

/**
 * Remove an item from the cart
 * @param {string} medicineId - Medicine ID
 */
function removeFromCart(medicineId) {
    const token = document.querySelector('meta[name="_csrf"]').content;
    const header = document.querySelector('meta[name="_csrf_header"]').content;

    fetch('/api/cart/remove', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [header]: token
        },
        body: JSON.stringify({ medicineId: medicineId })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to remove item');
        }
        return response.json();
    })
    .then(data => {
        if (data && typeof data.totalItems === 'number') {
            updateCartCount(data.totalItems);
        }
        if (data && data.items) {
            renderCartItems(data);
        }
        showToast('Success', 'Item removed from cart', 'success');
    })
    .catch(error => {
        console.error('Error removing item:', error);
        showToast('Error', error.message || 'Failed to remove item', 'error');
    });
}
/**
 * Add a medicine to the cart when clicking Buy Now
 */
async function addToCart(medicineId, medicineName, quantity = 1) {
    try {
        const token = document.querySelector('meta[name="_csrf"]')?.content;
        const header = document.querySelector('meta[name="_csrf_header"]')?.content;

        const response = await fetch('/api/cart/add', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                ...(token && header ? { [header]: token } : {})
            },
            body: JSON.stringify({
                medicineId: medicineId,
                quantity: quantity
            })
        });

        if (!response.ok) {
            const err = await response.json().catch(() => ({}));
            showToast('Error', err.error || 'Failed to add item to cart', 'error');
            return;
        }

        const data = await response.json();
        showToast('Success', `${medicineName} added to cart!`, 'success');

        // update cart count icon if data.totalItems exists
        if (data && typeof data.totalItems === 'number') {
            updateCartCount(data.totalItems);
        }

    } catch (error) {
        console.error('Error adding to cart:', error);
        showToast('Error', error.message || 'Could not add to cart', 'error');
    }
}
         