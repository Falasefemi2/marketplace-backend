# Marketplace API

This is a RESTful API for a marketplace application built with Spring Boot. It provides functionalities for user authentication, registration, and profile management, including upgrading a user to a vendor.

## Features

- User registration and login
- JWT-based authentication and authorization
- Role-based access control (USER, VENDOR, ADMIN)
- User profile management
- Upgrade user to vendor
- Refresh JWT tokens

## Technologies Used

- **Java 21**
- **Spring Boot 3.5.5**
  - Spring Web
  - Spring Security
  - Spring Data JPA
  - Spring Boot Actuator
- **MySQL**: Database
- **Lombok**: To reduce boilerplate code
- **JJWT**: For creating and parsing JSON Web Tokens
- **ModelMapper**: For object mapping
- **Maven**: Dependency management
- **Docker**: For containerization

## Prerequisites

- Java 21
- Maven
- Docker (optional, for running with Docker Compose)

## Installation and Setup

1.  **Clone the repository:**

    ```bash
    git clone https://github.com/Falasefemi2/marketplace-backend.git
    cd marketplace-backend
    ```

2.  **Create a `.env` file:**

    Create a `.env` file in the root directory and add the following environment variables:

    ```
    DB_USERNAME=your-db-username
    DB_PASSWORD=your-db-password
    ```

3.  **Build the project:**

    ```bash
    ./mvnw clean install
    ```

## Running the Application

### Using Docker Compose

The easiest way to run the application is with Docker Compose.

```bash
docker-compose up -d
```

The application will be running at `http://localhost:8080`.

### Using Maven

You can also run the application using the Spring Boot Maven plugin:

```bash
./mvnw spring-boot:run
```

## API Endpoints

All endpoints are prefixed with `/api`.

### Authentication

- **`POST /auth/register`**: Register a new user.

  **Request Body:**

  ```json
  {
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "password": "password123"
  }
  ```

- **`POST /auth/login`**: Authenticate a user and receive a JWT.

  **Request Body:**

  ```json
  {
    "email": "john.doe@example.com",
    "password": "password123"
  }
  ```

- **`POST /auth/refresh`**: Refresh an expired JWT.

  **Request Header:**

  ```
  Refresh-Token: <your-refresh-token>
  ```

### User

- **`GET /auth/profile`**: Get the profile of the authenticated user.

  **Authorization:** `Bearer <your-jwt>`

- **`POST /auth/upgrade-to-vendor`**: Upgrade a user to a vendor.

  **Authorization:** `Bearer <your-jwt>`

  **Request Body:**

  ```json
  {
    "businessName": "John's Store",
    "businessDescription": "Selling the best products."
  }
  ```

## Running Tests

To run the tests, use the following command:

```bash
./mvnw test
```
