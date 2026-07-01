# USSD Event Processor

A high-performance Spring Boot service designed to ingest, parse, and store Call Detail Records (CDRs) from pipe-delimited log files into a PostgreSQL database.

## Key Features

- **Automated File Polling**: Periodically monitors a configured directory for new CDR files.
- **Parallel Processing**: Utilizes a `ThreadPoolTaskExecutor` to process multiple files concurrently, maximizing throughput.
- **Efficient Ingestion**:
    - **Batch Processing**: Records are saved in batches (default: 1000) to minimize database round-trips.
    - **Partial Commits**: Uses `Propagation.REQUIRES_NEW` for batch saves, ensuring that large files don't lock the database in a single long-running transaction and allowing partial progress if an error occurs.
- **Robust Parsing**: Maps 33 distinct CDR fields using a dedicated `CdrMapper`, handling various data types and legacy timestamp formats.
- **Duplicate Prevention**:
    - **Application-level check**: Verifies if a file has already been processed by checking the `cdr_log` table.
    - **Database-level integrity**: Enforces a unique constraint on the filename in the database.
- **Automatic Archiving**: Moves successfully processed files to a dedicated folder to prevent re-ingestion.
- **Audit Logging**: Maintains a detailed history of file processing in the `cdr_log` table, including success/failure counts and timestamps.
- **Database Migrations**: Uses Flyway for version-controlled schema management.

## Architecture

The application is built with:
- **Java 21** & **Spring Boot 3**
- **Spring Data JPA**: For database interactions.
- **Hibernate**: For ORM and schema validation.
- **Flyway**: For database migrations.
- **Lombok**: To reduce boilerplate code.
- **PostgreSQL**: Production database (H2 used for tests).

### Core Components

- **`FileWatcherService`**: The orchestrator. It polls the watch folder, initiates asynchronous file processing tasks, and manages file movement.
- **`CdrMapper`**: Responsible for splitting pipe-delimited strings and converting them into `CallDetailRecord` entities.
- **`UssdEventProcessorApplication`**: Configures the asynchronous thread pool and task executor.

## Configuration

Key settings in `src/main/resources/application.properties`:

```properties
# Folder Configuration
cdr.watch-folder=data/ussd/incoming
cdr.processed-folder=data/ussd/processed
cdr.poll-rate-ms=60000

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/ussd_db
spring.datasource.username=postgres
spring.datasource.password=password

# Hibernate & Flyway
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=true
```

## Database Schema

### `call_detail_records`
Stores the actual CDR data. Primary key is a `UUID`. Includes fields like:
- `event_timestamp`
- `msisdn`, `imsi`, `destination_msisdn`
- `ussd_string`
- `duration_ms`, `bytes_sent`, `bytes_received`
- ... (Total 33 fields)

### `cdr_log`
Tracks file processing history.
- `file_name` (Unique)
- `upload_start_time`, `upload_end_time`
- `records_loaded` (Success count)
- `records_failed` (Failure count)

## Getting Started

### Prerequisites
- JDK 21
- Maven 3.x
- PostgreSQL instance

### Running the Application
1. Configure your database credentials in `application.properties`.
2. Ensure the `cdr.watch-folder` exists on your filesystem.
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## Testing

The project includes a comprehensive test suite:
- **Unit Tests**: `CdrMapperTest` verifies parsing logic.
- **Service Tests**: `FileWatcherServiceTest` mocks dependencies to test business logic.
- **Integration Tests**: `FileProcessingIntegrationTest` uses an H2 in-memory database and temporary folders to simulate end-to-end file ingestion.

Run all tests:
```bash
mvn test
```