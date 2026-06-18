/**
 * Main JavaScript file for Medly Pharma
 * Handles common frontend functionality
 */

console.log('main.js loaded successfully');

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Handle form validation
    const forms = document.querySelectorAll('.needs-validation');
    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Auto-hide alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert.alert-dismissible');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Cart functionality
    initializeCart();
    // Initialize medicine functionality on all pages
    initializeMedicinePage();
});

/**
 * Initialize medicine page functionality
 */
function initializeMedicinePage() {
    // Prevent multiple event listeners from being attached
    if (window.medicinePageInitialized) return;
    window.medicinePageInitialized = true;

    // Add event listeners for cart forms
       // ✅ FIXED: enable Buy-Now / Add-to-Cart button clicks (outside forms)
    document.addEventListener('click', function (e) {
        const btn = e.target.closest('.buy-now, .add-to-cart');
        if (btn && btn.dataset.medicineId) {
            e.preventDefault();
            const medicineId = btn.dataset.medicineId;
            const medicineName = btn.dataset.medicineName || 'Medicine';
            addToCart(medicineId, medicineName, 1);
        }
    });


    // Initialize filters
    initializeMedicineFilters();
}

/**
 * Initialize cart functionality
 */
function initializeCart() {
    // Check if we're on the cart page and have initial data
    if (document.getElementById('cartPage') && window.initialCartData) {
        // Update UI with server-provided cart data
        renderCartItems(window.initialCartData);
        updateCartCount(window.initialCartData.items.length);
    } else {
        // Not on cart page, just get count from server
        updateCartCount(0); // Initialize cart count
        fetch('/api/cart')
            .then(response => response.json())
            .then(data => {
                updateCartCount(data.totalItems);
            })
            .catch(error => {
                console.error('Error fetching cart count:', error);
                updateCartCount(0);
            });
    }
}

/**
 * Add item to cart
 * @param {string} medicineId - ID of the medicine to add
 * @param {string} medicineName - Name of the medicine to add
 * @param {number} quantity - Quantity to add
 */
async function addToCart(medicineId, medicineName, quantity = 1) {
    try {
        if (!medicineId) {
            throw new Error('Invalid medicine ID');
        }

        const token = document.querySelector('meta[name="_csrf"]').content;
        const header = document.querySelector('meta[name="_csrf_header"]').content;
        
        if (!token || !header) {
            throw new Error('Security token not found');
        }

        // Disable any buy now buttons for this medicine while request is in flight
        const buyButtons = document.querySelectorAll(`button[data-medicine-id="${medicineId}"]`);
        buyButtons.forEach(btn => btn.disabled = true);

        try {
            const payload = {
                medicineId: medicineId,
                quantity: parseInt(quantity) || 1
            };

            console.log('Adding to cart:', payload);

            const response = await fetch('/api/cart/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json',
                    [header]: token
                },
                body: JSON.stringify(payload)
            });

            if (!response.ok) {
                const text = await response.text();
                console.error('Server error response:', text);
                try {
                    const errorData = JSON.parse(text);
                    throw new Error(errorData.message || errorData.error || 'Failed to add item to cart');
                } catch (parseError) {
                    throw new Error(`Server error: ${response.status}`);
                }
            }

            const data = await response.json();
            console.log('Cart updated:', data);

            // Show success message
            showToast('Success', `Added ${medicineName} to cart!`, 'success');

            // Update cart badge with total items
            if (data && typeof data.totalItems === 'number') {
                updateCartCount(data.totalItems);
            }

            // If viewing cart page, update the display
            if (document.getElementById('cartPage')) {
                renderCartItems(data);
            }

        } finally {
            // Re-enable buy buttons
            buyButtons.forEach(btn => btn.disabled = false);
        }
   } catch (error) {
    console.error('Error adding to cart:', error);
    showToast('Error', error.message || 'Could not add item to cart', 'error');
    return false;
}
}

/**
 * Update cart count in the UI
 * @param {number} count - Number of items in cart
 */
function updateCartCount(count) {
    const cartCountElements = document.querySelectorAll('.cart-count');
    cartCountElements.forEach(el => {
        el.textContent = count;
        el.style.display = count > 0 ? 'inline-block' : 'none';
    });
}

/**
 * Show a toast notification
 * @param {string} title - Toast title
 * @param {string} message - Toast message
 * @param {string} type - Type of toast (success, error, warning, info)
 */
