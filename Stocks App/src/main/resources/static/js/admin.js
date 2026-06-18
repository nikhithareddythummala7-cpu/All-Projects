const trendingStocksContainer = document.getElementById("trending-stocks");
const allStocksContainer = document.getElementById("all-stocks");
const stockSearchInput = document.getElementById("stockSearch");

const RAPID_API_KEY = "947b801f92msh96b919932628932p1a1413jsncb9cc7188719";
let retryCount = 0;
const maxRetries = 3;

// Fallback trending stocks data
const fallbackTrendingStocks = [
  { symbol: "AAPL", shortName: "Apple Inc.", regularMarketOpen: 175.50, regularMarketChangePercent: 2.5 },
  { symbol: "GOOGL", shortName: "Alphabet Inc.", regularMarketOpen: 142.30, regularMarketChangePercent: -1.2 },
  { symbol: "MSFT", shortName: "Microsoft Corp.", regularMarketOpen: 378.85, regularMarketChangePercent: 3.1 },
  { symbol: "TSLA", shortName: "Tesla Inc.", regularMarketOpen: 248.42, regularMarketChangePercent: -0.8 },
  { symbol: "AMZN", shortName: "Amazon.com Inc.", regularMarketOpen: 155.18, regularMarketChangePercent: 1.5 }
];

// Fetch trending (top 25) stocks with error handling
async function fetchTrending() {
  try {
    const res = await axios.get(
      "https://mboum-finance.p.rapidapi.com/co/collections/most_actives",
      {
        params: { start: "0" },
        headers: {
          "X-RapidAPI-Key": RAPID_API_KEY,
          "X-RapidAPI-Host": "mboum-finance.p.rapidapi.com"
        }
      }
    );

    trendingStocksContainer.innerHTML = "";
    const stocks = res.data.body || fallbackTrendingStocks;
    stocks.forEach(stock => {
      const div = document.createElement("div");
      div.className = "trending-stock";
      div.innerHTML = `
        <span>
          <h5>Stock name</h5>
          <p>${stock.shortName}</p>
        </span>
        <span>
          <h5>Symbol</h5>
          <p>${stock.symbol}</p>
        </span>
        <span>
          <h5>Price</h5>
          <p style="color:${stock.regularMarketChangePercent > 0 ? "green" : "red"}">
            $ ${stock.regularMarketOpen} (${stock.regularMarketChangePercent.toFixed(2)}%)
          </p>
        </span>
      `;
      div.addEventListener("click", () => {
        window.location.href = `/admin-stock.html?symbol=${stock.symbol}`;
      });
      trendingStocksContainer.appendChild(div);
    });
  } catch (err) {
    console.error("Error fetching trending stocks:", err);
    
    if (err.response && err.response.status === 429) {
      console.warn("Rate limit exceeded for trending stocks API, using fallback data");
      renderFallbackTrending();
    } else if (retryCount < maxRetries) {
      retryCount++;
      console.log(`Retrying trending stocks fetch (${retryCount}/${maxRetries})...`);
      setTimeout(() => fetchTrending(), Math.pow(2, retryCount) * 1000);
    } else {
      console.log("Using fallback trending stocks data");
      renderFallbackTrending();
    }
  }
}

// Render fallback trending stocks
function renderFallbackTrending() {
  trendingStocksContainer.innerHTML = "";
  fallbackTrendingStocks.forEach(stock => {
    const div = document.createElement("div");
    div.className = "trending-stock";
    div.innerHTML = `
      <span>
        <h5>Stock name</h5>
        <p>${stock.shortName}</p>
      </span>
      <span>
        <h5>Symbol</h5>
        <p>${stock.symbol}</p>
      </span>
      <span>
        <h5>Price</h5>
        <p style="color:${stock.regularMarketChangePercent > 0 ? "green" : "red"}">
          $ ${stock.regularMarketOpen} (${stock.regularMarketChangePercent.toFixed(2)}%)
        </p>
      </span>
    `;
    div.addEventListener("click", () => {
      window.location.href = `/admin-stock.html?symbol=${stock.symbol}`;
    });
    trendingStocksContainer.appendChild(div);
  });
}

// Fetch all available stocks with error handling
async function fetchAllStocks() {
  try {
    const res = await axios.get("https://twelve-data1.p.rapidapi.com/stocks", {
      params: { exchange: "NASDAQ", format: "json" },
      headers: {
        "X-RapidAPI-Key": RAPID_API_KEY,
        "X-RapidAPI-Host": "twelve-data1.p.rapidapi.com"
      }
    });

    let stocks = res.data.data || [];
    renderAllStocks(stocks);

    stockSearchInput.addEventListener("input", e => {
      const search = e.target.value.toUpperCase();
      const filtered = stocks.filter(
        s => s.symbol.includes(search) || s.name.toUpperCase().includes(search)
      );
      renderAllStocks(filtered);
    });
  } catch (err) {
    console.error("Error fetching all stocks:", err);
    
    if (err.response && err.response.status === 429) {
      console.warn("Rate limit exceeded for all stocks API, using server data");
      await fetchStocksFromServer();
    } else if (retryCount < maxRetries) {
      retryCount++;
      console.log(`Retrying all stocks fetch (${retryCount}/${maxRetries})...`);
      setTimeout(() => fetchAllStocks(), Math.pow(2, retryCount) * 1000);
    } else {
      console.log("Using server-side stocks data");
      await fetchStocksFromServer();
    }
  }
}

// Fetch stocks from our server (fallback)
async function fetchStocksFromServer() {
  try {
    const res = await axios.get('/fetch-stocks');
    const serverStocks = res.data || [];
    
    // Transform server data to match expected format
    const stocks = serverStocks.map(stock => ({
      symbol: stock.symbol,
      name: stock.name,
      exchange: stock.stockExchange || 'NASDAQ',
      type: 'Common Stock'
    }));
    
    renderAllStocks(stocks);

    stockSearchInput.addEventListener("input", e => {
      const search = e.target.value.toUpperCase();
      const filtered = stocks.filter(
        s => s.symbol.includes(search) || s.name.toUpperCase().includes(search)
      );
      renderAllStocks(filtered);
    });
  } catch (err) {
    console.error("Error fetching stocks from server:", err);
    allStocksContainer.innerHTML = "<p>Unable to load stocks at this time. Please try again later.</p>";
  }
}

// Render stocks list
function renderAllStocks(stocks) {
  allStocksContainer.innerHTML = "";
  if (stocks.length === 0) {
    allStocksContainer.innerHTML = "<p>No stocks available</p>";
    return;
  }
  
  stocks.forEach(stock => {
    const div = document.createElement("div");
    div.className = "all-stocks-stock";
    div.innerHTML = `
      <h6>${stock.exchange || 'NASDAQ'}</h6>
      <span><h5>Stock name</h5><p>${stock.name}</p></span>
      <span><h5>Symbol</h5><p>${stock.symbol}</p></span>
      <span><h5>Stock Type</h5><p>${stock.type || 'Common Stock'}</p></span>
      <button class="btn btn-primary">View Chart</button>
    `;
    div.querySelector("button").addEventListener("click", () => {
      window.location.href = `/admin-stock.html?symbol=${stock.symbol}`;
    });
    allStocksContainer.appendChild(div);
  });
}

// Show loading state
function showLoading() {
  trendingStocksContainer.innerHTML = '<div class="loading-spinner"></div>';
  allStocksContainer.innerHTML = '<div class="loading-spinner"></div>';
}

// Load data when page is ready
showLoading();
fetchTrending();
fetchAllStocks();