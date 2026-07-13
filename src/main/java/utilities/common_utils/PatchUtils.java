package utilities.common_utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public final class PatchUtils {

    private static final String PATCH_FOLDER = "reports/patches/";

    private PatchUtils() {}

    public static String generatePatch(String filePath, String originalCode, String fixedCode) {
        List<String> patch = new ArrayList<>();
        patch.add("--- " + filePath);
        patch.add("+++ " + filePath);
        patch.add("@@ -1,1 +1,1 @@");

        String[] originalLines = originalCode.split("\n");
        String[] fixedLines = fixedCode.split("\n");

        for (String line : originalLines) {
            patch.add("-" + line);
        }
        for (String line : fixedLines) {
            patch.add("+" + line);
        }

        return String.join("\n", patch);
    }

    public static String savePatch(String fileName, String patchContent) throws IOException {
        Path folder = Paths.get(PATCH_FOLDER);
        if (!Files.exists(folder)) {
            Files.createDirectories(folder);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String patchFileName = fileName.replace(".java", "").replace(":", "_") + "_" + timestamp + ".patch";
        Path patchPath = folder.resolve(patchFileName);

        Files.writeString(patchPath, patchContent);
        return patchPath.toString();
    }

    public static boolean applyFix(String filePath, int startLine, int endLine, String fixedCode) {
        try {
            Path path = Paths.get(filePath);
            List<String> lines = Files.readAllLines(path);
            
            // Backup
            Files.copy(path, Paths.get(filePath + ".bak"), StandardCopyOption.REPLACE_EXISTING);

            List<String> newLines = new ArrayList<>();
            for (int i = 0; i < startLine; i++) {
                newLines.add(lines.get(i));
            }
            
            String[] fixLines = fixedCode.split("\n");
            for (String fixLine : fixLines) {
                newLines.add(fixLine);
            }
            
            for (int i = endLine + 1; i < lines.size(); i++) {
                newLines.add(lines.get(i));
            }
            
            Files.write(path, newLines);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to apply fix to " + filePath + ": " + e.getMessage());
            return false;
        }
    }
}
