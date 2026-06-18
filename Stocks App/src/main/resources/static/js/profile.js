// Profile Management JavaScript

document.addEventListener("DOMContentLoaded", () => {
  loadUserProfile();
  setupEventListeners();
});

let userProfile = {};

// Load user profile
async function loadUserProfile() {
  try {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      showNotification("Please login to view profile", "error");
      window.location.href = "/login";
      return;
    }

    const response = await fetch(`/fetch-user/${userId}`);
    if (response.ok) {
      userProfile = await response.json();
      populateProfileData();
    } else {
      showNotification("Failed to load profile", "error");
    }
  } catch (error) {
    console.error("Error loading profile:", error);
    showNotification("Error loading profile", "error");
  }
}

// Populate profile data
function populateProfileData() {
  // Basic info
  document.getElementById("userName").textContent = userProfile.username || "User";
  document.getElementById("userEmail").textContent = userProfile.email || "user@example.com";
  document.getElementById("userType").textContent = userProfile.usertype || "Customer";
  
  // Avatar initials
  const initials = (userProfile.username || "U").charAt(0).toUpperCase();
  document.getElementById("avatarInitials").textContent = initials;
  
  // Member since
  const memberSince = new Date(userProfile.createdAt || Date.now());
  document.getElementById("memberSince").textContent = memberSince.toLocaleDateString('en-US', { 
    year: 'numeric', 
    month: 'short' 
  });
  
  // Stats (mock data for now)
  document.getElementById("totalTrades").textContent = "0";
  document.getElementById("successRate").textContent = "0%";
  
  // Personal info form
  document.getElementById("firstName").value = userProfile.firstName || "";
  document.getElementById("lastName").value = userProfile.lastName || "";
  document.getElementById("email").value = userProfile.email || "";
  document.getElementById("phone").value = userProfile.phone || "";
  document.getElementById("address").value = userProfile.address || "";
  document.getElementById("city").value = userProfile.city || "";
  document.getElementById("state").value = userProfile.state || "";
  document.getElementById("zipCode").value = userProfile.zipCode || "";
  
  // Preferences
  document.getElementById("defaultOrderType").value = userProfile.defaultOrderType || "market";
  document.getElementById("riskTolerance").value = userProfile.riskTolerance || "moderate";
  document.getElementById("maxOrderAmount").value = userProfile.maxOrderAmount || "";
  document.getElementById("theme").value = userProfile.theme || "light";
  document.getElementById("currency").value = userProfile.currency || "USD";
  document.getElementById("timezone").value = userProfile.timezone || "UTC";
  
  // Notifications
  const emailNotifications = userProfile.emailNotifications || [];
  const pushNotifications = userProfile.pushNotifications || [];
  
  document.querySelectorAll('input[name="emailNotifications"]').forEach(checkbox => {
    checkbox.checked = emailNotifications.includes(checkbox.value);
  });
  
  document.querySelectorAll('input[name="pushNotifications"]').forEach(checkbox => {
    checkbox.checked = pushNotifications.includes(checkbox.value);
  });
}

// Setup event listeners
function setupEventListeners() {
  // Personal form
  document.getElementById("personalForm").addEventListener("submit", handlePersonalUpdate);
  
  // Password form
  document.getElementById("passwordForm").addEventListener("submit", handlePasswordChange);
  
  // Preferences form
  document.getElementById("preferencesForm").addEventListener("submit", handlePreferencesUpdate);
  
  // Display form
  document.getElementById("displayForm").addEventListener("submit", handleDisplayUpdate);
  
  // Email notifications form
  document.getElementById("emailNotificationsForm").addEventListener("submit", handleEmailNotificationsUpdate);
  
  // Push notifications form
  document.getElementById("pushNotificationsForm").addEventListener("submit", handlePushNotificationsUpdate);
}

// Show tab
function showTab(tabName) {
  // Hide all tabs
  document.querySelectorAll('.tab-content').forEach(tab => {
    tab.classList.remove('active');
  });
  
  // Remove active class from all buttons
  document.querySelectorAll('.tab-btn').forEach(btn => {
    btn.classList.remove('active');
  });
  
  // Show selected tab
  document.getElementById(tabName + 'Tab').classList.add('active');
  
  // Add active class to clicked button
  event.target.classList.add('active');
}

