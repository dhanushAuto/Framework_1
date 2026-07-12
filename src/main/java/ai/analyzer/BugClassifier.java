package ai.analyzer;

import ai.service.AIService;

public class BugClassifier {

    private final AIService aiService = new AIService();

    public BugClassification classifyBug(String errorMessage, String stackTrace, String testContext) throws Exception {
        
        String prompt = buildClassificationPrompt(errorMessage, stackTrace, testContext);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseClassification(aiResponse);
    }

    public String categorizeSeverity(String errorMessage, String impact) throws Exception {
        
        String prompt = buildSeverityPrompt(errorMessage, impact);
        
        String aiResponse = aiService.ask(prompt);
        
        return aiResponse.trim();
    }

    private String buildClassificationPrompt(String errorMessage, String stackTrace, String testContext) {
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
                classification.setBugType(line.split(":")[1].trim());
            } else if (line.startsWith("ROOT_CAUSE_CATEGORY:")) {
                classification.setRootCauseCategory(line.split(":")[1].trim());
            } else if (line.startsWith("AFFECTED_LAYER:")) {
                classification.setAffectedLayer(line.split(":")[1].trim());
            } else if (line.startsWith("REPRODUCIBILITY:")) {
                classification.setReproducibility(line.split(":")[1].trim());
            } else if (line.startsWith("ESCALATION_LEVEL:")) {
                classification.setEscalationLevel(line.split(":")[1].trim());
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
