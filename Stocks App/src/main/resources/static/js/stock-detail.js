// Stock Detail JavaScript

document.addEventListener("DOMContentLoaded", () => {
  loadStockData();
  setupEventListeners();
  initializeChart();
});

let priceChart;
let currentChartPeriod = '1D';
let currentStock = {};

// Load stock data from URL parameters
function loadStockData() {
  const urlParams = new URLSearchParams(window.location.search);
  const symbol = urlParams.get('symbol') || 'AAPL';
  
  // Update page title and header
  document.title = `${symbol} - Stock Details | SB Stocks`;
  document.getElementById('stockSymbol').textContent = symbol;
  document.getElementById('buyStockSymbol').textContent = symbol;
  document.getElementById('sellStockSymbol').textContent = symbol;
  
  // Load stock information (mock data for now)
  loadStockInfo(symbol);
}

// Load stock information
async function loadStockInfo(symbol) {
  try {
    // Mock stock data - in a real app, this would come from an API
    const mockStockData = {
      'AAPL': {
        name: 'Apple Inc.',
        price: 175.50,
        change: 2.45,
        changePercent: 1.42,
        marketCap: '2.8T',
        volume: '45.2M',
        high52W: 198.23,
        low52W: 124.17,
        peRatio: 28.5,
        dividendYield: 0.44,
        sector: 'Technology',
        industry: 'Consumer Electronics',
        exchange: 'NASDAQ',
        founded: '1976',
        ceo: 'Tim Cook',
        employees: '164,000',
        description: 'Apple Inc. designs, manufactures, and markets smartphones, personal computers, tablets, wearables, and accessories worldwide. The company\'s products include iPhone, Mac, iPad, Apple Watch, AirPods, Apple TV, and various accessories.',
        revenue: '394.3B',
        netIncome: '99.8B',
        eps: '6.13',
        roe: '147.3%',
        debtToEquity: '1.73',
        currentRatio: '1.04'
      },
      'GOOGL': {
        name: 'Alphabet Inc.',
        price: 142.30,
        change: -1.20,
        changePercent: -0.84,
        marketCap: '1.8T',
        volume: '28.5M',
        high52W: 155.20,
        low52W: 102.15,
        peRatio: 25.2,
        dividendYield: 0.00,
        sector: 'Technology',
        industry: 'Internet Services',
        exchange: 'NASDAQ',
        founded: '1998',
        ceo: 'Sundar Pichai',
        employees: '190,000',
        description: 'Alphabet Inc. provides online advertising services in the United States, Europe, the Middle East, Africa, the Asia-Pacific, Canada, and Latin America. The company offers performance and brand advertising services.',
        revenue: '282.8B',
        netIncome: '59.9B',
        eps: '4.56',
        roe: '18.2%',
        debtToEquity: '0.12',
        currentRatio: '2.85'
      },
      'MSFT': {
        name: 'Microsoft Corporation',
        price: 378.85,
        change: 3.25,
        changePercent: 0.87,
        marketCap: '2.8T',
        volume: '22.1M',
        high52W: '420.82',
        low52W: '309.45',
        peRatio: 32.1,
        dividendYield: 0.71,
        sector: 'Technology',
        industry: 'Software',
        exchange: 'NASDAQ',
        founded: '1975',
        ceo: 'Satya Nadella',
        employees: '221,000',
        description: 'Microsoft Corporation develops, licenses, and supports software, services, devices, and solutions worldwide. The company operates in Productivity and Business Processes, Intelligent Cloud, and More Personal Computing segments.',
        revenue: '211.9B',
        netIncome: '83.4B',
        eps: '11.06',
        roe: '39.4%',
        debtToEquity: '0.31',
        currentRatio: '2.52'
      }
    };
    
    currentStock = mockStockData[symbol] || mockStockData['AAPL'];
    
    // Update stock information
    document.getElementById('stockName').textContent = currentStock.name;
    document.getElementById('currentPrice').textContent = `$${currentStock.price}`;
    
    const changeElement = document.getElementById('priceChange');
    const isPositive = currentStock.change >= 0;
    changeElement.textContent = `${isPositive ? '+' : ''}${currentStock.change} (${isPositive ? '+' : ''}${currentStock.changePercent}%)`;
    changeElement.className = `price-change ${isPositive ? 'positive' : 'negative'}`;
    
    // Update stats
    document.getElementById('marketCap').textContent = currentStock.marketCap;
    document.getElementById('volume').textContent = currentStock.volume;
    document.getElementById('high52W').textContent = `$${currentStock.high52W}`;
    document.getElementById('low52W').textContent = `$${currentStock.low52W}`;
    document.getElementById('peRatio').textContent = currentStock.peRatio;
    document.getElementById('dividendYield').textContent = `${currentStock.dividendYield}%`;
    
    // Update company info
    document.getElementById('companyDescription').textContent = currentStock.description;
    
    // Update financial data
    const financialData = document.querySelectorAll('.financial-row .value');
    financialData[0].textContent = `$${currentStock.revenue}`;
    financialData[1].textContent = `$${currentStock.netIncome}`;
    financialData[2].textContent = `$${currentStock.eps}`;
    financialData[3].textContent = currentStock.roe;
    financialData[4].textContent = currentStock.debtToEquity;
    financialData[5].textContent = currentStock.currentRatio;
    
    // Update company info items
    const infoItems = document.querySelectorAll('.info-item .value');
    infoItems[0].textContent = currentStock.sector;
    infoItems[1].textContent = currentStock.industry;
    infoItems[2].textContent = currentStock.exchange;
    infoItems[3].textContent = currentStock.founded;
    infoItems[4].textContent = currentStock.ceo;
    infoItems[5].textContent = currentStock.employees;
    
  } catch (error) {
    console.error('Error loading stock data:', error);
    showNotification('Error loading stock data', 'error');
  }
}

