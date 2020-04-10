package com.example.openapi_mock_json_generater;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.swagger.oas.inflector.examples.ExampleBuilder;
import io.swagger.oas.inflector.examples.models.Example;
import io.swagger.oas.inflector.processors.JsonNodeExampleSerializer;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        // 引数からファイル名を取得
        String openapiFileName = "openapi.yaml";
        String outputDirName = "mock-json";
        boolean noEmptyFile = false;
        List<String> arguments = Arrays.asList(args);
        int openapiIndex = arguments.indexOf("-i");
        if (openapiIndex >= 0) {
            openapiFileName = args[openapiIndex + 1];
        }

        int outputIndex = arguments.indexOf("-o");
        if (outputIndex >= 0) {
            outputDirName = args[outputIndex + 1];
        }

        if (arguments.contains("--noEmptyFile")) {
            noEmptyFile = true;
        }

        OpenAPI openapi = new OpenAPIV3Parser().read(openapiFileName);
        Components components = openapi.getComponents();
        Map<String, Schema> schemas = components.getSchemas();

        String finalOutputDirName = outputDirName;
        Boolean finalNoEmptyFile = noEmptyFile;
        openapi.getPaths().forEach((key, item) -> {

            Operation get = item.getGet();
            if (get != null) {
                Main.write(get.getResponses(), schemas, "get", key, finalOutputDirName, finalNoEmptyFile);
            }

            Operation post = item.getPost();
            if (post != null) {
                Main.write(post.getResponses(), schemas, "post", key, finalOutputDirName, finalNoEmptyFile);
            }

            Operation put = item.getPut();
            if (put != null) {
                Main.write(put.getResponses(), schemas, "put", key, finalOutputDirName, finalNoEmptyFile);
            }

            Operation delete = item.getDelete();
            if (delete != null) {
                Main.write(delete.getResponses(), schemas, "delete", key, finalOutputDirName, finalNoEmptyFile);
            }
        });
    }

    // レスポンスの一覧を書き込み
    private static void write(
        ApiResponses responses,
        Map<String, Schema> schemas,
        String method,
        String path,
        String outputDir,
        Boolean noEmptyFile
    ) {
        // 200系のみJSONの対象にする
        Pattern pattern = Pattern.compile("20.");
        responses.entrySet().stream()
            .filter(response -> pattern.matcher(response.getKey()).find())
            .forEach(response -> {
                try {
                    Main.write(response.getValue(), schemas, method, path, outputDir, noEmptyFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }

    // レスポンスを書き込み
    private static void write(
        ApiResponse response,
        Map<String, Schema> schemas,
        String method,
        String path,
        String outputDir,
        Boolean noEmptyFile
    ) throws IOException {
        String fileName = method + path.replace("/", "-").replace("{", "_").replace("}", "") + ".json";
        // レスポンスのなく、noEmptyFileフラグが立っている場合はファイル作成不要
        if (response.getContent() == null) {
            if (noEmptyFile) {
                return;
            }
            Main.write(fileName, "", outputDir);
            return;
        }

        Schema schema = response.getContent()
            .get("application/json")
            .getSchema();

        Example example = ExampleBuilder.fromSchema(schema, schemas);
        Main.write(fileName, Main.toJsonString(example), outputDir);
    }

    private static String toJsonString(Example example) {
        SimpleModule module = new SimpleModule().addSerializer(new JsonNodeExampleSerializer());
        Json.mapper().registerModule(module);

        return Json.pretty(example);
    }


    private static void write(String name, String json, String outputDir) throws IOException {
        File dir = new File(outputDir);
        // if not exists mock_json directory, create directory.
        if (!dir.exists()) {
            if (!dir.mkdir()) {
                throw new RuntimeException("failed to create directory.");
            }
        }

        File jsonFile = new File(outputDir + "/" + name);
        try (FileWriter writer = new FileWriter(jsonFile)) {
            writer.write(json);
        }
    }
}
