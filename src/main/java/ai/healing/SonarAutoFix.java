package ai.healing;

import ai.analyzer.SonarIssueAnalyzer;
import ai.model.AISonarResult;
import ai.model.AISonarSummary;
import ai.model.SonarFix;
import ai.model.SonarIssue;
import ai.report.AISonarReportGenerator;
import ai.service.LocalSonarRunner;
import ai.service.SonarAutoFixVerifier;
import ai.service.SonarService;
import ai.service.SonarTask;
import ai.util.CompilationVerifier;
import ai.util.ExecutionTimer;
import utilities.api.ConfigUtils;
import utilities.common_utils.GitUtils;
import utilities.common_utils.LogUtils;
import utilities.common_utils.ReportUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Orchestrates the full AI Sonar Auto-Fix pipeline end-to-end:
 *
 * <pre>
 * IntelliJ Run
 *      |
 * Scan Java Files (local Sonar analysis, once)
 *      |
 * AI Analyze (safe vs unsafe vs already-fixed)
 *      |
 * Generate Safe Fixes (parallel, cached)
 *      |
 * Apply All Fixes
 *      |
 * Compile Once
 *      |
 * Run Sonar Once (re-scan)
 *      |
 * Generate reports/AISonarReport.html
 *      |
 * Remaining Issues == 0 ?
 *      |
 *  YES -> Ready for Jenkins
 *  NO  -> Stop &amp; Show Remaining Issues
 * </pre>
 *
 * Existing framework behaviour (other AI modules: Failure Analyzer, Root
 * Cause Analyzer, Log Summarizer, etc.) is untouched - this class only
 * orchestrates the Sonar-specific classes.
 */
public class SonarAutoFix {

    private final SonarService sonarService = new SonarService();
    private final SonarIssueAnalyzer analyzer = new SonarIssueAnalyzer();
    private final SonarAutoFixVerifier verifier = new SonarAutoFixVerifier();
    private final LocalSonarRunner localSonarRunner = new LocalSonarRunner();

    private final boolean runLocalScan =
            Boolean.parseBoolean(ConfigUtils.getProperty("sonar.autofix.run.local.scan", "true"));

    @SuppressWarnings("unchecked")
    public void execute() {
        ExecutionTimer timer = ExecutionTimer.start();
        AISonarSummary summary = new AISonarSummary();
        List<AISonarResult> reportResults = new ArrayList<>();

        LogUtils.info("Starting AI Sonar Auto-Fix Orchestrator");
        ReportUtils.createTest("AI Sonar Auto-Fix Pipeline");

        try {
            List<SonarIssue> issues = handleInitialScan(summary, timer);
            if (issues.isEmpty()) {
                return;
            }

            SonarIssueAnalyzer.AnalysisResult analysis = handleAnalysis(issues, summary, reportResults);
            if (analysis.getSafeIssues().isEmpty()) {
                handleNoSafeIssues(summary, reportResults, timer);
                return;
            }

            List<SonarFix> allFixes = handleFixGeneration(analysis);
            SonarAutoFixVerifier.ApplyResult applyResult = verifier.applyFixes(allFixes);
            
            Object[] compilationResult = handleCompilationAndApply(applyResult, allFixes, summary, reportResults);
            CompilationVerifier.CompileResult compileResult = (CompilationVerifier.CompileResult) compilationResult[0];
            List<SonarFix> finalFixedFixes = (List<SonarFix>) compilationResult[1];
            int remaining = handleRescan(issues, finalFixedFixes, summary, compileResult);
            summary.setRemainingIssuesAfterRescan(remaining);

            handleGitIntegration(finalFixedFixes);
            handleFinalReporting(summary, reportResults, finalFixedFixes, timer);

            finishRun(summary);

        } catch (Exception e) {
            handleOrchestrationError(e, summary, reportResults, timer);
        }
    }

    private List<SonarIssue> handleInitialScan(AISonarSummary summary, ExecutionTimer timer) {
        LogUtils.info("Step 1: Scanning project with local Sonar analysis...");
        if (runLocalScan) {
            boolean scanOk = localSonarRunner.runOnce();
            if (!scanOk) {
                LogUtils.warn("Local Sonar baseline scan did not complete cleanly; "
                        + "continuing with whatever issues are currently on the server.");
            }
        } else {
            LogUtils.info("Local Sonar scan disabled via config (sonar.autofix.run.local.scan=false); "
                    + "using existing server issues.");
        }

        LogUtils.info("Step 2: Fetching SonarQube issues...");
        List<SonarIssue> issues = sonarService.getIssues();

        if (issues.isEmpty()) {
            ReportUtils.info("No Sonar issues found.");
            LogUtils.info("No Sonar issues found. Nothing to do.");
            summary.setExecutionTimeMillis(timer.stop());
            finishRun(summary);
            return new ArrayList<>();
        }

        ReportUtils.info("Total Sonar Issues Found: " + issues.size());
        summary.setTotalIssuesFound(issues.size());
        return issues;
    }