// Setup event listeners
function setupEventListeners() {
  // Buy form
  document.getElementById('buyForm').addEventListener('submit', handleBuyStock);
  
  // Sell form
  document.getElementById('sellForm').addEventListener('submit', handleSellStock);
  
  // Calculate totals on input change
  document.getElementById('buyQuantity').addEventListener('input', calculateBuyTotal);
  document.getElementById('buyPrice').addEventListener('input', calculateBuyTotal);
  document.getElementById('sellQuantity').addEventListener('input', calculateSellTotal);
  document.getElementById('sellPrice').addEventListener('input', calculateSellTotal);
  
  // Set current price as default
  document.getElementById('buyPrice').value = currentStock.price;
  document.getElementById('sellPrice').value = currentStock.price;
}

// Initialize price chart
function initializeChart() {
  const ctx = document.getElementById('priceChart').getContext('2d');
  
  // Generate mock data based on current period
  const data = generateMockChartData(currentChartPeriod);
  
  priceChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.labels,
      datasets: [{
        label: 'Price',
        data: data.values,
        borderColor: '#667eea',
        backgroundColor: 'rgba(102, 126, 234, 0.1)',
        borderWidth: 3,
        fill: true,
        tension: 0.4,
        pointBackgroundColor: '#667eea',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: 4,
        pointHoverRadius: 6
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        x: {
          grid: {
            display: false
          },
          ticks: {
            color: '#666'
          }
        },
        y: {
          grid: {
            color: 'rgba(0,0,0,0.1)'
          },
          ticks: {
            color: '#666',
            callback: function(value) {
              return '$' + value.toFixed(2);
            }
          }
        }
      },
      interaction: {
        intersect: false,
        mode: 'index'
      }
    }
  });
}

// Generate mock chart data
function generateMockChartData(period) {
  const now = new Date();
  let labels = [];
  let values = [];
  let basePrice = currentStock.price;
  
  switch (period) {
    case '1D':
      // 24 hours, hourly data
      for (let i = 23; i >= 0; i--) {
        const time = new Date(now.getTime() - i * 60 * 60 * 1000);
        labels.push(time.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' }));
        basePrice += (Math.random() - 0.5) * 2;
        values.push(Math.max(0, basePrice));
      }
      break;
    case '5D':
      // 5 days, daily data
      for (let i = 4; i >= 0; i--) {
        const time = new Date(now.getTime() - i * 24 * 60 * 60 * 1000);
        labels.push(time.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));
        basePrice += (Math.random() - 0.5) * 5;
        values.push(Math.max(0, basePrice));
      }
      break;
    case '1M':
      // 30 days, daily data
      for (let i = 29; i >= 0; i--) {
        const time = new Date(now.getTime() - i * 24 * 60 * 60 * 1000);
        labels.push(time.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));
        basePrice += (Math.random() - 0.5) * 3;
        values.push(Math.max(0, basePrice));
      }
      break;
    case '3M':
      // 3 months, weekly data
      for (let i = 12; i >= 0; i--) {
        const time = new Date(now.getTime() - i * 7 * 24 * 60 * 60 * 1000);
        labels.push(time.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));
        basePrice += (Math.random() - 0.5) * 10;
        values.push(Math.max(0, basePrice));
      }
      break;
    case '1Y':
      // 12 months, monthly data
      for (let i = 11; i >= 0; i--) {
        const time = new Date(now.getFullYear(), now.getMonth() - i, 1);
        labels.push(time.toLocaleDateString('en-US', { month: 'short', year: '2-digit' }));
        basePrice += (Math.random() - 0.5) * 20;
        values.push(Math.max(0, basePrice));
      }
      break;
    case '5Y':
      // 5 years, monthly data
      for (let i = 59; i >= 0; i--) {
        const time = new Date(now.getFullYear(), now.getMonth() - i, 1);
        labels.push(time.toLocaleDateString('en-US', { month: 'short', year: '2-digit' }));
        basePrice += (Math.random() - 0.5) * 15;
        values.push(Math.max(0, basePrice));
      }
      break;
  }
  
  return { labels, values };
}

