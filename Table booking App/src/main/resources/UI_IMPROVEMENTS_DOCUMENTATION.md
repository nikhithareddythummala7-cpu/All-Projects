# Table Booking Web App - UI Improvements Documentation

## Overview
This document provides a complete guide to the modern UI design system implemented across the Table Booking Web App. All pages have been enhanced with contemporary design patterns, responsive layouts, and smooth interactions.

---

## 📁 CSS Files Structure

### Core Stylesheets
1. **`style.css`** - Global styles and responsive design system
   - Full-screen background with overlay
   - Fixed navbar and footer with glassmorphism
   - Button variants and forms
   - Table styling with hover effects
   - Mobile responsive utilities
   - Hero section and featured sections

2. **`ui-components.css`** - Reusable component styles
   - Cards and card panels with hover animations
   - Badge and status indicators
   - Alert/notification styling
   - Stats boxes with gradient backgrounds
   - Modals with smooth animations
   - Restaurant and table grid layouts
   - Empty state illustrations
   - Utility classes (margins, padding, flex)

3. **`dashboard-modern.css`** - Dashboard layout and styling
   - Fixed sidebar navigation (280px width)
   - Main content area with flexible layout
   - Stats cards with icons
   - Data tables with interactive rows
   - Action buttons (edit, delete, view)
   - Pagination controls
   - Responsive dashboard for mobile

4. **`toast-notifications.css`** - Notifications and feedback
   - Toast notification system (top-right)
   - Success/error/warning/info variants
   - Loading overlay with spinner
   - Confirmation dialogs
   - Status badges (Pending/Confirmed/Cancelled)
   - Breadcrumbs navigation
   - Tooltips

5. **`owner-modern.css`** - Owner-specific pages
   - Table management cards
   - Booking management tables
   - Profile sections with image upload
   - Modal forms with modern styling
   - Image upload drop zones
   - Responsive grid layouts

6. **`modern-design.css`** - Existing design system (kept)
   - Hero sections
   - Navigation with underline effects
   - Typography system
   - Search containers

7. **`modern-buttons.css`** - Button variants (kept)
   - Primary, ghost, flat buttons
   - Multiple color variants
   - Hover and active states

8. **`customer.css`** & **`admin.css`** & **`auth.css`** - Page-specific styles
   - Existing custom styles maintained

---

## 🎨 Design System

### Color Palette
```css
Primary:        #007bff (Blue)
Primary Dark:   #0056b3
Secondary:      #6c757d (Gray)
Success:        #28a745 (Green)
Danger:         #dc3545 (Red)
Warning:        #ffc107 (Yellow)
Info:           #17a2b8 (Teal)

Text Primary:   #212529
Text Secondary: #6c757d
Backgrounds:    #ffffff, #f8f9fa, #f1f3f5
```

### Typography
- Font Family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif
- Base Font Size: 15px
- Line Height: 1.6

### Spacing System
- Margin/Padding: 0.5rem (8px) to 3rem (48px)
- Border Radius: 8px (default), 6px (small), 25px (rounded pills)
- Gap Utilities: gap-1 (0.5rem) to gap-3 (1.5rem)

### Shadow System
- xs: 0 1px 3px rgba(0, 0, 0, 0.08)
- sm: 0 2px 8px rgba(0, 0, 0, 0.1)
- md: 0 4px 16px rgba(0, 0, 0, 0.12) [default]
- lg: 0 8px 24px rgba(0, 0, 0, 0.15)
- xl: 0 12px 32px rgba(0, 0, 0, 0.2)

### Transitions
- Fast: 0.15s ease
- Normal: 0.3s ease (default)
- Slow: 0.5s ease

---

## 📱 Page-by-Page Improvements

### 1. Login Page (`/login`)
**Features:**
- Full-screen gradient background (purple to violet)
- Centered card layout with rounded corners
- Blue gradient header with icon
- Email and password inputs with focus glow
- Modern login button with hover effects
- "Back to Home" link with icon
- Alert messages for errors/success
- Footer with sign-up link
- Responsive for mobile (max-width: 450px)

**CSS Classes Used:**
- `.login-container`, `.login-header`, `.login-body`, `.login-footer`
- `.form-control:focus` with glow shadow
- `.btn-modern`, `.btn-modern-primary`

---