function showToast(title, message, type = 'info') {
    const toastContainer = document.getElementById('toast-container') || createToastContainer();
    const toastId = 'toast-' + Date.now();
    const icon = getToastIcon(type);
    
    const toast = document.createElement('div');
    toast.id = toastId;
    toast.className = `toast show`;
    toast.role = 'alert';
    toast.setAttribute('aria-live', 'assertive');
    toast.setAttribute('aria-atomic', 'true');
    
    toast.innerHTML = `
        <div class="toast-header">
            ${icon}
            <strong class="me-auto">${title}</strong>
            <small class="text-muted">Just now</small>
            <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
        </div>
        <div class="toast-body">
            ${message}
        </div>
    `;
    
    toastContainer.appendChild(toast);
    
    // Auto-remove toast after 5 seconds
    setTimeout(() => {
        const bsToast = new bootstrap.Toast(toast);
        bsToast.hide();
        toast.addEventListener('hidden.bs.toast', function() {
            toast.remove();
        });
    }, 5000);
}

/**
 * Get icon for toast based on type
 * @param {string} type - Type of toast
 * @returns {string} - HTML for the icon
 */
function getToastIcon(type) {
    const icons = {
        success: '<i class="fas fa-check-circle text-success me-2"></i>',
        error: '<i class="fas fa-times-circle text-danger me-2"></i>',
        warning: '<i class="fas fa-exclamation-triangle text-warning me-2"></i>',
        info: '<i class="fas fa-info-circle text-info me-2"></i>'
    };
    
    return icons[type] || icons.info;
}

/**
 * Create toast container if it doesn't exist
 * @returns {HTMLElement} - The toast container
 */
function createToastContainer() {
    const container = document.createElement('div');
    container.id = 'toast-container';
    container.className = 'toast-container position-fixed top-0 end-0 p-3';
    container.style.zIndex = '1100';
    document.body.appendChild(container);
    return container;
}

/**
 * Format price with currency
 * @param {number} amount - The amount to format
 * @param {string} currency - Currency code (default: USD)
 * @returns {string} - Formatted price
 */
function formatPrice(amount, currency = 'USD') {
    return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: currency
    }).format(amount);
}

// Medicine search and filter functionality
function initializeMedicineFilters() {
    const searchInput = document.getElementById('searchInput');
    const searchBtn = document.getElementById('searchBtn');
    const categoryFilter = document.getElementById('categoryFilter');
    const manufacturerFilter = document.getElementById('manufacturerFilter');
    const priceFilter = document.getElementById('priceFilter');
    const medicinesList = document.getElementById('medicinesList');

    if (!medicinesList) return; // Only initialize on medicines page

    // Filter medicines based on search input and filters
    function filterMedicines() {
        const searchTerm = searchInput.value.toLowerCase();
        const category = categoryFilter.value;
        const manufacturer = manufacturerFilter.value;
        const priceRange = priceFilter.value;

        const medicines = Array.from(medicinesList.getElementsByClassName('medicine-card'));

        medicines.forEach(card => {
            const name = card.querySelector('h5').textContent.toLowerCase();
            const cardCategory = card.querySelector('[data-category]')?.dataset.category?.toLowerCase();
            const cardManufacturer = card.querySelector('[data-manufacturer]')?.dataset.manufacturer?.toLowerCase();
            const priceText = card.querySelector('.price span').textContent;
            const price = parseFloat(priceText.replace('₹', ''));

            let showCard = true;

            // Apply search filter
            if (searchTerm && !name.includes(searchTerm)) {
                showCard = false;
            }

            // Apply category filter
            if (category && cardCategory !== category.toLowerCase()) {
                showCard = false;
            }

            // Apply manufacturer filter
            if (manufacturer && cardManufacturer !== manufacturer.toLowerCase()) {
                showCard = false;
            }

            // Apply price filter
            if (priceRange) {
                const [min, max] = priceRange.split('-').map(val => val === '+' ? Infinity : parseFloat(val));
                if (price < min || (max !== Infinity && price > max)) {
                    showCard = false;
                }
            }

            card.style.display = showCard ? '' : 'none';
        });
    }

    // Event listeners
    searchInput?.addEventListener('input', filterMedicines);
    searchBtn?.addEventListener('click', filterMedicines);
    categoryFilter?.addEventListener('change', filterMedicines);
    manufacturerFilter?.addEventListener('change', filterMedicines);
    priceFilter?.addEventListener('change', filterMedicines);
}

