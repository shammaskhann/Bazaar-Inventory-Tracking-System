# Bazaar Inventory Management System 

## 📌 **Overview**

This system evolved from a **single-store inventory tracker** (Stage 1) to a **scalable, distributed platform** (Stage 3) supporting thousands of stores. Below is a breakdown of key decisions, trade-offs, and architecture at each stage.

---

## 🛠️ Tech Stack

| Category            | Technologies Used                      |
|---------------------|----------------------------------------|
| **Backend Framework** | Spring Boot (Java 17)                |
| **Database**        | Microsoft SQL Server (Primary & Replica), Redis (Cache) |
| **Messaging Queue** | RabbitMQ                                |
| **Containerization**| Docker                                  |
| **API Testing**     | Postman                                 |
| **API Protocol**    | RESTful (JSON), Event-driven messaging  |
| **DevOps & Deployment** | NGINX (Load Balancer), Docker Compose |
| **Caching**         | Redis                                   |
| **Others**          | JWT Auth, Rate Limiting|


# 🛠️ How to Run the Application

### 🚀 Prerequisites
- Java 17+
- Maven 3.6+
- Microsoft SQL Server (or any configured DB)
- Docker & Docker Compose
- RabbitMQ & Redis (required for Stage 3)

---

### 📦 Build & Run

#### 🧪 Local Deployment (Stage 1 or Stage 2)
```
# Clone the repository
git clone https://github.com/your-org/inventory-management.git
cd inventory-management

# Build the project
mvn clean install

# Run the application locally
mvn spring-boot:run
# The application will be available on port 8080
# Visit: http://localhost:8080

```
# 🧪 Local Deployment (Stage 3)
```
# Clone the repository
git clone https://github.com/your-org/inventory-management.git
cd inventory-management

# Build the project
mvn clean install

# Build and run the Docker containers
docker-compose up --build

# The application will be available on port 8080
# Visit: http://localhost:8080
```





## 🏗️ **Design Decisions & Trade-Offs**

### **Stage 1: Single-Store Foundation**

**Goal**: Basic inventory tracking for one store.

**Key Decisions**:
- ✅ **SQLite Database**: Simple file-based storage for local development.
- ✅ **Manual JDBC**: Raw SQL queries for maximum control.
- ✅ **Synchronous API**: Direct CRUD operations with no caching.

**Trade-Offs**:
- ⚠️ **No Scalability**: Only works for one store.
- ⚠️ **No Security**: No authentication or rate limiting.

---

### **Stage 2: Multi-Store API**

**Goal**: Support **500+ stores** with centralized management.

**Key Decisions**:
- ✅ **SQL Server Migration**: Replaced SQLite for production readiness.
- ✅ **JWT Authentication**: Added secure role-based access control.
- ✅ **Rate Limiting**: Protected APIs from abuse (`Resilience4j`).
- ✅ **Central Product Catalog**: Single source of truth for products.

**Trade-Offs**:
- ⚠️ **Vertical Scaling Only**: Still relies on a single DB instance.
- ⚠️ **Synchronous Writes**: Performance bottlenecks under high load.

---

### **Stage 3: Distributed & Scalable**

**Goal**: Support **thousands of stores** with **real-time sync** and **high availability**.

| **Feature**             | **Decision**                                                     | **Trade-Off**                                |
|-------------------------|------------------------------------------------------------------|-----------------------------------------------|
| **Master-Slave DB**     | Read replicas reduce load on the primary DB.                     | Increased DB management complexity.           |
| **Event-Driven Writes** | Stock movements queued via **RabbitMQ** for async processing.    | Eventual consistency (not strongly consistent). |
| **Redis Caching**       | Frequent queries (e.g., inventory) cached for 10 mins.           | Slight delay in cache invalidation.           |
| **Load Balancing**      | **NGINX** distributes traffic across multiple app instances.     | Requires sticky sessions for some use cases.  |
| **Health Checks**       | `/health` endpoint for monitoring and load balancing.            | Adds minor maintenance overhead.              |

**Why These Changes?**
- **Scalability**: Handles **concurrent users** and **multiple stores** efficiently.
- **Resilience**: Survives DB failures (read replicas + async processing).
- **Performance**: Caching + async writes reduce API latency.

---

## 📜 **Detailed Assumptions**

### **Stage 1 Assumptions**

🔹 **User Model**:
- Single admin manages all operations
- No multi-user concurrency requirements
- All actions treated as trusted (no audit trails)

