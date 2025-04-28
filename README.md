# Auto Hub Reactive

Auto Hub Reactive is a reactive microservices-based application built using Spring WebFlux. The project is designed to handle various aspects of a car rental service, including car management, booking, customer management, email notifications, and more.

## Project Structure

The project is organized into multiple modules, each responsible for a specific domain:

- `auto-hub-reactive-common`: Common utilities and shared code.
- `auto-hub-reactive-api-gateway`: API Gateway for routing and load balancing.
- `auto-hub-reactive-agency`: Manages car-related operations.
- `auto-hub-reactive-ai`: AI-related functionalities.
- `auto-hub-reactive-audit`: Auditing services.
- `auto-hub-reactive-booking`: Booking management.
- `auto-hub-reactive-customer`: Customer management.
- `auto-hub-reactive-email-notification`: Email notification services.
- `auto-hub-reactive-expense`: Expense management.
- `auto-hub-reactive-request-validator`: Request validation services.

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
   git clone https://github.com/georgeniculae/auto-hub-reactive.git
