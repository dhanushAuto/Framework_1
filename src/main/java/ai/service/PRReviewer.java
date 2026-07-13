package ai.service;

import ai.client.AIClient;
import utilities.common_utils.LogUtils;

public class PRReviewer {

    private final AIClient client = new AIClient();

    public String reviewPR(String diff) {
        String prompt = buildPRReviewPrompt(diff);
        try {
            return client.generateResponse(prompt);
        } catch (Exception e) {
            LogUtils.error("PR Review failed: " + e.getMessage());
            return "Review failed due to AI error.";
        }
    }

    private String buildPRReviewPrompt(String diff) {
        return """
            You are a Senior Software Engineer and Architect.
            Perform an AI Pull Request Review on the following code changes (git diff).
            
            Evaluate based on:
            1. Complexity: Identify high cyclomatic complexity.
            2. Naming: Check if variable and method names follow clean code principles.
            3. Duplication: Detect obvious code duplication.
            4. Maintainability: Assess overall code quality and readability.
            
            Diff:
            %s
            
            Return the review in a concise, bulleted format.
            """.formatted(diff);
    }
}
