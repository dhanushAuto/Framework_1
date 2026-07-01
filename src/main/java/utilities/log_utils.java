package utilities;

import java.util.logging.Logger;

public class log_utils {

    private static final Logger log = Logger.getLogger(String.valueOf(log_utils.class));

    public static void info(String message){
        log.info(message);
    }
    public static void warn(String message){
        log.warning(message);
    }
    public static void error(String message){
        log.severe(message);
    }
    public static void fatal(String message){
        log.log(java.util.logging.Level.SEVERE, message);
    }
    public static void debug(String message){
        log.finest(message);
    }
    public static void trace(String message){
        log.finer(message);
    }

}
