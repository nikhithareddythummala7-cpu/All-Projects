# TODO: Implement Working Charts in Reports & Analytics Dashboard

## Overview
The Reports & Analytics dashboard currently shows empty chart placeholders. Need to implement backend logic to populate charts with real data from the database.

## Tasks

### 1. Add Analytics Methods to OrderService
- [x] Add `getOrdersByMonth()` method to OrderService interface
- [x] Add `getRevenueByMonth()` method to OrderService interface
- [x] Implement both methods in OrderServiceImpl
- [x] Methods should return data for last 12 months

### 2. Add Top Selling Products Method to MedicineService
- [x] Add `getTopSellingProducts(int limit)` method to MedicineService interface
- [x] Implement method in MedicineServiceImpl
- [x] Method should aggregate sales data from OrderItems
- [x] Return List<Medicine> with quantitySold populated

### 3. Update AdminReportsController
- [x] Replace empty data initialization with calls to new service methods
- [x] Populate ordersByMonth, revenueByMonth, and topSellingProducts
- [x] Add proper error handling for data retrieval

### 4. Update Frontend to Use Real Data
- [x] Inject backend data into JavaScript variables in reports template
- [x] Update JavaScript chart code to use real data instead of hardcoded values
- [x] Add fallback handling for empty data

### 5. Test and Verify
- [ ] Test charts display correctly with sample data
- [ ] Verify data accuracy
- [ ] Ensure charts update on page load

## Technical Details
- Charts use Chart.js library (already included)
- Data formats: Map<String, Long> for orders, Map<String, Double> for revenue, List<Medicine> for products
- Time period: Last 12 months
- Status filter: Only completed/delivered orders for revenue

## Files to Modify
- `OrderService.java`
- `OrderServiceImpl.java`
- `MedicineService.java`
- `MedicineServiceImpl.java`
- `AdminReportsController.java`
- `admin/reports/index.html` (template)
- `admin-dashboard-charts-fixed.js` (JavaScript)
