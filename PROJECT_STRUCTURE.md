# Kafka Streams Multi-Module Project - Structure Overview

## Project Structure

```
kafka-streams/
├── pom.xml                                 # Parent POM (multi-module aggregator)
├── README.md                               # Main project documentation
├── CONTRIBUTING.md                         # Contribution guidelines
├── LICENSE                                 # Apache 2.0 License
├── .gitignore                              # Git ignore rules
│
├── kafka-streams-template/                 # Module 1: Reusable Kafka Streams template
│   ├── pom.xml                             # Template module POM
│   ├── README.md                           # Template-specific documentation
│   └── src/
│       ├── main/
│       │   ├── java/io/kcmhub/
│       │   │   ├── KafkaStreamsApplication.java
│       │   │   └── streams/
│       │   │       ├── KafkaStreamsStarter.java
│       │   │       ├── StreamsTopologyConfig.java
│       │   │       └── UppercaseTopologyBuilder.java
│       │   └── resources/
│       │       └── application.yml
│       └── test/
│
├── kafka-broker-tooling/                   # Tooling folder (NOT a Maven module)
│   ├── README.md                           # Tooling documentation
│   ├── docker-compose.yml                  # Docker Compose stack (Kafka, KCM, PostgreSQL, Redis)
│   ├── DOCKER.md                           # Docker setup guide
│   ├── Makefile                            # Make commands (Linux/macOS)
│   ├── docker.ps1                          # PowerShell script (Windows)
│   └── .env.example                        # Environment variables example
│
└── kafka-streams-archetype/                # Module 2: Maven archetype
    ├── pom.xml                             # Archetype module POM
    ├── README.md                           # Archetype-specific documentation
    └── src/
        └── main/
            └── resources/
                ├── META-INF/maven/
                │   └── archetype-metadata.xml
                └── archetype-resources/
                    ├── pom.xml             # Template POM for generated projects
                    └── src/
                        └── main/
                            ├── java/
                            │   ├── KafkaStreamsApplication.java
                            │   └── streams/
                            │       ├── KafkaStreamsStarter.java
                            │       ├── StreamsTopologyConfig.java
                            │       └── UppercaseTopologyBuilder.java
                            └── resources/
                                └── application.yml
```

## Module Descriptions

### 1. kafka-streams-template
A complete, production-ready Kafka Streams application template with:
- Spring Boot 3.3 integration
- Java 21 support
- Example uppercase transformation topology
- Actuator and Prometheus metrics
- Environment variable configuration support

**Use case**: Clone and customize for your own Kafka Streams applications.

### 2. kafka-broker-tooling (Tooling Folder - NOT a Maven Module)
Docker Compose stack and scripts for local Kafka development environment:
- **Kafka Broker** (port 9092) - KRaft mode (no Zookeeper required)
- **KCM UI** - Web interface for Kafka management (port 3000)
- **KCM API** - Backend for Kafka management (port 8080)
- **PostgreSQL** - Database for KCM (port 5432)
- **Redis** - Cache for KCM (port 6379)
- Cross-platform scripts (PowerShell for Windows, Makefile for Linux/macOS)

**Use case**: Quick setup of a local Kafka development environment with KCM (Kafka Cluster Manager).  
**Note**: This is NOT a Maven module - it contains only Docker Compose files and scripts (no Java code).

### 3. kafka-streams-archetype
Maven archetype for generating new Kafka Streams projects:
- Generates complete project structure
- Customizable package names
- Based on kafka-streams-template

**Use case**: Quickly bootstrap new Kafka Streams applications using `mvn archetype:generate`.

## Build Instructions

### Build all modules
```bash
mvn clean install
```

### Build specific module
```bash
cd kafka-streams-template
mvn clean install
```

### Build order (reactor)
Maven automatically builds modules in the correct order based on dependencies:
1. kafka-streams-parent (POM)
2. kafka-streams-template
3. kafka-streams-archetype

**Note**: `kafka-broker-tooling` is not part of the Maven build as it's a tooling folder (no pom.xml).

## Version Management

All modules share the same version defined in the parent POM:
- **Current version**: 1.0-SNAPSHOT
- **Java version**: 21
- **Spring Boot version**: 3.3.6
- **Kafka version**: 3.9.0

To update versions, modify the parent `pom.xml` properties section.

## Development Workflow

### Adding a new module
1. Create module directory under project root
2. Create module `pom.xml` with parent reference
3. Add module to parent POM's `<modules>` section
4. Build project: `mvn clean install`

### Working with the archetype
1. Build and install archetype: `cd kafka-streams-archetype && mvn clean install`
2. Generate new project: Use `mvn archetype:generate` with archetype coordinates
3. Customize generated project as needed

### Testing changes across modules
```bash
# Clean and rebuild everything
mvn clean install

# Run specific module
cd kafka-streams-template
mvn spring-boot:run
```

## Dependencies Between Modules

```
kafka-streams-parent (POM)
    ├── kafka-streams-template (JAR)
    └── kafka-streams-archetype (maven-archetype)

kafka-broker-tooling/  (Tooling folder - NOT a Maven module)
```

**Note**: 
- Maven modules are currently independent (no inter-module dependencies).
- `kafka-broker-tooling` is a standalone tooling folder and is not part of the Maven reactor build.

## IDE Setup

### IntelliJ IDEA
1. Open the root `pom.xml` as a project
2. IntelliJ will automatically detect all modules
3. Enable annotation processing if needed
4. Set Java SDK to 21

### VS Code
1. Open the root folder
2. Install Java Extension Pack
3. Maven will auto-detect the multi-module structure

### Eclipse
1. Import as "Existing Maven Project"
2. Select the root directory
3. Eclipse will import all modules

## CI/CD Considerations

### GitHub Actions example
```yaml
name: Build
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
      - run: mvn clean verify
```

### Docker multi-stage build
Each module can have its own Dockerfile, or use a multi-stage build referencing specific modules.

## License

All modules are licensed under Apache License 2.0. See LICENSE file.

## Contributing

See CONTRIBUTING.md for guidelines on contributing to any of the modules.

