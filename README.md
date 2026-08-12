# Spring Boot Kafka Microservices Demo

Event-driven microservices example using:

- Java 21
- Spring Boot
- Apache Kafka 4.3.1 (KRaft mode)
- Gradle Wrapper
- IntelliJ IDEA

## Project Structure

```
microservices/
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
│   │   │   │       ├── producer/
│   │   │   │       │   └── InventoryProducer.java
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

# PostgreSQL Local Development

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

### Checking the orders Table
After connecting to the database, run the following query to view the contents of the orders table:

```cmd
SELECT id, customer_id, product, quantity, status, created_at
FROM orders;
```

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
                └── inventory-service
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

This starts both Spring Boot applications automatically.

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
- Publishes events to Kafka
- Consumes inventory-result
- Updates order status in DB

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

## Processing Sequence

1. Client sends `POST /orders`.
2. Order Service validates the request.
3. Order Service creates the order with status `NEW`.
4. Order is saved to PostgreSQL.
5. After a successful save, Order Service publishes the order event to Kafka.
6. Inventory Service consumes the order event.
7. Inventory Service checks the quantity:
    - `quantity <= 5` → `AVAILABLE`
    - `quantity > 5` → `OUT_OF_STOCK`
8. Inventory Service publishes the inventory-result event.
9. Order Service consumes inventory-result.
10. Order Service finds the order by `orderId`.
11. Order Service updates the status in PostgreSQL.