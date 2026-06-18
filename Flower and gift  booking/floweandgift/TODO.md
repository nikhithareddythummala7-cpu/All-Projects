# TODO: Fix 500 Internal Server Error on GET /orders

## Completed Tasks
- [x] Investigate the cause of the 500 error on GET /orders endpoint
- [x] Identify that getUserId() can return null, causing issues in orderService.getOrdersByUser(null)
- [x] Add null checks in OrderController.orderHistory() to redirect to /login if userId is null
- [x] Add null checks in OrderController.orderDetails() to redirect to /login if userId is null
- [x] Add null checks in OrderController.cancelOrder() to redirect to /login if userId is null
- [x] Add null check in orders/history.html template for order.createdAt to prevent Thymeleaf errors

## Summary of Changes
- Modified OrderController.java to handle null userId by redirecting unauthenticated users to /login
- Updated orders/history.html to safely handle null createdAt dates

## Testing
- The application should now handle unauthenticated access to /orders gracefully by redirecting to login
- Orders with null createdAt dates will display "N/A" instead of causing template errors
