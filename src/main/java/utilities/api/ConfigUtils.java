package utilities.api;


import utilities.common_utils.LogUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigUtils {
    
    private ConfigUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static Properties properties = new Properties();
    private static final String DEFAULT_PATH = "src/test/resources/config/config/config.properties.txt";
    private static String currentPath = DEFAULT_PATH;
    static {
        loadProperties();
    }

    public static void loadProperties() {
        loadProperties(DEFAULT_PATH);
    }

    public static void loadProperties(String path) {
        currentPath = path;
        try (FileInputStream fis = new FileInputStream(path)) {
            properties.load(fis);
            LogUtils.info("Loaded properties from: " + path);
        } catch (IOException e) {
            LogUtils.error("Failed to load properties from: " + path + " - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void reloadProperties() {
        properties = new Properties();
        loadProperties(currentPath);
        LogUtils.info("Reloaded properties from: " + currentPath);
    }

    public static String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null) {
            LogUtils.warn("Property not found for key: " + key);
        }
        return value;
    }

    public static String getProperty(String key, String defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            LogUtils.warn("Property not found for key: " + key + ", using default: " + defaultValue);
            return defaultValue;
        }
        return value;
    }

    public static void setProperty(String key, String value) {
        properties.setProperty(key, value);
        try (FileOutputStream fos = new FileOutputStream(currentPath)) {
            properties.store(fos, "Updated by ConfigUtils");
            LogUtils.info("Set property " + key + " = " + value);
        } catch (IOException e) {
            LogUtils.error("Failed to persist property: " + key + " - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
