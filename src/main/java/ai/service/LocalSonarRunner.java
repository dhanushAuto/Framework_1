package ai.service;

import ai.util.ExecutionTimer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import utilities.api.ConfigUtils;
import utilities.common_utils.JsonUtils;
import utilities.common_utils.LogUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Triggers a single local SonarQube analysis run (Maven Sonar plugin) and
 * blocks until the server has finished processing the report, so that a
 * subsequent call to {@link SonarService#getIssues()} reflects the latest
 * code state. This lets the whole pipeline run end-to-end from IntelliJ
 * without a separate CI job having to run Sonar first.
 * <p>
 * Used twice in the pipeline: once to establish the baseline before fixes
 * are generated, and once after fixes are applied/compiled to confirm the
 * remaining issue count ("Run Sonar Once" in the flow diagram, per pass).
 */
public class LocalSonarRunner {

    private static final Pattern TASK_URL_PATTERN =
            Pattern.compile("api/ce/task\\?id=([A-Za-z0-9_-]+)");

    private final String sonarUrl = ConfigUtils.getProperty("sonar.url");
    private final String token = ConfigUtils.getProperty("sonar.token");

    public boolean runOnce() {
        ExecutionTimer timer = ExecutionTimer.start();
        String command = ConfigUtils.getProperty("sonar.scanner.command", defaultMavenSonarCommand());
        LogUtils.info("Running local Sonar analysis: " + command);

        String ceTaskId = null;
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
                    LogUtils.debug("[SONAR-SCAN] " + line);
                    Matcher matcher = TASK_URL_PATTERN.matcher(line);
                    if (matcher.find()) {
                        ceTaskId = matcher.group(1);
                    }
                }
            }

            boolean finished = process.waitFor(10, java.util.concurrent.TimeUnit.MINUTES);
            if (!finished) {
                process.destroyForcibly();
                LogUtils.error("Sonar scan timed out after 10 minutes.");
                return false;
            }

            if (process.exitValue() != 0) {
                LogUtils.error("Sonar scan process exited with code " + process.exitValue());
                return false;
            }

        } catch (Exception e) {
            LogUtils.error("Failed to run local Sonar scan: " + e.getMessage());
            return false;
        }

        boolean processed = waitForServerProcessing(ceTaskId);
        LogUtils.info("Local Sonar scan finished in " + timer.elapsedFormatted()
                + " (server processing complete: " + processed + ")");
        return processed;
    }

    /**
     * Polls SonarQube's Compute Engine task API until the background
     * analysis job has finished ("SUCCESS"/"FAILED"/"CANCELED"), or a
     * sensible timeout elapses. If we couldn't capture a task id from the
     * scanner output (older scanner versions, custom logging, etc.), falls
     * back to a fixed grace period so issues fetched immediately after
     * aren't stale.
     */
    private boolean waitForServerProcessing(String ceTaskId) {
        int maxAttempts = Integer.parseInt(ConfigUtils.getProperty("sonar.wait.max.attempts", "30"));
        long pollIntervalMs = Long.parseLong(ConfigUtils.getProperty("sonar.wait.poll.interval.ms", "2000"));

        if (ceTaskId == null) {
            LogUtils.warn("Could not detect Sonar background task id from scanner output; "
                    + "waiting a fixed grace period instead of polling.");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return false;
            }
            return true;
        }

        String taskApi = sonarUrl + "/api/ce/task?id=" + ceTaskId;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpGet get = new HttpGet(taskApi);
                get.addHeader("Authorization", "Basic "
                        + Base64.getEncoder().encodeToString((token + ":").getBytes(StandardCharsets.UTF_8)));

                ClassicHttpResponse response = client.executeOpen(null, get, null);
                String body;
                try {
                    body = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
                } finally {
                    response.close();
                }

                JsonNode task = JsonUtils.readJsonString(body).path("task");
                String status = task.path("status").asText("PENDING");

                LogUtils.debug("Sonar CE task " + ceTaskId + " status: " + status);

                if ("SUCCESS".equalsIgnoreCase(status)) {
                    return true;
                }
                if ("FAILED".equalsIgnoreCase(status) || "CANCELED".equalsIgnoreCase(status)) {
                    LogUtils.error("Sonar background analysis task ended with status: " + status);
                    return false;
                }

            } catch (Exception e) {
                LogUtils.warn("Error polling Sonar CE task status (attempt " + attempt + "): " + e.getMessage());
            }

            try {
                Thread.sleep(pollIntervalMs);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ie);
            }
        }

        LogUtils.warn("Timed out waiting for Sonar server to finish processing the report.");
        return false;
    }

    private static String defaultMavenSonarCommand() {
        String base = isWindows() ? "mvn.cmd -q -DskipTests" : "mvn -q -DskipTests";
        return base + " sonar:sonar";
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }
}
