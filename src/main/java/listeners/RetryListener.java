package listeners;

import ai.service.AIService;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import utilities.common_utils.LogUtils;

public class RetryListener implements IRetryAnalyzer {

    private int count = 0;
    private static final int MAX_RETRY = 2;
    private final AIService aiService = new AIService();

    @Override
    public boolean retry(ITestResult result) {
        if (count < MAX_RETRY) {
            if (shouldRetryWithAI(result)) {
                count++;
                LogUtils.info("AI decided to RETRY test: " + result.getName() + " (Attempt " + count + ")");
                return true;
            } else {
                LogUtils.info("AI decided NOT to retry test: " + result.getName());
                return false;
            }
        }
        return false;
    }

    private boolean shouldRetryWithAI(ITestResult result) {
        String errorMessage = result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown error";
        String prompt = """
            You are an AI Test Orchestrator.
            Based on the following error, should we retry the test, skip it, or fail immediately?
            Error: %s
            
            Respond with only one word: RETRY, FAIL, or SKIP.
            """.formatted(errorMessage);
        
        try {
            String decision = aiService.ask(prompt).trim().toUpperCase();
            return decision.contains("RETRY");
        } catch (Exception e) {
            return true; // Default to retry if AI fails
        }
    }
}