// Handle personal info update
async function handlePersonalUpdate(e) {
  e.preventDefault();
  
  const formData = new FormData(e.target);
  const personalData = Object.fromEntries(formData.entries());
  
  try {
    const response = await fetch(`/update-profile/${userProfile._id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(personalData)
    });
    
    if (response.ok) {
      showNotification("Personal information updated successfully!", "success");
      loadUserProfile(); // Reload profile
    } else {
      showNotification("Failed to update personal information", "error");
    }
  } catch (error) {
    console.error("Error updating personal info:", error);
    showNotification("Error updating personal information", "error");
  }
}

// Handle password change
async function handlePasswordChange(e) {
  e.preventDefault();
  
  const currentPassword = document.getElementById("currentPassword").value;
  const newPassword = document.getElementById("newPassword").value;
  const confirmPassword = document.getElementById("confirmPassword").value;
  
  if (newPassword !== confirmPassword) {
    showNotification("New passwords do not match", "error");
    return;
  }
  
  if (newPassword.length < 6) {
    showNotification("Password must be at least 6 characters long", "error");
    return;
  }
  
  try {
    const response = await fetch(`/change-password/${userProfile._id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        currentPassword,
        newPassword
      })
    });
    
    if (response.ok) {
      showNotification("Password changed successfully!", "success");
      e.target.reset();
    } else {
      showNotification("Failed to change password", "error");
    }
  } catch (error) {
    console.error("Error changing password:", error);
    showNotification("Error changing password", "error");
  }
}

// Handle preferences update
async function handlePreferencesUpdate(e) {
  e.preventDefault();
  
  const formData = new FormData(e.target);
  const preferencesData = Object.fromEntries(formData.entries());
  
  try {
    const response = await fetch(`/update-preferences/${userProfile._id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(preferencesData)
    });
    
    if (response.ok) {
      showNotification("Trading preferences updated successfully!", "success");
    } else {
      showNotification("Failed to update preferences", "error");
    }
  } catch (error) {
    console.error("Error updating preferences:", error);
    showNotification("Error updating preferences", "error");
  }
}

// Handle display update
async function handleDisplayUpdate(e) {
  e.preventDefault();
  
  const formData = new FormData(e.target);
  const displayData = Object.fromEntries(formData.entries());
  
  try {
    const response = await fetch(`/update-display/${userProfile._id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(displayData)
    });
    
    if (response.ok) {
      showNotification("Display settings updated successfully!", "success");
      // Apply theme if changed
      if (displayData.theme) {
        applyTheme(displayData.theme);
      }
    } else {
      showNotification("Failed to update display settings", "error");
    }
  } catch (error) {
    console.error("Error updating display settings:", error);
    showNotification("Error updating display settings", "error");
  }
}

// Handle email notifications update
async function handleEmailNotificationsUpdate(e) {
  e.preventDefault();
  
  const checkboxes = document.querySelectorAll('input[name="emailNotifications"]:checked');
  const emailNotifications = Array.from(checkboxes).map(cb => cb.value);
  
  try {
    const response = await fetch(`/update-email-notifications/${userProfile._id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ emailNotifications })
    });
    
    if (response.ok) {
      showNotification("Email notification settings updated successfully!", "success");
    } else {
      showNotification("Failed to update email notifications", "error");
    }
  } catch (error) {
    console.error("Error updating email notifications:", error);
    showNotification("Error updating email notifications", "error");
  }
}

// Handle push notifications update
async function handlePushNotificationsUpdate(e) {
  e.preventDefault();
  
  const checkboxes = document.querySelectorAll('input[name="pushNotifications"]:checked');
  const pushNotifications = Array.from(checkboxes).map(cb => cb.value);
  
  try {
    const response = await fetch(`/update-push-notifications/${userProfile._id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ pushNotifications })
    });
    
    if (response.ok) {
      showNotification("Push notification settings updated successfully!", "success");
    } else {
      showNotification("Failed to update push notifications", "error");
    }
  } catch (error) {
    console.error("Error updating push notifications:", error);
    showNotification("Error updating push notifications", "error");
  }
}

// Change avatar
function changeAvatar() {
  // In a real app, this would open a file picker
  showNotification("Avatar change feature coming soon!", "info");
}

// Apply theme
function applyTheme(theme) {
  if (theme === 'dark') {
    document.body.classList.add('dark-theme');
  } else {
    document.body.classList.remove('dark-theme');
  }
}

// Notification helper
function showNotification(message, type = "info") {
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
  `;
  
  notification.textContent = message;
  document.body.appendChild(notification);

  setTimeout(() => {
    notification.style.animation = 'slideOutRight 0.3s ease-in';
    setTimeout(() => notification.remove(), 300);
  }, 3000);
}