// Make functions available globally
// --- Cart page / Order placement functions ---
/**
 * Load cart page data and render
 */
async function loadCartPage() {
    try {
        const resp = await fetch('/api/cart');
        if (!resp.ok) throw new Error('Failed to fetch cart');
        const data = await resp.json();
        // Normalize older responses that may return only cartItem/totalItems
        if (!data.items && data.cartItem) {
            const item = data.cartItem;
            data.items = [item];
            data.totalAmount = (item.quantity || 0) * (item.price || 0);
            data.deliveryCost = data.deliveryCost || 0;
            data.totalItems = data.totalItems || (item.quantity || 0);
        }
        renderCartItems(data);
        if (data && typeof data.totalItems === 'number') {
            updateCartCount(data.totalItems);
        }
    } catch (err) {
        console.error('Error loading cart:', err);
        showToast('Error', err.message || 'Could not load cart', 'error');
    }
}

/**
 * Render cart items into the cart page
 * @param {object} data - cart data from server
 */
function renderCartItems(data) {
    const tbody = document.getElementById('cartItemsTbody');
    const itemsTotalEl = document.getElementById('itemsTotal');
    const deliveryCostEl = document.getElementById('deliveryCost');
    const grandTotalEl = document.getElementById('grandTotal');

    if (!tbody) return;
    tbody.innerHTML = '';

    const rawItems = data.items || [];
    // Normalize each item to have medicineId, medicineName, quantity, unitPrice, totalPrice
    const items = rawItems.map(i => {
        const quantity = i.quantity || 0;
        const unitPrice = (i.unitPrice !== undefined) ? i.unitPrice : (i.price !== undefined ? i.price : 0);
        return Object.assign({}, i, {
            medicineId: i.medicineId || i.id || i.medicineID,
            medicineName: i.medicineName || i.medicine_name || i.name || i.medicine || '',
            quantity: quantity,
            unitPrice: unitPrice,
            totalPrice: +(unitPrice * quantity)
        });
    });

    const itemsTotal = (typeof data.totalAmount === 'number') ? data.totalAmount : items.reduce((s, it) => s + (it.totalPrice || 0), 0);
    const delivery = (typeof data.deliveryCost === 'number') ? data.deliveryCost : 0;
    const grand = itemsTotal + delivery;

    items.forEach(item => {
        const tr = document.createElement('tr');

        tr.innerHTML = `
            <td>
                <div class="d-flex align-items-center gap-3">
                    <img src="${item.imageUrl || '/images/medicine-placeholder.jpg'}" 
                         alt="${item.medicineName}" 
                         style="width:60px;height:60px;object-fit:contain;border-radius:6px;"
                         onerror="this.onerror=null; this.src='/images/medicine-placeholder.jpg';">
                    <div>
                        <div class="fw-bold">${item.medicineName}</div>
                        <div class="text-muted small">${item.manufacturer || ''}</div>
                        <div class="text-muted small">${item.dosage || ''}</div>
                    </div>
                </div>
            </td>
            <td class="text-center">
                <input type="number" min="1" value="${item.quantity}" class="form-control form-control-sm cart-qty" data-id="${item.medicineId}" style="width:80px;margin:0 auto;">
            </td>
            <td class="text-end">₹<span class="unit-price">${(item.unitPrice||0).toFixed(2)}</span></td>
            <td class="text-end">₹<span class="line-total">${(item.totalPrice||0).toFixed(2)}</span></td>
            <td class="text-center">
                <button class="btn btn-sm btn-outline-danger btn-remove" data-id="${item.medicineId}"><i class="fas fa-trash"></i></button>
            </td>
        `;

        tbody.appendChild(tr);
    });

    // Update summary
    if (itemsTotalEl) itemsTotalEl.textContent = `₹${itemsTotal.toFixed(2)}`;
    if (deliveryCostEl) deliveryCostEl.textContent = `₹${delivery.toFixed(2)}`;
    if (grandTotalEl) grandTotalEl.textContent = `₹${grand.toFixed(2)}`;

    // Wire quantity change and remove buttons
    tbody.querySelectorAll('.cart-qty').forEach(input => {
        input.addEventListener('change', async (e) => {
            const newQty = parseInt(e.target.value) || 1;
            const id = e.target.dataset.id;
            await updateCartQuantity(id, newQty);
            await loadCartPage();
        });
    });

    tbody.querySelectorAll('.btn-remove').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            const id = btn.dataset.id;
            await removeFromCart(id);
            await loadCartPage();
        });
    });


}

