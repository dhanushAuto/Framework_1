package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    private static Properties properties = new Properties();

    static {
        try {
            // Load DB config
            properties.load(new FileInputStream("src/test/resources/db/config.properties"));
            // Load other config
            properties.load(new FileInputStream("src/test/resources/config/config/config.properties.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
