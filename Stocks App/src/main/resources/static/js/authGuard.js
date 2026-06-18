// authGuard.js
function protectPage() {
  if (!localStorage.getItem("userType")) {
    window.location.href = "/login.html";
  }
}

function protectAdminPage() {
  const userType = localStorage.getItem("userType");
  if (userType !== "admin") {
    window.location.href = "/login.html";
  }
}

function protectCustomerPage() {
  const userType = localStorage.getItem("userType");
  if (userType !== "customer") {
    window.location.href = "/login.html";
  }
}
