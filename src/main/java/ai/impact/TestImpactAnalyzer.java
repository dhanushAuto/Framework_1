package ai.impact;

import ai.service.AIService;
import ai.prompt.TestPromptBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestImpactAnalyzer {

    private final AIService aiService = new AIService();

    public ImpactAnalysisResult analyzeImpact(List<String> changedFiles, List<String> allTests) throws Exception {
        
        String prompt = TestPromptBuilder.buildTestImpactPrompt(
            String.join("\n", changedFiles), 
            String.join("\n", allTests)
        );
        
        String aiResponse = aiService.ask(prompt);
        
        return parseImpactAnalysis(aiResponse, allTests);
    }

    public ImpactAnalysisResult analyzeImpactHybrid(
            List<String> changedFiles, 
            List<String> allTests,
            Map<String, List<String>> dependencyGraph,
            Map<String, Double> testFailureRates) throws Exception {
        
        // Deterministic analysis based on dependency graph
        ImpactAnalysisResult deterministicResult = analyzeImpactDeterministic(
            changedFiles, allTests, dependencyGraph, testFailureRates);
        
        // AI analysis
        ImpactAnalysisResult aiResult = analyzeImpact(changedFiles, allTests);
        
        // Combine results with weighted approach
        return combineResults(deterministicResult, aiResult, 0.6);
    }

    public ImpactAnalysisResult analyzeImpactDeterministic(
            List<String> changedFiles,
            List<String> allTests,
            Map<String, List<String>> dependencyGraph,
            Map<String, Double> testFailureRates) {
        
        ImpactAnalysisResult result = new ImpactAnalysisResult();
        Set<String> highImpactSet = new HashSet<>();
        Set<String> mediumImpactSet = new HashSet<>();
        
        // Analyze based on dependency graph
        for (String changedFile : changedFiles) {
            String className = extractClassName(changedFile);
            
            for (String test : allTests) {
                List<String> dependencies = dependencyGraph.getOrDefault(test, new ArrayList<>());
                
                if (dependencies.contains(className)) {
                    double failureRate = testFailureRates.getOrDefault(test, 0.0);
                    
                    if (failureRate > 0.1 || isCriticalComponent(changedFile)) {
                        highImpactSet.add(test);
                    } else {
                        mediumImpactSet.add(test);
                    }
                }
            }
        }
        
        result.setHighImpactTests(new ArrayList<>(highImpactSet));
        result.setMediumImpactTests(new ArrayList<>(mediumImpactSet));
        result.setLowImpactTests(new ArrayList<>());
        result.setRiskLevel(calculateRiskLevel(highImpactSet.size(), mediumImpactSet.size(), allTests.size()));
        
        return result;
    }

    public List<String> getAffectedTests(String changedComponent, Map<String, List<String>> testDependencyMap) throws Exception {
        
        // First try deterministic approach
        List<String> deterministicAffected = getAffectedTestsDeterministic(changedComponent, testDependencyMap);
        
        if (!deterministicAffected.isEmpty()) {
            return deterministicAffected;
        }
        
        // Fallback to AI if deterministic doesn't find matches
        String prompt = buildAffectedTestsPrompt(changedComponent, testDependencyMap);
        String aiResponse = aiService.ask(prompt);
        
        return parseAffectedTests(aiResponse);
    }

    private List<String> getAffectedTestsDeterministic(String changedComponent, Map<String, List<String>> testDependencyMap) {
        List<String> affected = new ArrayList<>();
        String className = extractClassName(changedComponent);
        
        testDependencyMap.forEach((test, dependencies) -> {
            if (dependencies.contains(className)) {
                affected.add(test);
            }
        });
        
        return affected;
    }

    public double calculateRiskScore(List<String> changedFiles, List<String> failedTests, List<String> criticalTests) throws Exception {
        
        // Deterministic risk calculation
        double deterministicScore = calculateRiskScoreDeterministic(changedFiles, failedTests, criticalTests);
        
        // AI risk calculation
        double aiScore = calculateRiskScoreAI(changedFiles, failedTests, criticalTests);
        
        // Weighted combination (70% deterministic, 30% AI)
        return (deterministicScore * 0.7) + (aiScore * 0.3);
    }

    private double calculateRiskScoreDeterministic(List<String> changedFiles, List<String> failedTests, List<String> criticalTests) {
        double score = 0.0;
        
        // Weight for critical components
        for (String file : changedFiles) {
            if (isCriticalComponent(file)) {
                score += 0.3;
            } else {
                score += 0.1;
            }
        }
        
        // Weight for previously failed tests
        for (String test : failedTests) {
            if (criticalTests.contains(test)) {
                score += 0.2;
            } else {
                score += 0.1;
            }
        }
        
        // Normalize to 0-1 range
        return Math.min(score, 1.0);
    }

    private double calculateRiskScoreAI(List<String> changedFiles, List<String> failedTests, List<String> criticalTests) throws Exception {
        String prompt = buildRiskScorePrompt(changedFiles, failedTests, criticalTests);
        String aiResponse = aiService.ask(prompt);
        return parseRiskScore(aiResponse);
    }

    public Map<String, String> categorizeImpact(List<String> changedFiles) throws Exception {
        
        // Deterministic categorization
        Map<String, String> deterministicCats = categorizeImpactDeterministic(changedFiles);
        
        // AI categorization
        Map<String, String> aiCats = categorizeImpactAI(changedFiles);
        
        // Combine with preference for deterministic
        Map<String, String> result = new HashMap<>(deterministicCats);
        
        // Use AI for files not categorized deterministically
        for (String file : changedFiles) {
            if (!result.containsKey(file) && aiCats.containsKey(file)) {
                result.put(file, aiCats.get(file));
            }
        }
        
        return result;
    }

    private Map<String, String> categorizeImpactDeterministic(List<String> changedFiles) {
        Map<String, String> result = new HashMap<>();
        
        for (String file : changedFiles) {
            String lowerFile = file.toLowerCase();
            
            if (lowerFile.contains("controller") || lowerFile.contains("api") || lowerFile.contains("rest")) {
                result.put(file, "API");
            } else if (lowerFile.contains("service") || lowerFile.contains("business")) {
                result.put(file, "BUSINESS");
            } else if (lowerFile.contains("repository") || lowerFile.contains("dao") || lowerFile.contains("entity")) {
                result.put(file, "DATA");
            } else if (lowerFile.contains("config") || lowerFile.contains("properties") || lowerFile.contains("yaml")) {
                result.put(file, "CONFIG");
            } else if (lowerFile.contains("page") || lowerFile.contains("component") || lowerFile.contains("view")) {
                result.put(file, "UI");
            }
        }
        
        return result;
    }

    private Map<String, String> categorizeImpactAI(List<String> changedFiles) throws Exception {
        String prompt = buildImpactCategorizationPrompt(changedFiles);
        String aiResponse = aiService.ask(prompt);
        return parseImpactCategorization(aiResponse);
    }

    private String buildAffectedTestsPrompt(String changedComponent, Map<String, List<String>> testDependencyMap) {
        StringBuilder dependencies = new StringBuilder();
        testDependencyMap.forEach((test, deps) -> 
            dependencies.append(test).append(": ").append(String.join(", ", deps)).append("\n")
        );
        
        return """
            You are a Test Impact Analysis Expert.
            
            Given the changed component and test dependencies, identify which tests are affected.
            
            Changed Component: %s
            
            Test Dependencies:
            %s
            
            Return a comma-separated list of affected test names only.
            """.formatted(changedComponent, dependencies);
    }

    private String buildRiskScorePrompt(List<String> changedFiles, List<String> failedTests, List<String> criticalTests) {
        return """
            You are a Risk Assessment Expert.
            
            Calculate a risk score (0.0 to 1.0) based on:
            
            Changed Files:
            %s
            
            Previously Failed Tests:
            %s
            
            Critical Tests:
            %s
            
            Consider:
            - Number of critical components changed
            - Historical failure rate
            - Test coverage of changed areas
            - Complexity of changes
            
            Return only a decimal number between 0.0 and 1.0.
            """.formatted(
            String.join("\n", changedFiles),
            String.join("\n", failedTests),
            String.join("\n", criticalTests)
        );
    }

    private String buildImpactCategorizationPrompt(List<String> changedFiles) {
        return """
            You are a Code Impact Expert.
            
            Categorize each changed file by its impact type:
            - UI: User interface changes
            - API: Backend API changes
            - DATA: Database/data changes
            - CONFIG: Configuration changes
            - BUSINESS: Business logic changes
            
            Changed Files:
            %s
            
            Format: filename:IMPACT_TYPE
            One per line.
            """.formatted(String.join("\n", changedFiles));
    }

    private ImpactAnalysisResult parseImpactAnalysis(String aiResponse, List<String> allTests) {
        ImpactAnalysisResult result = new ImpactAnalysisResult();
        
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("HIGH_IMPACT:")) {
                result.setHighImpactTests(extractTests(line, allTests));
            } else if (line.startsWith("MEDIUM_IMPACT:")) {
                result.setMediumImpactTests(extractTests(line, allTests));
            } else if (line.startsWith("LOW_IMPACT:")) {
                result.setLowImpactTests(extractTests(line, allTests));
            } else if (line.startsWith("RISK_LEVEL:")) {
                result.setRiskLevel(line.split(":")[1].trim());
            }
        }
        
        return result;
    }

    private ImpactAnalysisResult combineResults(ImpactAnalysisResult deterministic, ImpactAnalysisResult ai, double deterministicWeight) {
        ImpactAnalysisResult combined = new ImpactAnalysisResult();
        
        // Combine high impact tests
        Set<String> highImpact = new HashSet<>();
        highImpact.addAll(deterministic.getHighImpactTests());
        highImpact.addAll(ai.getHighImpactTests());
        combined.setHighImpactTests(new ArrayList<>(highImpact));
        
        // Combine medium impact tests (excluding those already in high)
        Set<String> mediumImpact = new HashSet<>();
        mediumImpact.addAll(deterministic.getMediumImpactTests());
        mediumImpact.addAll(ai.getMediumImpactTests());
        mediumImpact.removeAll(highImpact);
        combined.setMediumImpactTests(new ArrayList<>(mediumImpact));
        
        // Low impact tests
        Set<String> lowImpact = new HashSet<>();
        lowImpact.addAll(deterministic.getLowImpactTests());
        lowImpact.addAll(ai.getLowImpactTests());
        lowImpact.removeAll(highImpact);
        lowImpact.removeAll(mediumImpact);
        combined.setLowImpactTests(new ArrayList<>(lowImpact));
        
        // Risk level based on combined results
        String riskLevel = deterministic.getRiskLevel();
        if (ai.getRiskLevel().equals("HIGH") && !riskLevel.equals("HIGH")) {
            riskLevel = "MEDIUM";
        }
        combined.setRiskLevel(riskLevel);
        
        return combined;
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

    private List<String> parseAffectedTests(String aiResponse) {
        String[] tests = aiResponse.split(",");
        List<String> result = new ArrayList<>();
        for (String test : tests) {
            result.add(test.trim());
        }
        return result;
    }

    private double parseRiskScore(String aiResponse) {
        try {
            return Double.parseDouble(aiResponse.trim());
        } catch (NumberFormatException e) {
            return 0.5;
        }
    }

    private Map<String, String> parseImpactCategorization(String aiResponse) {
        Map<String, String> result = new HashMap<>();
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length == 2) {
                result.put(parts[0].trim(), parts[1].trim());
            }
        }
        return result;
    }

    private String extractClassName(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    private boolean isCriticalComponent(String filePath) {
        String lowerFile = filePath.toLowerCase();
        return lowerFile.contains("auth") || 
               lowerFile.contains("payment") || 
               lowerFile.contains("security") ||
               lowerFile.contains("user") ||
               lowerFile.contains("login");
    }

    private String calculateRiskLevel(int highCount, int mediumCount, int totalCount) {
        double highRatio = (double) highCount / totalCount;
        double mediumRatio = (double) mediumCount / totalCount;
        
        if (highRatio > 0.3) {
            return "HIGH";
        } else if (highRatio > 0.1 || mediumRatio > 0.4) {
            return "MEDIUM";
        } else {
            return "LOW";
        }
    }

    public static class ImpactAnalysisResult {
        private List<String> highImpactTests = new ArrayList<>();
        private List<String> mediumImpactTests = new ArrayList<>();
        private List<String> lowImpactTests = new ArrayList<>();
        private String riskLevel = "MEDIUM";

        public List<String> getHighImpactTests() { return highImpactTests; }
        public void setHighImpactTests(List<String> highImpactTests) { this.highImpactTests = highImpactTests; }
        
        public List<String> getMediumImpactTests() { return mediumImpactTests; }
        public void setMediumImpactTests(List<String> mediumImpactTests) { this.mediumImpactTests = mediumImpactTests; }
        
        public List<String> getLowImpactTests() { return lowImpactTests; }
        public void setLowImpactTests(List<String> lowImpactTests) { this.lowImpactTests = lowImpactTests; }
        
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    }
}
