package ai.test;

import ai.service.AIService;
import utilities.api.ConfigUtils;

import utilities.common_utils.LogUtils;

public class AITest {

    private final AIService aiService = new AIService();

    public String ask(String prompt) throws Exception {
        return aiService.ask(prompt);
    }

}