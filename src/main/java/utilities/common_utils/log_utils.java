package utilities.common_utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class log_utils {

    private static Logger getLogger() {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[3];
        return LogManager.getLogger(caller.getClassName());
    }

    public static void info(String message) {
        getLogger().info(message);
    }

    public static void warn(String message) {
        getLogger().warn(message);
    }

    public static void error(String message) {
        getLogger().error(message);
    }

    public static void debug(String message) {
        getLogger().debug(message);
    }

    public static void fatal(String message) {
        getLogger().fatal(message);
    }
}

