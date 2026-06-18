document.addEventListener("DOMContentLoaded", () => {
  const navbar = document.getElementById("navbar");
  const userType = localStorage.getItem("userType");
  const userName = localStorage.getItem("userName");
  const userEmail = localStorage.getItem("userEmail");

  if (!userType) {
    navbar.innerHTML = `
      <div class="navbar-brand">
        <h3 onclick="goTo('/')">SB Stocks</h3>
      </div>
      
      <div class="navbar-search">
        <div class="search-container">
          <input type="text" id="navbarSearch" placeholder="Search stocks..." />
          <button id="searchBtn" onclick="performSearch()">🔍</button>
        </div>
      </div>
      
      <div class="navbar-actions">
        <button class="btn-login" onclick="goTo('/login')">Login</button>
        <button class="btn-register" onclick="goTo('/register')">Register</button>
      </div>
    `;
  } else if (userType === "customer") {
    navbar.innerHTML = `
      <div class="navbar-brand">
        <h3 onclick="goTo('/')">SB Stocks</h3>
      </div>
      
      <div class="navbar-search">
        <div class="search-container">
          <input type="text" id="navbarSearch" placeholder="Search stocks..." />
          <button id="searchBtn" onclick="performSearch()">🔍</button>
        </div>
      </div>
      
      <div class="navbar-actions">
        <div class="user-menu">
          <span class="user-name">${userName || 'User'}</span>
          <div class="dropdown">
            <button class="dropdown-btn">▼</button>
            <div class="dropdown-content">
              <a href="/">Home</a>
              <a href="/dashboard">Dashboard</a>
              <a href="/portfolio">Portfolio</a>
              <a href="/history">History</a>
              <a href="/profile">Profile</a>
              <a href="#" onclick="logout()">Logout</a>
            </div>
          </div>
        </div>
      </div>
    `;
  } else if (userType === "admin") {
    navbar.innerHTML = `
      <div class="navbar-brand">
        <h3 onclick="goTo('/admin')">SB Stocks (Admin)</h3>
      </div>
      
      <div class="navbar-search">
        <div class="search-container">
          <input type="text" id="navbarSearch" placeholder="Search stocks..." />
          <button id="searchBtn" onclick="performSearch()">🔍</button>
        </div>
      </div>
      
      <div class="navbar-actions">
        <div class="user-menu">
          <span class="user-name">${userName || 'Admin'}</span>
          <div class="dropdown">
            <button class="dropdown-btn">▼</button>
            <div class="dropdown-content">
              <a href="/admin">Dashboard</a>
              <a href="/all-orders">Orders</a>
              <a href="/all-transactions">Transactions</a>
              <a href="/admin-stock">Stock Charts</a>
              <a href="#" onclick="logout()">Logout</a>
            </div>
          </div>
        </div>
      </div>
    `;
  }

  // Add search functionality
  const searchInput = document.getElementById("navbarSearch");
  if (searchInput) {
    searchInput.addEventListener("keypress", function(e) {
      if (e.key === "Enter") {
        performSearch();
      }
    });

    searchInput.addEventListener("input", function(e) {
      if (e.target.value.length > 2) {
        showSearchSuggestions(e.target.value);
      } else {
        hideSearchSuggestions();
      }
    });
  }

  // Add dropdown functionality
  const dropdowns = document.querySelectorAll('.dropdown');
  dropdowns.forEach(dropdown => {
    const btn = dropdown.querySelector('.dropdown-btn');
    const content = dropdown.querySelector('.dropdown-content');
    
    btn.addEventListener('click', (e) => {
      e.stopPropagation();
      content.classList.toggle('show');
    });
  });

  // Close dropdowns when clicking outside
  document.addEventListener('click', () => {
    dropdowns.forEach(dropdown => {
      dropdown.querySelector('.dropdown-content').classList.remove('show');
    });
  });
});

// Search functionality
let searchResults = [];
let allStocksData = [];

// Load stocks data for search
async function loadStocksForSearch() {
  try {
    const response = await fetch('/fetch-stocks');
    if (response.ok) {
      const stocks = await response.json();
      allStocksData = stocks.map(stock => ({
        symbol: stock.symbol,
        name: stock.name,
        price: stock.price,
        exchange: stock.stockExchange || 'NASDAQ'
      }));
    }
  } catch (error) {
    console.error('Error loading stocks for search:', error);
  }
}

