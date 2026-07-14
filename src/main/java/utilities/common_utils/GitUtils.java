package utilities.common_utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GitUtils {

    private static final String GIT = "git";

    public static boolean isGitInstalled() {
        return executeCommand("git --version") == 0;
    }

    public static void createBranch(String branchName) {
        executeCommand("git checkout -b " + branchName);
    }

    public static void commit(String message) {
        executeCommand("git add .");
        executeCommand("git commit -m \"" + message + "\" --trailer \"Co-authored-by: Junie <junie@jetbrains.com>\"");
    }

    public static void push(String branchName) {
        executeCommand("git push origin " + branchName);
    }

    public static String getCurrentBranch() {
        List<String> output = new ArrayList<>();
        executeCommand("git rev-parse --abbrev-ref HEAD", output);
        return output.isEmpty() ? "unknown" : output.get(0);
    }

    private static int executeCommand(String command) {
        return executeCommand(command, null);
    }

    private static int executeCommand(String command, List<String> output) {
        try {
            LogUtils.info("Executing command: " + command);
            String[] cmdArray = command.split(" ");
            ProcessBuilder pb = new ProcessBuilder(cmdArray);
            pb.directory(new File(System.getProperty("user.dir")));
            pb.redirectErrorStream(true);
            Process p = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (output != null) output.add(line);
                    LogUtils.debug("[GIT] " + line);
                }
            }

            return p.waitFor();
        } catch (InterruptedException e) {
            LogUtils.error("Git command interrupted: " + command);
            Thread.currentThread().interrupt();
            return -1;
        } catch (Exception e) {
            LogUtils.error("Git command failed: " + command + " - " + e.getMessage());
            return -1;
        }
    }
}
