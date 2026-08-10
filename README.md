# Spring Boot Kafka Microservices Demo

Event-driven microservices example using:

- Java 21
- Spring Boot
- Apache Kafka 4.3.1 (KRaft mode)
- Gradle Wrapper
- IntelliJ IDEA

## Project Structure

```
microservices
│
├── event-contracts/
│   └── src/main/java/
│       └── org/example/events/
│           └── OrderEvent.java
│
├── inventory-service/
│   └── src/main/java/
│       └── org/example/inventoryservice/
│           └── InventoryServiceApplication.java
│
├── order-service/
│   ├── src/main/java/
│   │   └── org/example/orderservice/
│   │       └── OrderServiceApplication.java
│   └── test.http
│
├── scripts/
│   ├── create-kafka-topic.bat
│   ├── init-kafka.bat
│   └── start-kafka.bat
│
├── run-services.ps1
├── start-local-env.ps1
└── README.md
```

---

# Kafka Setup (Local Installation)

Kafka is running locally in KRaft mode.

## Option 1: Start the complete environment (Recommended)

The repository includes a startup script that automatically:

- Starts Kafka
- Waits until Kafka is available
- Creates the required Kafka topic
- Starts both Spring Boot applications

---

## First-time setup

Before running the complete startup script for the first time, Kafka storage must be initialized.

Run:

```cmd
scripts\init-kafka.bat
```

This will:

- Generate a Kafka cluster ID
- Format Kafka storage
- Prepare Kafka for startup

> **Note:** This only needs to be done once for a new Kafka data directory.

---

## Start everything

After Kafka storage has been initialized, run:

```powershell
.\start-local-env.ps1
```

The script starts the complete local environment:

```
start-local-env.ps1
        |
        ├── start-kafka.bat
        |
        ├── Wait until Kafka is available
        |
        ├── create-kafka-topic.bat
        |       |
        |       ├── topic exists → continue
        |       |
        |       └── topic missing → create it
        |
        └── run-services.ps1
                |
                ├── order-service
                |
                └── inventory-service
```

---

## Kafka installation path

The provided Kafka scripts expect Kafka to be installed in the kafka folder inside your Windows user profile:

```
C:\Users\<Your_User>\kafka
```

The scripts automatically detect the current Windows username, so no manual changes are required.


# Option 2: Automated Kafka and Service Startup

## Start Kafka

Run:

```cmd
scripts\start-kafka.bat
```

Kafka runs on:

```
localhost:9092
```

---

## Create Kafka Topic

The services use the `order-created` Kafka topic for communication.

Create the topic:

```cmd
scripts\create-kafka-topic.bat
```

The script creates:

```
order-created
```

Verify manually:

```bash
bin/windows/kafka-topics.bat ^
--list ^
--bootstrap-server localhost:9092
```

Expected output:

```
order-created
```

---

## Start Services

Run:

```powershell
.\run-services.ps1
```

This starts both Spring Boot applications automatically.

---

# Option 3: Manual Kafka Setup and Startup

## 1. Format Kafka Storage

Generate a cluster ID:

```bash
bin/windows/kafka-storage.bat random-uuid
```

Example:

```
M8vF4cQxR7kP2nL9aBcDeA
```

Format storage:

```bash
bin/windows/kafka-storage.bat format ^
-t M8vF4cQxR7kP2nL9aBcDeA ^
-c config/server.properties ^
--standalone
```

---

## 2. Start Kafka

Run:

```bash
bin/windows/kafka-server-start.bat config/server.properties
```

Kafka runs on:

```
localhost:9092
```

---

# Services

## 1. order-service

Responsibilities:

- Provides REST API
- Creates orders
- Generates UUID order ID
- Generates created time
- Publishes events to Kafka

Endpoint:

```
POST /orders
```

Example request:

```json
{
  "customerId": 15,
  "product": "Laptop",
  "quantity": 2
}
```

Example response:

```json
{
  "orderId": "uuid-value",
  "customerId": 15,
  "product": "Laptop",
  "quantity": 2,
  "createdTime": "2026-08-06T22:30:00"
}
```

Runs on:

```
http://localhost:8080
```

---

## 2. inventory-service

Responsibilities:

- Consumes `order-created` Kafka messages
- Prints order information

Example output:

```
Processing order:
OrderId: abc-123
Product: Laptop
Quantity: 2
```

Runs on:

```
http://localhost:8081
```

---

# Running Both Services

The project contains:

```
run-services.ps1
```

This script starts both Spring Boot applications automatically.

Run:

```powershell
.\run-services.ps1
```

It opens two PowerShell windows.

## order-service

Runs:

```powershell
.\gradlew bootRun
```

Expected:

```
Tomcat started on port 8080
```

---

## inventory-service

Runs:

```powershell
.\gradlew bootRun
```

Expected:

```
Tomcat started on port 8081
```

---

# Testing

This section explains how to test the order service and verify that the order is successfully published to Kafka and processed by the consuming service.

## 1. Send an Order Request

There are two ways to send a request to the order service.

### Option 1: IntelliJ HTTP Client

The REST request is stored in:

```text
order-service/test.http
```

Example:

```http
POST http://localhost:8080/orders
Content-Type: application/json

{
  "customerId": 15,
  "product": "Laptop",
  "quantity": 2
}
```

Open `test.http` in IntelliJ and click:

```text
▶ POST http://localhost:8080/orders
```

The expected response is:

```json
{
  "orderId": "generated-uuid",
  "customerId": 15,
  "product": "Laptop",
  "quantity": 2,
  "createdTime": "2026-08-06T22:30:00"
}
```

### Option 2: PowerShell

Alternatively, the request can be sent directly from a PowerShell terminal using `Invoke-RestMethod`:

```powershell
Invoke-RestMethod `
-Uri http://localhost:8080/orders `
-Method POST `
-Headers @{
    "Content-Type"="application/json"
} `
-Body '{
"customerId":15,
"product":"Laptop",
"quantity":2
}'
```

This should return the created order, for example:

```text
orderId      : generated-uuid
customerId   : 15
product      : Laptop
quantity     : 2
createdTime  : 2026-08-06T22:30:00
```

---

## 2. Verify the Kafka Message

To verify that the order was published to Kafka, start a Kafka console consumer (if not already running):

```bash
bin/windows/kafka-console-consumer.bat ^
--topic order-created ^
--bootstrap-server localhost:9092 ^
--from-beginning
```

Then send an order using either the IntelliJ HTTP Client or the PowerShell command above.

Kafka should display a message similar to:

```json
{
  "orderId": "...",
  "customerId": 15,
  "product": "Laptop",
  "quantity": 2,
  "createdTime": "..."
}
```

---

## 3. Verify Order Processing

The consuming service should receive the Kafka message and process the order.

The service should output:

```text
Processing order:
OrderId: abc-123
Product: Laptop
Quantity: 2
```

The `OrderId` will be different for each request because a new UUID is generated for every order.

### End-to-End Flow

The complete flow is:

```text
HTTP Request
     │
     ▼
Order Service
     │
     ▼
Kafka: order-created
     │
     ▼
Consumer Service
     │
     ▼
Processing order
```

If all three steps produce the expected output, the REST → Kafka → consumer flow is working correctly.
