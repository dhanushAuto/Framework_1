package ai.prompt;


public class PromptBuilder {

    // Failure Analysis Prompts - delegates to FailurePromptBuilder
    public static String buildFailurePrompt(String testName, String exception, String stackTrace) {
        return FailurePromptBuilder.buildFailurePrompt(testName, exception, stackTrace);
    }

    // Self-Healing Prompts - delegates to HealingPromptBuilder
    public static String buildSelfHealingPrompt(
            String originalLocator,
            String elementDescription,
            String pageContext,
            String errorMessage) {
        return HealingPromptBuilder.buildSelfHealingPrompt(originalLocator, elementDescription, pageContext, errorMessage);
    }

    // Test Impact & Generation Prompts - delegates to TestPromptBuilder
    public static String buildTestImpactPrompt(String changedFiles, String allTests) {
        return TestPromptBuilder.buildTestImpactPrompt(changedFiles, allTests);
    }

    public static String buildTestCaseGenerationPrompt(String requirement, String testType) {
        return TestPromptBuilder.buildTestCaseGenerationPrompt(requirement, testType);
    }

    public static String buildTraceabilityPrompt(String requirements, String testCases) {
        return TestPromptBuilder.buildTraceabilityPrompt(requirements, testCases);
    }

    // Performance Prompts - delegates to PerformancePromptBuilder
    public static String buildPerformanceAnalysisPrompt(String currentMetrics, String baselineMetrics) {
        return PerformancePromptBuilder.buildPerformanceAnalysisPrompt(currentMetrics, baselineMetrics);
    }

    // Release & Defect Prediction Prompts - delegates to ReleasePromptBuilder
    public static String buildReleaseReadinessPrompt(
            String version,
            String testResults,
            int defectCount,
            int criticalDefectCount,
            double codeCoverage,
            String performanceMetrics,
            String securityScanResults) {
        return ReleasePromptBuilder.buildReleaseReadinessPrompt(
            version, testResults, defectCount, criticalDefectCount,
            codeCoverage, performanceMetrics, securityScanResults);
    }

    public static String buildDefectPredictionPrompt(
            String changedFiles,
            String complexity,
            String developerExperience,
            double historicalDefectRate,
            double codeCoverage,
            String reviewStatus) {
        return ReleasePromptBuilder.buildDefectPredictionPrompt(
            changedFiles, complexity, developerExperience,
            historicalDefectRate, codeCoverage, reviewStatus);
    }

    // Analysis Prompts (Flaky, Bug, Log) - delegates to AnalysisPromptBuilder
    public static String buildFlakinessDetectionPrompt(String testName, String executionHistory) {
        return AnalysisPromptBuilder.buildFlakinessDetectionPrompt(testName, executionHistory);
    }

    public static String buildBugClassificationPrompt(String errorMessage, String stackTrace, String testContext) {
        return AnalysisPromptBuilder.buildBugClassificationPrompt(errorMessage, stackTrace, testContext);
    }

    public static String buildLogSummarizationPrompt(String logContent, String context) {
        return AnalysisPromptBuilder.buildLogSummarizationPrompt(logContent, context);
    }

    // Additional prompts for backward compatibility
    public static String buildTestPrioritizationPrompt(
            String changedFiles,
            String testMetadata,
            String timeConstraint) {

        return """
            You are a Test Execution Optimization Expert.
            
            Prioritize the following tests based on code changes, historical data, and time constraints.
            
            Changed Files:
            %s
            
            Time Constraint: %s
            
            Available Tests with Metadata:
            %s
            
            Provide:
            1. PRIORITY_1: Critical tests that MUST run (high impact, high failure rate)
            2. PRIORITY_2: Important tests that SHOULD run (medium impact)
            3. PRIORITY_3: Optional tests that CAN run if time permits (low impact)
            4. ESTIMATED_TIME: Total estimated execution time
            
            Format:
            PRIORITY_1: test1, test2, test3
            PRIORITY_2: test4, test5
            PRIORITY_3: test6, test7
            ESTIMATED_TIME: X minutes
            """.formatted(changedFiles, timeConstraint, testMetadata);
    }
}