// Change chart period
function changeChartPeriod(period) {
  currentChartPeriod = period;
  
  // Update button states
  document.querySelectorAll('.chart-btn').forEach(btn => {
    btn.classList.remove('active');
  });
  event.target.classList.add('active');
  
  // Update chart data
  const data = generateMockChartData(period);
  priceChart.data.labels = data.labels;
  priceChart.data.datasets[0].data = data.values;
  priceChart.update();
}

// Modal functions
function showBuyModal() {
  document.getElementById('buyModal').style.display = 'block';
  document.getElementById('buyQuantity').focus();
}

function showSellModal() {
  document.getElementById('sellModal').style.display = 'block';
  document.getElementById('sellQuantity').focus();
}

function closeModal(modalId) {
  document.getElementById(modalId).style.display = 'none';
  document.getElementById(modalId.replace('Modal', 'Form')).reset();
  // Reset to current price
  document.getElementById('buyPrice').value = currentStock.price;
  document.getElementById('sellPrice').value = currentStock.price;
}

// Calculate totals
function calculateBuyTotal() {
  const quantity = parseFloat(document.getElementById('buyQuantity').value) || 0;
  const price = parseFloat(document.getElementById('buyPrice').value) || 0;
  const total = quantity * price;
  document.getElementById('buyTotal').value = total.toFixed(2);
}

function calculateSellTotal() {
  const quantity = parseFloat(document.getElementById('sellQuantity').value) || 0;
  const price = parseFloat(document.getElementById('sellPrice').value) || 0;
  const total = quantity * price;
  document.getElementById('sellTotal').value = total.toFixed(2);
}

// Handle buy stock
async function handleBuyStock(e) {
  e.preventDefault();
  
  const symbol = document.getElementById('stockSymbol').textContent;
  const quantity = parseFloat(document.getElementById('buyQuantity').value);
  const price = parseFloat(document.getElementById('buyPrice').value);
  const total = quantity * price;
  
  try {
    const orderData = {
      user: localStorage.getItem('userId'),
      symbol: symbol,
      name: currentStock.name,
      price: price,
      count: quantity,
      totalPrice: total
    };
    
    const response = await fetch('/buyStock', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(orderData)
    });
    
    if (response.ok) {
      showNotification('Stock purchased successfully!', 'success');
      closeModal('buyModal');
    } else {
      showNotification('Failed to purchase stock', 'error');
    }
  } catch (error) {
    console.error('Error buying stock:', error);
    showNotification('Error purchasing stock', 'error');
  }
}

// Handle sell stock
async function handleSellStock(e) {
  e.preventDefault();
  
  const symbol = document.getElementById('stockSymbol').textContent;
  const quantity = parseFloat(document.getElementById('sellQuantity').value);
  const price = parseFloat(document.getElementById('sellPrice').value);
  const total = quantity * price;
  
  try {
    const orderData = {
      user: localStorage.getItem('userId'),
      symbol: symbol,
      name: currentStock.name,
      price: price,
      count: quantity,
      totalPrice: total
    };
    
    const response = await fetch('/sellStock', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(orderData)
    });
    
    if (response.ok) {
      showNotification('Stock sold successfully!', 'success');
      closeModal('sellModal');
    } else {
      showNotification('Failed to sell stock', 'error');
    }
  } catch (error) {
    console.error('Error selling stock:', error);
    showNotification('Error selling stock', 'error');
  }
}

// Toggle watchlist
function toggleWatchlist() {
  const button = document.querySelector('.btn-watchlist');
  const isInWatchlist = button.textContent === 'Remove from Watchlist';
  
  if (isInWatchlist) {
    button.textContent = 'Add to Watchlist';
    button.style.background = 'linear-gradient(135deg, #17a2b8 0%, #138496 100%)';
    showNotification('Removed from watchlist', 'info');
  } else {
    button.textContent = 'Remove from Watchlist';
    button.style.background = 'linear-gradient(135deg, #6c757d 0%, #495057 100%)';
    showNotification('Added to watchlist', 'success');
  }
}

// Notification helper
function showNotification(message, type = 'info') {
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
      modal.style.display = 'none';
    }
  });
}
