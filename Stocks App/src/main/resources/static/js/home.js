document.addEventListener("DOMContentLoaded", () => {
  const trendingContainer = document.getElementById("trendingStocks");
  const allStocksContainer = document.getElementById("allStocks");
  const searchInput = document.getElementById("stockSearch");

  let allStocks = [];
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

  // Fetch Trending Stocks with retry logic
  async function fetchTrending() {
    const options = {
      method: "GET",
      headers: {
        "X-RapidAPI-Key": "YOUR_KEY_HERE",
        "X-RapidAPI-Host": "mboum-finance.p.rapidapi.com"
      }
    };

    try {
      const res = await fetch("https://mboum-finance.p.rapidapi.com/co/collections/most_actives?start=0", options);
      
      if (res.status === 429) {
        console.warn("Rate limit exceeded for trending stocks API, using fallback data");
        renderTrending(fallbackTrendingStocks);
        return;
      }

      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
      }

      const data = await res.json();
      renderTrending(data.body || fallbackTrendingStocks);
    } catch (err) {
      console.error("Error fetching trending stocks:", err);
      if (retryCount < maxRetries) {
        retryCount++;
        console.log(`Retrying trending stocks fetch (${retryCount}/${maxRetries})...`);
        setTimeout(() => fetchTrending(), Math.pow(2, retryCount) * 1000); // Exponential backoff
      } else {
        console.log("Using fallback trending stocks data");
        renderTrending(fallbackTrendingStocks);
      }
    }
  }

  // Render Trending Stocks
  function renderTrending(stocks) {
    trendingContainer.innerHTML = "";
    if (stocks.length === 0) {
      trendingContainer.innerHTML = "<p>No trending stocks available</p>";
      return;
    }
    stocks.forEach(stock => {
      const div = document.createElement("div");
      div.className = "trending-stock";
      div.innerHTML = `
        <span>
          <h5>Stock name</h5>
          <p>${stock.shortName || "N/A"}</p>
        </span>
        <span>
          <h5>Symbol</h5>
          <p>${stock.symbol}</p>
        </span>
        <span>
          <h5>Price</h5>
          <p style="color:${stock.regularMarketChangePercent > 0 ? "green" : "red"}">
            $${stock.regularMarketOpen} (${stock.regularMarketChangePercent.toFixed(2)}%)
          </p>
        </span>
      `;
      div.onclick = () => goTo(`/stock/${stock.symbol}`);
      trendingContainer.appendChild(div);
    });
  }

  // Fetch All Stocks with retry logic
  async function fetchAllStocks() {
    const options = {
      method: "GET",
      headers: {
        "X-RapidAPI-Key": "YOUR_KEY_HERE",
        "X-RapidAPI-Host": "twelve-data1.p.rapidapi.com"
      }
    };

    try {
      const res = await fetch("https://twelve-data1.p.rapidapi.com/stocks?exchange=NASDAQ&format=json", options);
      
      if (res.status === 429) {
        console.warn("Rate limit exceeded for all stocks API, using server data");
        await fetchStocksFromServer();
        return;
      }

      if (!res.ok) {
        throw new Error(`HTTP error! status: ${res.status}`);
      }

      const data = await res.json();
      allStocks = data.data || [];
      renderAllStocks(allStocks);
    } catch (err) {
      console.error("Error fetching all stocks:", err);
      if (retryCount < maxRetries) {
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
      const res = await fetch('/fetch-stocks');
      if (res.ok) {
        const serverStocks = await res.json();
        // Transform server data to match expected format
        allStocks = serverStocks.map(stock => ({
          symbol: stock.symbol,
          name: stock.name,
          exchange: stock.stockExchange || 'NASDAQ',
          type: 'Common Stock'
        }));
        renderAllStocks(allStocks);
      } else {
        throw new Error('Failed to fetch from server');
      }
    } catch (err) {
      console.error("Error fetching stocks from server:", err);
      allStocksContainer.innerHTML = "<p>Unable to load stocks at this time. Please try again later.</p>";
    }
  }

  // Render All Stocks
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
        <span>
          <h5>Stock name</h5>
          <p>${stock.name}</p>
        </span>
        <span>
          <h5>Symbol</h5>
          <p>${stock.symbol}</p>
        </span>
        <span>
          <h5>Stock Type</h5>
          <p>${stock.type || 'Common Stock'}</p>
        </span>
        <button class="btn">View Chart</button>
      `;
      div.querySelector("button").onclick = () => goTo(`/stock/${stock.symbol}`);
      allStocksContainer.appendChild(div);
    });
  }

  // Search Functionality
  searchInput.addEventListener("input", e => {
    const search = e.target.value.toUpperCase();
    const filtered = allStocks.filter(stock =>
      stock.symbol.includes(search) || stock.name.toUpperCase().includes(search)
    );
    renderAllStocks(filtered);
    
    // Update navbar search if it exists
    const navbarSearch = document.getElementById("navbarSearch");
    if (navbarSearch && navbarSearch.value !== e.target.value) {
      navbarSearch.value = e.target.value;
    }
  });

  // Sync with navbar search
  const navbarSearch = document.getElementById("navbarSearch");
  if (navbarSearch) {
    navbarSearch.addEventListener("input", e => {
      if (searchInput.value !== e.target.value) {
        searchInput.value = e.target.value;
        const search = e.target.value.toUpperCase();
        const filtered = allStocks.filter(stock =>
          stock.symbol.includes(search) || stock.name.toUpperCase().includes(search)
        );
        renderAllStocks(filtered);
      }
    });
  }

  // Navigation
  function goTo(path) {
    window.location.href = path;
  }

  // Show loading state
  function showLoading() {
    trendingContainer.innerHTML = '<div class="loading-spinner"></div>';
    allStocksContainer.innerHTML = '<div class="loading-spinner"></div>';
  }

  // Init
  showLoading();
  fetchTrending();
  fetchAllStocks();
});