# DRS-Initial — Disaster Response System

A JavaFX desktop application for managing disaster reports, coordinating responses, and tracking resources.

## Requirements

- Java 22+
- Apache Maven 3.8+

## How to Run

```bash
mvn javafx:run
```

## How to Build

```bash
mvn compile
```

## How to Run Tests

```bash
# All tests
mvn test

# Single test class
mvn test -Dtest=DisasterReportTest

# Single test method
mvn test -Dtest=DisasterReportTest#testComputePriorityHurricaneCritical
```
