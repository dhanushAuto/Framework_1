package ai.impact;

import ai.service.AIService;
import ai.performance.PerformanceRegressionDetector.PerformanceRecommendation;
import ai.performance.PerformanceRegressionDetector.PerformanceMetrics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestImpactAnalyzer {

    private final AIService aiService = new AIService();

    public List<PerformanceRecommendation> getOptimizationRecommendations(Map<String, PerformanceMetrics> metrics) throws Exception {
        String prompt = buildOptimizationPrompt(metrics);
        String aiResponse = aiService.ask(prompt);
        return parseRecommendations(aiResponse);
    }

    public List<String> getAffectedTests(String changedComponent, Map<String, List<String>> testDependencyMap) throws Exception {
        List<String> affected = getAffectedTestsDeterministic(changedComponent, testDependencyMap);
        if (!affected.isEmpty()) return affected;

        String prompt = buildAffectedTestsPrompt(changedComponent, testDependencyMap);
        String aiResponse = aiService.ask(prompt);
        return parseAffectedTests(aiResponse);
    }

    private List<String> getAffectedTestsDeterministic(String changedComponent, Map<String, List<String>> testDependencyMap) {
        List<String> affected = new ArrayList<>();
        String className = extractClassName(changedComponent);
        testDependencyMap.forEach((test, dependencies) -> {
            if (dependencies.contains(className)) affected.add(test);
        });
        return affected;
    }

    public double calculateRiskScore(List<String> changedFiles, List<String> failedTests, List<String> criticalTests) throws Exception {
        double deterministicScore = calculateRiskScoreDeterministic(changedFiles, failedTests, criticalTests);
        double aiScore = calculateRiskScoreAI(changedFiles, failedTests, criticalTests);
        return (deterministicScore * 0.7) + (aiScore * 0.3);
    }

    private double calculateRiskScoreDeterministic(List<String> changedFiles, List<String> failedTests, List<String> criticalTests) {
        double score = 0.0;
        for (String file : changedFiles) {
            score += isCriticalComponent(file) ? 0.3 : 0.1;
        }
        for (String test : failedTests) {
            score += criticalTests.contains(test) ? 0.2 : 0.1;
        }
        return Math.min(score, 1.0);
    }

    private double calculateRiskScoreAI(List<String> changedFiles, List<String> failedTests, List<String> criticalTests) throws Exception {
        String prompt = buildRiskScorePrompt(changedFiles, failedTests, criticalTests);
        String aiResponse = aiService.ask(prompt);
        return parseRiskScore(aiResponse);
    }

    public Map<String, String> categorizeImpact(List<String> changedFiles) throws Exception {
        Map<String, String> deterministicCats = categorizeImpactDeterministic(changedFiles);
        Map<String, String> aiCats = categorizeImpactAI(changedFiles);
        Map<String, String> combined = new HashMap<>(deterministicCats);
        combined.putAll(aiCats);
        return combined;
    }

    private Map<String, String> categorizeImpactDeterministic(List<String> changedFiles) {
        Map<String, String> result = new HashMap<>();
        for (String file : changedFiles) {
            if (file.contains("/api/")) result.put(file, "API");
            else if (file.contains("/ui/")) result.put(file, "UI");
            else result.put(file, "BUSINESS");
        }
        return result;
    }

    private Map<String, String> categorizeImpactAI(List<String> changedFiles) throws Exception {
        String prompt = buildImpactCategorizationPrompt(changedFiles);
        String aiResponse = aiService.ask(prompt);
        return parseImpactCategorization(aiResponse);
    }

    private String buildOptimizationPrompt(Map<String, PerformanceMetrics> metrics) {
        return "Optimize performance...";
    }

    private String buildAffectedTestsPrompt(String changedComponent, Map<String, List<String>> testDependencyMap) {
        return "Find affected tests for " + changedComponent;
    }

    private String buildRiskScorePrompt(List<String> changedFiles, List<String> failedTests, List<String> criticalTests) {
        return "Calculate risk score...";
    }

    private String buildImpactCategorizationPrompt(List<String> changedFiles) {
        return "Categorize impact...";
    }

    private List<PerformanceRecommendation> parseRecommendations(String aiResponse) {
        return new ArrayList<>();
    }

    private List<String> parseAffectedTests(String aiResponse) {
        List<String> result = new ArrayList<>();
        for (String s : aiResponse.split(",")) result.add(s.trim());
        return result;
    }

    private double parseRiskScore(String aiResponse) {
        try { return Double.parseDouble(aiResponse.trim()); } catch (Exception e) { return 0.5; }
    }

    private Map<String, String> parseImpactCategorization(String aiResponse) {
        Map<String, String> result = new HashMap<>();
        for (String line : aiResponse.split("\n")) {
            String[] parts = line.split(":");
            if (parts.length == 2) result.put(parts[0].trim(), parts[1].trim());
        }
        return result;
    }

    private String extractClassName(String filePath) {
        int lastSlash = Math.max(filePath.lastIndexOf("/"), filePath.lastIndexOf("\\"));
        String fileName = filePath.substring(lastSlash + 1);
        int dot = fileName.lastIndexOf(".");
        return dot == -1 ? fileName : fileName.substring(0, dot);
    }

    private boolean isCriticalComponent(String filePath) {
        String lower = filePath.toLowerCase();
        return lower.contains("auth") || lower.contains("payment") || lower.contains("security");
    }

    public static class ImpactAnalysisResult {
        private List<String> highImpactTests = new ArrayList<>();
        private List<String> mediumImpactTests = new ArrayList<>();
        private List<String> lowImpactTests = new ArrayList<>();
        private String riskLevel = "MEDIUM";

        public List<String> getHighImpactTests() { return highImpactTests; }
        public void setHighImpactTests(List<String> l) { this.highImpactTests = l; }
        public List<String> getMediumImpactTests() { return mediumImpactTests; }
        public void setMediumImpactTests(List<String> l) { this.mediumImpactTests = l; }
        public List<String> getLowImpactTests() { return lowImpactTests; }
        public void setLowImpactTests(List<String> l) { this.lowImpactTests = l; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String r) { this.riskLevel = r; }
    }
}
