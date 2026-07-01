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
                return mapper.readTree(new File(filePath));
            } catch (IOException e) {
                throw new RuntimeException("Unable to read JSON file: " + filePath, e);
            }
        }

        public static String getValue(String filePath, String key) {
            return readJson(filePath).get(key).asText();
        }
    }



