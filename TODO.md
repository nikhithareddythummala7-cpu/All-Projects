# TODO: Fix Booking System

## 1. Edit restaurantDetails.html
- [x] Change "Book Now" button to use data attributes (data-table-id, data-capacity, data-status) and onclick="openBookingForm(this)"
- [x] Add hidden input for restaurant.id in booking form

## 2. Edit booking.js
- [x] Rename selectTableForBooking to openBookingForm(button) and update to use data attributes, check status
- [x] Add incrementTime(time, hours) function
- [x] Update submitBooking(): Add validations, check availability via API, use fetch() with encodeURIComponent(), send startTime and endTime
- [x] Add getToken() function for auth

## 3. Test and Verify
- [ ] Test booking flow: Select table, fill form, submit
- [ ] Check for errors, backend validation
- [ ] Ensure no Thymeleaf errors in console
