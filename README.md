# openapi-mock-json-generater
this project generate sample json from openapi.yaml 

## usage

### run

```
./gradlew run
```

### create jar

```
./gradlew shadowJar
```

```
java -jar build/libs/openapi-mock-json-generater-1.0-SNAPSHOT-all.jar \
  -i {{input_openapi_file_name}} \
  -o {{output_directory}}
```

If you do not need an empty file, include "--noEmptyFIle" as an argument.

## reference

・https://stackoverflow.com/questions/41408768/how-to-generate-json-examples-from-openapi-swagger-model-definition  
・https://github.com/swagger-api/swagger-inflector/
