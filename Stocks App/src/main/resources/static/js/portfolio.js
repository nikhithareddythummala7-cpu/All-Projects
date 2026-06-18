// Portfolio Management JavaScript

document.addEventListener("DOMContentLoaded", () => {
  loadPortfolioData();
  setupEventListeners();
});

let portfolioData = {
  stocks: [],
  transactions: [],
  balance: 0,
  totalValue: 0,
  totalGainLoss: 0
};

// Load portfolio data
async function loadPortfolioData() {
  try {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      showNotification("Please login to view portfolio", "error");
      window.location.href = "/login";
      return;
    }

    // Load user data
    const userResponse = await fetch(`/fetch-user/${userId}`);
    if (userResponse.ok) {
      const userData = await userResponse.json();
      portfolioData.balance = userData.balance || 0;
      updateBalanceDisplay();
    }

    // Load stocks
    const stocksResponse = await fetch("/fetch-stocks");
    if (stocksResponse.ok) {
      const allStocks = await stocksResponse.json();
      portfolioData.stocks = allStocks.filter(stock => stock.user === userId);
      renderHoldings();
      calculateTotalValue();
    }

    // Load transactions
    const transactionsResponse = await fetch("/transactions");
    if (transactionsResponse.ok) {
      const allTransactions = await transactionsResponse.json();
      portfolioData.transactions = allTransactions.filter(transaction => transaction.user === userId);
      renderTransactions();
    }

  } catch (error) {
    console.error("Error loading portfolio data:", error);
    showNotification("Error loading portfolio data", "error");
  }
}

// Setup event listeners
function setupEventListeners() {
  // Buy form
  document.getElementById("buyForm").addEventListener("submit", handleBuyStock);
  
  // Sell form
  document.getElementById("sellForm").addEventListener("submit", handleSellStock);
  
  // Deposit form
  document.getElementById("depositForm").addEventListener("submit", handleDeposit);
  
  // Withdraw form
  document.getElementById("withdrawForm").addEventListener("submit", handleWithdraw);

  // Calculate totals on input change
  document.getElementById("buyQuantity").addEventListener("input", calculateBuyTotal);
  document.getElementById("buyPrice").addEventListener("input", calculateBuyTotal);
  document.getElementById("sellQuantity").addEventListener("input", calculateSellTotal);
  document.getElementById("sellPrice").addEventListener("input", calculateSellTotal);
}

// Update balance display
function updateBalanceDisplay() {
  document.getElementById("availableBalance").textContent = `$${portfolioData.balance.toFixed(2)}`;
}

// Calculate total value
function calculateTotalValue() {
  portfolioData.totalValue = portfolioData.stocks.reduce((total, stock) => {
    return total + (stock.totalPrice || 0);
  }, 0);
  
  document.getElementById("totalValue").textContent = `$${portfolioData.totalValue.toFixed(2)}`;
  
  // Calculate gain/loss (simplified - in real app, you'd compare with purchase price)
  portfolioData.totalGainLoss = portfolioData.totalValue * 0.05; // Mock 5% gain
  const gainLossElement = document.getElementById("totalGainLoss");
  gainLossElement.textContent = `$${portfolioData.totalGainLoss.toFixed(2)}`;
  gainLossElement.className = portfolioData.totalGainLoss >= 0 ? "gain-loss" : "gain-loss negative";
}

