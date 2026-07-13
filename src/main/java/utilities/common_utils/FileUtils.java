package utilities.common_utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtils {

    private FileUtils() {
    }

    public static String readFile(String filePath) {

        try {

            return Files.readString(Path.of(filePath));

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to read file : " + filePath,
                    e);
        }

    }
    public static String getLocalFilePath(String component) {

        if (component == null || component.isBlank()) {
            return null;
        }

        // Remove project name before ':'
        if (component.contains(":")) {
            component = component.substring(component.indexOf(":") + 1);
        }

        return System.getProperty("user.dir")
                + File.separator
                + component.replace("/", File.separator);

    }

}