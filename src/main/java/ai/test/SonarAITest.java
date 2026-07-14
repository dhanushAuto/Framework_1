package ai.test;

import ai.generator.SonarFixGenerator;
import ai.model.SonarFix;
import ai.model.SonarIssue;
import ai.service.SonarAutoFixVerifier;
import ai.service.SonarService;
import ai.service.SonarTask;
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

public class SonarAITest {

    private final SonarService sonarService = new SonarService();
    private final SonarFixGenerator generator = new SonarFixGenerator();

    private void handleGitIntegration(List<SonarFix> allFixes) {
        boolean hasFixedAny = allFixes.stream().anyMatch(SonarFix::isFixed);
        if (hasFixedAny && GitUtils.isGitInstalled()) {
            String branchName = "ai-sonar-fix-" + System.currentTimeMillis();
            LogUtils.info("Creating new branch: " + branchName);
            GitUtils.createBranch(branchName);
            GitUtils.commit("🤖 AI Sonar Fix: Automatically remediated SonarQube issues");
            LogUtils.info("AI fixes committed to branch " + branchName);
            // GitUtils.push(branchName); // Optional: Push to remote
        }
    }

    private void collectFixesFromFutures(List<Future<List<SonarFix>>> futures, List<SonarFix> allFixes) {
        for (Future<List<SonarFix>> future : futures) {
            try {
                allFixes.addAll(future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted during future.get()", e);
            } catch (ExecutionException e) {
                LogUtils.error("Execution exception during future.get(): " + e.getMessage());
            }
        }
    }

    public void execute() {

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {

            ReportUtils.createTest("🤖 AI Sonar Review");

            LogUtils.info("Fetching SonarQube issues...");

            List<SonarIssue> issues = sonarService.getIssues();

            if (issues == null || issues.isEmpty()) {

                ReportUtils.info("No Sonar issues found.");

                Log,info("No Sonar issues found.");

                return;
            }

            ReportUtils.info("Total Sonar Issues : " + issues.size());

            List<SonarFix> allFixes = java.util.Collections.synchronizedList(new ArrayList<>());

            // Group issues by file (component)
            Map<String, List<SonarIssue>> groupedIssues = new HashMap<>();
            for (SonarIssue issue : issues) {
                groupedIssues.computeIfAbsent(issue.getComponent(), k -> new ArrayList<>()).add(issue);
            }

            List<Future<List<SonarFix>>> futures = new ArrayList<>();

            for (List<SonarIssue> fileIssues : groupedIssues.values()) {
                futures.add(executor.submit(new SonarTask(fileIssues)));
            }

            for (Future<List<SonarFix>> future : futures) {
                try {
                    allFixes.addAll(future.get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during future.get()", e);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }

            // Auto Sonar Recheck (Simulation)
            SonarAutoFixVerifier verifier = new SonarAutoFixVerifier();
            verifier.applyAndVerify(allFixes);

            ReportUtils.addSonarAIAnalysis(allFixes);

            LogUtils.info("AI Sonar Review Completed.");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ReportUtils.fail("Interrupted during execution: " + e.getMessage());
            LogUtils.error("Interrupted during execution: " + e.getMessage());
        } catch (Exception e) {
            ReportUtils.fail(e.getMessage());
            LogUtils.error(e.getMessage());
        }

    }

    private void shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