// Render holdings
function renderHoldings() {
  const holdingsGrid = document.getElementById("holdingsGrid");
  
  if (portfolioData.stocks.length === 0) {
    holdingsGrid.innerHTML = `
      <div class="no-holdings">
        <h3>No Holdings</h3>
        <p>You don't have any stocks in your portfolio yet.</p>
        <button class="btn-primary" onclick="showBuyModal()">Buy Your First Stock</button>
      </div>
    `;
    return;
  }

  holdingsGrid.innerHTML = portfolioData.stocks.map(stock => `
    <div class="holding-card">
      <div class="holding-symbol">${stock.symbol}</div>
      <div class="holding-name">${stock.name}</div>
      <div class="holding-details">
        <div class="holding-detail">
          <div class="holding-detail-label">Shares</div>
          <div class="holding-detail-value">${stock.count}</div>
        </div>
        <div class="holding-detail">
          <div class="holding-detail-label">Avg Price</div>
          <div class="holding-detail-value">$${stock.price.toFixed(2)}</div>
        </div>
        <div class="holding-detail">
          <div class="holding-detail-label">Current Price</div>
          <div class="holding-detail-value">$${(stock.price * 1.05).toFixed(2)}</div>
        </div>
        <div class="holding-detail">
          <div class="holding-detail-label">Gain/Loss</div>
          <div class="holding-detail-value" style="color: #28a745;">+5.0%</div>
        </div>
      </div>
      <div class="holding-total">
        <div class="holding-total-label">Total Value</div>
        <div class="holding-total-value">$${((stock.totalPrice || 0) * 1.05).toFixed(2)}</div>
      </div>
    </div>
  `).join('');
}

// Render transactions
function renderTransactions() {
  const transactionsList = document.getElementById("transactionsList");
  
  if (portfolioData.transactions.length === 0) {
    transactionsList.innerHTML = `
      <div class="no-transactions">
        <p>No transactions yet.</p>
      </div>
    `;
    return;
  }

  transactionsList.innerHTML = portfolioData.transactions.slice(0, 10).map(transaction => `
    <div class="transaction-item">
      <div class="transaction-info">
        <div class="transaction-type">${transaction.type}</div>
        <div class="transaction-symbol">${transaction.symbol || 'N/A'}</div>
        <div class="transaction-details">${transaction.paymentMode || 'N/A'}</div>
        <div class="transaction-date">${transaction.time || 'N/A'}</div>
      </div>
      <div class="transaction-amount ${transaction.type === 'Deposit' ? 'positive' : 'negative'}">
        ${transaction.type === 'Deposit' ? '+' : '-'}$${transaction.amount.toFixed(2)}
      </div>
    </div>
  `).join('');
}

// Modal functions
function showBuyModal() {
  document.getElementById("buyModal").style.display = "block";
  document.getElementById("buySymbol").focus();
}

function showSellModal() {
  const sellSymbol = document.getElementById("sellSymbol");
  sellSymbol.innerHTML = '<option value="">Select stock to sell</option>';
  
  portfolioData.stocks.forEach(stock => {
    const option = document.createElement('option');
    option.value = stock.symbol;
    option.textContent = `${stock.symbol} - ${stock.name} (${stock.count} shares)`;
    sellSymbol.appendChild(option);
  });
  
  document.getElementById("sellModal").style.display = "block";
}

function showDepositModal() {
  document.getElementById("depositModal").style.display = "block";
  document.getElementById("depositAmount").focus();
}

function showWithdrawModal() {
  document.getElementById("withdrawModal").style.display = "block";
  document.getElementById("withdrawAmount").focus();
}

function closeModal(modalId) {
  document.getElementById(modalId).style.display = "none";
  document.getElementById(modalId.replace('Modal', 'Form')).reset();
}

// Calculate totals
function calculateBuyTotal() {
  const quantity = parseFloat(document.getElementById("buyQuantity").value) || 0;
  const price = parseFloat(document.getElementById("buyPrice").value) || 0;
  const total = quantity * price;
  document.getElementById("buyTotal").value = total.toFixed(2);
}

function calculateSellTotal() {
  const quantity = parseFloat(document.getElementById("sellQuantity").value) || 0;
  const price = parseFloat(document.getElementById("sellPrice").value) || 0;
  const total = quantity * price;
  document.getElementById("sellTotal").value = total.toFixed(2);
}

