# TODO: Fix BCrypt Password Authentication Issue

## Problem Analysis
- Registration works correctly with BCrypt password encoding
- Login fails with "Invalid credentials" even with correct email/password
- Issue: HTML form uses `name="email"` but Spring Security expects `username` parameter by default

## Tasks
- [x] Update SecurityConfig.java to specify correct form login parameter names
- [x] Verify PasswordEncoder configuration (already correct)
- [x] Verify UserServiceImpl authenticateUser method (already correct)
- [x] Verify AuthController API login (already correct)
- [x] Test the complete authentication flow

## Files to Modify
- medly-pharma/src/main/java/com/example/medlypharma/config/SecurityConfig.java

# TODO: Fix Ambiguous Mapping for /cart Endpoint

## Problem Analysis
- Spring Boot application fails to start due to ambiguous mapping
- Two controllers have @GetMapping("/cart"):
  - ViewController.viewCart(Model)
  - CartViewController.viewCart(Authentication, Model, RedirectAttributes)
- Error: "Cannot map 'viewController' method to {GET [/cart]}: There is already 'cartViewController' bean method mapped."

## Tasks
- [x] Remove duplicate /cart mapping from ViewController.java
- [x] Keep CartViewController.java as the primary cart handler
- [x] Verify application starts successfully

## Files Modified
- medly-pharma/src/main/java/com/example/medlypharma/controller/ViewController.java (removed duplicate method)
