package Tests;

import org.testng.annotations.Test;
import utilities.common_utils.CSVUtils;
import utilities.common_utils.ReportUtils;
import utilities.dao.EmployeeDAO;

import java.util.Arrays;
import java.util.List;

public class DBValidationTest {

    @Test
    public void validateEmployeeData() {
        String filePath = "src/test/resources/testdata/CSV/Users.csv";
        try {
            List<String[]> data = CSVUtils.readCSV(filePath);

            // Skip header (i=1)
            for (int i = 1; i < data.size(); i++) {
                String[] row = data.get(i);
                int id = Integer.parseInt(row[0].trim());
                String expectedName = row[1].trim();

                String actualName = EmployeeDAO.getEmployeeNameById(id);

                String status = (actualName != null && actualName.equals(expectedName)) ? "EXPECTED" : "INVALID DATA";

                // Update status in column 3
                if (row.length < 4) {
                    row = Arrays.copyOf(row, 4);
                }
                row[3] = status;
                data.set(i, row);
            }

            ReportUtils.pass("✅ Validation complete. Results updated in " + filePath);

        } catch (Exception e) {
            ReportUtils.fail("❌ Validation failed: " + e.getMessage());
        }
    }
}