    private SonarIssueAnalyzer.AnalysisResult handleAnalysis(List<SonarIssue> issues, AISonarSummary summary, List<AISonarResult> reportResults) {
        LogUtils.info("Step 3: Analyzing issues (safe vs unsafe vs already fixed)...");
        SonarIssueAnalyzer.AnalysisResult analysis = analyzer.analyze(issues);

        for (SonarIssue skipped : analysis.getUnsafeIssues()) {
            reportResults.add(new AISonarResult(skipped, AISonarResult.Status.SKIPPED_UNSAFE));
        }
        for (SonarIssue cached : analysis.getAlreadyFixedIssues()) {
            reportResults.add(new AISonarResult(cached, AISonarResult.Status.SKIPPED_CACHED));
        }

        summary.setSafeIssuesSelected(analysis.getSafeIssues().size());
        summary.setSkippedUnsafeCount(analysis.getUnsafeIssues().size());
        summary.setSkippedCachedCount(analysis.getAlreadyFixedIssues().size());
        return analysis;
    }

    private void handleNoSafeIssues(AISonarSummary summary, List<AISonarResult> reportResults, ExecutionTimer timer) {
        LogUtils.info("No safe, auto-fixable issues found this run.");
        summary.setCompileSuccess(true);
        summary.setExecutionTimeMillis(timer.stop());
        generateReport(summary, reportResults);
        finishRun(summary);
    }

    private List<SonarFix> handleFixGeneration(SonarIssueAnalyzer.AnalysisResult analysis) {
        LogUtils.info("Step 4: Generating AI fixes for safe issues...");
        Map<String, List<SonarIssue>> groupedIssues = new HashMap<>();
        for (SonarIssue issue : analysis.getSafeIssues()) {
            groupedIssues.computeIfAbsent(issue.getComponent(), k -> new ArrayList<>()).add(issue);
        }
        return generateFixesInParallel(groupedIssues);
    }

    private Object[] handleCompilationAndApply(SonarAutoFixVerifier.ApplyResult applyResult, List<SonarFix> allFixes, 
            AISonarSummary summary, List<AISonarResult> reportResults) {
        LogUtils.info("Step 5: Applying generated fixes...");
        
        LogUtils.info("Step 6: Compiling project once to verify the applied batch...");
        CompilationVerifier.CompileResult compileResult = CompilationVerifier.compile();
        summary.setCompileSuccess(compileResult.success());

        List<SonarFix> finalFixedFixes;
        if (!compileResult.success()) {
            LogUtils.error("Compilation failed after applying fixes. Rolling back all changes.");
            ReportUtils.fail("Compilation failed after applying AI fixes - all changes rolled back.\n"
                    + trimForReport(compileResult.output()));
            verifier.rollback(applyResult.getTouchedFilePaths());
            finalFixedFixes = new ArrayList<>();
            summary.setRolledBackCount(applyResult.getAppliedFixes().size());
        } else {
            LogUtils.info("Compilation succeeded with all applied fixes.");
            verifier.markFixesAsCached(applyResult.getAppliedFixes());
            finalFixedFixes = applyResult.getAppliedFixes();
        }

        reportResults.addAll(buildFixResults(applyResult, compileResult.success()));
        summary.setFixedCount(finalFixedFixes.size());
        summary.setFailedCount(applyResult.getFailedToApplyFixes().size()
                + (int) allFixes.stream().filter(f -> !f.isFixed()).count());
        
        return new Object[]{compileResult, finalFixedFixes};
    }

    private int handleRescan(List<SonarIssue> issues, List<SonarFix> finalFixedFixes, AISonarSummary summary, CompilationVerifier.CompileResult compileResult) {
        LogUtils.info("Step 7: Re-running Sonar once to confirm remaining issues...");
        int remaining = -1;
        if (compileResult.success() && !finalFixedFixes.isEmpty() && runLocalScan) {
            boolean rescanOk = localSonarRunner.runOnce();
            summary.setSonarRescanPerformed(rescanOk);
            if (rescanOk) {
                List<SonarIssue> remainingIssues = sonarService.getIssues();
                remaining = remainingIssues == null ? 0 : remainingIssues.size();
            } else {
                LogUtils.warn("Re-scan did not complete cleanly; remaining issue count is unknown.");
            }
        } else if (compileResult.success()) {
            remaining = issues.size();
        }
        return remaining;
    }

    private void handleFinalReporting(AISonarSummary summary, List<AISonarResult> reportResults, List<SonarFix> finalFixedFixes, ExecutionTimer timer) {
        LogUtils.info("Step 8: Handling Git integration...");
        handleGitIntegration(finalFixedFixes);

        LogUtils.info("Step 9: Generating final report...");
        summary.setExecutionTimeMillis(timer.stop());
        generateReport(summary, reportResults);
        generateExtentSummary(finalFixedFixes);
    }

