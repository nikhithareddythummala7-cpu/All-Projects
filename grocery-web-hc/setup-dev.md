# Development Setup Guide

## Prerequisites
- Java 17+ (for Spring Boot backend)
- Node.js 16+ (for React frontend)
- MySQL 8.0+ (database)

## Backend Setup (Port 8080)

1. **Start MySQL Database**
   ```bash
   # Ensure MySQL is running on localhost:3306
   # Database: freshcart (will be created automatically)
   # Username: root
   # Password: Nikki@123
   ```

2. **Start Spring Boot Application**
   ```bash
   # From project root directory
   ./mvnw spring-boot:run
   # or on Windows
   mvnw.cmd spring-boot:run
   ```

## Frontend Setup (Port 3000)

1. **Install Dependencies**
   ```bash
   cd frontend
   npm install
   ```

2. **Create Environment File**
   ```bash
   # Copy the example file
   cp .env.example .env
   # Edit .env if you need to change the API URL
   ```

3. **Start React Development Server**
   ```bash
   npm start
   ```

## Testing the Connection

1. **Backend Health Check**
   - Open browser: http://localhost:8080/api/products
   - Should return JSON array of products

2. **Frontend Health Check**
   - Open browser: http://localhost:3000
   - Should display the React app

3. **API Integration Test**
   - Try registering a new user at: http://localhost:3000/register
   - Check browser Network tab to confirm requests go to port 8080

## Troubleshooting

### 403 Forbidden Error
- Ensure backend is running on port 8080
- Check CORS configuration in WebSecurityConfig.java
- Verify proxy configuration in frontend/package.json

### Database Connection Issues
- Check MySQL is running on port 3306
- Verify credentials in application.properties
- Ensure database 'freshcart' exists or can be created

### Port Conflicts
- Backend: Change port in application.properties: `server.port=8081`
- Frontend: Change port with: `PORT=3001 npm start`
