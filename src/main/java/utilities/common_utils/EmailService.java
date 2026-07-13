package utilities.common_utils;

import ai.service.AIService;

public class EmailService {

    private final AIService aiService = new AIService();

    public void sendSummaryEmail(int total, int passed, int failed, String details) {
        String aiSummary = generateAISummary(total, passed, failed, details);
        LogUtils.info("📧 Sending Email Summary...");
        LogUtils.info("Subject: AI Test Execution Summary");
        LogUtils.info("Body: " + aiSummary);
        // Real implementation would use JavaMailSender
    }

    private String generateAISummary(int total, int passed, int failed, String details) {
        String prompt = """
            Generate a professional executive summary for a test execution report.
            Total Tests: %d
            Passed: %d
            Failed: %d
            
            Details:
            %s
            
            Include a 'Recommendation' section.
            """.formatted(total, passed, failed, details);
        return aiService.ask(prompt);
    }
}
