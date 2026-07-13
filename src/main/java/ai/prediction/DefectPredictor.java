package ai.prediction;

import ai.service.AIService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefectPredictor {

    private final AIService aiService = new AIService();

    public DefectPrediction predictDefects(List<String> changedFiles, CodeChangeContext context) throws Exception {
        String prompt = buildPredictionPrompt(changedFiles, context);
        String aiResponse = aiService.ask(prompt);
        return parseDefectPrediction(aiResponse);
    }

    private String buildPredictionPrompt(List<String> changes, CodeChangeContext context) {
        return """
            Predict defects for: %s
            Complexity: %s
            """.formatted(String.join(", ", changes), context.complexity);
    }

    private DefectPrediction parseDefectPrediction(String aiResponse) {
        DefectPrediction prediction = new DefectPrediction();
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("DEFECT_PROBABILITY:")) try { prediction.setDefectProbability(Double.parseDouble(line.split(":")[1].trim())); } catch (Exception e) {}
            else if (line.startsWith("EXPECTED_DEFECT_COUNT:")) try { prediction.setExpectedDefectCount(Integer.parseInt(line.split(":")[1].trim())); } catch (Exception e) {}
        }
        return prediction;
    }

    public static class DefectPrediction {
        private double defectProbability = 0.0;
        private int expectedDefectCount = 0;
        private List<String> highRiskFiles = new ArrayList<>();
        private List<String> defectTypes = new ArrayList<>();
        private double confidence = 0.0;

        public double getDefectProbability() { return defectProbability; }
        public void setDefectProbability(double dp) { this.defectProbability = dp; }
        public int getExpectedDefectCount() { return expectedDefectCount; }
        public void setExpectedDefectCount(int edc) { this.expectedDefectCount = edc; }
        public List<String> getHighRiskFiles() { return highRiskFiles; }
        public void setHighRiskFiles(List<String> hrf) { this.highRiskFiles = hrf; }
        public List<String> getDefectTypes() { return defectTypes; }
        public void setDefectTypes(List<String> dt) { this.defectTypes = dt; }
        public double getConfidence() { return confidence; }
        public void setConfidence(double c) { this.confidence = c; }
    }

    public static class DefectProbability {
        private double probability = 0.0;
        private String riskLevel = "MEDIUM";
        private List<String> factors = new ArrayList<>();
        public double getProbability() { return probability; }
        public void setProbability(double p) { this.probability = p; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String rl) { this.riskLevel = rl; }
        public List<String> getFactors() { return factors; }
        public void setFactors(List<String> f) { this.factors = f; }
    }

    public static class ComponentMetrics {
        public double defectRate = 0.0;
        public double complexity = 0.0;
        public int churn = 0;
        public int age = 0;
    }

    public static class CodeChangeContext {
        public String complexity = "MEDIUM";
        public String developerExperience = "SENIOR";
        public double historicalDefectRate = 0.0;
        public double codeCoverage = 0.0;
        public String reviewStatus = "PENDING";
    }
}
