package ai.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class CodeExtractor {

    private static final int CONTEXT_LINES = 20;

    public static String extractCode(String filePath, int issueLine) throws IOException {

        List<String> lines = Files.readAllLines(Paths.get(filePath));

        if (lines.isEmpty()) {
            return "";
        }

        int lineIndex = Math.max(0, issueLine - 1);

        int methodStart = findMethodStart(lines, lineIndex);
        int methodEnd = findMethodEnd(lines, methodStart);

        if (methodStart != -1 && methodEnd != -1) {
            
            // Record the range in a thread-local or return it
            // For simplicity, let's keep it here but we'll need to pass it back
            // so we know where to re-insert the code.
            
            StringBuilder builder = new StringBuilder();

            for (int i = methodStart; i <= methodEnd; i++) {
                builder.append(lines.get(i)).append("\n");
            }

            return builder.toString();
        }

        return extractContext(lines, lineIndex);
    }

    public static int[] getMethodRange(String filePath, int issueLine) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        if (lines.isEmpty()) return null;

        int lineIndex = Math.max(0, issueLine - 1);
        int methodStart = findMethodStart(lines, lineIndex);
        int methodEnd = findMethodEnd(lines, methodStart);

        if (methodStart != -1 && methodEnd != -1) {
            return new int[]{methodStart, methodEnd};
        }
        return null;
    }

    private static int findMethodStart(List<String> lines, int issueLine) {

        for (int i = issueLine; i >= 0; i--) {

            String line = lines.get(i).trim();

            if (line.matches(".*(public|private|protected).*\\(.*\\).*\\{?")) {
                return i;
            }
        }

        return -1;
    }

    private static int findMethodEnd(List<String> lines, int start) {

        if (start == -1)
            return -1;

        int braces = 0;

        for (int i = start; i < lines.size(); i++) {

            String line = lines.get(i);

            braces += count(line, '{');
            braces -= count(line, '}');

            if (braces == 0 && i > start)
                return i;
        }

        return -1;
    }

    private static int count(String s, char c) {

        int total = 0;

        for (char x : s.toCharArray()) {
            if (x == c)
                total++;
        }

        return total;
    }

    private static String extractContext(List<String> lines, int issueLine) {

        int start = Math.max(0, issueLine - CONTEXT_LINES);

        int end = Math.min(lines.size() - 1, issueLine + CONTEXT_LINES);

        StringBuilder builder = new StringBuilder();

        for (int i = start; i <= end; i++) {
            builder.append(lines.get(i)).append("\n");
        }

        return builder.toString();
    }
}
