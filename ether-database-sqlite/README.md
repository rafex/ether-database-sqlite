# Ether Database SQLite

[![Maven Central](https://img.shields.io/maven-central/v/dev.rafex.ether.database/ether-database-sqlite)](https://central.sonatype.com/artifact/dev.rafex.ether.database/ether-database-sqlite)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java 25+](https://img.shields.io/badge/Java-25%2B-blue)](https://openjdk.org/projects/jdk/25/)

SQLite-specific JDBC helpers for the Ether database stack. Includes Write-Ahead Logging (WAL) support and SQLite-specific parameter types and error handling.

## Features

- **SQLite Error Classification**: Map SQLite numeric error codes to meaningful exceptions
- **SQLite-specific Parameters**: Type-safe parameter binding for SQLite data types (BLOB, JSON, INTEGER, REAL, TEXT)
- **Write-Ahead Logging (WAL) Support**: Configuration and management of SQLite WAL mode
- **Connection Configuration**: Builder pattern for SQLite connection configuration
- **Compatible with `ether-database-core`**: Implements the core database abstractions

## Installation

### Maven

```xml
<dependency>
    <groupId>dev.rafex.ether.database</groupId>
    <artifactId>ether-database-sqlite</artifactId>
    <version>9.5.5-SNAPSHOT</version>
</dependency>
```

### Gradle

```groovy
implementation 'dev.rafex.ether.database:ether-database-sqlite:9.5.5-SNAPSHOT'
```

## Quick Start

### Basic Usage

```java
import dev.rafex.ether.database.sqlite.errors.SQLiteErrorClassifier;
import dev.rafex.ether.database.sqlite.sql.SQLiteParameters;
import dev.rafex.ether.database.sqlite.config.SQLiteConfig;
import dev.rafex.ether.database.sqlite.config.JournalMode;
import dev.rafex.ether.database.sqlite.config.SynchronousMode;

// Create a SQLite configuration
SQLiteConfig config = SQLiteConfig.builder()
    .journalMode(JournalMode.WAL)
    .synchronousMode(SynchronousMode.NORMAL)
    .cacheSize(2000) // 2MB cache
    .foreignKeys(true)
    .build();

// Apply configuration to a connection
Connection connection = DriverManager.getConnection("jdbc:sqlite:test.db");
SQLitePragmas.apply(config, connection);

// Use SQLite-specific parameters
PreparedStatement stmt = connection.prepareStatement(
    "INSERT INTO users (id, name, data) VALUES (?, ?, ?)"
);
stmt.setObject(1, SQLiteParameters.integer(123));
stmt.setObject(2, SQLiteParameters.text("John Doe"));
stmt.setObject(3, SQLiteParameters.json("{\"age\": 30, \"active\": true}"));

// Classify SQLite errors
try {
    stmt.executeUpdate();
} catch (SQLException e) {
    RuntimeException classified = SQLiteErrorClassifier.classify(e);
    // classified will be DataIntegrityViolationException for UNIQUE violations, etc.
}
```

### Error Classification

SQLite uses numeric error codes instead of SQL states. This module provides classification for common SQLite errors:

```java
import dev.rafex.ether.database.sqlite.errors.SQLiteErrorCodes;

// Common error codes
int UNIQUE_VIOLATION = SQLiteErrorCodes.UNIQUE_VIOLATION; // 2067
int BUSY = SQLiteErrorCodes.BUSY; // 5
int LOCKED = SQLiteErrorCodes.LOCKED; // 6

// Classify exceptions
try {
    // database operation
} catch (SQLException e) {
    RuntimeException classified = SQLiteErrorClassifier.classify(e);
    // Returns appropriate RuntimeException subclass
}
```

### Parameter Types

SQLite has specific data types that differ from standard JDBC:

```java
import dev.rafex.ether.database.sqlite.sql.SQLiteParameters;

// Basic types
SQLiteParameters.integer(123);      // INTEGER
SQLiteParameters.real(3.14159);     // REAL
SQLiteParameters.text("Hello");     // TEXT
SQLiteParameters.blob(bytes);       // BLOB

// JSON support (requires JSON1 extension)
SQLiteParameters.json("{\"key\": \"value\"}");

// Null values with type hints
SQLiteParameters.nullInteger();
SQLiteParameters.nullReal();
SQLiteParameters.nullText();
SQLiteParameters.nullBlob();
SQLiteParameters.nullJson();
```

### WAL Configuration

Write-Ahead Logging improves concurrency and performance:

```java
import dev.rafex.ether.database.sqlite.config.*;

// Configure WAL mode
SQLiteConfig config = SQLiteConfig.builder()
    .journalMode(JournalMode.WAL)
    .synchronousMode(SynchronousMode.NORMAL)  // Balance safety vs performance
    .walAutoCheckpoint(1000)                  // Checkpoint every 1000 pages
    .cacheSize(-2000)                         // 2MB cache (negative = KB)
    .build();

// Apply to connection
Connection conn = DriverManager.getConnection("jdbc:sqlite:app.db");
SQLitePragmas.apply(config, conn);

// Check current journal mode
JournalMode current = SQLitePragmas.getJournalMode(conn);
```

## Configuration Options

| Option | Type | Default | Description |
|--------|------|---------|-------------|
| `journalMode` | `JournalMode` | `DELETE` | Journal mode (WAL, DELETE, TRUNCATE, etc.) |
| `synchronousMode` | `SynchronousMode` | `FULL` | Synchronous mode (OFF, NORMAL, FULL, EXTRA) |
| `foreignKeys` | `boolean` | `true` | Enable foreign key constraints |
| `cacheSize` | `int` | `-2000` | Cache size in KB (negative values) |
| `walAutoCheckpoint` | `int` | `1000` | WAL auto-checkpoint threshold |
| `busyTimeout` | `int` | `5000` | Busy timeout in milliseconds |
| `tempStore` | `int` | `0` | Temp store location (0=default, 1=file, 2=memory) |

## Journal Modes

- **`WAL`**: Write-Ahead Logging - allows concurrent reads/writes
- **`DELETE`**: Default - journal file is deleted after transaction
- **`TRUNCATE`**: Journal file is truncated to zero bytes
- **`PERSIST`**: Journal file is zeroed but not deleted
- **`MEMORY`**: Journal stored in memory (volatile)
- **`OFF`**: No journal (dangerous, can corrupt database)

## Synchronous Modes

- **`OFF`**: No sync - fastest but can lose data on power loss
- **`NORMAL`**: Sync at critical moments - good balance
- **`FULL`**: Sync after every write - safest (default)
- **`EXTRA`**: Extra sync - similar to FULL with additional guarantees

## Building from Source

```bash
# Clone the repository
git clone https://github.com/rafex/ether-database-sqlite.git
cd ether-database-sqlite

# Build with Maven
mvn clean install

# Run tests
mvn test
```

## Dependencies

- Java 25 or higher
- `ether-database-core` (provided)
- SQLite JDBC driver (runtime dependency, not included)

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing

Please read [CONTRIBUTING.md](CONTRIBUTING.md) for details on our code of conduct and the process for submitting pull requests.

## Support

- [GitHub Issues](https://github.com/rafex/ether-database-sqlite/issues)
- [Documentation](https://rafex.dev/ether/database/sqlite)