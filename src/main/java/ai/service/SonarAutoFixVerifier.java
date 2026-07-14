package ai.service;

import ai.model.SonarFix;
import ai.util.IssueCache;
import ai.util.SafeFixFilter;
import utilities.common_utils.LogUtils;
import utilities.common_utils.FileUtils;
import utilities.common_utils.PatchUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Applies AI-generated Sonar fixes to disk and, if compilation later fails,
 * rolls every applied file back to its pre-fix state. Only fixes that pass
 * {@link SafeFixFilter#isSafeFix(SonarFix)} are ever written to disk.
 * <p>
 * Compilation itself is intentionally NOT done here - the orchestrator
 * ({@code ai.healing.SonarAutoFix}) calls {@code ai.util.CompilationVerifier}
 * exactly once after all fixes in the batch have been applied, per the
 * "Compile Once" step in the pipeline.
 */
public class SonarAutoFixVerifier {

    public static class ApplyResult {
        private final List<SonarFix> appliedFixes = new ArrayList<>();
        private final List<SonarFix> skippedUnsafeFixes = new ArrayList<>();
        private final List<SonarFix> failedToApplyFixes = new ArrayList<>();
        private final Set<String> touchedFilePaths = new LinkedHashSet<>();

        public List<SonarFix> getAppliedFixes() {
            return appliedFixes;
        }

        public List<SonarFix> getSkippedUnsafeFixes() {
            return skippedUnsafeFixes;
        }

        public List<SonarFix> getFailedToApplyFixes() {
            return failedToApplyFixes;
        }

        public Set<String> getTouchedFilePaths() {
            return touchedFilePaths;
        }
    }

    /**
     * Applies every safe/confident fix in {@code fixes} to disk, keeping a
     * {@code .bak} backup of each touched file (created by
     * {@link PatchUtils#applyFix}) so {@link #rollback(Set)} can restore
     * everything if the subsequent single compile fails.
     */
    public ApplyResult applyFixes(List<SonarFix> fixes) {
        ApplyResult result = new ApplyResult();

        if (fixes == null || fixes.isEmpty()) {
            LogUtils.info("No fixes to apply.");
            return result;
        }

        for (SonarFix fix : fixes) {
            if (!SafeFixFilter.isSafeFix(fix)) {
                result.skippedUnsafeFixes.add(fix);
                continue;
            }

            String component = fix.getIssue().getComponent();
            String localPath = FileUtils.getLocalFilePath(component);

            boolean applied = PatchUtils.applyFix(localPath, fix.getStartLine(), fix.getEndLine(), fix.getFixedCode());

            if (applied) {
                LogUtils.info("Applied fix to " + localPath + " (rule: " + fix.getIssue().getRule() + ")");
                result.appliedFixes.add(fix);
                result.touchedFilePaths.add(localPath);
            } else {
                LogUtils.warn("Failed to apply fix to " + localPath + " (rule: " + fix.getIssue().getRule() + ")");
                result.failedToApplyFixes.add(fix);
            }
        }

        LogUtils.info(String.format("Apply phase complete: %d applied, %d skipped (unsafe), %d failed to apply.",
                result.appliedFixes.size(), result.skippedUnsafeFixes.size(), result.failedToApplyFixes.size()));

        return result;
    }

    /**
     * Restores every touched file from its {@code .bak} backup. Used when
     * the single post-fix compilation fails, so the working tree is left
     * exactly as it was before this run started.
     */
    public void rollback(Set<String> touchedFilePaths) {
        if (touchedFilePaths == null || touchedFilePaths.isEmpty()) {
            return;
        }

        LogUtils.warn("Rolling back " + touchedFilePaths.size() + " file(s) due to failed verification.");

        for (String filePath : touchedFilePaths) {
            Path backup = Paths.get(filePath + ".bak");
            Path original = Paths.get(filePath);

            if (!Files.exists(backup)) {
                LogUtils.error("No backup found for " + filePath + "; cannot roll back automatically.");
                continue;
            }

            try {
                Files.copy(backup, original, StandardCopyOption.REPLACE_EXISTING);
                LogUtils.info("Rolled back: " + filePath);
            } catch (IOException e) {
                LogUtils.error("Failed to roll back " + filePath + ": " + e.getMessage());
            }
        }
    }

    /** Marks every successfully applied+verified fix as fixed in the IssueCache so future runs skip it. */
    public void markFixesAsCached(List<SonarFix> appliedFixes) {
        if (appliedFixes == null) {
            return;
        }
        for (SonarFix fix : appliedFixes) {
            if (fix.getIssue() != null) {
                IssueCache.markFixed(fix.getIssue());
            }
        }
    }
}