🔹 **Data Requirements**:
- Inventory changes occur infrequently (<100/day)
- Product catalog changes are rare
- Historical data can be purged periodically

🔹 **Operational Constraints**:
- Runs on a single machine with local storage
- Downtime during updates is acceptable
- Backup procedures are manual

### **Stage 2 Assumptions**

🔹 **Scale Boundaries**:
- Maximum 500 stores with ~100 products each
- Peak traffic of 50 requests/second
- Regional deployment (single geography)

🔹 **Data Consistency**:
- 100ms max acceptable latency for stock updates
- Product catalog changes propagate immediately
- Reporting data can be 5 minutes stale

🔹 **Security Model**:
- All store managers are trusted internal users
- No need for IP restrictions or VPN access
- Password rotation every 90 days is sufficient

### **Stage 3 Assumptions**

🔹 **Performance Characteristics**:
- Read:Write ratio of 80:20 (heavy reporting load)
- Acceptable 2–5 second delay for stock synchronization
- 99.5% uptime SLA requirement

🔹 **Deployment Environment**:
- Cloud-native infrastructure (AWS/Azure)
- Containerized workloads with orchestration
- Multi-region disaster recovery not required

🔹 **Operational Complexities**:
- Dedicated DevOps team available
- Monitoring and alerting systems in place
- Can tolerate 15-minute recovery time objective

---

## 🚀 **Detailed API Evolution (v1 → v3)**

### **Stage 1 API (Monolithic)**

**Core Characteristics**:
- **Synchronous Request-Response**
```
http
POST /stock-in
Content-Type: application/x-www-form-urlencoded

productId=123&quantity=10
```

- **Stateful Operations**  
  - Immediate database commits  
  - No idempotency guarantees  
  - No bulk operation support  

**Limitations**:
- All requests block until DB completion  
- No partial failure handling  
- Hardcoded business logic  

### **Stage 2 API (Service-Oriented)**

**Key Improvements**:
- **RESTful Design**  
```
http
  POST /api/v2/inventory/stock-movements
  Authorization: Bearer eyJhbG...
  Content-Type: application/json

  {
    "productId": "123",
    "storeId": "456",
    "quantity": 5,
    "movementType": "STOCK_IN"
  }
```

**Enhanced Capabilities**:
- Batch processing support  
- Request validation middleware  
- Paginated responses for large datasets  

**New Features**:
- Audit logging on all mutations  
- Conditional requests (ETag/Last-Modified)  
- Deprecation headers for API versioning  

### *Stage 3 API (Distributed)*

**Architectural Shifts**:
- *Event-Driven Endpoints*  
```
http
  POST /api/v3/stock-movements
  X-Idempotency-Key: a1b2c3d4...

  {
    "eventId": "evt_789",
    "product": {"id": "123"},
    "store": {"id": "456"},
    "metadata": {
      "initiatedBy": "user@company.com",
      "source": "mobile-app-v3.2"
    }
  }
```

**Advanced Patterns**:
- Circuit breakers for dependent services  
- Structured error responses with remediation hints  
- Content negotiation (JSON/Protobuf)  

**Performance Optimizations**:
- Edge caching with CDN support  
- Streaming responses for large reports  
- Connection pooling and keep-alive  

---

## 🔄 *Evolution Rationale*  

| *Requirement*          | *Stage 1* | *Stage 2* | *Stage 3* |
|------------------------|-----------|-----------|-----------|
| *Stores Supported*     | 1         | 500+      | 1000s     |
| *Auth*                 | ❌ No      | ✅ JWT     | ✅ JWT + Rate Limiting |
| *DB*                   | SQLite    | SQL Server| *Master-Slave + Caching* |
| *Scalability*          | ❌ Single instance | ❌ Vertical scaling only | ✅ *Horizontal scaling* (Docker + NGINX) |
| *Performance*          | Basic     | Improved  | *Optimized* (async + caching) |
| *Consistency*          | Strong    | Strong    | *Eventual* (due to async) |

---

## 📌 *Final Thoughts*  

- *Stage 1*: Simple but limited.  
- *Stage 2*: Secure but not scalable.  
- *Stage 3*: **Production-ready**, handles **high traffic**, and **scales dynamically**.  

**Trade-Offs Made in Stage 3**:  
✔ *Eventual consistency* for better scalability.  
✔ *Increased complexity* (RabbitMQ, Redis, load balancing).  
✔ *More infrastructure* to manage (containers, DB replication).  
 

---


This system now meets *enterprise-grade* requirements while balancing *performance, scalability, and reliability*. 🎯  
