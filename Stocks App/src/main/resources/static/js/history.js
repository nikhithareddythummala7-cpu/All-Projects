// Transaction History JavaScript

document.addEventListener("DOMContentLoaded", () => {
  loadTransactions();
  setupEventListeners();
});

let allTransactions = [];
let filteredTransactions = [];

// Load transactions
async function loadTransactions() {
  try {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      showNotification("Please login to view transaction history", "error");
      window.location.href = "/login";
      return;
    }

    // Load all transactions
    const response = await fetch("/transactions");
    if (response.ok) {
      const transactions = await response.json();
      allTransactions = transactions.filter(transaction => transaction.user === userId);
      filteredTransactions = [...allTransactions];
      renderTransactions();
      calculateSummary();
    } else {
      showNotification("Failed to load transactions", "error");
    }
  } catch (error) {
    console.error("Error loading transactions:", error);
    showNotification("Error loading transactions", "error");
  }
}

// Setup event listeners
function setupEventListeners() {
  document.getElementById("typeFilter").addEventListener("change", applyFilters);
  document.getElementById("dateFrom").addEventListener("change", applyFilters);
  document.getElementById("dateTo").addEventListener("change", applyFilters);
}

// Apply filters
function applyFilters() {
  const typeFilter = document.getElementById("typeFilter").value;
  const dateFrom = document.getElementById("dateFrom").value;
  const dateTo = document.getElementById("dateTo").value;

  filteredTransactions = allTransactions.filter(transaction => {
    // Type filter
    if (typeFilter && transaction.type !== typeFilter && transaction.orderType !== typeFilter) {
      return false;
    }

    // Date filter
    if (dateFrom || dateTo) {
      const transactionDate = new Date(transaction.time || transaction.createdAt || new Date());
      const fromDate = dateFrom ? new Date(dateFrom) : new Date(0);
      const toDate = dateTo ? new Date(dateTo) : new Date();

      if (transactionDate < fromDate || transactionDate > toDate) {
        return false;
      }
    }

    return true;
  });

  renderTransactions();
}

// Clear filters
function clearFilters() {
  document.getElementById("typeFilter").value = "";
  document.getElementById("dateFrom").value = "";
  document.getElementById("dateTo").value = "";
  filteredTransactions = [...allTransactions];
  renderTransactions();
}

// Calculate summary
function calculateSummary() {
  const summary = {
    deposits: 0,
    withdrawals: 0,
    purchases: 0,
    sales: 0
  };

  allTransactions.forEach(transaction => {
    const amount = transaction.amount || 0;
    
    if (transaction.type === "Deposit") {
      summary.deposits += amount;
    } else if (transaction.type === "Withdraw") {
      summary.withdrawals += amount;
    } else if (transaction.orderType === "buy") {
      summary.purchases += amount;
    } else if (transaction.orderType === "sell") {
      summary.sales += amount;
    }
  });

  document.getElementById("totalDeposits").textContent = `$${summary.deposits.toFixed(2)}`;
  document.getElementById("totalWithdrawals").textContent = `$${summary.withdrawals.toFixed(2)}`;
  document.getElementById("totalPurchases").textContent = `$${summary.purchases.toFixed(2)}`;
  document.getElementById("totalSales").textContent = `$${summary.sales.toFixed(2)}`;
}

// Render transactions
function renderTransactions() {
  const tbody = document.getElementById("transactionsTableBody");
  
  if (filteredTransactions.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="6" class="no-transactions">
          <h3>No Transactions Found</h3>
          <p>No transactions match your current filters.</p>
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = filteredTransactions
    .sort((a, b) => new Date(b.time || b.createdAt || 0) - new Date(a.time || a.createdAt || 0))
    .map(transaction => {
      const type = transaction.type || transaction.orderType || "Unknown";
      const amount = transaction.amount || transaction.totalPrice || 0;
      const isPositive = type === "Deposit" || type === "sell";
      const status = transaction.orderStatus || "completed";
      
      return `
        <tr>
          <td>${formatDate(transaction.time || transaction.createdAt)}</td>
          <td>
            <span class="transaction-type ${type.toLowerCase()}">${type}</span>
          </td>
          <td>${getTransactionDescription(transaction)}</td>
          <td>
            <span class="transaction-amount ${isPositive ? 'positive' : 'negative'}">
              ${isPositive ? '+' : '-'}$${amount.toFixed(2)}
            </span>
          </td>
          <td>
            <span class="transaction-status ${status}">${status}</span>
          </td>
          <td>
            <button class="btn-view" onclick="viewTransaction('${transaction._id}')">View</button>
          </td>
        </tr>
      `;
    }).join('');
}

// Get transaction description
function getTransactionDescription(transaction) {
  if (transaction.type === "Deposit" || transaction.type === "Withdraw") {
    return `${transaction.type} via ${transaction.paymentMode || 'N/A'}`;
  } else if (transaction.orderType === "buy" || transaction.orderType === "sell") {
    return `${transaction.orderType.toUpperCase()} ${transaction.count || 0} shares of ${transaction.symbol || 'N/A'}`;
  }
  return "Transaction";
}

// Format date
function formatDate(dateString) {
  if (!dateString) return "N/A";
  
  const date = new Date(dateString);
  return date.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  });
}

