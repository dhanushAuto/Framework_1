package utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;

public class json_utils {


    private static final ObjectMapper mapper = new ObjectMapper();

    public static JsonNode readJson(String filePath) {
        try {
            JsonNode node = mapper.readTree(new File(filePath));
            log_utils.info("Read JSON from: " + filePath);
            return node;
        } catch (IOException e) {
            log_utils.error("Failed to read JSON from: " + filePath + " - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void writeJson(String filePath, Object data) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), data);
            log_utils.info("Wrote JSON to: " + filePath);
        } catch (IOException e) {
            log_utils.error("Failed to write JSON to: " + filePath + " - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String getValue(String filePath, String fieldPath) {
        JsonNode node = readJson(filePath).at(fieldPath); // e.g. "/user/name"
        String value = node.asText();
        log_utils.info("Got JSON value at " + fieldPath + ": " + value);
        return value;
    }

    public static void setValue(String filePath, String fieldName, String value) {
        try {
            JsonNode root = mapper.readTree(new File(filePath));
            ((com.fasterxml.jackson.databind.node.ObjectNode) root).put(fieldName, value);
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), root);
            log_utils.info("Set JSON value " + fieldName + " = " + value);
        } catch (IOException e) {
            log_utils.error("Failed to set JSON value: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static String convertObjectToJson(Object object) {
        try {
            String json = mapper.writeValueAsString(object);
            log_utils.info("Converted object to JSON");
            return json;
        } catch (IOException e) {
            log_utils.error("Failed to convert object to JSON - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertJsonToObject(String json, Class<T> clazz) {
        try {
            T object = mapper.readValue(json, clazz);
            log_utils.info("Converted JSON to object of type: " + clazz.getSimpleName());
            return object;
        } catch (IOException e) {
            log_utils.error("Failed to convert JSON to object - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}




