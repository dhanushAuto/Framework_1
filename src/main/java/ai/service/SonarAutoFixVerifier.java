package ai.service;

import ai.model.SonarFix;
import utilities.common_utils.FileUtils;
import utilities.common_utils.LogUtils;
import utilities.common_utils.PatchUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SonarAutoFixVerifier {

    public void applyAndVerify(List<SonarFix> fixes) {
        LogUtils.info("=== Starting Auto Sonar Recheck ===");

        int appliedCount = 0;
        List<SonarFix> successfullyApplied = new ArrayList<>();
        
        for (SonarFix fix : fixes) {
            if (fix.isFixed() && fix.getStartLine() != -1) {
                String localPath = FileUtils.getLocalFilePath(fix.getIssue().getComponent());
                if (localPath != null && Paths.get(localPath).toFile().exists()) {
                    LogUtils.info("Applying fix for " + fix.getIssue().getRule() + " in " + fix.getIssue().getComponent());

                    boolean success = PatchUtils.applyFix(localPath, fix.getStartLine(), fix.getEndLine(), fix.getFixedCode());
                    if (success) {
                        appliedCount++;
                        successfullyApplied.add(fix);
                    }
                }
            }
        }

        LogUtils.info("Applied " + appliedCount + " fixes.");

        if (appliedCount > 0) {
            LogUtils.info("Skipping heavy build verification and Sonar scan for quick execution.");
            // boolean buildSuccess = verifyBuild();
            // if (buildSuccess) {
            //    LogUtils.info("All applied fixes verified with build. Now running Sonar analysis...");
            //    runSonarAnalysis();
            // } else {
            //    LogUtils.error("Build failed after applying fixes. Rolling back...");
            //    rollbackFixes(successfullyApplied);
            // }
        }

        LogUtils.info("=== Auto Sonar Recheck Completed ===");
    }

    private void runSonarAnalysis() {
        LogUtils.info("Triggering new SonarQube analysis...");
        try {
            String mvnPath = System.getProperty("os.name").toLowerCase().contains("win") ? "mvn.cmd" : "mvn";
            ProcessBuilder pb = new ProcessBuilder(mvnPath, "sonar:sonar");
            pb.directory(new java.io.File(System.getProperty("user.dir")));
            pb.inheritIO();
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode == 0) {
                LogUtils.info("Sonar analysis completed successfully.");
            } else {
                LogUtils.warn("Sonar analysis failed with exit code: " + exitCode);
            }
        } catch (InterruptedException e) {
            LogUtils.error("Sonar analysis interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            LogUtils.error("Error during Sonar analysis: " + e.getMessage());
        }
    }

    private void rollbackFixes(List<SonarFix> fixes) {
        for (SonarFix fix : fixes) {
            String localPath = FileUtils.getLocalFilePath(fix.getIssue().getComponent());
            if (localPath != null) {
                Path backup = Paths.get(localPath + ".bak");
                if (Files.exists(backup)) {
                    try {
                        Files.move(backup, Paths.get(localPath), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                        LogUtils.info("Rolled back: " + localPath);
                    } catch (IOException e) {
                        LogUtils.error("Failed to rollback " + localPath + ": " + e.getMessage());
                    }
                }
            }
        }
    }

    private boolean verifyBuild() {
        LogUtils.info("Verifying build status...");
        try {
            String mvnPath = System.getProperty("os.name").toLowerCase().contains("win") ? "mvn.cmd" : "mvn";
            ProcessBuilder pb = new ProcessBuilder(mvnPath, "compile", "-DskipTests");
            pb.directory(new java.io.File(System.getProperty("user.dir")));
            Process p = pb.start();

            int exitCode = p.waitFor();

            if (exitCode == 0) {
                LogUtils.info("Build SUCCESSFUL.");
                return true;
            } else {
                LogUtils.error("Build FAILED with exit code: " + exitCode);
                return false;
            }
        } catch (InterruptedException e) {
            LogUtils.error("Build verification interrupted: " + e.getMessage());
            Thread.currentThread().interrupt();
            return false;
        } catch (Exception e) {
            LogUtils.error("Error during build verification: " + e.getMessage());
            return false;
        }
    }
}
