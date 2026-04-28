
# SmartCampus API

This project implements the `5COSC022W` coursework brief as a Tomcat-deployable JAX-RS REST API using Jersey and in-memory Java collections only. It covers the required SmartCampus scenario with rooms, sensors, nested sensor readings, filtered retrieval, and custom exception mappers.

## What It Includes

- `GET /api/v1/` discovery endpoint with API metadata and collection links
- Room management:
  - `GET /api/v1/rooms`
  - `POST /api/v1/rooms`
  - `GET /api/v1/rooms/{roomId}`
  - `DELETE /api/v1/rooms/{roomId}`
- Sensor management:
  - `GET /api/v1/sensors`
  - `GET /api/v1/sensors?status=ACTIVE`
  - `POST /api/v1/sensors`
  - `GET /api/v1/sensors/{sensorId}`
  - `DELETE /api/v1/sensors/{sensorId}`
- Nested sensor history:
  - `GET /api/v1/sensors/{sensorId}/readings`
  - `POST /api/v1/sensors/{sensorId}/readings`
- Business rules required by the coursework:
  - A room cannot be deleted while sensors still belong to it
  - A sensor cannot be created if its `roomId` does not exist
  - A sensor in `MAINTENANCE` cannot accept new readings
  - Posting a reading updates the parent sensor's `latestReading`
- Error handling with JSON responses for:
  - `409 Conflict`
  - `422 Unprocessable Entity`
  - `403 Forbidden`
  - `404 Not Found`
  - `500 Internal Server Error`

## Project Structure

```text
src/main/java/uk/ac/westminster/smartcampus/api
|-- SmartCampusApplication.java
|-- exception
|-- model
|-- resource
`-- service
```

## Build

Requirements:

- Java 17+
- Maven 3.9+

Build the WAR:

```bash
mvn clean package
```

Expected output:

```text
target/smart-campus-api.war
```

## Run On Tomcat 9

This project is designed for Tomcat 9, which matches the course materials in `WEEK_07/apache-tomcat-9.0.100`.

1. Build the WAR with `mvn clean package`
2. Copy `target/smart-campus-api.war` into your Tomcat `webapps` folder
3. Start Tomcat
4. Open:

```text
http://localhost:8080/smart-campus-api/api/v1/
```

If you rename the WAR to `ROOT.war`, the base URL becomes:

```text
http://localhost:8080/api/v1/
```

## Sample curl Commands

Discover the API:

```bash
curl http://localhost:8080/smart-campus-api/api/v1/
```

List rooms:

```bash
curl http://localhost:8080/smart-campus-api/api/v1/rooms
```

Create a room:

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"room-c301\",\"name\":\"AI Innovation Studio\",\"building\":\"Cavendish\",\"floor\":\"3\",\"capacity\":32}"
```

