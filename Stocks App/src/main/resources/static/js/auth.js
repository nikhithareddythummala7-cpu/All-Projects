// Enhanced auth.js with better UX

// 🔑 Login
async function login(email, password) {
  try {
    // Show loading state
    const loginBtn = document.querySelector('button[type="submit"]');
    const originalText = loginBtn.textContent;
    loginBtn.textContent = 'Logging in...';
    loginBtn.disabled = true;

    const loginInputs = { email, password };

    const res = await fetch("http://localhost:6001/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(loginInputs)
    });

    if (!res.ok) {
      const errorData = await res.json();
      showNotification(errorData.message || "Login failed. Please check your credentials.", "error");
      return;
    }

    const data = await res.json();

    // Store data in localStorage
    localStorage.setItem("userId", data._id);
    localStorage.setItem("userType", data.usertype);
    localStorage.setItem("userName", data.username);
    localStorage.setItem("userEmail", data.email);
    localStorage.setItem("balance", data.balance);

    showNotification(`Welcome back, ${data.username}!`, "success");

    // Redirect based on user type
    setTimeout(() => {
      if (data.usertype === "customer") {
        window.location.href = "/";
      } else if (data.usertype === "admin") {
        window.location.href = "/admin";
      }
    }, 1000);

  } catch (err) {
    console.error(err);
    showNotification("Login failed. Please try again.", "error");
  } finally {
    // Reset button state
    const loginBtn = document.querySelector('button[type="submit"]');
    if (loginBtn) {
      loginBtn.textContent = 'Sign in';
      loginBtn.disabled = false;
    }
  }
}

// 📝 Register
async function register(username, email, password, usertype) {
  try {
    // Show loading state
    const registerBtn = document.querySelector('button[type="submit"]');
    const originalText = registerBtn.textContent;
    registerBtn.textContent = 'Creating account...';
    registerBtn.disabled = true;

    const inputs = { username, email, password, usertype };

    const res = await fetch("http://localhost:6001/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(inputs)
    });

    if (!res.ok) {
      const errorData = await res.json();
      showNotification(errorData.message || "Registration failed. Please try again.", "error");
      return;
    }

    const data = await res.json();

    localStorage.setItem("userId", data._id);
    localStorage.setItem("userType", data.usertype);
    localStorage.setItem("userName", data.username);
    localStorage.setItem("userEmail", data.email);
    localStorage.setItem("balance", data.balance);

    showNotification(`Account created successfully! Welcome, ${data.username}!`, "success");

    // Redirect based on user type
    setTimeout(() => {
      if (data.usertype === "customer") {
        window.location.href = "/";
      } else if (data.usertype === "admin") {
        window.location.href = "/admin";
      }
    }, 1500);

  } catch (err) {
    console.error(err);
    showNotification("Registration failed. Please try again.", "error");
  } finally {
    // Reset button state
    const registerBtn = document.querySelector('button[type="submit"]');
    if (registerBtn) {
      registerBtn.textContent = 'Sign up';
      registerBtn.disabled = false;
    }
  }
}

// 🚪 Logout
function logout() {
  if (confirm("Are you sure you want to logout?")) {
    localStorage.clear();
    showNotification("You have been logged out successfully!", "success");
    setTimeout(() => {
      window.location.href = "/";
    }, 1000);
  }
}

// Notification helper
function showNotification(message, type = "info") {
  // Remove existing notifications
  const existingNotifications = document.querySelectorAll('.notification');
  existingNotifications.forEach(notification => notification.remove());

  const notification = document.createElement('div');
  notification.className = `notification notification-${type}`;
  notification.style.cssText = `
    position: fixed;
    top: 20px;
    right: 20px;
    background: ${type === 'success' ? '#4CAF50' : type === 'error' ? '#f44336' : '#2196F3'};
    color: white;
    padding: 1rem 1.5rem;
    border-radius: 8px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.2);
    z-index: 10000;
    animation: slideInRight 0.3s ease-out;
    max-width: 300px;
    font-weight: 500;
    font-size: 0.9rem;
  `;
  
  notification.textContent = message;
  document.body.appendChild(notification);

  // Auto remove after 4 seconds
  setTimeout(() => {
    notification.style.animation = 'slideOutRight 0.3s ease-in';
    setTimeout(() => notification.remove(), 300);
  }, 4000);
}

// Add CSS for notifications if not already present
if (!document.querySelector('#notification-styles')) {
  const style = document.createElement('style');
  style.id = 'notification-styles';
  style.textContent = `
    @keyframes slideInRight {
      from {
        transform: translateX(100%);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }
    
    @keyframes slideOutRight {
      from {
        transform: translateX(0);
        opacity: 1;
      }
      to {
        transform: translateX(100%);
        opacity: 0;
      }
    }
  `;
  document.head.appendChild(style);
}

// Form validation helpers
function validateEmail(email) {
  const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  return re.test(email);
}

function validatePassword(password) {
  return password.length >= 6;
}

function validateUsername(username) {
  return username.length >= 3;
}

// Enhanced form validation
function validateLoginForm(email, password) {
  if (!email || !password) {
    showNotification("Please fill in all fields", "error");
    return false;
  }
  
  if (!validateEmail(email)) {
    showNotification("Please enter a valid email address", "error");
    return false;
  }
  
  if (!validatePassword(password)) {
    showNotification("Password must be at least 6 characters long", "error");
    return false;
  }
  
  return true;
}

function validateRegisterForm(username, email, password, usertype) {
  if (!username || !email || !password || !usertype) {
    showNotification("Please fill in all fields", "error");
    return false;
  }
  
  if (!validateUsername(username)) {
    showNotification("Username must be at least 3 characters long", "error");
    return false;
  }
  
  if (!validateEmail(email)) {
    showNotification("Please enter a valid email address", "error");
    return false;
  }
  
  if (!validatePassword(password)) {
    showNotification("Password must be at least 6 characters long", "error");
    return false;
  }
  
  return true;
}