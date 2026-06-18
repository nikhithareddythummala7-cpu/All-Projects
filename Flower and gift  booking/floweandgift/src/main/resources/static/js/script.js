// Basic JavaScript for interactivity

// Example: Update cart quantity via AJAX (placeholder)
function updateCart(productId, quantity) {
    fetch('/cart/update', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'productId=' + productId + '&quantity=' + quantity
    })
    .then(response => response.text())
    .then(data => {
        // Update UI
        location.reload();
    });
}

// Add event listeners if needed
document.addEventListener('DOMContentLoaded', function() {
    // Add to cart buttons
    const addToCartButtons = document.querySelectorAll('.add-to-cart');
    addToCartButtons.forEach(button => {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            updateCart(productId, 1);
        });
    });

    // Handle delivery date picker visibility
    const deliveryDayOptions = document.querySelectorAll('input[name="deliveryDayOption"]');
    const datePickerContainer = document.getElementById('datePickerContainer');
    const deliveryDateInput = document.getElementById('deliveryDate');

    // Set minimum date to 2 days from today
    if (deliveryDateInput) {
        const minDate = new Date();
        minDate.setDate(minDate.getDate() + 2);
        deliveryDateInput.min = minDate.toISOString().split('T')[0];

        // Set maximum date to 7 days from today
        const maxDate = new Date();
        maxDate.setDate(maxDate.getDate() + 7);
        deliveryDateInput.max = maxDate.toISOString().split('T')[0];
    }

    // Show/hide date picker based on selection
    deliveryDayOptions.forEach(option => {
        option.addEventListener('change', function() {
            if (this.value === 'LATER') {
                datePickerContainer.style.display = 'block';
            } else {
                datePickerContainer.style.display = 'none';
            }
        });
    });
});