Create a sensor:

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"sensor-temp-01\",\"name\":\"Temperature Sensor 01\",\"type\":\"TEMPERATURE\",\"status\":\"ACTIVE\",\"roomId\":\"room-c301\"}"
```

Filter sensors by status:

```bash
curl "http://localhost:8080/smart-campus-api/api/v1/sensors?status=ACTIVE"
```

Append a sensor reading:

```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/sensor-temp-01/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":22.7,\"unit\":\"C\",\"notes\":\"Manual test reading\"}"
```

Read a sensor's history:

```bash
curl http://localhost:8080/smart-campus-api/api/v1/sensors/sensor-temp-01/readings
```

Trigger the deletion safety rule:

```bash
curl -X DELETE http://localhost:8080/smart-campus-api/api/v1/rooms/room-c301
```

## Coursework Report

Part 1: Service Architecture and Setup

1. Project and Application Configuration

In my project I configured the API in `SmartCampusApplication.java` using `@ApplicationPath("/api/v1")`.

I also registered the resource package and exception package and enabled Jackson so the API can handle JSON request/response bodies.

I learned that JAX-RS resource classes are created per request by default, so I did not keep shared data inside resource classes.
Instead, I used `SmartCampusStore` as a central in-memory store.

Inside `SmartCampusStore`, I used:
- `ConcurrentHashMap` for rooms, sensors, and readings
- `CopyOnWriteArrayList` for sensor reading history
- synchronized blocks (`mutationLock`) for updates that should happen together

This helped keep data safe when multiple requests come at the same time.

2. Discovery Endpoint

I implemented a discovery endpoint at `GET /api/v1` in `DiscoveryResource`.
It returns details like API name, version, contact and main links such as:
- `/api/v1/rooms`
- `/api/v1/sensors`
- `/api/v1/sensors/{sensorId}/readings`

This makes it easier for API users to understand available endpoints without only depending on external documentation.

Part 2: Room Management

1. Room Resource Implementation

In `RoomResource` I implemented:
- `GET /rooms`
- `POST /rooms`
- `GET /rooms/{roomId}`
- `DELETE /rooms/{roomId}`

For `GET /rooms` I return full room objects.

I chose this because it makes testing and frontend/client usage easier in this coursework scenario.

2. Room Deletion and Safety Logic (DELETE Idempotency)

When deleting a room my logic first checks whether the room still has sensors assigned.
- If sensors are still linked, it throws `RoomConflictException` and returns `409 Conflict`.
- If the room does not exist, it returns `404 Not Found`.
- If deletion is successful once, repeated same delete requests do not change the final state further.

So the behavior follows idempotent delete logic in terms of final state.

Part 3: Sensor Management

1. Sensor Resource and Input Validation

In `SensorResource`, I used JSON consumption and validation for sensor creation.

For `POST /sensors`, the request must include:
- `name`
- `type`
- `roomId`

If important fields are missing the API returns `400 Bad Request`.
If the provided `roomId` does not exist, I throw `InvalidSensorDependencyException`.

2. Filtering Sensors

I added query filtering on sensors with:
- `GET /sensors?status=ACTIVE`

I used query parameters for this because it is cleaner and easier to extend than creating many different path patterns.

Part 4: Sensor Readings

1. Sub-resource for Readings

To keep the code organized, I used a sub-resource approach:
- `SensorResource` handles sensor-level operations
- `SensorReadingResource` handles `/sensors/{sensorId}/readings`

This separation made the code easier to read and maintain.

2. Reading History and Latest Value Update

When a new reading is posted:
1. It gets added to that sensor’s reading history list.
2. The sensor’s latest reading is updated using `sensor.setLatestReading(...)`.

So both historical data and the latest value are maintained consistently.

Part 5: Error Handling and Exception Mapping

1. Room Conflict

If someone tries to delete a room that still has sensors, `RoomConflictExceptionMapper` returns `409 Conflict` with a structured JSON error.

2. Invalid Dependency (422)

If a sensor is created with a room that does not exist, `InvalidSensorDependencyExceptionMapper` returns `422 Unprocessable Entity`.

I used 422 because the endpoint is valid, but the payload references invalid related data.

3. Sensor in Maintenance (403)

If the sensor status is `MAINTENANCE`, posting a new reading is blocked.
And `SensorMaintenanceExceptionMapper` returns `403 Forbidden` for this case.

4. Not Found and Global Error Handling

- `NotFoundExceptionMapper` handles `404` cases.
- `WebApplicationExceptionMapper` handles standard web exceptions with relevant status.
- `GlobalExceptionMapper` handles unexpected errors and returns a safe generic `500` message.

This avoids exposing internal details to the client.

5. Standard Error Response Structure

I used `AbstractApiExceptionMapper` as a common base to keep the error response format consistent (timestamp, status, reason, message and path).

Conclusion

Overall I have implemented a working Smart Campus REST API with room/sensor/reading management, nested resource, validation and proper error handling.
I focused on keeping the design clean, thread-safe in-memory storage and clear HTTP status behavior so the API is reliable and easier to test.

## Notes

- The service uses only in-memory collections such as `ConcurrentHashMap` and `CopyOnWriteArrayList`, which follows the coursework restriction against databases.
- The project uses JAX-RS with Jersey and is intended for external deployment on Tomcat, not Spring Boot.
- Seed data is included so the API has working rooms, sensors, and readings immediately after startup.


