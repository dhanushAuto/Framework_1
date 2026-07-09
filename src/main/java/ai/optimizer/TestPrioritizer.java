package ai.optimizer;

import ai.service.AIService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestPrioritizer {

    private final AIService aiService = new AIService();

    public PrioritizedTestSuite prioritizeTests(List<String> allTests, Map<String, TestMetadata> testMetadata, 
                                                 List<String> changedFiles, String timeConstraint) throws Exception {
        
        String prompt = buildPrioritizationPrompt(allTests, testMetadata, changedFiles, timeConstraint);
        
        String aiResponse = aiService.ask(prompt);
        
        return parsePrioritizedSuite(aiResponse, allTests);
    }

    public List<String> selectSmartTests(List<String> allTests, Map<String, TestMetadata> testMetadata, 
                                         double riskThreshold) throws Exception {
        
        String prompt = buildSmartSelectionPrompt(allTests, testMetadata, riskThreshold);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseSelectedTests(aiResponse);
    }

    public Map<String, Integer> assignExecutionOrder(List<String> tests, Map<String, TestMetadata> testMetadata) throws Exception {
        
        String prompt = buildExecutionOrderPrompt(tests, testMetadata);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseExecutionOrder(aiResponse);
    }

    public List<String> getParallelizableTests(List<String> tests, Map<String, TestMetadata> testMetadata) throws Exception {
        
        String prompt = buildParallelizationPrompt(tests, testMetadata);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseParallelizableTests(aiResponse);
    }

    private String buildPrioritizationPrompt(List<String> allTests, Map<String, TestMetadata> testMetadata, 
                                              List<String> changedFiles, String timeConstraint) {
        StringBuilder metadataStr = new StringBuilder();
        testMetadata.forEach((test, meta) -> 
            metadataStr.append(test).append(" | ")
                      .append("Priority:").append(meta.priority).append(" | ")
                      .append("Duration:").append(avgDuration(meta.avgDuration)).append(" | ")
                      .append("FailureRate:").append(meta.failureRate).append(" | ")
                      .append("LastRun:").append(meta.lastRun).append("\n")
        );
        
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
            """.formatted(String.join("\n", changedFiles), timeConstraint, metadataStr);
    }

    private String buildSmartSelectionPrompt(List<String> allTests, Map<String, TestMetadata> testMetadata, double riskThreshold) {
        StringBuilder metadataStr = new StringBuilder();
        testMetadata.forEach((test, meta) -> 
            metadataStr.append(test).append(" | ")
                      .append("Risk:").append(calculateRisk(meta)).append(" | ")
                      .append("Impact:").append(meta.impact).append("\n")
        );
        
        return """
            You are a Test Selection Expert.
            
            Select tests that meet or exceed the risk threshold.
            
            Risk Threshold: %.2f
            
            Available Tests:
            %s
            
            Return a comma-separated list of selected test names only.
            """.formatted(riskThreshold, metadataStr);
    }

    private String buildExecutionOrderPrompt(List<String> tests, Map<String, TestMetadata> testMetadata) {
        StringBuilder metadataStr = new StringBuilder();
        testMetadata.forEach((test, meta) -> 
            metadataStr.append(test).append(" | ")
                      .append("Dependencies:").append(String.join(",", meta.dependencies)).append(" | ")
                      .append("Duration:").append(avgDuration(meta.avgDuration)).append("\n")
        );
        
        return """
            You are a Test Execution Order Expert.
            
            Determine the optimal execution order considering dependencies and duration.
            
            Tests:
            %s
            
            Return the execution order as: test1=1, test2=2, test3=3
            (where number is the execution order)
            """.formatted(metadataStr);
    }

    private String buildParallelizationPrompt(List<String> tests, Map<String, TestMetadata> testMetadata) {
        StringBuilder metadataStr = new StringBuilder();
        testMetadata.forEach((test, meta) -> 
            metadataStr.append(test).append(" | ")
                      .append("SharedResources:").append(String.join(",", meta.sharedResources)).append(" | ")
                      .append("Independent:").append(meta.isIndependent).append("\n")
        );
        
        return """
            You are a Test Parallelization Expert.
            
            Identify tests that can run in parallel without conflicts.
            
            Tests:
            %s
            
            Return a comma-separated list of parallelizable test names only.
            """.formatted(metadataStr);
    }

    private PrioritizedTestSuite parsePrioritizedSuite(String aiResponse, List<String> allTests) {
        PrioritizedTestSuite suite = new PrioritizedTestSuite();
        
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("PRIORITY_1:")) {
                suite.setPriority1(extractTests(line, allTests));
            } else if (line.startsWith("PRIORITY_2:")) {
                suite.setPriority2(extractTests(line, allTests));
            } else if (line.startsWith("PRIORITY_3:")) {
                suite.setPriority3(extractTests(line, allTests));
            } else if (line.startsWith("ESTIMATED_TIME:")) {
                suite.setEstimatedTime(line.split(":")[1].trim());
            }
        }
        
        return suite;
    }

    private List<String> extractTests(String line, List<String> allTests) {
        String[] parts = line.split(":");
        if (parts.length < 2) return new ArrayList<>();
        
        String[] testNames = parts[1].trim().split(",");
        List<String> tests = new ArrayList<>();
        for (String test : testNames) {
            String trimmed = test.trim();
            if (allTests.contains(trimmed)) {
                tests.add(trimmed);
            }
        }
        return tests;
    }

    private List<String> parseSelectedTests(String aiResponse) {
        String[] tests = aiResponse.split(",");
        List<String> result = new ArrayList<>();
        for (String test : tests) {
            result.add(test.trim());
        }
        return result;
    }

    private Map<String, Integer> parseExecutionOrder(String aiResponse) {
        Map<String, Integer> order = new HashMap<>();
        String[] items = aiResponse.split(",");
        for (String item : items) {
            String[] parts = item.trim().split("=");
            if (parts.length == 2) {
                order.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }
        }
        return order;
    }

    private List<String> parseParallelizableTests(String aiResponse) {
        String[] tests = aiResponse.split(",");
        List<String> result = new ArrayList<>();
        for (String test : tests) {
            result.add(test.trim());
        }
        return result;
    }

    private double calculateRisk(TestMetadata meta) {
        return (meta.failureRate * 0.4) + 
               (meta.priority.equals("HIGH") ? 0.3 : meta.priority.equals("MEDIUM") ? 0.2 : 0.1) +
               (meta.impact.equals("HIGH") ? 0.3 : meta.impact.equals("MEDIUM") ? 0.2 : 0.1);
    }

    private String avgDuration(long duration) {
        return duration > 60000 ? (duration / 60000) + "m" : duration + "s";
    }

    public static class PrioritizedTestSuite {
        private List<String> priority1 = new ArrayList<>();
        private List<String> priority2 = new ArrayList<>();
        private List<String> priority3 = new ArrayList<>();
        private String estimatedTime = "0m";

        public List<String> getPriority1() { return priority1; }
        public void setPriority1(List<String> priority1) { this.priority1 = priority1; }
        
        public List<String> getPriority2() { return priority2; }
        public void setPriority2(List<String> priority2) { this.priority2 = priority2; }
        
        public List<String> getPriority3() { return priority3; }
        public void setPriority3(List<String> priority3) { this.priority3 = priority3; }
        
        public String getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }
    }

    public static class TestMetadata {
        public String priority = "MEDIUM";
        public String impact = "MEDIUM";
        public double failureRate = 0.0;
        public long avgDuration = 0;
        public String lastRun = "";
        public List<String> dependencies = new ArrayList<>();
        public List<String> sharedResources = new ArrayList<>();
        public boolean isIndependent = true;
    }
}
