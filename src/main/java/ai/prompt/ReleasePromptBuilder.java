package ai.prompt;

public class ReleasePromptBuilder {

    public static String buildReleaseReadinessPrompt(
            String version,
            String testResults,
            int defectCount,
            int criticalDefectCount,
            double codeCoverage,
            String performanceMetrics,
            String securityScanResults) {

        return """
            You are a Release Readiness Assessment Expert.
            
            Assess the readiness of the release based on the following context.
            
            Release Version: %s
            Test Results: %s
            Defect Count: %d
            Critical Defects: %d
            Code Coverage: %.1f%%
            Performance Metrics: %s
            Security Scan Results: %s
            
            Provide:
            1. READINESS_SCORE: 0-100
            2. STATUS: READY/CONDITIONAL/NOT_READY
            3. BLOCKERS: List of blocking issues
            4. RECOMMENDATIONS: Action items before release
            5. RISK_LEVEL: LOW/MEDIUM/HIGH/CRITICAL
            
            Format:
            READINESS_SCORE: 85
            STATUS: CONDITIONAL
            BLOCKERS: issue1, issue2
            RECOMMENDATIONS: recommendation1, recommendation2
            RISK_LEVEL: MEDIUM
            """.formatted(version, testResults, defectCount, criticalDefectCount, codeCoverage, performanceMetrics, securityScanResults);
    }

    public static String buildDefectPredictionPrompt(
            String changedFiles,
            String complexity,
            String developerExperience,
            double historicalDefectRate,
            double codeCoverage,
            String reviewStatus) {

        return """
            You are a Defect Prediction Expert.
            
            Predict the likelihood of defects in the following code changes.
            
            Changed Files:
            %s
            
            Change Complexity: %s
            Developer Experience: %s
            Historical Defect Rate: %.1f%%
            Code Coverage: %.1f%%
            Review Status: %s
            
            Provide:
            1. DEFECT_PROBABILITY: 0.0-1.0
            2. EXPECTED_DEFECT_COUNT: Estimated number of defects
            3. HIGH_RISK_FILES: Files most likely to contain defects
            4. DEFECT_TYPES: Likely defect types (logic, UI, API, performance, security)
            5. CONFIDENCE: 0.0-1.0
            
            Format:
            DEFECT_PROBABILITY: 0.65
            EXPECTED_DEFECT_COUNT: 3
            HIGH_RISK_FILES: file1.java, file2.java
            DEFECT_TYPES: logic, API
            CONFIDENCE: 0.80
            """.formatted(changedFiles, complexity, developerExperience, historicalDefectRate, codeCoverage, reviewStatus);
    }

    public static String buildDefectPredictionWithMetricsPrompt(
            String changedFiles,
            double cyclomaticComplexity,
            int gitChurn,
            double historicalFailureRate,
            double codeCoverage,
            double mutationScore,
            String aiPrediction) {

        return """
            You are a Hybrid Defect Prediction Expert.
            
            Combine deterministic metrics with AI prediction for accurate defect assessment.
            
            Changed Files:
            %s
            
            Deterministic Metrics:
            - Cyclomatic Complexity: %.1f
            - Git Churn: %d commits
            - Historical Failure Rate: %.1f%%
            - Code Coverage: %.1f%%
            - Mutation Score: %.1f%%
            
            AI Prediction:
            %s
            
            Provide:
            1. FINAL_DEFECT_PROBABILITY: 0.0-1.0 (weighted combination of metrics and AI)
            2. CONFIDENCE: 0.0-1.0
            3. RISK_FACTORS: Key factors influencing the decision
            4. RECOMMENDATIONS: Specific actions to reduce risk
            
            Format:
            FINAL_DEFECT_PROBABILITY: 0.72
            CONFIDENCE: 0.85
            RISK_FACTORS: factor1, factor2, factor3
            RECOMMENDATIONS: action1, action2
            """.formatted(changedFiles, cyclomaticComplexity, gitChurn, historicalFailureRate, codeCoverage, mutationScore, aiPrediction);
    }
}
