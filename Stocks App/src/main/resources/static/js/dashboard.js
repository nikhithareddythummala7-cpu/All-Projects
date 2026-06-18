// Dashboard JavaScript

document.addEventListener("DOMContentLoaded", () => {
  loadDashboardData();
  setupEventListeners();
});

let portfolioChart;
let currentChartPeriod = '1D';

// Load dashboard data
async function loadDashboardData() {
  try {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      showNotification("Please login to view dashboard", "error");
      window.location.href = "/login";
      return;
    }

    await Promise.all([
      loadUserData(),
      loadPortfolioData(),
      loadTopHoldings(),
      loadRecentActivity(),
      loadMarketNews()
    ]);

    initializeChart();
  } catch (error) {
    console.error("Error loading dashboard data:", error);
    showNotification("Error loading dashboard data", "error");
  }
}

// Setup event listeners
function setupEventListeners() {
  // Add any additional event listeners here
}

// Load user data
async function loadUserData() {
  try {
    const userId = localStorage.getItem("userId");
    const response = await fetch(`/fetch-user/${userId}`);
    if (response.ok) {
      const userData = await response.json();
      document.getElementById("userName").textContent = userData.username || "User";
      document.getElementById("availableBalance").textContent = `$${(userData.balance || 0).toFixed(2)}`;
    }
  } catch (error) {
    console.error("Error loading user data:", error);
  }
}

// Load portfolio data
async function loadPortfolioData() {
  try {
    const response = await fetch("/fetch-stocks");
    if (response.ok) {
      const stocks = await response.json();
      const userId = localStorage.getItem("userId");
      const userStocks = stocks.filter(stock => stock.user === userId);
      
      let totalValue = 0;
      let totalGainLoss = 0;
      
      userStocks.forEach(stock => {
        const currentValue = (stock.totalPrice || 0) * 1.05; // Mock 5% gain
        totalValue += currentValue;
        totalGainLoss += currentValue - (stock.totalPrice || 0);
      });
      
      document.getElementById("portfolioValue").textContent = `$${totalValue.toFixed(2)}`;
      document.getElementById("totalHoldings").textContent = userStocks.length;
      
      const gainLossPercent = totalValue > 0 ? (totalGainLoss / (totalValue - totalGainLoss)) * 100 : 0;
      document.getElementById("portfolioChange").textContent = `+${gainLossPercent.toFixed(2)}%`;
      document.getElementById("portfolioChange").className = gainLossPercent >= 0 ? "stat-change positive" : "stat-change negative";
      
      // Mock today's gain/loss
      const todayGainLoss = totalGainLoss * 0.1; // 10% of total gain/loss
      document.getElementById("todayGainLoss").textContent = `$${todayGainLoss.toFixed(2)}`;
      document.getElementById("todayChange").textContent = `+${(gainLossPercent * 0.1).toFixed(2)}%`;
      document.getElementById("todayChange").className = todayGainLoss >= 0 ? "stat-change positive" : "stat-change negative";
    }
  } catch (error) {
    console.error("Error loading portfolio data:", error);
  }
}

// Load top holdings
async function loadTopHoldings() {
  try {
    const response = await fetch("/fetch-stocks");
    if (response.ok) {
      const stocks = await response.json();
      const userId = localStorage.getItem("userId");
      const userStocks = stocks.filter(stock => stock.user === userId);
      
      const topHoldings = userStocks
        .sort((a, b) => (b.totalPrice || 0) - (a.totalPrice || 0))
        .slice(0, 5);
      
      const holdingsContainer = document.getElementById("topHoldings");
      
      if (topHoldings.length === 0) {
        holdingsContainer.innerHTML = `
          <div class="no-data">
            <h3>No Holdings</h3>
            <p>You don't have any stocks yet.</p>
            <a href="/portfolio" class="btn-primary">Start Trading</a>
          </div>
        `;
        return;
      }
      
      holdingsContainer.innerHTML = topHoldings.map(stock => {
        const currentValue = (stock.totalPrice || 0) * 1.05;
        const gainLoss = currentValue - (stock.totalPrice || 0);
        const gainLossPercent = (gainLoss / (stock.totalPrice || 1)) * 100;
        
        return `
          <div class="holding-item">
            <div class="holding-info">
              <div class="holding-symbol">${stock.symbol}</div>
              <div class="holding-name">${stock.name}</div>
            </div>
            <div class="holding-value ${gainLoss >= 0 ? 'positive' : 'negative'}">
              $${currentValue.toFixed(2)}
              <div style="font-size: 0.8rem; font-weight: 500; color: #666;">
                ${gainLoss >= 0 ? '+' : ''}${gainLossPercent.toFixed(2)}%
              </div>
            </div>
          </div>
        `;
      }).join('');
    }
  } catch (error) {
    console.error("Error loading top holdings:", error);
  }
}

