# TODO: Debug 500 Internal Server Error

## Information Gathered
- Compilation succeeds, no syntax errors found in OrderServiceImpl.java
- The createOrder(String userEmail, List<OrderItem> items) method is implemented but has a logic error: OrderItem.unitPrice is not set, causing NullPointerException in getTotalPrice() when calculating order total
- This runtime error leads to 500 Internal Server Error caught by GlobalExceptionHandler
- OrderItem model has unitPrice field that must be set to medicine.price for total calculation

## Plan
- Set unitPrice for each OrderItem to item.getMedicine().getPrice() before calculating totals
- Ensure all OrderItem fields are properly initialized in createOrder methods

## Dependent Files to be edited
- medly-pharma/src/main/java/com/example/medlypharma/service/OrderServiceImpl.java

## Followup steps
- Test the application after fix to ensure 500 error is resolved
- Verify order creation functionality works correctly
- Check for any other runtime errors in the codebase
