# Archetype Parameterization - Summary

## What was changed

The Maven archetype has been updated to make the **topology builder class name** parameterizable.

### Files modified

1. **META-INF/maven/archetype-metadata.xml** ✅
   - Added `topologyBuilderClass` as a required property with default value `UppercaseTopologyBuilder`

2. **archetype-resources/src/main/java/streams/__topologyBuilderClass__.java** ✅
   - Renamed from `UppercaseTopologyBuilder.java` to `__topologyBuilderClass__.java`
   - Class name now uses `${topologyBuilderClass}` variable
   - Logger reference updated to use `${topologyBuilderClass}.class`

3. **archetype-resources/src/main/java/streams/StreamsTopologyConfig.java** ✅
   - Method parameter changed from `UppercaseTopologyBuilder uppercaseTopologyBuilder` to `${topologyBuilderClass} topologyBuilder`
   - JavaDoc updated to reference `${topologyBuilderClass}`

4. **README.md** ✅
   - Added documentation for the new `topologyBuilderClass` property
   - Updated all usage examples to show the new parameter
   - Added a table of customization properties
   - Updated generated project structure documentation

5. **TESTING_ARCHETYPE.md** ✅ (NEW FILE)
   - Comprehensive testing guide for the archetype
   - Multiple test scenarios
   - Verification checklist
   - Windows and Linux examples
   - Naming conventions guide

## How it works

### Maven archetype variable replacement

When Maven generates a project from the archetype:

1. It reads `archetype-metadata.xml` to find required properties
2. For each file with `filtered="true"`, it replaces all `${propertyName}` with the actual value
3. Files with names like `__propertyName__` are renamed to the property value

### Example

**User command:**
```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.example \
  -DartifactId=order-service \
  -Dpackage=com.example.order \
  -DtopologyBuilderClass=OrderTopologyBuilder
```

**Result:**
- File created: `OrderTopologyBuilder.java` (not `__topologyBuilderClass__.java`)
- Class name: `public class OrderTopologyBuilder`
- In `StreamsTopologyConfig`: `OrderTopologyBuilder topologyBuilder`

## Usage examples

### Example 1: Payment processing service

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.company.payment \
  -DartifactId=payment-processor \
  -Dversion=1.0.0 \
  -Dpackage=com.company.payment.streams \
  -DtopologyBuilderClass=PaymentTopologyBuilder \
  -DinteractiveMode=false
```

Generated class: `PaymentTopologyBuilder.java`

### Example 2: User event processing

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.company.user \
  -DartifactId=user-event-processor \
  -Dversion=2.0.0-SNAPSHOT \
  -Dpackage=com.company.user.events \
  -DtopologyBuilderClass=UserEventTopologyBuilder \
  -DinteractiveMode=false
```

Generated class: `UserEventTopologyBuilder.java`

### Example 3: Data enrichment service

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.company.enrichment \
  -DartifactId=data-enricher \
  -Dversion=1.0.0 \
  -Dpackage=com.company.enrichment \
  -DtopologyBuilderClass=EnrichmentTopologyBuilder \
  -DinteractiveMode=false
```

Generated class: `EnrichmentTopologyBuilder.java`

## Default behavior

If you don't specify `-DtopologyBuilderClass`, the default value `UppercaseTopologyBuilder` will be used:

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.example \
  -DartifactId=my-app \
  -Dpackage=com.example.streams \
  -DinteractiveMode=false
```

Result: `UppercaseTopologyBuilder.java` (backward compatible!)

## Benefits

✅ **More descriptive names** - Class name can match the business domain
✅ **Better code organization** - Easy to identify what each topology does
✅ **Follows conventions** - Name reflects the purpose
✅ **Backward compatible** - Default value keeps existing behavior
✅ **Flexible** - Works with any valid Java class name

## Naming recommendations

Good naming patterns:

- `OrderProcessingTopologyBuilder`
- `PaymentTopologyBuilder`
- `UserEventTopologyBuilder`
- `AggregationTopologyBuilder`
- `EnrichmentTopologyBuilder`
- `FilterTopologyBuilder`
- `TransformationTopologyBuilder`

Best practices:
- Use **PascalCase**
- End with **TopologyBuilder** (convention)
- Be **descriptive** of the topology's purpose
- Keep it **concise** but clear
- Use valid **Java identifier** rules (no spaces, special chars)

## Testing

To verify the archetype works correctly after changes:

1. **Build the archetype**:
   ```bash
   cd kafka-streams-archetype
   mvn clean install
   ```

2. **Generate a test project**:
   ```bash
   cd /tmp  # or any test directory
   mvn archetype:generate \
     -DarchetypeGroupId=io.kcmhub \
     -DarchetypeArtifactId=kafka-streams-archetype \
     -DarchetypeVersion=1.0-SNAPSHOT \
     -DgroupId=com.test \
     -DartifactId=test-project \
     -Dpackage=com.test \
     -DtopologyBuilderClass=TestTopologyBuilder \
     -DinteractiveMode=false
   ```

3. **Verify the generated files**:
   ```bash
   # Check file exists
   ls test-project/src/main/java/com/test/streams/TestTopologyBuilder.java
   
   # Check class name
   grep "public class TestTopologyBuilder" test-project/src/main/java/com/test/streams/TestTopologyBuilder.java
   
   # Check StreamsTopologyConfig
   grep "TestTopologyBuilder" test-project/src/main/java/com/test/streams/StreamsTopologyConfig.java
   ```

4. **Compile the generated project**:
   ```bash
   cd test-project
   mvn clean compile
   ```

5. **Clean up**:
   ```bash
   cd ..
   rm -rf test-project
   ```

## Troubleshooting

### Problem: Class name not replaced

**Symptom**: Generated file still named `__topologyBuilderClass__.java`

**Solution**: Ensure:
- The archetype was installed: `mvn clean install` in `kafka-streams-archetype/`
- You're using the correct archetype version in the generate command
- The archetype coordinates match exactly

### Problem: Variable ${topologyBuilderClass} appears in code

**Symptom**: Generated Java file contains `${topologyBuilderClass}` as literal text

**Solution**: 
- Check that `filtered="true"` is set in `archetype-metadata.xml` for the fileSet containing Java files
- Reinstall the archetype: `mvn clean install`

### Problem: Generated project doesn't compile

**Symptom**: Maven compile errors in generated project

**Solution**:
- Ensure the topology builder class name is a valid Java identifier
- Check that there are no typos in the archetype template files
- Verify all `${topologyBuilderClass}` references were replaced

## Comparison: Before vs After

### Before (fixed class name)

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.example \
  -DartifactId=my-app \
  -Dpackage=com.example.streams
```

**Always generated**: `UppercaseTopologyBuilder.java`

### After (parameterizable)

```bash
mvn archetype:generate \
  -DarchetypeGroupId=io.kcmhub \
  -DarchetypeArtifactId=kafka-streams-archetype \
  -DarchetypeVersion=1.0-SNAPSHOT \
  -DgroupId=com.example \
  -DartifactId=my-app \
  -Dpackage=com.example.streams \
  -DtopologyBuilderClass=MyCustomTopologyBuilder
```

**Generates**: `MyCustomTopologyBuilder.java` (or any name you want!)

## Conclusion

The archetype is now **flexible and reusable** for different use cases while maintaining **backward compatibility** with the default class name.

✅ **Implementation complete and tested**
✅ **Documentation updated**
✅ **Backward compatible**
✅ **Ready to use**

