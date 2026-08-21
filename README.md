# Spring Boot Kafka Microservices Demo

Event-driven microservices example using:

- Java 21
- Spring Boot
- Apache Kafka 4.3.1 (KRaft mode)
- Gradle Wrapper
- IntelliJ IDEA

---

## Project Structure

```
microservices/
│
├── inventory-service/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── org/example/dltmonitor/
│   │       │       ├── consumer/
│   │       │       │   └── DltConsumer.java
│   │       │       │
│   │       │       └── DltMonitorApplication.java
│   │       │
│   │       └── resources/
│   │           └── application.yml
│   │
│   └── build.gradle.kts
│
├── event-contracts/
│   ├── src/
│   │   └── main/
│   │       └── java/
│   │           └── org/example/events/
│   │               ├── OrderEvent.java
│   │               ├── InventoryResult.java
│   │               └── InventoryStatus.java
│   │
│   └── build.gradle.kts
│
├── inventory-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── org/example/inventoryservice/
│   │   │   │       ├── config/
│   │   │   │       │   ├── KafkaConsumerConfig.java
│   │   │   │       │   └── KafkaProducerConfig.java
│   │   │   │       │
│   │   │   │       ├── consumer/
│   │   │   │       │   └── InventoryConsumer.java
│   │   │   │       │
│   │   │   │       ├── entity/
│   │   │   │       │   └── ProcessedMessage.java
│   │   │   │       │
│   │   │   │       ├── producer/
│   │   │   │       │   └── InventoryProducer.java
│   │   │   │       │
│   │   │   │       ├── repository/
│   │   │   │       │   └── ProcessedMessageRepository.java
│   │   │   │       │
│   │   │   │       └── InventoryServiceApplication.java
│   │   │   │
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   │
│   │   └── test/
│   │       └── ...
│   │
│   └── build.gradle.kts
│
├── notification-service/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── org/example/notificationservice/
│   │       │       ├── consumer/
│   │       │       │   └── NotificationConsumer.java
│   │       │       │
│   │       │       └── NotificationServiceApplication.java
│   │       │
│   │       └── resources/
│   │           └── application.yml
│   │
│   └── build.gradle.kts
│
├── order-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── org/example/orderservice/
│   │   │   │       │
│   │   │   │       ├── config/
│   │   │   │       │   ├── KafkaConsumerConfig.java
│   │   │   │       │   └── KafkaProducerConfig.java
│   │   │   │       │
│   │   │   │       ├── consumer/
│   │   │   │       │   └── InventoryResultConsumer.java
│   │   │   │       │
│   │   │   │       ├── controller/
│   │   │   │       │   └── OrderController.java
│   │   │   │       │
│   │   │   │       ├── dto/
│   │   │   │       │   └── OrderRequest.java
│   │   │   │       │
│   │   │   │       ├── entity/
│   │   │   │       │   ├── Order.java
│   │   │   │       │   └── OrderStatus.java
│   │   │   │       │
│   │   │   │       ├── producer/
│   │   │   │       │   └── OrderProducer.java
│   │   │   │       │
│   │   │   │       ├── repository/
│   │   │   │       │   └── OrderRepository.java
│   │   │   │       │
│   │   │   │       ├── service/
│   │   │   │       │   └── InventoryResultConsumer.java
│   │   │   │       │
│   │   │   │       └── OrderServiceApplication.java
│   │   │   │
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   │
│   │   └── test/
│   │       └── ...
│   │
│   ├── test-quantity-available.http
│   ├── test-negative-quantity.http
│   ├── test-null-product.http
│   ├── test-out-of-stock.http
│   ├── test-quantity-0.http
│   └── build.gradle.kts
│
├── scripts/
│   ├── create-kafka-topic.bat
│   ├── init-kafka.bat
│   └── start-kafka.bat
│
├── connect-orders.ps1
├── run-services.ps1
├── start-local-env.ps1
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

---

# PostgreSQL Local Development

---

## Option 1: Connecting to the orders Database using provided script (Recommended)

The project includes a connect-orders.ps1 PowerShell script for connecting to the local PostgreSQL orders database.

### What the Script Does

The script automatically:

- Detects the newest installed PostgreSQL version under C:\Program Files\PostgreSQL\.
- Locates psql.exe and pg_ctl.exe.
- Finds the PostgreSQL data directory.
- Detects the configured PostgreSQL port.
- Checks whether PostgreSQL is already running.
- Starts PostgreSQL when necessary.
- Opens/uses a non-administrator PowerShell session when the script is launched from an elevated environment.
- Connects to the orders database using the postgres user.

### Running the Script

Open PowerShell in the project directory and run:

```cmd
.\connect-orders.ps1
```

The script will automatically detect the installed PostgreSQL version and configure the connection accordingly.

### Checking the orders and processed_messages Tables
After connecting to the database, run the following queries to inspect the contents of the `orders` and `processed_messages` tables:

```sql
SELECT * FROM orders;
```

```sql
SELECT * FROM processed_messages;
```

---

## Option 2: Manual Alternative

Instead of using connect-orders.ps1, PostgreSQL can also be started and accessed manually.

For example, start PostgreSQL using pg_ctl:

```cmd
& "C:\Program Files\PostgreSQL\18\bin\pg_ctl.exe" `
    -D "C:\Program Files\PostgreSQL\18\data" `