// View transaction details
function viewTransaction(transactionId) {
  const transaction = allTransactions.find(t => t._id === transactionId);
  if (!transaction) return;

  const details = document.getElementById("transactionDetails");
  details.innerHTML = `
    <div class="transaction-detail">
      <div class="transaction-detail-label">Transaction ID:</div>
      <div class="transaction-detail-value">${transaction._id}</div>
    </div>
    <div class="transaction-detail">
      <div class="transaction-detail-label">Type:</div>
      <div class="transaction-detail-value">${transaction.type || transaction.orderType || "Unknown"}</div>
    </div>
    <div class="transaction-detail">
      <div class="transaction-detail-label">Amount:</div>
      <div class="transaction-detail-value">$${(transaction.amount || transaction.totalPrice || 0).toFixed(2)}</div>
    </div>
    <div class="transaction-detail">
      <div class="transaction-detail-label">Date:</div>
      <div class="transaction-detail-value">${formatDate(transaction.time || transaction.createdAt)}</div>
    </div>
    <div class="transaction-detail">
      <div class="transaction-detail-label">Status:</div>
      <div class="transaction-detail-value">${transaction.orderStatus || "completed"}</div>
    </div>
    ${transaction.symbol ? `
    <div class="transaction-detail">
      <div class="transaction-detail-label">Symbol:</div>
      <div class="transaction-detail-value">${transaction.symbol}</div>
    </div>
    ` : ''}
    ${transaction.count ? `
    <div class="transaction-detail">
      <div class="transaction-detail-label">Quantity:</div>
      <div class="transaction-detail-value">${transaction.count}</div>
    </div>
    ` : ''}
    ${transaction.paymentMode ? `
    <div class="transaction-detail">
      <div class="transaction-detail-label">Payment Method:</div>
      <div class="transaction-detail-value">${transaction.paymentMode}</div>
    </div>
    ` : ''}
  `;

  document.getElementById("transactionModal").style.display = "block";
}

// Close modal
function closeModal(modalId) {
  document.getElementById(modalId).style.display = "none";
}

// Export transactions to CSV
function exportTransactions() {
  if (filteredTransactions.length === 0) {
    showNotification("No transactions to export", "error");
    return;
  }

  const csvContent = generateCSV(filteredTransactions);
  downloadCSV(csvContent, "transactions.csv");
  showNotification("Transactions exported successfully!", "success");
}

// Generate CSV content
function generateCSV(transactions) {
  const headers = ["Date", "Type", "Description", "Amount", "Status"];
  const rows = transactions.map(transaction => {
    const type = transaction.type || transaction.orderType || "Unknown";
    const amount = transaction.amount || transaction.totalPrice || 0;
    const isPositive = type === "Deposit" || type === "sell";
    const description = getTransactionDescription(transaction);
    const status = transaction.orderStatus || "completed";
    const date = formatDate(transaction.time || transaction.createdAt);
    
    return [
      date,
      type,
      description,
      `${isPositive ? '+' : '-'}$${amount.toFixed(2)}`,
      status
    ];
  });

  return [headers, ...rows].map(row => 
    row.map(field => `"${field}"`).join(",")
  ).join("\n");
}

// Download CSV file
function downloadCSV(csvContent, filename) {
  const blob = new Blob([csvContent], { type: "text/csv" });
  const url = window.URL.createObjectURL(blob);
  const a = document.createElement("a");
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
  window.URL.revokeObjectURL(url);
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

// Close modals when clicking outside
window.onclick = function(event) {
  const modals = document.querySelectorAll('.modal');
  modals.forEach(modal => {
    if (event.target === modal) {
      modal.style.display = "none";
    }
  });
}