### 2. Register Page (`/register`)
**Features:**
- Similar layout to login page
- Dual-role selection buttons (Customer/Owner)
- Role buttons with icons and descriptions
- Radio buttons with visual selection
- Role-specific icon and color changes
- Responsive grid layout (2 columns → 1 column)
- Alert messages for validation
- Bootstrap form group styling
- Active state indicator with checkmark

**CSS Classes Used:**
- `.register-container`, `.register-body`
- `.role-selection`, `.role-buttons`, `.role-btn.active`
- `.role-icon` with gradient backgrounds
- `.checkmark` for selection indicator

---

### 3. Home Page (`/`)
**Features:**
- Full-screen hero section with background image
- Gradient overlay for text readability
- Large hero title with text shadow
- Search container with modern input and button
- Featured restaurants grid
- Card-based restaurant display
- Restaurant cards with hover lift effect
- Rating stars and cuisine info
- Background image (fixed, cover, center)

**CSS Classes Used:**
- `.hero`, `.hero-content`, `.search-container`
- `.featured-restaurants`, `.restaurant-grid`
- `.restaurant-card:hover` with transform

---

### 4. Admin Dashboard (`/admin/dashboard`)
**Features:**
- Fixed sidebar navigation (280px)
- Main content area with flexible layout
- Page header with title and refresh button
- Stats cards with icons (4 column grid)
- Pending approvals table
- Recent users table
- Recent bookings table
- Approval/Rejection action buttons
- Table rows with hover highlighting
- Responsive sidebar for mobile
- Data refresh functionality

**Structure:**
```html
<header> - Fixed top navbar
<main>
  <container>
    <dashboard-header>
    <stats-container> (4 cards)
    <table-container> x3
      - Pending Restaurants
      - Recent Users
      - Recent Bookings
```

**CSS Classes Used:**
- `.dashboard`, `.dashboard-sidebar`, `.dashboard-main`
- `.stats-container`, `.stat-card`
- `.table-container`, `.table-header`, `.table-content`
- `.action-buttons`, `.action-btn-*`

---

### 5. Customer Dashboard (`/customer/dashboard`)
**Features:**
- Hero section with welcome message
- Quick stats cards (4 columns)
- Sidebar navigation on left
- Main content area
- Right sidebar with additional info
- Quick action cards
- Recent bookings list
- Popular restaurants grid
- Trending restaurants section
- Recommended restaurants large grid
- Top-rated restaurants sidebar
- Pro tips sidebar section
- Smooth animations and transitions
- Loading spinner while fetching data
- Three-column layout (responsive to 1 column)

**Layout:**
```
Header (fixed top)
Hero Section
Stats Grid (4 columns)
Main Dashboard Layout:
  - Left Sidebar (Quick links, Help, Promo)
  - Main Content (Actions, Bookings, Activity, Restaurants)
  - Right Sidebar (Stats, Top Rated, Tips)
```

**Features:**
- JavaScript loads real data from API
- Dynamic restaurant and booking rendering
- Stat cards with gradients
- Sidebar sections with icons
- Activity tracker
- Empty states with icons
- Responsive grid for restaurants

---

### 6. Restaurant Owner Dashboard (`/owner/dashboard`)
**Features:**
- Navigation bar with owner-specific links
- Welcome section with restaurant name
- Today's bookings card
- Quick stats card
- Total bookings display
- Available tables count
- Average rating display
- Total reviews count
- Modern card layout

**CSS Classes Used:**
- `.card`, `.card-body`, `.card-title`
- `.stat-card`, `.stat-number`

---

### 7. Owner Tables Management (`/owner/tables`)
**Features:**
- Page header with title and subtitle
- Add Table button (top-right)
- Table cards in grid layout
- Table number display
- Status badge (Available/Occupied/Reserved)
- Table details panel:
  - Seating capacity
  - Location/Section
  - Price per cover
  - Status
- Action buttons (Edit, Delete)
- Hover animation (lift effect)
- Empty state when no tables
- Responsive grid (single column on mobile)
- Modal for adding/editing tables
- Modal form with validation

**Card Elements:**
```
[Table Card]
┌─────────────────────┐
│ Table #1    [Status]│
├─────────────────────┤
│ Seats: 4            │
│ Section: Main Hall  │
│ Price: $50          │
├─────────────────────┤
│ [Edit] [Delete]     │
└─────────────────────┘
```