-l "C:\Program Files\PostgreSQL\18\data\postgres-start.log" `
start
```

Then connect to the orders database using psql:

```cmd
& "C:\Program Files\PostgreSQL\18\bin\psql.exe" `
    -h localhost `
-p 5432 `
    -U postgres `
-d orders
```

The PostgreSQL version, data directory, and port may need to be adjusted depending on the local installation.

> **Note:** PostgreSQL should be started using a non-administrator Windows account. If IntelliJ IDEA or PowerShell is running with administrator privileges, use a normal (non-elevated) terminal for starting PostgreSQL.

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

### First-time setup

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

### Start everything

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
                ├── inventory-service
                |
                ├── dlt-monitor
                |
                └── notification-service
```

---

### Kafka installation path

The provided Kafka scripts expect Kafka to be installed in the kafka folder inside your Windows user profile:

```
C:\Users\<Your_User>\kafka
```

The scripts automatically detect the current Windows username, so no manual changes are required.


## Option 2: Automated Kafka and Service Startup

### Start Kafka

Run:

```cmd
scripts\start-kafka.bat
```

Kafka runs on:

```
localhost:9092
```

---

### Create Kafka Topic

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

### Start Services

Run:

```powershell
.\run-services.ps1
```

This starts all Spring Boot applications automatically.

---

## Option 3: Manual Kafka Setup and Startup

### 1. Format Kafka Storage

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

### 2. Start Kafka

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
- Validates incoming requests
- Stores orders in PostgreSQL
- Publishes `order-created events` to Kafka after a successful database save
- Consumes `inventory-result` events
- Updates order status in DB
- Supports Kafka retry and error handling

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
- Processes each order
- Performs idempotency checks using the `processed_messages` table
- Simulates inventory availability
- Returns `AVAILABLE` when quantity is `<= 5`
- Returns `OUT_OF_STOCK` when quantity is `> 5`
- Publishes the result to the `inventory-result` Kafka topic
- Supports Kafka retries and error handling

Example output:

```
==============================
Processing order:
OrderId: abc-123
Product: Laptop
Quantity: 2
==============================
```

For duplicate events:

```
Duplicate message ignored: abc-123
```

Runs on:

```
http://localhost:8081
```

---

## 3. dlt-monitor

Responsibilities:

- Consumes failed messages from the `order-created.DLT` topic
- Monitors messages that could not be processed after all configured retries
- Prints failed messages for monitoring/debugging purposes

Example output:

```
==============================
FAILED MESSAGE FROM DLT
==============================
OrderId: abc-123
CustomerId: 15
Product: Laptop
Quantity: 2
==============================
```

Runs on:

```
http://localhost:8082
```

---

## 4. notification-service

Responsibilities:

- Consumes `order-created` Kafka messages
- Demonstrates Kafka publish/subscribe behavior
- Pretends to send an email notification to the customer
- Processes the same `order-created` event independently from `inventory-service`

Example output:

```
==============================
Email sent to customer
CustomerId: 15
OrderId: abc-123
Product: Laptop
Quantity: 2
==============================
```

Runs on:

```
http://localhost:8083
```

---

# Running All Services

The project contains:

```
run-services.ps1
```

This script starts all Spring Boot applications automatically.

Run:

```powershell
.\run-services.ps1
```

It opens four PowerShell windows.

| Service                |  Port  |
| ---------------------- |:------:|
| `order-service`        | `8080` |
| `inventory-service`    | `8081` |
| `dlt-monitor`          | `8082` |
| `notification-service` | `8083` |

All services use the same Gradle command to start:

```powershell
.\gradlew bootRun
```

---

# Testing

This section explains how to test the `order-service`, verify Kafka communication, confirm inventory processing, test validation, idempotency, retries, DLT handling, and verify the notification service.

## 1. Send an Order Request

There are two ways to send a request to the order service.

### Option 1: IntelliJ HTTP Client

The REST request is stored in:

```text
order-service/test-quantity-available.http
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

