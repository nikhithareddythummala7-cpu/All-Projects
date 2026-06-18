// Admin Dashboard Charts - Complete Working Implementation
console.log("🚀 Initializing Admin Dashboard Charts...");

// Check if Chart.js is loaded
if (typeof Chart === 'undefined') {
    alert('Chart.js library failed to load. Please check your internet connection and refresh the page.');
    console.error('❌ Chart.js not found');
} else {
    console.log("✅ Chart.js loaded successfully");
}

document.addEventListener("DOMContentLoaded", function() {
    console.log("📊 DOM Content Loaded - Starting Chart Rendering...");

    // Ensure chart containers are visible
    const chartContainers = document.querySelectorAll('.chart-area, .chart-pie, .chart-bar');
    chartContainers.forEach(container => {
        container.style.display = 'block';
        container.style.height = 'auto';
        container.style.minHeight = '300px';
    });

    // Ensure canvases are visible and have background
    const canvases = document.querySelectorAll('canvas');
    canvases.forEach(canvas => {
        canvas.style.display = 'block';
        canvas.style.height = '300px';
        canvas.style.width = '100%';
        canvas.style.backgroundColor = '#f8f9fc';
        canvas.style.border = '1px solid #e3e6f0';
        canvas.style.borderRadius = '0.35rem';
    });

    // Dummy data
    const ordersByDate = {
        labels: ["Oct 20", "Oct 21", "Oct 22", "Oct 23", "Oct 24"],
        data: [2, 4, 3, 5, 6]
    };

    const topProducts = {
        labels: ["Paracetamol", "Azithromycin", "Amoxicillin", "Dolo 650"],
        data: [20, 15, 10, 8]
    };

    const revenueByMonth = {
        labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun"],
        data: [2000, 3500, 3000, 4500, 5000, 6200]
    };

    try {
        // 🟢 Orders Overview (Bar Chart)
        console.log("✅ Starting Orders Chart Render...");
        const ordersCanvas = document.getElementById('ordersChart');
        if (ordersCanvas) {
            const ordersChart = new Chart(ordersCanvas, {
                type: 'bar',
                data: {
                    labels: ordersByDate.labels,
                    datasets: [{
                        label: 'Orders',
                        data: ordersByDate.data,
                        backgroundColor: 'rgba(54, 162, 235, 0.7)',
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: { precision: 0 }
                        }
                    }
                }
            });
            ordersChart.update();
            console.log("✅ Orders Chart Rendered Successfully");
        } else {
            console.error("❌ Orders Chart Canvas not found");
        }

        // 🟣 Top Selling Products (Pie Chart)
        console.log("✅ Starting Top Products Chart Render...");
        const topProductsCanvas = document.getElementById('topProductsChart');
        if (topProductsCanvas) {
            const topProductsChart = new Chart(topProductsCanvas, {
                type: 'pie',
                data: {
                    labels: topProducts.labels,
                    datasets: [{
                        data: topProducts.data,
                        backgroundColor: [
                            'rgba(255, 99, 132, 0.7)',
                            'rgba(54, 162, 235, 0.7)',
                            'rgba(255, 206, 86, 0.7)',
                            'rgba(75, 192, 192, 0.7)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    }
                }
            });
            topProductsChart.update();
            console.log("✅ Top Products Chart Rendered Successfully");

            // Generate legend
            const legendContainer = document.getElementById('topProductsLegend');
            if (legendContainer) {
                legendContainer.innerHTML = '';
                topProducts.labels.forEach((label, index) => {
                    const item = document.createElement('div');
                    item.className = 'd-flex align-items-center mb-2';
                    item.innerHTML = `
                        <div class="me-2" style="width: 12px; height: 12px; background-color: rgba(255, 99, 132, 0.7); border-radius: 50%;"></div>
                        <div class="small text-truncate" style="max-width: 120px;" title="${label}">${label}</div>
                        <div class="ms-auto fw-bold">${topProducts.data[index] || 0}</div>
                    `;
                    legendContainer.appendChild(item);
                });
            }
        } else {
            console.error("❌ Top Products Chart Canvas not found");
        }

        // 🟢 Revenue by Month (Line Chart)
        console.log("✅ Starting Revenue Chart Render...");
        const revenueCanvas = document.getElementById('revenueChart');
        if (revenueCanvas) {
            const revenueChart = new Chart(revenueCanvas, {
                type: 'line',
                data: {
                    labels: revenueByMonth.labels,
                    datasets: [{
                        label: 'Revenue (₹)',
                        data: revenueByMonth.data,
                        borderColor: 'rgba(75, 192, 192, 1)',
                        backgroundColor: 'rgba(75, 192, 192, 0.2)',
                        fill: true,
                        tension: 0.3
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: { display: false }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function(value) {
                                    return '₹' + value.toLocaleString();
                                }
                            }
                        }
                    }
                }
            });
            revenueChart.update();
            console.log("✅ Revenue Chart Rendered Successfully");
        } else {
            console.error("❌ Revenue Chart Canvas not found");
        }

        console.log("🎉 All Charts Rendered Successfully!");

    } catch (error) {
        console.error("❌ Error rendering charts:", error);
        alert("Error rendering charts: " + error.message);
    }
});
