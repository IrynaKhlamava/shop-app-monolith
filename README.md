# A Monolithic Shop Application

## 1. Project Overview
This project is a monolithic shop application with a RESTful backend and a simple React frontend.  
- The backend is built with Spring Boot.  
- The database is MongoDB.  
- The frontend uses React and Bootstrap for styling.  
- Security is done with tokens (access and refresh tokens).  
- Redis is used to manage token lists and to store the customer cart.  
- All parts (application, Redis, MongoDB) run in Docker containers managed by Docker Compose.  

## 2. Technology Stack
- **Backend:**  
  - Java 17  
  - Spring Boot (RESTful API)

- **Database:**  
  - MongoDB

- **Frontend:**  
  - React  
  - Bootstrap (for UI components)

- **Security:**  
  - Token-based security (access and refresh tokens)

- **Caching & State Management:**  
  - Redis (for token black-list and customer cart)

- **Containerization:**  
  - Docker  
  - Docker Compose

---

This application allows users to browse products, add items to their cart, and complete orders.  
Token-based security keeps user sessions safe, and Redis helps manage tokens and cart data quickly.  
Docker Compose makes it easy to start and stop all services together.
