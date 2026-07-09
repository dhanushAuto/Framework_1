package ai.prompt;

public class PerformancePromptBuilder {

    public static String buildPerformanceAnalysisPrompt(String currentMetrics, String baselineMetrics) {
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
            REGRESSIONS: test1 (+50%%), test2 (+30%%)
            IMPROVEMENTS: test3 (-20%%)
            STABLE: test4, test5
            ANOMALIES: test6 (spike in CPU)
            """.formatted(currentMetrics, baselineMetrics);
    }

    public static String buildOptimizationPrompt(String metrics) {
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
            """.formatted(metrics);
    }
}
