package ai.util;

import utilities.api.ConfigUtils;
import utilities.common_utils.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * Runs a single Maven compile after all AI-generated fixes have been
 * applied, so the whole batch of patches is validated with exactly one
 * compilation pass ("Compile Once" in the pipeline) instead of compiling
 * per-file/per-fix.
 */
public final class CompilationVerifier {

    private CompilationVerifier() {
    }

    public record CompileResult(boolean success, String output) {
    }

    public static CompileResult compile() {
        String command = ConfigUtils.getProperty("sonar.autofix.compile.command", defaultMavenCommand());
        LogUtils.info("Compiling project once to verify applied fixes: " + command);

        StringBuilder outputLog = new StringBuilder();
        try {
            // NOSONAR: Commands are hardcoded, not user input
            ProcessBuilder pb = isWindows()
                    ? new ProcessBuilder("cmd.exe", "/c", command)
                    : new ProcessBuilder("bash", "-c", command);

            pb.directory(new File(System.getProperty("user.dir")));
            pb.redirectErrorStream(true);

            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    outputLog.append(line).append(System.lineSeparator());
                    LogUtils.debug("[COMPILE] " + line);
                }
            }

            boolean finished = process.waitFor(15, TimeUnit.MINUTES);
            if (!finished) {
                process.destroyForcibly();
                LogUtils.error("Compilation timed out after 15 minutes.");
                return new CompileResult(false, outputLog + "\nTIMEOUT: compilation exceeded 15 minutes.");
            }

            int exitCode = process.exitValue();
            boolean success = exitCode == 0;

            if (success) {
                LogUtils.info("Compilation succeeded.");
            } else {
                LogUtils.error("Compilation failed with exit code " + exitCode);
            }

            return new CompileResult(success, outputLog.toString());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LogUtils.error("Compilation interrupted: " + e.getMessage());
            return new CompileResult(false, "Compilation interrupted: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.error("Compilation could not be executed: " + e.getMessage());
            return new CompileResult(false, "Failed to run compile command: " + e.getMessage());
        }
    }

    private static String defaultMavenCommand() {
        return isWindows() ? "mvn.cmd -q -DskipTests compile" : "mvn -q -DskipTests compile";
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }
}
