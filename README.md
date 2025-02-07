# Authentication and Authorization Microservice

A secure authentication and authorization microservice developed with Java Spring Boot, featuring JWT authentication (with RSA-signed tokens and a public key endpoint) and OAuth2 authentication using GitHub as a provider. It also includes a Thymeleaf-based login page for testing authentication flows.

---
## Table of contents

- [Features](#-features)
- [Technical Stack](#-technical-stack)
- [API Documentation](#-api-documentation)
- [Security](#-security)
- [Notification System](#-notification-system)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
- [Database Schema](#-database-schema)
- [Future Improvements](#-future-improvements)
- [License](#-license)
- [Contributing](#-contributing)

---


## 🚀 Features

- **User authentication and registration system**
- **JWT authentication with RSA-signed tokens**
- **Public key endpoint for token verification**
- **OAuth2 authentication**: With Github as a provider
- **Refresh token support**
- **Role-based access control** with JWT authentication
- **Thymeleaf login page for testing authentication**

---

## 🛠 Technical Stack

### Core
- **Java**
- **Spring Boot**
- **PostgreSQL** or any other relational database

### Spring Framework
- **Spring Security**
- **Spring Data JPA**
- **Spring Web**
- **Spring OAuth2 Client**
- **Spring Validation**

### Tools & Libraries
- **Hibernate**
- **JWT** for authentication
- **Thymeleaf** for html page display
- **OpenAPI** for documentation
- **Lombok**
- **JDBC**

---

## 📝 API Documentation

The API is documented using **OpenAPI (Swagger)**, providing:

- Detailed endpoint descriptions
- Request/Response examples
- Authentication requirements
- Schema definitions

---

## 🔐 Security

- **JWT Authentication with RSA-signed Tokens**: Ensures secure and verifiable authentication using RSA key pairs, preventing token forgery.
- **OAuth2 Authentication with GitHub**: Users can authenticate via GitHub, eliminating the need for password management and enhancing security.
- **Role-Based Access Control (RBAC)**: Restricts access to different parts of the system based on predefined user roles, ensuring least privilege access.
- **Secure Storage of Authentication Keys**: Private keys used for signing JWTs are securely stored and never exposed in the application code.
- **Brute Force and Rate Limiting Protections**: Authentication endpoints can be configured with rate limits to prevent brute-force attacks. **To be Added**
- **Encryption and Secure Transmission**: All sensitive data is transmitted over HTTPS to prevent interception and man-in-the-middle attacks. **To be Added**
- **Audit Logging and Monitoring**: Security-related events are logged to enable detection of unauthorized access attempts and suspicious activities. **To be Added**

---

## Getting Started

### Prerequisites

- **Java Development Kit (JDK) 17+**
- **Maven**
- **PostgreSQL** or any other relational database

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/EduardoMango/UserMicroservice.git
   ```

2. Configure application properties in a `.env` file:
   ```properties
   DB_URL:postgresql://localhost:5432/your_database
   DB_USER=your_username
   DB_PASSWORD=your_password

   JWT_EXPIRATION=expiration-time-in-milliseconds
   REFRESH_TOKEN_EXPIRATION=expiration-time-in-milliseconds

   SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID=your-client-id
   SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET=your-client-secret
   ```
   
3. Build and run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access the API documentation:
   - OpenAPI Documentation: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 📈 Future Improvements

- Implement multi-factor authentication (MFA)
- Add support for more OAuth2 providers
- Improve logging and monitoring features
- Implement rate limiting for enhanced security
- Implement secure https connection

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

---

## 👥 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
