package Tests;

import com.mongodb.client.MongoClient;
import org.testng.annotations.Test;
import utilities.common_utils.CSVUtils;
import utilities.common_utils.MongoUtils;
import utilities.common_utils.ReportUtils;
import config.ConfigLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseListTest {
    @Test
    public void exportDatabaseListToCSV() {
        String filePath = "src/test/resources/testdata/CSV/DatabaseList.csv";
        String expectedDbsStr = ConfigLoader.getProperty("mongo.expected.dbs");
        List<String> expectedDbs = expectedDbsStr != null ? Arrays.asList(expectedDbsStr.split(",")) : new ArrayList<>();

        try {
            MongoClient client = MongoUtils.getClient();
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"DatabaseName", "Status"});

            for (String dbName : client.listDatabaseNames()) {
                String status = expectedDbs.contains(dbName) ? "EXPECTED" : "NotMatched";
                data.add(new String[]{dbName, status});
            }

            CSVUtils.writeCSV(filePath, data);
            ReportUtils.pass("✅ Database list exported to " + filePath);

        } catch (Exception e) {
            ReportUtils.fail("❌ Failed to export database list: " + e.getMessage());
        }
    }
}
