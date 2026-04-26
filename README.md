# Smart Campus API
### 5COSC022W - Client Server Architectures
### Student: Rasayan

---

## Overview
The Smart Campus API is a RESTful web service built using JAX-RS (Jersey) 
and an embedded Grizzly HTTP server. It manages Rooms and Sensors across 
a university campus, providing endpoints for creating, retrieving, and 
deleting rooms and sensors, as well as logging sensor readings.

---

## Technology Stack
- Java 11
- JAX-RS (Jersey 2.41)
- Grizzly HTTP Server (Embedded)
- Maven
- In-memory storage (HashMap/ArrayList)

---

## How to Build and Run

### Prerequisites
- Java 11 or higher
- Maven 3.6+

### Steps
1. Clone the repository:
git clone https://github.com/R1sayan/SmartCampusAPI.git
2. Navigate to project folder:
cd SmartCampusAPI
3. Build the project:
mvn clean install
4. Run the server:
mvn exec:java -Dexec.mainClass="com.smartcampus.Main"
5. API will be running at:
http://localhost:8080/api/v1

---

## Sample curl Commands

### 1. Get API Info
curl -X GET http://localhost:8080/api/v1

### 2. Create a Room
curl -X POST http://localhost:8080/api/v1/rooms 
-H "Content-Type: application/json" 
-d '{"id":"LIB-301","name":"Library Quiet Study","capacity":50}'

### 3. Get All Rooms
curl -X GET http://localhost:8080/api/v1/rooms

### 4. Create a Sensor
curl -X POST http://localhost:8080/api/v1/sensors 
-H "Content-Type: application/json" 
-d '{"id":"TEMP-001","type":"Temperature","status":"ACTIVE","currentValue":22.5,"roomId":"LIB-301"}'

### 5. Get All Sensors filtered by type
curl -X GET http://localhost:8080/api/v1/sensors?type=Temperature

### 6. Add a Sensor Reading
curl -X POST http://localhost:8080/api/v1/sensors/TEMP-001/readings 
-H "Content-Type: application/json" 
-d '{"value":23.5}'

### 7. Get All Readings for a Sensor
curl -X GET http://localhost:8080/api/v1/sensors/TEMP-001/readings

### 8. Delete a Room
curl -X DELETE http://localhost:8080/api/v1/rooms/LIB-301

---

## Report - Questions and Answers

### Part 1.1 - JAX-RS Resource Lifecycle
By default, JAX-RS creates a new instance of a Resource class for every 
incoming HTTP request. This is known as per-request lifecycle. This means 
each request gets its own fresh object, which avoids issues with shared 
state between requests. However, since we are using in-memory data structures 
(HashMaps) to store our data, these must be declared as static so they are 
shared across all instances. Without static, each new instance would have its 
own empty map and data would be lost between requests. To prevent race 
conditions in a multi-threaded environment, we could use ConcurrentHashMap 
instead of HashMap for thread safety.

### Part 1.2 - HATEOAS
HATEOAS (Hypermedia as the Engine of Application State) means that API 
responses include links to related resources and actions. For example, our 
discovery endpoint returns links to /api/v1/rooms and /api/v1/sensors. This 
benefits client developers because they do not need to hardcode URLs or rely 
on static documentation. Instead, they can navigate the API dynamically by 
following the links provided in responses, making the API self-discoverable 
and easier to use.

### Part 2.1 - IDs vs Full Objects in List Responses
Returning only IDs in a list response reduces network bandwidth as less data 
is transferred. However, the client must make additional requests to fetch 
details for each ID, increasing the number of API calls. Returning full 
objects provides all data in one response, reducing API calls but increasing 
payload size. For large collections, returning full objects can be costly. 
The best approach depends on the use case - for dashboards showing summaries, 
full objects are better, while for large datasets, IDs with pagination is 
more efficient.