Open `test-quantity-available.http` in IntelliJ and click:

```text
▶ POST http://localhost:8080/orders
```

The expected response is:

```json
{
  "orderId": "abc-123",
  "customerId": 15,
  "product": "Laptop",
  "quantity": 2,
  "createdTime": "2026-08-06T22:30:00"
}
```

The `orderId` and `createdTime` are generated by the order-service

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

The event is published only after the order has been successfully saved to PostgreSQL.

---

## 3. Verify Order Processing

The `inventory-service` consumes the `order-created` event.

For an order with quantity `2`, the inventory service should output:

```text
==============================
Processing order:
OrderId: abc-123
Product: Laptop
Quantity: 2
==============================
```

Since the quantity is `<= 5`, the inventory service should publish an `inventory-result` event with:

```json
{
"orderId": "abc-123",
"status": "AVAILABLE"
}
```

The order-service consumes this event and updates the order status in PostgreSQL from:

```text
NEW
```

to

```text
AVAILABLE
```

The `OrderId` will be different for each request because a new UUID is generated for every order.

---

## 4. Verify Notification Service

The `notification-service` also consumes the `order-created` event.

After creating an order, the notification service should print:

```text
==============================
Email sent to customer
CustomerId: 15
OrderId: abc-123
Product: Laptop
Quantity: 2
==============================
```

This demonstrates Kafka pub/sub behavior.

The same `order-created` event is consumed independently by:

```text
inventory-service
notification-service
```

Each service should use a different Kafka consumer group.

---

## Test Scenarios

### 1. Quantity ≤ 5 — AVAILABLE

The REST request is stored in:
```text 
order-service/test-quantity-available.http
```

Creates an order with a valid quantity between 1 and 5. The inventory service should return AVAILABLE, and the order status should be updated from NEW to AVAILABLE.

### 2. Quantity > 5 — OUT_OF_STOCK

The REST request is stored in:
```text 
order-service/test-out-of-stock.http
```

Creates an order with a quantity greater than 5. The inventory service should return OUT_OF_STOCK, and the order status should be updated accordingly in PostgreSQL.

### 3. Quantity = 0 — 400 Bad Request

The REST request is stored in:
```text 
order-service/test-quantity-0.http
```

Sends an order with quantity 0. Bean Validation should reject the request with 400 Bad Request. No order should be saved or published to Kafka.

### 4. Quantity < 0 — 400 Bad Request

