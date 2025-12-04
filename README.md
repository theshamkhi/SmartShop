# 🛒 SmartShop - Commercial Management REST API

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen?style=for-the-badge&logo=spring)
![Maven](https://img.shields.io/badge/Maven-3.6+-blue?style=for-the-badge&logo=apache-maven)

**A modern, fully-featured REST API for commercial management built with Spring Boot and Domain-Driven Design principles.**

</div>

---

## 📖 Overview

SmartShop is a comprehensive REST API backend for commercial management, featuring user authentication, product inventory, order processing, payment handling, and an intelligent customer loyalty program. Built with enterprise-grade patterns and best practices.

### 🎯 Key Highlights

- 🔐 **Session-Based Authentication** - Secure login/logout without JWT complexity
- 💳 **Multi-Payment Support** - Cash, Check, and Wire Transfer processing
- 🎁 **Smart Loyalty Program** - Automatic tier upgrades (BASIC → SILVER → GOLD → PLATINUM)
- 💰 **Dynamic Pricing Engine** - Promo codes + loyalty discounts + VAT calculation
- 🛡️ **Role-Based Access Control** - Granular permissions for ADMIN and CLIENT roles
- 📦 **Soft Delete Pattern** - Products marked as deleted without data loss
- ✅ **Comprehensive Validation** - Business rules enforcement at every layer
- 📚 **Auto-Generated API Docs** - Interactive Swagger UI included

---

## ✨ Features

### 👥 User Management
- Dual role system (Admin/Client) with inheritance-based design
- Session-based authentication with HTTP-only cookies
- Secure credential management

### 📦 Product Management
- Full CRUD operations with soft delete
- Stock tracking and validation
- Product catalog with availability status

### 🛒 Order Processing
- Multi-item order creation with validation
- Real-time stock availability checking
- Order lifecycle management (PENDING → CONFIRMED/CANCELED/REJECTED)
- Automatic pricing calculations:
    - Subtotal (HT)
    - Discounts (Promo + Loyalty)
    - VAT (20%)
    - Total (TTC)

### 💰 Payment System
- Multiple payment methods (Cash, Check, Wire Transfer)
- Payment amount validation
- Cash transaction limit enforcement (20,000 DH)
- Sequential payment numbering per order
- Full payment tracking before order confirmation

### 🏆 Loyalty Program

| Tier | Requirements | Discount |
|------|-------------|----------|
| 🥉 **BASIC** | Default | 0% |
| 🥈 **SILVER** | 5+ orders OR 2,000 DH | 5% |
| 🥇 **GOLD** | 10+ orders OR 5,000 DH | 10% |
| 💎 **PLATINUM** | 20+ orders OR 10,000 DH | 15% |

*Automatic tier calculation after each confirmed order*

---

## 🚀 Quick Start

### Prerequisites

```bash
☕ Java 17+
📦 Maven 3.6+
```

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/theshamkhi/SmartShop.git
cd SmartShop
```

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

4. **Access the application**
- API Base URL: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/api/swagger-ui.html`

---

## 📚 API Documentation

<img width="1920" height="1436" alt="swagger" src="https://github.com/user-attachments/assets/5d92b546-0588-44d2-9b9f-48b2c38e1827" />

### 🔐 Authentication

```http
POST /api/auth/login
POST /api/auth/logout
GET  /api/auth/me
```

### 👥 Clients (Users)

```http
POST   /api/clients          # Create client (ADMIN)
GET    /api/clients          # List all clients (ADMIN)
GET    /api/clients/{id}     # Get client details
PUT    /api/clients/{id}     # Update client (ADMIN)
DELETE /api/clients/{id}     # Delete client (ADMIN)
```

### 📦 Products

```http
POST   /api/products         # Create product (ADMIN)
GET    /api/products         # List all products
GET    /api/products/{id}    # Get product details
PUT    /api/products/{id}    # Update product (ADMIN)
DELETE /api/products/{id}    # Soft delete (ADMIN)
```

### 🛒 Orders (Commandes)

```http
POST   /api/commandes                    # Create order
GET    /api/commandes                    # List orders (filtered by role)
GET    /api/commandes/{id}               # Get order details
GET    /api/commandes/client/{clientId}  # Get client orders
PUT    /api/commandes/{id}/confirm       # Confirm order (ADMIN)
PUT    /api/commandes/{id}/cancel        # Cancel order
PUT    /api/commandes/{id}/reject        # Reject order (ADMIN)
```

### 💳 Payments (Paiements)

```http
POST   /api/paiements        # Create payment (ADMIN)
GET    /api/paiements        # List all payments (ADMIN)
GET    /api/paiements/{id}   # Get payment details (ADMIN)
DELETE /api/paiements/{id}   # Delete payment (ADMIN)
```

---

## 🏗 Architecture

### Layered DDD Architecture

```
📁 com.smartshop.smartshop
├── 📂 model
│   ├── entity/      # Domain entities (User, Client, Product, etc.)
│   └── enums/       # Business enumerations
│   └── dto/ 
│       ├── request/     # API request DTOs
│       └── response/    # API response DTOs
├── 📂 repository    # Data access layer (Spring Data JPA)
├── 📂 mapper        # MapStruct mappers
├── 📂 service       # Business logic layer
├── 📂 controller    # REST API endpoints
├── 📂 exception     # Custom exceptions & handlers
└── 📂 config        # Configuration classes
```

### UML Diagram

<img width="851" height="952" alt="Class" src="https://github.com/user-attachments/assets/2cee1a3e-e2d1-4d46-922a-116b2c0f0864" />

---

## 🧪 Testing

### Run Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CommandeServiceTest
```
---

<div align="center">

**⭐ Star this repository if you find it helpful!**

</div>