// Perform search
function performSearch() {
  const searchTerm = document.getElementById("navbarSearch").value.trim();
  if (searchTerm.length === 0) {
    showNotification("Please enter a search term", "error");
    return;
  }

  // Search in current page stocks first
  const currentPageStocks = document.querySelectorAll('.all-stocks-stock, .trending-stock');
  let found = false;

  currentPageStocks.forEach(stockElement => {
    const stockText = stockElement.textContent.toLowerCase();
    if (stockText.includes(searchTerm.toLowerCase())) {
      stockElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
      stockElement.style.animation = 'highlight 2s ease-in-out';
      found = true;
    }
  });

  if (found) {
    showNotification(`Found stocks matching "${searchTerm}"`, "success");
  } else {
    // Search in all stocks data
    const matchingStocks = allStocksData.filter(stock => 
      stock.symbol.toLowerCase().includes(searchTerm.toLowerCase()) ||
      stock.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    if (matchingStocks.length > 0) {
      showSearchResults(matchingStocks, searchTerm);
    } else {
      showNotification(`No stocks found matching "${searchTerm}"`, "error");
    }
  }
}

// Show search suggestions
function showSearchSuggestions(term) {
  const suggestions = allStocksData.filter(stock => 
    stock.symbol.toLowerCase().includes(term.toLowerCase()) ||
    stock.name.toLowerCase().includes(term.toLowerCase())
  ).slice(0, 5);

  if (suggestions.length > 0) {
    const searchContainer = document.querySelector('.search-container');
    let suggestionsDiv = document.getElementById('searchSuggestions');
    
    if (!suggestionsDiv) {
      suggestionsDiv = document.createElement('div');
      suggestionsDiv.id = 'searchSuggestions';
      suggestionsDiv.className = 'search-suggestions';
      searchContainer.appendChild(suggestionsDiv);
    }

    suggestionsDiv.innerHTML = suggestions.map(stock => `
      <div class="suggestion-item" onclick="selectSuggestion('${stock.symbol}')">
        <span class="suggestion-symbol">${stock.symbol}</span>
        <span class="suggestion-name">${stock.name}</span>
      </div>
    `).join('');
  }
}

// Hide search suggestions
function hideSearchSuggestions() {
  const suggestionsDiv = document.getElementById('searchSuggestions');
  if (suggestionsDiv) {
    suggestionsDiv.remove();
  }
}

// Select suggestion
function selectSuggestion(symbol) {
  document.getElementById("navbarSearch").value = symbol;
  hideSearchSuggestions();
  performSearch();
}

// Show search results modal
function showSearchResults(stocks, searchTerm) {
  const modal = document.createElement('div');
  modal.className = 'search-results-modal';
  modal.innerHTML = `
    <div class="modal-content">
      <div class="modal-header">
        <h3>Search Results for "${searchTerm}"</h3>
        <button class="close-btn" onclick="closeSearchResults()">&times;</button>
      </div>
      <div class="modal-body">
        ${stocks.map(stock => `
          <div class="search-result-item" onclick="viewStock('${stock.symbol}')">
            <div class="result-symbol">${stock.symbol}</div>
            <div class="result-name">${stock.name}</div>
            <div class="result-price">$${stock.price}</div>
            <div class="result-exchange">${stock.exchange}</div>
          </div>
        `).join('')}
      </div>
    </div>
  `;
  
  document.body.appendChild(modal);
  showNotification(`Found ${stocks.length} stocks matching "${searchTerm}"`, "success");
}

// Close search results
function closeSearchResults() {
  const modal = document.querySelector('.search-results-modal');
  if (modal) {
    modal.remove();
  }
}

// View stock details
function viewStock(symbol) {
  closeSearchResults();
  // Navigate to stock chart or details page
  window.location.href = `/admin-stock.html?symbol=${symbol}`;
}

// Navigation helper
function goTo(path) {
  window.location.href = path;
}

// Logout helper
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
  `;
  
  notification.textContent = message;
  document.body.appendChild(notification);

  // Auto remove after 3 seconds
  setTimeout(() => {
    notification.style.animation = 'slideOutRight 0.3s ease-in';
    setTimeout(() => notification.remove(), 300);
  }, 3000);
}

// Load stocks data when page loads
loadStocksForSearch();

// Add CSS for enhanced navbar
const style = document.createElement('style');
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

  @keyframes highlight {
    0% { background-color: rgba(255, 255, 0, 0.3); }
    50% { background-color: rgba(255, 255, 0, 0.6); }
    100% { background-color: transparent; }
  }

  .search-suggestions {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    background: white;
    border: 1px solid #ddd;
    border-radius: 8px;
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    z-index: 1000;
    max-height: 200px;
    overflow-y: auto;
  }

  .suggestion-item {
    padding: 0.5rem 1rem;
    cursor: pointer;
    border-bottom: 1px solid #eee;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .suggestion-item:hover {
    background: #f5f5f5;
  }

  .suggestion-symbol {
    font-weight: bold;
    color: #667eea;
  }

  .suggestion-name {
    color: #666;
    font-size: 0.9rem;
  }

  .search-results-modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 10000;
  }

  .modal-content {
    background: white;
    border-radius: 12px;
    max-width: 600px;
    width: 90%;
    max-height: 80vh;
    overflow: hidden;
  }

  .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    border-bottom: 1px solid #eee;
  }

  .close-btn {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: #666;
  }

  .modal-body {
    max-height: 60vh;
    overflow-y: auto;
    padding: 1rem;
  }

  .search-result-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.75rem;
    border: 1px solid #eee;
    border-radius: 8px;
    margin-bottom: 0.5rem;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .search-result-item:hover {
    background: #f5f5f5;
    transform: translateY(-2px);
    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
  }

  .result-symbol {
    font-weight: bold;
    color: #667eea;
    font-size: 1.1rem;
  }

  .result-name {
    color: #333;
    flex: 1;
    margin: 0 1rem;
  }

  .result-price {
    color: #27ae60;
    font-weight: bold;
  }

  .result-exchange {
    color: #666;
    font-size: 0.9rem;
    margin-left: 1rem;
  }
`;
document.head.appendChild(style);