package utilities;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class config_utils {
    private static Properties properties = new Properties();
    private static final String DEFAULT_PATH = "src/main/resources/config.properties";
    private static String currentPath = DEFAULT_PATH;

    public static void loadProperties() {
        loadProperties(DEFAULT_PATH);
    }

    public static void loadProperties(String path) {
        currentPath = path;
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
            log_utils.info("Loaded properties from: " + path);
        } catch (IOException e) {
            log_utils.error("Failed to load properties from: " + path + " - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void reloadProperties() {
        properties = new Properties();
        loadProperties(currentPath);
        log_utils.info("Reloaded properties from: " + currentPath);
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            log_utils.warn("Property not found for key: " + key);
        }
        return value;
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
        try (FileOutputStream fos = new FileOutputStream(currentPath)) {
            properties.store(fos, "Updated by ConfigUtils");
            log_utils.info("Set property " + key + " = " + value);
        } catch (IOException e) {
            log_utils.error("Failed to persist property: " + key + " - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
