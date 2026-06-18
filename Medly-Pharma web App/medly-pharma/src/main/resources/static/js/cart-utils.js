// Global variable to store the current cart count
let currentCartCount = 0;

/**
 * Update the cart count in the navigation bar
 * @param {number} count - The new cart count
 */
function updateCartCount(count) {
    // Ensure count is a number
    count = parseInt(count) || 0;
    currentCartCount = count;
    
    // Update all cart count elements on the page
    document.querySelectorAll('.cart-count').forEach(element => {
        element.textContent = count;
        element.style.display = count > 0 ? 'inline-block' : 'none';
    });
}

/**
 * Fetch and update the cart count from the server
 * @returns {Promise<number>} The current cart count
 */
function fetchAndUpdateCartCount() {
    return fetch('/api/cart/count', {
        credentials: 'same-origin', // Include cookies for authentication
        headers: {
            'Accept': 'application/json',
            'Cache-Control': 'no-cache, no-store, must-revalidate',
            'Pragma': 'no-cache',
            'Expires': '0'
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to fetch cart count: ' + response.status);
        }
        return response.json();
    })
    .then(data => {
        const count = data && typeof data.count === 'number' ? data.count : 0;
        updateCartCount(count);
        return count;
    })
    .catch(error => {
        console.error('Error fetching cart count:', error);
        updateCartCount(0); // Reset to 0 on error
        return 0;
    });
}

// Initialize cart count when the page loads and set up periodic refresh
document.addEventListener('DOMContentLoaded', function() {
    // Initial fetch
    fetchAndUpdateCartCount();
    
    // Set up periodic refresh (every 30 seconds)
    setInterval(fetchAndUpdateCartCount, 30000);
    
    // Also update cart count when the page becomes visible again
    document.addEventListener('visibilitychange', function() {
        if (!document.hidden) {
            fetchAndUpdateCartCount();
        }
    });
});

// Make functions available globally
window.updateCartCount = updateCartCount;
window.fetchAndUpdateCartCount = fetchAndUpdateCartCount;

/**
 * Show a toast notification
 * @param {string} title - The title of the toast
 * @param {string} message - The message to display
 * @param {'success'|'error'|'info'} type - The type of toast
 */
function showToast(title, message, type = 'info') {
    // Implementation of showToast if not already defined
    if (typeof window.showToast !== 'function') {
        const toastContainer = document.createElement('div');
        toastContainer.className = 'position-fixed bottom-0 end-0 p-3';
        toastContainer.style.zIndex = '11';
        document.body.appendChild(toastContainer);
        
        const toastId = 'toast-' + Date.now();
        const toastHtml = `
            <div id="${toastId}" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="toast-header">
                    <strong class="me-auto">${title}</strong>
                    <button type="button" class="btn-close" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
                <div class="toast-body">
                    ${message}
                </div>
            </div>
        `;
        
        toastContainer.insertAdjacentHTML('beforeend', toastHtml);
        const toastElement = document.getElementById(toastId);
        const toast = new bootstrap.Toast(toastElement, { autohide: true, delay: 3000 });
        toast.show();
        
        // Remove the toast from DOM after it's hidden
        toastElement.addEventListener('hidden.bs.toast', function () {
            toastElement.remove();
            if (toastContainer.children.length === 0) {
                toastContainer.remove();
            }
        });
    } else {
        window.showToast(title, message, type);
    }
}
