package ai.release;

import ai.service.AIService;
import java.util.ArrayList;
import java.util.List;

public class ReleaseRiskPredictor {

    private final AIService aiService = new AIService();

    public ReleaseReadiness predictReadiness(ReleaseContext context) throws Exception {
        String prompt = buildReadinessPrompt(context);
        String aiResponse = aiService.ask(prompt);
        return parseReadiness(aiResponse);
    }

    private String buildReadinessPrompt(ReleaseContext context) {
        return "Predict release readiness for version " + context.version;
    }

    private ReleaseReadiness parseReadiness(String aiResponse) {
        ReleaseReadiness readiness = new ReleaseReadiness();
        // Basic parsing logic
        return readiness;
    }

    public static class ReleaseReadiness {
        private int readinessScore = 0;
        private String status = "NOT_READY";
        private List<String> blockers = new ArrayList<>();
        private String riskLevel = "HIGH";

        public int getReadinessScore() { return readinessScore; }
        public void setReadinessScore(int rs) { this.readinessScore = rs; }
        public String getStatus() { return status; }
        public void setStatus(String s) { this.status = s; }
        public List<String> getBlockers() { return blockers; }
        public void setBlockers(List<String> b) { this.blockers = b; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String rl) { this.riskLevel = rl; }
    }

    public static class ReleaseContext {
        public String version = "1.0";
        public List<String> changedComponents = new ArrayList<>();
        public String testResults = "";
        public int defectCount = 0;
        public int criticalDefectCount = 0;
        public double codeCoverage = 0.0;
        public String performanceMetrics = "";
        public String securityScanResults = "";
        public double testPassRate = 0.0;
        public double defectDensity = 0.0;
        public double historicalFailureRate = 0.0;
        public double complexityScore = 0.0;
        public String teamExperience = "SENIOR";
        public String timePressure = "LOW";
        public String failedTests = "";
        public String performanceRegressions = "";
        public String securityVulnerabilities = "";
        public String complianceIssues = "";
    }
}
