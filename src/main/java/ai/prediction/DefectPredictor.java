package ai.prediction;

import ai.service.AIService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefectPredictor {

    private final AIService aiService = new AIService();

    public DefectPrediction predictDefects(CodeChangeContext context) throws Exception {
        
        String prompt = buildDefectPredictionPrompt(context);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseDefectPrediction(aiResponse);
    }

    public List<String> identifyHighRiskAreas(List<String> changedFiles, Map<String, ComponentMetrics> componentMetrics) throws Exception {
        
        String prompt = buildHighRiskPrompt(changedFiles, componentMetrics);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseHighRiskAreas(aiResponse);
    }

    public DefectProbability calculateDefectProbability(String component, String changeType, 
                                                         double complexity, String developerExperience) throws Exception {
        
        String prompt = buildProbabilityPrompt(component, changeType, complexity, developerExperience);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseDefectProbability(aiResponse);
    }

    public List<String> suggestPreventiveActions(DefectPrediction prediction) throws Exception {
        
        String prompt = buildPreventiveActionsPrompt(prediction);
        
        String aiResponse = aiService.ask(prompt);
        
        return parsePreventiveActions(aiResponse);
    }

    private String buildDefectPredictionPrompt(CodeChangeContext context) {
        StringBuilder changes = new StringBuilder();
        context.changedFiles.forEach((file, changeType) -> 
            changes.append(file).append(" (").append(changeType).append(")\n")
        );
        
        return """
            You are a Defect Prediction Expert.
            
            Predict the likelihood of defects in the following code changes.
            
            Changed Files:
            %s
            
            Change Complexity: %s
            Developer Experience: %s
            Historical Defect Rate: %.1f%%
            Code Coverage: %.1f%%
            Review Status: %s
            
            Provide:
            1. DEFECT_PROBABILITY: 0.0-1.0
            2. EXPECTED_DEFECT_COUNT: Estimated number of defects
            3. HIGH_RISK_FILES: Files most likely to contain defects
            4. DEFECT_TYPES: Likely defect types (logic, UI, API, performance, security)
            5. CONFIDENCE: 0.0-1.0
            
            Format:
            DEFECT_PROBABILITY: 0.65
            EXPECTED_DEFECT_COUNT: 3
            HIGH_RISK_FILES: file1.java, file2.java
            DEFECT_TYPES: logic, API
            CONFIDENCE: 0.80
            """.formatted(
            changes,
            context.complexity,
            context.developerExperience,
            context.historicalDefectRate,
            context.codeCoverage,
            context.reviewStatus
        );
    }

    private String buildHighRiskPrompt(List<String> changedFiles, Map<String, ComponentMetrics> componentMetrics) {
        StringBuilder metrics = new StringBuilder();
        componentMetrics.forEach((component, m) -> 
            metrics.append(component).append(" | ")
                   .append("DefectRate:").append(m.defectRate).append("% | ")
                   .append("Complexity:").append(m.complexity).append(" | ")
                   .append("Churn:").append(m.churn).append(" | ")
                   .append("Age:").append(m.age).append("days\n")
        );
        
        return """
            You are a Risk Assessment Expert.
            
            Identify high-risk areas based on changed files and component metrics.
            
            Changed Files:
            %s
            
            Component Metrics:
            %s
            
            Return a comma-separated list of high-risk component names only.
            """.formatted(String.join("\n", changedFiles), metrics);
    }

    private String buildProbabilityPrompt(String component, String changeType, 
                                          double complexity, String developerExperience) {
        return """
            You are a Defect Probability Calculation Expert.
            
            Calculate the probability of defects for the following change.
            
            Component: %s
            Change Type: %s
            Complexity Score: %.1f
            Developer Experience: %s
            
            Provide:
            1. PROBABILITY: 0.0-1.0
            2. RISK_LEVEL: LOW/MEDIUM/HIGH/CRITICAL
            3. FACTORS: Key factors influencing the probability
            
            Format:
            PROBABILITY: 0.45
            RISK_LEVEL: MEDIUM
            FACTORS: factor1, factor2, factor3
            """.formatted(component, changeType, complexity, developerExperience);
    }

    private String buildPreventiveActionsPrompt(DefectPrediction prediction) {
        return """
            You are a Defect Prevention Expert.
            
            Suggest preventive actions to reduce the predicted defect risk.
            
            Defect Probability: %.2f
            Expected Defect Count: %d
            High Risk Files: %s
            Defect Types: %s
            
            Provide specific, actionable preventive measures for:
            1. Code review focus areas
            2. Additional testing strategies
            3. Code quality checks
            4. Documentation requirements
            
            Return a comma-separated list of preventive action descriptions.
            """.formatted(
            prediction.defectProbability,
            prediction.expectedDefectCount,
            String.join(", ", prediction.highRiskFiles),
            String.join(", ", prediction.defectTypes)
        );
    }

    private DefectPrediction parseDefectPrediction(String aiResponse) {
        DefectPrediction prediction = new DefectPrediction();
        
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("DEFECT_PROBABILITY:")) {
                prediction.setDefectProbability(Double.parseDouble(line.split(":")[1].trim()));
            } else if (line.startsWith("EXPECTED_DEFECT_COUNT:")) {
                prediction.setExpectedDefectCount(Integer.parseInt(line.split(":")[1].trim()));
            } else if (line.startsWith("HIGH_RISK_FILES:")) {
                prediction.setHighRiskFiles(extractList(line.split(":")[1]));
            } else if (line.startsWith("DEFECT_TYPES:")) {
                prediction.setDefectTypes(extractList(line.split(":")[1]));
            } else if (line.startsWith("CONFIDENCE:")) {
                prediction.setConfidence(Double.parseDouble(line.split(":")[1].trim()));
            }
        }
        
        return prediction;
    }

    private List<String> parseHighRiskAreas(String aiResponse) {
        String[] areas = aiResponse.split(",");
        List<String> result = new ArrayList<>();
        for (String area : areas) {
            result.add(area.trim());
        }
        return result;
    }

    private DefectProbability parseDefectProbability(String aiResponse) {
        DefectProbability prob = new DefectProbability();
        
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("PROBABILITY:")) {
                prob.setProbability(Double.parseDouble(line.split(":")[1].trim()));
            } else if (line.startsWith("RISK_LEVEL:")) {
                prob.setRiskLevel(line.split(":")[1].trim());
            } else if (line.startsWith("FACTORS:")) {
                prob.setFactors(extractList(line.split(":")[1]));
            }
        }
        
        return prob;
    }

    private List<String> parsePreventiveActions(String aiResponse) {
        String[] actions = aiResponse.split(",");
        List<String> result = new ArrayList<>();
        for (String action : actions) {
            result.add(action.trim());
        }
        return result;
    }

    private List<String> extractList(String str) {
        String[] items = str.trim().split(",");
        List<String> result = new ArrayList<>();
        for (String item : items) {
            result.add(item.trim());
        }
        return result;
    }

    public static class DefectPrediction {
        private double defectProbability = 0.0;
        private int expectedDefectCount = 0;
        private List<String> highRiskFiles = new ArrayList<>();
        private List<String> defectTypes = new ArrayList<>();
        private double confidence = 0.0;

        public double getDefectProbability() { return defectProbability; }
        public void setDefectProbability(double defectProbability) { this.defectProbability = defectProbability; }
        
        public int getExpectedDefectCount() { return expectedDefectCount; }
        public void setExpectedDefectCount(int expectedDefectCount) { this.expectedDefectCount = expectedDefectCount; }
        
        public List<String> getHighRiskFiles() { return highRiskFiles; }
        public void setHighRiskFiles(List<String> highRiskFiles) { this.highRiskFiles = highRiskFiles; }
        
        public List<String> getDefectTypes() { return defectTypes; }
        public void setDefectTypes(List<String> defectTypes) { this.defectTypes = defectTypes; }
        
        public double getConfidence() { return confidence; }
        public void setConfidence(double confidence) { this.confidence = confidence; }
    }

    public static class DefectProbability {
        private double probability = 0.0;
        private String riskLevel = "LOW";
        private List<String> factors = new ArrayList<>();

        public double getProbability() { return probability; }
        public void setProbability(double probability) { this.probability = probability; }
        
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        
        public List<String> getFactors() { return factors; }
        public void setFactors(List<String> factors) { this.factors = factors; }
    }

    public static class ComponentMetrics {
        public double defectRate = 0.0;
        public double complexity = 0.0;
        public int churn = 0;
        public int age = 0;
    }

    public static class CodeChangeContext {
        public Map<String, String> changedFiles = new HashMap<>();
        public String complexity = "MEDIUM";
        public String developerExperience = "MEDIUM";
        public double historicalDefectRate = 0.0;
        public double codeCoverage = 0.0;
        public String reviewStatus = "PENDING";
    }
}
