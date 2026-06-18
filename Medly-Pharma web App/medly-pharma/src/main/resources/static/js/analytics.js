/**
 * Analytics JavaScript for Medly Pharma
 * Handles dashboard charts and analytics
 */

document.addEventListener('DOMContentLoaded', function() {
    // Initialize all charts when the DOM is fully loaded
    initCharts();
    
    // Set up auto-refresh for charts (every 5 minutes)
    setInterval(initCharts, 5 * 60 * 1000);
});

/**
 * Initialize all charts on the dashboard
 */
async function initCharts() {
    try {
        // Load data from API endpoints
        const [ordersData, productsData, revenueData] = await Promise.all([
            fetchData('/api/orders/stats'),
            fetchData('/api/analytics/top-products'),
            fetchData('/api/revenue/monthly')
        ]);
        
        // Initialize charts with the fetched data
        initOrdersChart(ordersData);
        initProductsChart(productsData);
        initRevenueChart(revenueData);
        
    } catch (error) {
        console.error('Error initializing charts:', error);
        showToast('Error', 'Failed to load analytics data', 'error');
    }
}

/**
 * Initialize Orders Overview chart
 */
function initOrdersChart(ordersData) {
    const ctx = document.getElementById('ordersChart');
    if (!ctx) return;
    
    // Process orders data for the chart
    const labels = ordersData.map(item => new Date(item.date).toLocaleDateString('en-US', { month: 'short', day: 'numeric' }));
    const data = ordersData.map(item => item.count);
    
    // Destroy existing chart instance if it exists
    if (window.ordersChart) {
        window.ordersChart.destroy();
    }
    
    // Create new chart instance
    window.ordersChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Orders',
                data: data,
                backgroundColor: 'rgba(78, 115, 223, 0.7)',
                borderColor: 'rgba(78, 115, 223, 1)',
                borderWidth: 1,
                borderRadius: 4,
                barPercentage: 0.7,
                categoryPercentage: 0.8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleFont: { size: 14, weight: 'bold' },
                    bodyFont: { size: 13 },
                    padding: 12,
                    cornerRadius: 6,
                    displayColors: false,
                    callbacks: {
                        label: function(context) {
                            return `Orders: ${context.raw}`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        display: true,
                        drawBorder: false,
                        color: 'rgba(0, 0, 0, 0.05)'
                    },
                    ticks: {
                        stepSize: 1
                    }
                },
                x: {
                    grid: {
                        display: false,
                        drawBorder: false
                    }
                }
            }
        }
    });
}

/**
 * Initialize Top Selling Products chart
 */
function initProductsChart(productsData) {
    const ctx = document.getElementById('productsChart');
    if (!ctx) return;
    
    // Process products data for the chart
    const labels = productsData.map(item => item.name);
    const data = productsData.map(item => item.quantitySold);
    const backgroundColors = generateColors(data.length, 0.7);
    const borderColors = generateColors(data.length, 1);
    
    // Destroy existing chart instance if it exists
    if (window.productsChart) {
        window.productsChart.destroy();
    }
    
    // Create new chart instance
    window.productsChart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: backgroundColors,
                borderColor: borderColors,
                borderWidth: 1,
                hoverOffset: 10
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '70%',
            plugins: {
                legend: {
                    position: 'right',
                    labels: {
                        padding: 20,
                        usePointStyle: true,
                        pointStyle: 'circle'
                    }
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleFont: { size: 14, weight: 'bold' },
                    bodyFont: { size: 13 },
                    padding: 12,
                    cornerRadius: 6,
                    displayColors: false,
                    callbacks: {
                        label: function(context) {
                            const label = context.label || '';
                            const value = context.raw || 0;
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const percentage = Math.round((value / total) * 100);
                            return `${label}: ${value} units (${percentage}%)`;
                        }
                    }
                }
            }
        }
    });
}

/**
 * Initialize Revenue by Month chart
 */
function initRevenueChart(revenueData) {
    const ctx = document.getElementById('revenueChart');
    if (!ctx) return;
    
    // Process revenue data for the chart
    const labels = Object.keys(revenueData).map(month => 
        new Date(month).toLocaleDateString('en-US', { year: 'numeric', month: 'short' })
    );
    const data = Object.values(revenueData);
    
    // Destroy existing chart instance if it exists
    if (window.revenueChart) {
        window.revenueChart.destroy();
    }
    
    // Create gradient for the area under the line
    const gradient = ctx.getContext('2d').createLinearGradient(0, 0, 0, 400);
    gradient.addColorStop(0, 'rgba(40, 167, 69, 0.2)');
    gradient.addColorStop(1, 'rgba(40, 167, 69, 0.05)');
    
    // Create new chart instance
    window.revenueChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Revenue',
                data: data,
                fill: true,
                backgroundColor: gradient,
                borderColor: 'rgba(40, 167, 69, 1)',
                borderWidth: 2,
                pointBackgroundColor: 'rgba(40, 167, 69, 1)',
                pointBorderColor: '#fff',
                pointHoverRadius: 6,
                pointHoverBackgroundColor: '#fff',
                pointHoverBorderColor: 'rgba(40, 167, 69, 1)',
                pointHoverBorderWidth: 2,
                pointRadius: 4,
                pointHitRadius: 10,
                tension: 0.3
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    backgroundColor: 'rgba(0, 0, 0, 0.8)',
                    titleFont: { size: 14, weight: 'bold' },
                    bodyFont: { size: 13 },
                    padding: 12,
                    cornerRadius: 6,
                    displayColors: false,
                    callbacks: {
                        label: function(context) {
                            return `Revenue: $${context.raw.toLocaleString()}`;
                        }
                    }
                }
            },
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        display: true,
                        drawBorder: false,
                        color: 'rgba(0, 0, 0, 0.05)'
                    },
                    ticks: {
                        callback: function(value) {
                            return '$' + value.toLocaleString();
                        }
                    }
                },
                x: {
                    grid: {
                        display: false,
                        drawBorder: false
                    }
                }
            }
        }
    });
}

/**
 * Helper function to fetch data from API endpoints
 */
async function fetchData(endpoint) {
    try {
        const response = await fetch(endpoint, {
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            credentials: 'same-origin'
        });
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        return await response.json();
    } catch (error) {
        console.error(`Error fetching data from ${endpoint}:`, error);
        throw error;
    }
}

/**
 * Generate an array of colors for charts
 */
function generateColors(count, opacity = 1) {
    const colors = [
        'rgba(78, 115, 223, {opacity})',    // Primary blue
        'rgba(40, 167, 69, {opacity})',     // Success green
        'rgba(255, 193, 7, {opacity})',     // Warning yellow
        'rgba(220, 53, 69, {opacity})',     // Danger red
        'rgba(23, 162, 184, {opacity})',    // Info teal
        'rgba(111, 66, 193, {opacity})',    // Purple
        'rgba(253, 126, 20, {opacity})',    // Orange
        'rgba(13, 110, 253, {opacity})',    // Blue
        'rgba(25, 135, 84, {opacity})',     // Green
        'rgba(255, 193, 7, {opacity})'      // Yellow
    ];
    
    // If we need more colors than we have, cycle through the array
    return Array(count).fill().map((_, i) => 
        colors[i % colors.length].replace('{opacity}', opacity)
    );
}

// Make functions available globally
window.medlyAnalytics = {
    initCharts,
    initOrdersChart,
    initProductsChart,
    initRevenueChart,
    fetchData
};
