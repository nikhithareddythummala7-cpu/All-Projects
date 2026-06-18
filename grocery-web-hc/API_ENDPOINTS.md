# Grocery Web Application API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication Endpoints

### 1. Register User
**POST** `/auth/signup`

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "role": ["user"]
}
```

**Response:**
```json
{
  "message": "User registered successfully!",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

### 2. Login User
**POST** `/auth/signin`

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "roles": ["ROLE_USER"]
}
```

## Product Endpoints

### 3. Get All Products
**GET** `/products`

**Response:**
```json
[
  {
    "id": 1,
    "name": "Organic Apples",
    "description": "Fresh organic apples from local farm",
    "price": 2.99,
    "stock": 50,
    "category": "Fruits",
    "imageUrl": "https://example.com/apples.jpg",
    "unit": "kg",
    "inStock": true
  },
  {
    "id": 2,
    "name": "Whole Wheat Bread",
    "description": "Freshly baked whole wheat bread",
    "price": 3.49,
    "stock": 25,
    "category": "Bakery",
    "imageUrl": "https://example.com/bread.jpg",
    "unit": "pieces",
    "inStock": true
  }
]
```

### 4. Create Product (Admin Only)
**POST** `/products`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "name": "Organic Bananas",
  "description": "Fresh organic bananas",
  "price": 1.99,
  "stock": 75,
  "category": "Fruits",
  "imageUrl": "https://example.com/bananas.jpg",
  "unit": "kg",
  "inStock": true
}
```

**Response:**
```json
{
  "id": 3,
  "name": "Organic Bananas",
  "description": "Fresh organic bananas",
  "price": 1.99,
  "stock": 75,
  "category": "Fruits",
  "imageUrl": "https://example.com/bananas.jpg",
  "unit": "kg",
  "inStock": true
}
```

### 5. Get Product by ID
**GET** `/products/{id}`

**Response:**
```json
{
  "id": 1,
  "name": "Organic Apples",
  "description": "Fresh organic apples from local farm",
  "price": 2.99,
  "stock": 50,
  "category": "Fruits",
  "imageUrl": "https://example.com/apples.jpg",
  "unit": "kg",
  "inStock": true
}
```

### 6. Update Product (Admin Only)
**PUT** `/products/{id}`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "name": "Organic Apples Updated",
  "description": "Premium organic apples",
  "price": 3.49,
  "stock": 40,
  "category": "Fruits",
  "imageUrl": "https://example.com/apples-premium.jpg",
  "unit": "kg",
  "inStock": true
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Organic Apples Updated",
  "description": "Premium organic apples",
  "price": 3.49,
  "stock": 40,
  "category": "Fruits",
  "imageUrl": "https://example.com/apples-premium.jpg",
  "unit": "kg",
  "inStock": true
}
```

### 7. Delete Product (Admin Only)
**DELETE** `/products/{id}`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "message": "Product deleted successfully"
}
```

## Cart Endpoints

### 8. Get User Cart
**GET** `/cart/{userId}`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
[
  {
    "id": 1,
    "quantity": 2,
    "product": {
      "id": 1,
      "name": "Organic Apples",
      "description": "Fresh organic apples from local farm",
      "price": 2.99,
      "stock": 50,
      "category": "Fruits",
      "imageUrl": "https://example.com/apples.jpg",
      "unit": "kg",
      "inStock": true
    }
  }
]
```

### 9. Add to Cart
**POST** `/cart/{userId}`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "productId": 1,
  "quantity": 2
}
```

**Response:**
```json
{
  "id": 1,
  "quantity": 2,
  "product": {
    "id": 1,
    "name": "Organic Apples",
    "description": "Fresh organic apples from local farm",
    "price": 2.99,
    "stock": 50,
    "category": "Fruits",
    "imageUrl": "https://example.com/apples.jpg",
    "unit": "kg",
    "inStock": true
  }
}
```

### 10. Update Cart Item
**PUT** `/cart/{userId}/{itemId}`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Request Body:**
```json
{
  "quantity": 3
}
```

**Response:**
```json
{
  "id": 1,
  "quantity": 3,
  "product": {
    "id": 1,
    "name": "Organic Apples",
    "description": "Fresh organic apples from local farm",
    "price": 2.99,
    "stock": 50,
    "category": "Fruits",
    "imageUrl": "https://example.com/apples.jpg",
    "unit": "kg",
    "inStock": true
  }
}
```

### 11. Remove from Cart
**DELETE** `/cart/{userId}/{itemId}`

**Headers:**
```
Authorization: Bearer <jwt_token>
```

**Response:**
```json
{
  "message": "Item removed from cart"
}
```

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid request parameters",
  "path": "/api/products"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/products"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied",
  "path": "/api/products"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "path": "/api/products/999"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2024-01-15T10:30:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/products"
}
```

## Authentication Headers
All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## Product Unit Types
Available unit values for products:
- `pieces` - Individual items
- `kg` - Kilograms
- `g` - Grams  
- `lb` - Pounds
- `pack` - Packaged items
- `bottle` - Bottles
- `can` - Cans
- `box` - Boxes

## User Roles
- `ROLE_USER` - Regular user (can browse products, manage cart)
- `ROLE_ADMIN` - Administrator (can manage products and users)
