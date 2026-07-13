package ai.analyzer;

import ai.service.AIService;

public class BugClassifier {

    private final AIService aiService = new AIService();

    public String ask(String prompt) throws Exception {
        return aiService.ask(prompt);
    }

    public String generateTestData(String testCaseDescription, String dataType) throws Exception {
        
        String prompt = buildTestDataPrompt(testCaseDescription, dataType);
        
        return aiService.ask(prompt);
    }

    private String buildSeverityPrompt(String errorMessage, String impact) {
        return """
            You are a Bug Severity Assessment Expert.
            
            Assess the severity of the following bug.
            
            Error Message: %s
            Impact: %s
            
            Return one of: CRITICAL, HIGH, MEDIUM, LOW, TRIVIAL
            """.formatted(errorMessage, impact);
    }

    private BugClassification parseClassification(String aiResponse) {
        BugClassification classification = new BugClassification();
        
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("BUG_TYPE:")) {
    public String generateTestData(String testCaseDescription, String dataType) throws Exception {
        
        String prompt = buildTestDataPrompt(testCaseDescription, dataType);
        
        return aiService.ask(prompt);
    }
        return classification;
    }

    public static class BugClassification {
        private String bugType = "LOGIC";
        private String rootCauseCategory = "Code";
        private String affectedLayer = "Backend";
    public String generateTestData(String testCaseDescription, String dataType) throws Exception {
        
        String prompt = buildTestDataPrompt(testCaseDescription, dataType);
        
        return aiService.ask(prompt);
    }
