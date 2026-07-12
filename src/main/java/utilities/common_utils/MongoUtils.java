package utilities.common_utils;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import config.ConfigLoader;

public class MongoUtils {
    private static MongoClient mongoClient;

    // Helper to get the client instance (Singleton pattern)
    public static MongoClient getClient() {
        if (mongoClient == null) {
            String uri = ConfigLoader.getProperty("mongo.uri");
            mongoClient = MongoClients.create(uri);
        }
        return mongoClient;
    }

    // Existing method
    public static MongoDatabase getDatabase() {
        return getClient().getDatabase(ConfigLoader.getProperty("mongo.dbname"));
    }
}