/**
 * Update cart quantity (best-effort; may rely on server endpoints)
 */
async function updateCartQuantity(medicineId, quantity) {
    try {
        const token = document.querySelector('meta[name="_csrf"]')?.content || '';
        const header = document.querySelector('meta[name="_csrf_header"]')?.content || '';


        // Attempt to call server endpoint for update (if present)
        const resp = await fetch('/api/cart/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify({ medicineId, quantity })
        });

        if (!resp.ok) {
            // ignore if endpoint not implemented, show message if other error
            const text = await resp.text().catch(() => '');
            console.warn('Update cart returned non-ok:', resp.status, text);
        }
    } catch (err) {
        console.error('Error updating cart quantity', err);
    }
}

/**
 * Remove an item from the cart (best-effort)
 */
async function removeFromCart(medicineId) {
    try {
        const token = document.querySelector('meta[name="_csrf"]').content;
        const header = document.querySelector('meta[name="_csrf_header"]').content;

        const resp = await fetch('/api/cart/remove', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify({ medicineId })
        });

        if (!resp.ok) {
            const text = await resp.text().catch(() => '');
            console.warn('Remove item returned non-ok:', resp.status, text);
            showToast('Error', 'Could not remove item from cart', 'error');
        } else {
            showToast('Success', 'Item removed from cart', 'success');
            // Update cart count if server returned totals
            try { const data = await resp.json(); if (data.totalItems !== undefined) updateCartCount(data.totalItems); } catch(e){}
        }
    } catch (err) {
        console.error('Error removing item', err);
        showToast('Error', 'Could not remove item from cart', 'error');
    }
}

/**
 * Gather address form data and send an order request
 */
async function placeOrder() {
    const form = document.getElementById('orderForm');
    if (!form) { showToast('Error', 'Order form not found', 'error'); return; }

    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        showToast('Warning', 'Please fill required address fields', 'warning');
        return;
    }

    const formData = new FormData(form);
    const address = Object.fromEntries(formData.entries());

    // Get current cart items to include in order
    const cartItemsTbody = document.getElementById('cartItemsTbody');
    const orderItems = Array.from(cartItemsTbody.querySelectorAll('tr')).map(row => {
        const qtyInput = row.querySelector('.cart-qty');
        const medicineId = qtyInput.dataset.id;
        const quantity = parseInt(qtyInput.value) || 0;
        const unitPriceText = row.querySelector('.unit-price').textContent.replace('₹', '').trim();
        const unitPrice = parseFloat(unitPriceText) || 0;
        
        return {
            medicineId: medicineId,
            quantity: quantity,
            unitPrice: unitPrice
        };
    });

    // Filter out any invalid items
    const validItems = orderItems.filter(item => item.medicineId && item.quantity > 0 && item.unitPrice > 0);

    if (validItems.length === 0) {
        showToast('Error', 'No valid items in cart', 'error');
        return;
    }

    try {
        const token = document.querySelector('meta[name="_csrf"]').content;
        const header = document.querySelector('meta[name="_csrf_header"]').content;

        console.log('Sending order with address data:', address);

        // Send the address data directly as expected by backend
        const response = await fetch('/api/orders/place', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            },
            body: JSON.stringify(address)
        });

        if (!response.ok) {
            const text = await response.text();
            console.error('Server response:', text);
            try {
                const parsed = JSON.parse(text);
                throw new Error(parsed.message || parsed.error || 'Failed to place order');
            } catch (parseError) {
                throw new Error('Failed to place order. Please try again.');
            }
        }

        const data = await response.json();
        showToast('Success', 'Order placed successfully', 'success');
        // Redirect to orders page
        setTimeout(() => { window.location.href = '/orders'; }, 900);
    } catch (err) {
        console.error('Place order error:', err);
        showToast('Error', err.message || 'Could not place order', 'error');
    }
}

// Make functions available globally
window.medlyPharma = {
    addToCart,
    showToast,
    formatPrice,
    loadCartPage,
    placeOrder,
    removeFromCart
};

// Initialize medicine filters when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeMedicineFilters();
});