### Part 2.2 - Is DELETE Idempotent?
Yes, DELETE is idempotent in our implementation. The first DELETE request 
on a room that exists will successfully delete it and return 200 OK. Any 
subsequent DELETE requests for the same room ID will return 404 Not Found 
because the room no longer exists. The end result is the same - the room 
does not exist in the system. This is consistent with REST principles where 
idempotency means multiple identical requests have the same effect as a 
single request.

### Part 3.1 - Wrong @Consumes Format
The @Consumes(MediaType.APPLICATION_JSON) annotation tells JAX-RS that the 
endpoint only accepts JSON data. If a client sends data in a different format 
such as text/plain or application/xml, JAX-RS will automatically return an 
HTTP 415 Unsupported Media Type error. The request will be rejected before 
it even reaches our method, protecting the API from unexpected data formats 
and ensuring data consistency.

### Part 3.2 - Query Param vs Path Param for Filtering
Using @QueryParam for filtering (e.g. /api/v1/sensors?type=CO2) is better 
than path parameters (e.g. /api/v1/sensors/type/CO2) because query parameters 
are optional by nature, making them perfect for filtering. With path parameters, 
the filter becomes part of the resource identity which is semantically incorrect 
since we are still accessing the sensors collection, just filtering it. Query 
parameters also allow multiple filters to be combined easily (e.g. ?type=CO2&status=ACTIVE) 
without changing the URL structure.

### Part 4.1 - Sub-Resource Locator Pattern Benefits
The Sub-Resource Locator pattern delegates handling of nested paths to 
separate dedicated classes. Instead of defining all endpoints in one massive 
class, we separate concerns - RoomResource handles rooms, SensorResource 
handles sensors, and SensorReadingResource handles readings. This makes the 
code more maintainable, readable and testable. Each class has a single 
responsibility, making it easier to modify or extend individual resources 
without affecting others. It also mirrors the logical hierarchy of the data 
model.

### Part 5.2 - Why 422 over 404
HTTP 404 means the requested resource was not found, which would imply the 
sensor endpoint itself does not exist. HTTP 422 Unprocessable Entity is more 
semantically accurate because the request was syntactically correct JSON, 
but the content was invalid - specifically the roomId referenced does not 
exist. The server understood the request but could not process it due to a 
validation failure. Using 422 gives clients clearer information about what 
went wrong and how to fix it.

### Part 5.4 - Security Risks of Exposing Stack Traces
Exposing Java stack traces to external API consumers is dangerous because 
they reveal internal implementation details such as class names, method names, 
line numbers, and library versions. An attacker can use this information to 
identify known vulnerabilities in specific library versions, understand the 
application architecture, craft targeted attacks, and identify potential 
injection points. Our GlobalExceptionMapper prevents this by catching all 
unexpected errors and returning only a generic 500 error message to clients 
while logging the full details internally.

### Part 5.5 - Why Filters over Manual Logging
Using JAX-RS filters for cross-cutting concerns like logging is better than 
manually inserting Logger.info() statements in every resource method because 
filters follow the DRY (Don't Repeat Yourself) principle. With filters, 
logging logic is written once and applied automatically to every request and 
response. Manual logging would require adding the same code to every method, 
making the code repetitive and harder to maintain. If logging requirements 
change, only the filter needs to be updated rather than every single method.

---

## API Endpoints Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/v1 | API Discovery |
| GET | /api/v1/rooms | Get all rooms |
| POST | /api/v1/rooms | Create a room |
| GET | /api/v1/rooms/{roomId} | Get a room |
| DELETE | /api/v1/rooms/{roomId} | Delete a room |
| GET | /api/v1/sensors | Get all sensors |
| POST | /api/v1/sensors | Create a sensor |
| GET | /api/v1/sensors/{sensorId} | Get a sensor |
| GET | /api/v1/sensors/{sensorId}/readings | Get readings |
| POST | /api/v1/sensors/{sensorId}/readings | Add a reading |