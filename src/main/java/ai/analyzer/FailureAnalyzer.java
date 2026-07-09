package ai.analyzer;

import ai.prompt.FailurePromptBuilder;
import ai.service.AIService;

public class FailureAnalyzer {

    private final AIService aiService = new AIService();

    public String analyze(
            String testName,
            @org.jetbrains.annotations.UnknownNullability Throwable exception,
            String stackTrace) throws Exception {

        String prompt = FailurePromptBuilder.buildFailurePrompt(
                testName,
                exception.toString(),
                stackTrace);

        return aiService.ask(prompt);
    }

    public String analyzeWithContext(
            String testName,
            @org.jetbrains.annotations.UnknownNullability Throwable exception,
            String stackTrace,
            String screenshotPath,
            String currentUrl,
            String browser,
            String requestPayload,
            String responseBody,
            String headers,
            String executionLogs) throws Exception {

        String prompt = FailurePromptBuilder.buildFailurePromptWithContext(
                testName,
                exception.toString(),
                stackTrace,
                screenshotPath,
                currentUrl,
                browser,
                requestPayload,
                responseBody,
                headers,
                executionLogs);

        return aiService.ask(prompt);
    }
}