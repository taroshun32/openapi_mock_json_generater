# openapi-mock-json-generater
openapi.yamlからmock-serverのmock-jsonを生成する

## タスク一覧

### 実行

```
./gradlew run
```

### ビルド(jarファイル生成)

```
./gradlew shadowJar
```

```
java -jar build/libs/openapi-mock-json-generater-1.0-SNAPSHOT-all.jar \
  -i {{input_openapi_file_name}} \
  -o {{output_directory}}
```

APIのレスポンスが無い場合、空ファイルを生成  
必要ない場合は、引数に「--noEmptyFIle」を含める

## 参考

・https://stackoverflow.com/questions/41408768/how-to-generate-json-examples-from-openapi-swagger-model-definition  
・https://github.com/swagger-api/swagger-inflector/