// Load recent activity
async function loadRecentActivity() {
  try {
    const response = await fetch("/transactions");
    if (response.ok) {
      const transactions = await response.json();
      const userId = localStorage.getItem("userId");
      const userTransactions = transactions.filter(transaction => transaction.user === userId);
      
      const recentTransactions = userTransactions
        .sort((a, b) => new Date(b.time || b.createdAt || 0) - new Date(a.time || a.createdAt || 0))
        .slice(0, 5);
      
      const activityContainer = document.getElementById("recentActivity");
      
      if (recentTransactions.length === 0) {
        activityContainer.innerHTML = `
          <div class="no-data">
            <h3>No Activity</h3>
            <p>No recent transactions found.</p>
          </div>
        `;
        return;
      }
      
      activityContainer.innerHTML = recentTransactions.map(transaction => {
        const type = transaction.type || transaction.orderType || "Unknown";
        const amount = transaction.amount || transaction.totalPrice || 0;
        const isPositive = type === "Deposit" || type === "sell";
        const time = formatTime(transaction.time || transaction.createdAt);
        
        return `
          <div class="activity-item">
            <div class="activity-info">
              <div class="activity-type">${type}</div>
              <div class="activity-details">${getActivityDescription(transaction)}</div>
            </div>
            <div class="activity-amount ${isPositive ? 'positive' : 'negative'}">
              ${isPositive ? '+' : '-'}$${amount.toFixed(2)}
              <div style="font-size: 0.8rem; font-weight: 500; color: #666;">
                ${time}
              </div>
            </div>
          </div>
        `;
      }).join('');
    }
  } catch (error) {
    console.error("Error loading recent activity:", error);
  }
}

// Load market news
async function loadMarketNews() {
  try {
    // Mock market news data
    const mockNews = [
      {
        title: "Tech Stocks Rally on Strong Earnings",
        summary: "Major technology companies report better-than-expected quarterly results",
        time: "2 hours ago"
      },
      {
        title: "Federal Reserve Maintains Interest Rates",
        summary: "Central bank keeps rates steady amid economic uncertainty",
        time: "4 hours ago"
      },
      {
        title: "Energy Sector Sees Mixed Performance",
        summary: "Oil prices fluctuate as supply concerns persist",
        time: "6 hours ago"
      },
      {
        title: "Cryptocurrency Market Shows Volatility",
        summary: "Bitcoin and other digital assets experience significant price swings",
        time: "8 hours ago"
      }
    ];
    
    const newsContainer = document.getElementById("marketNews");
    newsContainer.innerHTML = mockNews.map(news => `
      <div class="news-item">
        <div class="news-info">
          <div class="news-title">${news.title}</div>
          <div class="news-summary">${news.summary}</div>
        </div>
        <div class="news-time">${news.time}</div>
      </div>
    `).join('');
  } catch (error) {
    console.error("Error loading market news:", error);
  }
}

// Initialize portfolio chart
function initializeChart() {
  const ctx = document.getElementById('portfolioChart').getContext('2d');
  
  // Generate mock data based on current period
  const data = generateMockChartData(currentChartPeriod);
  
  portfolioChart = new Chart(ctx, {
    type: 'line',
    data: {
      labels: data.labels,
      datasets: [{
        label: 'Portfolio Value',
        data: data.values,
        borderColor: '#667eea',
        backgroundColor: 'rgba(102, 126, 234, 0.1)',
        borderWidth: 3,
        fill: true,
        tension: 0.4,
        pointBackgroundColor: '#667eea',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointRadius: 6,
        pointHoverRadius: 8
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
              return '$' + value.toLocaleString();
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
  let baseValue = 10000; // Starting portfolio value
  
  switch (period) {
    case '1D':
      // 24 hours, hourly data
      for (let i = 23; i >= 0; i--) {
        const time = new Date(now.getTime() - i * 60 * 60 * 1000);
        labels.push(time.toLocaleTimeString('en-US', { hour: '2-digit', minute: '2-digit' }));
        baseValue += (Math.random() - 0.5) * 200;
        values.push(Math.max(0, baseValue));
      }
      break;
    case '1W':
      // 7 days, daily data
      for (let i = 6; i >= 0; i--) {
        const time = new Date(now.getTime() - i * 24 * 60 * 60 * 1000);
        labels.push(time.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));
        baseValue += (Math.random() - 0.5) * 1000;
        values.push(Math.max(0, baseValue));
      }
      break;
    case '1M':
      // 30 days, daily data
      for (let i = 29; i >= 0; i--) {
        const time = new Date(now.getTime() - i * 24 * 60 * 60 * 1000);
        labels.push(time.toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));
        baseValue += (Math.random() - 0.5) * 500;
        values.push(Math.max(0, baseValue));
      }
      break;
    case '1Y':
      // 12 months, monthly data
      for (let i = 11; i >= 0; i--) {
        const time = new Date(now.getFullYear(), now.getMonth() - i, 1);
        labels.push(time.toLocaleDateString('en-US', { month: 'short', year: '2-digit' }));
        baseValue += (Math.random() - 0.5) * 2000;
        values.push(Math.max(0, baseValue));
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
  portfolioChart.data.labels = data.labels;
  portfolioChart.data.datasets[0].data = data.values;
  portfolioChart.update();
}

// Helper functions
function getActivityDescription(transaction) {
  if (transaction.type === "Deposit" || transaction.type === "Withdraw") {
    return `${transaction.type} via ${transaction.paymentMode || 'N/A'}`;
  } else if (transaction.orderType === "buy" || transaction.orderType === "sell") {
    return `${transaction.orderType.toUpperCase()} ${transaction.count || 0} shares of ${transaction.symbol || 'N/A'}`;
  }
  return "Transaction";
}

function formatTime(dateString) {
  if (!dateString) return "N/A";
  
  const date = new Date(dateString);
  const now = new Date();
  const diffMs = now - date;
  const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
  const diffDays = Math.floor(diffHours / 24);
  
  if (diffHours < 1) {
    return "Just now";
  } else if (diffHours < 24) {
    return `${diffHours}h ago`;
  } else if (diffDays < 7) {
    return `${diffDays}d ago`;
  } else {
    return date.toLocaleDateString('en-US', { month: 'short', day: 'numeric' });
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
