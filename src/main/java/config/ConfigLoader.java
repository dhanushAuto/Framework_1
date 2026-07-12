package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigLoader {
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final Properties properties = new Properties();

    static {
        try {
            // Load DB config
            properties.load(new FileInputStream("src/test/resources/db/config.properties"));

            properties.load(new FileInputStream("src/test/resources/config/config/config.properties.txt"));
        } catch (IOException e) {
            logger.error("Failed to load configuration files", e);
        }
    }

    private ConfigLoader() {
        /* This utility class should not be instantiated */
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