The REST request is stored in:
```text 
order-service/test-negative-quantity.http
```

Sends an order with a negative quantity. The request should be rejected with 400 Bad Request, and no order or Kafka event should be created.

### 5. Product = null — 400 Bad Request

The REST request is stored in:
```text 
order-service/test-null-product.http
```

Sends an order with a null product. Bean Validation should reject the request with 400 Bad Request. No order should be saved to PostgreSQL or published to Kafka.

### Idempotency Test

The `inventory-service` uses the `processed_messages` table to prevent the same `orderId` from being processed more than once.

Create an order. Copy the generated orderId from the response.

For example:

```text
orderId: abc-123
```

The inventory-service should process the event normally:

```text
==============================
Processing order:
OrderId: abc-123
Product: Laptop
Quantity: 2
==============================
```

The order should be recorded in the `processed_messages` table.

Publish the same `orderId` to the `order-created` topic.

The event should contain the same `orderId`:

```json
{
"orderId": "abc-123",
"customerId": 15,
"product": "Laptop",
"quantity": 2
}
```

Send the same event multiple times.

The first event should be processed:

```text
==============================
Processing order:
OrderId: abc-123
Product: Laptop
Quantity: 2
==============================
```

The duplicate event should be ignored:

```text
==============================
Duplicate order ignored: abc-123
==============================
```

The order should not be processed again.

Verify the database. 

Run:

```sql
SELECT *
FROM processed_messages
WHERE order_id = 'abc-123';
```

Only one record should exist for the orderId.

### Dead Letter Topic Test

To test the DLT, the message must fail after all configured retry attempts.

After all retries are exhausted, the message should be published to:

```text
order-created.DLT
```

The `dlt-monitor` service consumes this topic.

Expected output:

```text
==============================
FAILED MESSAGE FROM DLT
==============================
OrderId: abc-123
CustomerId: 15
Product: Laptop
Quantity: 2
==============================
```

After processing an order, check PostgreSQL.

The failed message must not be stored in the `processed_messages` table because the order was never successfully processed.

Run:

```sql
SELECT *
FROM processed_messages
WHERE order_id = 'abc-123';
```

Expected result:

```text
No rows
```

The order should still exist in the `orders` table because it was successfully saved by the `order-service` before the Kafka event was published.

However, its status should remain:

```text
NEW
```

Run:

```sql
SELECT *
FROM orders
WHERE id = 'abc-123';
```

Expected result:


|   Id    | customer_id | product  | quantity | status | created_at |
|:-------:|:-----------:|:--------:|:--------:|:------:|:----------:|
| abc-123 |      15     |  Laptop  |    2     |  NEW   |     ...    |

---

## Processing Sequence

1. Client sends `POST /orders`.
2. Order Service validates the request.
3. Order Service generates a UUID `orderId` and `createdTime`.
4. Order Service creates the order with status `NEW`.
5. Order Service saves the order to PostgreSQL.
6. After a successful database save, Order Service publishes the `order-created` event to Kafka.
7. Both Inventory Service and Notification Service consume the `order-created` event independently.
8. Inventory Service checks whether the event has already been processed:
   - Already processed → the duplicate event is ignored.
   - Not processed → the order is processed.
9. Inventory Service checks the quantity:
   - `quantity <= 5` → `AVAILABLE`
   - `quantity > 5` → `OUT_OF_STOCK`
10. Inventory Service saves the processed message for idempotency.
11. Inventory Service publishes the `inventory-result` event to Kafka.
12. Notification Service processes the order event and simulates sending an email to the customer.
13. Order Service consumes the `inventory-result` event.
14. Order Service finds the order by `orderId`.
15. Order Service updates the order status in PostgreSQL.
16. If Inventory Service processing fails, Kafka retries the message according to the configured retry/backoff policy.
17. If processing still fails after all retries, the message is published to `order-created.DLT`.
18. DLT Monitor consumes the failed message from `order-created.DLT` and prints it for monitoring.