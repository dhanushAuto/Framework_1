package ai.optimizer;

import ai.service.AIService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestPrioritizer {

    private final AIService aiService = new AIService();

    public PrioritizedTestSuite prioritizeTests(List<String> allTests, Map<String, TestMetadata> testMetadata, List<String> changedFiles, String timeConstraint) throws Exception {
        String prompt = buildPrioritizationPrompt(allTests, testMetadata, changedFiles, timeConstraint);
        String aiResponse = aiService.ask(prompt);
        return parsePrioritizedSuite(aiResponse, allTests);
    }

    public List<String> selectSmartTests(List<String> allTests, Map<String, TestMetadata> testMetadata, double riskThreshold) throws Exception {
        String prompt = buildSmartSelectionPrompt(allTests, testMetadata, riskThreshold);
        String aiResponse = aiService.ask(prompt);
        return parseSelectedTests(aiResponse);
    }

    public List<String> getParallelizableTests(List<String> tests, Map<String, TestMetadata> testMetadata) throws Exception {
        String prompt = buildParallelizationPrompt(tests, testMetadata);
        String aiResponse = aiService.ask(prompt);
        return parseParallelizableTests(aiResponse);
    }

    private String buildPrioritizationPrompt(List<String> allTests, Map<String, TestMetadata> testMetadata, List<String> changedFiles, String timeConstraint) {
        return "Prioritize tests based on changes: " + String.join(", ", changedFiles);
    }

    private String buildSmartSelectionPrompt(List<String> allTests, Map<String, TestMetadata> testMetadata, double riskThreshold) {
        return "Select tests with risk threshold: " + riskThreshold;
    }

    private String buildParallelizationPrompt(List<String> tests, Map<String, TestMetadata> testMetadata) {
        return "Identify parallelizable tests among: " + String.join(", ", tests);
    }

    private PrioritizedTestSuite parsePrioritizedSuite(String aiResponse, List<String> allTests) {
        return new PrioritizedTestSuite();
    }

    private List<String> parseSelectedTests(String aiResponse) {
        List<String> result = new ArrayList<>();
        for (String s : aiResponse.split(",")) result.add(s.trim());
        return result;
    }

    private List<String> parseParallelizableTests(String aiResponse) {
        List<String> result = new ArrayList<>();
        for (String s : aiResponse.split(",")) result.add(s.trim());
        return result;
    }

    public static class PrioritizedTestSuite {
        private List<String> priority1 = new ArrayList<>();
        private List<String> priority2 = new ArrayList<>();
        private List<String> priority3 = new ArrayList<>();
        private String estimatedTime = "0m";

        public List<String> getPriority1() { return priority1; }
        public void setPriority1(List<String> l) { this.priority1 = l; }
        public List<String> getPriority2() { return priority2; }
        public void setPriority2(List<String> l) { this.priority2 = l; }
        public List<String> getPriority3() { return priority3; }
        public void setPriority3(List<String> l) { this.priority3 = l; }
        public String getEstimatedTime() { return estimatedTime; }
        public void setEstimatedTime(String t) { this.estimatedTime = t; }
    }

    public static class TestMetadata {
        public long duration = 0;
        public double failureRate = 0.0;
        public boolean isCritical = false;
        public List<String> dependencies = new ArrayList<>();
    }
}