**CSS Classes Used:**
- `.table-card`, `.table-card-header`, `.table-details`
- `.status-available`, `.status-occupied`, `.status-reserved`
- `.table-card-footer`, `.table-edit`, `.table-delete`

---

### 8. Owner Bookings Management (`/owner/bookings`)
**Features:**
- Bookings header with filters
- Data table with columns:
  - Customer Name
  - Booking Date/Time
  - Party Size
  - Table
  - Status
  - Actions
- Action buttons:
  - Confirm booking
  - Cancel booking
  - View details
- Status indicators:
  - Pending (yellow)
  - Confirmed (green)
  - Cancelled (red)
- Row hover highlighting
- Responsive table with scrolling on mobile
- Filters for date/status
- Sorting capabilities

**Status Colors:**
- Pending: #fff3cd (yellow)
- Confirmed: #d4edda (green)
- Cancelled: #f8d7da (red)

---

### 9. Owner Profile (`/owner/profile`)
**Features:**
- Header with restaurant image
- Restaurant name and cuisine type
- Profile sections:
  - Basic Information
  - Address & Location
  - Contact Details
  - Operating Hours
  - Facilities & Amenities
- Image upload drop zone
- Edit fields with focus glow
- Save and Cancel buttons
- Textarea for description
- Input fields with labels
- Responsive form layout
- Image preview
- Success/error notifications

**Form Structure:**
```
Profile Header (with image)
├─ Basic Info Section
├─ Address Section
├─ Hours Section
└─ Actions [Save] [Cancel]
```

---

### 10. Booking Creation (`/createBooking`)
**Features:**
- Page title and description
- Form with fields:
  - Restaurant selector
  - Table selector
  - Booking date/time
  - Number of guests
- Submit button
- Responsive form layout
- Input validation
- Error messages
- Success notifications

---

### 11. My Bookings (`/bookings/my`)
**Features:**
- Bookings list view
- Booking cards/rows with:
  - Restaurant name
  - Booking date/time
  - Party size
  - Table info
  - Status badge
  - Action buttons
- Filter options
- Sort by date
- Cancel booking option
- View details option
- Empty state when no bookings

---

### 12. Restaurant Details (`/restaurantDetails`)
**Features:**
- Hero image section
- Restaurant info header
- About section
- Amenities list
- Photos gallery
- Reviews section
- Book table button
- Rating display
- Opening hours
- Contact information
- Map location (if available)

---

## 🎯 Key UI Features

### 1. **Glassmorphism**
- Navbar and footer use `backdrop-filter: blur(10px)`
- Semi-transparent backgrounds with rgba
- Used on fixed positioned elements

### 2. **Gradient Backgrounds**
- Primary: `linear-gradient(135deg, #007bff 0%, #0056b3 100%)`
- Success: `linear-gradient(135deg, #28a745 0%, #1e7e34 100%)`
- Applied to headers, buttons, stat cards

### 3. **Smooth Animations**
- Hover lift effect: `transform: translateY(-2px)`
- Fade-in animations: `animation: fadeIn 0.3s ease-out`
- Slide animations for modals and notifications
- Progress bar animation for toasts

### 4. **Responsive Design**
- Breakpoints:
  - Desktop: > 992px
  - Tablet: 768px - 992px
  - Mobile: < 768px
  - Phone: < 480px

### 5. **Interactive Elements**
- Buttons with scale + shadow on hover
- Table rows with background color change
- Links with underline animation
- Active navigation highlighting
- Focus states on inputs with glow

### 6. **Notifications**
- Toast system at top-right
- Success/Error/Warning/Info variants
- Auto-dismiss with progress bar
- Confirmation dialogs
- Loading overlays

---

## 📦 JavaScript Utilities (`ui-notifications.js`)

### Classes Available

#### Toast
```javascript
toast.success(message, duration, title)
toast.error(message, duration, title)
toast.warning(message, duration, title)
toast.info(message, duration, title)
```

#### LoadingOverlay
```javascript
LoadingOverlay.show(message)
LoadingOverlay.hide()
LoadingOverlay.setMessage(message)
```

#### ConfirmDialog
```javascript
ConfirmDialog.show(title, message, onConfirm, onCancel)
```