    private void handleOrchestrationError(Exception e, AISonarSummary summary, List<AISonarResult> reportResults, ExecutionTimer timer) {
        ReportUtils.fail("Orchestration failed: " + e.getMessage());
        LogUtils.error("Orchestration error: " + e.getMessage());
        summary.setExecutionTimeMillis(timer.stop());
        generateReport(summary, reportResults);
    }

    private void finishRun(AISonarSummary summary) {
        if (summary.isReadyForJenkins()) {
            LogUtils.info("AI Sonar Auto-Fix Pipeline Completed. Remaining issues: 0. READY FOR JENKINS.");
            ReportUtils.pass("AI Sonar Auto-Fix Pipeline Completed - Ready for Jenkins.");
        } else {
            LogUtils.warn("AI Sonar Auto-Fix Pipeline Completed WITH remaining issues ("
                    + summary.getRemainingIssuesAfterRescan() + "). NOT ready for Jenkins - see report for details.");
            ReportUtils.fail("AI Sonar Auto-Fix Pipeline Completed - remaining issues found ("
                    + summary.getRemainingIssuesAfterRescan() + "). Stopping before Jenkins handoff.");
        }
        ReportUtils.flushReport();
    }

    private List<SonarFix> generateFixesInParallel(Map<String, List<SonarIssue>> groupedIssues) {
        List<SonarFix> allFixes = java.util.Collections.synchronizedList(new ArrayList<>());
        int poolSize = Integer.parseInt(ConfigUtils.getProperty("sonar.autofix.thread.pool.size", "1"));
        ExecutorService executor = Executors.newFixedThreadPool(
                Math.min(Math.max(groupedIssues.size(), 1), Math.max(poolSize, 1)));
        List<Future<List<SonarFix>>> futures = new ArrayList<>();

        for (List<SonarIssue> fileIssues : groupedIssues.values()) {
            futures.add(executor.submit(new SonarTask(fileIssues)));
        }

        for (Future<List<SonarFix>> future : futures) {
            try {
                allFixes.addAll(future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LogUtils.error("Interrupted during fix generation.");
            } catch (ExecutionException e) {
                LogUtils.error("Error in AI Fix generation task: " + e.getCause().getMessage());
            }
        }

        shutdownExecutor(executor);
        return allFixes;
    }

    private List<AISonarResult> buildFixResults(SonarAutoFixVerifier.ApplyResult applyResult, boolean compileSucceeded) {
        List<AISonarResult> results = new ArrayList<>();

        for (SonarFix fix : applyResult.getAppliedFixes()) {
            AISonarResult result = AISonarResult.fromFix(fix);
            result.setStatus(compileSucceeded ? AISonarResult.Status.FIXED : AISonarResult.Status.ROLLED_BACK);
            results.add(result);
        }
        for (SonarFix fix : applyResult.getSkippedUnsafeFixes()) {
            AISonarResult result = AISonarResult.fromFix(fix);
            result.setStatus(AISonarResult.Status.SKIPPED_UNSAFE);
            results.add(result);
        }
        for (SonarFix fix : applyResult.getFailedToApplyFixes()) {
            AISonarResult result = AISonarResult.fromFix(fix);
            result.setStatus(AISonarResult.Status.FAILED);
            results.add(result);
        }

        return results;
    }

    private void handleGitIntegration(List<SonarFix> appliedFixes) {
        boolean hasFixedAny = appliedFixes != null && appliedFixes.stream().anyMatch(SonarFix::isFixed);
        if (hasFixedAny && GitUtils.isGitInstalled()) {
            String branchName = "ai-sonar-fix-" + System.currentTimeMillis();
            LogUtils.info("Creating new branch: " + branchName);
            GitUtils.createBranch(branchName);
            GitUtils.commit("AI Sonar Fix: Automatically remediated SonarQube issues");
            LogUtils.info("AI fixes committed to branch " + branchName);
        } else {
            LogUtils.info("Skipping Git integration (no fixes applied or Git not installed).");
        }
    }

    private void generateReport(AISonarSummary summary, List<AISonarResult> results) {
        String reportPath = ConfigUtils.getProperty("sonar.report.path", "reports/AISonarReport.html");
        AISonarReportGenerator.generate(summary, results, reportPath);
    }

    private void generateExtentSummary(List<SonarFix> fixes) {
        long fixedCount = fixes.stream().filter(SonarFix::isFixed).count();
        long failedCount = fixes.size() - fixedCount;

        ReportUtils.info(String.format("<b>Summary Report:</b><br/>"
                        + "Total Issues Processed: %d<br/>"
                        + "Successfully Fixed: %d<br/>"
                        + "Failed to Fix: %d<br/>"
                        + "Success Rate: %.2f%%",
                fixes.size(), fixedCount, failedCount,
                (fixes.isEmpty() ? 0 : (double) fixedCount / fixes.size() * 100)));

        ReportUtils.addSonarAIAnalysis(fixes);
    }

    private String trimForReport(String output) {
        if (output == null) {
            return "";
        }
        return output.length() > 2000 ? output.substring(0, 2000) + "...(truncated)" : output;
    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
