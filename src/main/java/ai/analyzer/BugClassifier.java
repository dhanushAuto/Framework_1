package ai.analyzer;

import ai.service.AIService;

public class BugClassifier {

    private final AIService aiService = new AIService();

    public String ask(String prompt) {
        return aiService.ask(prompt);
    }

    public String classifyBug(String errorMessage, String impact) {
        String prompt = buildSeverityPrompt(errorMessage, impact);
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

    public String generateTestData(String testCaseDescription, String dataType) {
        String prompt = buildTestDataPrompt(testCaseDescription, dataType);
        return aiService.ask(prompt);
    }

    private String buildTestDataPrompt(String description, String type) {
        return """
            You are a QA Data Engineer.
            Generate test data for the following test case:
            Description: %s
            Data Type: %s
            
            Provide the data in a clear, usable format.
            """.formatted(description, type);
    }

    public BugClassification getClassification(String aiResponse) {
        return parseClassification(aiResponse);
    }

    private BugClassification parseClassification(String aiResponse) {
        BugClassification classification = new BugClassification();
        
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("BUG_TYPE:")) {
                classification.setBugType(line.split(":")[1].trim());
            } else if (line.startsWith("ROOT_CAUSE:")) {
                classification.setRootCauseCategory(line.split(":")[1].trim());
            } else if (line.startsWith("LAYER:")) {
                classification.setAffectedLayer(line.split(":")[1].trim());
            }
        }
        return classification;
    }

    public static class BugClassification {
        private String bugType = "LOGIC";
        private String rootCauseCategory = "Code";
        private String affectedLayer = "Backend";
        private String reproducibility = "Always";
        private String escalationLevel = "L2";

        public String getBugType() { return bugType; }
        public void setBugType(String bugType) { this.bugType = bugType; }
        
        public String getRootCauseCategory() { return rootCauseCategory; }
        public void setRootCauseCategory(String rootCauseCategory) { this.rootCauseCategory = rootCauseCategory; }
        
        public String getAffectedLayer() { return affectedLayer; }
        public void setAffectedLayer(String affectedLayer) { this.affectedLayer = affectedLayer; }
        
        public String getReproducibility() { return reproducibility; }
        public void setReproducibility(String reproducibility) { this.reproducibility = reproducibility; }
        
        public String getEscalationLevel() { return escalationLevel; }
        public void setEscalationLevel(String escalationLevel) { this.escalationLevel = escalationLevel; }
    }
}