#### UIUtils
- `scrollToElement(selector, offset)`
- `setActiveNav(selector)`
- `formatCurrency(value, currency)`
- `formatDate(date, locale)`
- `formatTime(date, locale)`
- `isInViewport(element)`
- `validateEmail(email)`
- `validatePhone(phone)`
- `debounce(func, wait)`
- `ripple(event)` - Button ripple effect
- `highlightRow(selector)` - Table row selection

---

## 🚀 Implementation Checklist

### CSS Files to Include in HTML
```html
<!-- Global & Components -->
<link rel="stylesheet" th:href="@{/css/style.css}">
<link rel="stylesheet" th:href="@{/css/ui-components.css}">
<link rel="stylesheet" th:href="@{/css/toast-notifications.css}">

<!-- Specific Pages -->
<link rel="stylesheet" th:href="@{/css/dashboard-modern.css}">  <!-- For dashboards -->
<link rel="stylesheet" th:href="@{/css/owner-modern.css}">      <!-- For owner pages -->
<link rel="stylesheet" th:href="@{/css/modern-design.css}">     <!-- Existing -->
<link rel="stylesheet" th:href="@{/css/modern-buttons.css}">    <!-- Existing -->
```

### JavaScript to Include
```html
<!-- At end of body -->
<script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/js/all.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
<script th:src="@{/js/ui-notifications.js}"></script>
```

---

## 📋 Styling Guidelines

### Button Styling
```css
/* Primary Button */
.btn {
    background: linear-gradient(135deg, #007bff, #0056b3);
    padding: 12px 24px;
    border-radius: 8px;
    font-weight: 600;
    transition: all 0.3s ease;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}
```

### Form Inputs
```css
input, select, textarea {
    padding: 12px 16px;
    border: 2px solid #dee2e6;
    border-radius: 8px;
    transition: all 0.3s ease;
}

input:focus {
    outline: none;
    border-color: #007bff;
    box-shadow: 0 0 0 3px rgba(0, 123, 255, 0.1);
}
```

### Cards
```css
.card {
    background: white;
    border-radius: 8px;
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    transition: all 0.3s ease;
    padding: 20px;
}

.card:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}
```

---

## 🎬 Animation Library

All animations are smooth 0.3s transitions:

1. **Fade In** - `fadeIn` - Used for components appearing
2. **Slide In Left** - `slideInLeft` - Sidebar/drawer opening
3. **Slide In Right** - `slideInRight` - Notifications
4. **Slide Up** - `slideUp` - Modals
5. **Slide Out Right** - `slideOutRight` - Toast dismissal
6. **Scale In** - `scaleIn` - Component expansion
7. **Toast Progress** - `toastProgress` - Auto-dismiss bar

---

## 📐 Responsive Behavior

### Dashboard Sidebar
- Desktop (>992px): Fixed 280px sidebar
- Tablet (768-992px): Horizontal flexible menu
- Mobile (<768px): Collapsible menu

### Grid Layouts
- Desktop: 4+ columns
- Tablet: 2-3 columns
- Mobile: 1 column

### Tables
- Desktop: Full width with all columns
- Mobile: Horizontal scroll, smaller font

### Forms
- Desktop: Multi-column layouts
- Mobile: Single column, full width

---

## 🔄 Status Badge Colors

| Status | Color | Background |
|--------|-------|------------|
| Pending | #856404 | #fff3cd |
| Confirmed | #155724 | #d4edda |
| Cancelled | #721c24 | #f8d7da |
| Completed | #155724 | #d4edda |

---

## 🎯 Best Practices

1. **Always include Font Awesome** for icons
2. **Use CSS variables** for consistent theming
3. **Apply smooth transitions** for interactive elements
4. **Use utility classes** for spacing (mt-, mb-, p-, etc.)
5. **Maintain color consistency** with primary blue theme
6. **Test responsive layouts** on all breakpoints
7. **Ensure proper z-index** for fixed elements (navbar: 1000, footer: 999, modals: 2000)
8. **Use semantic HTML** for accessibility
9. **Keep shadow depths consistent** with system
10. **Always provide loading states** for async operations

---

## 📞 Support

For questions or issues with the UI implementation, refer to:
- CSS files for styling details
- JavaScript file for notification system
- This documentation for page structures

Last Updated: December 2024
