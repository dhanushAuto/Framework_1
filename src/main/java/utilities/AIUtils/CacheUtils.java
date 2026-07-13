package utilities.AIUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CacheUtils {

    private static final String CACHE_FOLDER = "cache/sonar/";

    private CacheUtils(){}

    public static boolean exists(String key){

        return Files.exists(
                Paths.get(CACHE_FOLDER + key + ".json")
        );
    }

    public static String read(String key) throws IOException{

        return Files.readString(
                Paths.get(CACHE_FOLDER + key + ".json")
        );
    }

    public static void write(String key,String value)
            throws IOException{

        Path folder = Paths.get(CACHE_FOLDER);

        if(!Files.exists(folder))
            Files.createDirectories(folder);

        Files.writeString(
                Paths.get(CACHE_FOLDER + key + ".json"),
                value
        );
    }

}