// Handle buy stock
async function handleBuyStock(e) {
  e.preventDefault();
  
  const symbol = document.getElementById("buySymbol").value.toUpperCase();
  const quantity = parseFloat(document.getElementById("buyQuantity").value);
  const price = parseFloat(document.getElementById("buyPrice").value);
  const total = quantity * price;
  
  if (total > portfolioData.balance) {
    showNotification("Insufficient balance", "error");
    return;
  }
  
  try {
    const orderData = {
      user: localStorage.getItem("userId"),
      symbol: symbol,
      name: `${symbol} Inc.`, // In real app, fetch from API
      price: price,
      count: quantity,
      totalPrice: total
    };
    
    const response = await fetch("/buyStock", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(orderData)
    });
    
    if (response.ok) {
      showNotification("Stock purchased successfully!", "success");
      closeModal("buyModal");
      loadPortfolioData(); // Reload data
    } else {
      showNotification("Failed to purchase stock", "error");
    }
  } catch (error) {
    console.error("Error buying stock:", error);
    showNotification("Error purchasing stock", "error");
  }
}

// Handle sell stock
async function handleSellStock(e) {
  e.preventDefault();
  
  const symbol = document.getElementById("sellSymbol").value;
  const quantity = parseFloat(document.getElementById("sellQuantity").value);
  const price = parseFloat(document.getElementById("sellPrice").value);
  const total = quantity * price;
  
  // Check if user has enough shares
  const stock = portfolioData.stocks.find(s => s.symbol === symbol);
  if (!stock || stock.count < quantity) {
    showNotification("Insufficient shares", "error");
    return;
  }
  
  try {
    const orderData = {
      user: localStorage.getItem("userId"),
      symbol: symbol,
      name: stock.name,
      price: price,
      count: quantity,
      totalPrice: total
    };
    
    const response = await fetch("/sellStock", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(orderData)
    });
    
    if (response.ok) {
      showNotification("Stock sold successfully!", "success");
      closeModal("sellModal");
      loadPortfolioData(); // Reload data
    } else {
      showNotification("Failed to sell stock", "error");
    }
  } catch (error) {
    console.error("Error selling stock:", error);
    showNotification("Error selling stock", "error");
  }
}

// Handle deposit
async function handleDeposit(e) {
  e.preventDefault();
  
  const amount = parseFloat(document.getElementById("depositAmount").value);
  const method = document.getElementById("depositMethod").value;
  
  try {
    const transactionData = {
      user: localStorage.getItem("userId"),
      amount: amount,
      paymentMode: method
    };
    
    const response = await fetch("/deposit", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(transactionData)
    });
    
    if (response.ok) {
      showNotification("Deposit successful!", "success");
      closeModal("depositModal");
      loadPortfolioData(); // Reload data
    } else {
      showNotification("Deposit failed", "error");
    }
  } catch (error) {
    console.error("Error depositing:", error);
    showNotification("Error processing deposit", "error");
  }
}

// Handle withdraw
async function handleWithdraw(e) {
  e.preventDefault();
  
  const amount = parseFloat(document.getElementById("withdrawAmount").value);
  const method = document.getElementById("withdrawMethod").value;
  
  if (amount > portfolioData.balance) {
    showNotification("Insufficient balance", "error");
    return;
  }
  
  try {
    const transactionData = {
      user: localStorage.getItem("userId"),
      amount: amount,
      paymentMode: method
    };
    
    const response = await fetch("/withdraw", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(transactionData)
    });
    
    if (response.ok) {
      showNotification("Withdrawal successful!", "success");
      closeModal("withdrawModal");
      loadPortfolioData(); // Reload data
    } else {
      showNotification("Withdrawal failed", "error");
    }
  } catch (error) {
    console.error("Error withdrawing:", error);
    showNotification("Error processing withdrawal", "error");
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

// Close modals when clicking outside
window.onclick = function(event) {
  const modals = document.querySelectorAll('.modal');
  modals.forEach(modal => {
    if (event.target === modal) {
      modal.style.display = "none";
    }
  });
}
