# TODO: Implement Role-Based Access Control (RBAC) for Admin Features

## Current Status
- Basic admin dashboard and user management controllers exist
- SecurityConfig has method security enabled
- User model has role field but authority naming is incorrect
- Admin paths not properly secured in SecurityConfig
- Missing admin controllers for prescription, medicine, order management, and reports

## Pending Tasks
- [x] Fix User.java authority naming to use Spring Security conventions (ROLE_ADMIN, ROLE_USER)
- [x] Update SecurityConfig to secure /admin/** paths for ADMIN role only
- [x] Update DashboardController to properly check for ROLE_ADMIN authority
- [x] Create AdminPrescriptionController for prescription management
- [x] Create AdminMedicineController for medicine catalog management
- [x] Create AdminOrderController for order management
- [x] Create AdminReportsController for reports and analytics
- [x] Update admin dashboard template to include navigation to all admin sections
- [ ] Add admin-specific templates for prescription, medicine, order management
- [ ] Test role-based access and redirects
- [ ] Update UserService to support role-based operations if needed
