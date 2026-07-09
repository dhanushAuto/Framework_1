package ai.analyzer;

import ai.service.AIService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FlakyDetector {

    private final AIService aiService = new AIService();

    public FlakinessAnalysis detectFlakiness(String testName, List<TestExecution> executions) throws Exception {
        
        String prompt = buildFlakinessPrompt(testName, executions);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseFlakinessAnalysis(aiResponse);
    }

    public List<String> identifyFlakyTests(List<String> allTests, Map<String, List<TestExecution>> executionHistory) throws Exception {
        
        String prompt = buildFlakyTestIdentificationPrompt(allTests, executionHistory);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseFlakyTests(aiResponse);
    }

    public String suggestStabilizationFix(String testName, String flakinessPattern) throws Exception {
        
        String prompt = buildStabilizationPrompt(testName, flakinessPattern);
        
        return aiService.ask(prompt);
    }

    private String buildFlakinessPrompt(String testName, List<TestExecution> executions) {
        StringBuilder execStr = new StringBuilder();
        for (TestExecution exec : executions) {
            execStr.append("Run ").append(exec.runNumber).append(": ")
                   .append(exec.status).append(" | ")
                   .append("Duration: ").append(exec.duration).append("ms | ")
                   .append("Error: ").append(exec.error != null ? exec.error : "None").append("\n");
        }
        
        long passCount = executions.stream().filter(e -> e.status.equals("PASS")).count();
        long failCount = executions.stream().filter(e -> e.status.equals("FAIL")).count();
        double passRate = (double) passCount / executions.size() * 100;
        
        return """
            You are a Test Flakiness Detection Expert.
            
            Analyze the test execution history to determine if the test is flaky.
            
            Test Name: %s
            Total Executions: %d
            Pass Count: %d
            Fail Count: %d
            Pass Rate: %.1f%%
            
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
            """.formatted(testName, executions.size(), passCount, failCount, passRate, execStr);
    }

    private String buildFlakyTestIdentificationPrompt(List<String> allTests, Map<String, List<TestExecution>> executionHistory) {
        StringBuilder historyStr = new StringBuilder();
        executionHistory.forEach((test, executions) -> {
            long passCount = executions.stream().filter(e -> e.status.equals("PASS")).count();
            long failCount = executions.stream().filter(e -> e.status.equals("FAIL")).count();
            double passRate = (double) passCount / executions.size() * 100;
            historyStr.append(test).append(" | ")
                     .append("PassRate: ").append(String.format("%.1f%%", passRate)).append(" | ")
                     .append("TotalRuns: ").append(executions.size()).append("\n");
        });
        
        return """
            You are a Flaky Test Identification Expert.
            
            Identify which tests are flaky based on execution history.
            
            Execution History:
            %s
            
            A test is considered flaky if pass rate is below 95% with at least 5 executions.
            
            Return a comma-separated list of flaky test names only.
            """.formatted(historyStr);
    }

    private String buildStabilizationPrompt(String testName, String flakinessPattern) {
        return """
            You are a Test Stabilization Expert.
            
            Suggest specific fixes to stabilize the flaky test.
            
            Test Name: %s
            Flakiness Pattern: %s
            
            Provide specific code-level fixes including:
            1. Wait strategies
            2. Retry mechanisms
            3. Test isolation improvements
            4. Dependency management
            5. Configuration changes
            
            Return detailed fix recommendations.
            """.formatted(testName, flakinessPattern);
    }

    private FlakinessAnalysis parseFlakinessAnalysis(String aiResponse) {
        FlakinessAnalysis analysis = new FlakinessAnalysis();
        
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("IS_FLAKY:")) {
                analysis.setFlaky(Boolean.parseBoolean(line.split(":")[1].trim()));
            } else if (line.startsWith("FLAKINESS_SCORE:")) {
                analysis.setFlakinessScore(Double.parseDouble(line.split(":")[1].trim()));
            } else if (line.startsWith("FLAKINESS_TYPE:")) {
                analysis.setFlakinessType(line.split(":")[1].trim());
            } else if (line.startsWith("STABILIZATION_RECOMMENDATION:")) {
                analysis.setStabilizationRecommendation(line.split(":")[1].trim());
            } else if (line.startsWith("CONFIDENCE:")) {
                analysis.setConfidence(Double.parseDouble(line.split(":")[1].trim()));
            }
        }
        
        return analysis;
    }

    private List<String> parseFlakyTests(String aiResponse) {
        String[] tests = aiResponse.split(",");
        List<String> result = new ArrayList<>();
        for (String test : tests) {
            result.add(test.trim());
        }
        return result;
    }

    public static class FlakinessAnalysis {
        private boolean isFlaky = false;
        private double flakinessScore = 0.0;
        private String flakinessType = "Unknown";
        private String stabilizationRecommendation = "";
        private double confidence = 0.0;

        public boolean isFlaky() { return isFlaky; }
        public void setFlaky(boolean flaky) { isFlaky = flaky; }
        
        public double getFlakinessScore() { return flakinessScore; }
        public void setFlakinessScore(double flakinessScore) { this.flakinessScore = flakinessScore; }
        
        public String getFlakinessType() { return flakinessType; }
        public void setFlakinessType(String flakinessType) { this.flakinessType = flakinessType; }
        
        public String getStabilizationRecommendation() { return stabilizationRecommendation; }
        public void setStabilizationRecommendation(String stabilizationRecommendation) { this.stabilizationRecommendation = stabilizationRecommendation; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }

    public static class TestExecution {
        public int runNumber;
        public String status;
        public long duration;
        public String error;
    }
}
