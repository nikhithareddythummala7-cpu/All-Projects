// Restaurant JavaScript for handling restaurant-related frontend logic

// Load restaurants
function loadRestaurants() {
    apiRequest('/api/restaurants')
        .then(response => response.json())
        .then(data => {
            displayRestaurants(data);
        })
        .catch(error => {
            console.error('Error loading restaurants:', error);
            alert('Error loading restaurants. Please try again.');
        });
}

// Display restaurants
function displayRestaurants(restaurants) {
    const container = document.getElementById('restaurants-list');
    container.innerHTML = '';

    if (restaurants.length === 0) {
        container.innerHTML = '<p>No restaurants available.</p>';
        return;
    }

    restaurants.forEach(restaurant => {
        const restaurantDiv = document.createElement('div');
        restaurantDiv.className = 'restaurant-card';
        restaurantDiv.innerHTML = `
            <h3>${restaurant.name}</h3>
            <p>${restaurant.description}</p>
            <p>Location: ${restaurant.location}</p>
            <p>Cuisine: ${restaurant.cuisineType}</p>
            <a href="/restaurants/${restaurant.id}" class="btn">View Details</a>
        `;
        container.appendChild(restaurantDiv);
    });
}

// Load restaurant details
function loadRestaurantDetails(restaurantId) {
    apiRequest(`/api/restaurants/${restaurantId}`)
        .then(response => response.json())
        .then(data => {
            displayRestaurantDetails(data);
        })
        .catch(error => {
            console.error('Error loading restaurant details:', error);
            alert('Error loading restaurant details. Please try again.');
        });
}

// Display restaurant details
function displayRestaurantDetails(restaurant) {
    document.getElementById('restaurant-name').textContent = restaurant.name;
    document.getElementById('restaurant-description').textContent = restaurant.description;
    document.getElementById('restaurant-location').textContent = restaurant.location;
    document.getElementById('restaurant-cuisine').textContent = restaurant.cuisineType;
    document.getElementById('restaurant-contact').textContent = restaurant.contactInfo;

    // Load tables for this restaurant
    loadRestaurantTables(restaurant.id);
}

// Load tables for a restaurant
function loadRestaurantTables(restaurantId) {
    apiRequest(`/api/restaurants/${restaurantId}/tables`)
        .then(response => response.json())
        .then(data => {
            displayRestaurantTables(data);
        })
        .catch(error => {
            console.error('Error loading restaurant tables:', error);
            alert('Error loading restaurant tables. Please try again.');
        });
}

// Display restaurant tables
function displayRestaurantTables(tables) {
    const container = document.getElementById('restaurant-tables');
    container.innerHTML = '';

    if (tables.length === 0) {
        container.innerHTML = '<p>No tables available.</p>';
        return;
    }

    tables.forEach(table => {
        const tableDiv = document.createElement('div');
        tableDiv.className = 'table-item';
        tableDiv.innerHTML = `
            <h4>Table ${table.tableNumber}</h4>
            <p>Capacity: ${table.capacity}</p>
        `;
        container.appendChild(tableDiv);
    });
}

// Submit review
function submitReview(restaurantId) {
    const rating = document.getElementById('rating').value;
    const comment = document.getElementById('comment').value;

    const reviewData = {
        restaurantId: restaurantId,
        rating: parseInt(rating),
        comment: comment
    };

    apiRequest('/api/reviews', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(reviewData)
    })
    .then(response => {
        if (response.ok) {
            alert('Review submitted successfully!');
            document.getElementById('review-form').reset();
            loadRestaurantReviews(restaurantId);
        } else {
            throw new Error('Failed to submit review');
        }
    })
    .catch(error => {
        console.error('Error submitting review:', error);
        alert('Error submitting review. Please try again.');
    });
}

// Load reviews for a restaurant
function loadRestaurantReviews(restaurantId) {
    apiRequest(`/api/restaurants/${restaurantId}/reviews`)
        .then(response => response.json())
        .then(data => {
            displayRestaurantReviews(data);
        })
        .catch(error => {
            console.error('Error loading reviews:', error);
            alert('Error loading reviews. Please try again.');
        });
}

// Display restaurant reviews
function displayRestaurantReviews(reviews) {
    const container = document.getElementById('restaurant-reviews');
    container.innerHTML = '';

    if (reviews.length === 0) {
        container.innerHTML = '<p>No reviews yet.</p>';
        return;
    }

    reviews.forEach(review => {
        const reviewDiv = document.createElement('div');
        reviewDiv.className = 'review-item';
        reviewDiv.innerHTML = `
            <h5>Rating: ${review.rating}/5</h5>
            <p>${review.comment}</p>
            <small>By ${review.userName} on ${new Date(review.createdAt).toLocaleDateString()}</small>
        `;
        container.appendChild(reviewDiv);
    });
}

function submitBooking() {
    const form = document.getElementById('booking-form');
    const formData = new FormData(form);

    const date = formData.get('date'); // yyyy-mm-dd
    const startTime = formData.get('startTime'); // hh:mm
    const endTime = formData.get('endTime'); // hh:mm

    if (!date || !startTime || !endTime) { alert('Choose date and start/end times'); return; }

    const startIso = `${date}T${startTime}:00`;
    const endIso = `${date}T${endTime}:00`;

    const bookingData = new URLSearchParams();
    bookingData.append('tableId', formData.get('tableId'));
    bookingData.append('startTime', startIso);
    bookingData.append('endTime', endIso);
    bookingData.append('numberOfGuests', formData.get('guests') || '1');

    fetch('/api/bookings/create', {
        method: 'POST',
        headers: {
            'Authorization': 'Bearer ' + (localStorage.getItem('jwtToken') || ''),
            'Content-Type': 'application/x-www-form-urlencoded'
        },
        body: bookingData.toString()
    })
    .then(async res => {
        if (res.ok) {
            alert('Booking created successfully!');
            window.location.href = '/bookings/my?success=true';
        } else {
            const err = await res.json().catch(()=>({error:'Unknown'}));
            alert('Failed to create booking: ' + (err.error || JSON.stringify(err)));
        }
    })
    .catch(e => { console.error(e); alert('Error creating booking'); });
}


// Initialize restaurant pages
document.addEventListener('DOMContentLoaded', function() {
    if (document.getElementById('restaurants-list')) {
        // Restaurant list page
        loadRestaurants();
    }

    if (document.getElementById('restaurant-details')) {
        // Restaurant details page
        const restaurantId = window.location.pathname.split('/').pop();
        loadRestaurantDetails(restaurantId);
        loadRestaurantReviews(restaurantId);

        // Review form
        const reviewForm = document.getElementById('review-form');
        if (reviewForm) {
            reviewForm.addEventListener('submit', function(e) {
                e.preventDefault();
                submitReview(restaurantId);
            });
        }
    }
});
