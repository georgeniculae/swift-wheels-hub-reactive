# Swift Wheels Hub Reactive

Swift Wheels Hub Reactive is a reactive microservices-based application built using Spring WebFlux. The project is designed to handle various aspects of a car rental service, including car management, booking, customer management, email notifications, and more.

## Project Structure

The project is organized into multiple modules, each responsible for a specific domain:

- `swift-wheels-hub-reactive-common`: Common utilities and shared code.
- `swift-wheels-hub-reactive-api-gateway`: API Gateway for routing and load balancing.
- `swift-wheels-hub-reactive-agency`: Manages car-related operations.
- `swift-wheels-hub-reactive-ai`: AI-related functionalities.
- `swift-wheels-hub-reactive-audit`: Auditing services.
- `swift-wheels-hub-reactive-booking`: Booking management.
- `swift-wheels-hub-reactive-customer`: Customer management.
- `swift-wheels-hub-reactive-email-notification`: Email notification services.
- `swift-wheels-hub-reactive-expense`: Expense management.
- `swift-wheels-hub-reactive-request-validator`: Request validation services.

## Technologies Used

- Java
- Spring Boot
- Spring WebFlux
- Maven
- Reactive Programming
- Kafka

## Flow Description

### Car Management

The `CarHandler` class handles various car-related operations such as finding, saving, updating, and deleting cars. It uses the `CarService` to perform these operations and returns `Mono<ServerResponse>` for reactive handling.

### Booking Management

The `BookingUpdateProducerService` class is responsible for sending booking update messages to Kafka using `StreamBridge`. It builds messages with necessary headers and sends them asynchronously.

### Kafka Consumers

There are multiple Kafka consumers, such as `BookingCarStatusUpdateConsumer` and `BookingCarStatusSaveConsumer`, which listen to specific topics and process messages accordingly. They use the `Acknowledgment` interface to acknowledge message processing.

### Email Notifications

The email notification module handles sending email notifications related to various events in the system.

## Running the Project

To run the project, use the following Maven command:

### Installation
Clone the repository:
```sh
   git clone https://github.com/georgeniculae/swift-wheels-hub-reactive.git
