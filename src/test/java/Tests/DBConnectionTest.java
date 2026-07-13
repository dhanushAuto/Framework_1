package Tests;

import com.mongodb.client.MongoClient;
import org.testng.annotations.Test;
import utilities.common_utils.MongoUtils;
import utilities.common_utils.ReportUtils;

public class DBConnectionTest {

    @Test
    public void testListAllDatabases() {
        try {
            // Get the shared client instance
            MongoClient client = MongoUtils.getClient();
            
            ReportUtils.info("✅ Databases present on localhost:27017:");
            
            // listDatabaseNames() returns an iterable of strings
            for (String dbName : client.listDatabaseNames()) {
                ReportUtils.info(" - " + dbName);
            }
            
        } catch (Exception e) {
            ReportUtils.fail("DB Connection failed: " + e.getMessage());
        }
    }
}
