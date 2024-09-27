# TK Integration Service

## Overview

This project is a Java-based asynchronous integration service that proxies the TK Extraction service. It provides the ability to upload a CV for processing, return a process ID for tracking, and allows clients to retrieve the processed result asynchronously. The service uses **Spring Boot** and **WebFlux** to handle reactive programming, providing scalability and non-blocking operations.

### Key Features

- **Submit CV for Processing** (`/api/submit`): Upload a CV file and get an immediate `processId` for tracking.
- **Retrieve Processing Status/Result** (`/api/retrieve/{processId}`): Poll the service to retrieve either the status of the processing or the final result.
- **Asynchronous Processing**: The integration service does not wait for the result from the TK Extraction service, but instead allows the client to poll for updates.
- **In-memory Data Storage**: The service stores process results temporarily in-memory until they are retrieved.
- **Basic Authentication**: Both endpoints are protected with basic authentication to ensure that only authorized users can access the service.
- **Unit Testing**: The project includes unit tests for the controller, service, and error handling logic using **JUnit 5**, **Mockito**, and **WebTestClient**.

## How It Works

1. **Submission**: A client submits a CV file to the `/api/submit` endpoint, along with their account details. The file is asynchronously forwarded to the TK Extraction service. The client receives a `processId` immediately to track the processing.

2. **Retrieval**: The client polls the `/api/retrieve/{processId}` endpoint using the `processId` they received. If the processing is complete, the client gets the result. Otherwise, the service responds with the status `PROGRESS`.

## Project Structure

- **Controller**: Handles incoming HTTP requests and validates authentication headers.
- **Service Layer**: Manages the communication with the TK Extraction service and handles asynchronous logic for file submission and result retrieval.
- **Credentials Handler**: Extracts and validates the credentials from the `Authorization` header.
- **In-memory Storage**: Stores process results temporarily until they are retrieved.

## Setup

### Prerequisites

- **Java 17+**
- **Maven**

### Installation

1. Navigate to the project directory:
   ```bash
   cd tk-integration-service
   ```
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```

3. Run the service:
   ```bash
   java -jar target/tk-integration-service-0.0.1-SNAPSHOT.jar
   ```

### Endpoints

1. **Submit a CV for Processing**

    - **URL**: `POST /api/submit`
    - **Authorization**: Basic authentication
    - **Parameters**:
        - `uploaded_file`: The CV file (as `multipart/form-data`)
        - `account`: The account name for the TK Extraction service
    - **Response**: A process ID (`processId`) that can be used to track the status of the file.
    - **Example (curl)**:
      ```bash
      curl -X POST http://localhost:8080/api/submit \
      -H "Authorization: Basic <base64-encoded-credentials>" \
      -F "uploaded_file=@/path/to/cv.doc" \
      -F "account=your-account"
      ```

2. **Retrieve Processing Status/Result**

    - **URL**: `GET /api/retrieve/{processId}`
    - **Authorization**: Basic authentication
    - **Response**: The processing result, or status `PROGRESS` if the file is still being processed.
    - **Example (curl)**:
      ```bash
      curl -X GET http://localhost:8080/api/retrieve/process-id-123 \
      -H "Authorization: Basic <base64-encoded-credentials>"
      ```

### Running Tests

The project includes unit tests for the controller and service layer. To run the tests:

```bash
mvn test
```

This will execute all JUnit tests for the project, ensuring that the service behaves as expected.

### Design Decisions

- **Asynchronous Processing**: The service is designed with asynchronous capabilities using **Spring WebFlux** to handle non-blocking requests. This allows the client to submit files and poll for results without the service waiting for the TK Extraction service to finish.

- **Basic Authentication**: Both endpoints are protected with basic authentication, ensuring that only authenticated users can submit files or retrieve results.

- **In-memory Data Storage**: The results are stored in memory temporarily. While this is sufficient for the task, in a production environment, this could be replaced with persistent storage or a distributed cache like Redis.

- **Error Handling**: Basic error handling is implemented to return appropriate status codes and messages when something goes wrong, such as invalid credentials or missing files.

- **Unit Testing**: The project uses **Mockito** to mock dependencies and **WebTestClient** to simulate HTTP requests for controller testing. These tests ensure that the service behaves as expected under different scenarios.

### Technologies Used

- **Java 17**
- **Spring Boot**
- **Spring Mono** for reactive programming
- **Mockito** for unit testing and mocking
- **JUnit 5** for testing
- **WebTestClient** for simulating HTTP requests in tests
- **Maven** for build and dependency management

## Known Limitations

- The service is designed to store results in memory. This is not suitable for long-term storage or high-concurrency environments.
- Only Basic Authentication has been implemented which is not suitable for high concurrency environment.
---