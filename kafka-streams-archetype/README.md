# Kafka Streams Archetype Module

Maven archetype for quickly generating new Kafka Streams projects based on the kafka-streams-template module.

## Features
- Generates a complete Kafka Streams project structure
- Customizable group ID, artifact ID, and package name
- Based on Spring Boot 3.3 and Kafka Streams 3.9
- Includes all necessary dependencies and configuration

## Prerequisites
- Maven 3.x installed
- Java 21 (JDK 21) for the generated projects

## Installation

First, build and install the archetype to your local Maven repository:

```bash
cd kafka-streams-archetype
mvn clean install
```

## Usage

### Generate a new project

After installing the archetype, you can generate a new Kafka Streams project:

##### Linux/macOS (Bash)
```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.example \
  -DartifactId=my-kafka-streams-app \
  -Dversion=1.0-SNAPSHOT \
  -Dpackage=com.example.streams \
  -DtopologyBuilderClass=MyTopologyBuilder
```

##### Windows (PowerShell)
```powershell
mvn archetype:generate `
  -DarchetypeGroupId=io.kcmhub `
  -DarchetypeArtifactId=kafka-streams-archetype `
  -DarchetypeVersion=1.0-SNAPSHOT `
  -DgroupId=com.example `
  -DartifactId=my-kafka-streams-app `
  -Dversion=1.0-SNAPSHOT `
  -Dpackage=com.example.streams `
  -DtopologyBuilderClass=MyTopologyBuilder
```

### Customization Properties

| Property | Description | Default Value |
|----------|-------------|---------------|
| `groupId` | Maven group ID | `com.example` |
| `artifactId` | Maven artifact ID | `kafka-streams-app` |
| `version` | Project version | `1.0-SNAPSHOT` |
| `package` | Base Java package | `com.example.streams` |
| `topologyBuilderClass` | Name of the topology builder class | `UppercaseTopologyBuilder` |

### Interactive mode

You can also use Maven in interactive mode to be prompted for the values:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT
```

Maven will ask you for:
- `groupId` - Your project's group ID (e.g., `com.example`)
- `artifactId` - Your project's artifact ID (e.g., `my-kafka-streams-app`)
- `version` - Your project's version (e.g., `1.0-SNAPSHOT`)
- `package` - Your project's base package (e.g., `com.example.streams`)
- `topologyBuilderClass` - Name of your topology builder class (e.g., `MyTopologyBuilder`)

## Generated project structure

The archetype will generate a project with the following structure:

```
my-kafka-streams-app/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/streams/
│   │   │       ├── KafkaStreamsApplication.java
│   │   │       └── streams/
│   │   │           ├── KafkaStreamsStarter.java
│   │   │           ├── StreamsTopologyConfig.java
│   │   │           └── MyTopologyBuilder.java  (or your custom name)
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/
```

**Note**: The topology builder class name is customizable via the `topologyBuilderClass` property.

## Next steps

After generating the project:

1. Navigate to the generated project directory:
   ```bash
   cd my-kafka-streams-app
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Customize the topology by modifying the files in the `streams` package.

## Customizing the archetype

To customize the archetype itself:

1. Edit the files in `src/main/resources/archetype-resources/`
2. Update the archetype descriptor in `src/main/resources/META-INF/maven/archetype-metadata.xml`
3. Rebuild and reinstall the archetype:
   ```bash
   mvn clean install
   ```

