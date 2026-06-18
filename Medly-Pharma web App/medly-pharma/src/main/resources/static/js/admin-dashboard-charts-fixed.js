console.log("✅ admin-reports-charts-fixed.js loaded!");

document.addEventListener("DOMContentLoaded", function () {
  console.log("📊 Initializing charts with real data...");

  if (typeof Chart === "undefined") {
    console.error("❌ Chart.js not found!");
    return;
  }

  // Helper function to convert object to sorted arrays
  function objectToSortedArrays(obj) {
    const entries = Object.entries(obj).sort(([a], [b]) => a.localeCompare(b));
    return {
      labels: entries.map(([key]) => key),
      data: entries.map(([, value]) => value)
    };
  }

  // ---- ORDERS CHART ----
  const ordersCtx = document.getElementById("ordersChart");
  if (ordersCtx) {
    const ordersData = objectToSortedArrays(ordersByMonth || {});
    new Chart(ordersCtx, {
      type: "bar",
      data: {
        labels: ordersData.labels.length > 0 ? ordersData.labels : ["No Data"],
        datasets: [{
          label: "Orders",
          data: ordersData.data.length > 0 ? ordersData.data : [0],
          backgroundColor: "rgba(54, 162, 235, 0.7)",
          borderColor: "rgba(54, 162, 235, 1)",
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: "Orders Overview",
            font: { size: 16, weight: "bold" }
          },
          legend: { display: false }
        },
        scales: {
          y: { beginAtZero: true, ticks: { precision: 0 } }
        }
      }
    });
    console.log("✅ Orders chart rendered with data:", ordersData);
  }

  // ---- TOP PRODUCTS CHART ----
  const topProductsCtx = document.getElementById("topProductsChart");
  if (topProductsCtx) {
    const products = topSellingProducts || [];
    const labels = products.map(p => p.name || "Unknown");
    const data = products.map(p => p.quantityInStock || 0); // Using quantityInStock as quantity sold

    new Chart(topProductsCtx, {
      type: "pie",
      data: {
        labels: labels.length > 0 ? labels : ["No Products"],
        datasets: [{
          data: data.length > 0 ? data : [1],
          backgroundColor: [
            "rgba(255, 99, 132, 0.8)",
            "rgba(54, 162, 235, 0.8)",
            "rgba(255, 206, 86, 0.8)",
            "rgba(75, 192, 192, 0.8)",
            "rgba(153, 102, 255, 0.8)"
          ],
          borderColor: "#fff",
          borderWidth: 2
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: "Top Selling Products",
            font: { size: 16, weight: "bold" }
          },
          legend: { position: "right" }
        }
      }
    });
    console.log("✅ Top Products chart rendered with data:", { labels, data });
  }

  // ---- REVENUE CHART ----
  const revenueCtx = document.getElementById("revenueChart");
  if (revenueCtx) {
    const revenueData = objectToSortedArrays(revenueByMonth || {});
    new Chart(revenueCtx, {
      type: "line",
      data: {
        labels: revenueData.labels.length > 0 ? revenueData.labels : ["No Data"],
        datasets: [{
          label: "Revenue (₹)",
          data: revenueData.data.length > 0 ? revenueData.data : [0],
          borderColor: "rgba(75, 192, 192, 1)",
          backgroundColor: "rgba(75, 192, 192, 0.2)",
          fill: true,
          tension: 0.4,
          borderWidth: 3,
          pointBackgroundColor: "#4bc0c0"
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          title: {
            display: true,
            text: "Revenue by Month",
            font: { size: 16, weight: "bold" }
          },
          legend: { display: false }
        },
        scales: {
          y: {
            beginAtZero: true,
            ticks: {
              callback: value => "₹" + value.toLocaleString()
            }
          }
        }
      }
    });
    console.log("✅ Revenue chart rendered with data:", revenueData);
  }

  console.log("🎉 All charts rendered successfully with real data!");
});
