package utilities.common_utils;

import java.sql.Connection;
import java.sql.DriverManager;
import config.ConfigLoader;

public class DBUtils {

    public static Connection getConnection(String dbType) throws Exception {
        return DriverManager.getConnection(
                ConfigLoader.getProperty(dbType + ".url"),
                ConfigLoader.getProperty(dbType + ".username"),
                ConfigLoader.getProperty(dbType + ".password"));
    }

}