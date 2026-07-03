package config;

public class EnvironmentManager {

    public static String getEnvironment(String env) {

        return switch (env.toLowerCase()) {
            case "dev" -> "dev.properties.txt";
            case "qa" -> "qa.properties.txt";
            case "sit" -> "sit.properties.txt";
            case "prod" -> "prod.properties.txt";
            default -> null;
        };
    }
}