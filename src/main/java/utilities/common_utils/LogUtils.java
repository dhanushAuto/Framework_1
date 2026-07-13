package utilities.common_utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LogUtils {
    
    private LogUtils() {
        throw new IllegalStateException("Utility class");
    }

    private static Logger getLogger() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        String className = LogUtils.class.getName();
        for (int i = 1; i < stackTrace.length; i++) {
            if (!stackTrace[i].getClassName().equals(className)) {
                return LogManager.getLogger(stackTrace[i].getClassName());
            }
        }
        return LogManager.getLogger(LogUtils.class);
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
