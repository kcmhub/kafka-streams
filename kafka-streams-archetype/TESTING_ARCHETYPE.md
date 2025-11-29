# Testing the Archetype with Custom Topology Class Name

This document provides instructions to test the archetype with a custom topology builder class name.

## Test 1: Default topology class name

Generate a project with default settings:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.test \
  -DartifactId=test-default \
  -Dversion=1.0-SNAPSHOT \
  -Dpackage=com.test.streams \
  -DinteractiveMode=false
```

**Expected result**: Generates `UppercaseTopologyBuilder.java` (default)

## Test 2: Custom topology class name

Generate a project with a custom topology builder class name:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.test \
  -DartifactId=test-custom \
  -Dversion=1.0-SNAPSHOT \
  -Dpackage=com.test.streams \
  -DtopologyBuilderClass=MyCustomTopologyBuilder \
  -DinteractiveMode=false
```

**Expected result**: Generates `MyCustomTopologyBuilder.java`

## Test 3: Another custom name

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.example \
  -DartifactId=order-processor \
  -Dversion=1.0.0 \
  -Dpackage=com.example.orderprocessor \
  -DtopologyBuilderClass=OrderProcessingTopologyBuilder \
  -DinteractiveMode=false
```

**Expected result**: Generates `OrderProcessingTopologyBuilder.java`

## Verification checklist

After generating a project, verify:

1. **File name is correct**:
   ```bash
   # Should exist with custom name
   ls test-custom/src/main/java/com/test/streams/streams/MyCustomTopologyBuilder.java
   ```

2. **Class name in file matches**:
   ```bash
   # Should contain "public class MyCustomTopologyBuilder"
   grep "public class MyCustomTopologyBuilder" test-custom/src/main/java/com/test/streams/streams/MyCustomTopologyBuilder.java
   ```

3. **StreamsTopologyConfig uses correct class**:
   ```bash
   # Should contain "MyCustomTopologyBuilder topologyBuilder"
   grep "MyCustomTopologyBuilder topologyBuilder" test-custom/src/main/java/com/test/streams/streams/StreamsTopologyConfig.java
   ```

4. **Project compiles**:
   ```bash
   cd test-custom
   mvn clean compile
   ```

5. **Logger reference is correct**:
   ```bash
   # Should contain "LoggerFactory.getLogger(MyCustomTopologyBuilder.class)"
   grep "getLogger(MyCustomTopologyBuilder.class)" test-custom/src/main/java/com/test/streams/streams/MyCustomTopologyBuilder.java
   ```

## Interactive mode test

Test with interactive mode to ensure the property is prompted:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT
```

Maven should prompt for:
1. groupId
2. artifactId
3. version
4. package
5. **topologyBuilderClass** (NEW!)

## Windows PowerShell examples

```powershell
# Test 1: Default
mvn archetype:generate `
  -DarchetypeGroupId=io.kcmhub `
  -DarchetypeArtifactId=kafka-streams-archetype `
  -DarchetypeVersion=1.0-SNAPSHOT `
  -DgroupId=com.test `
  -DartifactId=test-default `
  -Dversion=1.0-SNAPSHOT `
  -Dpackage=com.test.streams `
  -DinteractiveMode=false

# Test 2: Custom
mvn archetype:generate `
  -DarchetypeGroupId=io.kcmhub `
  -DarchetypeArtifactId=kafka-streams-archetype `
  -DarchetypeVersion=1.0-SNAPSHOT `
  -DgroupId=com.test `
  -DartifactId=test-custom `
  -Dversion=1.0-SNAPSHOT `
  -Dpackage=com.test.streams `
  -DtopologyBuilderClass=MyCustomTopologyBuilder `
  -DinteractiveMode=false
```

## Clean up test projects

```bash
# Linux/macOS
rm -rf test-default test-custom order-processor

# Windows PowerShell
Remove-Item -Path test-default,test-custom,order-processor -Recurse -Force
```

## Naming conventions

Recommended naming patterns for topology builder classes:

- `OrderProcessingTopologyBuilder` - For order processing streams
- `UserEventTopologyBuilder` - For user event processing
- `PaymentTopologyBuilder` - For payment processing
- `AggregationTopologyBuilder` - For aggregation logic
- `EnrichmentTopologyBuilder` - For data enrichment
- `FilterTopologyBuilder` - For filtering operations

The name should:
- End with `TopologyBuilder` (convention)
- Use PascalCase
- Be descriptive of what the topology does
- Be a valid Java class name (no spaces, special characters, etc.)

