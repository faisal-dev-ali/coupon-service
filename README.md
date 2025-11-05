# 🧾 Coupon Service

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green)
![MySQL](https://img.shields.io/badge/MySQL-8.0-orange)
![Redis](https://img.shields.io/badge/Redis-Cache-red)
![Kafka](https://img.shields.io/badge/Kafka-Event%20Bus-black)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

## 🚀 Overview

**Coupon Service** is a backend microservice built with **Spring Boot**, providing APIs to create, validate, and apply discount coupons across multiple business domains like:

- ✈️ Flights  
- 🏨 Hotels  
- 🚌 Buses  
- 🎁 eGift Cards  
- 🍎 Apple Store  

The service supports **flat, percentage, and cashback** coupons with configurable rules, start/end validity windows, and stackable logic.

---

## 🧱 Tech Stack

| Layer | Technology |
|-------|-------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.x |
| **Database** | MySQL 8 |
| **Cache** | Redis |
| **Broker** | Kafka |
| **Build Tool** | Maven |
| **ORM** | Spring Data JPA (Hibernate) |
| **Validation** | Jakarta Validation |
| **Logging** | SLF4J + Logback |
| **Date/Time Zone** | IST (`Asia/Kolkata`) |

---

## ⚙️ Features

✅ Create, update, delete coupons  
✅ Validate coupon eligibility (date range, min cart, domain, limits)  
✅ Apply coupon and compute discount  
✅ JSON-based extensible metadata for campaigns  
✅ Timezone-safe (`Asia/Kolkata` for all timestamps)  
✅ Docker-ready microservice configuration  
✅ Centralized validation and exception handling  

---

---

## 🧩 Setup Instructions

### 1️⃣ Clone the repository

```bash
git clone https://github.com/faisal-dev-ali/coupon-service.git
cd coupon-service

2️⃣ Configure Database
Update src/main/resources/application.yml with your local credentials:
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/coupon_service
    username: root
    password: yourpassword
  jackson:
    time-zone: Asia/Kolkata

Then run:
mysql -u root -p
CREATE DATABASE coupon_service;
🐳 Run via Docker (Optional)
Build JAR
mvn clean package -DskipTests
Run MySQL and Redis
docker-compose up -d
Run Application
java -jar target/coupon-service-0.0.1-SNAPSHOT.jar
App will start on 👉 http://localhost:8085/coupon-service

📡 API Endpoints

| Method | Endpoint                   | Description                       |
| ------ | -------------------------- | --------------------------------- |
| `POST` | `/api/v1/coupons`          | Create a new coupon               |
| `GET`  | `/api/v1/coupons`          | Get all coupons                   |
| `GET`  | `/api/v1/coupons/{code}`   | Get coupon by code                |
| `POST` | `/api/v1/coupons/validate` | Validate coupon before applying   |
| `POST` | `/api/v1/coupons/apply`    | Apply coupon and get final amount |


🧠 Example Coupon Payloads
✈️ Flight Coupon
{
  "code": "FLYHIGH500",
  "title": "₹500 Off Domestic Flights",
  "description": "Flat ₹500 off on domestic flights above ₹3,000.",
  "domain": "FLIGHT",
  "type": "FLAT",
  "value": 500,
  "maxDiscount": 500,
  "minCartValue": 3000,
  "stackable": false,
  "startAt": "2025-11-06T00:00:00+05:30",
  "endAt": "2026-01-01T23:59:59+05:30"
}
🍎 Apple Coupon
{
  "code": "APPLEFEST2000",
  "title": "₹2000 Off on iPhones and MacBooks",
  "description": "Flat ₹2000 off on Apple devices worth ₹50,000+.",
  "domain": "APPLE",
  "type": "FLAT",
  "value": 2000,
  "maxDiscount": 2000,
  "minCartValue": 50000,
  "stackable": false,
  "startAt": "2025-11-10T00:00:00+05:30",
  "endAt": "2025-12-31T23:59:59+05:30"
}
🧪 Postman Collection
You can import the ready-to-use Postman collection:
👉 coupon-validation-tests.postman_collection.json
It includes:
Validation for Flight / Hotel / Bus / eGiftCard / Apple
Apply coupon examples
Expired / inactive coupon tests
🕒 Timezone Handling
All timestamps are stored and processed in IST (Asia/Kolkata).
Layer	Type	Zone
Java	LocalDateTime	ZoneId.of("Asia/Kolkata")
Jackson	Asia/Kolkata	Configured via application.yml
MySQL	DATETIME	Stores literal IST time
🧰 Developer Notes
Java Version: 17
Build: mvn clean install
Run: mvn spring-boot:run
Port: 8085
Context Path: /coupon-service
🧑‍💻 Author
Faisal Ali
💼 Backend Developer | Java | Spring Boot | Microservices
📧 faisalali.dev@gmail.com
🌐 GitHub: faisal-dev-ali
🪪 License
This project is licensed under the MIT License.
