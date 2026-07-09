package ai.prompt;

public class FailurePromptBuilder {

    public static String buildFailurePrompt(String testName, String exception, String stackTrace) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("""
You are a Senior Automation Architect.

Analyze the following failed automation test.

                Provide:
                1. Root Cause
                
                2. Suggested Fix

----------------------------------------""");

        prompt.append("Test Name : ")
                .append(testName)
                .append("\n\n");

        prompt.append("Exception : ")
                .append(exception)
                .append("\n\n");

        prompt.append("Stack Trace :\n")
                .append(stackTrace);

        return prompt.toString();
    }

    public static String buildFailurePromptWithContext(
            String testName, 
            String exception, 
            String stackTrace,
            String screenshotPath,
            String currentUrl,
            String browser,
            String requestPayload,
            String responseBody,
            String headers,
            String executionLogs) {

        StringBuilder prompt = new StringBuilder();

        prompt.append("""
You are a Senior Automation Architect.

Analyze the following failed automation test with full context.

                Provide:
                1. Root Cause
                2. Suggested Fix
                3. Prevention Strategy

----------------------------------------""");

        prompt.append("Test Name : ").append(testName).append("\n\n");
        prompt.append("Exception : ").append(exception).append("\n\n");
        prompt.append("Stack Trace :\n").append(stackTrace).append("\n\n");

        if (screenshotPath != null && !screenshotPath.isEmpty()) {
            prompt.append("Screenshot Path : ").append(screenshotPath).append("\n\n");
        }
        if (currentUrl != null && !currentUrl.isEmpty()) {
            prompt.append("Current URL : ").append(currentUrl).append("\n\n");
        }
        if (browser != null && !browser.isEmpty()) {
            prompt.append("Browser : ").append(browser).append("\n\n");
        }
        if (requestPayload != null && !requestPayload.isEmpty()) {
            prompt.append("Request Payload : ").append(requestPayload).append("\n\n");
        }
        if (responseBody != null && !responseBody.isEmpty()) {
            prompt.append("Response Body : ").append(responseBody).append("\n\n");
        }
        if (headers != null && !headers.isEmpty()) {
            prompt.append("Headers : ").append(headers).append("\n\n");
        }
        if (executionLogs != null && !executionLogs.isEmpty()) {
            prompt.append("Execution Logs :\n").append(executionLogs).append("\n\n");
        }

        return prompt.toString();
    }
}
