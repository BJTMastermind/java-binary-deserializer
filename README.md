> [!note]
> This project has been migrated to Codeberg and will no longer be updated here.<br>
> Visit the projects Codeberg repository at: https://codeberg.org/VoxelBill/java-binary-deserializer

# Java Binary Data Deserializer

## Requirements

* Java 17

## Quick usage examples

* Deserialize Java Serialized Binary data:
```bash
java -jar javaDeserializer.jar --deserialize -f <input_java_binary> -o <output_filename>
```

* Serialize into Java Binary data
```bash
java -jar javaDeserializer.jar --serialize -f <input_deserialized_data> -o <output_filename>
```

## References

https://docs.oracle.com/javase/8/docs/platform/serialization/spec/protocol.html
