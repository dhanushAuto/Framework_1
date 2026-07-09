package ai.prompt;

public class AnalysisPromptBuilder {

    public static String buildFlakinessDetectionPrompt(String testName, String executionHistory) {
        return """
            You are a Test Flakiness Detection Expert.
            
            Analyze the test execution history to determine if the test is flaky.
            
            Test Name: %s
            
            Execution History:
            %s
            
            Provide:
            1. IS_FLAKY: true/false
            2. FLAKINESS_SCORE: 0.0-1.0
            3. FLAKINESS_TYPE: Timing/Dependency/Environment/Data/Concurrency
            4. STABILIZATION_RECOMMENDATION: Specific fix recommendation
            5. CONFIDENCE: 0.0-1.0
            
            Format:
            IS_FLAKY: true
            FLAKINESS_SCORE: 0.75
            FLAKINESS_TYPE: Timing
            STABILIZATION_RECOMMENDATION: Add explicit wait for element
            CONFIDENCE: 0.90
            """.formatted(testName, executionHistory);
    }

    public static String buildBugClassificationPrompt(String errorMessage, String stackTrace, String testContext) {
        return """
            You are a Bug Classification Expert.
            
            Classify the following bug based on the error information.
            
            Error Message: %s
            Stack Trace: %s
            Test Context: %s
            
            Provide:
            1. BUG_TYPE: UI/API/DATABASE/NETWORK/PERFORMANCE/SECURITY/LOGIC
            2. ROOT_CAUSE_CATEGORY: Code/Config/Environment/Data/Timing
            3. AFFECTED_LAYER: Frontend/Backend/Integration/Infrastructure
            4. REPRODUCIBILITY: Always/Intermittent/Rare
            5. ESCALATION_LEVEL: L1/L2/L3
            
            Format:
            BUG_TYPE: API
            ROOT_CAUSE_CATEGORY: Code
            AFFECTED_LAYER: Backend
            REPRODUCIBILITY: Always
            ESCALATION_LEVEL: L2
            """.formatted(errorMessage, stackTrace, testContext);
    }

    public static String buildLogSummarizationPrompt(String logContent, String context) {
        return """
            You are a Log Analysis Expert.
            
            Summarize the following log content.
            
            Context: %s
            
            Log Content:
            %s
            
            Provide:
            1. SUMMARY: Brief summary of the log
            2. KEY_EVENTS: List of key events
            3. ERROR_COUNT: Number of errors
            4. WARNING_COUNT: Number of warnings
            5. OVERALL_STATUS: HEALTHY/DEGRADED/CRITICAL
            
            Format:
            SUMMARY: [Summary text]
            KEY_EVENTS: event1, event2, event3
            ERROR_COUNT: 5
            WARNING_COUNT: 12
            OVERALL_STATUS: DEGRADED
            """.formatted(context, logContent);
    }
}
