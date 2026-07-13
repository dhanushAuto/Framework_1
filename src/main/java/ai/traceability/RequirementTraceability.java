package ai.traceability;

import ai.service.AIService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

public class RequirementTraceability {

    private final AIService aiService = new AIService();

    public TraceabilityMatrix generateTraceabilityMatrix(List<String> requirements, List<String> testCases) throws Exception {
        String prompt = buildTraceabilityPrompt(requirements, testCases);
        String aiResponse = aiService.ask(prompt);
        return parseTraceabilityMatrix(aiResponse, requirements, testCases);
    }

    private TraceabilityMatrix parseTraceabilityMatrix(String aiResponse, List<String> requirements, List<String> testCases) {
        TraceabilityMatrix matrix = new TraceabilityMatrix();
        
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.contains(":")) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String requirement = parts[0].trim();
                    if (!requirements.contains(requirement)) {
                        continue;
                    }
                    String[] tests = parts[1].trim().split(",");
                    List<String> testList = new ArrayList<>();
                    for (String test : tests) {
                        String trimmed = test.trim();
                        if (testCases.contains(trimmed)) {
                            testList.add(trimmed);
                        }
                    }
                    matrix.addMapping(requirement, testList);
                }
            }
        }
        
        return matrix;
    }

    private String buildTraceabilityPrompt(List<String> requirements, List<String> testCases) {
        return """
            You are a Requirements Traceability Expert.
            Map the following requirements to the provided test cases.
            
            Requirements:
            %s
            
            Test Cases:
            %s
            
            Format: REQ-ID: test1, test2
            """.formatted(String.join("\n", requirements), String.join("\n", testCases));
    }

    public List<String> mapRequirementToTests(String requirement, List<String> availableTests) throws Exception {
        String prompt = buildMappingPrompt(requirement, availableTests);
        String aiResponse = aiService.ask(prompt);
        return parseMappedTests(aiResponse);
    }

    public CoverageAnalysis analyzeCoverage(List<String> requirements, Map<String, List<String>> traceabilityMap) throws Exception {
        String prompt = buildCoveragePrompt(requirements, traceabilityMap);
        String aiResponse = aiService.ask(prompt);
        return parseCoverageAnalysis(aiResponse);
    }

    public List<String> findOrphanTests(List<String> allTests, Map<String, List<String>> traceabilityMap) throws Exception {
        String prompt = buildOrphanTestsPrompt(allTests, traceabilityMap);
        String aiResponse = aiService.ask(prompt);
        return parseOrphanTests(aiResponse);
    }

    public List<String> suggestMissingTests(List<String> uncoveredRequirements) throws Exception {
        String prompt = buildMissingTestPrompt(uncoveredRequirements);
        String aiResponse = aiService.ask(prompt);
        return parseSuggestedTests(aiResponse);
    }

    private String buildMappingPrompt(String requirement, List<String> availableTests) {
        return """
            You are a Requirements Traceability Expert.
            Map the following requirement to the most relevant test cases.
            Requirement: %s
            Available Test Cases: %s
            Return a comma-separated list of test case names.
            """.formatted(requirement, String.join("\n", availableTests));
    }

    private String buildCoveragePrompt(List<String> requirements, Map<String, List<String>> traceabilityMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyze coverage for requirements:\n");
        requirements.forEach(req -> sb.append("- ").append(req).append("\n"));
        sb.append("\nTraceability Map:\n");
        traceabilityMap.forEach((req, tests) -> sb.append(req).append(": ").append(String.join(", ", tests)).append("\n"));
        return sb.toString();
    }

    private String buildOrphanTestsPrompt(List<String> allTests, Map<String, List<String>> traceabilityMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("Find orphan tests from:\n");
        allTests.forEach(test -> sb.append("- ").append(test).append("\n"));
        sb.append("\nTraceability Map:\n");
        traceabilityMap.forEach((req, tests) -> sb.append(req).append(": ").append(String.join(", ", tests)).append("\n"));
        return sb.toString();
    }

    private String buildMissingTestPrompt(List<String> uncoveredRequirements) {
        return "Suggest missing tests for: " + String.join(", ", uncoveredRequirements);
    }

    private List<String> parseMappedTests(String aiResponse) {
        return extractList(aiResponse);
    }

    private CoverageAnalysis parseCoverageAnalysis(String aiResponse) {
        CoverageAnalysis analysis = new CoverageAnalysis();
        // Simplified parsing logic
        return analysis;
    }

    private List<String> parseOrphanTests(String aiResponse) {
        return extractList(aiResponse);
    }

    private List<String> parseSuggestedTests(String aiResponse) {
        return extractList(aiResponse);
    }

    private List<String> extractList(String str) {
        if (str == null || str.trim().isEmpty()) return new ArrayList<>();
        String[] items = str.trim().split(",");
        List<String> result = new ArrayList<>();
        for (String item : items) {
            result.add(item.trim());
        }
        return result;
    }

    public static class TraceabilityMatrix {
        private Map<String, List<String>> matrix = new HashMap<>();
        public void addMapping(String requirement, List<String> testCases) { matrix.put(requirement, testCases); }
        public Map<String, List<String>> getMatrix() { return matrix; }
        public List<String> getTestsForRequirement(String requirement) { return matrix.getOrDefault(requirement, new ArrayList<>()); }
    }

    public static class CoverageAnalysis {
        private double coveragePercentage = 0.0;
        private List<String> coveredRequirements = new ArrayList<>();
        private List<String> uncoveredRequirements = new ArrayList<>();
        private List<String> partiallyCovered = new ArrayList<>();
        private List<String> overCovered = new ArrayList<>();

        public double getCoveragePercentage() { return coveragePercentage; }
        public void setCoveragePercentage(double cp) { this.coveragePercentage = cp; }
        public List<String> getCoveredRequirements() { return coveredRequirements; }
        public void setCoveredRequirements(List<String> cr) { this.coveredRequirements = cr; }
        public List<String> getUncoveredRequirements() { return uncoveredRequirements; }
        public void setUncoveredRequirements(List<String> ur) { this.uncoveredRequirements = ur; }
        public List<String> getPartiallyCovered() { return partiallyCovered; }
        public void setPartiallyCovered(List<String> pc) { this.partiallyCovered = pc; }
        public List<String> getOverCovered() { return overCovered; }
        public void setOverCovered(List<String> oc) { this.overCovered = oc; }
    }
}
