// Main JavaScript for general frontend logic

// Navigation functions
function navigateTo(page) {
    window.location.href = page;
}

// Logout function
function logout() {
    // Clear JWT token from localStorage
    localStorage.removeItem('jwtToken');
    // Clear JWT cookie
    document.cookie = 'jwt=; path=/; expires=Thu, 01 Jan 1970 00:00:00 GMT';
    // Redirect to login page
    navigateTo('/login');
}

// Check if user is logged in
function isLoggedIn() {
    return localStorage.getItem('jwtToken') !== null;
}

// Get JWT token
function getToken() {
    return localStorage.getItem('jwtToken');
}

// Set JWT token
function setToken(token) {
    localStorage.setItem('jwtToken', token);
}

// Make authenticated API request
function apiRequest(url, options = {}) {
    const token = getToken();
    if (token) {
        options.headers = {
            ...options.headers,
            'Authorization': 'Bearer ' + token
        };
    }
    return fetch(url, options);
}

// Show/hide elements based on authentication
function updateUI() {
    const loggedIn = isLoggedIn();
    const authElements = document.querySelectorAll('.auth-required');
    const noAuthElements = document.querySelectorAll('.no-auth');

    authElements.forEach(el => el.style.display = loggedIn ? 'block' : 'none');
    noAuthElements.forEach(el => el.style.display = loggedIn ? 'none' : 'block');
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    updateUI();
});
