package ai.performance;

import ai.service.AIService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerformanceRegressionDetector {

    private final AIService aiService = new AIService();

    public PerformanceAnalysisResult analyzePerformance(Map<String, PerformanceMetrics> currentMetrics, 
                                                         Map<String, PerformanceMetrics> baselineMetrics) throws Exception {
        
        String prompt = buildPerformanceAnalysisPrompt(currentMetrics, baselineMetrics);
        
        String aiResponse = aiService.ask(prompt);
        
        return parsePerformanceAnalysis(aiResponse);
    }

    public List<String> detectRegressions(Map<String, PerformanceMetrics> currentMetrics, 
                                          Map<String, PerformanceMetrics> baselineMetrics, 
                                          double threshold) throws Exception {
        
        String prompt = buildRegressionDetectionPrompt(currentMetrics, baselineMetrics, threshold);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseRegressions(aiResponse);
    }

    public String generatePerformanceReport(Map<String, PerformanceMetrics> currentMetrics, 
                                            Map<String, PerformanceMetrics> baselineMetrics) throws Exception {
        
        String prompt = buildReportPrompt(currentMetrics, baselineMetrics);
        
        return aiService.ask(prompt);
    }

    public List<PerformanceRecommendation> getOptimizationRecommendations(Map<String, PerformanceMetrics> metrics) throws Exception {
        
        String prompt = buildOptimizationPrompt(metrics);
        
        String aiResponse = aiService.ask(prompt);
        
        return parseRecommendations(aiResponse);
    }

    private String buildPerformanceAnalysisPrompt(Map<String, PerformanceMetrics> currentMetrics, 
                                                  Map<String, PerformanceMetrics> baselineMetrics) {
        StringBuilder currentStr = new StringBuilder();
        currentMetrics.forEach((test, metrics) -> 
            currentStr.append(test).append(" | ")
                     .append("ResponseTime:").append(metrics.responseTime).append("ms | ")
                     .append("Throughput:").append(metrics.throughput).append("req/s | ")
                     .append("ErrorRate:").append(metrics.errorRate).append("% | ")
                     .append("CPU:").append(metrics.cpuUsage).append("% | ")
                     .append("Memory:").append(metrics.memoryUsage).append("MB\n")
        );
        
        StringBuilder baselineStr = new StringBuilder();
        baselineMetrics.forEach((test, metrics) -> 
            baselineStr.append(test).append(" | ")
                      .append("ResponseTime:").append(metrics.responseTime).append("ms | ")
                      .append("Throughput:").append(metrics.throughput).append("req/s | ")
                      .append("ErrorRate:").append(metrics.errorRate).append("%\n")
        );
        
        return """
            You are a Performance Analysis Expert.
            
            Analyze the current performance metrics against the baseline to identify trends and anomalies.
            
            Current Metrics:
            %s
            
            Baseline Metrics:
            %s
            
            Provide:
            1. REGRESSIONS: Tests with significant performance degradation
            2. IMPROVEMENTS: Tests with performance improvements
            3. STABLE: Tests with stable performance
            4. ANOMALIES: Unusual patterns or outliers
            
            Format:
            REGRESSIONS: test1 (+50%), test2 (+30%)
            IMPROVEMENTS: test3 (-20%)
            STABLE: test4, test5
            ANOMALIES: test6 (spike in CPU)
            """.formatted(currentStr, baselineStr);
    }

    private String buildRegressionDetectionPrompt(Map<String, PerformanceMetrics> currentMetrics, 
                                                 Map<String, PerformanceMetrics> baselineMetrics, 
                                                 double threshold) {
        StringBuilder comparison = new StringBuilder();
        for (String test : currentMetrics.keySet()) {
            PerformanceMetrics current = currentMetrics.get(test);
            PerformanceMetrics baseline = baselineMetrics.getOrDefault(test, current);
            
            double responseTimeChange = calculatePercentChange(baseline.responseTime, current.responseTime);
            double throughputChange = calculatePercentChange(baseline.throughput, current.throughput);
            double errorRateChange = calculatePercentChange(baseline.errorRate, current.errorRate);
            
            comparison.append(test).append(" | ")
                     .append("ResponseTimeChange:").append(String.format("%.1f%%", responseTimeChange)).append(" | ")
                     .append("ThroughputChange:").append(String.format("%.1f%%", throughputChange)).append(" | ")
                     .append("ErrorRateChange:").append(String.format("%.1f%%", errorRateChange)).append("\n");
        }
        
        return """
            You are a Performance Regression Detection Expert.
            
            Identify tests with performance regression exceeding the threshold.
            
            Threshold: %.1f%%
            
            Performance Changes:
            %s
            
            Return a comma-separated list of regressed test names only.
            """.formatted(threshold * 100, comparison);
    }

    private String buildReportPrompt(Map<String, PerformanceMetrics> currentMetrics, 
                                     Map<String, PerformanceMetrics> baselineMetrics) {
        StringBuilder currentStr = new StringBuilder();
        currentMetrics.forEach((test, metrics) -> 
            currentStr.append(test).append(": ")
                     .append("RT=").append(metrics.responseTime).append("ms, ")
                     .append("TP=").append(metrics.throughput).append("req/s, ")
                     .append("ER=").append(metrics.errorRate).append("%\n")
        );
        
        return """
            You are a Performance Reporting Expert.
            
            Generate a comprehensive performance report comparing current metrics with baseline.
            
            Current Metrics:
            %s
            
            Provide a detailed report including:
            1. Executive Summary
            2. Key Findings
            3. Performance Trends
            4. Recommendations
            5. Risk Assessment
            """.formatted(currentStr);
    }

    private String buildOptimizationPrompt(Map<String, PerformanceMetrics> metrics) {
        StringBuilder metricsStr = new StringBuilder();
        metrics.forEach((test, m) -> 
            metricsStr.append(test).append(" | ")
                     .append("ResponseTime:").append(m.responseTime).append("ms | ")
                     .append("CPU:").append(m.cpuUsage).append("% | ")
                     .append("Memory:").append(m.memoryUsage).append("MB | ")
                     .append("DBQueries:").append(m.dbQueryCount).append("\n")
        );
        
        return """
            You are a Performance Optimization Expert.
            
            Analyze the performance metrics and provide optimization recommendations.
            
            Metrics:
            %s
            
            For each performance issue, provide:
            1. Root cause analysis
            2. Specific optimization recommendations
            3. Expected improvement
            4. Implementation priority
            
            Format:
            TEST_NAME: Recommendation | Priority: HIGH/MEDIUM/LOW
            """.formatted(metricsStr);
    }

    private PerformanceAnalysisResult parsePerformanceAnalysis(String aiResponse) {
        PerformanceAnalysisResult result = new PerformanceAnalysisResult();
        
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.startsWith("REGRESSIONS:")) {
                result.setRegressions(extractList(line.split(":")[1]));
            } else if (line.startsWith("IMPROVEMENTS:")) {
                result.setImprovements(extractList(line.split(":")[1]));
            } else if (line.startsWith("STABLE:")) {
                result.setStable(extractList(line.split(":")[1]));
            } else if (line.startsWith("ANOMALIES:")) {
                result.setAnomalies(extractList(line.split(":")[1]));
            }
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

    private List<String> parseRegressions(String aiResponse) {
        String[] tests = aiResponse.split(",");
        List<String> result = new ArrayList<>();
        for (String test : tests) {
            result.add(test.trim());
        }
        return result;
    }

    private List<PerformanceRecommendation> parseRecommendations(String aiResponse) {
        List<PerformanceRecommendation> recommendations = new ArrayList<>();
        String[] lines = aiResponse.split("\n");
        for (String line : lines) {
            if (line.contains(":")) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    PerformanceRecommendation rec = new PerformanceRecommendation();
                    rec.setTestName(parts[0].trim());
                    rec.setRecommendation(parts[1].split("\\|")[0].trim());
                    if (line.contains("Priority:")) {
                        rec.setPriority(line.split("Priority:")[1].trim());
                    }
                    recommendations.add(rec);
                }
            }
        }
        return recommendations;
    }

    private double calculatePercentChange(double baseline, double current) {
        if (baseline == 0) return 0;
        return ((current - baseline) / baseline) * 100;
    }

    public static class PerformanceAnalysisResult {
        private List<String> regressions = new ArrayList<>();
        private List<String> improvements = new ArrayList<>();
        private List<String> stable = new ArrayList<>();
        private List<String> anomalies = new ArrayList<>();

        public List<String> getRegressions() { return regressions; }
        public void setRegressions(List<String> regressions) { this.regressions = regressions; }
        
        public List<String> getImprovements() { return improvements; }
        public void setImprovements(List<String> improvements) { this.improvements = improvements; }
        
        public List<String> getStable() { return stable; }
        public void setStable(List<String> stable) { this.stable = stable; }
        
        public List<String> getAnomalies() { return anomalies; }
        public void setAnomalies(List<String> anomalies) { this.anomalies = anomalies; }
    }

    public static class PerformanceMetrics {
        public long responseTime = 0;
        public double throughput = 0.0;
        public double errorRate = 0.0;
        public double cpuUsage = 0.0;
        public double memoryUsage = 0.0;
        public int dbQueryCount = 0;
    }

    public static class PerformanceRecommendation {
        private String testName;
        private String recommendation;
        private String priority = "MEDIUM";

        public String getTestName() { return testName; }
        public void setTestName(String testName) { this.testName = testName; }
        
        public String getRecommendation() { return recommendation; }
        public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }
}
